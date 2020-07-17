package com.yunya.employee.common.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.yunya.clinic.employee.common.client.SysUserClient;
import com.yunya.clinic.employee.common.client.SysUserPostClient;
import com.yunya.clinic.employee.common.constant.*;
import com.yunya.clinic.employee.common.entity.BaseEmployee;
import com.yunya.clinic.employee.common.entity.ClinicEmployee;
import com.yunya.clinic.employee.common.exception.EmployeeCommonException;
import com.yunya.clinic.employee.common.mapper.BaseEmployeeMapper;
import com.yunya.clinic.employee.common.mapper.ClinicEmployeeMapper;
import com.yunya.clinic.employee.common.model.request.ClinicEmployeeConfigReq;
import com.yunya.clinic.employee.common.model.request.ClinicEmployeePageReq;
import com.yunya.clinic.employee.common.model.response.BaseEmployeePageRes;
import com.yunya.clinic.employee.common.model.response.ClinicEmployeePageRes;
import com.yunya.clinic.employee.common.model.response.SysUserDetailRes;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-05-27 9:59
 */
@Service
public class ClinicEmployeeBiz extends BaseBiz<ClinicEmployeeMapper, ClinicEmployee> {

    @Resource
    private BaseEmployeeMapper baseEmployeeMapper;

    @Resource
    private SysUserPostClient sysUserPostClient;

    @Resource
    private SysUserClient sysUserClient;


    /**
     * 新增
     *
     * @param clinicEmployee
     */
    public void saveClinicEmployee(ClinicEmployee clinicEmployee) {
        ClinicEmployee data = new ClinicEmployee();
        data.setUpdId(clinicEmployee.getClinicId());
        data.setClinicId(clinicEmployee.getClinicId());
        if (mapper.selectOne(data) != null) {
            throw new EmployeeCommonException("员工已存在");
        }

        insertSelective(clinicEmployee);
    }

    /**
     * 修改
     *
     * @param clinicEmployee
     */
    public void updateClinicEmployee(ClinicEmployee clinicEmployee) {
        Integer employeeId = clinicEmployee.getEmployeeId();
        Integer clinicId = clinicEmployee.getClinicId();
        ClinicEmployee data = selectById(clinicEmployee.getId());

        if (data == null || !(employeeId.equals(data.getEmployeeId()) || clinicId.equals(data.getClinicId()))) {
            throw new EmployeeCommonException("员工不存在");
        }

        updateSelectiveById(clinicEmployee);
    }

    /**
     * 门诊端 员工配置
     * @param employeeId
     * @param configRequest
     */
    public void modifyClinicEmployeeConfig(Integer employeeId, ClinicEmployeeConfigReq configRequest) {
        BaseEmployee baseEmployee = new BaseEmployee();
        baseEmployee.setUserId(employeeId);
        BaseEmployee employee = baseEmployeeMapper.selectOne(baseEmployee);
        if (EmployeeTypeEnum.LEAVING.getCode().equals(employee.getType())) {
            throw new EmployeeCommonException("员工已离职");
        }
        //查询该员工对应扩展表主键
        ClinicEmployee clinicEmployee = new ClinicEmployee();
        clinicEmployee.setEmployeeId(employeeId);
        clinicEmployee = mapper.selectOne(clinicEmployee);
        ClinicEmployee updateEmployee = EntityUtils.build(configRequest, ClinicEmployee.class);
        updateEmployee.setId(clinicEmployee.getId());
        mapper.updateByPrimaryKeySelective(updateEmployee);
    }

    /**
     * 门诊端员工列表
     * @param pageReq
     * @return
     */
    public PageInfo<ClinicEmployeePageRes> getEmployeePageList(ClinicEmployeePageReq pageReq) {
        //查询岗位下的用户Ids
        List<Integer> postUserIds = sysUserPostClient.getPostUser(pageReq.getPostIds());
        PageHelper.startPage(pageReq.getPageNum(), pageReq.getPageSize());
        List<BaseEmployeePageRes> baseEmployeeList = baseEmployeeMapper.getBaseEmployeeList(pageReq.getEmployeeKeywords(),
                postUserIds, null);
        List<ClinicEmployeePageRes> resultList = EntityUtils.build(baseEmployeeList, ClinicEmployeePageRes.class);
        List<Integer> employeeIds = resultList.stream().map(ClinicEmployeePageRes::getEmployeeId).collect(Collectors.toList());
        //获取用户岗位名称映射
        Map<Integer, String> userPostNameMap = sysUserPostClient.getUserPostName(employeeIds);
        //获取系统用户信息
        List<SysUserDetailRes> userList = sysUserClient.getUserRecord(employeeIds);
        Map<Integer, SysUserDetailRes> sysUserMap = userList.stream().collect(Collectors.toMap(SysUserDetailRes::getId,
                    Function.identity(), (v1, v2) -> v2));
        resultList.forEach(r -> {
            r.setEmployeePost(userPostNameMap.get(r.getEmployeeId()));
            r.setBirthday(sysUserMap.get(r.getEmployeeId()) == null ? null : sysUserMap.get(r.getEmployeeId()).getBirthday());
        });
        return new PageInfo<ClinicEmployeePageRes>(resultList);
    }


}
