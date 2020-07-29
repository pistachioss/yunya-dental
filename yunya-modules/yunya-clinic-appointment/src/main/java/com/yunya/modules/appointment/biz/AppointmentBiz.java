package com.yunya.modules.appointment.biz;

import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.vo.EmployeeScheduleResultVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.Appointment;
import com.yunya.modules.appointment.form.AppointmentBaseForm;
import com.yunya.modules.appointment.mapper.AppointmentMapper;
import com.yunya.modules.appointment.vo.AppointConflictInfoVo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 患者预约服务
 *
 * @author yunya-lihuibin
 * @create 2020-07-28 10:46
 * @update yunya-lihuibin    2020-07-28    新建
 */
public class AppointmentBiz extends BaseBiz<AppointmentMapper, Appointment> {

    /** 默认显示7天的预约信息 */
    private static final int APPOINT_DAYS = 7;

    /** 注入yunya-admin-system Feign接口服务 */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /** 注入员工排班服务 */
    @Autowired
    private EmployeeAttendServiceFeign employeeAttendServiceFeign;


    /**
     * 添加预约、检查预约是否冲突
     * @param form  预约参数封装
     * @throws ParseException
     */
    public ResponseResult addAppointment(AppointmentBaseForm form) throws ParseException {

        // 检查当前预约的医生是否排班
        ResponseResult dentistScheduleResult = this.checkScheduling(form);
        // 如果预约的医生没有排班，则返回结果
        if (dentistScheduleResult.getStatus().equals(0)){
            return dentistScheduleResult;
        }
        // 检查当前预约是否冲突
        ResponseResult appointConflictResult = this.checkConflict(form);
        // 如果当前的预约没有冲突则添加新预约
        if (null == appointConflictResult){
            // TODO

            // 如果添加预约成功，则返回预约信息
            return ResponseUtil.success();
        }

        // 如果预约有冲突返回冲突的预约
        return appointConflictResult;
    }


    /**
     * 添加预约时，检查预约当日预约的医生和助手是否排班
     * @param appointmentBaseForm    预约参数封装表单
     * @return
     */
    public ResponseResult checkScheduling(AppointmentBaseForm appointmentBaseForm){
        // 获取预约医生Id
        String dentistId = appointmentBaseForm.getDentistId();

        if (!StringHelper.isEmpty(dentistId)){
            EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();

            // 排班结束日期（排班当天的下一天）
            DateTime dateTime = new DateTime(appointmentBaseForm.getAppointDate());
            Date endDate = dateTime.plusDays(1).toDate();

            employeeScheduleQueryForm.setUserId(Integer.valueOf(dentistId));
            employeeScheduleQueryForm.setStartDate(appointmentBaseForm.getAppointDate());
            employeeScheduleQueryForm.setEndDate(endDate);
            // 获取排班列表
            EmployeeScheduleResultVO employeeScheduleResult = employeeAttendServiceFeign.findList(employeeScheduleQueryForm);
            if (employeeScheduleResult.getShiftWorkDatas().size() <= 0){
                return ResponseUtil.fail(0,"预约医生在预约日期当天未排班，建议排班后再新增预约！", employeeScheduleResult);
            }
            // 成功返回排班信息
            return ResponseUtil.success(employeeScheduleResult);
        }
        return ResponseUtil.fail(0,"预约医生id不能为空！",null);
    }

