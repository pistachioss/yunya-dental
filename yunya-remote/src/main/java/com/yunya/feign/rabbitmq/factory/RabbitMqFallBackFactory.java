package com.yunya.feign.rabbitmq.factory;

import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RabbitMqFallBackFactory implements RemoteRabbitMqServiceFeign {
  @Override
  public String sendMessage(MessageModel messageModel) {
    log.error("remoteRabbitMqServiceFeign: rabbitmq service 未启用");
    return "fail";
  }

  @Override
  public String sendMessage(Integer dataId, Integer operateType, MsgCategoryEnum msgCategoryEnum) {
    log.error("remoteRabbitMqServiceFeign: rabbitmq service 未启用");
    return "fail";
  }

  @Override
  public String sendMessage(
      Integer dataId, Integer dateType, Integer operateType, MsgCategoryEnum msgCategoryEnum) {
    log.error("remoteRabbitMqServiceFeign: rabbitmq service 未启用");
    return "fail";
  }

  @Override
  public String sendMessage(
      Map<String, Object> paramMap, Integer operateType, MsgCategoryEnum msgCategoryEnum) {
    log.error("remoteRabbitMqServiceFeign: rabbitmq service 未启用");
    return "fail";
  }
}
