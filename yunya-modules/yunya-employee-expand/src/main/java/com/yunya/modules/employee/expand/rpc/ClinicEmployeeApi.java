package com.yunya.modules.employee.expand.rpc;

import com.yunya.models.expand.ClinicEmployeeConfig;
import com.yunya.modules.employee.expand.model.response.EnableEmployeeRes;
import com.yunya.modules.employee.expand.service.ClinicEmployeeConfigBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
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

    /**
     * 编辑员工可预约可挂号配置
     * @param clinicEmployeeConfigs
     * @return
     */
    @ApiOperation(value = "编辑员工可预约可挂号配置--内部服务使用",hidden = true)
    @PostMapping("/api/edit/employee/config")
    public Integer editEmployeeConfig(@RequestBody List<ClinicEmployeeConfig> clinicEmployeeConfigs) {
        return clinicEmployeeConfigBiz.editEmployeeConfig(clinicEmployeeConfigs);
    }

    /**
     * 根据员工ID查询该员工在所有门诊的可预约可挂号信息
     * @param employeeId
     * @return
     */
    @ApiOperation(value = "根据员工ID查询该员工在所有门诊的可预约可挂号信息--内部服务使用",hidden = true)
    @GetMapping("/api/employee/config/{employeeId}")
    public List<ClinicEmployeeConfig> findClinicEmployeeConfigs(@PathVariable(value = "employeeId") Integer employeeId) {
        return clinicEmployeeConfigBiz.findClinicEmployeeConfigs(employeeId);
    }

    /**
     * 根据员工ID和组织ID删除员工配置信息
     * @param employeeId 员工ID
     * @param clinicId  门诊ID
     * @return 成功返回 删除行数，否则返回0
     */
    @ApiOperation(value = "根据员工ID和组织ID删除员工配置信息--内部服务使用",hidden = true)
    @GetMapping("/api/delete/employee/config/{employeeId}/{clinicId}")
    public Integer deleteClinicEmployeeConfig(@PathVariable("employeeId") @Validated @NotNull(message = "员工ID不能为空") Integer employeeId,
                                              @PathVariable("clinicId") @Validated @NotNull(message = "门诊ID不能为空") Integer clinicId) {
        return this.clinicEmployeeConfigBiz.deleteClinicEmployeeConfig(employeeId,clinicId);
    }
}
