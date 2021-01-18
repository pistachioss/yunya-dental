package com.yunya.feign.expand;

import com.yunya.feign.expand.factory.RemoteClinicEmployeeConfigFactory;
import com.yunya.feign.expand.model.response.EnableEmployeeRes;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.expand.ClinicEmployeeConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author bruce
 * @date 2020/7/24
 */
@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_EMPLOYEE_EXPAND, fallbackFactory = RemoteClinicEmployeeConfigFactory.class)
public interface RemoteClinicEmployeeConfigFeign {

    /**
     * 查询可预约，可挂号医生
     * @param clinicId 组织id
     * @return EnableEmployeeRes
     */
    @GetMapping("/api/{clinicId}/config/list")
    public EnableEmployeeRes getEnableEmployeeList(@PathVariable(value = "clinicId") Integer clinicId);

    /**
     * 新增员工可预约可挂号配置
     * @param clinicEmployeeConfig
     * @return
     */
    @ApiOperation(value = "新增员工默认可预约可挂号--内部服务使用",hidden = true)
    @PostMapping("/api/default/employee/config")
    public Integer addEmployeeConfig(@RequestBody ClinicEmployeeConfig clinicEmployeeConfig);

    /**
     * 编辑员工可预约可挂号配置
     * @param clinicEmployeeConfigs
     * @return
     */
    @ApiOperation(value = "编辑员工可预约可挂号配置--内部服务使用",hidden = true)
    @PostMapping("/api/edit/employee/config")
    public Integer editEmployeeConfig(@RequestBody List<ClinicEmployeeConfig> clinicEmployeeConfigs);

    /**
     * 根据员工ID查询该员工在所有门诊的可预约可挂号信息
     * @param employeeId
     * @return
     */
    @ApiOperation(value = "根据员工ID查询该员工在所有门诊的可预约可挂号信息--内部服务使用",hidden = true)
    @GetMapping("/api/employee/config/{employeeId}")
    public List<ClinicEmployeeConfig> findClinicEmployeeConfigs(@PathVariable(value = "employeeId") Integer employeeId);
}
