package com.yunya.modules.employeeattend.controller;


import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.ClinicScheduleBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-06-04 14:15
 */
@Api(tags = "门诊排班表接口")
@RestController
@RequestMapping("/clinic_schedule")
@CrossOrigin
public class ClinicScheduleController {
    @Autowired
    private ClinicScheduleBiz clinicScheduleBiz;

//    /**
//     * 新增或修改
//     *
//     * @param clinicSchedule
//     * @return
//     */
//    @PostMapping
//    @ApiOperation("新增或修改")
//    public Map<String, Object> saveOrUpdate(@RequestBody ClinicSchedule clinicSchedule) {
//        clinicScheduleBiz.saveOrUpdate(clinicSchedule);
//        return ResponseUtil.success();
//    }

    @GetMapping("/{clinicId}")
    @ApiOperation("获取列表")
    public ResponseResult findVOsByClinicId(@PathVariable("clinicId") Integer clinicId) {
        return ResponseUtil.success(clinicScheduleBiz.findVOsByClinicId(clinicId));
    }

    @GetMapping("/inservice/{clinicId}")
    @ApiOperation("获取列表")
    public ResponseResult findVOsByClinicIdAndInservice(@PathVariable("clinicId") Integer clinicId) {
        return ResponseUtil.success(clinicScheduleBiz.findVOsByClinicIdAndInservice(clinicId));
    }
}
