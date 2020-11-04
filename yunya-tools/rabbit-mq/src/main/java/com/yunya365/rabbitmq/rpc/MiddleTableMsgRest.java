package com.yunya365.rabbitmq.rpc;

import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MiddleTableMsgRest {

    @Autowired
    RabbitTemplate rabbitTemplate;

    private String sendMessageTemp(MessageModel messageModel) {
        String messageId = String.valueOf(UUID.randomUUID());
        messageModel.setMsgID(messageId);
        // 将消息携带绑定键值：DirectExchange_MiddleSingle 发送到交换机：DirectRouting_Temp
        rabbitTemplate.convertAndSend(
                "DirectExchange_MiddleSingle", "DirectRouting_Temp", messageModel, message -> {
                    // 设置超时时间 3000ms
                    message.getMessageProperties().setExpiration("5000");
                    return message;
                });
        return "ok";
    }

    @PostMapping("direct/single")
    public String sendDirectMessage(@RequestBody MessageModel messageModel) {
        return sendMessageTemp(messageModel);
    }

    @PostMapping("/direct/single1")
    public String sendDirectMessage(
            @RequestParam("dataId") Integer dataId,
            @RequestParam("operateType") Integer operateType,
            @RequestParam("msgCategoryEnum") MsgCategoryEnum msgCategoryEnum) {
        MessageModel messageModel = new MessageModel();
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("id", dataId);
        messageModel.setParamMap(paramMap);
        messageModel.setOperateType(operateType);
        messageModel.setMsgCategoryEnum(msgCategoryEnum);
        return sendMessageTemp(messageModel);
    }

    @PostMapping("/direct/single2")
    public String sendDirectMessage(
            @RequestParam("dataId") Integer dataId,
            @RequestParam("dateType") Integer dateType,
            @RequestParam("operateType") Integer operateType,
            @RequestParam("msgCategoryEnum") MsgCategoryEnum msgCategoryEnum) {
        MessageModel messageModel = new MessageModel();
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("id", dataId);
        paramMap.put("type", dateType);
        messageModel.setParamMap(paramMap);
        messageModel.setOperateType(operateType);
        messageModel.setMsgCategoryEnum(msgCategoryEnum);
        return sendMessageTemp(messageModel);
    }

    @PostMapping("/direct/single3")
    public String sendDirectMessage(
            @RequestParam("paramMap") Map<String, Object> paramMap,
            @RequestParam("operateType") Integer operateType,
            @RequestParam("msgCategoryEnum") MsgCategoryEnum msgCategoryEnum) {
        MessageModel messageModel = new MessageModel();
        messageModel.setParamMap(paramMap);
        messageModel.setOperateType(operateType);
        messageModel.setMsgCategoryEnum(msgCategoryEnum);
        return sendMessageTemp(messageModel);
    }
}
