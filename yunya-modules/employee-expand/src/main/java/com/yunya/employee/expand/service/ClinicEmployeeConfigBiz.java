package com.yunya.employee.expand.service;


import com.yunya.employee.expand.mapper.ClinicEmployeeConfigMapper;
import com.yunya.employee.expand.model.request.ClinicEmployeeConfigQueryReq;
import com.yunya.employee.expand.model.request.ClinicEmployeeConfigReq;
import com.yunya.employee.expand.model.response.ClinicEmployeeConfigRes;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.epcommon.ClinicEmployeeConfig;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
        if (BusinessConstants.USER_RESIGNATION_STATUS.equals(employee.getWorkStatus())) {
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

    /**
     * 查询门诊员工配置信息
     * @param req
     * @return
     */
    public ClinicEmployeeConfigRes getEmployeeConfig(ClinicEmployeeConfigQueryReq req) {
        Integer employeeId = req.getEmployeeId();
        Example example = new Example(ClinicEmployeeConfig.class);
        example.createCriteria().andEqualTo("clinicId", req.getClinicId())
                                .andEqualTo("employeeId", employeeId);
        ClinicEmployeeConfig config = mapper.selectOneByExample(example);
        ClinicEmployeeConfigRes result = EntityUtils.build(config, ClinicEmployeeConfigRes.class);
        SysUserInfoDetail assistantEmployee = systemServiceFeign.findSysUserEmployeeInfoByUserId(employeeId);
        //todo 查询科室信息
        result.setAssistantName(assistantEmployee.getName());
        return result;
    }

}
