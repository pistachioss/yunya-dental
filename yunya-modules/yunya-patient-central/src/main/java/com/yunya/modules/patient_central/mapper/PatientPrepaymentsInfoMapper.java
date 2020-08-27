package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.vo.PatientPrepaymentsInfoVo;
import com.yunya.models.patient_central.PatientPrepaymentsInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface PatientPrepaymentsInfoMapper extends Mapper<PatientPrepaymentsInfo> {
    /**
     * 根据患者id患者预付款基本信息查询
     * @param id
     * @return PatientPrepaymentRelationVo
     */
    PatientPrepaymentsInfoVo findPrepaymentInfo(@Param("id") Integer id);

    /**
     * 根据预付款卡号和患者id查询预付款信息
     * @param prepaidId
     * @return PatientPrepaymentsInfo
     */
    PatientPrepaymentsInfo selectOneByCardNumber(@Param("prepaidId") String prepaidId,@Param("patientId") Integer patientId);
}