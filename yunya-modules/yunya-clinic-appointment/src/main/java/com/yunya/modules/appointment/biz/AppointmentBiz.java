package com.yunya.modules.appointment.biz;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.yunya.feign.appointment.domain.base.AppointmentSplitBaseInfo;
import com.yunya.feign.appointment.domain.base.AppointmentSplitUpdateBaseInfo;
import com.yunya.feign.appointment.domain.form.AppointmentBaseForm;
import com.yunya.feign.appointment.domain.form.AppointmentCancelCauseForm;
import com.yunya.feign.appointment.domain.form.AppointmentSplitForm;
import com.yunya.feign.appointment.domain.model.AppointOperationModel;
import com.yunya.feign.appointment.domain.model.AppointmentSplitModel;
import com.yunya.feign.appointment.domain.query.AppointmentQuery;
import com.yunya.feign.appointment.vo.AppointmentVo;
import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.vo.EmployeeScheduleResultVO;
import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.Appointment;
import com.yunya.feign.appointment.domain.model.AppointmentBaseModel;
import com.yunya.models.appointment.AppointmentModifyRecord;
import com.yunya.models.appointment.AppointmentOperateRecord;
import com.yunya.models.auth.Client;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.modules.appointment.mapper.AppointmentMapper;
import com.yunya.feign.appointment.vo.AppointConflictInfoVo;
import io.swagger.models.auth.In;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zipkin2.Call;

