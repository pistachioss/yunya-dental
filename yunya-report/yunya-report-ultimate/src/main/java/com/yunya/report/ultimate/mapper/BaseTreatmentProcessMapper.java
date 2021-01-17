package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BaseTreatmentProcess;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseTreatmentProcessMapper extends Mapper<BaseTreatmentProcess> {

  /**
   * 根据条件查询就诊列表
   *
   * @param query 查询条件
   * @return
   */
  List<TreatmentRecordReportVO> selectTreatmentRecordReportVOList(
      @Param("query") TreatmentRecordQuery query);

  /**
   * 根据条件查询就诊配诊记录列表
   *
   * @param query 查询条件
   * @return
   */
  List<TreatmentMatchingRecordVO> selectTreatmentMatchingRecord(
      @Param("query") TreatmentMatchingRecordQuery query);

  /**
   * 根据条件查询助手配诊明细列表
   *
   * @param query 查询条件
   * @return List<EmployeeTreatMatchingDetailVO>
   */
  List<EmployeeTreatMatchingDetailVO> selectAssistantMatchingDetailList(
      @Param("query") AssistantMatchingDetailQuery query);

  /**
   * 根据患者id查询履约次数
   *
   * @param id 患者id
   * @return int
   */
  Integer selectPatientPerformance(@Param("patientId") Integer id);

  /**
   * 根据患者id查询预约次数
   *
   * @param id 患者id
   * @return Integer
   */
  Integer selectPatientReservation(@Param("patientId") Integer id);

  /**
   * 根据患者id查询失约次数
   *
   * @param id 患者id
   * @return Integer
   */
  Integer selectMissedAppointment(@Param("patientId") Integer id);

  /**
   * 查询患者就诊次数
   *
   * @param id 患者id
   * @return 返回患者就诊次数
   */
  Integer selectNumberOfVisits(@Param("patientId") Integer id);

  /**
   * 根据门诊ID,医生ID,当前时间查询就诊信息
   *
   * @param orgId 门诊ID
   * @param dentistId 医生ID
   * @param currentDate 当前日期
   * @return 返回信息列表
   */
  List<BaseTreatmentProcessVO> treatmentList4App(
      @Param("orgId") Integer orgId,
      @Param("dentistId") Integer dentistId,
      @Param("currentDate") String currentDate);

  /**
   * 患者档案-预约信息-履约次数/失约次数/改约次数/取消预约次数
   *
   * @param patientId 患者ID
   * @param query 扩展参数
   * @return
   */
  AppointmentCountVO appointmentCount(
      @Param("patientId") Integer patientId, @Param("query") AppointmentCountQuery query);

  /**
   * 查询患者初诊信息
   *
   * @param id 患者id
   * @return 患者初诊信息
   */
  PatientDataFirstVisitVo selectFirstVisitInfo(@Param("patientId") Integer id);

  /**
   * 查询患者末诊信息
   *
   * @param id 患者id
   * @return 患者末诊信息
   */
  PatientDataVo selectLastVisitInfo(@Param("patientId") Integer id);

  /**
   * 根据条件查询初诊患者来源分布
   *
   * @param query 查询条件
   * @return List<PatientFirstTreatOriginVO>
   */
  List<PatientFirstTreatOriginVO> selectPatientFirstTreatOriginList(
      @Param("query") PatientFirstTreatOriginQuery query);

  /**
   * 根据条件查询初诊患者总数
   *
   * @param query 查询条件
   * @return Integer
   */
  Integer selectFirstTreatTotalCount(@Param("query") PatientFirstTreatOriginQuery query);
}
