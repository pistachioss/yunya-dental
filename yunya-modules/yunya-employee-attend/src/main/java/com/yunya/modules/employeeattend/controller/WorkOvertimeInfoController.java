package com.yunya.modules.employeeattend.controller;

import com.yunya.feign.employee_attend.vo.WorkOvertimeInfoListVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.WorkOvertimeInfoBiz;
import com.yunya.modules.employeeattend.form.NoWorkByDateForm;
import com.yunya.modules.employeeattend.form.NoWorkForm;
import com.yunya.modules.employeeattend.form.WorkForm;
import com.yunya.modules.employeeattend.form.WorkOvertimeInfoForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Api(tags = "加班接口")
@RestController
@RequestMapping("/work_overtime_info")
@CrossOrigin
public class WorkOvertimeInfoController {
    @Autowired
    private WorkOvertimeInfoBiz workOvertimeInfoBiz;

    /**
     * 新增加班申请
     *
     * @param
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增加班申请")
    @RepeatSubmit
    public ResponseResult create(@RequestBody @Validated WorkOvertimeInfoForm workOvertimeInfoForm) {
        return ResponseUtil.success(workOvertimeInfoBiz.create(workOvertimeInfoForm));
    }

    /**
     * 获取加班申请列表
     *
     * @param
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("获取加班申请列表")
    @CurrentUser
    public ResponseResult<List<WorkOvertimeInfoListVO>> findList(@RequestBody @Validated WorkOvertimeInfoForm workOvertimeInfoForm) {
        return ResponseUtil.success(workOvertimeInfoBiz.findList(workOvertimeInfoForm));
    }

    /**
     * 审核加班申请
     *
     * @param
     * @return
     */
    @PostMapping("/examine")
    @ApiOperation("审核加班申请")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult examine(@RequestBody @Validated WorkOvertimeInfoForm workOvertimeInfoForm) {
        return ResponseUtil.success(workOvertimeInfoBiz.examine(workOvertimeInfoForm));
    }

    /**
     * 撤销加班申请
     *
     * @param
     * @return
     */
    @PostMapping("/revoke")
    @ApiOperation("撤销加班申请")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult revoke(@RequestBody @Validated WorkOvertimeInfoForm workOvertimeInfoForm) {
        return ResponseUtil.success(workOvertimeInfoBiz.revoke(workOvertimeInfoForm));
    }

    /**
     * 根据时间段和门诊iD获取加班班次列表
     *
     * @param
     * @return
     */
    @PostMapping("/findWorkEm")
    @ApiOperation("根据时间段和门诊iD获取加班班次列表")
    @RepeatSubmit
    public ResponseResult findWorkEm(@RequestBody @Validated WorkForm workForm) {
        return ResponseUtil.success(workOvertimeInfoBiz.findWorkEm(workForm));
    }

    /**
     * 根据日期时间段和用户id获取休息班班次列表
     *
     * @param
     * @return
     */
    @PostMapping("/findNoWorkEm")
    @ApiOperation("根据日期时间段和用户id获取休息班班次列表")
    @RepeatSubmit
    public ResponseResult findNoWorkEm(@RequestBody @Validated NoWorkForm noWorkForm) {
        return ResponseUtil.success(workOvertimeInfoBiz.findNoWorkEm(noWorkForm));
    }

    /**
     * 根据日期和用户id获取当天的休息班次以及门诊信息
     *
     * @param
     * @return
     */
    @PostMapping("/findNoWorkEmByDate")
    @ApiOperation("根据日期和用户id获取当天的休息班次以及对应的门诊信息")
    @RepeatSubmit
    public ResponseResult findNoWorkEmByDate(@RequestBody @Validated NoWorkByDateForm noWorkByDateForm) {
        return ResponseUtil.success(workOvertimeInfoBiz.findNoWorkEmByDate(noWorkByDateForm));
    }

}
