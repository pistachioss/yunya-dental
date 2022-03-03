package com.yunya.modules.patient_central.mapper;

import com.yunya.models.patient_central.PatientToothInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface PatientToothInfoMapper extends Mapper<PatientToothInfo> {
    PatientToothInfo selectPatientToothInfoByPatientId(@Param("patientId") Integer patientId);
}