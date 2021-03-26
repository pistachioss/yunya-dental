package com.yunya.middletable.dao.report;

import com.yunya.models.patient_central.PatientOriginLog;
import com.yunya.models.report.BasePatientOriginLog;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author YK
 */
public interface BasePatientOriginLogMapper extends Mapper<BasePatientOriginLog> {

    /**
     * 批量删除
     * @param patientOriginLogList 患者来源关系批量删除
     */
    void deleteList(@Param("list") List<PatientOriginLog> patientOriginLogList);

    /**
     * 批量插入
     * @param patientOriginLogList 患者来源关系批量插入
     */
    void insertList(@Param("list") List<PatientOriginLog> patientOriginLogList);
}