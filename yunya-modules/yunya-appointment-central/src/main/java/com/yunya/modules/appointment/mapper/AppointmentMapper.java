package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.domain.query.AppointPatientRecordQuery;
import com.yunya.feign.appointment.domain.query.AppointmentCurrentListQuery;
import com.yunya.feign.appointment.domain.query.AppointmentQuery;
import com.yunya.feign.appointment.vo.*;
import com.yunya.models.appointment.Appointment;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface AppointmentMapper extends Mapper<Appointment> {

  /**
   * 根据设备id，预约开始时间，预约结束时间查询所有预约列表
   *
   * @param deviceId 设备id
   * @param appointStartTime 预约开始时间
   * @param appointEndTime 预约结束时间
   * @return 预约冲突信息
   */
  List<AppointConflictInfoVo> findAppointListByDeviceIdAndAppointStartTimeAndAppointEndTime(
      @Param("deviceId") Integer deviceId,
      @Param("appointStartTime") Date appointStartTime,
      @Param("appointEndTime") Date appointEndTime);

  /**
   * 根据患者id，预约开始时间，预约结束时间查询所有预约列表
   *
   * @param patientId 患者Id
   * @param appointStartTime 预约开始时间
   * @param appointEndTime 预约结束时间
   * @return 预约冲突信息
   */
  List<AppointConflictInfoVo> findAppointListByPatientIdAndAppointStartTimeAndAppointEndTime(
      @Param("patientId") Integer patientId,
      @Param("appointStartTime") Date appointStartTime,
      @Param("appointEndTime") Date appointEndTime);

  /**
   * 根据医生id，预约开始时间，预约结束时间查询所有预约列表
   *
   * @param dentistId 医生id
   * @param appointStartTime 预约开始时间
   * @param appointEndTime 预约结束时间
   * @return 预约冲突信息
   */
  List<AppointConflictInfoVo> findAppointListByDentistIdAndAppointStartTimeAndAppointEndTime(
      @Param("dentistId") Integer dentistId,
      @Param("appointStartTime") Date appointStartTime,
      @Param("appointEndTime") Date appointEndTime);

  /**
   * 根据条件查询预约列表
   *
   * @param query 条件查询参数
   * @return
   */
  List<AppointmentVo> findAppointmentByExample(@Param("query") AppointmentQuery query);

  /**
   * 添加预约
   *
   * @param appointment 预约信息
   * @return 插入预约的id
   */
  Integer insertAppointment(Appointment appointment);

  /**
   * 编辑预约检查患者预约冲突（排除自身）
   *
   * @param id
   * @param pId
   * @param appointStartTime
   * @param appointEndTime
   * @return
   */
  List<AppointConflictInfoVo> editCheckPatientConflict(
      @Param("id") Integer id,
      @Param("patientId") Integer pId,
      @Param("appointStartTime") Date appointStartTime,
      @Param("appointEndTime") Date appointEndTime);

  /**
   * 编辑预约检查医生预约冲突（排除自身）
   *
   * @param id
   * @param dentistId
   * @param appointStartTime
   * @param appointEndTime
   * @return
   */
  List<AppointConflictInfoVo> editCheckDentistConflict(
      @Param("id") Integer id,
      @Param("dentistId") Integer dentistId,
      @Param("appointStartTime") Date appointStartTime,
      @Param("appointEndTime") Date appointEndTime);

  /**
   * 编辑预约检查设备预约冲突（排除自身）
   *
   * @param id
   * @param deviceId
   * @param appointStartTime
   * @param appointEndTime
   * @return
   */
  List<AppointConflictInfoVo> editCheckDeviceConflict(
      @Param("id") Integer id,
      @Param("deviceId") Integer deviceId,
      @Param("appointStartTime") Date appointStartTime,
      @Param("appointEndTime") Date appointEndTime);

  /**
   * 根据日期查询失约患者名单
   *
   * @param currentDate 当前日期
   * @return
   */
  List<Appointment> findMissedAppointmentByDate(@Param("currentDate") Date currentDate);

  /**
   * 根据预约id查询预约
   *
   * @param id 预约id
   * @return
   */
  AppointmentVo findAppointmentById(@Param("id") Integer id);

  /**
   * 根据时间查询预约可视图（患者维度）
   *
   * @param startDate 开始时间
   * @param endDate 结束时间
   * @param dentistId 医生id
   * @param orgId 门诊id
   * @return
   */
  List<AppointmentDimensionVo> findAppointmentDimensionInfoByDateAndDentistId(
      @Param("startDate") Date startDate,
      @Param("endDate") Date endDate,
      @Param("dentistId") Integer dentistId,
      @Param("orgId") Integer orgId);

  /**
   * 根据条件查询预约未到患者信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<AppointmentUnDonePatientInfoVO> selectAppointmentUnDonePatientInfoList(
      @Param("queryForm") AppointmentCurrentListQuery queryForm);

  /**
   * 根据患者id查询患者所有预约列表
   * @param patientId 患者id
   * @return 患者列表
   */
  List<Appointment> findAppointmentByPatientId(@Param("patientId") Integer patientId);

  /**
   * 根据条件查询患者预约信息（患者档案-预约信息）用
   * @param query 查询条件
   * @return 患者列表
   */
  List<AppointPatientRecordVo> findAppointPatientRecord(AppointPatientRecordQuery query);

}
