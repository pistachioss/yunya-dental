package com.yunya.modules.employee.expand.rpc;

import com.yunya.models.expand.ClinicEmployeeConfig;
import com.yunya.modules.employee.expand.model.response.EnableEmployeeRes;
import com.yunya.modules.employee.expand.service.ClinicEmployeeConfigBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 新增员工可预约可挂号配置
     * @param clinicEmployeeConfig
     * @return
     */
    @ApiOperation(value = "新增员工默认可预约可挂号--内部服务使用",hidden = true)
    @PostMapping("/api/default/employee/config")
    public Integer addEmployeeConfig(@RequestBody ClinicEmployeeConfig clinicEmployeeConfig) {
        return clinicEmployeeConfigBiz.addEmployeeConfig(clinicEmployeeConfig);
    }
}
