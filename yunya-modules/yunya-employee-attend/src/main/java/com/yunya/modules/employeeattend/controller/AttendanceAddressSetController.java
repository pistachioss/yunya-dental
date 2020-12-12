package com.yunya.modules.employeeattend.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.form.AttendanceAddressSetForm;
import com.yunya.feign.employee_attend.form.AttendanceAddressSetQueryForm;
import com.yunya.feign.employee_attend.model.AttendanceAddressSetModel;
import com.yunya.feign.employee_attend.vo.AttendanceAddressSetVO;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.AttendanceAddressSetBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介：考勤地址设置管理
 *
 * @author: chenlin
 * @Description: 考勤地址设置管理
 * @Date: 2020/11/5 9:13
 * @since: 1.0.0
 */
@Api(tags = "考勤地址设置管理")
@RestController
@RequestMapping("attendanceAddressSet")
public class AttendanceAddressSetController {
    /** 注入对象 */
    @Autowired
    private AttendanceAddressSetBiz attendanceAddressSetBiz;

    /**
     * 分页查询所有机构以及关联的考勤地址列表
     *
     * @param model 查询参数
     * @return ResponseResult<PageInfo<AttendanceAddressSetVO>>
     */
    @ApiOperation(value = "分页查询所有机构以及关联的考勤地址列表")
    @PostMapping("/orgList")
    public ResponseResult<PageInfo<AttendanceAddressSetVO>> findOrganizationAttendanceAddressSetList(@RequestBody OrganizationModel model) {
        PageInfo<AttendanceAddressSetVO> result = attendanceAddressSetBiz.findOrganizationAttendanceAddressSetList(model);
        return ResponseUtil.success(result);
    }

    /**
     * 分页查询考勤地址设置列表
     *
     * @param attendanceAddressSetQueryForm 查询参数
     * @return ResponseResult<PageInfo<AttendanceAddressSetVO>>
     */
    @ApiOperation(value = "分页查询考勤地址设置列表")
    @PostMapping("/list")
    public ResponseResult<PageInfo<AttendanceAddressSetVO>> findAttendanceAddressSetList(@RequestBody AttendanceAddressSetQueryForm attendanceAddressSetQueryForm) {
        PageInfo<AttendanceAddressSetVO> result = attendanceAddressSetBiz.findAttendanceAddressSetList(attendanceAddressSetQueryForm);
        return ResponseUtil.success(result);
    }

    /**
     * 根据id查询考勤地址设置信息
     *
     * @param id 主键id
     * @return ResponseResult<AttendanceAddressSetVO>
     */
    @ApiOperation("根据id查询考勤地址设置信息")
    @GetMapping("/one/{id}")
    public ResponseResult<AttendanceAddressSetVO> findAttendanceAddressSetById(@PathVariable(value = "id") Integer id) {
        AttendanceAddressSetVO attendanceAddressSetVO = attendanceAddressSetBiz.findAttendanceAddressSetById(id);
        return ResponseUtil.success(attendanceAddressSetVO);
    }

    /**
     * 添加考勤地址设置信息
     *
     * @param attendanceAddressSetModel 考勤地址设置模型
     * @return
     */
    @CurrentUser
    @ApiOperation("添加考勤地址设置信息")
    @PostMapping("/add")
    @RepeatSubmit
    public ResponseResult add(@RequestBody @Validated AttendanceAddressSetModel attendanceAddressSetModel) {
        attendanceAddressSetBiz.add(attendanceAddressSetModel);
        return ResponseUtil.success(null);
    }

    /**
     * 修改考勤地址设置信息
     *
     * @param id 主键id
     * @param attendanceAddressSetForm 考勤地址设置模型
     * @return
     */
    @CurrentUser
    @ApiOperation("修改考勤地址设置信息")
    @PutMapping("/update/{id}")
    @RepeatSubmit
    public ResponseResult update(@PathVariable(value = "id") Integer id, @RequestBody @Validated AttendanceAddressSetForm attendanceAddressSetForm) {
        attendanceAddressSetBiz.update(id, attendanceAddressSetForm);
        return ResponseUtil.success(null);
    }

    /**
     * 删除考勤地址设置信息
     *
     * @param id 主键id
     * @return
     */
    @ApiOperation("删除考勤地址设置信息")
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        attendanceAddressSetBiz.delete(id);
        return ResponseUtil.success(null);
    }
}
