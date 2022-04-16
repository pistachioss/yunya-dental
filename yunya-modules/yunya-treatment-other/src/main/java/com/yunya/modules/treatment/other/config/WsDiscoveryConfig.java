package com.yunya.modules.treatment.other.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.cloud.nacos.registry.NacosServiceRegistry;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

@Configuration
public class WsDiscoveryConfig implements SmartInitializingSingleton, ApplicationListener<ContextClosedEvent> {

    @Value("${netty.discovery.namespace}")
    String namespace;

    @Value("${netty.discovery.server-addr}")
    String serverAddr;
    // 自定义配置类
    @Autowired
    WsProperties wsProperties;

    // 注册 NacosRegistration
    @Autowired
    NacosServiceRegistry registry;

    @Autowired
    ApplicationContext context;

    @Autowired
    InetUtils inetUtils;

    NacosRegistration registration;


    @Override
    public void afterSingletonsInstantiated() {
        NacosDiscoveryProperties properties = new NacosDiscoveryProperties();
        properties.setNamespace(namespace);
        properties.setPort(wsProperties.getPort());
        properties.setIp(wsProperties.getFirstIp());
        properties.setServerAddr(serverAddr);
        properties.setService(wsProperties.getServiceName());
        registration = new NacosRegistration(properties, context);
        registration.setPort(wsProperties.getPort());
        registry.register(registration);

    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        registry.deregister(registration);

    }
}