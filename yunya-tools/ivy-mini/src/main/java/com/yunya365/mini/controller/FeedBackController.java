package com.yunya365.mini.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.FeedBackAddForm;
import com.yunya.feign.ivy_mini.domain.form.FeedBackForm;
import com.yunya.feign.ivy_mini.domain.vo.FeedBackVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.FeedBackServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/23
 * @description:
 */
@RestController
@Api(tags = "小程序-意见反馈管理")
public class FeedBackController extends BaseController{
    @Resource
    private FeedBackServiceImpl feedBackService;

    @PostMapping("/feedback/add")
    @ApiOperation("小程序-意见反馈-新增")
    public ResponseResult findList(@RequestBody @Valid FeedBackAddForm form) {
        return ResponseUtil.success(feedBackService.add(form));
    }

}
