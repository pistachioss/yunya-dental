package com.yunya.modules.employeeattend.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.employee_attend.VacationSet;
import com.yunya.modules.employeeattend.biz.ApprovalCriteriaBiz;
import com.yunya.modules.employeeattend.form.ApprovalCriteriaByDayForm;
import com.yunya.modules.employeeattend.form.ApprovalCriteriaForm;
import com.yunya.modules.employeeattend.form.VacationSetQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介: 审批条件设置接口
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Api(tags = "审批条件设置接口")
@RestController
@RequestMapping("/approval_criteria")
@CrossOrigin
public class ApprovalCriteriaController {

    @Autowired
    private ApprovalCriteriaBiz approvalCriteriaBiz;

    /**
     * 查看假期设置列表
     *
     * @param
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("查看审批条件列表")
    public ResponseResult findList() {
        return ResponseUtil.success(approvalCriteriaBiz.selectListAll());
    }

    /**
     * 根据请假日期获取审批条件
     *
     * @param
     * @return
     */
    @PostMapping("/findByDate")
    @ApiOperation("根据请假日期获取审批条件")
    public ResponseResult findByDate(@RequestBody @Validated ApprovalCriteriaByDayForm approvalCriteriaByDayForm) {
        return ResponseUtil.success(approvalCriteriaBiz.findByDate(approvalCriteriaByDayForm));
    }

    /**
     * 查看假期设置列表
     *
     * @param
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增审批条件")
    @CurrentUser
    public ResponseResult create(@RequestBody @Validated ApprovalCriteriaForm approvalCriteriaForm) {
        return ResponseUtil.success(approvalCriteriaBiz.create(approvalCriteriaForm));
    }

    /**
     * 修改审批条件
     *
     * @param
     * @return
     */
    @PutMapping()
    @ApiOperation("修改审批条件")
    public ResponseResult update(@RequestBody @Validated ApprovalCriteriaForm approvalCriteriaForm) {
        return ResponseUtil.success(approvalCriteriaBiz.update(approvalCriteriaForm));
    }

    /**
     * 删除假审批条件
     *
     * @param
     * @return
     */
    @DeleteMapping()
    @ApiOperation("删除审批条件")
    public ResponseResult delete(@RequestBody @Validated ApprovalCriteriaForm approvalCriteriaForm) {
        return ResponseUtil.success(approvalCriteriaBiz.delete(approvalCriteriaForm));
    }


}
