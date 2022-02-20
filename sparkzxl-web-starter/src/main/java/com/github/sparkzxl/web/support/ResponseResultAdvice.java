package com.github.sparkzxl.web.support;

import cn.hutool.core.convert.Convert;
import com.github.sparkzxl.annotation.response.IgnoreResponseWrap;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.entity.response.Response;
import com.github.sparkzxl.core.util.RequestContextHolderUtils;
import com.github.sparkzxl.entity.response.ResponseCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * description: 判断是否需要返回值包装，如果需要就直接包装
 *
 * @author zhouxinlei
 */
@Slf4j
@ControllerAdvice
public class ResponseResultAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        final IgnoreResponseWrap[] declaredAnnotationsByType = returnType.getExecutable().getDeclaredAnnotationsByType(IgnoreResponseWrap.class);
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        com.github.sparkzxl.annotation.response.Response response = (com.github.sparkzxl.annotation.response.Response) servletRequest.getAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        Boolean supported = ObjectUtils.isNotEmpty(response) && declaredAnnotationsByType.length == 0;
        if (log.isDebugEnabled()) {
            log.debug("判断是否需要全局统一API响应：{}", supported ? "是" : "否");
        }
        return supported;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<?
            extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = RequestContextHolderUtils.getResponse();
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        servletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (body instanceof Response) {
            return body;
        }
        Boolean fallback = Convert.toBool(RequestContextHolderUtils.getAttribute(BaseContextConstants.REMOTE_CALL), Boolean.FALSE);
        int status = servletResponse.getStatus();
        Response<?> result;
        if (fallback) {
            result = Response.failDetail(ExceptionErrorCode.SERVICE_DEGRADATION.getCode(), ExceptionErrorCode.SERVICE_DEGRADATION.getMessage());
        } else if (body instanceof Boolean && !(Boolean) body) {
            result = Response.failDetail(ExceptionErrorCode.FAILURE.getCode(), ExceptionErrorCode.FAILURE.getMessage());
        } else if (status == ResponseCode.FAILURE.getCode()) {
            result = Response.failDetail(ExceptionErrorCode.INTERNAL_SERVER_ERROR.getCode(), ExceptionErrorCode.INTERNAL_SERVER_ERROR.getMessage());
            servletResponse.setStatus(ResponseCode.SUCCESS.getCode());
        } else {
            result = Response.success(body);
        }
        return result;
    }
}
