package com.yunya.modules.patient_central.biz;

import com.yunya.feign.patient_central.domain.vo.PatientPrepaymentsInfoVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.patient_central.PatientPrepaymentRelation;
import com.yunya.modules.patient_central.mapper.PatientPrepaymentRelationMapper;
import com.yunya.modules.patient_central.mapper.PatientPrepaymentsInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/7/31 9:31
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientPrepaymentRelationBiz extends BaseBiz<PatientPrepaymentRelationMapper, PatientPrepaymentRelation> {

    @Autowired PatientPrepaymentRelationMapper patientPrepaymentRelationMapper;

    @Autowired PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

    /**
     * 患者预付款基本信息查询
     * @return PatientPrepaymentRelationVo
     */
    public PatientPrepaymentsInfoVo findPrepaymentInfo(Integer id) {
        return patientPrepaymentsInfoMapper.findPrepaymentInfo(id);
    }
}
