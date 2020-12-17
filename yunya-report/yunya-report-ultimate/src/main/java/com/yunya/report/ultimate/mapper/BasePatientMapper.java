package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.PatientAnalysisQueryForm;
import com.yunya.feign.report.domain.query.PatientReportQueryForm;
import com.yunya.feign.report.domain.vo.AnalysisPatientGenderVo;
import com.yunya.feign.report.domain.vo.BasePatientNotSeenVo;
import com.yunya.feign.report.domain.vo.AnalysisPatientOriginVo;
import com.yunya.feign.report.domain.vo.PatientDataVo;
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

    /**
     * 查询来源类型总数量
     * @param patientAnalysisQueryForm 来源类型查询条件
     * @return Integer
     */
    Integer selectCountOriginType(@Param("form") PatientAnalysisQueryForm patientAnalysisQueryForm);

    /**
     * 查询来患者源类型占比
     * @param patientAnalysisQueryForm 患者来源查询条件
     * @param countOriginType  来源总数量
     * @return List<PatientOriginAnalysisVo>
     */
    List<AnalysisPatientOriginVo> analysis(@Param("form") PatientAnalysisQueryForm patientAnalysisQueryForm, @Param("countOriginType") Integer countOriginType);

    /**
     * 查询性别总数量
     * @param patientAnalysisQueryForm 查询条件
     * @return Integer
     */
    Integer selectCountGender(@Param("form") PatientAnalysisQueryForm patientAnalysisQueryForm);

    /**
     * 查询男女类型占比
     * @param patientAnalysisQueryForm 查询条件
     * @param countGender 男女总数量
     * @return List<AnalysisPatientGenderVo>
     */
    List<AnalysisPatientGenderVo> selectAnalysisPatientGender(@Param("form") PatientAnalysisQueryForm patientAnalysisQueryForm, @Param("countGender") Integer countGender);

    /**
     * 查询时间段内 患者个数
     * @param patientAnalysisQueryForm 查询条件
     * @return Integer
     */
    Integer selectCountAnalysisAge(@Param("form") PatientAnalysisQueryForm patientAnalysisQueryForm);

    /**
     * 根据条件查询 年龄段患者数量
     * @param patientAnalysisQueryForm 查询条件
     * @param ageOne <=14
     * @param ageTwo >=14 <=60
     * @param ageThree >=60 <=999
     * @return Integer
     */
    Integer selectAnalysisAge(@Param("form") PatientAnalysisQueryForm patientAnalysisQueryForm, @Param("ageOne") int ageOne, @Param("ageTwo") int ageTwo,@Param("ageThree") int ageThree);

    /**
     * 计算百分比
     * @param countAge 年龄段人数
     * @param count 总人数
     * @return String
     */
    String calculateAgePercentage(@Param("countAge") Integer countAge, @Param("count") Integer count);

    /**
     * 查询患者预约信息
     * @param patientId 患者id
     * @return PatientDataVo
     */
    PatientDataVo findPatientDataVo(@Param("patientId") Integer patientId);
}