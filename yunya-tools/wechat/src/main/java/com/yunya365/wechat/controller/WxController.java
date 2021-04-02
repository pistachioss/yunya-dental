package com.yunya365.wechat.controller;

import com.yunya.feign.wechat.domain.model.WxRegisterModel;
import com.yunya.feign.wechat.domain.vo.WxAuthVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.wechat.service.impl.WXService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

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

    @GetMapping(value = "/auth")
    @ApiOperation(value = "获取用户授权信息")
    public ResponseResult<WxAuthVo> getUserOpenId(@RequestParam String code) {
        return ResponseUtil.success(wxService.getAuthInfo(code));
    }

    @PostMapping(value = "/auth")
    @ApiOperation(value = "获取用户授权信息")
    public void wxRegister(@NotBlank @RequestParam(required = true) String openId, @Valid @RequestBody WxRegisterModel model) {
        wxService.register(openId, model);
    }
}
