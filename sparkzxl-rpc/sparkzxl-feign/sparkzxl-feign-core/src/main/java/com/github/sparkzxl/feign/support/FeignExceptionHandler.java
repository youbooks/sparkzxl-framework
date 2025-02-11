package com.github.sparkzxl.feign.support;

import cn.hutool.core.bean.OptionalBean;
import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.Response;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import com.github.sparkzxl.feign.exception.RemoteCallTransferException;
import feign.*;
import feign.codec.DecodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketTimeoutException;

/**
 * description: Springboot WEB应用全局异常处理
 *
 * @author zhouxinlei
 */
@Slf4j
@RestControllerAdvice
public class FeignExceptionHandler implements Ordered {

    @ExceptionHandler(SocketTimeoutException.class)
    public Response<?> handleSocketTimeoutException(SocketTimeoutException e) {
        log.error("SocketTimeoutException异常:", e);
        return Response.fail(ResultErrorCode.TIME_OUT_ERROR.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(RetryableException.class)
    public Response<?> handleRetryableException(RetryableException e) {
        log.error("RetryableException异常:", e);
        String applicationName =
                OptionalBean.ofNullable(e.request()).getBean(Request::requestTemplate).getBean(RequestTemplate::feignTarget).getBean(Target::name)
                        .orElseGet(() -> "unKnownServer");
        String message = StrFormatter.format(ResultErrorCode.RETRY_ABLE_EXCEPTION.getErrorMsg(), applicationName);
        return Response.fail(ResultErrorCode.RETRY_ABLE_EXCEPTION.getErrorCode(), message);
    }

    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public Response<?> handleServiceUnavailableException(FeignException.ServiceUnavailable e) {
        log.error("ServiceUnavailable异常:", e);
        String applicationName =
                OptionalBean.ofNullable(e.request()).getBean(Request::requestTemplate).getBean(RequestTemplate::feignTarget).getBean(Target::name)
                        .orElseGet(() -> "unKnownServer");
        String message = StrFormatter.format(ResultErrorCode.OPEN_SERVICE_UNAVAILABLE.getErrorMsg(), applicationName);
        return Response.fail(ResultErrorCode.OPEN_SERVICE_UNAVAILABLE.getErrorCode(), message);
    }

    @ExceptionHandler(DecodeException.class)
    public Response<?> handleDecodeException(DecodeException e) {
        log.error("DecodeException异常:", e);
        return Response.fail(ResultErrorCode.DECODE_EXCEPTION.getErrorCode(), ResultErrorCode.DECODE_EXCEPTION.getErrorMsg());
    }

    @ExceptionHandler(RemoteCallTransferException.class)
    public Response<?> handleRemoteCallException(RemoteCallTransferException e) {
        log.error("RemoteCallException异常:", e);
        String applicationName =
                OptionalBean.ofNullable(e.request()).getBean(Request::requestTemplate).getBean(RequestTemplate::feignTarget).getBean(Target::name)
                        .orElseGet(() -> "unKnownServer");
        String message = StrFormatter.format("【{}】异常,{}", applicationName, e.getErrorMsg());
        return Response.fail(e.getErrorCode(), message);
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.FEIGN_EXCEPTION_ORDER.getOrder();
    }
}
