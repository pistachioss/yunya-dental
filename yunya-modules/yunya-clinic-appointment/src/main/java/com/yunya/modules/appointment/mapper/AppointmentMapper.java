package com.yunya.modules.appointment.mapper;

import com.yunya.models.appointment.Appointment;
import com.yunya.modules.appointment.vo.AppointConflictInfoVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface AppointmentMapper extends Mapper<Appointment> {

    /**
     * 根据设备id，预约开始时间，预约结束时间查询所有预约列表
     *
     * @param deviceId         设备id
     * @param appointStartTime 预约开始时间
     * @param appointEndTime   预约结束时间
     * @return 所有符合条件的预约列表
     */
    List<AppointConflictInfoVo> findAppointListByDeviceIdAndAppointStartTimeAndAppointEndTime(
            @Param("deviceId") Integer deviceId,
            @Param("appointStartTime") Date appointStartTime,
            @Param("appointEndTime") Date appointEndTime);

    /**
     * 根据患者id，预约开始时间，预约结束时间查询所有预约列表
     *
     * @param patientId        患者Id
     * @param appointStartTime 预约开始时间
     * @param appointEndTime   预约结束时间
     * @return 所有符合条件的预约列表
     */
    List<AppointConflictInfoVo> findAppointListByPatientIdAndAppointStartTimeAndAppointEndTime(
            @Param("patientId") Integer patientId,
            @Param("appointStartTime") Date appointStartTime,
            @Param("appointEndTime") Date appointEndTime);


    /**
     * 根据医生id，预约开始时间，预约结束时间查询所有预约列表
     * @param dentistId    医生id
     * @param appointStartTime  预约开始时间
     * @param appointEndTime    预约结束时间
     * @return    符合条件的所有预约列表
     */
    List<AppointConflictInfoVo> findAppointListByDentistIdAndAppointStartTimeAndAppointEndTime(
            @Param("dentistId") Integer dentistId,
            @Param("appointStartTime") Date appointStartTime,
            @Param("appointEndTime") Date appointEndTime);


    /**
     * 新增患者预约
     * @param appointment
     * @return
     */
    Integer insertAppointment(Appointment appointment);

}