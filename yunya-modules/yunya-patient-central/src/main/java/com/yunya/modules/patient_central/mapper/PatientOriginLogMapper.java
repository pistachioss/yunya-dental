package com.yunya.modules.patient_central.mapper;

import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientOriginLog;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientOriginLogMapper extends Mapper<PatientOriginLog> {
    /**
     * 批量插入 患者来源推荐关系
     * @param patientOriginLogList 患者来源推荐关系
     */
    void insertList(@Param("list") List<PatientOriginLog> patientOriginLogList);


    /**
     * 批量删除
     * @param patientOriginLogList 患者来源关系批量删除
     */
    void deleteList(@Param("list") List<PatientOriginLog> patientOriginLogList);
}