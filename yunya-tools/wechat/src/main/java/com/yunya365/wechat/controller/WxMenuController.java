package com.yunya365.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.wechat.service.impl.WXService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Api(tags = {"微信公众号菜单"})
@RestController
@Slf4j
@CrossOrigin
public class WxMenuController {

    @Resource
    private WXService wxService;

    @ApiOperation(value = "获取当前菜单")
    @GetMapping(value = "/wxMenu")
    public ResponseResult get() {
        return ResponseUtil.success(wxService.menuGet());
    }

    @ApiOperation(value = "创建菜单")
    @PostMapping(value = "/wxMenu")
    public ResponseResult create(@Valid @RequestBody JSONObject menu) {
        return ResponseUtil.success(wxService.menuCreate(menu));
    }
}
