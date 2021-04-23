package com.yunya365.wechat.rpc;

import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya365.wechat.service.impl.WXService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2021/4/2 10:53
 **/
@Api(tags = {"微信会员中心"})
@RestController
public class WxPushRpc {

    @Resource
    private WXService wxService;

    @PostMapping(value = "/wxVip/template/msg/push")
    public void pushTemplate(@RequestBody WxTemplateMsgModel msgModel) {
        wxService.pushTemplateMsg(msgModel);
    }

    @PostMapping(value = "/wxVip/template/msg/batch/push")
    public void batchPushTemplate(@RequestBody List<WxTemplateMsgModel> list) {
        wxService.batchPushTemplate(list);
    }

}
