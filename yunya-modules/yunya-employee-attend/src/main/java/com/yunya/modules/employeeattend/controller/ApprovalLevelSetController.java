package com.yunya.modules.employeeattend.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.ApprovalLevelSetBiz;
import com.yunya.modules.employeeattend.form.ApprovalLevelSetQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介:审批人级别设置接口
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Api(tags = "审批人级别设置接口")
@RestController
@RequestMapping("/approvalLevel_set")
@CrossOrigin
public class ApprovalLevelSetController {

    @Autowired
    private ApprovalLevelSetBiz approvalLevelSetBiz;

    /**
     * 查看假期设置列表
     *
     * @param
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("查看审批人级别设置列表")
    public ResponseResult findList(@RequestBody @Validated ApprovalLevelSetQuery approvalLevelSetQuery) {
        return ResponseUtil.success(approvalLevelSetBiz.findList(approvalLevelSetQuery));
    }

    /**
     * 新增审批人级别设置
     *
     * @param
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增审批人级别设置")
    @CurrentUser
    public ResponseResult create(@RequestBody @Validated ApprovalLevelSetQuery approvalLevelSetQuery) {
        return ResponseUtil.success(approvalLevelSetBiz.create(approvalLevelSetQuery));
    }

    /**
     * 修改审批人级别设置
     *
     * @param
     * @return
     */
    @PutMapping()
    @ApiOperation("修改审批人级别设置")
    @CurrentUser
    public ResponseResult update(@RequestBody @Validated ApprovalLevelSetQuery approvalLevelSetQuery) {
        return ResponseUtil.success(approvalLevelSetBiz.update(approvalLevelSetQuery));
    }

    /**
     * 修改审批人级别设置
     *
     * @param
     * @return
     */
    @DeleteMapping()
    @ApiOperation("删除审批人级别设置")
    public ResponseResult delete(@RequestBody @Validated ApprovalLevelSetQuery approvalLevelSetQuery) {
        return ResponseUtil.success(approvalLevelSetBiz.delete(approvalLevelSetQuery));
    }
}
