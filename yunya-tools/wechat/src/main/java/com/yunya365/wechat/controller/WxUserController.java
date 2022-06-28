package com.yunya365.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.wechat.domain.vo.WxAuthVo;
import com.yunya.feign.wechat.domain.vo.WxUserInfoVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.wechat.service.impl.WXService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 简介：微信用户控制器
 *
 * @author: chenlin
 * @Description: 微信用户控制器
 * @Date: 2022/6/27 15:08
 * @since: 1.0.0
 */
@Api(tags = "微信用户控制器")
@RestController
public class WxUserController {

    @Resource
    private WXService wxService;

    /**
     * 根据用户授权code获取微信用户信息
     *
     * @param code 授权code
     * @return
     */
    @ApiOperation("根据用户授权code获取微信用户信息")
    @GetMapping("/wxUser/info/{code}")
    public ResponseResult<WxUserInfoVO> getAuthWxUserInfo(@RequestParam("code") String code) {
        WxUserInfoVO user = wxService.getAuthWxUserInfo(code);
        return ResponseUtil.success(user);
    }
}
