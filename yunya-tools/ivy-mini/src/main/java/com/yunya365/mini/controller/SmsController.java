package com.yunya365.mini.controller;

import com.yunya.feign.ivy_mini.domain.form.SmsCaptchaCheckForm;
import com.yunya.feign.ivy_mini.domain.form.SmsCaptchaForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.ICaptchaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @description:
 * @author: xy
 * @date 2021/11/15 12:38
 **/
@RestController
@Api(tags = "手机验证码")
public class SmsController extends BaseController {

    @Resource
    private ICaptchaService captchaService;

    @PostMapping("/captcha/sms")
    @ApiOperation("发送验证码")
    public ResponseResult<Boolean> sendSmsCaptcha(@Valid @RequestBody SmsCaptchaForm form) {
        captchaService.sendSmsCaptcha(form.getPhoneNumber());
        return ResponseUtil.success();
    }

    @ApiOperation("【小程序】患者绑定校验")
    @PostMapping("/captcha/checking")
    public ResponseResult<Boolean> checkCaptcha(@Valid @RequestBody SmsCaptchaCheckForm form) {
        captchaService.checkPatientSmsCaptcha(form);
        return ResponseUtil.success();
    }
}
