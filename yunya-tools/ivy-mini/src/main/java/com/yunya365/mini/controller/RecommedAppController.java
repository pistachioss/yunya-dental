package com.yunya365.mini.controller;

import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.entity.recommed;
import com.yunya365.mini.service.impl.RecommedServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/21
 * @description:
 */
@RestController
@Api(tags = "小程序-推荐专区")
@IgnoreUserToken
public class RecommedAppController extends BaseController{

    @Resource
    private RecommedServiceImpl recommedService;

    @PostMapping("/recommed/findlist")
    @ApiOperation("后台-推荐专区-查询")
    public ResponseResult<recommed> findList() {
        return ResponseUtil.success(recommedService.findList());
    }
}
