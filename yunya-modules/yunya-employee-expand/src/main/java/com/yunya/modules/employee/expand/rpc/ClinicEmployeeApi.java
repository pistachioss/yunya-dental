package com.yunya.modules.employee.expand.rpc;

import com.yunya.modules.employee.expand.model.response.EnableEmployeeRes;
import com.yunya.modules.employee.expand.service.ClinicEmployeeConfigBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author bruce
 * @date 2020/7/24
 */
@Api(tags = {"门诊端员工管理"})
@RestController
public class ClinicEmployeeApi {
    @Resource
    private ClinicEmployeeConfigBiz clinicEmployeeConfigBiz;

    @ApiOperation("查询可预约，可挂号医生")
    @GetMapping("/api/{clinicId}/config/list")
    public EnableEmployeeRes getEnableEmployeeList(@PathVariable(value = "clinicId") Integer clinicId) {
        EnableEmployeeRes result = clinicEmployeeConfigBiz.getAllEnableEmployee(clinicId);
        return result;
    }
}
