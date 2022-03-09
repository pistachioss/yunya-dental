package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.vo.web.PatientChildInfoVO;
import com.yunya.models.patient_central.PatientChildInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface PatientChildInfoMapper extends Mapper<PatientChildInfo> {
    PatientChildInfoVO selectPatientChildInfoByPatientId(@Param("patientId") Integer patientId);
}