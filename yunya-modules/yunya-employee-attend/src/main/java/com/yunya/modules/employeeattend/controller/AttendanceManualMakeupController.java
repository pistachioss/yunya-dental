package com.yunya.modules.employeeattend.controller;

import com.yunya.feign.employee_attend.model.AttendanceManualMakeupModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.AttendanceManualMakeupBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介：考勤手动补入时长管理
 *
 * @author: chenlin
 * @Description: 考勤手动补入时长管理
 * @Date: 2020/11/28 12:53
 * @since: 1.0.0
 */
@Api(tags = "考勤手动补入时长管理")
@RestController
@RequestMapping("attendanceManualMakeup")
public class AttendanceManualMakeupController {
    /** 注入对象 */
    @Autowired
    private AttendanceManualMakeupBiz attendanceManualMakeupBiz;

    /**
     * 修改手动补入时长信息
     *
     * @param attendanceManualMakeupModel 手动补入时长添加模型
     * @return
     */
    @CurrentUser
    @ApiOperation("修改手动补入时长信息")
    @PostMapping("/update")
    @RepeatSubmit
    public ResponseResult update(@RequestBody @Validated AttendanceManualMakeupModel attendanceManualMakeupModel) {
        Integer id = attendanceManualMakeupBiz.update(attendanceManualMakeupModel);
        return ResponseUtil.success(id);
    }
}
