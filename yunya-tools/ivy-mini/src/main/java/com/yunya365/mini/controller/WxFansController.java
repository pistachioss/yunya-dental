package com.yunya365.mini.controller;


import com.yunya.feign.ivy_mini.domain.form.WxAuthUserInfoForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.IWxFansService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 公司微信公众号粉丝 前端控制器
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-11
 */
@RestController
@Api(tags = "小程序用户api")
public class WxFansController extends BaseController{

    @Resource
    private IWxFansService wxFansService;


    @PostMapping("/member/auth/complete")
    @ApiOperation("微信授权手机号")
    @CurrentUser
    public ResponseResult<String> save(@Valid @RequestBody WxAuthUserInfoForm form) {
        return ResponseUtil.success(wxFansService.competeAuthPhone(form));
    }
}

