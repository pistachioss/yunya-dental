package com.yunya.modules.appointment.biz;

import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.vo.EmployeeScheduleResultVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.Appointment;
import com.yunya.feign.appointment.domain.base.AppointmentBaseForm;
import com.yunya.modules.appointment.mapper.AppointmentMapper;
import com.yunya.modules.appointment.vo.AppointConflictInfoVo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 患者预约服务
 *
 * @author yunya-lihuibin
 * @create 2020-07-28 10:46
 * @update yunya-lihuibin    2020-07-28    新建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppointmentBiz extends BaseBiz<AppointmentMapper, Appointment> {

    /** 默认显示7天的预约信息 */
    private static final int APPOINT_DAYS = 7;

    /** 注入yunya-admin-system Feign接口服务 */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /** 注入员工排班服务 */
    @Autowired
    private EmployeeAttendServiceFeign employeeAttendServiceFeign;

    /** 注入病历模板服务 */
    //@Autowired
    //private EmrServiceFeign emrServiceFeign;

    /** 注入预约操作服务 */
    @Autowired
    private AppointmentOperateRecordBiz appointOperateRecordBiz;


    /**
     * 添加预约、检查预约是否冲突
     * @param form  预约参数封装
     * @throws ParseException
     */
    public Map<String,Object> addAppointment(AppointmentBaseForm form) throws ParseException {

        // 检查当前预约的医生是否排班
        Map<String, Object> checkSchedulingResult = this.checkScheduling(form);
        // 如果预约的医生没有排班，则返回警告信息结果
        if (StringHelper.isNull(checkSchedulingResult.get("data"))){
            return checkSchedulingResult;
        }
        // 检查当前预约是否冲突
        Map<String,Object> appointConflictResult = this.checkConflict(form);
        // 如果当前的预约没有冲突则添加新预约
        if (null == appointConflictResult){
            // 患者名字
            String patientame = form.getPatientName();
            // 将预约form转化为实体
            Appointment appointmentEntity = this.transferFormToEntity(form);
            // 插入预约
            Integer index = mapper.insertAppointment(appointmentEntity);
            if (index <= 0){
                throw new ClientServiceException("【"+patientame + "】预约失败！", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
            // 插入预约操作记录(添加)
            Integer appointmentOperateRecord = appointOperateRecordBiz.insertAppointmentOperateRecord(
                    appointmentEntity.getId(), appointmentEntity.getOrgId(), (byte) 0);
            if (appointmentOperateRecord <= 0 ){
                throw new ClientServiceException("【"+patientame+"】的预约操作记录添加失败！", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }

            Map<String,Object> responseResult = new HashMap<>();
            responseResult.put("appointment",appointmentEntity);

            // 如果添加预约成功，则返回预约成功信息
            return responseResult;
        }

        // 如果预约有冲突返回冲突的预约
        return appointConflictResult;
    }


    /**
     * 添加预约时，检查预约当日预约的医生和助手是否排班
     * @param appointmentBaseForm    预约参数封装表单
     * @return
     */
    private Map<String,Object> checkScheduling(AppointmentBaseForm appointmentBaseForm){
        // 获取预约医生Id
        String dentistId = appointmentBaseForm.getDentistId();
        Map<String,Object> responseResultMap = new HashMap<>();

        if (!StringHelper.isEmpty(dentistId)){
            EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();

            // 排班结束日期（排班当天的下一天）
            DateTime dateTime = new DateTime(appointmentBaseForm.getAppointDate());
            Date endDate = dateTime.plusDays(1).toDate();

            employeeScheduleQueryForm.setUserId(Integer.valueOf(dentistId));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr = sdf.format(appointmentBaseForm.getAppointDate());
            String endDateStr = sdf.format(endDate);
            employeeScheduleQueryForm.setStartDate(startDateStr);
            employeeScheduleQueryForm.setClinicId(Integer.valueOf(BaseContextHandler.getOrgId()));
            employeeScheduleQueryForm.setEndDate(endDateStr);
            // 获取排班列表
            EmployeeScheduleResultVO employeeScheduleResult = employeeAttendServiceFeign.findList(employeeScheduleQueryForm);
            // 预约医生没有排班，返回空
            if (employeeScheduleResult.getShiftWorkDatas().size() <= 0){
                responseResultMap.put("status",0);
                responseResultMap.put("errMwg","预约医生在预约日期当天未排班，建议排班后再新增预约！");
                responseResultMap.put("data",null);
                return responseResultMap;
            }
            // 成功返回排班信息
            responseResultMap.put("status",1);
            responseResultMap.put("errMwg",null);
            responseResultMap.put("data",employeeScheduleResult);
            return responseResultMap;
        }
        responseResultMap.put("status",0);
        responseResultMap.put("errMwg","预约医生id不能为空！");
        responseResultMap.put("data",null);
        return responseResultMap;
    }

    /**
     * 添加预约时检查是否存在预约冲突
     * @param appointmentForm
     * @throws ParseException
     */
    private Map<String,Object> checkConflict(AppointmentBaseForm appointmentForm) throws ParseException {
        // 获取患者id、医生id、设备id、预约日期、时间、时长
        String patientId = appointmentForm.getPatientId();
        String dentistId = appointmentForm.getDentistId();
        String deviceId = appointmentForm.getClinicDeviceItemId();
        Date appointDate = appointmentForm.getAppointDate();
        // 转换字符串预约时间为Date类型
        String appointTimeStr = appointmentForm.getAppointTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date appointTime = simpleDateFormat.parse(appointTimeStr);
        // 预约时长
        Integer time = appointmentForm.getAppointDuration();
        // 获取预约开始时间的毫秒值
        long ms = appointDate.getTime() + appointTime.getTime();
        // 转换预约开始时间（消除东八区时间的影响，加上28800000毫秒）
        DateTime startTime = new DateTime(ms + 28800000L);
        Date appointStartTime = startTime.toDate();
        // 计算预约结束时间(预约开始时间+预约时长)
        DateTime endTime = startTime.plusMinutes(time);
        Date appointEndTime = endTime.toDate();

        Map<String,Object> responseMapResult = new HashMap<>();

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
                responseMapResult.put("status",4);
                responseMapResult.put("errMwg","患者预约冲突！");
                responseMapResult.put("data",patientList);
                return responseMapResult;
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
                responseMapResult.put("status",5);
                responseMapResult.put("errMwg","医生预约冲突！");
                responseMapResult.put("data",dentisList);
                return responseMapResult;
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
                responseMapResult.put("status",6);
                responseMapResult.put("errMwg","设备预约冲突！");
                responseMapResult.put("data",deviceList);
                return responseMapResult;
            }
        }
        return null;
    }

    /**
     * 将form表单转化为实体对象
     * @param form  表单
     * @return  appointment
     */
    private Appointment transferFormToEntity(AppointmentBaseForm form) throws ParseException {
        // 将form表单转化为appointment实体
        Appointment appointment = EntityUtils.build(form, Appointment.class);
        // 设置患者Id
        appointment.setPatientId(Integer.valueOf(form.getPatientId()));
        // 获取预约日期、时间、时长
        Date appointDate = form.getAppointDate();
        String appointTimeStr = form.getAppointTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date appointTime = simpleDateFormat.parse(appointTimeStr);
        Integer time = form.getAppointDuration();
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
         *  患者病历model = emrServiceFeign.通过患者id查询患者病历(患者id);
         *  if(患者病历model != null ){
         *    // 病历号为空，初诊
         *    appointment.setAppointType(0);
         *  } else {
         *    // 病历号不为空，复诊
         *    appointment.setAppointType(1);
         *  }
         */
        // 设置预约种类Mock数据
        appointment.setAppointType((byte)0);

        // 设置预约状态 0-预约未到，1-履约，2，取消预约，3-失约
        appointment.setAppointStatus((byte)0);
        // 设置诊所id
        appointment.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
        // 设置操作人id
         appointment.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));

        return appointment;
    }

}
