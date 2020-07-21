package com.yunya.employee.common.service;


import com.yunya.employee.common.mapper.ClinicEmployeeConfigMapper;
import com.yunya.employee.common.model.request.ClinicEmployeeConfigQueryReq;
import com.yunya.employee.common.model.request.ClinicEmployeeConfigReq;
import com.yunya.employee.common.model.response.ClinicEmployeeConfigRes;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.enums.EmployeeWorkStatuEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.epcommon.ClinicEmployeeConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-05-27 9:59
 */
@Service
public class ClinicEmployeeConfigBiz extends BaseBiz<ClinicEmployeeConfigMapper, ClinicEmployeeConfig> {

    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;

    /**
     * 门诊端 员工配置
     * @param employeeId
     * @param configRequest
     */
    public void modifyClinicEmployeeConfig(Integer employeeId, ClinicEmployeeConfigReq configRequest) {
        SysUserInfoDetail employee = systemServiceFeign.findSysUserEmployeeInfoByUserId(employeeId);
        if (EmployeeWorkStatuEnum.LEAVING.getCode().equals(employee.getWorkStatus())) {
            throw new ClientServiceException("员工已离职",null);
        }
        //查询该员工对应扩展表主键
        ClinicEmployeeConfig clinicEmployeeConfig = new ClinicEmployeeConfig();
        clinicEmployeeConfig.setEmployeeId(employeeId);
        clinicEmployeeConfig = mapper.selectOne(clinicEmployeeConfig);
        ClinicEmployeeConfig updateEmployee = EntityUtils.build(configRequest, ClinicEmployeeConfig.class);
        updateEmployee.setId(clinicEmployeeConfig.getId());
        mapper.updateByPrimaryKeySelective(updateEmployee);
    }

    public ClinicEmployeeConfigRes getEmployeeConfig(ClinicEmployeeConfigQueryReq req) {
        return null;
    }

}
