package com.yunya.feign.expand.factory;

import com.yunya.feign.expand.RemoteClinicEmployeeConfigFeign;
import com.yunya.feign.expand.model.response.EnableEmployeeRes;
import com.yunya.models.expand.ClinicEmployeeConfig;

import java.util.List;

/**
 * @program: yunya-dental
 * @description: 服务降级
 * @author: LHB
 * @create: 2021-01-17 13:35
 **/
public class RemoteClinicEmployeeConfigFactory implements RemoteClinicEmployeeConfigFeign {
    @Override
    public EnableEmployeeRes getEnableEmployeeList(Integer clinicId) {
        return null;
    }

    @Override
    public Integer addEmployeeConfig(ClinicEmployeeConfig clinicEmployeeConfig) {
        return null;
    }

    @Override
    public Integer editEmployeeConfig(List<ClinicEmployeeConfig> clinicEmployeeConfigs) {
        return null;
    }

    @Override
    public List<ClinicEmployeeConfig> findClinicEmployeeConfigs(Integer employeeId) {
        return null;
    }
}
