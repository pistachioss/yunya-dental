package com.yunya365.rabbitmq.controller;

import com.rabbitmq.client.Channel;
import com.yunya.feign.report.domain.model.MessageModel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
//@RabbitListener(queues = {"DirectExchange_Temp"})
public class ReceiverMessageController {

    public static int n = 0;

//    @RabbitHandler
    public void handleTest1(MessageModel messageModel, Channel channel, Message message) throws Exception {
        // 处理消息
        System.out.println("handleMessage :" + messageModel.getMsgID());

        n++;
        switch (n % 3) {
            case 0:
                // 消费成功：确认收到消息，消息将被队列移除，false只确认当前consumer一个消息收到，true确认所有consumer获得的消息。
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                break;
            case 1:
                // 确认否定消息：第一个boolean表示一个consumer还是所有，第二个boolean表示requeue是否重新回到队列，true重新入队。
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                break;
            case 2:
                // 拒绝消息：requeue=false 表示不再重新入队，如果配置了死信队列则进入死信队列。
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
                break;
        }
    }
}
