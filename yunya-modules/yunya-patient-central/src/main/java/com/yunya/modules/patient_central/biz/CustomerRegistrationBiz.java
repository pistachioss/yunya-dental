package com.yunya.modules.patient_central.biz;

import com.yunya.feign.patient_central.domain.model.CustomerRegistrationModel;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientOriginMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介: 客户登记业务层
 *
 * @author: WY
 * @date: 2020/11/5 17:49
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CustomerRegistrationBiz extends BaseBiz<PatientBaseInfoMapper, PatientBaseInfo> {

    /** 注入患者Biz */
    @Autowired private PatientBaseInfoBiz  patientBaseInfoBiz;

    /** 注入患者来源Mapper */
    @Autowired private PatientOriginMapper patientOriginMapper;

    /**
     * 添加客户登记
     * @param customerRegistrationModel  客户登记
     * @return PatientBaseInfoVo
     */
    public PatientBaseInfoVo addPatient(CustomerRegistrationModel customerRegistrationModel) {
        PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
        BeanUtils.copyProperties(customerRegistrationModel, patientBaseInfo);
        if (patientBaseInfo.getOriginId() != null) {
            PatientOrigin patientOrigin =
                    this.patientOriginMapper.selectByPrimaryKey(patientBaseInfo.getOriginId());
            if (patientOrigin != null) {
                patientBaseInfo.setOriginType(patientOrigin.getOriginType());
            }
        }
        patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
        patientBaseInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientBaseInfo.setCrtName(BaseContextHandler.getName());
        patientBaseInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        mapper.insertPatientInfo(patientBaseInfo);

        PatientBaseInfoVo patientBaseInfoVo =
                mapper.selectPatientInfoByNameAndMobileAndOrgId(patientBaseInfo);
        // 创建预付款 并发送消息
        patientBaseInfoBiz.sendMessages(patientBaseInfo.getId(), 0);
        patientBaseInfoBiz.addPatientPrepaymentsInfo(patientBaseInfo);
        return patientBaseInfoVo;
    }
}