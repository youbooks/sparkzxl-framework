package com.github.sparkzxl.sms.support;


import lombok.Getter;

/**
 * description: 短信异常
 *
 * @author zhouxinlei
 * @date 2022-01-03 15:41:54
 */
@Getter
public class SmsException extends RuntimeException {

    private static final long serialVersionUID = -9028217853132686956L;

    private int code;

    private String message;

    public SmsException(Throwable cause) {
        super(cause);
    }

    public SmsException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}