package com.yunya365.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitDirectConfig implements BeanPostProcessor {

    @Resource
    private RabbitAdmin rabbitAdmin;

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 只有设置为 true，spring 才会加载 RabbitAdmin 这个类
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

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
    public Queue DirectQueue_Order() {
        // 设置超时转发策略 超时后消息会通过x-dead-letter-exchange 转发到x-dead-letter-routing-key绑定的队列中
        Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("x-dead-letter-exchange", "DirectExchange_MiddleSingle");
        arguments.put("x-dead-letter-routing-key", "DirectRouting_Order_Delay");
        return new Queue("DirectQueue_Order",true,false,false,arguments);
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

    @Bean
    Binding bindingDirect_Order() {
        return BindingBuilder.bind(DirectQueue_Order()).to(DirectExchange1()).with("DirectRouting_Order");
    }

    @Bean
    public Queue directOrderQueueDelay() {
        return new Queue("DirectQueue_Order_Delay", true, false, false);
    }

    @Bean
    Binding bindingOrderDirectDelay() {
        return BindingBuilder.bind(directOrderQueueDelay()).to(DirectExchange1()).with("DirectRouting_Order_Delay");
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //创建交换机
        rabbitAdmin.declareExchange(DirectExchange1());
        //创建队列
        rabbitAdmin.declareQueue(DirectQueue_Temp());
        rabbitAdmin.declareQueue(DirectQueue1());
        rabbitAdmin.declareQueue(DirectQueue_Order());
        rabbitAdmin.declareQueue(directOrderQueueDelay());
        return null;
    }

}
