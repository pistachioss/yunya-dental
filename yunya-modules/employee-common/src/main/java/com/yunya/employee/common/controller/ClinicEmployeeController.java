package com.yunya.employee.common.controller;

import com.github.pagehelper.PageInfo;
import com.github.wxiaoqi.security.common.util.ResponseUtil;
import com.github.wxiaoqi.security.common.util.RestDataResult;
import com.yunya.clinic.employee.common.model.request.ClinicEmployeeConfigReq;
import com.yunya.clinic.employee.common.model.request.ClinicEmployeePageReq;
import com.yunya.clinic.employee.common.model.response.ClinicEmployeePageRes;
import com.yunya.clinic.employee.common.service.ClinicEmployeeBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

/**
 * @author bruce
 * @date 2020/7/10
 */
@Api(tags = {"门诊端员工管理"})
@RestController
@RequestMapping("clinic/employee")
public class ClinicEmployeeController {

    @Resource
    private ClinicEmployeeBiz clinicEmployeeBiz;

    /**
     * 门诊员工配置
     *
     * @return
     */
    @ApiOperation("门诊端-诊所设置-员工设置-员工配置")
    @PutMapping("/config/{employeeId}")
    public RestDataResult modifyEmployeeConfig(@PathVariable("employeeId") Integer employeeId,
                                               @RequestBody ClinicEmployeeConfigReq configRequest) {
        clinicEmployeeBiz.modifyClinicEmployeeConfig(employeeId, configRequest);
        return ResponseUtil.successRes();
    }

    /**
     * 门诊员工列表查询
     * @return
     */
    @ApiOperation("门诊端-员工管理-列表")
    @PostMapping("/list")
    public RestDataResult<PageInfo<ClinicEmployeePageRes>> getEmployeePage(@RequestBody ClinicEmployeePageReq pageReq) {
        PageInfo<ClinicEmployeePageRes> pageInfo = clinicEmployeeBiz.getEmployeePageList(pageReq);
        return ResponseUtil.successRes(pageInfo);
    }

}
