package com.yunya.modules.employeeattend.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.employee_attend.ApprovalPeople;
import com.yunya.modules.employeeattend.biz.ApprovalPeopleBiz;
import com.yunya.modules.employeeattend.form.ApprovalPeopleDeleteForm;
import com.yunya.modules.employeeattend.form.ApprovalPeopleForm;
import com.yunya.modules.employeeattend.form.ApprovalPeopleQuery;
import com.yunya.modules.employeeattend.vo.ApprovalPeopleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介: 审批人员设置接口
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Api(tags = "审批人员设置接口")
@RestController
@RequestMapping("/approval_people")
@CrossOrigin
public class ApprovalPeopleController {

    @Autowired private ApprovalPeopleBiz approvalPeopleBiz;

    /**
     * 查看审批人员设置列表
     * @param
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("查看审批人员设置列表")
    public ResponseResult<PageInfo<ApprovalPeopleVO>> findList(@RequestBody @Validated ApprovalPeopleQuery approvalPeopleQuery) {
        return ResponseUtil.success(approvalPeopleBiz.findlist(approvalPeopleQuery));
    }

    /**
     * 新增审批人员设置
     * @param
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增审批人员设置")
    @CurrentUser
    public ResponseResult create(@RequestBody @Validated ApprovalPeopleForm approvalPeopleForm) {
        return ResponseUtil.success(approvalPeopleBiz.create(approvalPeopleForm));
    }

    /**
     * 删除审批人员设置
     * @param
     * @return
     */
    @DeleteMapping("")
    @ApiOperation("删除审批人员设置")
    public ResponseResult delete(@RequestBody @Validated ApprovalPeopleDeleteForm approvalPeopleDeleteForm) {
        return ResponseUtil.success(approvalPeopleBiz.delete(approvalPeopleDeleteForm));
    }
}