import javax.validation.constraints.NotEmpty;
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

    /** 患者中心服务 */
    @Autowired
    private PatientCentralServiceFeign patientCentralServiceFeign;

    /** 预约操作记录服务 */
    @Autowired
    private AppointmentOperateRecordBiz appointOperateRecordBiz;

    /** 时长分解服务 */
    @Autowired
    private AppointmentSplitBiz appointmentSplitBiz;

    /** 预约修改服务 */
    @Autowired
    private AppointmentModifyRecordBiz appointmentModifyRecordBiz;


    /**
     * 添加预约（检查预约是否冲突）
     * @param form  预约参数封装
     * @throws ParseException
     */
    public ResponseResult addAppointment(AppointmentBaseModel form) throws ParseException {
        // 检查预约当天预约的医生是否排班
        Map<String, Object> distentSchedulingConflict = this.checkScheduling(form);
        if (distentSchedulingConflict.get("errMwg") != null){
            return ResponseUtil.success(distentSchedulingConflict);
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
                throw new ClientServiceException("【"+patientame + "】预约失败！",OperationCodeConstants.OBJECT_EDIT_FAIL);
            }

            // 添加预约时长分解
            if (form.getSplitList() != null || !form.getSplitList().isEmpty()){
                AppointmentSplitModel model = new AppointmentSplitModel();
                model.setSplitList(form.getSplitList());
                model.setOrgId(appointmentEntity.getOrgId());
                model.setAppointmentId(appointmentEntity.getId());
                model.setAppointDuration(appointmentEntity.getAppointDuration());
                Integer splitResult = appointmentSplitBiz.insertAppointSplit(model);
                if (splitResult == null || splitResult <= 0){
                    throw new ClientServiceException((String) "分解时长失败！",OperationCodeConstants.OBJECT_EDIT_FAIL);
                }
            }

            // 插入预约操作记录(添加)
            AppointOperationModel operationModel = new AppointOperationModel();
            operationModel.setAppointmentId(appointmentEntity.getId());
            operationModel.setOperateType((byte) 0);
            Integer appointmentOperateRecord = appointOperateRecordBiz.insertAppointmentOperateRecord(operationModel);
            if (appointmentOperateRecord <= 0 ){
                throw new ClientServiceException("【"+patientame+"】的预约操作记录添加失败！",OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
            // 如果添加预约成功，则返回预约成功信息
            return ResponseUtil.success();
        }

        // 如果预约有冲突返回冲突的预约
        return ResponseUtil.success(appointConflictResult);
    }

    /**
     *  添加预约（冲突后继续添加）
     * @param form  预约参数封装
     * @return
     */
    public ResponseResult continueAddAppointment(AppointmentBaseModel form) {
        // 将Form转为Entity
        Appointment build = transferFormToEntity(form);
        int result = mapper.insertAppointment(build);
        if (result > 0) {
            // 添加预约时长分解
            List<AppointmentSplitBaseInfo> splitList = form.getSplitList();
            if (splitList != null && !splitList.isEmpty()){
                AppointmentSplitModel splitModel = new AppointmentSplitModel();
                splitModel.setSplitList(form.getSplitList());
                splitModel.setAppointDuration(build.getAppointDuration());
                splitModel.setAppointmentId(build.getId());
                splitModel.setOrgId(build.getOrgId());
                Integer splitResult = appointmentSplitBiz.insertAppointSplit(splitModel);
                if (splitResult == null || splitResult <= 0){
                    throw new ClientServiceException((String) "分解时长失败！",OperationCodeConstants.OBJECT_EDIT_FAIL);
                }
            }
            // 判断预约是否添加成功
            AppointmentOperateRecord record = new AppointmentOperateRecord();
            record.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
            record.setAppointmentId(build.getId());
            // 操作类型 操作记录(0-新建预约；1-修改预约；2-取消预约；3-确认预约；4；取消确认)
            record.setOperateType((byte) 0);
            record.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
            record.setCrtName(BaseContextHandler.getName());
            record.setCrtTime(new Date(System.currentTimeMillis()));
            // 生成新增预约操作记录
            appointOperateRecordBiz.insertSelective(record);
        }
        return ResponseUtil.success();
    }

    /**
     * 修改预约状态
     * @param id 预约id
     * @param appointState 预约状态 0-预约未到，1-履约，2，取消预约，3-失约
     * @return
     */
    public Appointment updateAppointStatus(Integer id, Byte appointState, String remarks) {
        Appointment appointment = mapper.selectByPrimaryKey(id);
        if (appointment == null){
            throw new ClientServiceException("预约不存在！",OperationCodeConstants.DATA_NOT_EXIST);
        }
        appointment.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        appointment.setUpdName(BaseContextHandler.getName());
        appointment.setUpdTime(new Date(System.currentTimeMillis()));
        // 预约操作记录
        AppointmentOperateRecord record = new AppointmentOperateRecord();
        record.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        record.setCrtName(BaseContextHandler.getName());
        record.setCrtTime(new Date(System.currentTimeMillis()));
        // 履约
        if (appointState == 1){
            appointment.setAppointStatus((byte) 1);
            mapper.updateByPrimaryKeySelective(appointment);
            // 操作记录
            AppointOperationModel model = new AppointOperationModel();
            model.setAppointmentId(id);
            model.setOperateType((byte) 3);
            model.setRemarks(remarks);
            appointOperateRecordBiz.insertAppointmentOperateRecord(model);
        } else if (appointState == 2){
            // 取消预约
            appointment.setAppointStatus((byte) 2);
            appointment.setInservice(false);
            mapper.updateByPrimaryKeySelective(appointment);
            // 操作记录
            AppointOperationModel model = new AppointOperationModel();
            model.setAppointmentId(id);
            model.setOperateType((byte) 2);
            model.setRemarks(remarks);
            appointOperateRecordBiz.insertAppointmentOperateRecord(model);
        } else if (appointState == 3){
            // 失约
            appointment.setAppointStatus((byte) 3);
            appointment.setInservice(false);
            mapper.updateByPrimaryKeySelective(appointment);
            // 操作记录
            AppointOperationModel model = new AppointOperationModel();
            model.setAppointmentId(id);
            model.setOperateType((byte) 2);
            model.setRemarks(remarks);
            appointOperateRecordBiz.insertAppointmentOperateRecord(model);
        }
        return appointment;
    }


    /**
     * 删除预约（取消预约）
     * @param id   预约id
     * @param form  取消预约原因表单
     * @return
     */
    public ResponseResult appointmentCancel(Integer id, AppointmentCancelCauseForm form){
        Appointment appointment = mapper.selectByPrimaryKey(id);
        if (appointment == null){
            throw new ClientServiceException("预约数据不存在！",OperationCodeConstants.DATA_NOT_EXIST);
        }
        appointment.setInservice(false);
        appointment.setRemarks(form.getCause());
        appointment.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        appointment.setUpdName(BaseContextHandler.getName());
        appointment.setUpdTime(new Date(System.currentTimeMillis()));
        int result = mapper.updateByPrimaryKeySelective(appointment);
        if (result <= 0){
            throw new ClientServiceException("取消预约失败！",OperationCodeConstants.OBJECT_EDIT_FAIL);
        }

        AppointOperationModel record = new AppointOperationModel();
        record.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
        record.setAppointmentId(appointment.getId());
        record.setOperateType((byte) 2);
        record.setRemarks(form.getCause());
        appointOperateRecordBiz.insertAppointmentOperateRecord(record);
        return ResponseUtil.success();
    }

    /**
     * 编辑预约（出现冲突）
     * @param form  预约表单
     * @return
     */
    public ResponseResult updateAppointment(AppointmentBaseForm form){
        AppointmentBaseModel appointBaseModel = EntityUtils.build(form, AppointmentBaseModel.class);
        // 检查预约当天预约的医生是否排班
        Map<String, Object> distentSchedulingConflict = this.checkScheduling(appointBaseModel);
        if (distentSchedulingConflict.get("errMwg") != null){
            return ResponseUtil.success(distentSchedulingConflict);
        }
        // 检查预约冲突（只检查医生预约冲突、设备预约冲突）
        Map<String, Object> objectMap = editCheckConflict(form.getId(), form);
        if (null == objectMap) {
            appointmentModifyRecordBiz.saveAppointModify(mapper.selectByPrimaryKey(form.getId()),form);
            // 转换预约内容
            Appointment appointment = transferFormToEntity(appointBaseModel);
            appointment.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
            appointment.setUpdName(BaseContextHandler.getName());
            appointment.setUpdTime(new Date(System.currentTimeMillis()));
            appointment.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
            List<Appointment> hasAppoints = mapper.select(appointment);
            if (!StringHelper.isEmpty(hasAppoints)){
                throw new ClientServiceException("已经存在相同的预约！",OperationCodeConstants.SAME_DATA_EXIST);
            }

            int num = mapper.updateByPrimaryKeySelective(appointment);
            if (num <= 0){
                throw new ClientServiceException("编辑预约失败！", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }

            // 修改时长分解
            List<AppointmentSplitUpdateBaseInfo> splitList = form.getSplitList();
            if (splitList != null && !splitList.isEmpty()){
                AppointmentSplitForm splitForm = new AppointmentSplitForm();
                splitForm.setSplitList(splitList);
                splitForm.setOrgId(appointment.getOrgId());
                splitForm.setAppointDuration(appointment.getAppointDuration());
                splitForm.setAppointmentId(appointment.getId());
                Integer splitResult = appointmentSplitBiz.updateAppointSplit(splitForm);
                if (splitResult == null || splitResult <= 0){
                    throw new ClientServiceException("时长分解失败！",OperationCodeConstants.OBJECT_EDIT_FAIL);
                }
            }

            // 生成修改预约操作记录
            appointOperateRecordBiz.saveAppointOperationRecord(appointment,form);
            return ResponseUtil.success();
        }
        // 返回冲突数据
        return ResponseUtil.success(objectMap);
    }


    /**
     * 编辑预约（有冲突继续保存）
     * @param appointmentForm 更新预约信息form
     */
    public ResponseResult continueUpdateAppointment(AppointmentBaseForm appointmentForm) {
        Appointment appointEntity = transferFormToEntity(appointmentForm);
        appointEntity.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        appointEntity.setUpdName(BaseContextHandler.getName());
        appointEntity.setUpdTime(new Date(System.currentTimeMillis()));
        int num = mapper.updateByPrimaryKeySelective(appointEntity);
        if (num <= 0){
            throw new ClientServiceException("编辑预约失败！",OperationCodeConstants.OBJECT_EDIT_FAIL);
        }

        // 保存预约更新被修改的日期、医生
        appointmentModifyRecordBiz.saveAppointModify(mapper.selectByPrimaryKey(appointmentForm.getId()),appointmentForm);

        // 修改时长分解
        List<AppointmentSplitUpdateBaseInfo> splitList = appointmentForm.getSplitList();
        if (splitList != null && !splitList.isEmpty()){

            AppointmentSplitForm splitForm = new AppointmentSplitForm();
            splitForm.setSplitList(appointmentForm.getSplitList());
            splitForm.setOrgId(appointmentForm.getOrgId());
            splitForm.setAppointDuration(appointmentForm.getAppointDuration());
            splitForm.setAppointmentId(appointmentForm.getId());
            Integer splitResult = appointmentSplitBiz.updateAppointSplit(splitForm);
            if (splitResult == null || splitResult <= 0){
                throw new ClientServiceException("时长分解失败！",OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        }

        // 生成修改预约操作记录
        appointOperateRecordBiz.saveAppointOperationRecord(appointEntity,appointmentForm);
        return ResponseUtil.success();
    }

//    TODO  查询预约（根据预约id）

//    TODO  查询预约（根据条件）


    /**
     * 根据id查询预约
     * @param id 预约id
     * @return
     */
    public AppointmentVo findAppointmentById(Integer id){
        AppointmentVo appointmentVo = mapper.findAppointmentById(id);
        return appointmentVo;
    }



    /**
     * 确认预约
     * @param id  预约id
     * @return
     */
    public Integer confirmAppointment(Integer id){
        Appointment appointment = mapper.selectByPrimaryKey(id);
        AppointOperationModel appointOperationModel = new AppointOperationModel();
        if (appointment == null){
            throw new ClientServiceException("预约数据不存在！",OperationCodeConstants.DATA_NOT_EXIST);
        }
        appointOperationModel.setBeforeOperation(appointment.getConfirmStatus()?"确认":"未确认");
        appointment.setConfirmStatus(true);
        appointment.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        appointment.setUpdName(BaseContextHandler.getName());
        appointment.setUpdTime(new Date(System.currentTimeMillis()));
        int result = mapper.updateByPrimaryKeySelective(appointment);
        if (result > 0) {
            appointOperationModel.setOperateType((byte) 3);
            appointOperationModel.setAppointmentId(appointment.getId());
            appointOperationModel.setOrgId(Integer.valueOf(BaseContextHandler.getUserID()));
            appointOperationModel.setAfterOperation(appointment.getConfirmStatus()?"确认":"未确认");
            appointOperationModel.setRemarks("确认预约");
            return appointOperateRecordBiz.insertAppointmentOperateRecord(appointOperationModel);
        }
        return result;
    }


    /**
     * 根据条件查询预约列表
     * @param query  条件查询参数
     * @return
     */
    public List<AppointmentVo> findAppointmentByExample(AppointmentQuery query){
        return mapper.findAppointmentByExample(query);
    }


    /**
     * 根据日期查询失约患者名单
     * @param currentDate  当前日期
     * @return
     */
    public List<Appointment> findMissedAppointmentByDate(Date currentDate){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(currentDate);
        List<Appointment> missedAppointmentByDate = mapper.findMissedAppointmentByDate(currentDate);
        return missedAppointmentByDate;
    }


    /**
     * 添加预约时，检查预约当日预约的医生和助手是否排班
     * @param appointmentBaseModel    预约参数封装表单
     * @return
     */
    private Map<String,Object> checkScheduling(AppointmentBaseModel appointmentBaseModel){
        // 获取预约医生Id
        Integer dentistId = appointmentBaseModel.getDentistId();
        Map<String,Object> responseResultMap = new HashMap<>();

        if (dentistId != null){
            EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();

            // 排班结束日期（排班当天的下一天）
            DateTime dateTime = new DateTime(appointmentBaseModel.getAppointDate());
            Date endDate = dateTime.plusDays(1).toDate();

            employeeScheduleQueryForm.setUserId(Integer.valueOf(dentistId));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr = sdf.format(appointmentBaseModel.getAppointDate());
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
    private Map<String,Object> checkConflict(AppointmentBaseModel appointmentForm) throws ParseException {
        // 获取患者id、医生id、设备id、预约日期、时间、时长
        Integer patientId = appointmentForm.getPatientId();
        Integer dentistId = appointmentForm.getDentistId();
        Integer deviceId = appointmentForm.getClinicDeviceItemId();
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
        if (patientId != null) {
            List<AppointConflictInfoVo> patientList = mapper.findAppointListByPatientIdAndAppointStartTimeAndAppointEndTime(
                    patientId, appointStartTime, appointEndTime);
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
        if (dentistId != null){
            List<AppointConflictInfoVo> dentisList = mapper.findAppointListByDentistIdAndAppointStartTimeAndAppointEndTime(
                    dentistId, appointStartTime, appointEndTime);
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
        if (deviceId != null){
            List<AppointConflictInfoVo> deviceList = mapper.findAppointListByDeviceIdAndAppointStartTimeAndAppointEndTime(
                    deviceId, appointStartTime, appointEndTime);
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
    private Appointment transferFormToEntity(Object form) {
        if (!(form instanceof AppointmentBaseModel) && !(form instanceof AppointmentBaseForm)){
            throw new ClientServiceException("对象转换实体异常！",OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
        }
        // 将form表单转化为appointment实体
        Appointment appointment = EntityUtils.build(form, Appointment.class);
        // 获取预约日期、时间、时长
        Date appointDate = appointment.getAppointDate();
        String appointTimeStr = appointment.getAppointTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date appointTime = null;
        try {
            appointTime = simpleDateFormat.parse(appointTimeStr);
        } catch (ParseException e) {
            throw new ClientServiceException("[时间格式转换异常]："+e.getMessage(),OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
        }
        Integer time = appointment.getAppointDuration();
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
        PatientBaseInfo patientBaseInfo = patientCentralServiceFeign.findPatientInfoById(appointment.getPatientId());
        if (patientBaseInfo == null){
            // 病历号为空，初诊
            appointment.setAppointType((byte)0);
        } else {
            // 病历号不为空，复诊
            appointment.setAppointType((byte)1);
        }

        // 设置预约状态 0-预约未到，1-履约，2，取消预约，3-失约
        appointment.setAppointStatus((byte)0);
        appointment.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
        appointment.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        appointment.setCrtName(BaseContextHandler.getName());
        appointment.setCrtTime(new Date(System.currentTimeMillis()));

        return appointment;
    }

    /**
     * 编辑预约检查预约冲突（排除自身）
     *
     * @param id 预约id
     * @param appointmentForm 预约form
     * @return
     * @throws ParseException
     */
    private Map<String, Object> editCheckConflict(Integer id,  AppointmentBaseForm appointmentForm) {
        // 获取患者id、医生id、设备id、预约日期、时间、时长
        Integer patientId = appointmentForm.getPatientId();
        Integer dentistId = appointmentForm.getDentistId();
        Integer deviceId = appointmentForm.getClinicDeviceItemId();
        Date appointDate = appointmentForm.getAppointDate();
        // 转换字符串预约时间为Date类型
        String appointTimeStr = appointmentForm.getAppointTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date appointTime = null;
        try {
            appointTime = simpleDateFormat.parse(appointTimeStr);
        } catch (ParseException e) {
            throw new ClientServiceException("时间格式转换错误！",OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
        }
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

        if (patientId != null) {
            // 判断患者预约是否存在冲突
            List<AppointConflictInfoVo> appointConflictInfoVos =
                    mapper.editCheckPatientConflict(id, appointmentForm.getPatientId(), appointStartTime, appointEndTime);
            if (!appointConflictInfoVos.isEmpty()) {
                // 存在患者预约冲突
                responseMapResult.put("status",4);
                responseMapResult.put("errMsg","患者预约冲突！");
                responseMapResult.put("data",appointConflictInfoVos);
                return responseMapResult;
            }
        }
        // 判断医生预约是否存在冲突
        if (dentistId != null) {
            List<AppointConflictInfoVo> appointConflictInfoVos =
                    mapper.editCheckDentistConflict(id, appointmentForm.getPatientId(), appointStartTime, appointEndTime);
            if (!appointConflictInfoVos.isEmpty()) {
                // 存在医生预约冲突
                responseMapResult.put("status",5);
                responseMapResult.put("errMsg","医生预约冲突！");
                responseMapResult.put("data",appointConflictInfoVos);
                return responseMapResult;
            }
        }
        // 判断设备预约是否存在冲突
        if (deviceId != null) {
            List<AppointConflictInfoVo> appointConflictInfoVos =
                    mapper.editCheckDeviceConflict(id, deviceId, appointStartTime, appointEndTime);
            if (!appointConflictInfoVos.isEmpty()) {
                // 存在设备预约冲突
                responseMapResult.put("status",6);
                responseMapResult.put("errMsg","设备预约冲突！");
                responseMapResult.put("data",appointConflictInfoVos);
                return responseMapResult;
            }
        }
        return null;
    }




}
