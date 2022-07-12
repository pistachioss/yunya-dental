package com.yunya365.mini.config;

import com.rabbitmq.client.Channel;
import com.yunya.feign.report.domain.model.MessageOrderModel;
import com.yunya365.mini.service.IOrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
@RabbitListener(queues = {"DirectQueue_Order_Delay"})
@Slf4j
public class OrderMessageMqReceiver {

    @Resource
    private IOrderInfoService orderInfoService;

    @RabbitHandler
    public void handleMiddleSingle(MessageOrderModel messageModel, Channel channel, Message message)
            throws Exception {
        // 处理消息
        log.info("【订单消息体】：handleMessage[{}]", messageModel);
        int result = 0;
        try {
            Map<String, Object> paramMap = messageModel.getParamMap();
            orderInfoService.handleDelayPay((Integer) paramMap.get("order_id"));
        } catch (Exception e) {
            log.warn("【订单支付消息异常】:", e);
            result = 2;
        }
        switch (result) {
            case 0:
                // 消费成功：确认收到消息，消息将被队列移除，false只确认当前consumer一个消息收到，true确认所有consumer获得的消息。
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                log.info("【消费成功】订单消息已被确认");
                break;
            case 1:
                // 确认否定消息：第一个boolean表示一个consumer还是所有，第二个boolean表示requeue是否重新回到队列，true重新入队。
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                log.info("【消费否定】订单消息已被否定");
                break;
            case 2:
                // 拒绝消息：requeue=false 表示不再重新入队，如果配置了死信队列则进入死信队列。
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                log.info("【消费失败】订单消息已被拒绝");
                break;
            default:
                break;
        }
    }
}
