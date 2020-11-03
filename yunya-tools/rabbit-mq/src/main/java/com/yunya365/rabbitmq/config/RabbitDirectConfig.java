package com.yunya365.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitDirectConfig {

    @Bean
    DirectExchange DirectExchange1() {

        return new DirectExchange("DirectExchange_MiddleSingle", true, false, null);
    }

    @Bean
    public Queue DirectQueue_Temp() {

//        return new Queue("DirectQueue_Temp", true);
        // 设置超时转发策略 超时后消息会通过x-dead-letter-exchange 转发到x-dead-letter-routing-key绑定的队列中
        Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("x-dead-letter-exchange", "DirectExchange_MiddleSingle");
        arguments.put("x-dead-letter-routing-key", "DirectRouting_MiddleSingle");
        Queue queue = new Queue("DirectQueue_Temp",true,false,false,arguments);
        return queue;
    }

    @Bean
    public Queue DirectQueue1() {

        return new Queue("DirectQueue_MiddleSingle", true, false, false);
    }

    @Bean
    Binding bindingDirect_Temp() {

        return BindingBuilder.bind(DirectQueue_Temp()).to(DirectExchange1()).with("DirectRouting_Temp");
    }

    @Bean
    Binding bindingDirect1() {

        return BindingBuilder.bind(DirectQueue1()).to(DirectExchange1()).with("DirectRouting_MiddleSingle");
    }

}
