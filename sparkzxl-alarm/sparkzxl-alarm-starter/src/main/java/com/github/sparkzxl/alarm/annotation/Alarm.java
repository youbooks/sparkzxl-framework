package com.github.sparkzxl.alarm.annotation;

import com.github.sparkzxl.alarm.constant.AlarmConstant;
import com.github.sparkzxl.alarm.enums.MessageSubType;

import java.lang.annotation.*;

/**
 * description: 告警注解
 *
 * @author zhouxinlei
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Alarm {

    /**
     * 报警名称
     *
     * @return String
     */
    String name() default "";

    MessageSubType messageType() default MessageSubType.TEXT;

    String templateId() default "";

    String variablesBeanName() default AlarmConstant.DEFAULT_ALARM_VARIABLES_HANDLER_BEAN_NAME;

    String extractParams() default "";

    /**
     * 表达式条件
     *
     * @return String
     */
    String expressionJson() default "";


}