    /**
     * 添加预约时检查是否存在预约冲突
     * @param appointmentForm
     * @throws ParseException
     */
    public ResponseResult checkConflict(AppointmentBaseForm appointmentForm) throws ParseException {
        // 获取患者id、医生id、设备id、预约日期、时间、时长
        String patientId = appointmentForm.getPatientId();
        String dentistId = appointmentForm.getDentistId();
        String deviceId = appointmentForm.getDeviceId();
        Date appointDate = appointmentForm.getAppointDate();
        // 转换字符串预约时间为Date类型
        String appointTimeStr = appointmentForm.getAppointTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date appointTime = simpleDateFormat.parse(appointTimeStr);
        // 预约时长
        Integer time = appointmentForm.getTime();
        // 获取预约开始时间的毫秒值
        long ms = appointDate.getTime() + appointTime.getTime();
        // 转换预约开始时间（消除东八区时间的影响，加上28800000毫秒）
        DateTime startTime = new DateTime(ms + 28800000L);
        Date appointStartTime = startTime.toDate();
        // 计算预约结束时间(预约开始时间+预约时长)
        DateTime endTime = startTime.plusMinutes(time);
        Date appointEndTime = endTime.toDate();

        // 判断患者预约是否存在冲突
        if (!StringHelper.isEmpty(patientId)) {
            List<AppointConflictInfoVo> patientList = mapper.findAppointListByPatientIdAndAppointStartTimeAndAppointEndTime(
                    Integer.valueOf(patientId), appointStartTime, appointEndTime);
            if (!StringHelper.isEmpty(patientList)){
                patientList.forEach(appointConflictInfoVo -> {
                    OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(appointConflictInfoVo.getOrgId());
                    appointConflictInfoVo.setClinicName(organizationInfo.getName());
                    appointConflictInfoVo.setClinicNumber(organizationInfo.getClinicNumber());
                    appointConflictInfoVo.setAbbreviation(organizationInfo.getAbbreviation());
                });
                // 预约冲突返回冲突信息
                return ResponseUtil.fail(4,"患者预约冲突！",patientList);
            }
        }

        // 判断医生预约是否存在冲突
        if (!StringHelper.isEmpty(dentistId)){
            List<AppointConflictInfoVo> dentisList = mapper.findAppointListByDentistIdAndAppointStartTimeAndAppointEndTime(
                    Integer.valueOf(dentistId), appointStartTime, appointEndTime);
            if (!StringHelper.isEmpty(dentisList)){
                dentisList.forEach(appointConflictInfoVo -> {
                    OrganizationInfo organizatioinInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(appointConflictInfoVo.getOrgId());
                    appointConflictInfoVo.setClinicName(organizatioinInfo.getName());
                    appointConflictInfoVo.setClinicNumber(organizatioinInfo.getClinicNumber());
                    appointConflictInfoVo.setAbbreviation(organizatioinInfo.getAbbreviation());
                });
                // 预约冲突返回冲突信息
                return ResponseUtil.fail(5,"医生预约冲突！",dentisList);
            }
        }

        // 判断设备预约是否存在冲突
        if (!StringHelper.isEmpty(deviceId)){
            List<AppointConflictInfoVo> deviceList = mapper.findAppointListByDeviceIdAndAppointStartTimeAndAppointEndTime(
                    Integer.valueOf(deviceId), appointStartTime, appointEndTime);
            if (!StringHelper.isEmpty(deviceList)){
                deviceList.forEach(appointConflictInfoVo -> {
                    OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(appointConflictInfoVo.getOrgId());
                    appointConflictInfoVo.setClinicName(organizationInfo.getName());
                    appointConflictInfoVo.setAbbreviation(organizationInfo.getAbbreviation());
                    appointConflictInfoVo.setClinicNumber(organizationInfo.getClinicNumber());
                });
                // 预约冲突返回冲突信息
                return ResponseUtil.fail(6,"设备预约冲突！", deviceList);
            }
        }
        return null;
    }

    /**
     * 将form表单转化为实体对象
     * @param form  表单
     * @return  appointment
     */
    public Appointment transferFormToEntity(AppointmentBaseForm form) throws ParseException {
        // 将form表单转化为appointment实体
        Appointment appointment = EntityUtils.build(form, Appointment.class);
        // 设置患者Id
        appointment.setPatientId(Integer.valueOf(form.getPatientId()));
        // 获取预约日期、时间、时长
        Date appointDate = form.getAppointDate();
        String appointTimeStr = form.getAppointTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date appointTime = simpleDateFormat.parse(appointTimeStr);
        Integer time = form.getTime();
        // 获取预约开始时间的毫秒
        long ms = appointDate.getTime() + appointTime.getTime();
        // 转换预约开始时间
        DateTime startTime = new DateTime(ms + 28800000L);
        Date appointStartTime = startTime.toDate();
        // 计算预约结束时间
        DateTime endTime = startTime.plusMinutes(time);
        Date appointEndTime = endTime.toDate();

        // 设置预约开始时间
        appointment.setAppointStartTime(appointStartTime);
        // 设置预约结束时间
        appointment.setAppointEndTime(appointEndTime);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String start = dateFormat.format(appointStartTime);
        String end = dateFormat.format(appointEndTime);
        // 设置预约时间段
        appointment.setAppointPeriod(start + "-" + end);

        // 设置预约类型(0-初诊；1-复诊)
        // 根据患者是否有病历号来判断患者预约类型
        // TODO
        /**
         *  患者病历model = 患者病历Feign.通过患者id查询患者病历(患者id);
         *  if(患者病历model != null ){
         *    // 病历号为空，初诊
         *    appointment.setAppointType(0);
         *  } else {
         *    // 病历号不为空，复诊
         *    appointment.setAppointType(1);
         *  }
         */
        // 设置预约取消初始状态为0-未取消
        appointment.setAppointStatus(0);


        return null;
    }


}
