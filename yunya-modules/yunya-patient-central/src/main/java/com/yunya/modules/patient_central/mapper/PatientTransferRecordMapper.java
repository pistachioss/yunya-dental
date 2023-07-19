package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.PatientTransferRecordQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientTransferRecordVO;
import com.yunya.models.patient_central.PatientTransferRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientTransferRecordMapper extends Mapper<PatientTransferRecord> {
    List<PatientTransferRecordVO> selectPatientTransferRecords(@Param("query") PatientTransferRecordQuery query);
}