package com.yunya.feign.wechat.factory;

import com.yunya.feign.wechat.RemoteWechatServiceFeign;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 简介: 就诊、价目表服务调用降级处理 111
 *
 * @author: chow
 * @date: 2020/8/6 17:53
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Component
public class RemoteWechatServiceFeignFallBackFactory implements RemoteWechatServiceFeign {

    @Override
    public void pushTemplate(WxTemplateMsgModel msgModel) {
    }

    @Override
    public void batchPushTemplate(List<WxTemplateMsgModel> list) {

    }
}
