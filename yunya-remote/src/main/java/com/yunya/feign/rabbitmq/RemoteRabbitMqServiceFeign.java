package com.yunya.feign.rabbitmq;

import com.yunya.feign.rabbitmq.factory.RabbitMqFallBackFactory;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = YunyaServiceNameConstants.YUNYA_RABBIT_MQ,
        fallbackFactory = RabbitMqFallBackFactory.class)
public interface RemoteRabbitMqServiceFeign {

    /**
     * 发送更新中间表的消息
     *
     * @param messageModel 消息体
     * @return 成功返回ok
     */
    @RequestMapping(value = "/api/direct/single", method = RequestMethod.POST)
    String sendMessage(@RequestBody MessageModel messageModel);
}
