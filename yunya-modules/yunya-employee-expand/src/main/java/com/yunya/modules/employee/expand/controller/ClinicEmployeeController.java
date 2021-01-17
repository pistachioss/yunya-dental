package com.yunya.modules.employee.expand.controller;


import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employee.expand.model.request.ClinicEmployeeConfigQueryReq;
import com.yunya.modules.employee.expand.model.request.ClinicEmployeeConfigReq;
import com.yunya.modules.employee.expand.model.response.ClinicEmployeeConfigRes;
import com.yunya.modules.employee.expand.model.response.EnableEmployeeRes;
import com.yunya.modules.employee.expand.service.ClinicEmployeeConfigBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author bruce
 * @date 2020/7/10
 */
@Api(tags = {"门诊端员工管理"})
@RestController
public class ClinicEmployeeController {

    @Resource
    private ClinicEmployeeConfigBiz clinicEmployeeConfigBiz;

    /**
     *
     * 门诊员工配置
     *
     * @return
     */
    @ApiOperation("门诊端-诊所设置-员工设置-员工配置")
    @PutMapping("clinic/employee/config/{clinicId}/{employeeId}")
    public ResponseResult modifyEmployeeConfig(@PathVariable("clinicId") Integer clinicId,
                                               @PathVariable("employeeId") Integer employeeId,
                                               @Valid @RequestBody ClinicEmployeeConfigReq configRequest) {
        clinicEmployeeConfigBiz.modifyClinicEmployeeConfig(employeeId, clinicId, configRequest);
        return ResponseUtil.success();
    }

    @ApiOperation("门诊员工配置详情")
    @PostMapping("clinic/employee/config/detail")
    public ResponseResult<ClinicEmployeeConfigRes> getEmployeeConfigRecord(@Valid @RequestBody ClinicEmployeeConfigQueryReq req) {
        ClinicEmployeeConfigRes employeeConfig = clinicEmployeeConfigBiz.getEmployeeConfig(req);
        return ResponseUtil.success(employeeConfig);
    }

    @ApiOperation("查询可预约，可挂号医生")
    @GetMapping("{clinicId}/config/list")
    @CurrentUser
    public ResponseResult<EnableEmployeeRes> getEnableEmployeeList(@PathVariable(value = "clinicId") Integer clinicId) {
        EnableEmployeeRes result = clinicEmployeeConfigBiz.getAllEnableEmployee(clinicId);
        return ResponseUtil.success(result);
    }

}
