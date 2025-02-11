package com.github.sparkzxl.gateway.plugin.filter;

import com.github.sparkzxl.gateway.plugin.common.constant.GatewayConstant;
import com.github.sparkzxl.gateway.plugin.common.constant.enums.FilterEnum;
import com.github.sparkzxl.gateway.plugin.context.DefaultGatewayContextBuilder;
import com.github.sparkzxl.gateway.plugin.context.GatewayContext;
import com.github.sparkzxl.gateway.plugin.context.GatewayContextBuilder;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description: the gateway Context Filter
 *
 * @author zhouxinlei
 * @since 2022-01-10 11:31:43
 */
public class GatewayContextFilter extends AbstractGlobalFilter {

    private final GatewayContextBuilder contextBuilder = new DefaultGatewayContextBuilder();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        GatewayContext gatewayContext = contextBuilder.build(exchange);
        exchange.getAttributes().put(GatewayConstant.NEED_SKIP, gatewayPluginProperties.match(gatewayContext.getUrl()));
        exchange.getAttributes().put(GatewayConstant.GATEWAY_CONTEXT_CONSTANT, gatewayContext);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterEnum.CONTEXT.getCode();
    }

    @Override
    public String named() {
        return FilterEnum.CONTEXT.getName();
    }
}
