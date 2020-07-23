package com.yunya.modules.employeeattend.service;


import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.BaseSchedule;
import com.yunya.models.employee_attend.ClinicSchedule;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.modules.employeeattend.form.ClinicCommonForm;
import com.yunya.modules.employeeattend.form.ClinicNode;
import com.yunya.modules.employeeattend.mapper.BaseScheduleMapper;
import com.yunya.modules.employeeattend.vo.BaseInserviceVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-05-15 13:17
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseScheduleBiz extends BaseBiz<BaseScheduleMapper, BaseSchedule> {
    @Autowired
    private ClinicScheduleBiz clinicScheduleBiz;
    @Autowired
    private EmployeeScheduleBiz employeeScheduleBiz;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    /**
     * 新增排班
     *
     * @param baseSchedule
     */
    public void saveBaseSchedule(BaseSchedule baseSchedule) {
        BaseSchedule data = new BaseSchedule();
        data.setName(baseSchedule.getName());
        if (mapper.selectOne(data) != null) {
            throw new ClientServiceException("排班冲突",OperationCodeConstants.SAME_DATA_EXIST);
        }

        if (compareDate(baseSchedule)) {
            insertSelective(baseSchedule);
        }

        Integer id = baseSchedule.getId();
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);

        for (OrganizationInfoDetail clinic : clinics) {
            ClinicSchedule clinicSchedule = new ClinicSchedule();
            clinicSchedule.setScheduleId(id);
            clinicSchedule.setClinicId(clinic.getId());
            clinicScheduleBiz.insertSelective(clinicSchedule);
        }
    }

    /**
     * 修改排班
     *
     * @param baseSchedule
     */
    public void updateBaseSchedule(BaseSchedule baseSchedule) {
        BaseSchedule data = new BaseSchedule();
        String name = baseSchedule.getName();
        if (StringUtils.isNotBlank(name)) {
            data.setName(name);
            if (mapper.select(data).size() >= 2) {
                throw new ClientServiceException("修改失败",OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        }

        if (compareDate(baseSchedule)) {
            mapper.updateById(baseSchedule);
        }
    }

    /**
     * 判断时间是否冲突
     *
     * @param baseSchedule
     * @return
     */
    private boolean compareDate(BaseSchedule baseSchedule) {
        Date firstStartTime = baseSchedule.getFirstStartTime();
        Date firstEndTime = baseSchedule.getFirstEndTime();
        Date secondStartTime = baseSchedule.getSecondStartTime();
        Date secondEndTime = baseSchedule.getSecondEndTime();

        if (firstEndTime == null || firstStartTime == null) {
            throw new ClientServiceException("开始时间点为空",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }

        if (firstEndTime.before(firstStartTime)) {
            throw new ClientServiceException("结束时间点早于开始时间点",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }

        if (secondStartTime != null && secondEndTime != null) {
            if (secondStartTime.before(firstEndTime) || secondEndTime.before(secondStartTime)) {
                throw new ClientServiceException("时间冲突",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
            }
        }

        return true;
    }

    /**
     * 设置门诊是否可用
     *
     * @param clinicCommonForm
     */
    public void setting(ClinicCommonForm clinicCommonForm) {
        Integer id = clinicCommonForm.getId();
        if (selectById(id) == null) {
            throw new ClientServiceException("查询无结果",OperationCodeConstants.RETURN_VALUE_ISNULL);
        }

        List<ClinicNode> clinicScheduleNodes = clinicCommonForm.getClinicNodes();
        if (!clinicScheduleNodes.isEmpty()) {
            ClinicSchedule data = new ClinicSchedule();
            data.setScheduleId(id);
            clinicScheduleBiz.delete(data);

            clinicScheduleBiz.batchInsert(id, clinicScheduleNodes);
        }
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(Integer id) {
        EmployeeSchedule employeeSchedule = new EmployeeSchedule();
        employeeSchedule.setScheduleId(id);
        if (!employeeScheduleBiz.selectList(employeeSchedule).isEmpty()) {
            throw new ClientServiceException("查询无结果",OperationCodeConstants.RETURN_VALUE_ISNULL);
        }
        deleteById(id);

        ClinicSchedule clinicSchedule = new ClinicSchedule();
        clinicSchedule.setScheduleId(id);
        clinicScheduleBiz.delete(clinicSchedule);
    }

    /**
     * 排班门诊列表
     *
     * @param scheduleId
     */
    public List<BaseInserviceVO> clinicList(Integer scheduleId) {
        //获取门诊信息
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);

        List<BaseInserviceVO>list = mapper.selectInserviceVOsByScheduleId(scheduleId);
        Map<String, BaseInserviceVO> BaseMap = new HashMap();
        list.forEach(z -> BaseMap.put(z.getClinicId() + "", z));

        List<BaseInserviceVO>relist = new ArrayList<>();
        for (OrganizationInfoDetail organizationInfoDetail:clinics){
            BaseInserviceVO baseInserviceVO = new BaseInserviceVO();
            baseInserviceVO.setClinicId(organizationInfoDetail.getId());
            baseInserviceVO.setClinicName(organizationInfoDetail.getName());
            if(BaseMap.get(organizationInfoDetail.getId()+"")!=null){
                baseInserviceVO.setInservice(BaseMap.get(organizationInfoDetail.getId()+"").getInservice());
            }else{
                baseInserviceVO.setInservice(false);
            }
            relist.add(baseInserviceVO);
        }
        return relist;
    }

    /**
     * 查询列表
     *
     * @param typeName
     * @param name
     * @return
     */
    public List<BaseSchedule> search(String typeName, String name) {
        return mapper.selectByTypeAndName(typeName, name);
    }
}
