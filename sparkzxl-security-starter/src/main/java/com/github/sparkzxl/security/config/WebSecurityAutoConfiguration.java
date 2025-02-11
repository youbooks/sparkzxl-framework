package com.github.sparkzxl.security.config;

import com.github.sparkzxl.core.util.ListUtils;
import com.github.sparkzxl.core.util.SwaggerStaticResource;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import com.github.sparkzxl.security.authorization.DynamicAccessDecisionManager;
import com.github.sparkzxl.security.component.RestAuthenticationEntryPoint;
import com.github.sparkzxl.security.component.RestfulAccessDeniedHandler;
import com.github.sparkzxl.security.filter.DynamicSecurityFilter;
import com.github.sparkzxl.security.filter.JwtAuthenticationTokenFilter;
import com.github.sparkzxl.security.intercept.DynamicSecurityMetadataSource;
import com.github.sparkzxl.security.properties.SecurityProperties;
import com.github.sparkzxl.security.service.DynamicSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.List;

/**
 * description: Spring Security 配置
 *
 * @author zhouxinlei
 */

@Configuration
@EnableConfigurationProperties({SecurityProperties.class})
@EnableWebSecurity
@Slf4j
public class WebSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    private final SecurityProperties securityProperties;
    private JwtTokenService jwtTokenService;
    private DynamicSecurityService dynamicSecurityService;
    private UserDetailsService userDetailsService;

    public WebSecurityAutoConfiguration(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Autowired(required = false)
    public void setJwtTokenService(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Autowired(required = false)
    public void setDynamicSecurityService(DynamicSecurityService dynamicSecurityService) {
        this.dynamicSecurityService = dynamicSecurityService;
    }

    @Autowired(required = false)
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(WebSecurity web) {
        String[] excludeStaticPatterns = ListUtils.listToArray(SwaggerStaticResource.EXCLUDE_STATIC_PATTERNS);
        web.ignoring().antMatchers(excludeStaticPatterns);
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        web.httpFirewall(firewall);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity
                .authorizeRequests();
        List<String> excludePatterns = securityProperties.getIgnore();
        if (CollectionUtils.isNotEmpty(excludePatterns)) {
            for (String url : excludePatterns) {
                registry.antMatchers(url).permitAll();
            }
        }
        RestfulAccessDeniedHandler restfulAccessDeniedHandler = new RestfulAccessDeniedHandler();
        RestAuthenticationEntryPoint restAuthenticationEntryPoint = new RestAuthenticationEntryPoint();
        // 任何请求需要身份认证
        registry.and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                // 关闭跨站请求防护及不使用session
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        // 禁用缓存
        httpSecurity.headers().cacheControl();
        //添加自定义未授权和未登录结果返回
        httpSecurity.exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint);
        if (securityProperties.isAllowUrlCtrl()) {
            registry.and().addFilterBefore(dynamicSecurityFilter(), FilterSecurityInterceptor.class);
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter = new JwtAuthenticationTokenFilter();
        jwtAuthenticationTokenFilter.setJwtTokenService(jwtTokenService);
        jwtAuthenticationTokenFilter.setUserDetailsService(userDetailsService);
        return jwtAuthenticationTokenFilter;
    }

    @Bean
    @ConditionalOnProperty(name = {"security.allow-url-ctrl"}, havingValue = "true")
    public DynamicAccessDecisionManager dynamicAccessDecisionManager() {
        log.info("DynamicAccessDecisionManager registered success! ");
        return new DynamicAccessDecisionManager();
    }

    @Bean
    @ConditionalOnProperty(name = {"security.allow-url-ctrl"}, havingValue = "true")
    public DynamicSecurityFilter dynamicSecurityFilter() {
        return new DynamicSecurityFilter(dynamicSecurityMetadataSource(), securityProperties);
    }

    @Bean
    @ConditionalOnProperty(name = {"security.allow-url-ctrl"}, havingValue = "true")
    public DynamicSecurityMetadataSource dynamicSecurityMetadataSource() {
        DynamicSecurityMetadataSource dynamicSecurityMetadataSource = new DynamicSecurityMetadataSource();
        dynamicSecurityMetadataSource.setDynamicSecurityService(dynamicSecurityService);
        return dynamicSecurityMetadataSource;
    }
}
