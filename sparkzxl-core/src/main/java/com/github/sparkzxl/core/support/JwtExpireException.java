package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.support.code.IErrorCode;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import lombok.Getter;

/**
 * description: jwt过期异常类
 *
 * @author zhouxinlei
 */
@Getter
public class JwtExpireException extends BaseUncheckedException {

    private static final long serialVersionUID = -6710673514378835453L;

    public JwtExpireException() {
        super(ResultErrorCode.LOGIN_EXPIRE);
    }

    public JwtExpireException(String message) {
        super(ResultErrorCode.LOGIN_EXPIRE.getErrorCode(), message);
    }

    public JwtExpireException(IErrorCode errorCode) {
        super(errorCode.getErrorCode(), errorCode.getErrorMsg());
    }

    public JwtExpireException(IErrorCode errorCode, String message) {
        super(errorCode.getErrorCode(), message);
    }

    public JwtExpireException(IErrorCode errorCode,
                              String message,
                              Throwable cause) {
        super(errorCode.getErrorCode(), message, cause);
    }
}
