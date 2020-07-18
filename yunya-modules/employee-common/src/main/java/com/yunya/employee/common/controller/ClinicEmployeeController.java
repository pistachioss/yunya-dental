package com.yunya.employee.common.controller;


import com.github.pagehelper.PageInfo;
import com.yunya.employee.common.model.request.ClinicEmployeeConfigReq;
import com.yunya.employee.common.model.request.ClinicEmployeePageReq;
import com.yunya.employee.common.model.response.ClinicEmployeePageRes;
import com.yunya.employee.common.service.ClinicEmployeeBiz;
import com.yunya.framework.common.model.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author bruce
 * @date 2020/7/10
 */
@Api(tags = {"门诊端员工管理"})
@RestController
public class ClinicEmployeeController {
//
//    @Resource
//    private ClinicEmployeeBiz clinicEmployeeBiz;
//
//    /**
//     * 门诊员工配置
//     *
//     * @return
//     */
//    @ApiOperation("门诊端-诊所设置-员工设置-员工配置")
//    @PutMapping("/config/{employeeId}")
//    public ResponseResult modifyEmployeeConfig(@PathVariable("employeeId") Integer employeeId,
//                                               @RequestBody ClinicEmployeeConfigReq configRequest) {
//        clinicEmployeeBiz.modifyClinicEmployeeConfig(employeeId, configRequest);
//        return ResponseResult.success();
//    }
//
//    /**
//     * 门诊员工列表查询
//     * @return
//     */
//    @ApiOperation("门诊端-员工管理-列表")
//    @PostMapping("/list")
//    public ResponseResult getEmployeePage(@RequestBody ClinicEmployeePageReq pageReq) {
//        PageInfo<ClinicEmployeePageRes> pageInfo = clinicEmployeeBiz.getEmployeePageList(pageReq);
//        return ResponseResult.success(pageInfo);
//    }

    /**
     * todo
     * 门诊员工列表查询
     * @return
     */
    @ApiOperation("门诊端-员工管理-列表")
    @PostMapping("clinic/employee/list")
    public ResponseResult getEmployeePage(@RequestBody ClinicEmployeePageReq pageReq) {
        return null;
    }

    /**
     * todo
     * 门诊员工配置
     *
     * @return
     */
    @ApiOperation("门诊端-诊所设置-员工设置-员工配置")
    @PutMapping("clinic/employee/config/{employeeId}")
    public ResponseResult modifyEmployeeConfig(@PathVariable("employeeId") Integer employeeId,
                                               @RequestBody ClinicEmployeeConfigReq configRequest) {
       return null;
    }


}
