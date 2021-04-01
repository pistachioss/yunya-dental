package com.yunya.middletable.dao.report;

import com.yunya.feign.report.domain.vo.PatientTreatInfoVo;
import com.yunya.models.report.BasePatient;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BasePatientMapper extends Mapper<BasePatient> {
    /**
     * 根据患者id 查询患者初诊信息
     * @param patientId 患者id
     * @return 初诊信息
     */
    PatientTreatInfoVo selectFirstVisitInfo(@Param("patientId") Integer patientId);

    /**
     * 根据患者id 查询患者末诊信息
     * @param patientId 患者id
     * @return 末诊信息
     */
    PatientTreatInfoVo selectLastVisitInfo(@Param("patientId") Integer patientId);

    /**
     * 查询有过初诊的患者id
     * @return 患者id
     */
    List<BasePatient> selectPatientIdList();

    /**
     * 批量修改患者信息
     * @param basePatientList 患者信息
     */
    void updatePatientInfoList(@Param("list") List<BasePatient> basePatientList);

    /**
     * 查询患者初诊信息
     * @return 初诊信息
     */
    List<PatientTreatInfoVo> selectFirstVisitInfoList();

    /**
     * 查询患者末诊信息
     * @return 末诊信息
     */
    List<PatientTreatInfoVo> selectLastVisitInfoList();
}