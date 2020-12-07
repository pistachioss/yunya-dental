package com.yunya.modules.employeeattend.controller;

import com.yunya.feign.employee_attend.vo.ApprovalInfoVO;
import com.yunya.feign.employee_attend.vo.EmLeaveVO;
import com.yunya.feign.employee_attend.vo.LeaveInfoListVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.employee_attend.ApprovalLevelSet;
import com.yunya.modules.employeeattend.biz.LeaveInfoBiz;
import com.yunya.modules.employeeattend.form.FindApprovalByMeForm;
import com.yunya.modules.employeeattend.form.LeaveInfoByEmForm;
import com.yunya.modules.employeeattend.form.LeaveInfoForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简介: 请假控制层
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Api(tags = "请假接口")
@RestController
@RequestMapping("/leave_info")
@CrossOrigin
public class LeaveInfoController {

    @Autowired
    private LeaveInfoBiz leaveInfoBiz;

    /**
     * 新增按天请假申请
     *
     * @param
     * @return
     */
    @PostMapping("/addDay")
    @ApiOperation("新增按天请假申请")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult create(@RequestBody @Validated LeaveInfoForm leaveInfoForm) {
        return ResponseUtil.success(leaveInfoBiz.createDay(leaveInfoForm));
    }

    /**
     * 查询请假时包含的班次
     *
     * @param
     * @return
     */
    @PostMapping("/selectBaseByDay")
    @ApiOperation("查询请假时包含的班次（参数为开始时间，结束时间，用户ID，请假Id 按需求传）")
    @RepeatSubmit
    public ResponseResult<List<EmLeaveVO>> selectBaseByDay(@RequestBody @Validated LeaveInfoForm leaveInfoForm) {
        return ResponseUtil.success(leaveInfoBiz.selectBaseByDay(leaveInfoForm));
    }

    /**
     * 根据天数获得审批信息
     */
    @PostMapping("/selectApprovalByDay")
    @ApiOperation("根据天数获得审批信息")
    @RepeatSubmit
    public ResponseResult<List<ApprovalLevelSet>> selectApprovalByDay(@RequestBody @Validated LeaveInfoForm leaveInfoForm) {
        return ResponseUtil.success(leaveInfoBiz.selectApprovalByDay(leaveInfoForm));
    }

    /**
     * 新增按班次请假申请
     *
     * @param
     * @return
     */
    @PostMapping("/addEm")
    @ApiOperation("新增按班次请假申请")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult addEm(@RequestBody @Validated LeaveInfoByEmForm leaveInfoForm) {
        return ResponseUtil.success(leaveInfoBiz.addEm(leaveInfoForm));
    }

    /**
     * 审核请假申请
     *
     * @param
     * @return
     */
    @PostMapping("/examine")
    @ApiOperation("审核请假申请")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult examine(@RequestBody @Validated LeaveInfoForm leaveInfoForm) {
        return ResponseUtil.success(leaveInfoBiz.examine(leaveInfoForm));
    }

    /**
     * 撤销请假申请
     *
     * @param
     * @return
     */
    @PostMapping("/revoke")
    @ApiOperation("撤销请假申请")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult revoke(@RequestBody @Validated LeaveInfoForm leaveInfoForm) {
        return ResponseUtil.success(leaveInfoBiz.revoke(leaveInfoForm));
    }

    /**
     * 获取请假申请列表
     *
     * @param
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("获取请假申请列表")
    @RepeatSubmit
    public ResponseResult<List<LeaveInfoListVO>> findList(@RequestBody @Validated LeaveInfoForm leaveInfoForm) {
        return ResponseUtil.success(leaveInfoBiz.findList(leaveInfoForm));
    }

    /**
     * 根据请假ID获取审批信息
     *
     * @param
     * @return
     */
    @PostMapping("/findApproval")
    @ApiOperation("根据请假ID获取审批信息 传参数 id")
    @RepeatSubmit
    public ResponseResult<List<ApprovalInfoVO>> findApproval(@RequestBody @Validated LeaveInfoForm leaveInfoForm) {
        return ResponseUtil.success(leaveInfoBiz.findApproval(leaveInfoForm));
    }


    /**
     * 待我审批
     *
     * @param
     * @return
     */
    @PostMapping("/findApprovalByMe")
    @ApiOperation("待我审批 传用户id")
    @RepeatSubmit
    public ResponseResult findApprovalByMe(@RequestBody @Validated FindApprovalByMeForm findApprovalByMeForm) {
        return ResponseUtil.success(leaveInfoBiz.findApprovalByMe(findApprovalByMeForm));
    }


    /**
     * 我已审批
     *
     * @param
     * @return
     */
    @PostMapping("/findOverApprovalByMe")
    @ApiOperation("我已审批 传用户id")
    @RepeatSubmit
    public ResponseResult findOverApprovalByMe(@RequestBody @Validated FindApprovalByMeForm findApprovalByMeForm) {
        return ResponseUtil.success(leaveInfoBiz.findOverApprovalByMe(findApprovalByMeForm));
    }
}
