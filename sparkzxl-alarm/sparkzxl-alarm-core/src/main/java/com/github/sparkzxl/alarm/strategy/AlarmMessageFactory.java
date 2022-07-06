package com.github.sparkzxl.alarm.strategy;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;

/**
 * description: 告警消息工厂
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:59:08
 */
public class AlarmMessageFactory {
    private static final Map<String, MsgHandleStrategy> ALARM_MESSAGE_STRATEGY_MAP = Maps.newHashMap();

    static {
        Set<Class<?>> classSet = ClassUtil.scanPackageBySuper("com.github.sparkzxl.alarm", MsgHandleStrategy.class);
        classSet.forEach(cla -> {
            MsgHandleStrategy instance = (MsgHandleStrategy) ReflectUtil.newInstance(cla);
            ALARM_MESSAGE_STRATEGY_MAP.put(instance.unionId(), instance);
        });
    }


    public static MsgHandleStrategy create(String type, String messageType) {
        String unionId = MessageFormat.format("{0}#{1}", type, messageType);
        MsgHandleStrategy msgHandleStrategy = ALARM_MESSAGE_STRATEGY_MAP.get(unionId);
        if (ObjectUtils.isEmpty(msgHandleStrategy)) {
            throw new AlarmException(AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorCode(), AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorMsg());
        }
        return msgHandleStrategy;
    }
}
