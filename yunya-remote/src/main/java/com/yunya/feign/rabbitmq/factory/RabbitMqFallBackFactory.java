package com.yunya.feign.rabbitmq.factory;

import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.domain.model.MessageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RabbitMqFallBackFactory implements RemoteRabbitMqServiceFeign {
    @Override
    public String sendMessage(MessageModel messageModel) {
        log.error("remoteRabbitMqServiceFeign: rabbitmq service 未启用");
        return "fail";
    }
}
