package com.yunya365.mini.controller;

import com.yunya.feign.ivy_mini.domain.vo.BannerVO;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.BannerServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/21
 * @description:
 */
@RestController
@Api(tags = "小程序-banner")
@IgnoreUserToken
public class BannerAppController extends BaseController{

    @Resource
    private BannerServiceImpl bannerService;

    @PostMapping("/wechat/banner/findlist")
    @ApiOperation("小程序-banner-列表")
    public ResponseResult<List<BannerVO>> findList() {
        return ResponseUtil.success(bannerService.findList());
    }
}
