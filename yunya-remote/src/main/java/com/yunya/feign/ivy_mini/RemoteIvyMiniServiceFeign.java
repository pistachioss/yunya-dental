package com.yunya.feign.ivy_mini;

import com.yunya.feign.ivy_mini.domain.form.VirtualActiveForm;
import com.yunya.feign.ivy_mini.factory.RemoteIvyMiniServiceFeignFallBackFactory;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 简介: 服务接口调用
 */
@FeignClient(
        name = YunyaServiceNameConstants.IVY_MINI,
        fallbackFactory = RemoteIvyMiniServiceFeignFallBackFactory.class)
public interface RemoteIvyMiniServiceFeign {

    @ApiOperation("推送共用接口")
    @RequestMapping(value = "/wxVip/push/msg", method = RequestMethod.POST)
    void pushTemplate(@RequestBody @Validated WxTemplateMsgModel msgModel);

    @PostMapping("/orderVirtual/active/save")
    void activeCard(@RequestBody @Valid VirtualActiveForm form);

    @GetMapping("/card/refund")
    public boolean cardRefund( @RequestParam Integer cardId);
}
