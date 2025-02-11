package com.github.sparkzxl.gateway.plugin.logging;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;

/**
 * description: Context Use gateway Request Log
 *
 * @author zhouxinlei
 * @since 2022-01-10 10:05:12
 */
@Getter
@Setter
@ToString
public class LogContext implements Serializable {

    private static final long serialVersionUID = -3144226237800826700L;

    /**
     * cache form data
     */
    protected MultiValueMap<String, String> formData;
    /**
     * cache json body
     */
    protected String requestBody;
    /**
     * cache Response Body
     */
    protected String responseBody;

}
