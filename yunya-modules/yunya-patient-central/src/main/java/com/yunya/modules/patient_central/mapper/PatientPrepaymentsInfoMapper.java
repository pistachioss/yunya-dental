package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.vo.PatientPrepaymentsInfoVo;
import com.yunya.models.patient_central.PatientPrepaymentsInfo;
import tk.mybatis.mapper.common.Mapper;

public interface PatientPrepaymentsInfoMapper extends Mapper<PatientPrepaymentsInfo> {
    /**
     * 患者预付款基本信息查询
     * @param id
     * @return PatientPrepaymentRelationVo
     */
    PatientPrepaymentsInfoVo findPrepaymentInfo(Integer id);
}