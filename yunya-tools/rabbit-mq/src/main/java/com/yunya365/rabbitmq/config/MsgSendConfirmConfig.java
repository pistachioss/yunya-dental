package com.yunya365.rabbitmq.config;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class MsgSendConfirmConfig implements RabbitTemplate.ConfirmCallback {
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        System.out.println("MsgSendConfirmCallBack  , 回调id:" + correlationData);
        if (b) {
            System.out.println("消息发送成功");
        } else {
            System.out.println("消息发送失败:" + s+"\n重新发送");
        }
    }
}
