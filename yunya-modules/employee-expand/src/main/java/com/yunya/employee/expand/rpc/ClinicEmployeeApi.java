package com.yunya.employee.expand.rpc;

import com.yunya.employee.expand.model.request.ClinicEmployeeConfigQueryReq;
import com.yunya.employee.expand.model.response.ClinicEmployeeConfigRes;
import com.yunya.employee.expand.model.response.EnableEmployeeRes;
import com.yunya.employee.expand.service.ClinicEmployeeConfigBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author bruce
 * @date 2020/7/24
 */
@Api(tags = {"门诊端员工管理"})
@RestController
public class ClinicEmployeeApi {

    @Resource
    private ClinicEmployeeConfigBiz clinicEmployeeConfigBiz;

    @ApiOperation("门诊员工配置详情")
    @PostMapping("clinic/employee/config/detail")
    public ClinicEmployeeConfigRes getEmployeeConfigRecord(@Valid @RequestBody ClinicEmployeeConfigQueryReq req) {
        return clinicEmployeeConfigBiz.getEmployeeConfig(req);

    }

    @ApiOperation("查询可预约，可挂号医生")
    @GetMapping("clinic/employee/config/{clinicId}/list")
    public EnableEmployeeRes getEnableEmployeeList(@PathVariable("clinicId") Integer clinicId) {
        return clinicEmployeeConfigBiz.getAllEnableEmployee(clinicId);
    }
}
