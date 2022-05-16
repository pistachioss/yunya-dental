package com.yunya365.mini.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.FeedBackForm;
import com.yunya.feign.ivy_mini.domain.vo.FeedBackVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.FeedBackServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/16
 * @description:
 */
@RestController
@RequestMapping("/feedback")
@Api(tags = "意见反馈管理")
public class FeedBackController {

    @Resource
    private FeedBackServiceImpl feedBackService;

    @PostMapping("/findlist")
    @ApiOperation("意见反馈-列表")
    public ResponseResult<PageInfo<FeedBackVO>> findList(@RequestBody @Valid FeedBackForm form) {
        return ResponseUtil.success(feedBackService.findList(form));
    }
}
