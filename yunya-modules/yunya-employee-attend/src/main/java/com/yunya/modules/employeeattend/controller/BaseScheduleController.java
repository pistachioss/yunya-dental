package com.yunya.modules.employeeattend.controller;


import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.employee_attend.BaseSchedule;
import com.yunya.modules.employeeattend.form.ClinicCommonForm;
import com.yunya.modules.employeeattend.form.ScheduleForm;
import com.yunya.modules.employeeattend.biz.BaseScheduleBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-05-15 13:23
 */
@Api(tags = "公司班次设置接口")
@RestController
@RequestMapping("/base_schedule")
@CrossOrigin
public class BaseScheduleController {
    @Autowired
    private BaseScheduleBiz baseScheduleBiz;

    /**
     * 添加班次
     *
     * @param baseSchedule
     * @return
     */
    @PostMapping
    @ApiOperation("添加班次")
    public ResponseResult save(@RequestBody @Valid BaseSchedule baseSchedule) {
        baseScheduleBiz.saveBaseSchedule(baseSchedule);
        return ResponseUtil.success();
    }

    /**
     * 修改班次
     *
     * @param baseSchedule
     * @return
     */
    @PutMapping
    @ApiOperation("修改班次")
    public ResponseResult update(@RequestBody BaseSchedule baseSchedule) {
        baseScheduleBiz.updateBaseSchedule(baseSchedule);
        return ResponseUtil.success();
    }

    /**
     * 设置门诊是否可用当前班次
     *
     * @param clinicCommonForm
     * @return
     */
    @PostMapping("/setting")
    @ApiOperation("设置门诊是否可用当前班次")
    @CurrentUser
    public ResponseResult setting(@RequestBody @Valid ClinicCommonForm clinicCommonForm) {
        baseScheduleBiz.setting(clinicCommonForm);
        return ResponseUtil.success();
    }
    /**
     * 统一开启门诊班次
     *
     * @param
     * @return
     */
    @GetMapping("/settingAll/{id}")
    @ApiOperation("统一开启门诊班次")
    @CurrentUser
    public ResponseResult settingAll(@PathVariable(name = "id") Integer id){
        return ResponseUtil.success(baseScheduleBiz.settingAll(id));
    }

    /**
     * 获取班次
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("获取班次")
    public ResponseResult find(@PathVariable(name = "id") Integer id) {
        return ResponseUtil.success(baseScheduleBiz.selectById(id));
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除")
    public ResponseResult delete(@PathVariable(name = "id") Integer id) {
        baseScheduleBiz.delete(id);
        return ResponseUtil.success();
    }

    /**
     * 班次门诊列表
     *
     * @return
     */
    @GetMapping("/clinic/{id}")
    @ApiOperation("班次门诊列表")
    public ResponseResult clinicList(@PathVariable("id") Integer id) {
        return ResponseUtil.success(baseScheduleBiz.clinicList(id));
    }

    /**
     * 列表
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("列表")
    public ResponseResult list() {
        return ResponseUtil.success(baseScheduleBiz.selectListAll());
    }


    /**
     * 查询
     *
     * @param scheduleForm
     * @return
     */
    @PostMapping("/search")
    @ApiOperation("查询")
    public ResponseResult search(@RequestBody ScheduleForm scheduleForm) {
        PageInfo<BaseSchedule>pa =  baseScheduleBiz.search(scheduleForm);
        return ResponseUtil.success(pa);
    }
}
