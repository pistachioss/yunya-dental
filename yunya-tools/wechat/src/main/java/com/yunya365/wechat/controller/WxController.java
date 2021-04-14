package com.yunya365.wechat.controller;

import com.yunya.feign.wechat.domain.model.*;
import com.yunya.feign.wechat.domain.vo.*;
import com.yunya.framework.common.model.*;
import com.yunya.framework.common.utils.*;
import com.yunya365.wechat.service.impl.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.*;
import javax.validation.*;
import javax.validation.constraints.*;

/**
 * @description:
 * @author: xy
 * @date 2021/4/2 10:53
 **/
@Api(tags = {"微信公众号授权，消息推送"})
@RestController
@Slf4j
public class WxController {

    @Resource
    private WXService wxService;

    @GetMapping(value = "/wxVip/auth")
    @ApiOperation(value = "获取用户授权信息")
    public ResponseResult<WxAuthVo> getUserOpenId(@RequestParam String code) {
        return ResponseUtil.success(wxService.getAuthInfo(code));
    }

    @PostMapping(value = "/wxVip/home/register")
    @ApiOperation(value = "会员注册")
    public ResponseResult wxRegister(@NotBlank @RequestParam(required = true) String openId, @Valid @RequestBody WxRegisterModel model) {
        wxService.register(openId, model);
        return ResponseUtil.success();
    }

    @GetMapping(value = "/wxVip/home/vipInfo")
    @ApiOperation(value = "会员中心")
    public ResponseResult<WxVipInfoVo> vipInfo(@NotBlank @RequestParam(required = true) String openId,
                                               @RequestParam(required = false) Integer patientId) {
        WxVipInfoVo wxVipInfoVo = wxService.vipInfo(openId, patientId);
        return ResponseUtil.success(wxVipInfoVo);
    }

}
