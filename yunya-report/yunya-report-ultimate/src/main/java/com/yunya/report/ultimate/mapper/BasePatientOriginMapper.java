package com.yunya.report.ultimate.mapper;

import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.models.report.BasePatientOrigin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WY
 */
@Repository
public interface BasePatientOriginMapper extends Mapper<BasePatientOrigin> {


}