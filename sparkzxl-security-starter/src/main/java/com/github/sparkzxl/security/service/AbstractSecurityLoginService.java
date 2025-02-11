package com.github.sparkzxl.security.service;

import cn.hutool.core.date.DateUtil;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.util.TimeUtil;
import com.github.sparkzxl.entity.core.AuthUserInfo;
import com.github.sparkzxl.entity.core.CaptchaInfo;
import com.github.sparkzxl.entity.core.JwtUserInfo;
import com.github.sparkzxl.entity.security.AuthRequest;
import com.github.sparkzxl.entity.security.AuthUserDetail;
import com.github.sparkzxl.entity.security.UserToken;
import com.github.sparkzxl.jwt.properties.JwtProperties;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.security.auth.login.AccountNotFoundException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description: 登录授权Service
 *
 * @author zhouxinlei
 */
@Slf4j
public abstract class AbstractSecurityLoginService<ID extends Serializable> {

    /**
     * 登录
     *
     * @param authRequest 登录认证
     * @return java.lang.String
     */
    public UserToken login(AuthRequest authRequest) throws AccountNotFoundException {
        String username = authRequest.getUsername();
        AuthUserDetail authUserDetail = (AuthUserDetail) getUserDetailsService().loadUserByUsername(username);
        if (ObjectUtils.isEmpty(authUserDetail)) {
            throw new AccountNotFoundException("账户不存在");
        }
        //校验密码输入是否正确
        checkPasswordError(authRequest, authUserDetail);
        UserToken userToken = authorization(authUserDetail);
        return userToken;
    }

    /**
     * 认证登录获取token
     *
     * @param authUserDetail 授权用户
     * @return AuthToken
     */
    public UserToken authorization(AuthUserDetail authUserDetail) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUserDetail,
                null, authUserDetail.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        long seconds = TimeUtil.toSeconds(getJwtProperties().getExpire(), getJwtProperties().getUnit());
        String username = authUserDetail.getUsername();
        AuthUserInfo authUserInfo = getAuthUserInfo(username);
        UserToken userToken = new UserToken();
        userToken.setAccessToken(createJwtToken(authUserDetail));
        userToken.setExpiration(seconds);
        userToken.setAuthUserInfo(authUserInfo);
        userToken.setTokenType(BaseContextConstants.BEARER_TOKEN);
        //设置accessToken缓存
        settingCacheToken(userToken, authUserInfo);
        return userToken;
    }


    public String createJwtToken(AuthUserDetail authUserDetail) {
        long seconds = TimeUtil.toSeconds(getJwtProperties().getExpire(), getJwtProperties().getUnit());
        Date expire = DateUtil.offsetSecond(new Date(), (int) seconds);
        JwtUserInfo jwtUserInfo = new JwtUserInfo();
        jwtUserInfo.setId(authUserDetail.getId());
        jwtUserInfo.setName(authUserDetail.getName());
        jwtUserInfo.setUsername(authUserDetail.getUsername());
        jwtUserInfo.setSub(authUserDetail.getUsername());
        jwtUserInfo.setIat(System.currentTimeMillis());
        jwtUserInfo.setExpire(expire);
        Collection<? extends GrantedAuthority> grantedAuthorities = authUserDetail.getAuthorities();
        if (CollectionUtils.isNotEmpty(grantedAuthorities)) {
            List<String> authorities = grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            jwtUserInfo.setAuthorities(authorities);
        }
        return getJwtTokenService().createTokenByHmac(jwtUserInfo);
    }

    /**
     * 校验密码正确性
     *
     * @param authRequest    登录请求
     * @param authUserDetail 用户信息
     */
    public abstract void checkPasswordError(AuthRequest authRequest, AuthUserDetail authUserDetail);


    /**
     * 获取全局用户
     *
     * @param username 用户名
     * @return AuthUserInfo<T>
     */
    public abstract AuthUserInfo getAuthUserInfo(String username);

    /**
     * 生成验证码
     *
     * @param type 验证码类型
     * @return CaptchaInfo
     */
    public CaptchaInfo createCaptcha(String type) {
        return null;
    }

    /**
     * 校验验证码
     *
     * @param key   前端上送 key
     * @param value 前端上送待校验值
     * @return 是否成功
     */
    public boolean checkCaptcha(String key, String value) {
        return false;
    }

    /**
     * 设置accessToken缓存
     *
     * @param userToken    用户token
     * @param authUserInfo 全局用户
     */
    public abstract void settingCacheToken(UserToken userToken, AuthUserInfo authUserInfo);

    /**
     * 获取jwt配置属性
     *
     * @return JwtProperties
     */
    public abstract JwtProperties getJwtProperties();

    /**
     * 获取jwt service
     *
     * @return JwtTokenService
     */
    public abstract JwtTokenService getJwtTokenService();

    /**
     * 获取用户信息接口
     *
     * @return UserDetailsService
     */
    public abstract UserDetailsService getUserDetailsService();


}
