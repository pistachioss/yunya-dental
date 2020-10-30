package com.yunya365.rabbitmq.rpc;

import com.yunya.feign.report.domain.model.MessageModel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MiddleTableMsgRest {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @PostMapping("direct/single")
    public String sendDirectMessage(@RequestBody MessageModel messageModel) {
        String messageId = String.valueOf(UUID.randomUUID());
        messageModel.setMsgID(messageId);
        //将消息携带绑定键值：DirectExchange_MiddleSingle 发送到交换机：DirectExchange_MiddleSingle
        rabbitTemplate.convertAndSend("DirectExchange_MiddleSingle", "DirectRouting_MiddleSingle", messageModel);
        return "ok";
    }
}
