package com.yunya.middletable.service.patient;

import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.middletable.dao.patient.PatientBaseInfoMapper;
import com.yunya.middletable.dao.report.BasePatientMapper;
import com.yunya.models.middletable.BasePatient;
import com.yunya.models.patient_central.PatientBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介: 报表服务患者信息同步
 *
 * @author: WY
 * @date: 2020/10/15 13:23
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BasePatientBiz extends BaseBiz<BasePatientMapper, BasePatient> {
    /**注入对象*/
    @Autowired private PatientBaseInfoMapper patientBaseInfoMapper;

    /**
     * 患者信息操作
     * @param msg
     */
    public void operate(MessageModel msg) {
        Integer patientId = msg.getId();
        Integer operateType = msg.getOperateType();
        BasePatient patient = setPatientBaseInfo(patientId);
        if (null == patient){
            return;
        }
        switch (operateType) {
            case 0:
                mapper.delete(patient);
                mapper.insertSelective(patient);
                break;
            case 1:
                mapper.updateByPrimaryKeySelective(patient);
                break;
            case 2:
                mapper.delete(patient);
                break;
            default:
                break;
        }
    }

    private BasePatient setPatientBaseInfo(Integer patientId) {
        PatientBaseInfo patientBaseInfo = patientBaseInfoMapper.selectByPrimaryKey(patientId);
        if (null != patientBaseInfo){
            BasePatient basePatient = new BasePatient();
            basePatient.setPatientId(patientBaseInfo.getId());
            basePatient.setOrgId(patientBaseInfo.getOrgId());
            basePatient.setName(patientBaseInfo.getName());
            basePatient.setMobile(patientBaseInfo.getMobile());
            basePatient.setMedicalNumber(patientBaseInfo.getMedicalNumber());
            basePatient.setBirthday(patientBaseInfo.getBirthday());
            basePatient.setOriginType(patientBaseInfo.getOriginType());
            basePatient.setOriginId(patientBaseInfo.getOriginId());
            basePatient.setGender(patientBaseInfo.getGender());
            return basePatient;
        }
        return null;
    }

    /**
     * 根据时间段批量拉取患者信息
     * @param startDate 开始时间
     * @param endDate 结束时间
     */
    public void pullPatient(String startDate, String endDate) {

    }
}