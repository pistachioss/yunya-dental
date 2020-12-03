package com.yunya.modules.employeeattend.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.form.AttendanceDeviceBindingQueryForm;
import com.yunya.feign.employee_attend.model.AttendanceDeviceBindingModel;
import com.yunya.feign.employee_attend.vo.AttendanceDeviceBindingVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.AttendanceDeviceBindingBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 简介：考勤设备绑定管理
 *
 * @author: chenlin
 * @Description: 考勤设备绑定管理
 * @Date: 2020/11/5 18:13
 * @since: 1.0.0
 */
@Api(tags = "考勤设备绑定管理")
@RestController
@RequestMapping("attendanceDeviceBinding")
public class AttendanceDeviceBindingController {
    /** 注入对象 */
    @Autowired
    private AttendanceDeviceBindingBiz attendanceDeviceBindingBiz;

    /**
     * 分页查询员工列表，以及考勤设备绑定信息
     *
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendanceDeviceBindingVO>>
     */
    @ApiOperation(value = "分页查询员工考勤设备绑定列表")
    @PostMapping("/bindingDeviceEmployeeList")
    public ResponseResult<PageInfo<AttendanceDeviceBindingVO>> findBindingDeviceEmployeeList(@RequestBody AttendanceDeviceBindingQueryForm queryForm) {
        PageInfo<AttendanceDeviceBindingVO> result = attendanceDeviceBindingBiz.findBindingDeviceEmployeeList(queryForm);
        return ResponseUtil.success(result);
    }


    /**
     * 根据userid查询员工当前绑定的考勤设备信息
     *
     * @param userId 用户id
     * @return ResponseResult<AttendanceDeviceBindingVO>
     */
    @ApiOperation("根据userid查询员工当前绑定的考勤设备信息")
    @ApiImplicitParam(value = "用户Id", name = "userId", required = true)
    @GetMapping("/employeeBindingDevice/{userId}")
    public ResponseResult<AttendanceDeviceBindingVO> findEmployeeBindingDevice(@PathVariable(value = "userId") @Valid Integer userId) {
        AttendanceDeviceBindingVO result = attendanceDeviceBindingBiz.findEmployeeBindingDevice(userId);
        return ResponseUtil.success(result);
    }

    /**
     * 根据userid分页查询员工的考勤设备的绑定记录列表
     *
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendanceDeviceBindingVO>>
     */
    @ApiOperation("根据userId查询考勤设备绑定记录列表")
    @PostMapping("/employeeBindingDeviceList")
    public ResponseResult<PageInfo<AttendanceDeviceBindingVO>> findEmployeeBindingDeviceList(@RequestBody AttendanceDeviceBindingQueryForm queryForm) {
        PageInfo<AttendanceDeviceBindingVO> result = attendanceDeviceBindingBiz.findEmployeeBindingDeviceList(queryForm);
        return ResponseUtil.success(result);
    }

    /**
     * 发送设备绑定短信验证码
     *
     * @return 返回短信验证码
     */
    @ApiOperation("发送设备绑定短信验证码")
    @ApiImplicitParam(value = "手机号码", name = "mobile", required = true)
    @GetMapping("/verifyCode")
    public ResponseResult authorizationCode(
            @Pattern(regexp = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$")
            @NotBlank(message = "绑定打卡账号不能为空")
            @ApiParam(name = "mobile", value = "手机号", required = true)
                    String mobile) {
        return attendanceDeviceBindingBiz.authorizationCode(mobile);
    }

    /**
     * 考勤设备绑定
     *
     * @param attendanceDeviceBindingModel 考勤地址设置模型
     * @return
     */
    @CurrentUser
    @ApiOperation("考勤设备绑定")
    @PostMapping("/deviceBinding")
    @RepeatSubmit
    public ResponseResult deviceBinding(@RequestBody @Validated AttendanceDeviceBindingModel attendanceDeviceBindingModel) {
        return attendanceDeviceBindingBiz.deviceBinding(attendanceDeviceBindingModel);
    }
}
