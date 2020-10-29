package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.PatientReportQueryForm;
import com.yunya.feign.report.domain.vo.BasePatientNotSeenVo;
import com.yunya.models.report.BasePatient;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author YK
 */
public interface BasePatientMapper extends Mapper<BasePatient> {

    /**
     * 根据姓名/手机号/拼音姓名
     * @param combination 查询条件
     * @return List<Integer>
     */
    List<Integer> selectKilePatientId(@Param("combination") String combination);

    /**
     * 患者报表-未复诊预约且未提醒List
     * @param form 查询条件
     * @param patientIds 患者ids
     * @return List<BasePatientNotSeenVo>
     */
    List<BasePatientNotSeenVo> selectNotSeenList(@Param("form") PatientReportQueryForm form, @Param("patientIds") List<Integer> patientIds);
}