package com.yunya365.mini.controller;

import com.yunya.feign.ivy_mini.domain.form.WeChatLoginForm;
import com.yunya.feign.ivy_mini.domain.vo.AuthInfoVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.LoginServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @description:
 * @author: xy
 **/
@RestController
@RequestMapping("/white")
@Api(tags = "小程序用户登录")
public class LoginController {

    @Resource
    private LoginServiceImpl authLoginService;

    @PostMapping("/mini/wechat/login")
    @ApiOperation("微信授权登录")
    public ResponseResult<AuthInfoVO> wechatLogin(@RequestBody @Valid WeChatLoginForm form, HttpServletRequest request) {
        return ResponseUtil.success(authLoginService.wechatLogin(form, request));
    }

    @PostMapping("/mini/logout")
    @ApiOperation("登出")
    public ResponseResult<Boolean> login(HttpServletRequest request) {
        authLoginService.logout(request);
        return ResponseUtil.success();
    }

}
