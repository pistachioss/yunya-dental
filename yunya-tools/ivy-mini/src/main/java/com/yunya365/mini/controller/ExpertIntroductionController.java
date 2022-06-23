package com.yunya365.mini.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.ExpertIntroductionForm;
import com.yunya.feign.ivy_mini.domain.vo.ExpertIntroductionVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.ExpertIntroductionServiceImpl;
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
@Api(tags = "小程序-专家介绍")
public class ExpertIntroductionController extends BaseController{

    @Resource
    private ExpertIntroductionServiceImpl expertIntroductionService;

    @PostMapping("/expertIntroduction/findlist")
    @ApiOperation("小程序-专家介绍-列表")
    public ResponseResult<PageInfo<ExpertIntroductionVO>> findList(@RequestBody @Valid ExpertIntroductionForm form) {
        return ResponseUtil.success(expertIntroductionService.findList(form));
    }
}
