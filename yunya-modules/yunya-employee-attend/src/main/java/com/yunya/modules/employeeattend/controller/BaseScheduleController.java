package com.yunya.modules.employeeattend.controller;


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
@Api(tags = "公司排班设置接口")
@RestController
@RequestMapping("/base_schedule")
@CrossOrigin
public class BaseScheduleController {
    @Autowired
    private BaseScheduleBiz baseScheduleBiz;

    /**
     * 添加排班
     *
     * @param baseSchedule
     * @return
     */
    @PostMapping
    @ApiOperation("添加排班")
    public ResponseResult save(@RequestBody @Valid BaseSchedule baseSchedule) {
        baseScheduleBiz.saveBaseSchedule(baseSchedule);
        return ResponseUtil.success();
    }

    /**
     * 修改排班
     *
     * @param baseSchedule
     * @return
     */
    @PutMapping
    @ApiOperation("修改排班")
    public ResponseResult update(@RequestBody BaseSchedule baseSchedule) {
        baseScheduleBiz.updateBaseSchedule(baseSchedule);
        return ResponseUtil.success();
    }

    /**
     * 设置门诊是否可用
     *
     * @param clinicCommonForm
     * @return
     */
    @PostMapping("/setting")
    @ApiOperation("配置")
    public ResponseResult setting(@RequestBody @Valid ClinicCommonForm clinicCommonForm) {
        baseScheduleBiz.setting(clinicCommonForm);
        return ResponseUtil.success();
    }

    /**
     * 获取排班
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("获取排班")
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
     * 排班门诊列表
     *
     * @return
     */
    @GetMapping("/clinic/{id}")
    @ApiOperation("排班门诊列表")
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
        return ResponseUtil.success(baseScheduleBiz.search(scheduleForm.getTypeName(), scheduleForm.getName()));
    }
}
