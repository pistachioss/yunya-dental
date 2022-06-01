package com.yunya365.mini.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.recommedForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.entity.recommed;
import com.yunya365.mini.service.impl.RecommedServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/12
 * @description:
 */
@RestController
@RequestMapping("/recommed")
@Api(tags = "推荐专区")
public class RecommedController extends PcBaseController{

    @Resource
    private RecommedServiceImpl recommedService;

    @PostMapping("/findlist")
    @ApiOperation("后台-推荐专区-查询")
    public ResponseResult<recommed> findList() {
        return ResponseUtil.success(recommedService.findList());
    }

    @PostMapping("/add")
    @ApiOperation("后台-推荐专区-新增/修改 因为只有一条 新增也是修改 每次都会重新插入")
    @RepeatSubmit
    public ResponseResult add(@RequestBody @Valid recommedForm form) {
        recommedService.add(form);
        return ResponseUtil.success(null);
    }
}
