package com.yunya365.wechat.config;

import com.yunya365.wechat.enums.NotifyEnum;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 15:58
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface NotifyType {
    /**
     * 事件推送的类型 支持枚举多个事件
     * @return
     */
    NotifyEnum[] value();
}
