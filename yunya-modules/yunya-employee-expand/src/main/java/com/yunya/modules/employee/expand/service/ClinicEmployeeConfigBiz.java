package com.yunya.modules.employee.expand.service;


import com.google.common.base.Objects;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.expand.ClinicEmployeeConfig;
import com.yunya.modules.employee.expand.mapper.ClinicEmployeeConfigMapper;
import com.yunya.modules.employee.expand.model.request.ClinicEmployeeConfigQueryReq;
import com.yunya.modules.employee.expand.model.request.ClinicEmployeeConfigReq;
import com.yunya.modules.employee.expand.model.response.ClinicEmployeeConfigRes;
import com.yunya.modules.employee.expand.model.response.EnableChooseEmployeeRes;
import com.yunya.modules.employee.expand.model.response.EnableEmployeeRes;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-05-27 9:59
 */
@Service
public class ClinicEmployeeConfigBiz extends BaseBiz<ClinicEmployeeConfigMapper, ClinicEmployeeConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClinicEmployeeConfigBiz.class);

    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;

    /**
     * 门诊端 员工配置
     * @param employeeId
     * @param configRequest
     */
    public void modifyClinicEmployeeConfig(Integer employeeId, Integer clinicId, ClinicEmployeeConfigReq configRequest) {
        SysUserInfoDetail employee = systemServiceFeign.findSysUserEmployeeInfoByUserId(employeeId);
        if (BusinessConstants.USER_RESIGNATION_STATUS.equals(employee.getWorkStatus())) {
            throw new ClientServiceException("员工已离职", OperationCodeConstants.QUERY_RESULT_INVALID);
        }
        //查询该员工对应扩展表主键
        ClinicEmployeeConfig clinicEmployeeConfig = new ClinicEmployeeConfig();
        clinicEmployeeConfig.setEmployeeId(employeeId);
        clinicEmployeeConfig.setClinicId(clinicId);
        clinicEmployeeConfig = mapper.selectOne(clinicEmployeeConfig);
        ClinicEmployeeConfig updateEmployee = EntityUtils.build(configRequest, ClinicEmployeeConfig.class);
        updateEmployee.setEmployeeId(employeeId);
        updateEmployee.setClinicId(clinicId);
        if (clinicEmployeeConfig == null) {
            mapper.insertSelective(updateEmployee);
        } else {
            updateEmployee.setId(clinicEmployeeConfig.getId());
            mapper.updateByPrimaryKeySelective(updateEmployee);
        }
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

    /**
     * 查询可预约，可挂号医生
     * @param clinicId 门诊id
     * @return
     */
    public EnableEmployeeRes getAllEnableEmployee(Integer clinicId) {
        LOGGER.info("查询可预约，挂号接口请求参数：clinic:{}",clinicId);
        Example example = new Example(ClinicEmployeeConfig.class);
        example.createCriteria().andEqualTo("clinicId",clinicId);
        List<ClinicEmployeeConfig> allList = mapper.selectByExample(example);
        EnableEmployeeRes res = new EnableEmployeeRes();
        if (CollectionUtils.isNotEmpty(allList)) {
            //可预约医生
            List<ClinicEmployeeConfig> appointList = allList.stream().filter(config -> Objects.equal(BusinessConstants.ENABLE_NUM,config.getEnableAppoint()))
                                                            .collect(Collectors.toList());
            //可挂号医生
            List<ClinicEmployeeConfig> registerList = allList.stream().filter(config -> Objects.equal(BusinessConstants.ENABLE_NUM,config.getEnableRegistry()))
                                                            .collect(Collectors.toList());
            List<EnableChooseEmployeeRes> appointResList = appointList.stream().map(this::assembleEnableEmployee).collect(Collectors.toList());
            List<EnableChooseEmployeeRes> registerResList = registerList.stream().map(this::assembleEnableEmployee).collect(Collectors.toList());
            res.setEnableAppointList(appointResList);
            res.setEnableRegistryList(registerResList);
        }
        return res;
    }

    private EnableChooseEmployeeRes assembleEnableEmployee(ClinicEmployeeConfig config) {
        EnableChooseEmployeeRes configRes = new EnableChooseEmployeeRes();
        BeanCopier copier = BeanCopier.create(ClinicEmployeeConfig.class, EnableChooseEmployeeRes.class, false);
        copier.copy(config,configRes,null);
        SysUserInfoDetail employee = systemServiceFeign.findSysUserEmployeeInfoByUserId(config.getEmployeeId());
        SysUserInfoDetail assist = systemServiceFeign.findSysUserEmployeeInfoByUserId(config.getAssistantEmployeeId());
        configRes.setAssistantName(assist == null ? null : assist.getUsername());
        configRes.setEmployeeName(employee == null ? null : employee.getUsername());
        //TODO 科室
        return configRes;
    }


}
