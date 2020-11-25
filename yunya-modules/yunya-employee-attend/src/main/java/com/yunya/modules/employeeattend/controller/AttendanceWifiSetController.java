package com.yunya.modules.employeeattend.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.form.AttendanceWifiSetForm;
import com.yunya.feign.employee_attend.form.AttendanceWifiSetQueryForm;
import com.yunya.feign.employee_attend.model.AttendanceSetModel;
import com.yunya.feign.employee_attend.model.AttendanceWifiSetModel;
import com.yunya.feign.employee_attend.vo.AttendanceWifiSetVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.AttendanceWifiSetBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介：考勤Wifi设置管理
 *
 * @author: chenlin
 * @Description: 考勤Wifi设置管理
 * @Date: 2020/11/5 12:53
 * @since: 1.0.0
 */
@Api(tags = "考勤Wifi设置管理")
@RestController
@RequestMapping("attendanceWifiSet")
public class AttendanceWifiSetController {

    @Autowired
    private AttendanceWifiSetBiz attendanceWifiSetBiz;

    /**
     * 分页查询考勤Wifi设置列表
     *
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendanceWifiSetVO>>
     */
    @ApiOperation(value = "分页查询考勤Wifi设置列表")
    @PostMapping("/list")
    public ResponseResult<PageInfo<AttendanceWifiSetVO>> findAttendanceWifiSetList(@RequestBody AttendanceWifiSetQueryForm queryForm) {
        PageInfo<AttendanceWifiSetVO> result = attendanceWifiSetBiz.findAttendanceWifiSetList(queryForm);
        return ResponseUtil.success(result);
    }

    /**
     * 根据id查询考勤Wifi设置信息
     *
     * @param id 主键id
     * @return ResponseResult<AttendanceWifiSetVO>
     */
    @ApiOperation("根据id查询考勤Wifi设置信息")
    @GetMapping("/one/{id}")
    public ResponseResult<AttendanceWifiSetVO> findAttendanceWifiSetById(@PathVariable(value = "id") Integer id) {
        AttendanceWifiSetVO attendanceAddressSetVO = attendanceWifiSetBiz.findAttendanceWifiSetById(id);
        return ResponseUtil.success(attendanceAddressSetVO);
    }

    /**
     * 添加考勤Wifi设置信息
     *
     * @param attendanceWifiSetModel 考勤地址设置模型
     * @return
     */
    @CurrentUser
    @ApiOperation("添加考勤Wifi设置信息")
    @PostMapping("/add")
    @RepeatSubmit
    public ResponseResult add(@RequestBody @Validated AttendanceWifiSetModel attendanceWifiSetModel) {
        attendanceWifiSetBiz.add(attendanceWifiSetModel);
        return ResponseUtil.success(null);
    }

    /**
     * 批量添加考勤Wifi设置信息
     *
     * @param attendanceSetModel 考勤设置模型列表
     * @return
     */
    @CurrentUser
    @ApiOperation("批量添加考勤Wifi设置信息")
    @PostMapping("/batchAdd")
    @RepeatSubmit
    public ResponseResult batchAdd(@RequestBody @Validated AttendanceSetModel attendanceSetModel) {
        attendanceWifiSetBiz.batchAdd(attendanceSetModel);
        return ResponseUtil.success(null);
    }

    /**
     * 修改考勤Wifi设置信息
     *
     * @param id 主键id
     * @param attendanceWifiSetForm 考勤Wifi设置模型
     * @return
     */
    @CurrentUser
    @ApiOperation("修改考勤Wifi设置信息")
    @PutMapping("/update/{id}")
    @RepeatSubmit
    public ResponseResult update(@PathVariable(value = "id") Integer id, @RequestBody @Validated AttendanceWifiSetForm attendanceWifiSetForm) {
        attendanceWifiSetBiz.update(id, attendanceWifiSetForm);
        return ResponseUtil.success(null);
    }

    /**
     * 删除考勤Wifi设置信息
     *
     * @param id 主键id
     * @return
     */
    @ApiOperation("删除考勤Wifi设置信息")
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        attendanceWifiSetBiz.delete(id);
        return ResponseUtil.success(null);
    }
}
