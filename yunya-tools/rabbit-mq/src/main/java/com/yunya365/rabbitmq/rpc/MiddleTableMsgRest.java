package com.yunya365.rabbitmq.rpc;

import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MiddleTableMsgRest {

  @Autowired RabbitTemplate rabbitTemplate;

  @PostMapping("direct/single")
  public String sendDirectMessage(@RequestBody MessageModel messageModel) {
    String messageId = String.valueOf(UUID.randomUUID());
    messageModel.setMsgID(messageId);
    // 将消息携带绑定键值：DirectExchange_MiddleSingle 发送到交换机：DirectExchange_MiddleSingle
    rabbitTemplate.convertAndSend(
        "DirectExchange_MiddleSingle", "DirectRouting_MiddleSingle", messageModel);
    return "ok";
  }

  @PostMapping("/direct/single1")
  public String sendDirectMessage(
      Integer dataId, Integer operateType, MsgCategoryEnum msgCategoryEnum) {
    MessageModel messageModel = new MessageModel();
    Map<String, Object> paramMap = new HashMap<>(16);
    paramMap.put("id", dataId);
    messageModel.setParamMap(paramMap);
    messageModel.setOperateType(operateType);
    messageModel.setMsgCategoryEnum(msgCategoryEnum);
    String messageId = String.valueOf(UUID.randomUUID());
    messageModel.setMsgID(messageId);
    // 将消息携带绑定键值：DirectExchange_MiddleSingle 发送到交换机：DirectExchange_MiddleSingle
    rabbitTemplate.convertAndSend(
        "DirectExchange_MiddleSingle", "DirectRouting_MiddleSingle", messageModel);
    return "ok";
  }

  @PostMapping("/direct/single2")
  public String sendDirectMessage(
      Integer dataId, Integer dateType, Integer operateType, MsgCategoryEnum msgCategoryEnum) {
    MessageModel messageModel = new MessageModel();
    Map<String, Object> paramMap = new HashMap<>(16);
    paramMap.put("id", dataId);
    paramMap.put("type", dateType);
    messageModel.setParamMap(paramMap);
    messageModel.setOperateType(operateType);
    messageModel.setMsgCategoryEnum(msgCategoryEnum);
    String messageId = String.valueOf(UUID.randomUUID());
    messageModel.setMsgID(messageId);
    // 将消息携带绑定键值：DirectExchange_MiddleSingle 发送到交换机：DirectExchange_MiddleSingle
    rabbitTemplate.convertAndSend(
        "DirectExchange_MiddleSingle", "DirectRouting_MiddleSingle", messageModel);
    return "ok";
  }

  @PostMapping("/direct/single3")
  public String sendDirectMessage(
      Map<String, Object> paramMap, Integer operateType, MsgCategoryEnum msgCategoryEnum) {
    MessageModel messageModel = new MessageModel();
    messageModel.setParamMap(paramMap);
    messageModel.setOperateType(operateType);
    messageModel.setMsgCategoryEnum(msgCategoryEnum);
    String messageId = String.valueOf(UUID.randomUUID());
    messageModel.setMsgID(messageId);
    // 将消息携带绑定键值：DirectExchange_MiddleSingle 发送到交换机：DirectExchange_MiddleSingle
    rabbitTemplate.convertAndSend(
        "DirectExchange_MiddleSingle", "DirectRouting_MiddleSingle", messageModel);
    return "ok";
  }
}
