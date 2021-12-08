package com.yunya.report.ultimate.mapper;

import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.feign.wechat.domain.vo.WxAppointConfirmPushVo;
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
   * @param patientId 患者id
   * @return 患者初诊信息
   */
  PatientTreatInfoVo selectFirstVisitInfo(@Param("patientId") Integer patientId);

  /**
   * 查询患者末诊信息
   *
   * @param patientId 患者id
   * @return 患者末诊信息
   */
  PatientTreatInfoVo selectLastVisitInfo(@Param("patientId") Integer patientId);

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

  /**
   * 查询患者预约相关信息
   *
   * @param patientId 患者ID
   * @return PatientAppointmentInfoVO 预约相关信息
   */
  PatientAppointmentInfoVO selectPatientAppointmentInfo(@Param("patientId") Integer patientId);

  /**
   * 查询初诊报表
   *
   * @param
   * @return
   */
  List<FirstVisitVO> firstVisitRecord(FirstVisitQuery query);
  /**
   * 查询初诊报表患者数量合计
   *
   * @param
   * @return
   */
  Integer firstVisitRecordCount(FirstVisitQuery query);

  /**
   * 查询初诊报表查看明细
   * @param query
   * @return
   */
  List<FirstVisitDetailVO> firstVisitRecordDetail(FirstVisitDetailQuery query);

  /**
   * 查询初诊报表查看明细
   * @param
   * @return
   */
  List<FirstVisitDetailVO> findFirstVisitRecordDetail();

  /**
   * 查询个人初诊报表查看明细
   * @param query
   * @return
   */
  List<FirstVisitPersonalVO> firstVisitRecordPersonalList(FirstVisitPersonalQuery query);

  /**
   * 查询wx需要推送的预约未到且未确认的数据
   */
  List<WxAppointConfirmPushVo> listAppointConfirm();

  /**
   * 查询在条件门诊就诊过的患者
   * @param form 条件
   * @param orgId 门诊id
   * @return
   */
    List<PatientBaseInfoVo> findPatientLikePatientInfo(@Param("form") PatientLikeFinleQueryForm form, @Param("orgId") Integer orgId);

    List<InMonthReFirstVisitVO> selectInMonthReFirstVisit(@Param("patientIds") List<Integer> patientIds);

    List<PatientDateVO> selectNextAppointDateByPatientId(@Param("patientIds") List<Integer> patientIds);

  List<PatientDateVO> selectFirstVisitDateByPatientId(@Param("patientIds") List<Integer> patientIds);

  List<PatientDateVO> selectLastVisitDateByPatientId(@Param("patientIds") List<Integer> patientIds);

  List<PatientCountVO> selectPatientTreatNum(@Param("patientIds") List<Integer> patientIds);

  List<EmployeeCountVO> selectPatientTreatNumGroupEmp(@Param("query") ClinicEmployeeWorkloadQuery query, @Param("treatType") Integer treatType);

  List<EmployeeCountVO> selectTreatVisitsTimes(@Param("query") ClinicEmployeeWorkloadQuery query);
}
