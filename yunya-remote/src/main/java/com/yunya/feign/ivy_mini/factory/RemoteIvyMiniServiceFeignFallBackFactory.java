package com.yunya.feign.ivy_mini.factory;

import com.yunya.feign.ivy_mini.RemoteIvyMiniServiceFeign;
import com.yunya.feign.ivy_mini.domain.form.VirtualActiveForm;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 简介: 降级处理
 *
 */
@Slf4j
@Component
public class RemoteIvyMiniServiceFeignFallBackFactory implements RemoteIvyMiniServiceFeign {

    @Override
    public void pushTemplate(WxTemplateMsgModel msgModel) {
    }

    @Override
    public void activeCard(VirtualActiveForm form) {

    }
}
