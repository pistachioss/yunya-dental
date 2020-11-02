package com.yunya.middletable.service.patient;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.patient.PatientBaseInfoMapper;
import com.yunya.middletable.dao.patient.PatientOriginMapper;
import com.yunya.middletable.dao.report.BasePatientMapper;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.models.report.BasePatient;
import com.yunya.models.patient_central.PatientBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

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

    @Autowired private PatientOriginMapper patientOriginMapper;

    /**
     * 患者信息操作
     * @param msg 消息
     */
    public void operate(MessageModel msg) {
        Integer patientId = (Integer) msg.getParamMap().get("id");
        Integer operateType = msg.getOperateType();
        BasePatient patient = generatePatientBaseInfo(patientId);
        switch (operateType) {
            case 0:
                mapper.delete(patient);
                mapper.insertSelective(patient);
                break;
            case 1:
                mapper.updateByPrimaryKeySelective(patient);
                break;
            case 2:
                PatientBaseInfo patientBaseInfo = patientBaseInfoMapper.selectByPrimaryKey(patientId);
                if (StringHelper.isNotNull(patientBaseInfo) && StringHelper.isNotNull(patient)){
                    mapper.delete(patient);
                    mapper.insertSelective(patient);
                }
                mapper.delete(patient);
                break;
            default:
                break;
        }
    }

    /**
     * 构建中间表组织信息
     *
     * @param patientId 患者id
     */
    private BasePatient generatePatientBaseInfo(Integer patientId) {
        PatientBaseInfo patientBaseInfo = patientBaseInfoMapper.selectByPrimaryKey(patientId);
        return null != patientBaseInfo ? setPatientBaseInfo(patientId) : null;
    }


    /**
     * 设置患者信息属性
     * @param patientId 患者信息
     * @return BasePatient
     */
    private BasePatient setPatientBaseInfo(Integer patientId) {
        PatientBaseInfo patientBaseInfo = patientBaseInfoMapper.selectByPrimaryKey(patientId);
        if (null != patientBaseInfo){
            BasePatient basePatient = new BasePatient();
            basePatient.setPatientId(patientBaseInfo.getId());
            basePatient.setOrgId(patientBaseInfo.getOrgId());
            basePatient.setName(patientBaseInfo.getName());
            basePatient.setMobile(patientBaseInfo.getMobile());
            basePatient.setMedicalNumber(patientBaseInfo.getMedicalNumber());
            if (patientBaseInfo.getBirthday() != null){
                basePatient.setBirthday(patientBaseInfo.getBirthday());
            }
            if (patientBaseInfo.getOriginId()!=null){
                basePatient.setOriginType(patientBaseInfo.getOriginType());
                basePatient.setOriginId(patientBaseInfo.getOriginId());
                PatientOrigin patientOrigin = new PatientOrigin();
                patientOrigin.setParentId(0);
                patientOrigin.setOriginType(patientBaseInfo.getOriginType());
                PatientOrigin origin = patientOriginMapper.selectOne(patientOrigin);
                if (origin != null){
                    basePatient.setOriginTypeName(origin.getName());
                }
            }
            basePatient.setGender(patientBaseInfo.getGender());
            basePatient.setPinyinName(patientBaseInfo.getPinyinName());
            basePatient.setPatientCrtTime(patientBaseInfo.getCrtTime());
            return basePatient;
        }
        return null;
    }

    /**
     * 拉取某段时间内的组织数据并更新中间表
     *
     * @param form 拉取时间
     */
    public void pullPatientData(PullForm form) {
        String startDate = form.getStartDate();
        String endDate = form.getEndDate();
        Example emp = new Example(PatientBaseInfo.class);
        emp.createCriteria().andBetween("updTime",startDate,endDate);
        List<PatientBaseInfo> patientBaseInfos = patientBaseInfoMapper.selectByExample(emp);
        if (StringHelper.isNotEmpty(patientBaseInfos)) {
            patientBaseInfos.forEach(
                    patientBaseInfo -> {
                        Integer patientId = patientBaseInfo.getId();
                        mapper.deleteByPrimaryKey(patientId);
                        BasePatient patient = setPatientBaseInfo(patientId);
                        mapper.insertSelective(patient);
                    }
            );
        }


    }

    /**
     * 修改患者信息
     * @param basePatient 患者信息
     */
    public void upd(BasePatient basePatient) {
        mapper.updateByPrimaryKeySelective(basePatient);
    }
}