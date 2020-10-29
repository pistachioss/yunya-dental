package com.yunya365.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitDirectConfig {

    @Bean
    public Queue DirectQueue1() {

        return new Queue("DirectQueue_MiddleSingle", true);
    }


    @Bean
    DirectExchange DirectExchange1() {

        return new DirectExchange("DirectExchange_MiddleSingle", true, false);
    }

    @Bean
    Binding bindingDirect1() {

        return BindingBuilder.bind(DirectQueue1()).to(DirectExchange1()).with("DirectRouting_MiddleSingle");
    }

}
