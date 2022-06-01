package com.yunya365.mini.controller;

import com.yunya.feign.ivy_mini.domain.form.DistributionForm;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.entity.Distribution;
import com.yunya365.mini.service.impl.DistributionServiceImpl;
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
 * @date: 2022/5/16
 * @description:
 */
@RestController
@Api(tags = "配送设置管理")
public class DistributionController extends PcBaseController{
    @Resource
    private DistributionServiceImpl distributionService;

    @PostMapping("/distribution/findlist")
    @ApiOperation("后台-配送设置管理-查询")
    public ResponseResult<Distribution> findList() {
        return ResponseUtil.success(distributionService.findList());
    }

    @PostMapping("/distribution/add")
    @ApiOperation("后台-配送设置管理-新增/修改 因为只有一条 新增也是修改 每次都会重新插入")
    @RepeatSubmit
    public ResponseResult add(@RequestBody @Valid DistributionForm form) {
        distributionService.add(form);
        return ResponseUtil.success(null);
    }

}
