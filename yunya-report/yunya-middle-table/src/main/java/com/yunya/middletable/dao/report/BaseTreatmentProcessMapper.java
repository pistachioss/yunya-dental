package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseTreatmentProcess;
import com.yunya.models.report.StatEmpTreat;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseTreatmentProcessMapper extends Mapper<BaseTreatmentProcess> {
  /**
   * 根据预约ID查询就诊流程信息
   *
   * @param appointmentId 预约ID
   * @return
   */
  BaseTreatmentProcess selectOneByAppointmentId(@Param("appointmentId") Integer appointmentId);

  /**
   * 根据挂号ID查询就诊流程信息
   *
   * @param registeredId 挂号ID
   * @return
   */
  BaseTreatmentProcess selectOneByRegisteredId(@Param("registeredId") Integer registeredId);

  /**
   * 根据就诊ID查询就诊流程信息
   *
   * @param treatmentId 就诊ID
   * @return
   */
  BaseTreatmentProcess selectOneByTreatmentId(@Param("treatmentId") Integer treatmentId);

  /**
   * 根据预约ID更新就诊流程
   *
   * @param appointmentId 预约ID
   * @param treatmentProcess 更新信息
   */
  void updateByAppointmentId(
      @Param("appointmentId") Integer appointmentId,
      @Param("treatmentProcess") BaseTreatmentProcess treatmentProcess);

  /**
   * 根据挂号ID更新就诊流程
   *
   * @param registeredId 挂号ID
   * @param treatmentProcess 更新信息
   */
  void updateByRegisteredId(
      @Param("registeredId") Integer registeredId,
      @Param("treatmentProcess") BaseTreatmentProcess treatmentProcess);

  /**
   * 根据就诊ID更新就诊流程
   *
   * @param treatmentRecordId 就诊ID
   * @param treatmentProcess 更新信息
   */
  void updateByTreatmentRecordId(
      @Param("treatmentId") Integer treatmentRecordId,
      @Param("treatmentProcess") BaseTreatmentProcess treatmentProcess);

  /**
   * 批量插入就诊流程
   *
   * @param treatmentProcesses 就诊流程列表
   */
  void batchInsertSelective(
      @Param("treatmentProcesses") List<BaseTreatmentProcess> treatmentProcesses);

  /**
   * 根据预约ID删除
   *
   * @param appointmentId 预约ID
   */
  void deleteByAppointmentId(@Param("appointmentId") Integer appointmentId);

  /**
   * 根据挂号ID删除
   *
   * @param registeredId 挂号ID
   */
  void deleteByRegisteredId(@Param("registeredId") Integer registeredId);

  void updateAssistantValueByTreatmentId(@Param("treatmentProcess") BaseTreatmentProcess treatmentProcess);

  /**
   * 根据预约id清空挂号信息
   *
   * @param appointmentId
   */
  void updateRegisteredValueByAppointmentId(@Param("appointmentId") Integer appointmentId);

  StatEmpTreat countTreatNumByDateAndDentist(
          @Param("orgId") Integer orgId,
          @Param("dentistId") Integer dentistId,
          @Param("treatDate") Integer treatDate);

  List<StatEmpTreat> countTreatNumByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

  BaseTreatmentProcess selectOneInMonthAndPreTreat(
          @Param("orgId") Integer orgId,
          @Param("patientId") Integer patientId,
          @Param("dentistId") Integer dentistId,
          @Param("treatmentId") Integer treatmentId,
          @Param("sDate") String sDate,
          @Param("eDate") String eDate);

  List<BaseTreatmentProcess> selectAllTreatmentList();
}
