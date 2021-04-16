package com.yunya365.wechat.config;

import com.yunya.feign.wechat.domain.model.WxUserMsgModel;
import com.yunya.feign.wechat.domain.vo.WxSendMsgVo;
import com.yunya365.wechat.enums.NotifyEnum;
import com.yunya365.wechat.service.WeChatNotify;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.*;


/**
 * @description:
 * @author: xy
 * @date 2021/3/25 10:38
 **/
@Component
public class NotifyFactory implements ApplicationContextAware {
    /**
     * 策略列表
     */
    private Map<NotifyEnum, WeChatNotify> notifyMap = new HashMap<>();

    /**
     * 工厂获取事件执行策略对象
     *
     * @param notifyType
     * @return
     */
    public WeChatNotify loadWeChatNotify(NotifyEnum notifyType) {
        WeChatNotify notify = notifyMap.get(notifyType);
        //对于没配置的策略 返回一个默认的空实现即可
        return Optional.ofNullable(notify).orElse(this::defaultNotify);
    }

    /**
     * 工厂提供默认空实现
     *
     * @param msgReq
     * @return
     */
    public WxSendMsgVo defaultNotify(WxUserMsgModel msgReq) {
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(NotifyType.class);
        Map<NotifyEnum[], WeChatNotify> collect = beans.values().stream().filter(obj -> ArrayUtils.contains(obj.getClass().getInterfaces(), WeChatNotify.class))
                .map(obj -> (WeChatNotify) obj)
                .collect(toMap(obj -> obj.getClass().getAnnotation(NotifyType.class).value(), Function.identity()));
        collect.forEach((key, value) -> Arrays.stream(key).forEach(obj -> notifyMap.put(obj, value)));
    }
}
