package com.github.sparkzxl.gateway.filter.authorization;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.util.StringHandlerUtil;
import com.github.sparkzxl.core.util.SwaggerStaticResource;
import com.github.sparkzxl.entity.core.JwtUserInfo;
import com.github.sparkzxl.gateway.option.FilterOrderEnum;
import com.github.sparkzxl.gateway.support.GatewayException;
import com.github.sparkzxl.gateway.util.ReactorHttpHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: JWT授权校验管理过滤器
 *
 * @author zhouxinlei
 */
@Slf4j
public abstract class AbstractAuthorizationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestUrl = request.getPath().toString();
        String tenantId = ReactorHttpHelper.getHeader(BaseContextConstants.TENANT_ID, request);
        MDC.put(BaseContextConstants.TENANT_ID, String.valueOf(tenantId));
        log.info("请求租户id：[{}]，请求接口：[{}]", tenantId, requestUrl);
        RequestLocalContextHolder.setVersion(ReactorHttpHelper.getHeader(BaseContextConstants.VERSION, request));
        // 请求放行后置操作
        if (StringHandlerUtil.matchUrl(SwaggerStaticResource.EXCLUDE_STATIC_PATTERNS, request.getPath().toString())
                || StringHandlerUtil.matchUrl(ignorePatterns(), request.getPath().toString())) {
            // 放行请求清除token
            ignoreCheckAfterCompletion(exchange);
            return chain.filter(exchange);
        }
        // 鉴权
        return authentication(exchange, chain);
    }

    @Override
    public int getOrder() {
        return FilterOrderEnum.AUTHORIZATION_FILTER.getOrder();
    }

    private Mono<Void> authentication(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = ReactorHttpHelper.getHeader(getHeaderKey(), request);
        // 校验是否存在token
        if (preAuthenticationCheck(exchange)) {
            if (token.startsWith(BaseContextConstants.BASIC_AUTH)) {
                return chain.filter(exchange);
            }
            onAuthSuccess(exchange);
        } else {
            // 校验失败，后置操作
            afterAuthenticationCheck(exchange, chain);
        }
        return chain.filter(exchange.mutate().request(request.mutate().build()).build());
    }

    /**
     * 请求放行后置操作
     *
     * @param exchange exchange
     */
    protected void ignoreCheckAfterCompletion(ServerWebExchange exchange) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                .header(BaseContextConstants.JWT_TOKEN_HEADER, "").build();
        exchange.mutate().request(serverHttpRequest).build();
    }

    protected void checkTokenAuthority(ServerWebExchange exchange, String token) throws GatewayException {
        JwtUserInfo jwtUserInfo = getJwtUserInfo(token);
        if (jwtUserInfo.getExpire().getTime() < System.currentTimeMillis()) {
            throw new GatewayException(ResponseInfoStatus.TOKEN_EXPIRED_ERROR);
        }
        ServerHttpRequest.Builder mutate = exchange.getRequest().mutate();
        ReactorHttpHelper.addHeader(mutate, BaseContextConstants.JWT_KEY_ACCOUNT, jwtUserInfo.getUsername());
        ReactorHttpHelper.addHeader(mutate, BaseContextConstants.JWT_KEY_USER_ID, jwtUserInfo.getId());
        ReactorHttpHelper.addHeader(mutate, BaseContextConstants.JWT_KEY_NAME, jwtUserInfo.getName());
        MDC.put(BaseContextConstants.JWT_KEY_USER_ID, String.valueOf(jwtUserInfo.getId()));
        ServerHttpRequest serverHttpRequest = mutate.build();
        exchange.mutate().request(serverHttpRequest).build();
    }

    /**
     * 鉴权前校验
     *
     * @param exchange exchange
     * @return true 通过 ; false 不通过
     */
    protected boolean preAuthenticationCheck(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String token = ReactorHttpHelper.getHeader(getHeaderKey(), request);
        return !StringUtils.isEmpty(token);
    }

    protected void afterAuthenticationCheck(ServerWebExchange exchange, GatewayFilterChain chain) {
        throw new GatewayException(ResponseInfoStatus.JWT_EMPTY_ERROR);
    }

    /**
     * 鉴权成功操作
     *
     * @param exchange exchange
     */
    protected void onAuthSuccess(ServerWebExchange exchange) throws GatewayException {
        String token = ReactorHttpHelper.getHeader(getHeaderKey(), exchange.getRequest());
        token = StringUtils.removeStartIgnoreCase(token, BaseContextConstants.BEARER_TOKEN);
        checkTokenAuthority(exchange, token);
    }

    /**
     * 获取header
     *
     * @return 返回值
     */
    public abstract String getHeaderKey();

    /**
     * 放行地址集合
     *
     * @return List<String>
     */
    protected List<String> ignorePatterns() {
        return Lists.newArrayList();
    }

    /**
     * 获取token用户信息
     *
     * @param token token值
     * @return JwtUserInfo
     * @throws GatewayException 异常
     */
    public abstract JwtUserInfo getJwtUserInfo(String token) throws GatewayException;

}
