package com.github.sparkzxl.alarm.wetalk.strategy;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;
import com.github.sparkzxl.alarm.strategy.MessageSource;

/**
 * description: 企业微信卡片类型消息
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:32:43
 */
public class ActionCardMsgHandleStrategy implements MsgHandleStrategy {

    @Override
    public MsgType getMessage(AlarmRequest request) {
        throw new AlarmException(AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorCode(), AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorMsg());
    }

    @Override
    public String unionId() {
        MessageSource messageSource = new MessageSource();
        messageSource.setMessageType(MessageSubType.ACTION_CARD.getCode());
        messageSource.setAlarmType(AlarmType.WETALK.getType());
        return messageSource.convert();
    }
}
