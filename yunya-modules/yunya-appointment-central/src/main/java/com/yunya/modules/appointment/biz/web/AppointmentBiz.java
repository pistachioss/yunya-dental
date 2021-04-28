package com.yunya.modules.appointment.biz.web;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.base.AppointmentSplitBaseInfo;
import com.yunya.feign.appointment.domain.form.AppointmentBaseForm;
import com.yunya.feign.appointment.domain.form.AppointmentSplitForm;
import com.yunya.feign.appointment.domain.model.AppointOperationModel;
import com.yunya.feign.appointment.domain.model.AppointmentBaseModel;
import com.yunya.feign.appointment.domain.model.AppointmentSplitModel;
import com.yunya.feign.appointment.domain.query.*;
import com.yunya.feign.appointment.vo.*;
import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.form.FieldInfoForm;
import com.yunya.feign.employee_attend.form.LeaveInfoForm;
import com.yunya.feign.employee_attend.form.WorkOvertimeInfoForm;
import com.yunya.feign.employee_attend.vo.*;
import com.yunya.feign.expand.RemoteClinicEmployeeConfigFeign;
import com.yunya.feign.expand.model.response.EnableChooseEmployeeRes;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.sms.RemoteSmsServiceFeign;
import com.yunya.feign.sms.model.AppointmentSmsSendRecordModel;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.MedicalOrganizationInfoVO;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.model.DebtAmountModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.SmsTemplateItemEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.appointment.AppointmentOperateRecord;
import com.yunya.models.employee_attend.WorkOvertimeInfo;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.DepartmentRoom;
import com.yunya.models.system.MemberType;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.AppointmentMapper;
import com.yunya.modules.appointment.util.pageUtil.PageUtil;
import com.yunya.modules.appointment.util.pageUtil.model.Page;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.core.Local;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.util.CellRangeAddress;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseTreatmentProcess;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 患者预约服务
 *
 * @author yunya-lihuibin
 * @create 2020-07-28 10:46
 * @update yunya-lihuibin    2020-07-28    新建
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppointmentBiz extends BaseBiz<AppointmentMapper, Appointment> {

    /** 消息中间件调用 */
    @Autowired
    private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;

    /** 注入yunya-admin-system Feign接口服务 */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /** 注入员工排班服务 */
    @Autowired
    private EmployeeAttendServiceFeign employeeAttendServiceFeign;

    /** 患者中心服务 */
    @Autowired
    private RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;

    /** 预约操作记录服务 */
    @Autowired
    private AppointmentOperateRecordBiz appointOperateRecordBiz;

    /** 时长分解服务 */
    @Autowired
    private AppointmentSplitBiz appointmentSplitBiz;

    /** 预约修改服务 */
    @Autowired
    private AppointmentModifyRecordBiz appointmentModifyRecordBiz;

    /** 患者就诊服务 */
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;

    /** 门诊设备服务 */
    @Autowired
    private ClinicDeviceItemBiz clinicDeviceItemBiz;

    /** 门诊员工设置服务 */
    @Autowired
    private RemoteClinicEmployeeConfigFeign clinicEmployeeConfigFeign;

    /** 短信服务调用 */
    @Autowired
    private RemoteSmsServiceFeign remoteSmsServiceFeign;

    /** 注入redis缓冲服务 */
    @Autowired
    private RedisUtils redisUtils;

    @Resource(name = "poolExecutor")
    private ExecutorService poolExecutor;
    /**
     * 添加预约（检查预约是否冲突）
     * @param form  预约参数封装
     * @return ResponseResult
     * @throws ParseException 日期转换异常
     */
    public ResponseResult<T> addAppointment(AppointmentBaseModel form) throws ParseException {
        // 检查预约当天预约的医生是否排班
        ResponseResult dentistSchedulingConflict = this.checkScheduling(form);
        if (null != dentistSchedulingConflict){
            return dentistSchedulingConflict;
        }
        // 检测预约分解参数是否正常
        List<AppointmentSplitBaseInfo> splits = this.checkAppointSplitField(form.getSplitList());
        if (!StringHelper.isEmpty(splits)) {
            return ResponseUtil.fail(AppointmentError.APPOINT_SPLIT_PARAM_ERR.getCode(),
                    AppointmentError.APPOINT_SPLIT_PARAM_ERR.getMessage(),null);
        }
        // 检查当前预约是否冲突
        ResponseResult appointConflictResult = this.checkConflict(form);
        // 如果当前的预约没有冲突则添加新预约
        if (null == appointConflictResult){
            // 将预约form转化为实体
            Appointment appointmentEntity = this.transferFormToEntity(form);
            // 插入预约
            Integer index = mapper.insertAppointment(appointmentEntity);
            if (index <= 0){
                return ResponseUtil.fail(AppointmentError.APPOINTMENT_FAIL.getCode(),AppointmentError.APPOINTMENT_FAIL.getMessage(),null);
            }
            rabbitMqServiceFeign.sendMessage(appointmentEntity.getId(),0,0, BaseTreatmentProcess);

            List<AppointmentSplitBaseInfo> splitList = form.getSplitList();
            // 添加预约时长分解
            if (form.getSplitList() != null && !splitList.isEmpty()){
                AppointmentSplitModel model = new AppointmentSplitModel();
                model.setSplitList(form.getSplitList());
                model.setOrgId(appointmentEntity.getOrgId());
                model.setAppointmentId(appointmentEntity.getId());
                model.setAppointDuration(appointmentEntity.getAppointDuration());
                model.setAppointDate(appointmentEntity.getAppointDate());
                Integer splitResult = appointmentSplitBiz.insertAppointSplit(model);
                if (splitResult == null || splitResult <= 0){
                    return ResponseUtil.fail(AppointmentError.APPOINTMENT_SPLIT_FAIL.getCode(), AppointmentError.APPOINTMENT_SPLIT_FAIL.getMessage(),null);
                }
            }
            // 插入预约操作记录(添加)
            AppointOperationModel operationModel = new AppointOperationModel();
            operationModel.setAppointmentId(appointmentEntity.getId());
            operationModel.setOperateType((byte) 0);
            Integer appointmentOperateRecord = appointOperateRecordBiz.insertAppointmentOperateRecord(operationModel);
            if (appointmentOperateRecord <= 0 ){
                return ResponseUtil.fail(AppointmentError.OPERATION_RECORD_FAIL.getCode(),AppointmentError.OPERATION_RECORD_FAIL.getMessage(),null);
            }
            // 如果添加预约成功，则返回预约成功信息
            return ResponseUtil.success();
        }

        // 如果预约有冲突返回冲突的预约
        return appointConflictResult;
    }

    /**
     *  添加预约（冲突后继续添加）
     * @param form  预约参数封装
     * @return  ResponseResult
     */
    public ResponseResult continueAddAppointment(AppointmentBaseModel form) {
        // 检测预约分解参数是否正常
        List<AppointmentSplitBaseInfo> splits = this.checkAppointSplitField(form.getSplitList());
        if (!StringHelper.isEmpty(splits)) {
            return ResponseUtil.fail(AppointmentError.APPOINT_SPLIT_PARAM_ERR.getCode(),
                    AppointmentError.APPOINT_SPLIT_PARAM_ERR.getMessage(),null);
        }
        // 将Form转为Entity
        Appointment build = transferFormToEntity(form);
        int result = mapper.insertAppointment(build);
        if (result > 0) {
            rabbitMqServiceFeign.sendMessage(build.getId(),0,0, BaseTreatmentProcess);
            // 添加预约时长分解
            List<AppointmentSplitBaseInfo> splitList = form.getSplitList();
            if (splitList != null && !splitList.isEmpty()){
                AppointmentSplitModel splitModel = new AppointmentSplitModel();
                splitModel.setSplitList(form.getSplitList());
                splitModel.setAppointDuration(build.getAppointDuration());
                splitModel.setAppointmentId(build.getId());
                splitModel.setOrgId(build.getOrgId());
                splitModel.setAppointDate(form.getAppointDate());
                Integer splitResult = appointmentSplitBiz.insertAppointSplit(splitModel);
                if (splitResult == null || splitResult <= 0){
                    return ResponseUtil.fail(AppointmentError.APPOINTMENT_SPLIT_FAIL.getCode(),AppointmentError.APPOINTMENT_SPLIT_FAIL.getMessage(),null);
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
     * @return Appointment
     */
    public ResponseResult updateAppointStatus(Integer id, Byte appointState, String remarks) {
        Appointment appointment = mapper.selectByPrimaryKey(id);
        if (appointment == null){
            return ResponseUtil.fail(AppointmentError.APPOINT_DATA_NOT_EXIST.getCode(),AppointmentError.APPOINT_DATA_NOT_EXIST.getMessage(),null);
        }

        // 预约未到以外的情况不能编辑预约
        if (appointment.getAppointStatus() != 0){
            return ResponseUtil.fail(AppointmentError.APPOINT_NOT_ALLOW_EDIT_1.getCode(),AppointmentError.APPOINT_NOT_ALLOW_EDIT_1.getMessage(),null);
        }

        // inservice 无效时预约不可以编辑
        if (!appointment.getInservice()){
            return ResponseUtil.fail(AppointmentError.APPOINT_INVALID_NOT_ALLOW_EDIT.getCode(),AppointmentError.APPOINT_INVALID_NOT_ALLOW_EDIT.getMessage(),null);
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
        int i =0;
        if (appointState == 1){
            appointment.setAppointStatus((byte) 1);
            i = mapper.updateByPrimaryKeySelective(appointment);

            // 操作记录
            AppointOperationModel model = new AppointOperationModel();
            model.setAppointmentId(id);
            model.setOperateType((byte) 3);
            appointOperateRecordBiz.insertAppointmentOperateRecord(model);
        } else if (appointState == 2){
            // 取消预约
            appointment.setAppointStatus((byte) 2);
            appointment.setInservice(false);
            i = mapper.updateByPrimaryKeySelective(appointment);
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
            i = mapper.updateByPrimaryKeySelective(appointment);
            // 操作记录
            AppointOperationModel model = new AppointOperationModel();
            model.setAppointmentId(id);
            model.setOperateType((byte) 2);
            appointOperateRecordBiz.insertAppointmentOperateRecord(model);
        }
        if (i > 0) {
            rabbitMqServiceFeign.sendMessage(id,0,1, BaseTreatmentProcess);
        }
        return ResponseUtil.success(appointment);
    }


    /**
     * 删除预约（取消预约）
     * @param id   预约id
     * @param cause  取消预约原因
     * @return ResponseResult
     */
    public ResponseResult appointmentCancel(Integer id, String cause){
        // 检查预约是否已经挂号，如果已经挂号，则不允许修改操作
        Registered registerQuery = new Registered();
        registerQuery.setAppointmentId(id);
        registerQuery.setInservice(true);
        Registered registeredByExample = this.remoteTreatmentServiceFeign.findRegisteredByExample(registerQuery);
        if (null != registeredByExample) {
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_REGISTRATERED.getCode(),
                    AppointmentError.APPOINTMENT_REGISTRATERED.getMessage(),null);
        }
        // 检查取消原因内容长度
        if (null != cause && cause.length() > 500) {
            return ResponseUtil.fail(AppointmentError.TEXT_MAX_LENGTH_ERROR.getCode(),
                    AppointmentError.TEXT_MAX_LENGTH_ERROR.getMessage(),null);
        }
        Appointment appointment = mapper.selectByPrimaryKey(id);
        // 预约不存在的情况
        if (appointment == null){
            return ResponseUtil.fail(AppointmentError.APPOINT_DATA_NOT_EXIST.getCode(),
                    AppointmentError.APPOINT_DATA_NOT_EXIST.getMessage(),null);
        }
        // 除预约未到意外的其他情况，不可以取消预约
        Byte appointStatus = appointment.getAppointStatus();
        if (appointStatus != 0) {
            return ResponseUtil.fail(AppointmentError.APPOINT_NOT_ALLOW_CANCEL.getCode(),
                    AppointmentError.APPOINT_NOT_ALLOW_CANCEL.getMessage(),null);
        }
        appointment.setAppointStatus((byte) 2);
        appointment.setCancelReason(cause);
        appointment.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        appointment.setUpdName(BaseContextHandler.getName());
        appointment.setUpdTime(new Date(System.currentTimeMillis()));
        int result = mapper.updateByPrimaryKeySelective(appointment);
        if (result <= 0){
            return ResponseUtil.fail(AppointmentError.APPOINT_CANCEL_FAIL.getCode(),AppointmentError.APPOINT_CANCEL_FAIL.getMessage(),null);
        }
        AppointOperationModel record = new AppointOperationModel();
        record.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
        record.setAppointmentId(appointment.getId());
        record.setOperateType((byte) 2);
        record.setRemarks(cause);
        appointOperateRecordBiz.insertAppointmentOperateRecord(record);
        // 发送消息更新中间表就诊流程
        rabbitMqServiceFeign.sendMessage(id,0,2, BaseTreatmentProcess);
        return ResponseUtil.success();
    }

    /**
     * 编辑预约（出现冲突）
     * @param form  预约表单
     * @return ResponseResult
     */
    public ResponseResult updateAppointment(AppointmentBaseForm form){
        // 检查预约是否已经挂号，如果已经挂号，则不允许修改操作
        Integer id = form.getId();
        Registered registerQuery = new Registered();
        registerQuery.setAppointmentId(id);
        registerQuery.setInservice(true);
        Registered registeredByExample = this.remoteTreatmentServiceFeign.findRegisteredByExample(registerQuery);
        if (null != registeredByExample) {
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_REGISTRATERED.getCode(),
                    AppointmentError.APPOINTMENT_REGISTRATERED.getMessage(),null);
        }
        AppointmentBaseModel appointBaseModel = EntityUtils.build(form, AppointmentBaseModel.class);
        // 检查预约当天预约的医生是否排班
        ResponseResult dentistSchedulingConflict = this.checkScheduling(appointBaseModel);
        if (dentistSchedulingConflict != null){
            return dentistSchedulingConflict;
        }
        // 检测预约分解参数是否正常
        List<AppointmentSplitBaseInfo> splits = this.checkAppointSplitField(form.getSplitList());
        if (!StringHelper.isEmpty(splits)) {
            return ResponseUtil.fail(AppointmentError.APPOINT_SPLIT_PARAM_ERR.getCode(),
                    AppointmentError.APPOINT_SPLIT_PARAM_ERR.getMessage(),null);
        }
        // 检查预约冲突（只检查医生预约冲突、设备预约冲突）
        ResponseResult responseResult = editCheckConflict(form.getId(), form);
        if (null == responseResult) {
            return this.continueUpdateAppointment(form);
        }
        // 返回冲突数据
        return responseResult;
    }

    /**
     * 检测预约分解参数是否正常
     * @param splitList 预约分解列表
     * @return 如果分解参数正常返回null，否则返回错误的分解信息
     */
    private List<AppointmentSplitBaseInfo> checkAppointSplitField(List<AppointmentSplitBaseInfo> splitList) {
        if (!StringHelper.isEmpty(splitList)) {
            List<AppointmentSplitBaseInfo> splitBaseInfos = splitList.stream().filter(appointmentSplitBaseInfo -> appointmentSplitBaseInfo.getAssistantId() == null ||
                    appointmentSplitBaseInfo.getSplitEndTime() == null ||
                    appointmentSplitBaseInfo.getSplitEndTime() == null).collect(Collectors.toList());
            if (StringHelper.isEmpty(splitBaseInfos)) {
                int position = 0;
                for (int index = 0; index < splitList.size(); index++) {
                    AppointmentSplitBaseInfo baseInfo = splitList.get(index);
                    for (position = index+1; position < splitList.size(); position++) {
                        AppointmentSplitBaseInfo baseInfo1 = splitList.get(position);
                        if (baseInfo.getAssistantId().equals(baseInfo1.getAssistantId())) {
                            return splitList;
                        }
                    }
                }
            }
        }
        return null;
    }


    /**
     * 编辑预约（有冲突继续保存）
     * @param appointmentForm 更新预约信息form
     * @return ResponseResult
     */
    public ResponseResult continueUpdateAppointment(AppointmentBaseForm appointmentForm) {
        // 判断预约医生是否离职，如果离职则不可以编辑预约
        Integer dentistId = appointmentForm.getDentistId();
        SysEmployee dentistInfo = this.remoteSystemServiceFeign.findSysEmployeeById(dentistId);
        if (dentistInfo.getWorkStatus().equals(BusinessConstants.USER_RESIGNATION_STATUS)){
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_DENTIST_LEAVE.getCode(),
                    AppointmentError.APPOINTMENT_DENTIST_LEAVE.getMessage(),null);
        }

        // 检查预约是否已经挂号，如果已经挂号，则不允许修改操作
        Integer id = appointmentForm.getId();
        Registered registerQuery = new Registered();
        registerQuery.setAppointmentId(id);
        registerQuery.setInservice(true);
        Registered registeredByExample = this.remoteTreatmentServiceFeign.findRegisteredByExample(registerQuery);
        if (null != registeredByExample) {
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_REGISTRATERED.getCode(),
                    AppointmentError.APPOINTMENT_REGISTRATERED.getMessage(),null);
        }
        // 检测预约分解参数是否正常
        List<AppointmentSplitBaseInfo> splits = this.checkAppointSplitField(appointmentForm.getSplitList());
        if (!StringHelper.isEmpty(splits)) {
            return ResponseUtil.fail(AppointmentError.APPOINT_SPLIT_PARAM_ERR.getCode(),
                    AppointmentError.APPOINT_SPLIT_PARAM_ERR.getMessage(),null);
        }
        Appointment appointEntity = transferFormToEntity(appointmentForm);
        appointEntity.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        appointEntity.setUpdName(BaseContextHandler.getName());
        appointEntity.setUpdTime(new Date(System.currentTimeMillis()));

        // 预约未到以外的情况不能编辑预约
        if (appointEntity.getAppointStatus() != 0){
            return ResponseUtil.fail(AppointmentError.APPOINT_NOT_ALLOW_EDIT_1.getCode(),AppointmentError.APPOINT_NOT_ALLOW_EDIT_1.getMessage(),null);
        }

        // inservice 无效时预约不可以编辑
        if (!appointEntity.getInservice()){
            return ResponseUtil.fail(AppointmentError.APPOINT_INVALID_NOT_ALLOW_EDIT.getCode(),AppointmentError.APPOINT_INVALID_NOT_ALLOW_EDIT.getMessage(),null);
        }

        // 查询修改前的预约信息，方便做操作记录使用
        Appointment appointment = mapper.selectByPrimaryKey(id);

        int num = mapper.updateByPrimaryKeySelective(appointEntity);
        if (num <= 0){
            return ResponseUtil.fail(AppointmentError.APPOINT_EDIT_FAIL.getCode(),AppointmentError.APPOINT_EDIT_FAIL.getMessage(),null);
        }
        // 发送消息更新中间表就诊流程
        rabbitMqServiceFeign.sendMessage(id,0,1, BaseTreatmentProcess);

        // 保存预约更新被修改的日期、医生
        appointmentModifyRecordBiz.saveAppointModify(appointmentForm,appointment);
        // 修改时长分解
        List<AppointmentSplitBaseInfo> splitList = appointmentForm.getSplitList();
        if (splitList != null && !splitList.isEmpty()){
            AppointmentSplitForm splitForm = new AppointmentSplitForm();
            splitForm.setAppointDate(appointmentForm.getAppointDate());
            splitForm.setSplitList(appointmentForm.getSplitList());
            splitForm.setOrgId(appointmentForm.getOrgId());
            splitForm.setAppointDuration(appointmentForm.getAppointDuration());
            splitForm.setAppointmentId(appointmentForm.getId());
            Integer splitResult = appointmentSplitBiz.updateAppointSplit(splitForm);
            if (splitResult == null || splitResult <= 0){
                return ResponseUtil.fail(AppointmentError.APPOINTMENT_SPLIT_FAIL.getCode(),AppointmentError.APPOINTMENT_SPLIT_FAIL.getMessage(),null);
            }
        }
        // 生成修改预约操作记录
        appointOperateRecordBiz.saveAppointOperationRecord(appointment,appointmentForm);
        return ResponseUtil.success();
    }


    /**
     * 根据条件查询预约列表
     * @param query  查询条件
     * @return  预约列表
     */
    public PageInfo findAppointmentListByExample(AppointListQuery query){
        // 按条件检索之后的列表
        List<AppointmentListItemVo> collect = null;
        // 没有经过检索的列表
        List<AppointmentListItemVo> appointmentList = new ArrayList<>();
        AppointmentQuery appointmentQuery = new AppointmentQuery();
        appointmentQuery.setAppointDate(query.getAppointDate());
        appointmentQuery.setOrgId(query.getOrgId());
        appointmentQuery.setAppointType(query.getFirstVisit());
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<AppointmentVo> appointmentVos = mapper.findAppointmentByExample(appointmentQuery);
        PageInfo pageInfo = new PageInfo(appointmentVos);
        // 预约医生ID列表,预约助手ID列表
        List<Integer> dentistIdsAndAssistantIds = new ArrayList<>();
        // 预约科室ID列表
        List<Integer> deptRoomIds = new ArrayList<>();
        // 预约患者ID列表
        List<Integer> patientIds = new ArrayList<>();
        if (StringHelper.isNotEmpty(appointmentVos)) {
            // 设置预约医生/助手信息
            appointmentVos.forEach(appointmentVo -> {
                Integer dentistId = appointmentVo.getDentistId();
                if (!dentistIdsAndAssistantIds.contains(dentistId)) {
                    dentistIdsAndAssistantIds.add(dentistId);
                }
                Integer assistantId = appointmentVo.getAssistantId();
                if (!dentistIdsAndAssistantIds.contains(assistantId)) {
                    dentistIdsAndAssistantIds.add(assistantId);
                }
                Integer deptRoomId = appointmentVo.getDeptRoomId();
                if (!deptRoomIds.contains(dentistId)) {
                    deptRoomIds.add(deptRoomId);
                }
                patientIds.add(appointmentVo.getPatientId());
            });
            // 调用服务获取医生信息和助手信息列表
            List<SysUserInfoDetail> dentistAndAssistantInfos = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserIds(dentistIdsAndAssistantIds);
            // 获取患者信息列表
            List<PatientTotalInfoVo> patientTotalInfos = remotePatientCentralServiceFeign.findPatientTotalInfo(patientIds);
            // 获取科室信息列表
            List<DepartmentRoom> departmentRooms = remoteSystemServiceFeign.findDepartmentRoomByIds(deptRoomIds);
            // 获取患者欠费信息
            List<DebtAmountModel> debtAmountList = remoteTreatmentServiceFeign.findDebtAmountList(patientIds);

            // 获取患者会员卡类型ID列表
            List<Integer> memberTypeIds = patientTotalInfos.stream().map(PatientTotalInfoVo::getMemberTypeId).collect(Collectors.toList());
            // 会员卡类型列表
            List<MemberType> memberTypes = remoteSystemServiceFeign.findMemberTypeByIds(memberTypeIds);
            // 组合预约列表信息
            appointmentVos.forEach(appointmentVo -> {
                AppointmentListItemVo build = EntityUtils.build(appointmentVo, AppointmentListItemVo.class);
                // 向预约列表中注入预约相关信息
                this.setAppointmentInfo(build, appointmentVo, dentistAndAssistantInfos, departmentRooms);
                // 向预约列表中注入患者信息
                this.setPatientInfo(build, patientTotalInfos, debtAmountList,memberTypes);
                appointmentList.add(build);
            });
            // 关键字检索
            collect = this.searchMatchs(appointmentList,
                    query.getFirstVisit(),
                    query.getDentistName(),
                    query.getMedicalNumber(),
                    query.getSearch());
        }
        // 如果不为空，则有内容过滤，返回过滤之后的结果
        if (StringHelper.isNotEmpty(collect)) {
            pageInfo.setList(collect);
        } else {
            pageInfo.setList(appointmentList);
        }
        return pageInfo;
    }

    /**
     * 检索关键词匹配
     * @param appointmentList  月列表
     * @param appointType  预约类型
     * @param dentistName  医生名字
     * @param medicalNumber  病历号
     * @param search  检索关键字
     * @return  返回检索之后的结果列表
     */
    private List<AppointmentListItemVo> searchMatchs(List<AppointmentListItemVo> appointmentList,
                                                     Byte appointType,
                                                     String dentistName,
                                                     String medicalNumber,
                                                     String search) {
        List<AppointmentListItemVo> collect = null;
        if (appointType != null
                || !StringHelper.isEmpty(dentistName)
                || !StringHelper.isEmpty(medicalNumber)
                || !StringHelper.isEmpty(search)) {
            collect = appointmentList.stream()
                    .filter(
                            appointmentListItemVo -> {
                                int icount = 0;
                                boolean result = false;
                                // 按病历号检索
                                if (!StringHelper.isEmpty(medicalNumber)){
                                    String medicalNumberTmp = appointmentListItemVo.getMedicalNumber();
                                    if (StringHelper.isNotBlank(medicalNumberTmp)) {
                                        result = result | medicalNumberTmp.equals(medicalNumber);
                                        icount++;
                                    }
                                }
                                // 按预约医生检索
                                if (!StringHelper.isEmpty(dentistName)) {
                                    if (icount == 1 && !result) {
                                        return false;
                                    } else {
                                        result = result | appointmentListItemVo.getDentistName().equals(dentistName);
                                    }
                                    icount++;
                                }
                                // 按姓名/手机号/姓名拼音
                                if (!StringHelper.isEmpty(search)){
                                    if (icount == 2 && !result) {
                                        return false;
                                    }
                                    // 检索值
                                    String mobile = appointmentListItemVo.getMobile();
                                    String patientName = appointmentListItemVo.getPatientName();
                                    String pinyinName = appointmentListItemVo.getPinyinName();
                                    // 按姓名检索
                                    if (search.matches(BusinessConstants.NAME_REGEXP) && !StringHelper.isEmpty(patientName)){
                                        result = result | patientName.contains(search);
                                    } else if (search.matches(BusinessConstants.MOBILE_REGEXP) && !StringHelper.isEmpty(mobile)) {
                                        // 按手机号检索
                                        result = result |  mobile.equals(search);
                                    } else if (search.matches(BusinessConstants.PINYIN_REGEXP) && !StringHelper.isEmpty(pinyinName)){
                                        // 按拼音名字检索
                                        result = result |  pinyinName.contains(search);
                                    } else {
                                        result = false;
                                    }
                                }
                                return result;
                            }
                    ).collect(Collectors.toList());
        }
        return collect;
    }


    /**
     * 根据条件查询预约可视图（患者维度）按预约患者数量降序排列
     * 可用范围 根据医生id、排班时间查询医生预约信息
     * @param query 查询条件
     * @return List<AppointmentDimensionVo>
     */
    public List<AppointmentDimensionVo> findAppointmentPatientDimensionByExample(PatientDimensionByDayQuery query) {
        List<AppointmentDimensionVo> appointmentDimensionVos;
        // 根据门诊ID获取该门诊所有可预约医生的ID
        List<Integer> enableDentistIds = this.enableAppointDentistIds(query.getOrgId());
        // 组合预约中心预约信息（包含预约医生，护士的排班以及预约人数）
        appointmentDimensionVos = this.dimensionAppointInfo(query,enableDentistIds);
        // 最后进行排序
        return this.sort(appointmentDimensionVos, query.getOrder(), query.getOrderBy());
    }

    /**
     * 根据门诊ID获取该门诊所有可预约医生的ID
     * @param orgId 门诊ID
     * @return 可预约ID集合
     */
    private List<Integer> enableAppointDentistIds(Integer orgId) {
        // 获取可预约的医生
        List<EnableChooseEmployeeRes> enableChooseEmployeeRes = this.clinicEmployeeConfigFeign.enableAppointEmployeeList(orgId,false);
        if (StringHelper.isNotEmpty(enableChooseEmployeeRes)) {
            List<Integer> enableDentistIds = enableChooseEmployeeRes.stream().map(EnableChooseEmployeeRes::getEmployeeId).collect(Collectors.toList());
            return enableDentistIds;
        }
        throw new ClientServiceException(AppointmentError.CLINIC_NOT_EXIST_ENABLE_APPOINT_DENTIST.getMessage(),
                AppointmentError.CLINIC_NOT_EXIST_ENABLE_APPOINT_DENTIST.getCode());
    }
    /**
     * 组合预约中心预约信息（包含预约医生，护士的排班以及预约人数）
     * @param enableDentistIds 可预约医生ID列表
     * @param query 预约信息查询参数
     * @return 返回预约信息列表
     */
    private List<AppointmentDimensionVo> dimensionAppointInfo(PatientDimensionByDayQuery query, List<Integer> enableDentistIds) {
        Date startDate = query.getStartDate();
        Date endDate = query.getEndDate();
        Integer orgId = query.getOrgId();
        // 预约可视图列表
        List<AppointmentDimensionVo> appointmentDimensionVoList = Collections.synchronizedList(new ArrayList<>());
        // 查询当前天有排班的员工列表
        // 获取当天的所有医生的排班（包含有排班和没有排班的医生）
        List<UserWorkVO> shiftWorkDatas = this.getEmployeeScheduleList(startDate,endDate,query.getOrgId(),query.getDentistId());
        // 没有排班医生
        List<Integer> unScheduleIds = new ArrayList<>();
        // 查询某一天数据会过滤出没有排班员工
        if (startDate.getTime() == endDate.getTime()) {
            for (UserWorkVO userWorkVO : shiftWorkDatas) {
                List<WorkDayVO> days = userWorkVO.getDays();
                if (!StringHelper.isEmpty(days)) {
                    for (WorkDayVO workDayVO: days) {
                        Integer id = workDayVO.getId();
                        // 将没有排班的员工ID放入flags列表中
                        if (null == id) {
                            Integer compEmpId = userWorkVO.getCompEmpId();
                            unScheduleIds.add(compEmpId);
                        }
                    }
                }
            }
        }

        // 过滤出可预约医生的排班信息
        List<UserWorkVO> filterAppointIds = shiftWorkDatas.stream().filter(
                userWorkVO -> enableDentistIds.contains(userWorkVO.getCompEmpId())).collect(Collectors.toList());

        // 所有可预约医生ID列表（可能包含离职员工）
        List<Integer> dentistIdsList = filterAppointIds.stream().map(UserWorkVO::getCompEmpId).collect(Collectors.toList());
        // 如果医生ID为空则表示查询预约可视图所有医生的预约，则需要过滤出离职还有预约的医生，否则不做操作
        if (null == query.getDentistId()) {
            // 过滤出已经离职的但是还有预约的医生ID
            this.leavePostEmployeeIdHandler(startDate, endDate, orgId, enableDentistIds, dentistIdsList);
        } else if (this.isLeavePostById(query.getDentistId())) {
            /* 如果员工离职，则在编辑预约换面预约可视图也要显示 */
            dentistIdsList.add(query.getDentistId());
        }

        // 所有预约基础信息
        List<AppointmentDimensionVo> appointmentDimensionCommInfos = mapper.findAppointmentDimensionCommInfo(startDate, endDate, dentistIdsList, orgId);
        // 处理已经离职的，但是还有预约的医生，也要显示在预约可视图中
        this.leavePostEmployeeHandler(orgId, appointmentDimensionCommInfos,filterAppointIds);
        // 预约ID列表
        List<Integer> appointIdsList = null;
        // 患者ID列表
        List<Integer> patientIdsList = new ArrayList<>();
        if (StringHelper.isNotEmpty(appointmentDimensionCommInfos)) {
            List<Integer> appointIdsTemp = new ArrayList<>();
            List<Integer> patientIdsTemp = new ArrayList<>();
            appointmentDimensionCommInfos.forEach(item -> {
                // 过滤预约ID,并将过滤出的ID列表合并到appointIdsTemp
                List<AppointmentPatientCardVo> appointmentPatientCardVos = item.getAppointmentPatientCardVos();
                List<Integer> collect = appointmentPatientCardVos.stream().map(AppointmentPatientCardVo::getId).collect(Collectors.toList());
                collect.stream().sequential().collect(Collectors.toCollection(()->appointIdsTemp));
                // 过滤患者ID并将过滤出的ID列表合并到patientIdsTemp
                List<Integer> collect1 = appointmentPatientCardVos.stream().map(AppointmentPatientCardVo::getPatientId).collect(Collectors.toList());
                collect1.stream().sequential().collect(Collectors.toCollection(()->patientIdsTemp));
            });
            appointIdsList = new ArrayList<>(new HashSet<>(appointIdsTemp));
            patientIdsList = new ArrayList<>(new HashSet<>(patientIdsTemp));
        }

        log.info("患者维度预约医生ID==>{}",appointIdsList);

        // 查询患者接诊记录
        List<TreatmentRecord> treatmentRecordListByAppointIds;
        if (StringHelper.isNotEmpty(appointIdsList)) {
            treatmentRecordListByAppointIds = remoteTreatmentServiceFeign.findTreatmentRecordListByAppointIds(appointIdsList);
        } else {
            treatmentRecordListByAppointIds = new ArrayList<>();
        }
        log.info("批量查询患者就诊记录==>{}",treatmentRecordListByAppointIds);

        // 查询患者信息
        List<PatientTotalInfoVo> patientTotalInfos;
        if (StringHelper.isNotEmpty(patientIdsList)) {
            patientTotalInfos = remotePatientCentralServiceFeign.findPatientTotalInfo(patientIdsList);
        } else {
            patientTotalInfos = new ArrayList<>();
        }

        // 查询员工请假信息
        List<LeaveInfoListVO> employeeLeaveInfos = this.findEmployeeLeaveInfos(dentistIdsList, query.getStartDate(), query.getEndDate());
        // 查询员工加班信息
        List<WorkOvertimeInfoListVO> employeeWorkOvertimes = this.findEmployeeWorkOvertimes(orgId, query.getStartDate(), query.getEndDate());
        // 查询员工外勤信息
        List<FieldInfoListVO> fieldList = this.findFieldList(dentistIdsList, query.getStartDate(), query.getEndDate());

        for (UserWorkVO userWorkVO : filterAppointIds) {
            // 组合预约医生和患者信息（患者维度）
            List<AppointmentDimensionVo> dimensionVoList = this.combinationPatientDimensionVo(userWorkVO,
                    appointmentDimensionCommInfos,treatmentRecordListByAppointIds,
                    patientTotalInfos,employeeLeaveInfos,employeeWorkOvertimes,fieldList);
            // 将预约信息放入预约可视图列表
            if (StringHelper.isNotEmpty(dimensionVoList)) {
                dimensionVoList.forEach(dimensionVo -> {
                    Integer dentistId = dimensionVo.getDentistId();
                    List<AppointmentDimensionVo> appointmentAssistants = dimensionVo.getAppointmentAssistants();
                    List<AppointmentPatientCardVo> appointmentPatientCardVos = dimensionVo.getAppointmentPatientCardVos();
                    // 只有在当天有预约或者有排班才将预约信息添加到预约信息列表
                    if (!(StringHelper.isEmpty(appointmentAssistants) &&
                            StringHelper.isEmpty(appointmentPatientCardVos) &&
                            unScheduleIds.contains(dentistId))) {
                        appointmentDimensionVoList.add(dimensionVo);
                    }
                });
            }
        }
        return appointmentDimensionVoList;
    }

    /**
     * 批量查询员工外勤信息
     * @param dentistIdsList  员工ID
     * @param startDate  外勤开始时间
     * @param endDate  外勤结束时间
     * @return
     */
    private List<FieldInfoListVO> findFieldList(List<Integer> dentistIdsList,Date startDate, Date endDate) {
        FieldInfoForm fieldQuery = new FieldInfoForm();
        fieldQuery.setUserList(dentistIdsList);
        fieldQuery.setStartWorkDate(startDate);
        fieldQuery.setEndWorkDate(endDate);
        fieldQuery.setApprovalStatus(1);
        List<FieldInfoListVO> fieldInfoListVOS = this.employeeAttendServiceFeign.fieldFindList(fieldQuery);
        if (StringHelper.isNotEmpty(fieldInfoListVOS)) {
            fieldInfoListVOS = fieldInfoListVOS.stream().filter(entity-> entity.getApprovalStatus() == 1).collect(Collectors.toList());
        }
        return fieldInfoListVOS;
    }

    /**
     * 批量查询员工请假信息列表
     * @param ids 员工ID
     * @param startDate 开始时间
     * @param endDate  结束时间
     * @return 员工请假信息列表
     */
    private List<LeaveInfoListVO> findEmployeeLeaveInfos(List<Integer> ids,Date startDate, Date endDate) {
        LeaveInfoForm query = new LeaveInfoForm();
        query.setStartTime(startDate);
        query.setEndTime(endDate);
        query.setUserIds(ids);
        // 审批状态 0 审批中 1通过 2拒绝 3撤回
        query.setApprovalStatus(1);
        List<LeaveInfoListVO> listByIds = this.employeeAttendServiceFeign.backFindListByIds(query);
        if (StringHelper.isNotEmpty(listByIds)) {
            if (startDate.compareTo(endDate) != 0) {
                listByIds = listByIds.stream().filter(entity->{
                    // 请假类型是按天请假时直接返回
                    if (entity.getVacationStatus() == 1) {
                        return true;
                    } else {
                        // 按日期范围查找请假信息时，只显示范围内的请假详情
                        return null != entity.getWorkDate()
                                && startDate.compareTo(entity.getWorkDate()) <= 0
                                && endDate.compareTo(entity.getWorkDate()) >= 0;
                    }
                }).collect(Collectors.toList());
            } else {
                listByIds = listByIds.stream().filter(entity->{
                    // 请假类型是按天请假时直接返回
                    if (entity.getVacationStatus() == 1) {
                        return true;
                    } else {
                        // 按指定日期查找请假信息时，只显示指定日期的请假信息
                        return null != entity.getWorkDate()
                                && startDate.compareTo(entity.getWorkDate()) == 0;
                    }
                }).collect(Collectors.toList());
            }
        }
        return listByIds;
    }

    /**
     * 批量查询员工请假信息列表
     * @param orgId 门诊ID
     * @param startDate 开始时间
     * @param endDate  结束时间
     * @return 员工请假信息列表
     */
    private List<WorkOvertimeInfoListVO> findEmployeeWorkOvertimes(Integer orgId,Date startDate, Date endDate) {
        // 查询员工加班信息
        WorkOvertimeInfoForm workOverTime = new WorkOvertimeInfoForm();
        workOverTime.setCompanyId(orgId);
        workOverTime.setStartWorkDate(startDate);
        workOverTime.setEndWorkDate(endDate);
        workOverTime.setApprovalStatus(1);
        List<WorkOvertimeInfoListVO> workOvertimeInfoListVOS = this.employeeAttendServiceFeign.workFindList(workOverTime);
        if (StringHelper.isNotEmpty(workOvertimeInfoListVOS)) {
            workOvertimeInfoListVOS = workOvertimeInfoListVOS.stream().filter(entity-> null !=entity.getId() && entity.getApprovalStatus() == 1).collect(Collectors.toList());
        }
        return workOvertimeInfoListVOS;
    }

    /**
     * 构建员工排班信息
     * @param obj  源对象
     * @param userId  用户ID
     * @param empScheduleVo 排班实例对象
     */
    private void setEmpSchedule(Object obj, Integer userId, String userName, EmpScheduleVo empScheduleVo) {
        if (obj instanceof WorkDayVO) {
            WorkDayVO wdv = (WorkDayVO) obj;
            this.buildEmpScheduleInfoFromWorkDayVO(empScheduleVo,wdv,userId,userName);
        } else if (obj instanceof LeaveInfoListVO) {
            LeaveInfoListVO leaveInfoListVO = (LeaveInfoListVO) obj;
            // 请假信息entity转化为员工排班entity
            this.buildEmpScheduleInfoFromLeaveInfoList(empScheduleVo, leaveInfoListVO);
        } else if (obj instanceof WorkOvertimeInfoListVO) {
            WorkOvertimeInfoListVO workOvertimeInfos = (WorkOvertimeInfoListVO) obj;
            // 加班信息转化为排班信息
            this.buildEmpScheduleInfoFromWorkOvertime(empScheduleVo,workOvertimeInfos);
        } else if (obj instanceof FieldInfoListVO) {
            FieldInfoListVO fieldInfoVO = (FieldInfoListVO) obj;
            // 外勤信息转化为排班信息
            this.buildEmpScheduleInfoFromFieldInfo(empScheduleVo,fieldInfoVO,userId,userName);
        }
    }

    /**
     * 外勤信息转化为排班信息
     * FieldInfoVO ---> EmpScheduleVo
     * @param empScheduleVo  员工排班请假信息对象实例
     * @param fieldInfoVO 员工外勤信息
     * @param userId  用户ID
     * @param userName  用户名称
     */
    private void buildEmpScheduleInfoFromFieldInfo(EmpScheduleVo empScheduleVo, FieldInfoListVO fieldInfoVO, Integer userId, String userName) {
        String dateTimePattern = "yyyy-MM-dd HH:mm";
        empScheduleVo.setUserId(userId);
        empScheduleVo.setUserName(userName);
        empScheduleVo.setType(4);
        empScheduleVo.setStatus("外勤");
        empScheduleVo.setCompanyId(fieldInfoVO.getCompanyId());
        empScheduleVo.setComName(fieldInfoVO.getCompanyName());
        String startTime = LocalDateTime.fromDateFields(fieldInfoVO.getStartTime()).toString(dateTimePattern);
        String endTime = LocalDateTime.fromDateFields(fieldInfoVO.getEndTime()).toString(dateTimePattern);
        empScheduleVo.setStartDate(startTime);
        empScheduleVo.setEndDate(endTime);
        empScheduleVo.setName(fieldInfoVO.getFieldAddress());
    }

    /**
     * 加班信息转化为排班信息
     * WorkOvertimeInfoListVO ---> EmpScheduleVo
     * @param empScheduleVo
     * @param workOvertimeInfos
     */
    private void buildEmpScheduleInfoFromWorkOvertime(EmpScheduleVo empScheduleVo,WorkOvertimeInfoListVO workOvertimeInfos) {
        String datePattern = "yyyy-MM-dd";
        String timePattern = "HH:mm";
        empScheduleVo.setUserId(workOvertimeInfos.getUserId());
        empScheduleVo.setUserName(workOvertimeInfos.getUserName());
        empScheduleVo.setCompanyId(workOvertimeInfos.getCompanyId());
        empScheduleVo.setComName(workOvertimeInfos.getCompanyName());
        empScheduleVo.setType(3);
        empScheduleVo.setStatus("加班");
        String workDate = LocalDate.fromDateFields(workOvertimeInfos.getWorkDate()).toString(datePattern);
        String workStartTime = LocalDateTime.fromDateFields(workOvertimeInfos.getStartTime()).toString(timePattern);
        String workEndTime = LocalDateTime.fromDateFields(workOvertimeInfos.getEndTime()).toString(timePattern);
        empScheduleVo.setStartDate(workDate + " " + workStartTime);
        empScheduleVo.setEndDate(workDate + " " + workEndTime);
        empScheduleVo.setName(workOvertimeInfos.getName());
    }

    /**
     * 请假信息entity转化为员工排班entity
     * LeaveInfoListVO ---> EmpScheduleVo
     * @param empScheduleVo  员工排班请假信息对象实例
     * @param leaveInfoListVO 员工请假信息对象
     */
    private void buildEmpScheduleInfoFromLeaveInfoList(EmpScheduleVo empScheduleVo, LeaveInfoListVO leaveInfoListVO) {
        String dataPatterStr = "yyyy-MM-dd";
        empScheduleVo.setUserId(leaveInfoListVO.getUserId());
        empScheduleVo.setType(2);
        empScheduleVo.setUserName(leaveInfoListVO.getUserName());
        Integer vacationStatus = leaveInfoListVO.getVacationStatus();
        String startTime = "";
        String endTime = "";
        if (vacationStatus == 0) {
            empScheduleVo.setStatus("班次");
            String hmsPatterStr = "HH:mm";
            String ymd = LocalDate.fromDateFields(leaveInfoListVO.getWorkDate()).toString(dataPatterStr);
            String hmsStart = LocalDateTime.fromDateFields(leaveInfoListVO.getLsStartDate()).toString(hmsPatterStr);
            String hmsEnd = LocalDateTime.fromDateFields(leaveInfoListVO.getLsEndDate()).toString(hmsPatterStr);
            startTime = ymd + " " + hmsStart;
            endTime = ymd + " " + hmsEnd;
        } else if (vacationStatus == 1) {
            empScheduleVo.setStatus("天");
            startTime = LocalDateTime.fromDateFields(leaveInfoListVO.getStartTime()).toString(dataPatterStr);
            endTime = LocalDateTime.fromDateFields(leaveInfoListVO.getEndTime()).toString(dataPatterStr);
        }

        empScheduleVo.setCompanyId(leaveInfoListVO.getCompanyId());
        empScheduleVo.setStartDate(startTime);
        empScheduleVo.setEndDate(endTime);
        empScheduleVo.setName(leaveInfoListVO.getLeaveReason());
    }

    /**
     * WorkDayVO ---> EmpScheduleVo
     * @param empScheduleVo  员工排班请假信息对象实例
     * @param userWorkVO   员工排班信息对象
     * @param userId  员工ID
     * @param userName  员工名字
     */
    private void buildEmpScheduleInfoFromUserWorkVO(EmpScheduleVo empScheduleVo, UserWorkVO userWorkVO, Integer userId, String userName) {
        empScheduleVo.setUserId(userId);
        empScheduleVo.setUserName(userName);
        List<WorkDayVO> days = userWorkVO.getDays();
        if (StringHelper.isNotEmpty(days)) {
            for (WorkDayVO uwd: days) {
                empScheduleVo.setName(uwd.getName());
                empScheduleVo.setComName(uwd.getComName());
                empScheduleVo.setType(1);
                empScheduleVo.setStatus(uwd.getType());
                empScheduleVo.setCompanyId(uwd.getCompClinId());
                if (StringHelper.isNotBlank(uwd.getStime()) && uwd.getDate() != null) {
                    String[] times = uwd.getStime().split("-");
                    String dataStr = LocalDate.fromDateFields(uwd.getDate()).toString("yyyy-MM-dd");
                    empScheduleVo.setStartDate(dataStr + " " + times[0]);
                    empScheduleVo.setEndDate(dataStr + " " + times[1]);
                }
            }
        }
    }

    /**
     * WorkDayVO ---> EmpScheduleVo
     * @param empScheduleVo  员工排班请假信息对象实例
     * @param wdv   员工排班信息对象
     * @param userId  员工ID
     * @param userName  员工名字
     */
    private void buildEmpScheduleInfoFromWorkDayVO(EmpScheduleVo empScheduleVo, WorkDayVO wdv, Integer userId, String userName) {
        empScheduleVo.setUserId(userId);
        empScheduleVo.setUserName(userName);
        empScheduleVo.setType(1);
        empScheduleVo.setStatus(wdv.getType());
        empScheduleVo.setName(wdv.getName());
        empScheduleVo.setComName(wdv.getComName());
        empScheduleVo.setCompanyId(wdv.getCompClinId());
        if (StringHelper.isNotBlank(wdv.getStime()) && wdv.getDate() != null) {
            String[] times = wdv.getStime().split("-");
            String dataStr = LocalDate.fromDateFields(wdv.getDate()).toString("yyyy-MM-dd");
            empScheduleVo.setStartDate(dataStr + " " + times[0]);
            empScheduleVo.setEndDate(dataStr + " " + times[1]);
        }
    }


    /**
     * 当前用户ID是否离职
     * @param dentistId 医生ID
     * @return  离职返回true 否则返回false
     */
    private boolean isLeavePostById(Integer dentistId) {
        SysEmployee sysEmployeeById = this.remoteSystemServiceFeign.findSysEmployeeById(dentistId);
        if (null != sysEmployeeById) {
            return sysEmployeeById.getWorkStatus() == BusinessConstants.USER_RESIGNATION_STATUS.byteValue();
        }
        return true;
    }


    /**
     * 过滤出已经离职的但是还有预约的医生ID
     * @param startDate  开始时间
     * @param endDate   结束日期
     * @param orgId    组织ID
     * @param enableDentistIds  所有可预约员工ID
     * @param dentistIdsList  当前日期有预约的医生ID列表
     */
    private void leavePostEmployeeIdHandler(Date startDate, Date endDate, Integer orgId, List<Integer> enableDentistIds, List<Integer> dentistIdsList) {

        List<AppointmentDimensionVo> appointmentDimensionCommInfos = mapper.findAppointmentDimensionCommInfo(startDate, endDate, null, orgId);
        if (StringHelper.isNotEmpty(appointmentDimensionCommInfos)) {
            List<Integer> leavePostDentistIds = appointmentDimensionCommInfos.stream().filter(entity -> !enableDentistIds.contains(entity.getDentistId())).map(AppointmentDimensionVo::getDentistId).collect(Collectors.toList());
            if (StringHelper.isNotEmpty(leavePostDentistIds)) {
                leavePostDentistIds.stream().sequential().collect(Collectors.toCollection(()->dentistIdsList));
            }
        }
    }

    /**
     * 处理已经离职的，但是还有预约的医生，也要显示在预约可视图中
     * @param orgId
     * @param appointmentDimensionCommInfos   当前天所有的预约信息列表
     * @param hasSechduleAndEnableAppointList  当前天排班的并且可预约的医生Id列表
     */
    private void leavePostEmployeeHandler(Integer orgId,List<AppointmentDimensionVo> appointmentDimensionCommInfos, List<UserWorkVO> hasSechduleAndEnableAppointList) {

        // 查询最近离职的100个员工信息,
        SysUserEmployeeModel employeeQuery = new SysUserEmployeeModel();
        employeeQuery.setOrgIds(Arrays.asList(orgId));
        employeeQuery.setWorkStatus(new Byte[]{2});
        // 预约医生ID列表
        List<Integer> dentistIdsList = hasSechduleAndEnableAppointList.stream().map(UserWorkVO::getCompEmpId).collect(Collectors.toList());
        // 所有有预约的医生，包含离职医生
        List<Integer> appointIds = appointmentDimensionCommInfos.stream().map(AppointmentDimensionVo::getDentistId).collect(Collectors.toList());

        // 离职员工ID列表
        List<Integer> leavePostDentistIds = appointIds.stream().filter(id -> !dentistIdsList.contains(id)).collect(Collectors.toList());

        // 离职员工信息列表
        List<SysUserInfoDetail> leavePostUserInfoList = this.remoteSystemServiceFeign.findSysUserEmployeeInfoByUserIds(leavePostDentistIds);

        // 将离职员工的信息加入排班表中,为了解决已离职医生的预约显示在预约可视图中
        if (StringHelper.isNotEmpty(leavePostUserInfoList)) {
            leavePostUserInfoList.forEach(entity->{
                UserWorkVO userWorkVO = new UserWorkVO();
                userWorkVO.setName(entity.getName());
                userWorkVO.setCompEmpId(entity.getUserId());

                WorkDayVO workDayVO = new WorkDayVO();
                // 获取离职员工下预约的患者是哪一天的
                AppointmentDimensionVo appointmentDimensionVo = appointmentDimensionCommInfos.stream().filter(appointInfo -> appointInfo.getDentistId().equals(entity.getUserId())).findFirst().get();
                Date currentDate = appointmentDimensionVo.getCurrentDate();
                // 给离职员工创建空的离职排班
                List<WorkDayVO> workDayVOS = new ArrayList<>();
                if (StringHelper.isBlank(entity.getLeaveTime())) {
                    workDayVO.setDate(currentDate);
                } else {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date leaveDate = dateFormat.parse(entity.getLeaveTime());
                        workDayVO.setDate(leaveDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                workDayVOS.add(workDayVO);
                userWorkVO.setDays(workDayVOS);
                hasSechduleAndEnableAppointList.add(userWorkVO);
            });
        }
    }

    /**
     * 获取指定日期，指定门诊,指定医生的排班信息
     * @param startDate  开始日期
     * @param endDate  结束日期
     * @param orgId  门诊ID
     * @param dentistId  医生ID
     * @return
     */
    private List<UserWorkVO> getEmployeeScheduleList(Date startDate, Date endDate, Integer orgId, Integer dentistId) {
        // 查询当前天有排班的员工列表
        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr = simpleDateFormat.format(startDate);
        String endDateStr = simpleDateFormat.format(endDate);
        employeeScheduleQueryForm.setStartDate(startDateStr);
        employeeScheduleQueryForm.setEndDate(endDateStr);
        employeeScheduleQueryForm.setClinicId(orgId);
        employeeScheduleQueryForm.setUserId(dentistId);
        EmployeeScheduleResultVO scheduleResultVO = employeeAttendServiceFeign.findList(employeeScheduleQueryForm);

        if (scheduleResultVO == null || scheduleResultVO.getCount() <= 0) {
            // 查看当前员工是否处于离职状态
            SysEmployee sysEmployeeInfo = remoteSystemServiceFeign.findSysEmployeeById(dentistId);
            if (null != sysEmployeeInfo) {
                Byte workStatus = sysEmployeeInfo.getWorkStatus();
                // 如果员工处于离职状态，则返回离职员工信息，方便后面显示离职员工下的患者预约
                if (workStatus == BusinessConstants.USER_RESIGNATION_STATUS.byteValue()) {
                    UserWorkVO userWorkVO = new UserWorkVO();
                    userWorkVO.setCompEmpId(dentistId);
                    userWorkVO.setName(sysEmployeeInfo.getName());
                    List<UserWorkVO> leavePostEmployeeList = new ArrayList<>();
                    leavePostEmployeeList.add(userWorkVO);
                    return leavePostEmployeeList;
                } else {
                    // 预约的医生被调到其他门诊，但是在该门诊还会查到医生在该门诊的预约信息
                    UserWorkVO userWorkVO = new UserWorkVO();
                    userWorkVO.setCompEmpId(dentistId);
                    userWorkVO.setName(sysEmployeeInfo.getName());
                    List<UserWorkVO> sysEmployeeList = new ArrayList<>();
                    sysEmployeeList.add(userWorkVO);
                    return sysEmployeeList;
                }
            }
            return null;
        }
        // 获取当天的所有医生的排班（包含有排班和没有排班的医生）
        List<UserWorkVO> shiftWorkDatas = scheduleResultVO.getShiftWorkDatas();
        return shiftWorkDatas;
    }

    /**
     * 患者维度预约可视图排序
     * @param appointmentDimensionVoList 预约信息 要排序的列表
     * @param field 根据哪个字段排序
     * @param orderBy 升序asc 还是降序desc
     * @return 返回排序之后的列表
     */
    private List<AppointmentDimensionVo> sort(List<AppointmentDimensionVo> appointmentDimensionVoList, String field, String orderBy) {
        // 按患者预约数量升序排列
        // 按照患者数量排序
        String ORDER_PATIENTNUM = "patientNum";
        // 按照日期排序
        String ORDER_DATE = "date";
        // 升序
        String ORDER_BY_ASC = "asc";
        // 降序
        String ORDER_BY_DESC = "desc";
        if (StringHelper.isNotEmpty(appointmentDimensionVoList)) {
            if (ORDER_PATIENTNUM.equals(field) && ORDER_BY_ASC.equals(orderBy)) {
                return appointmentDimensionVoList
                        .stream()
                        .sorted(Comparator.comparing(AppointmentDimensionVo::getPatientNum,
                                Comparator.nullsLast(Integer::compareTo))).collect(Collectors.toList());

            } else if (ORDER_PATIENTNUM.equals(field) && ORDER_BY_DESC.equals(orderBy)) {
                // 按患者预约数量降序排列
                return appointmentDimensionVoList
                        .stream()
                        .sorted(Comparator.comparing(AppointmentDimensionVo::getPatientNum,
                                Comparator.nullsFirst(Integer::compareTo)).reversed()).collect(Collectors.toList());
            } else if (ORDER_DATE.equals(field) && ORDER_BY_ASC.equals(orderBy)) {
                // 按日期升序排列
                return appointmentDimensionVoList
                        .stream()
                        .sorted(Comparator.comparing(AppointmentDimensionVo::getCurrentDate,
                                Comparator.nullsLast(Date::compareTo))).collect(Collectors.toList());

            } else if (ORDER_DATE.equals(field) && ORDER_BY_DESC.equals(orderBy)) {
                // 按日期降序排列
                return appointmentDimensionVoList
                        .stream()
                        .sorted(Comparator.comparing(AppointmentDimensionVo::getCurrentDate,
                                Comparator.nullsFirst(Date::compareTo)).reversed()).collect(Collectors.toList());
            }
        }
        return appointmentDimensionVoList;
    }

    /**
     * 预约可视图列表（医生维度）
     * 通过日期、门诊id、查询医生预约情况
     * @param query 参数
     * @return 返回response
     */
    public ResponseResult<Page<AppointmentDimensionVo>> findAppointmentDentistDimensionByExample(PatientDimensionByDayQuery query){
        List<AppointmentDimensionVo> appointmentDentistDimensionVoList = new ArrayList<>();
        // 校验检索日期
        long startTime = query.getStartDate().getTime();
        long endTime = query.getEndDate().getTime();
        if (startTime > endTime) {
            // 返回 “ 开始日期不能大于结束日期” 提示
            return ResponseUtil.fail(AppointmentError.START_DATE_AFTER_END_DATE.getCode(),
                    AppointmentError.START_DATE_AFTER_END_DATE.getMessage(),null);
        }
        // 根据门诊ID获取该门诊所有可预约医生的ID
        List<Integer> enableDentistIds = this.enableAppointDentistIds(query.getOrgId());
        // 组合预约中心预约信息（包含预约医生，护士的排班以及预约人数）
        List<AppointmentDimensionVo> appointmentDimensionVos = this.dimensionAppointInfo(query,enableDentistIds);
        // 最后进行排序
        List<AppointmentDimensionVo> appointmentDimensionVosSort = this.sort(appointmentDimensionVos,query.getOrder(),query.getOrderBy());

        if (StringHelper.isEmpty(appointmentDimensionVosSort)) {
            return ResponseUtil.fail(AppointmentError.DENTIST_NOT_SCHEDULE.getCode(),AppointmentError.DENTIST_NOT_SCHEDULE.getMessage(),null);
        }

        if (StringHelper.isNotEmpty(appointmentDimensionVos)) {
            List<Integer> appointIds = new ArrayList<>();
            appointmentDimensionVos.forEach(appointDimVo->{
                List<AppointmentPatientCardVo> appointmentPatientCardVos = appointDimVo.getAppointmentPatientCardVos();
                if (StringHelper.isNotEmpty(appointmentPatientCardVos)) {
                    // 过滤出预约ID
                    List<Integer> collect = appointmentPatientCardVos.stream().map(AppointmentPatientCardVo::getId).collect(Collectors.toList());
                    if (StringHelper.isNotEmpty(collect)) {
                        // 将预约ID合并到appointIds
                        collect.stream().collect(Collectors.toCollection(()->appointIds));
                    }
                }
            });

            // 查询预约分解信息
            List<AppointmentSplitVo> appointmentSplits = appointmentSplitBiz.findAppointmentSplitByExampleBatch(query.getOrgId(), appointIds);
            List<Integer> assistentIds;
            if (StringHelper.isNotEmpty(appointmentSplits)) {
                // 分解预约助手ID列表
                assistentIds = appointmentSplits.stream().map(AppointmentSplitVo::getAssistantId).collect(Collectors.toList());
            } else {
                assistentIds = new ArrayList<>();
            }
            // 助手信息列表
            List<SysUserInfoDetail> assistentInfoList;
            if (StringHelper.isNotEmpty(assistentIds)) {
                assistentInfoList = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserIds(assistentIds);
            } else {
                assistentInfoList = new ArrayList<>();
            }

            log.info("==>预约ID集合：{}",appointIds);

            // 批量获取患者就诊信息
            List<TreatmentRecord> treatmentRecordList = remoteTreatmentServiceFeign.findTreatmentRecordListByAppointIds(appointIds);

            // 根据大医生id查询相关助手信息并且设置助手信息
            appointmentDimensionVos.forEach(appointmentDimensionVo -> {
                // 组合患者预约维度信息（预约患者信息+医生排班信息）
                AppointmentDimensionVo appointmentDimensionItem = this.combinationDentistDimensionVo(query.getOrgId(), appointmentDimensionVo,appointmentSplits,assistentInfoList,treatmentRecordList);
                Integer patientNum = appointmentDimensionItem.getPatientNum();
                if (patientNum == null) {
                    appointmentDimensionItem.setPatientNum(0);
                }
                // 最后将分解之后的整个大医生+助手放入到视图模型中
                appointmentDentistDimensionVoList.add(appointmentDimensionItem);
            });
        }


        // 按患者数量对医生降序排序
        Collections.sort(appointmentDentistDimensionVoList, (o1, o2) -> {
            if (o1.getPatientNum() != null && o2.getPatientNum() != null){
                if (o1.getPatientNum() > o2.getPatientNum()) {
                    return -1;
                }
                if (o1.getPatientNum() < o2.getPatientNum()) {
                    return 1;
                }
            }
            return 0;
        });
        // 设置分页
        PageUtil<AppointmentDimensionVo> paging = new PageUtil<>(query.getPageNum(), query.getPageSize());
        Page<AppointmentDimensionVo> pageAssistantData = paging.getPageAssistantData(appointmentDentistDimensionVoList);
        return ResponseUtil.success(pageAssistantData);
    }

    /**
     * 根据id查询预约
     * @param id 预约id
     * @return 返回预约视图
     */
    public AppointmentVo findAppointmentById(Integer id){
        AppointmentVo appointmentVo = mapper.findAppointmentById(id);
        if (null == appointmentVo) {
            return null;
        }
        // 设置医生信息
        Integer dentistId = appointmentVo.getDentistId();
        if (null != dentistId) {
            SysEmployee sysDentist = this.remoteSystemServiceFeign.findSysEmployeeById(dentistId);
            if (null != sysDentist) {
                String name = sysDentist.getName();
                appointmentVo.setDentistName(name);
            }
        }
        // 设置默认助手信息
        Integer assistantId = appointmentVo.getAssistantId();
        if (null != assistantId) {
            SysEmployee sysAssistant = this.remoteSystemServiceFeign.findSysEmployeeById(assistantId);
            if (null != sysAssistant) {
                String name = sysAssistant.getName();
                appointmentVo.setAssistantName(name);
            }
        }
        // 设置设备名称
        Integer clinicDeviceItemId = appointmentVo.getClinicDeviceItemId();
        if (null != clinicDeviceItemId) {
            DeviceItemVo deviceItemVo = this.clinicDeviceItemBiz.selectDeviceItemById(clinicDeviceItemId);
            if (null != deviceItemVo) {
                String name = deviceItemVo.getName();
                appointmentVo.setClinicDeviceItemName(name);
            }
        }
        // 设置门诊名称
        Integer deptRoomId = appointmentVo.getDeptRoomId();
        if (null != deptRoomId) {
            DepartmentRoom departmentRoom = this.remoteSystemServiceFeign.findDepartmentRoomById(deptRoomId);
            if (null != departmentRoom) {
                String name = departmentRoom.getName();
                appointmentVo.setDeptRoomName(name);
            }
        }
        // 设置患者信息
        PatientBaseInfo patientBaseInfo = this.remotePatientCentralServiceFeign.findPatientInfoById(appointmentVo.getPatientId());
        if (null != patientBaseInfo) {
            // 设置患者名字
            String name = patientBaseInfo.getName();
            appointmentVo.setPatientName(name);
            // 设置患者手机号
            String mobile = patientBaseInfo.getMobile();
            appointmentVo.setPatientMobile(mobile);
            // 设置患者年龄
            Integer age = patientBaseInfo.getAge();
            appointmentVo.setAge(age);
            // 设置患者性别
            Byte gender = patientBaseInfo.getGender();
            appointmentVo.setGender(gender);
        }
        // 查询预约分解信息
        AppointmentSplitQuery splitQuery = new AppointmentSplitQuery();
        splitQuery.setAppointmentId(appointmentVo.getId());
        splitQuery.setAppointDate(appointmentVo.getAppointDate());
        splitQuery.setOrgId(appointmentVo.getOrgId());
        List<AppointmentSplitVo> splitVos = this.appointmentSplitBiz.findAppointmentSplitByExample(splitQuery);
        appointmentVo.setSplitList(splitVos);
        return appointmentVo;
    }

    /**
     * 确认预约
     * @param id  预约id
     * @return 返回结果
     */
    public ResponseResult confirmAppointment(Integer id, Boolean flag){
        Appointment appointment = mapper.selectByPrimaryKey(id);
        AppointOperationModel appointOperationModel = new AppointOperationModel();
        if (appointment == null){
            return ResponseUtil.fail(AppointmentError.APPOINT_DATA_NOT_EXIST.getCode(),AppointmentError.APPOINT_DATA_NOT_EXIST.getMessage(),null);
        }
        appointOperationModel.setBeforeOperation(appointment.getConfirmStatus()?"确认":"未确认");
        appointment.setConfirmStatus(flag == null ? true : flag);
        appointment.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        appointment.setUpdName(BaseContextHandler.getName());
        appointment.setUpdTime(new Date(System.currentTimeMillis()));
        int result = mapper.updateByPrimaryKeySelective(appointment);
        if (result > 0) {
            // 发送消息更新中间表就诊流程
            rabbitMqServiceFeign.sendMessage(id,0,1, BaseTreatmentProcess);

            Boolean confirmStatus = appointment.getConfirmStatus();
            appointOperationModel.setOperateType(confirmStatus? (byte) 3 : 4);
            appointOperationModel.setAppointmentId(appointment.getId());
            appointOperationModel.setOrgId(Integer.valueOf(BaseContextHandler.getUserID()));
            appointOperationModel.setAfterOperation(appointment.getConfirmStatus()?"确认":"未确认");
            Integer recordResult = appointOperateRecordBiz.insertAppointmentOperateRecord(appointOperationModel);
            if (recordResult > 0) {
                return ResponseUtil.success();
            }
        }
        return ResponseUtil.fail(AppointmentError.APPOINT_CONFIRM.getCode(),AppointmentError.APPOINT_CONFIRM.getMessage(),null);
    }

    /**
     * 根据条件查询预约列表
     * @param query  条件查询参数
     * @return 预约视图列表
     */
    public List<AppointmentVo> findAppointmentByExample(AppointmentQuery query){
        return mapper.findAppointmentByExample(query);
    }

    /**
     * 根据患者id查询患者预约列表
     * @param patientId 患者id
     * @return 患者预约列表
     */
    public List<Appointment> findAppointmentByPatientId(Integer patientId){
        return mapper.findAppointmentByPatientId(patientId);
    }

    /**
     * 根据日期查询失约患者名单
     * @param currentDate  当前日期
     * @return 预约列表
     */
    public List<Appointment> findMissedAppointmentByDate(Date currentDate){
        return mapper.findMissedAppointmentByDate(currentDate);
    }

    /**
     * 定时任务设置失约患者状态, 每天01：00：00执行
     * @return 返回处理个数
     */
    public Integer missedAppointmentsStatusSchedule(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        List<Appointment> missedAppointments = this.findMissedAppointmentByDate(calendar.getTime());
        missedAppointments.forEach(appointment -> {
            appointment.setAppointStatus((byte) 3);
            mapper.updateByPrimaryKeySelective(appointment);
        });
        return missedAppointments.size();
    }

    /**
     * 添加预约时，检查预约当日预约的医生和助手是否排班
     * @param appointmentBaseModel    预约参数封装表单
     * @return Map<String,Object>
     */
    private ResponseResult checkScheduling(AppointmentBaseModel appointmentBaseModel){
        // 获取预约医生Id
        Integer dentistId = appointmentBaseModel.getDentistId();
        // 获取分解列表
        List<AppointmentSplitBaseInfo> splitList = appointmentBaseModel.getSplitList();
        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr = sdf.format(appointmentBaseModel.getAppointDate());
        employeeScheduleQueryForm.setStartDate(startDateStr);
        employeeScheduleQueryForm.setClinicId(appointmentBaseModel.getOrgId());
        // 将排班结束日期退后一天
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(appointmentBaseModel.getAppointDate());
        calendar.add(Calendar.DAY_OF_MONTH,1);
        String endDateStr = sdf.format(calendar.getTime());
        employeeScheduleQueryForm.setEndDate(endDateStr);
        if (dentistId != null){
            employeeScheduleQueryForm.setUserId(dentistId);
            // 获取排班列表
            EmployeeScheduleResultVO employeeScheduleResult = employeeAttendServiceFeign.findList(employeeScheduleQueryForm);
            if (employeeScheduleResult != null){
                List<UserWorkVO> shiftWorkDatas = employeeScheduleResult.getShiftWorkDatas();
                // 预约医生没有排班，返回空
                if (!StringHelper.isEmpty(shiftWorkDatas)) {
                    UserWorkVO userWorkVO = shiftWorkDatas.get(0);
                    List<WorkDayVO> days = userWorkVO.getDays();
                    if (!StringHelper.isEmpty(days)) {
                        WorkDayVO workDayVO = days.get(0);
                        Integer id = workDayVO.getId();
                        if (null == id) {
                            return ResponseUtil.fail(AppointmentError.DENTIST_NOT_WORK.getCode(), AppointmentError.DENTIST_NOT_WORK.getMessage(), null);
                        }
                    }

                }
            }
        }
        // 检查分解的助手是否排班
        if (!StringHelper.isEmpty(splitList)) {
            for (AppointmentSplitBaseInfo baseInfo : splitList) {
                Integer assistantId = baseInfo.getAssistantId();
                employeeScheduleQueryForm.setUserId(assistantId);
                // 获取排班列表
                EmployeeScheduleResultVO employeeScheduleResult = employeeAttendServiceFeign.findList(employeeScheduleQueryForm);
                if (employeeScheduleResult != null){
                    List<UserWorkVO> shiftWorkDatas = employeeScheduleResult.getShiftWorkDatas();
                    // 预约助手没有排班，返回空
                    if (StringHelper.isEmpty(shiftWorkDatas)) {
                        return ResponseUtil.fail(AppointmentError.DENTIST_NOT_WORK.getCode(),AppointmentError.DENTIST_NOT_WORK.getMessage(),null);
                    }
                }

            }

        }
        // 成功返回null
        return null;
    }

    /**
     * 添加预约时检查是否存在预约冲突
     * @param appointmentForm 表单
     * @return Map<String,Object>
     * @throws ParseException 异常抛出
     */
    private ResponseResult checkConflict(AppointmentBaseModel appointmentForm) throws ParseException {
        // 获取患者id、医生id、设备id、预约日期、时间、时长
        Integer patientId = appointmentForm.getPatientId();
        Integer dentistId = appointmentForm.getDentistId();
        Integer deviceId = appointmentForm.getClinicDeviceItemId();
        Integer assistantId = appointmentForm.getAssistantId();
        // 获取预约分解的助手列表
        List<AppointmentSplitBaseInfo> splitList = appointmentForm.getSplitList();
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

        // 判断患者预约是否存在冲突
        if (patientId != null) {
            ResponseResult responseResult = this.patientConflictInfo(null, patientId, appointStartTime, appointEndTime);
            if (null != responseResult) {
                return responseResult;
            }
        }
        // 判断医生预约是否存在冲突
        if (dentistId != null){
            ResponseResult responseResult = this.dentistConflictInfo(null, dentistId, appointStartTime, appointEndTime);
            if (null != responseResult) {
                return responseResult;
            }
        }

        // 判断助手预约是否存在冲突
        if (assistantId != null){
            ResponseResult responseResult = this.assistantConflictInfo(null, assistantId, appointStartTime, appointEndTime);
            if (null != responseResult) {
                return responseResult;
            }
        }

        // 判断预约分解助手是否冲突
        if (!StringHelper.isEmpty(splitList)){
            ResponseResult responseResult = this.splitAssistantConflictInfo(null, assistantId, appointStartTime, appointEndTime);
            if (null != responseResult) {
                return responseResult;
            }
        }

        // 判断设备预约是否存在冲突
        if (deviceId != null){
            ResponseResult responseResult = this.deviceConflictInfo(null, deviceId, appointStartTime, appointEndTime);
            return responseResult;
        }
        return null;
    }

    /**
     * 患者冲突信息
     * @param id 预约ID id==null 时 添加预约冲突检查；id!=null 时，编辑预约冲突检查
     * @param patientId 患者ID
     * @param appointStartTime 预约开始时间
     * @param appointEndTime 预约结束时间
     * @return 冲突则返回冲突信息，否则返回null
     */
    private ResponseResult patientConflictInfo(Integer id, Integer patientId, Date appointStartTime, Date appointEndTime) {
        List<AppointConflictInfoVo> patientList;
        if (null != id) {
            patientList = mapper.editCheckPatientConflict(id,patientId,appointStartTime,appointEndTime);
        } else {
            patientList = mapper.findAppointListByPatientIdAndAppointStartTimeAndAppointEndTime(
                    patientId, appointStartTime, appointEndTime);
        }
        if (!StringHelper.isEmpty(patientList)){
            StringBuilder errBuffer = new StringBuilder();
            patientList.forEach(appointConflictInfoVo -> {
                OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(appointConflictInfoVo.getOrgId());
                if (organizationInfo != null){
                    appointConflictInfoVo.setClinicName(organizationInfo.getName());
                    appointConflictInfoVo.setClinicNumber(organizationInfo.getClinicNumber());
                    appointConflictInfoVo.setAbbreviation(organizationInfo.getAbbreviation());
                }
                // 设置患者名字
                PatientBaseInfo patientInfo = this.remotePatientCentralServiceFeign.findPatientInfoById(patientId);
                if (null != patientInfo) {
                    appointConflictInfoVo.setPatientName(patientInfo.getName());
                }
                String errMsg = AppointmentError.APPOINT_PATIENT_EXIST.paddingParams(
                        appointConflictInfoVo.getPatientName(),
                        appointConflictInfoVo.getClinicName(),
                        appointConflictInfoVo.getAppointPeriod());
                boolean bresult = StringHelper.inStringIgnoreCase(errBuffer.toString(), errMsg);
                if (!bresult) {
                    errBuffer.append(errMsg);
                }

            });
            // 预约冲突返回冲突信息
            return ResponseUtil.fail(AppointmentError.APPOINT_PATIENT_EXIST.getCode(),
                    errBuffer.toString(),
                    null);
        }
        return null;
    }

    /**
     * 预约医生冲突信息
     * @param id 预约ID id==null 时 添加预约冲突检查；id!=null 时，编辑预约冲突检查
     * @param dentistId 医生ID
     * @param appointStartTime 预约开始时间
     * @param appointEndTime 预约结束时间
     * @return 有冲突返回冲突信息，否则返回null
     */
    private ResponseResult dentistConflictInfo(Integer id, Integer dentistId, Date appointStartTime, Date appointEndTime) {
        List<AppointConflictInfoVo> dentisList;
        if (null != id) {
            // 编辑预约医生冲突检测
            dentisList = mapper.editCheckDentistConflict(id,dentistId,appointStartTime,appointEndTime);
        } else {
            // 添加预约医生冲突检测
            dentisList = mapper.findAppointListByDentistIdAndAppointStartTimeAndAppointEndTime(
                    dentistId, appointStartTime, appointEndTime);
        }
        if (!StringHelper.isEmpty(dentisList)){
            StringBuilder errBuffer = new StringBuilder();
            dentisList.forEach(appointConflictInfoVo -> {
                OrganizationInfo organizatioinInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(appointConflictInfoVo.getOrgId());
                if (organizatioinInfo != null){
                    appointConflictInfoVo.setClinicName(organizatioinInfo.getName());
                    appointConflictInfoVo.setClinicNumber(organizatioinInfo.getClinicNumber());
                    appointConflictInfoVo.setAbbreviation(organizatioinInfo.getAbbreviation());
                }
                Integer conflictPatientId = appointConflictInfoVo.getPatientId();
                // 设置患者名字
                PatientBaseInfo patientInfo = this.remotePatientCentralServiceFeign.findPatientInfoById(conflictPatientId);
                if (null != patientInfo) {
                    appointConflictInfoVo.setPatientName(patientInfo.getName());
                }
                // 设置医生名称
                SysEmployee dentistInfo = this.remoteSystemServiceFeign.findSysEmployeeById(dentistId);
                if (null != dentistInfo) {
                    appointConflictInfoVo.setDentistName(dentistInfo.getName());
                }
                String errMsg = AppointmentError.APPOINT_DENTIST_EXIST.paddingParams(
                        appointConflictInfoVo.getDentistName(),
                        appointConflictInfoVo.getClinicName(),
                        appointConflictInfoVo.getAppointPeriod(),
                        appointConflictInfoVo.getPatientName());

                boolean bresult = StringHelper.inStringIgnoreCase(errBuffer.toString(), errMsg);
                if(!bresult) {
                    errBuffer.append(errMsg);
                }

            });
            // 预约冲突返回冲突信息
            return ResponseUtil.fail(AppointmentError.APPOINT_DENTIST_EXIST.getCode(),
                    errBuffer.toString(),
                    null);
        }
        return null;
    }

    /**
     * 默认助手冲突信息
     * @param id 预约ID id==null 时 添加预约冲突检查；id!=null 时，编辑预约冲突检查
     * @param assistantId 助手ID
     * @param appointStartTime 预约开始时间
     * @param appointEndTime 预约结束时间
     * @return 有冲突返回冲突信息，否则返回null
     */
    private ResponseResult assistantConflictInfo(Integer id, Integer assistantId, Date appointStartTime, Date appointEndTime) {
        List<AppointConflictInfoVo> dentisList;
        if(null != id){
            dentisList = mapper.editCheckAssistantConflict(id,assistantId,appointStartTime,appointEndTime);
        } else {
            dentisList = mapper.findAppointListByAssistantIdAndAppointStartTimeAndAppointEndTime(
                    assistantId, appointStartTime, appointEndTime);
        }
        if (!StringHelper.isEmpty(dentisList)){
            StringBuilder errBuffer = new StringBuilder();
            dentisList.forEach(appointConflictInfoVo -> {
                OrganizationInfo organizatioinInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(appointConflictInfoVo.getOrgId());
                if (organizatioinInfo != null){
                    appointConflictInfoVo.setClinicName(organizatioinInfo.getName());
                    appointConflictInfoVo.setClinicNumber(organizatioinInfo.getClinicNumber());
                    appointConflictInfoVo.setAbbreviation(organizatioinInfo.getAbbreviation());
                }
                Integer conflictPatientId = appointConflictInfoVo.getPatientId();
                // 设置助手信息
                SysEmployee assistantInfo = this.remoteSystemServiceFeign.findSysEmployeeById(assistantId);
                if (null != assistantInfo) {
                    appointConflictInfoVo.setAssistantName(assistantInfo.getName());
                }
                // 设置患者名字
                PatientBaseInfo patientInfo = this.remotePatientCentralServiceFeign.findPatientInfoById(conflictPatientId);
                if (null != patientInfo) {
                    appointConflictInfoVo.setPatientName(patientInfo.getName());
                }
                String errMsg = AppointmentError.APPOINT_ASSISTANT_EXIST.paddingParams(
                        appointConflictInfoVo.getAssistantName(),
                        appointConflictInfoVo.getClinicName(),
                        appointConflictInfoVo.getAppointPeriod(),
                        appointConflictInfoVo.getPatientName());
                boolean bresult = StringHelper.inStringIgnoreCase(errBuffer.toString(), errMsg);
                if (!bresult) {
                    errBuffer.append(errMsg);
                }
            });
            // 预约助手冲突返回冲突信息
            return ResponseUtil.fail(AppointmentError.APPOINT_ASSISTANT_EXIST.getCode(),
                    errBuffer.toString(),
                    null);
        }
        return null;
    }

    /**
     * 分解助手冲突信息
     * @param id 预约ID id==null 时 添加预约冲突检查；id!=null 时，编辑预约冲突检查
     * @param assistantId 助手ID
     * @param appointStartTime 预约开始时间
     * @param appointEndTime 预约结束时间
     * @return 冲突返回冲突信息，否则返回null
     */
    private ResponseResult splitAssistantConflictInfo(Integer id, Integer assistantId, Date appointStartTime, Date appointEndTime) {
        List<AppointConflictInfoVo> dentisList;
        if (null != id) {
            dentisList = mapper.editCheckAssistantConflict(id,assistantId,appointStartTime,appointEndTime);
        } else {
            dentisList = mapper.findByIdAndStartTimeAndEndTime(
                    assistantId, appointStartTime, appointEndTime);
        }
        if (!StringHelper.isEmpty(dentisList)){
            StringBuilder errBuffer = new StringBuilder();
            dentisList.forEach(appointConflictInfoVo -> {
                OrganizationInfo organizatioinInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(appointConflictInfoVo.getOrgId());
                if (organizatioinInfo != null){
                    appointConflictInfoVo.setClinicName(organizatioinInfo.getName());
                    appointConflictInfoVo.setClinicNumber(organizatioinInfo.getClinicNumber());
                    appointConflictInfoVo.setAbbreviation(organizatioinInfo.getAbbreviation());
                }
                Integer conflictPatientId = appointConflictInfoVo.getPatientId();
                // 设置患者名字
                PatientBaseInfo patientInfo = this.remotePatientCentralServiceFeign.findPatientInfoById(conflictPatientId);
                if (null != patientInfo) {
                    appointConflictInfoVo.setPatientName(patientInfo.getName());
                }
                String errMsg = AppointmentError.SPLIT_ASSISTANT_EXIST.paddingParams(
                        appointConflictInfoVo.getAssistantName(),
                        appointConflictInfoVo.getClinicName(),
                        appointConflictInfoVo.getAppointPeriod(),
                        appointConflictInfoVo.getPatientName());
                boolean bresult = StringHelper.inStringIgnoreCase(errBuffer.toString(), errMsg);
                if (!bresult) {
                    errBuffer.append(errMsg);
                }
            });
            // 预约分解助手冲突返回冲突信息
            return ResponseUtil.fail(AppointmentError.SPLIT_ASSISTANT_EXIST.getCode(),
                    errBuffer.toString(),
                    null);
        }
        return null;
    }

    /**
     * 设备冲突信息
     * @param id 预约ID id==null 时 添加预约冲突检查；id!=null 时，编辑预约冲突检查
     * @param deviceId 设备ID
     * @param appointStartTime 预约开始信息
     * @param appointEndTime 预约结束信息
     * @return 冲突返回冲突信息，否则返回null
     */
    private ResponseResult deviceConflictInfo(Integer id, Integer deviceId, Date appointStartTime, Date appointEndTime) {
        List<AppointConflictInfoVo> deviceList;
        // id 不为null 时，编辑预约冲突检查，否则添加预约冲突检查
        if (null != id) {
            deviceList = mapper.editCheckDeviceConflict(id,deviceId,appointStartTime,appointEndTime);
        } else {
            deviceList = mapper.findAppointListByDeviceIdAndAppointStartTimeAndAppointEndTime(
                    deviceId, appointStartTime, appointEndTime);
        }
        if (!StringHelper.isEmpty(deviceList)){
            StringBuilder errBuffer = new StringBuilder();
            deviceList.forEach(appointConflictInfoVo -> {
                OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(appointConflictInfoVo.getOrgId());
                if (organizationInfo != null){
                    appointConflictInfoVo.setClinicName(organizationInfo.getName());
                    appointConflictInfoVo.setAbbreviation(organizationInfo.getAbbreviation());
                    appointConflictInfoVo.setClinicNumber(organizationInfo.getClinicNumber());
                }
                // 设置设备信息
                DeviceItemVo deviceItemVo = this.clinicDeviceItemBiz.selectDeviceItemById(deviceId);
                if (null != deviceItemVo) {
                    appointConflictInfoVo.setClinicDeviceItemName(deviceItemVo.getName() + "("+ deviceItemVo.getNumber() +")");
                }
                Integer conflictPatientId = appointConflictInfoVo.getPatientId();
                // 设置患者名字
                PatientBaseInfo patientInfo = this.remotePatientCentralServiceFeign.findPatientInfoById(conflictPatientId);
                if (null != patientInfo) {
                    appointConflictInfoVo.setPatientName(patientInfo.getName());
                }
                String errMsg = AppointmentError.APPOINT_DEVICE_EXIST.paddingParams(
                        appointConflictInfoVo.getPatientName(),
                        appointConflictInfoVo.getAppointPeriod());
                boolean bresult = StringHelper.inStringIgnoreCase(errBuffer.toString(), errMsg);
                if (!bresult) {
                    errBuffer.append(errMsg);
                }
            });
            // 预约冲突返回冲突信息
            return ResponseUtil.fail(AppointmentError.APPOINT_DEVICE_EXIST.getCode(),
                    errBuffer.toString(),
                    null);
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
            throw new ClientServiceException(AppointmentError.DATA_FROM_MODEL_EXP.getMessage(),AppointmentError.DATA_FROM_MODEL_EXP.getCode());
        }
        // 将form表单转化为appointment实体
        Appointment appointment = EntityUtils.build(form, Appointment.class);
        // 获取预约日期、时间、时长
        Date appointDate = appointment.getAppointDate();
        String appointTimeStr = appointment.getAppointTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date appointTime;
        try {
            appointTime = simpleDateFormat.parse(appointTimeStr);
        } catch (ParseException e) {
            throw new ClientServiceException("[时间格式转换异常]："+e.getMessage(),OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
        }
        Integer time = appointment.getAppointDuration();
        // 获取预约开始时间的毫秒值
        long ms = appointDate.getTime() + appointTime.getTime();
        // 转换预约开始时间（消除东八区时间的影响，加上28800000毫秒）
        DateTime startTime = new DateTime(ms + 28800000L);
        Date appointStartTime = startTime.toDate();
        // 计算预约结束时间(预约开始时间+预约时长)
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
        TreatmentRecord treatmentRecord = new TreatmentRecord();
        treatmentRecord.setPatientId(appointment.getPatientId());
        List<TreatmentRecord> treatmentRecords = remoteTreatmentServiceFeign.findTreatmentRecordList(treatmentRecord);
        byte appointType = 0;
        if (treatmentRecords!=null && treatmentRecords.size()>= 1) {
            appointType = 1;
        }
        appointment.setAppointType(appointType);
        appointment.setInservice(true);
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
     * @return Map
     */
    private ResponseResult editCheckConflict(Integer id,  AppointmentBaseForm appointmentForm) {
        // 获取患者id、医生id、设备id、预约日期、时间、时长
        Integer patientId = appointmentForm.getPatientId();
        Integer dentistId = appointmentForm.getDentistId();
        Integer deviceId = appointmentForm.getClinicDeviceItemId();
        Integer assistantId = appointmentForm.getAssistantId();
        // 预约分解列表
        List<AppointmentSplitBaseInfo> splitList = appointmentForm.getSplitList();
        Date appointDate = appointmentForm.getAppointDate();
        // 转换字符串预约时间为Date类型
        String appointTimeStr = appointmentForm.getAppointTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date appointTime;
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
        if (patientId != null) {
            // 判断患者预约是否存在冲突
            ResponseResult responseResult = this.patientConflictInfo(id, patientId, appointStartTime, appointEndTime);
            if (null != responseResult) {
                return responseResult;
            }
        }
        // 判断医生预约是否存在冲突
        if (dentistId != null) {
            ResponseResult responseResult = this.dentistConflictInfo(id, dentistId, appointStartTime, appointEndTime);
            if (null != responseResult) {
                return responseResult;
            }
        }
        // 判断助手预约是否存在冲突
        if (assistantId != null) {
            ResponseResult responseResult = this.assistantConflictInfo(id, assistantId, appointStartTime, appointEndTime);
            if (null != responseResult) {
                return responseResult;
            }
        }

        // 判断预约分解助手是否冲突
        if (!StringHelper.isEmpty(splitList)){
            ResponseResult responseResult = this.splitAssistantConflictInfo(id, assistantId, appointStartTime, appointEndTime);
            if (null != responseResult) {
                return responseResult;
            }
        }

        // 判断设备预约是否存在冲突
        if (deviceId != null) {
            ResponseResult responseResult = this.deviceConflictInfo(id, deviceId, appointStartTime, appointEndTime);
            return responseResult;
        }
        return null;
    }

    /**
     * 预约列表合并导出
     * @param response  HttpServletResponse
     * @param exportQuery  预约查询Form
     * @throws IOException I/O异常
     */
    public void exportAppointListToExcel(HttpServletResponse response, AppointListExportQuery exportQuery) throws IOException {
        // 将参数转化为预约列表查询的参数实体
        AppointListQuery listQuery = EntityUtils.build(exportQuery,AppointListQuery.class);
        PageInfo pageInfo = this.findAppointmentListByExample(listQuery);
        List<AppointmentListItemVo> appointmentListItemVoList = pageInfo.getList();
        // 预约列表为空抛出异常
        if (StringHelper.isNotEmpty(appointmentListItemVoList)) {
            // 预约列表信息
            List<AppointListExportVo> appointListExportVos = new ArrayList<>();
            if (appointmentListItemVoList != null && !appointmentListItemVoList.isEmpty()) {
                List<AppointListExportVo> appointListExportVoList = new ArrayList<>();
                // 设置预约患者信息
                appointmentListItemVoList.forEach(appointmentListItemVo -> {
                    AppointListExportVo appointListExportVo = appointListItemTransformExportEntity(appointmentListItemVo);
                    appointListExportVoList.add(appointListExportVo);
                });
                // 对预约列表信息排序
                appointListExportVos = appointListExportVoList.stream().sorted(Comparator.comparingInt(AppointListExportVo::getDentistId)).collect(Collectors.toList());
            }

            Integer orgId = exportQuery.getOrgId();

            SimpleDateFormat exportAppointDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String exportAppointDate = exportAppointDateFormat.format(exportQuery.getAppointDate());
            // 合并行
            List<CellRangeAddress> mergeCells = new ArrayList<>();
            // 将列表中第一个医生的名字作为初始值
            String firstDentistName = appointListExportVos.get(0).getDentistName();
            int firstRow = 1;
            int lastRow = 1;
            int firstCol = 0;
            int lastCol = 0;
            boolean isSameName = false;
            for (int index = 0; index < appointListExportVos.size() - 1; index++) {
                String nextDentistName = appointListExportVos.get(index + 1).getDentistName();
                // 如果第一个医生和下一个医生是同一个医生，lastRow + 1
                if (!firstDentistName.equalsIgnoreCase(nextDentistName)) {
                    // 判断是否存在合并行， 如果lastRow - firstRow > 1 说明存在合并行 ，进行合并
                    if (lastRow - firstRow >= 1 && isSameName) {
                        CellRangeAddress mergeCell = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
                        mergeCells.add(mergeCell);
                        isSameName = false;
                    }
                    // 更新第一行指针到最后一行
                    firstRow = ++lastRow;
                    firstDentistName = nextDentistName;
                } else {
                    isSameName = true;
                    lastRow++;
                }
            }
            ExcelUtil<AppointListExportVo> appointExcelExport = new ExcelUtil<>(AppointListExportVo.class);
            appointExcelExport.setMergeRegion(mergeCells);
            OrganizationInfo orgInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
            String orgName = null;
            if (orgInfo != null) {
                orgName = orgInfo.getName();
            }
            // 导出excel文件名  "XXX门诊预约报表（2020-06-10）"
            String excelName = orgName + "预约报表(" + exportAppointDate + ")";
            appointExcelExport.exportExcel(response, appointListExportVos, excelName, excelName);
        }
    }

    /**
     * 将预约列表中的每一条预约转化为导出Excel对象实体
     * @param appointmentListItemVo 预约列表中的每一条预约
     * @return Excel对象实体
     */
    private AppointListExportVo appointListItemTransformExportEntity (AppointmentListItemVo appointmentListItemVo) {
        AppointListExportVo appointListExportVo = new AppointListExportVo();

        appointListExportVo.setDentistId(appointmentListItemVo.getDentistId());
        appointListExportVo.setDentistName(appointmentListItemVo.getDentistName());
        appointListExportVo.setMedicalNumber(appointmentListItemVo.getMedicalNumber());
        appointListExportVo.setPatientName(appointmentListItemVo.getPatientName());
        appointListExportVo.setMobile(appointmentListItemVo.getMobile());
        appointListExportVo.setAssistantName(appointmentListItemVo.getAssistantName());
        appointListExportVo.setAppointType(appointmentListItemVo.getFirstVisit());
        appointListExportVo.setClinicDeptRoomName(appointmentListItemVo.getClinicDeptRoomName());
        appointListExportVo.setAppointTime(appointmentListItemVo.getAppointTime());
        appointListExportVo.setAppointDuration(appointmentListItemVo.getAppointDuration());
        appointListExportVo.setAppointContent(appointmentListItemVo.getAppointContent());
        appointListExportVo.setArrears(appointmentListItemVo.getArrears());
        appointListExportVo.setConfirmStatus(appointmentListItemVo.getConfirmStatus());
        appointListExportVo.setRemarks(appointmentListItemVo.getRemarks());
        appointListExportVo.setPatientRemarks(appointmentListItemVo.getPatientRemark());

        // 预约操作记录参数
        AppointOperationQuery operationQuery = new AppointOperationQuery();
        operationQuery.setOrgId(appointmentListItemVo.getOrgId());
        operationQuery.setAppointmentId(appointmentListItemVo.getId());
        List<AppointOperationRecordVo> appointOperationRecords = appointOperateRecordBiz.findAppointOperationRecordByExample(operationQuery);
        if (appointOperationRecords != null && !appointOperationRecords.isEmpty()){
            AppointOperationRecordVo appointOperationRecordVo = appointOperationRecords.get(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String crtTime = dateFormat.format(appointOperationRecordVo.getCrtTime());
            String operationRecordContent = "";

            Byte operateType = appointOperationRecordVo.getOperateType();
            switch (operateType){
                case 0:
                    operationRecordContent = "[" + crtTime + "]" + appointOperationRecordVo.getCrtName() + "新建了这条预约";
                    break;
                case 1:
                    String beforeOperation = appointOperationRecordVo.getBeforeOperation();
                    String afterOperation = appointOperationRecordVo.getAfterOperation();
                    operationRecordContent = "[" + crtTime + "]" +  "修改了【" + appointOperationRecordVo.getOperateItem() +
                            "】，将\"" + (StringHelper.isEmpty(beforeOperation) ? "无" : beforeOperation) + "" +
                            "\"改成了\"" + (StringHelper.isEmpty(afterOperation) ? "无" : afterOperation) + "\"";
                    break;
                case 2:
                    operationRecordContent = "[" + crtTime + "]" + appointOperationRecordVo.getCrtName() + "取消了这条预约";
                    // 设置取消预约原因
                    appointListExportVo.setCancleReasion(appointOperationRecordVo.getRemarks());
                    break;
                case 3:
                    operationRecordContent = "[" + crtTime + "]" + appointOperationRecordVo.getCrtName() + "确认了这条预约";
                    break;
                case 4:
                    operationRecordContent = "[" + crtTime + "]" + appointOperationRecordVo.getCrtName() + "取消了这条预约的确认";
                    break;
                default:
            }
            appointListExportVo.setAppointOperationRecord(operationRecordContent);
        }
        return appointListExportVo;
    }

    /**
     * 根据条件查询预约未到患者信息列表
     * 注意：如果后期优化：可以将该方法中的注释取消，同时涉及到的相关接口都需要更改为操作redis缓冲
     * @param queryForm 查询条件
     * @return list
     */
    public PageInfo<AppointmentUnDonePatientInfoVO> findUnComingAppointmentList(
            AppointmentCurrentListQuery queryForm) {
        PageInfo<AppointmentUnDonePatientInfoVO> result = null;
/*        Integer dentistId = queryForm.getDentistId();
        String redisKey = RedisConstants.setKey(RedisConstants.REDIS_KEY_APPOINTMENT_UN_DONE,
                queryForm.getCurrentDate(),
                String.valueOf(queryForm.getId()),
                dentistId != null ? String.valueOf(dentistId) : "*",
                "*");

        Set<String> keys = redisUtils.keys(redisKey);
        if (StringHelper.isNotEmpty(keys)) {
            log.info("====================================================================================================");
            log.info("==> 【RedisKey common】: {}",redisKey);
            log.info("==> 【当前类】：com.yunya.modules.appointment.biz.web.AppointmentBiz");
            log.info("==> 【当前方法】：public PageInfo<AppointmentUnDonePatientInfoVO> findUnComingAppointmentList(...)");
            log.info("==> 【数据来源】：Redis Cache");
            log.info("==> 【params】：{}",keys.toArray());
            log.info("====================================================================================================");
            result = new ArrayList<>();
            for (String key:keys) {
                AppointmentUnDonePatientInfoVO appointmentUnDonePatientInfoVO =
                        redisUtils.get(key, AppointmentUnDonePatientInfoVO.class);
                result.add(appointmentUnDonePatientInfoVO);
            }
        } else {
            log.info("====================================================================================================");
            log.info("==> 【当前类】：com.yunya.modules.appointment.biz.web.AppointmentBiz");
            log.info("==> 【当前方法】：public PageInfo<AppointmentUnDonePatientInfoVO> findUnComingAppointmentList(...)");
            log.info("==> 【数据来源】：Database");
            log.info("==> 【params】：{}",queryForm);
            log.info("====================================================================================================");
            // 从数据库中查询
            result = this.findUnComingAppointmentListFromDB(queryForm);
        }*/

        log.info("====================================================================================================");
        log.info("==> 【当前类】：com.yunya.modules.appointment.biz.web.AppointmentBiz");
        log.info("==> 【当前方法】：public PageInfo<AppointmentUnDonePatientInfoVO> findUnComingAppointmentList(...)");
        log.info("==> 【数据来源】：Database");
        log.info("==> 【params】：{}",queryForm);
        log.info("====================================================================================================");
        // 从数据库中查询
        result = this.findUnComingAppointmentListFromDB(queryForm);
        return result;
    }

    /**
     * 从数据库中查询预约未到患者相关信息
     * @param queryForm 查询参数封装
     */
    private PageInfo<AppointmentUnDonePatientInfoVO> findUnComingAppointmentListFromDB(AppointmentCurrentListQuery queryForm) {
        String currentDate = queryForm.getCurrentDate();
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(),queryForm.getPageSize());
        }
        List<AppointmentUnDonePatientInfoVO> resultList =
                mapper.selectAppointmentUnDonePatientInfoList(queryForm);
        if (StringHelper.isNotEmpty(resultList)) {
            // 预约ID集合
            List<Integer> appointIds = new ArrayList<>();
            List<Integer> assistentIds = new ArrayList<>();
            List<Integer> dentistIds = new ArrayList<>();
            List<Integer> deptRoomIds = new ArrayList<>();
            List<Integer> patientIds = new ArrayList<>();
            resultList.forEach(vo -> {
                patientIds.add(vo.getPatientId());
                deptRoomIds.add(vo.getAppointDeptRoomId());
                dentistIds.add(vo.getAppointDentistId());
                appointIds.add(vo.getId());
                assistentIds.add(vo.getAppointAssistantId());
            });
            // 设置患者信息
            setPatientInfo(resultList, patientIds,appointIds,dentistIds,assistentIds,deptRoomIds);
        }

        // 设置redis缓冲
        /*if (StringHelper.isNotEmpty(resultList)) {
            resultList.forEach(appointmentUnDonePatientInfoVO -> {
                Integer patientId = appointmentUnDonePatientInfoVO.getPatientId();
                Integer id = appointmentUnDonePatientInfoVO.getId();
                String redisKey = RedisConstants.setKey(RedisConstants.REDIS_KEY_APPOINTMENT_UN_DONE,
                        queryForm.getCurrentDate(),
                        String.valueOf(id),
                        String.valueOf(queryForm.getDentistId()),
                        String.valueOf(patientId));
                if (!redisUtils.hasKey(redisKey)) {
                    redisUtils.set(redisKey,appointmentUnDonePatientInfoVO,3600);
                }
            });
        }*/
        return new PageInfo<>(resultList);
    }

    /**
     * 计算预约未到列表数量
     *
     * @param queryForm 查询条件
     * @return
     */
    public Integer countAppointNotArrived(AppointmentCurrentListQuery queryForm){
        return mapper.countAppointNotArrived(queryForm);
    }

    /**
     * 计算后续指定患者的预约数量
     * @param patientId 患者ID
     * @return 返回预约数量
     */
    public Integer countNextAppoint(Integer patientId) {
        return mapper.selectCountNextAppoint(patientId);
    }

    /**
     * 设置候诊患者患者信息
     * @param vos 患者候诊
     * @param patientIds 患者ID集合
     */
    private void setPatientInfo(List<AppointmentUnDonePatientInfoVO> vos,
                                List<Integer> patientIds,
                                List<Integer> appointIds,
                                List<Integer> dentistIds,
                                List<Integer> assistantIds,
                                List<Integer> deptRoomIds) {
        // 患者信息列表
        List<PatientTotalInfoVo> patientInfoByIds = remotePatientCentralServiceFeign.findPatientTotalInfo(patientIds);
        // 欠费金额列表
        List<DebtAmountModel> debtAmountList1 = remoteTreatmentServiceFeign.findDebtAmountList(patientIds);
        // 会员卡类型ID
        List<Integer> memberTypeIds = new ArrayList<>();
        patientInfoByIds.forEach(patientTotalInfoVo -> {
            memberTypeIds.add(patientTotalInfoVo.getMemberTypeId());
        });
        // 获取预约患者
        List<Appointment> appointmentList = this.appointmentListByIds(appointIds);

        // 将dentistIds合并到assistentIds中
        dentistIds.stream().sequential().collect(Collectors.toCollection(()->assistantIds));
        // 根据预约医生ID和预约助手ID列表
        List<SysUserInfoDetail> appointDentistAndAssistentInfoList = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserIds(assistantIds);
        // 根据预约科室ID集合查询预约科室信息列表
        List<DepartmentRoom> appointDepartmentRooms = remoteSystemServiceFeign.findDepartmentRoomByIds(deptRoomIds);
        // 获取会员类型信息
        List<MemberType> memberTypeByIds = remoteSystemServiceFeign.findMemberTypeByIds(memberTypeIds);

        // 注入患者基本信息
        vos.forEach(patientEntity -> {
            Integer patientId1 = patientEntity.getPatientId();
            // 设置患者基本信息
            if (StringHelper.isNotEmpty(patientInfoByIds)) {
                List<PatientTotalInfoVo> collect = patientInfoByIds.stream().filter(patientTotalInfoVo -> patientTotalInfoVo.getId().equals(patientId1)).collect(Collectors.toList());
                if (StringHelper.isNotEmpty(collect)) {
                    PatientTotalInfoVo patientTotalInfoVo = collect.get(0);
                    patientEntity.setPatientName(patientTotalInfoVo.getName());
                    patientEntity.setMobile(patientTotalInfoVo.getMobile());
                    patientEntity.setGender(patientTotalInfoVo.getGender());
                    patientEntity.setAge(patientTotalInfoVo.getAge());
                    patientEntity.setBirthday(patientTotalInfoVo.getBirthday());
                    patientEntity.setPatientRemark(patientTotalInfoVo.getRemarks());
                    String medicalNumber = patientTotalInfoVo.getMedicalNumber();
                    patientEntity.setMedicalNumber(StringHelper.isNotBlank(medicalNumber) ? medicalNumber : "--");
                    patientEntity.setAllergenDescription(patientTotalInfoVo.getAllergensDescriptions());
                    patientEntity.setAllergen(patientTotalInfoVo.getAllergens());
                    patientEntity.setPatientKind(patientTotalInfoVo.getPatientKindName());
                    // 设置会员类型图标
                    if (StringHelper.isNotEmpty(memberTypeByIds)) {
                        List<MemberType> collect1 = memberTypeByIds.stream().filter(memberType -> memberType.getId().equals(patientTotalInfoVo.getMemberTypeId())).collect(Collectors.toList());
                        if (StringHelper.isNotEmpty(collect1)) {
                            MemberType memberType = collect1.get(0);
                            patientEntity.setMemberIcon(memberType.getIcon());
                            patientEntity.setMemberCardName(memberType.getName());
                        }
                    }
                }
            }
            // 设置候诊患者预约信息
            setAppointmentInfo(patientEntity,
                    appointmentList,
                    appointDentistAndAssistentInfoList,
                    appointDepartmentRooms,
                    patientEntity.getId());
            // 欠费金额
            if (StringHelper.isNotEmpty(debtAmountList1)) {
                List<DebtAmountModel> collect = debtAmountList1.stream().filter(debtAmountModel -> debtAmountModel.getPatientId().equals(patientId1)).collect(Collectors.toList());
                if (StringHelper.isNotEmpty(collect)) {
                    DebtAmountModel debtAmountModel = collect.get(0);
                    patientEntity.setArrears(debtAmountModel.getDebtAmount());
                }
            }

        });
    }

    /**
     * 设置候诊患者预约信息
     * @param vo  患者候诊信息
     * @param appointmentList 预约信息列表
     * @param dentistAndAssistentInfoList 预约医生和预约助手信息列表
     * @param departmentRooms  预约科室信息列表
     * @param appointId  预约ID
     */
    private void setAppointmentInfo(AppointmentUnDonePatientInfoVO vo,
                                    List<Appointment> appointmentList,
                                    List<SysUserInfoDetail> dentistAndAssistentInfoList,
                                    List<DepartmentRoom> departmentRooms,
                                    Integer appointId) {
        if (StringHelper.isNotEmpty(appointmentList)) {
            // 根据appointId从预约列表中检索预约
            List<Appointment> collect = appointmentList.stream().filter(appointment -> appointment.getId().equals(appointId)).collect(Collectors.toList());
            if (StringHelper.isNotEmpty(collect)) {
                Appointment appointment = collect.get(0);
                vo.setAppointDentistId(appointment.getDentistId());
                // 根据医生ID从医生信息列表中查询医生名字
                Integer dentistId = appointment.getDentistId();
                List<SysUserInfoDetail> sysUserInfoDetailList = dentistAndAssistentInfoList.stream().filter(
                        sysUserInfoDetail -> sysUserInfoDetail.getUserId().equals(dentistId)).collect(Collectors.toList());
                if (StringHelper.isNotEmpty(sysUserInfoDetailList)) {
                    SysUserInfoDetail dentistInfo = sysUserInfoDetailList.get(0);
                    vo.setAppointDentistName(null != dentistInfo ? dentistInfo.getName() : "--");
                }
                // 根据助手ID从医生信息列表中查询助手名字
                Integer assistantId = appointment.getAssistantId();
                List<SysUserInfoDetail> assistantInfos = dentistAndAssistentInfoList.stream().filter(
                        sysUserInfoDetail -> sysUserInfoDetail.getUserId().equals(assistantId)).collect(Collectors.toList());
                if (StringHelper.isNotEmpty(assistantInfos)) {
                    SysUserInfoDetail assistantInfo = assistantInfos.get(0);
                    vo.setAppointAssistantName(null != assistantInfo ? assistantInfo.getName() : "--");
                }
                // 根据科室ID从科室信息列表中查询科室名字
                Integer deptRoomId = appointment.getDeptRoomId();
                List<DepartmentRoom> departmentRoomInfos = departmentRooms.stream().filter(
                        departmentRoom -> departmentRoom.getId().equals(deptRoomId)).collect(Collectors.toList());
                if (StringHelper.isNotEmpty(departmentRoomInfos)) {
                    vo.setAppointDeptRoomId(appointment.getDeptRoomId());
                    DepartmentRoom departmentRoom = departmentRoomInfos.get(0);
                    vo.setAppointDeptRoomName(null != departmentRoom ? departmentRoom.getName() : "--");
                }
                vo.setAppointTime(appointment.getAppointTime());
                vo.setAppointDuration(appointment.getAppointDuration());
                vo.setAppointContent(appointment.getAppointContent());
                vo.setAppointRemark(appointment.getRemarks());
                vo.setFirstVisit(appointment.getAppointType());
                vo.setAppointStatus(appointment.getAppointStatus());
                vo.setConfirmStatus(appointment.getConfirmStatus());
            }
        }
    }


    /**
     * 组合患者预约医生维度信息（预约患者信息+医生排班信息）
     * @param orgId 门诊id
     * @param appointmentDimensionVo 患者信息视图信息
     * @param appointmentSplits 预约分解信息
     * @param assistentInfoList 助手信息列表
     * @param treatmentRecordList 就诊记录列表
     * @return AppointmentDentistDimensionVo
     */
    private AppointmentDimensionVo combinationDentistDimensionVo(Integer orgId,
                                                                 AppointmentDimensionVo appointmentDimensionVo,
                                                                 List<AppointmentSplitVo> appointmentSplits,
                                                                 List<SysUserInfoDetail> assistentInfoList,
                                                                 List<TreatmentRecord> treatmentRecordList) {
        // 取出患者信息
        List<AppointmentPatientCardVo> patientCardVos = appointmentDimensionVo.getAppointmentPatientCardVos();
        if (!StringHelper.isEmpty(patientCardVos)) {
            // 助手预约信息列表
            List<AppointmentDimensionVo> assistantAppointList;
            // 获取患者维度模型中的助手预约列表
            List<AppointmentDimensionVo> appointmentAssistants = appointmentDimensionVo.getAppointmentAssistants();
            // 如果助手预约列表不为空，则使用该列表，否则新建列表
            if (null != appointmentAssistants) {
                assistantAppointList = appointmentAssistants;
            } else {
                // 新建助手预约信息列表
                assistantAppointList = new ArrayList<>();
            }
            // 查询预约分解信息
            AppointmentSplitQuery splitQuery = new AppointmentSplitQuery();
            // 根据患者信息中的预约医生ID和助手ID填充医生和助手基础信息
            for(int index = 0; index < patientCardVos.size(); index++) {
                AppointmentPatientCardVo appointmentPatientCardVo = patientCardVos.get(index);
                // 设置预约id
                splitQuery.setAppointmentId(appointmentPatientCardVo.getId());
                // 设置门诊id
                splitQuery.setOrgId(orgId);
                // 设置预约时间
                splitQuery.setAppointDate(appointmentPatientCardVo.getAppointDate());
                // 分解的预约是否有大医生,如果有则 distentSplitCount++ 不删除大医生下的预约，如果没有则 distentSplitCount=0 删除大医生下的预约
                // 防止被分解掉的且没有分解到大医生身上的预约显示到大医生下
                int distentSplitCount = 0;
                // 预约是否分解
                boolean isSplited = false;
                // 查询本次预约相关的分解助手信息
                if (StringHelper.isNotEmpty(appointmentSplits)) {
                    // 检测患者预约是否被分解
                    boolean b = appointmentSplits.stream().anyMatch(entity -> entity.getAppointmentId().equals(appointmentPatientCardVo.getId()));
                    if (b) {
                        List<AppointmentSplitVo> appointSplitVo = appointmentSplits.stream().filter(entity -> entity.getAppointmentId().equals(appointmentPatientCardVo.getId())).collect(Collectors.toList());
                        isSplited = true;
                        for(AppointmentSplitVo appointmentSplitVo : appointSplitVo) {
                            // 判断预约是否有分解到大医生
                            Integer assistantId = appointmentSplitVo.getAssistantId();
                            Integer dentistId = appointmentDimensionVo.getDentistId();
                            // 通过判断助手ID和大医生ID是否相等来判断是否分解到大医生下， 如果分解到大医生下，则直接修改大医生下患者卡片中患者预约时间段，否则将分解的预约添加到分解助手卡片中
                            if (assistantId.equals(dentistId)) {
                                distentSplitCount++;
                                String[] splitStartTimeArr = appointmentSplitVo.getSplitStartTime().split(":");
                                String[] splitEndTimeArr = appointmentSplitVo.getSplitEndTime().split(":");
                                // 设置预约时间段
                                Integer startMinute = Integer.parseInt(splitStartTimeArr[0]) * 60 + Integer.parseInt(splitStartTimeArr[1]);
                                Integer endMinute = Integer.parseInt(splitEndTimeArr[0]) * 60 + Integer.parseInt(splitEndTimeArr[1]);
                                appointmentPatientCardVo.setAppointDuration(endMinute-startMinute);
                                // 助手预约时间
                                appointmentPatientCardVo.setAppointTime(appointmentSplitVo.getSplitStartTime());
                            } else {
                                // 设置医生维度大医生下的预约助手相关信息（助手排班、助手分解到的患者信息）
                                AppointmentDimensionVo assistantPatientInfo = this.setAppointAssistantInfo(orgId,appointmentPatientCardVo, appointmentSplitVo,assistentInfoList,treatmentRecordList);
                                // 设置预约助手下的患者数量，每循环一次加1；
                                if(null != assistantPatientInfo) {
                                    Integer patientNum = assistantPatientInfo.getPatientNum();
                                    patientNum = (null == patientNum ? 0 : patientNum);
                                    assistantPatientInfo.setPatientNum(++patientNum);

                                    // 将助手信息放入助手预约信息列表中
                                    if (!assistantAppointList.contains(assistantPatientInfo)) {
                                        assistantAppointList.add(assistantPatientInfo);
                                    }
                                }
                            }
                        }
                        // 将助手预约信息设置到医生维度信息实体中
                        appointmentDimensionVo.setAppointmentAssistants(assistantAppointList);
                    }
                }
                // 如果分解的预约没有分解到大医生下(distentSplitCount <= 0) 并且 该预约下是有预约分解列表(isSplited=true)，则
                // 删除大医生下的患者信息；否则不删除,直接设置患者就诊状态
                if (distentSplitCount <= 0 && isSplited) {
                    // 移除没有分解到大医生身上的预约
                    patientCardVos.remove(index);
                    // 删除列表中的元素之后索引-1 ， 目的是为了patientCardVos列表中的下一个对象能够被循环到
                    // 非常重要，解决了在预约都被分解的请况下，第一个预约分解可找到，第二个预约分解找不到的bug
                    index--;
                } else {
                    // 获取大医生下患者当前处于哪个就诊状态
                    // 设置大医生下所有的患者就诊状态
                    this.setTreatmentStation(appointmentPatientCardVo,appointmentPatientCardVo.getId(),treatmentRecordList);
                }
            }
            // 判断大医生下面的助手列表中是否已经存在同一个助手了，如果存在同一个助手，则合并助手下的所有患者
            appointmentDimensionVo = this.mergeAssistant(appointmentDimensionVo);
        }
        return appointmentDimensionVo;
    }

    /**
     * 合并大医生下的所有相同的助手
     * @param appointmentDimensionVo 预约可视图对象
     * @return 对象
     */
    private AppointmentDimensionVo mergeAssistant(AppointmentDimensionVo appointmentDimensionVo) {
        if (null == appointmentDimensionVo) {
            return null;
        }
        Map<Integer, AppointmentDimensionVo> tempMap = new HashMap<>();
        // 获取大医生下的所有助手列表
        List<AppointmentDimensionVo> appointmentAssistants = appointmentDimensionVo.getAppointmentAssistants();
        if (!StringHelper.isEmpty(appointmentAssistants)) {
            for (AppointmentDimensionVo item : appointmentAssistants) {
                Integer assistantId = item.getDentistId();
                if (tempMap.containsKey(assistantId)) {
                    AppointmentDimensionVo appointmentDimension_1 = tempMap.get(assistantId);
                    AppointmentDimensionVo mergeAppointmentDimension = EntityUtils.build(appointmentDimension_1,AppointmentDimensionVo.class);
                    Integer beforePatientNum_B = item.getPatientNum();
                    Integer beforePatientNum_A = mergeAppointmentDimension.getPatientNum();
                    // 设置合并后的助手总预约数
                    mergeAppointmentDimension.setPatientNum(beforePatientNum_B + beforePatientNum_A);
                    // 合并患者列表前的患者列表
                    List<AppointmentPatientCardVo> appointmentPatientCard_A = mergeAppointmentDimension.getAppointmentPatientCardVos();
                    List<AppointmentPatientCardVo> appointmentPatientCard_B = item.getAppointmentPatientCardVos();
                    // 合并助手下的患者列表 将 appointmentPatientCard_A 合并到appointmentAssistants_B
                    if (!StringHelper.isEmpty(appointmentPatientCard_A) && !StringHelper.isEmpty(appointmentPatientCard_B)) {
                        appointmentPatientCard_A.stream().sequential().collect(Collectors.toCollection(() -> appointmentPatientCard_B));
                        // 将助手下的患者列表设置到 mergeAppointmentDimension
                        mergeAppointmentDimension.setAppointmentPatientCardVos(appointmentPatientCard_B);
                    } else if (StringHelper.isEmpty(appointmentPatientCard_A) && !StringHelper.isEmpty(appointmentPatientCard_B)) {
                        // appointmentPatientCard_A = null && appointmentAssistants_B != null 时，合并之后的结果为 appointmentAssistants_B
                        mergeAppointmentDimension.setAppointmentPatientCardVos(appointmentPatientCard_B);
                    } else if (!StringHelper.isEmpty(appointmentPatientCard_A) && StringHelper.isEmpty(appointmentPatientCard_B)){
                        // appointmentPatientCard_A != null && appointmentAssistants_B = null 时，合并之后的结果为 appointmentPatientCard_A
                        mergeAppointmentDimension.setAppointmentPatientCardVos(appointmentPatientCard_A);
                    }
                    // 将mergeAppointmentDimension覆盖到tempMap中
                    tempMap.put(assistantId,mergeAppointmentDimension);

                } else {
                    tempMap.put(assistantId,item);
                }
            }
            // 去掉重复助手之后的助手预约列表
            List<AppointmentDimensionVo> mergeAfterList = new ArrayList<>();
            for (Integer index : tempMap.keySet()) {
                mergeAfterList.add(tempMap.get(index));
            }
            // 根据预约患者数量进行降序排列
            List<AppointmentDimensionVo> collect = mergeAfterList.stream().sorted(
                    Comparator.comparing(AppointmentDimensionVo::getPatientNum).reversed()).collect(Collectors.toList());
            // 将去重之后的列表重新设置到appointmentDimensionVo中
            appointmentDimensionVo.setAppointmentAssistants(collect);
        }
        return appointmentDimensionVo;
    }

    /**
     * 设置医生维度大医生下的预约助手相关信息（助手排班、助手分解到的患者信息）
     * @param appointmentPatientCardVo  大医生下的患者卡信息
     * @param appointmentSplitVo  大医生下患者预约相关的预约分解
     * @param assistentInfoList 助手信息列表
     * @param treatmentRecordList 就诊记录列表
     * @return 医生维度视图模型
     */
    private AppointmentDimensionVo setAppointAssistantInfo(Integer orgId, AppointmentPatientCardVo appointmentPatientCardVo,
                                                           AppointmentSplitVo appointmentSplitVo,
                                                           List<SysUserInfoDetail> assistentInfoList,
                                                           List<TreatmentRecord> treatmentRecordList) {
        if (StringHelper.isEmpty(assistentInfoList) || appointmentSplitVo == null) {
            return null;
        }
        // 检测给助手是否有分解的预约，如果没有返回null
        boolean b = assistentInfoList.stream().anyMatch(entity -> entity.getUserId().equals(appointmentSplitVo.getAssistantId()));
        if (!b) {
            return null;
        }
        // 获取助手信息
        SysUserInfoDetail assistantDetailInfo = assistentInfoList.stream().filter(entity -> entity.getUserId().equals(appointmentSplitVo.getAssistantId())).findAny().get();
        // 助手患者信息
        // 助手患者信息列表
        List<AppointmentPatientCardVo> assistantPatientCardList = new ArrayList<>();
        // 医生维度列表模型
        AppointmentDimensionVo  assistantPatientInfo = new AppointmentDimensionVo();
        // 设置助手ID
        assistantPatientInfo.setDentistId(assistantDetailInfo.getUserId());
        // 设置助手名字
        assistantPatientInfo.setName(assistantDetailInfo.getName());

        // 助手患者信息模型
        AppointmentPatientCardVo assistantPatientCardInfo = EntityUtils.build(appointmentPatientCardVo, AppointmentPatientCardVo.class);
        String[] splitStartTimeArr = appointmentSplitVo.getSplitStartTime().split(":");
        String[] splitEndTimeArr = appointmentSplitVo.getSplitEndTime().split(":");
        // 设置预约时间段
        Integer startMinute = Integer.parseInt(splitStartTimeArr[0]) * 60 + Integer.parseInt(splitStartTimeArr[1]);
        Integer endMinute = Integer.parseInt(splitEndTimeArr[0]) * 60 + Integer.parseInt(splitEndTimeArr[1]);
        assistantPatientCardInfo.setAppointDuration(endMinute-startMinute);
        // 助手预约时间
        assistantPatientCardInfo.setAppointTime(appointmentSplitVo.getSplitStartTime());
        // 查询助手下患者就诊流程的状态
        // 设置助手下患者就诊流程的状态 0-待挂号状态；1-就诊中状态；2-就诊完成状态
        this.setTreatmentStation(assistantPatientCardInfo, appointmentPatientCardVo.getPatientId(), treatmentRecordList);

        // 将助手患者信息设置到患者信息列表中
        assistantPatientCardList.add(assistantPatientCardInfo);
        // 将患者信息列表设置到医生维度信息实体中
        assistantPatientInfo.setAppointmentPatientCardVos(assistantPatientCardList);

        // 设置医生或者助手排班信息
        this.setAppointmentScheduleInfo(orgId,assistantDetailInfo,appointmentPatientCardVo,assistantPatientInfo);
        return assistantPatientInfo;
    }

    /**
     *
     * @param assistantDetailInfo  助手或者医生信息
     * @param appointmentPatientCardVo 预约患者信息卡，
     * @param assistantPatientInfo 要注入的助手或医生类
     */
    private void setAppointmentScheduleInfo(Integer orgId,SysUserInfoDetail assistantDetailInfo,
                                               AppointmentPatientCardVo appointmentPatientCardVo,
                                               AppointmentDimensionVo  assistantPatientInfo) {
        // 设置助手排班信息
        EmployeeScheduleQueryForm scheduleQueryForm = new EmployeeScheduleQueryForm();
        scheduleQueryForm.setUserId(assistantDetailInfo.getUserId());
        scheduleQueryForm.setName(assistantDetailInfo.getName());
        Date appointDate = appointmentPatientCardVo.getAppointDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = dateFormat.format(appointDate);
        scheduleQueryForm.setStartDate(format);
        scheduleQueryForm.setEndDate(format);
        scheduleQueryForm.setClinicId(orgId);
        // 查询助手排班信息，查询预约当天助手排班 TODO 前端页面没有实现，暂时不优化
        EmployeeScheduleResultVO employeeScheduleResultVO = this.employeeAttendServiceFeign.findList(scheduleQueryForm);
        if (employeeScheduleResultVO.getCount() > 0) {
            List<UserWorkVO> shiftWorkDatas = employeeScheduleResultVO.getShiftWorkDatas();
            if (!StringHelper.isEmpty(shiftWorkDatas)) {
                List<WorkDayVO> days = shiftWorkDatas.get(0).getDays();
                List<EmpScheduleVo> empScheduleVos = new ArrayList<>();
                if (StringHelper.isNotEmpty(days)) {
                    EmpScheduleVo empSchedule = null;
                    for (WorkDayVO entity : days) {
                        empSchedule = new EmpScheduleVo();
                        setEmpSchedule(entity,assistantPatientInfo.getDentistId(),assistantPatientInfo.getName(),empSchedule);
                        empScheduleVos.add(empSchedule);
                    }
                }
                assistantPatientInfo.setDentistScheduleVos(empScheduleVos);
            }
        }
        // 查询医生/助手请假信息
        LeaveInfoForm leaveInfoQuery = new LeaveInfoForm();
        leaveInfoQuery.setUserId(assistantDetailInfo.getUserId());
        leaveInfoQuery.setStartTime(appointDate);
        leaveInfoQuery.setEndTime(appointDate);
        List<LeaveInfoListVO> leaveInfoList = this.employeeAttendServiceFeign.findEmployeeLeaveInfoList(leaveInfoQuery);
        EmpScheduleVo empSchedule = null;
        List<EmpScheduleVo> empScheduleVos = new ArrayList<>();
        if (StringHelper.isNotEmpty(leaveInfoList)) {
            for (LeaveInfoListVO entity : leaveInfoList) {
                empSchedule = new EmpScheduleVo();
                setEmpSchedule(entity,entity.getUserId(),entity.getUserName(),empSchedule);
                empScheduleVos.add(empSchedule);
            }
        }
        assistantPatientInfo.setDentistScheduleVos(empScheduleVos);
    }

    /**
     * 组合预约医生和患者信息（患者维度）
     * @param dentistWorkSchedule 排班表
     * @param appointmentDimensionCommInfos  预约基础信息列表
     * @param treatmentRecordListByAppointIds  接诊记录ID列表
     * @param patientTotalInfos  患者信息列表
     * @return  预约可视图vo
     */
    private List<AppointmentDimensionVo> combinationPatientDimensionVo(UserWorkVO dentistWorkSchedule,
                                                                       List<AppointmentDimensionVo> appointmentDimensionCommInfos,
                                                                       List<TreatmentRecord> treatmentRecordListByAppointIds,
                                                                       List<PatientTotalInfoVo> patientTotalInfos,
                                                                       List<LeaveInfoListVO> employeeLeaveInfos,
                                                                       List<WorkOvertimeInfoListVO> employeeWorkOvertimes,
                                                                       List<FieldInfoListVO> fieldList){
        List<AppointmentDimensionVo> appointmentDimensionVoList = new LinkedList<>();
        Integer userId = dentistWorkSchedule.getCompEmpId();
        String dentistName = dentistWorkSchedule.getName();

        List<AppointmentDimensionVo> collect = appointmentDimensionCommInfos.stream().filter(appointmentDimensionVo -> appointmentDimensionVo.getDentistId().equals(userId)).collect(Collectors.toList());
        if (StringHelper.isNotEmpty(collect)) {
            collect.forEach(item -> {
                item.setName(dentistName);
                // 组合患者基本信息
                List<AppointmentPatientCardVo> appointmentPatientCardVos = item.getAppointmentPatientCardVos();
                appointmentPatientCardVos.forEach(appointmentPatientCardVo -> {
                    this.setPatientBaseInfo(appointmentPatientCardVo,patientTotalInfos,treatmentRecordListByAppointIds);
                });
                List<EmpScheduleVo> empScheduleVos = new ArrayList<>();
                List<WorkDayVO> days = dentistWorkSchedule.getDays();
                // 设置排班
                if (StringHelper.isNotEmpty(days)) {
                    this.setEmpScheduleList(item,empScheduleVos,days);
                }
                // 设置请假信息
                if (StringHelper.isNotEmpty(employeeLeaveInfos)) {
                    this.setEmployeeLeaveInfo(item,empScheduleVos,employeeLeaveInfos);
                }
                // 设置加班信息
                if (StringHelper.isNotEmpty(employeeWorkOvertimes)) {
                    this.setWorkOvertime(item,employeeWorkOvertimes,empScheduleVos);
                }
                // 设置外勤信息
                if (StringHelper.isNotEmpty(fieldList)) {
                    this.setFieldInfo(item,fieldList,empScheduleVos);
                }
                appointmentDimensionVoList.add(item);
            });
        } else {
            // 如果该医生在时间段内没有预约，则只设置医生信息和排班信息，不设置患者预约信息
            AppointmentDimensionVo appointmentDimensionVoNull = new AppointmentDimensionVo();
            appointmentDimensionVoNull.setDentistId(userId);
            appointmentDimensionVoNull.setName(dentistName);
            List<EmpScheduleVo> empScheduleVos = new ArrayList<>();
            List<WorkDayVO> days = dentistWorkSchedule.getDays();
            // 设置排班
            if (StringHelper.isNotEmpty(days)) {
                this.setEmpScheduleList(appointmentDimensionVoNull,empScheduleVos,days);
            }

            // 设置请假信息
            if (StringHelper.isNotEmpty(employeeLeaveInfos)) {
                this.setEmployeeLeaveInfo(appointmentDimensionVoNull,empScheduleVos,employeeLeaveInfos);
            }
            // 设置加班信息
            if (StringHelper.isNotEmpty(employeeWorkOvertimes)) {
                this.setWorkOvertime(appointmentDimensionVoNull,employeeWorkOvertimes,empScheduleVos);
            }
            // 设置外勤信息
            if (StringHelper.isNotEmpty(fieldList)) {
                this.setFieldInfo(appointmentDimensionVoNull,fieldList,empScheduleVos);
            }
            appointmentDimensionVoList.add(appointmentDimensionVoNull);
        }
        return appointmentDimensionVoList;
    }

    /**
     * 设置员工外勤信息
     * @param item  预约可视图对象
     * @param fieldList  外勤信息列表
     * @param empScheduleVos 源排班实例对象列表
     */
    private void setFieldInfo(AppointmentDimensionVo item,List<FieldInfoListVO> fieldList, List<EmpScheduleVo> empScheduleVos) {
        Integer dentistId = item.getDentistId();
        boolean b = fieldList.stream().anyMatch(entity -> entity.getUserId().equals(dentistId));
        if (b) {
            List<FieldInfoListVO> collect = fieldList.stream().filter(entity -> entity.getUserId().equals(dentistId)).collect(Collectors.toList());
            EmpScheduleVo empScheduleVo = null;
            for (FieldInfoListVO fieldInfo : collect) {
                empScheduleVo = new EmpScheduleVo();
                this.setEmpSchedule(fieldInfo,dentistId,item.getName(),empScheduleVo);
                empScheduleVos.add(empScheduleVo);
            }
        }
    }

    /**
     * 设置员工加班信息
     * @param item  预约可视图对象
     * @param employeeWorkOvertimes  加班信息列表
     * @param empScheduleVos 源排班实例对象列表
     */
    private void setWorkOvertime(AppointmentDimensionVo item, List<WorkOvertimeInfoListVO> employeeWorkOvertimes, List<EmpScheduleVo> empScheduleVos) {
        Integer dentistId = item.getDentistId();
        boolean b = employeeWorkOvertimes.stream().anyMatch(entity -> entity.getUserId().equals(dentistId));
        if (b) {
            List<WorkOvertimeInfoListVO> workOvertimes = employeeWorkOvertimes.stream().filter(entity -> entity.getUserId().equals(dentistId)).collect(Collectors.toList());
            EmpScheduleVo empScheduleVo = null;
            for (WorkOvertimeInfoListVO workOvertime: workOvertimes) {
                empScheduleVo = new EmpScheduleVo();
                this.setEmpSchedule(workOvertime,workOvertime.getUserId(),workOvertime.getUserName(),empScheduleVo);
                empScheduleVos.add(empScheduleVo);
            }
        }
    }

    /**
     * 注入员工排班，请假信息
     * @param appointment
     * @param empScheduleVos
     * @param workDayVOS
     */
    private void setEmpScheduleList(AppointmentDimensionVo appointment, List<EmpScheduleVo> empScheduleVos, List<WorkDayVO> workDayVOS) {
        if (StringHelper.isNotEmpty(workDayVOS)) {
            EmpScheduleVo empScheduleVo = null;
            for (WorkDayVO workDay : workDayVOS) {
                if (null != workDay.getId()) {
                    empScheduleVo = new EmpScheduleVo();
                    this.setEmpSchedule(workDay, appointment.getDentistId(), appointment.getName(), empScheduleVo);
                    empScheduleVos.add(empScheduleVo);
                }
            }
        }
        appointment.setDentistScheduleVos(empScheduleVos);
    }

    /**
     * 设置医生请假信息
     * @param appoint 医生预约信息
     * @param employeeLeaveInfos 员工请假信息列表
     */
    private void setEmployeeLeaveInfo(AppointmentDimensionVo appoint,List<EmpScheduleVo> empScheduleVos, List<LeaveInfoListVO> employeeLeaveInfos) {
        if (StringHelper.isNotEmpty(employeeLeaveInfos)) {
            EmpScheduleVo empScheduleVo = null;
            Integer dentistId = appoint.getDentistId();
            boolean b = employeeLeaveInfos.stream().anyMatch(entity -> entity.getUserId().equals(dentistId));
            if (b) {
                List<LeaveInfoListVO> collect = employeeLeaveInfos.stream().filter(entity -> entity.getUserId().equals(dentistId)).collect(Collectors.toList());
                for (LeaveInfoListVO leaveInfo : collect) {
                    empScheduleVo = new EmpScheduleVo();
                    this.setEmpSchedule(leaveInfo, dentistId, appoint.getName(), empScheduleVo);
                    empScheduleVos.add(empScheduleVo);
                }
            }
            appoint.setDentistScheduleVos(empScheduleVos);
        }
    }


    /**
     * 注入患者信息
     * @param appointmentPatientCardVo 要注入的类
     * @param patientTotalInfo  患者信息
     * @param treatmentRecordListByAppointIds 就诊记录ID列表
     */
    private void setPatientBaseInfo(AppointmentPatientCardVo appointmentPatientCardVo,List<PatientTotalInfoVo> patientTotalInfo,List<TreatmentRecord> treatmentRecordListByAppointIds) {
        Integer appointId = appointmentPatientCardVo.getId();
        Integer patientId = appointmentPatientCardVo.getPatientId();
        if (StringHelper.isNotEmpty(patientTotalInfo)) {
            boolean hasElement = patientTotalInfo.stream().anyMatch(patientTotalInfoVo -> patientTotalInfoVo.getId().equals(patientId));
            if (hasElement) {
                PatientTotalInfoVo patientInfo = patientTotalInfo.stream().filter(item -> item.getId().equals(patientId)).findAny().get();
                appointmentPatientCardVo.setAge(patientInfo.getAge());
                appointmentPatientCardVo.setGender(patientInfo.getGender());
                appointmentPatientCardVo.setName(patientInfo.getName());
                // 设置患者就诊状态
                this.setTreatmentStation(appointmentPatientCardVo,appointId,treatmentRecordListByAppointIds);
            }
        }
    }

    /**
     * 设置患者就诊流程的状态
     * @param appointmentPatientCardVo 要注入的类
     * @param appointId 预约ID
     * @param treatmentRecordListByAppointIds 就诊记录ID集合
     */
    private void setTreatmentStation(AppointmentPatientCardVo appointmentPatientCardVo,Integer appointId,  List<TreatmentRecord> treatmentRecordListByAppointIds) {
        if (StringHelper.isNotEmpty(treatmentRecordListByAppointIds) && null != appointId) {
            boolean present = treatmentRecordListByAppointIds.stream().anyMatch(record -> record.getAppointmentId() != null && record.getAppointmentId().equals(appointId));
            if (present) {
                TreatmentRecord treatmentRecord = treatmentRecordListByAppointIds.stream().filter(record -> record.getAppointmentId().equals(appointId)).findAny().get();
                byte splitStatus = 2;
                // 诊疗状态(0-接诊中;1-已开单;2-接诊完成3-已结账)
                Byte type = treatmentRecord.getStatus();
                // 诊疗状态为 “0-接诊中;1-已开单”时返回 1(就诊中状态)
                if (type < splitStatus) {
                    appointmentPatientCardVo.setStation((byte) 1);
                } else {
                    // 诊疗状态为 “2-接诊完成3-已结账”时返回 2(就诊完成状态)
                    appointmentPatientCardVo.setStation((byte) 2);
                }
            }
        } else {
            // 如果没有挂号信息则设置患者就诊状态为 0-待挂号
            appointmentPatientCardVo.setStation((byte) 0);
        }
    }

    /**
     * 向预约列表中注入预约相关信息
     * @param build 预约信息列表
     * @param appointmentVo  预约信息
     * @param sysUserInfoDetails 医生或者助手信息列表
     * @param departmentRooms 科室信息列表
     */
    private void setAppointmentInfo(AppointmentListItemVo build,
                                    AppointmentVo appointmentVo,
                                    List<SysUserInfoDetail> sysUserInfoDetails,
                                    List<DepartmentRoom> departmentRooms) {
        // 查询预约医生信息
        Integer dentistId = build.getDentistId();
        if (dentistId != null && StringHelper.isNotEmpty(sysUserInfoDetails)) {
            boolean b = sysUserInfoDetails.stream().anyMatch(entity -> entity.getUserId().equals(dentistId));
            if (b) {
                SysUserInfoDetail userInfoDetail = sysUserInfoDetails.stream().filter(entity -> entity.getUserId().equals(dentistId)).findAny().get();
                String name = userInfoDetail.getName();
                build.setDentistName(StringHelper.isBlank(name) ? "--" : name);
            }
        } else {
            build.setDentistName("--");
        }

        // 查询预约助手信息
        Integer assistantId = build.getAssistantId();
        if (assistantId != null && StringHelper.isNotEmpty(sysUserInfoDetails)) {
            boolean b = sysUserInfoDetails.stream().anyMatch(entity -> entity.getUserId().equals(assistantId));
            if (b) {
                SysUserInfoDetail userInfoDetail = sysUserInfoDetails.stream().filter(entity -> entity.getUserId().equals(assistantId)).findAny().get();
                String name = userInfoDetail.getName();
                build.setAssistantName(StringHelper.isBlank(name) ? "--" : name);
            }
        } else {
            build.setAssistantName("--");
        }

        // 设置默认科室信息
        Integer deptRoomId = appointmentVo.getDeptRoomId();
        if (deptRoomId != null && StringHelper.isNotEmpty(departmentRooms)) {
            boolean b = departmentRooms.stream().anyMatch(entity -> entity.getId().equals(deptRoomId));
            if(b) {
                DepartmentRoom departmentRoom = departmentRooms.stream().filter(entity -> entity.getId().equals(deptRoomId)).findAny().get();
                String name = departmentRoom.getName();
                build.setClinicDeptRoomName(StringHelper.isBlank(name) ? "--" : name);
            }
        } else {
            build.setClinicDeptRoomName("--");
        }
        // 设置初诊/复诊
        build.setFirstVisit(appointmentVo.getAppointType());
        String remarks = appointmentVo.getRemarks();
        build.setRemarks(StringHelper.isBlank(remarks) ? "--" : remarks);
        String appointContent = appointmentVo.getAppointContent();
        build.setAppointContent(StringHelper.isBlank(appointContent) ? "--" : appointContent);
    }

    /**
     * 向预约列表中注入患者信息
     * @param build  预约信息列表
     * @param patientTotalInfoVos 患者信息列表
     * @param debtAmountModels  患者欠费金额列表
     * @param memberTypes 会员类型列表
     */
    private void setPatientInfo(AppointmentListItemVo build,
                                List<PatientTotalInfoVo> patientTotalInfoVos,
                                List<DebtAmountModel> debtAmountModels,
                                List<MemberType> memberTypes) {
        // 设置患者详细信息
        Integer patientId = build.getPatientId();
        if (patientId != null && StringHelper.isNotEmpty(patientTotalInfoVos)){
            boolean result = patientTotalInfoVos.stream().anyMatch(entity -> entity.getId().equals(patientId));
            if (result){
                PatientTotalInfoVo patientInfo = patientTotalInfoVos.stream().filter(entity->entity.getId().equals(patientId)).findAny().get();
                build.setAge(patientInfo.getAge());
                try {
                    String birthday = patientInfo.getBirthday();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    if (StringHelper.isNotEmpty(birthday)) {
                        Date parse = dateFormat.parse(patientInfo.getBirthday());
                        build.setBirthday(dateFormat.format(parse));
                    }
                } catch (ParseException e) {
                    throw new ClientServiceException("时间格式转化异常！",OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
                }
                build.setGender(patientInfo.getGender());
                build.setMedicalNumber(StringHelper.isEmpty(patientInfo.getMedicalNumber()) ? "--" : patientInfo.getMedicalNumber());
                build.setMobile(patientInfo.getMobile());
                build.setPatientId(patientInfo.getId());
                build.setPatientName(patientInfo.getName());
                build.setPatientRemark(StringHelper.isBlank(patientInfo.getRemarks()) ? "--" : patientInfo.getRemarks());
                build.setAllergen(patientInfo.getAllergens());
                build.setPinyinName(patientInfo.getPinyinName());
                build.setPatientKind(patientInfo.getPatientKindName());
                // 设置会员卡图标类型
                Integer memberTypeId = patientInfo.getMemberTypeId();
                if (memberTypeId != null && StringHelper.isNotEmpty(memberTypes)) {
                    boolean b = memberTypes.stream().anyMatch(memberType -> memberType.getId().equals(memberTypeId));
                    if (b) {
                        MemberType memberType = memberTypes.stream().filter(entity -> entity.getId().equals(memberTypeId)).findAny().get();
                        build.setMemberIcon(String.valueOf(memberType.getIcon()));
                    }
                }
            }
            // 欠费金额
            if (StringHelper.isNotEmpty(debtAmountModels)) {
                boolean b = debtAmountModels.stream().anyMatch(entity -> entity.getPatientId().equals(patientId));
                if (b) {
                    DebtAmountModel debtAmountModel = debtAmountModels.stream().filter(entity -> entity.getPatientId().equals(patientId)).findAny().get();
                    build.setArrears(debtAmountModel.getDebtAmount() == null ? new BigDecimal(0) : debtAmountModel.getDebtAmount());
                }
            }
        }
    }

    /**
     * 根据条件查询患者预约信息（患者档案-预约信息;查看详细-预约信息）用
     * @param query 查询条件
     * @return 预约列表
     */
    public ResponseResult<PageInfo<AppointPatientRecordVo>> findAppointPatientRecord(AppointPatientRecordQuery query) {
        Date startDate = query.getStartDate();
        Date endDate = query.getEndDate();
        if (null != startDate && null != endDate) {
            long startDateTime = startDate.getTime();
            long endDateTime = endDate.getTime();
            if (startDateTime > endDateTime) {
                return ResponseUtil.fail(AppointmentError.START_DATE_AFTER_END_DATE.getCode(),AppointmentError.START_DATE_AFTER_END_DATE.getMessage(),null);
            }
        }
        // 设置分页
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        // 查询患者预约信息
        List<AppointPatientRecordVo> appointPatientRecord = mapper.findAppointPatientRecord(query);
        if(StringHelper.isEmpty(appointPatientRecord)) {
            return ResponseUtil.success(new PageInfo<>(appointPatientRecord));
        }
        // 获取医生ID、助手ID
        List<Integer> dentistAndAssistentIds = new ArrayList<>();
        // 获取门诊ID
        List<Integer> orgIds = new ArrayList<>();
        // 获取科室ID
        List<Integer> departmentRoomIds = new ArrayList<>();
        // 获取设备ID
        List<Integer> deviceItemIds = new ArrayList<>();
        appointPatientRecord.forEach(item -> {
            Integer dentistId = item.getDentistId();
            if (!dentistAndAssistentIds.contains(dentistId) && dentistId != null) {
                dentistAndAssistentIds.add(dentistId);
            }
            Integer assistantId = item.getAssistantId();
            if (!dentistAndAssistentIds.contains(assistantId) && assistantId != null) {
                dentistAndAssistentIds.add(assistantId);
            }
            Integer orgId = item.getOrgId();
            if (!orgIds.contains(orgId) && orgId != null) {
                orgIds.add(orgId);
            }
            Integer deptRoomId = item.getDeptRoomId();
            if (!departmentRoomIds.contains(deptRoomId) && deptRoomId != null) {
                departmentRoomIds.add(deptRoomId);
            }
            Integer clinicDeviceItemId = item.getClinicDeviceItemId();
            if (!deviceItemIds.contains(clinicDeviceItemId) && clinicDeviceItemId != null) {
                deviceItemIds.add(clinicDeviceItemId);
            }
        });

        // 查询医生和助手个人信息
        List<SysUserInfoDetail> dentistAndAssistantInfos = this.remoteSystemServiceFeign.findSysUserEmployeeInfoByUserIds(dentistAndAssistentIds);
        // 查询门诊信息
        List<OrganizationInfoDetail> orgInfoInIds = this.remoteSystemServiceFeign.findOrgInfoInIds(orgIds);
        // 查询科室信息
        List<DepartmentRoom> departmentRoomByIds = this.remoteSystemServiceFeign.findDepartmentRoomByIds(departmentRoomIds);
        // 查询设备项目信息
        List<DeviceItemVo> deviceItemVos = this.clinicDeviceItemBiz.selectDeviceItemByIds(deviceItemIds);

        appointPatientRecord.forEach(appointPatientRecordVo -> {
            // 设置医生名字
            Integer dentistId = appointPatientRecordVo.getDentistId();
            if (null != dentistId && StringHelper.isNotEmpty(dentistAndAssistantInfos)) {
                List<SysUserInfoDetail> collect = dentistAndAssistantInfos.stream().filter(item -> item.getUserId().equals(dentistId)).collect(Collectors.toList());
                if (StringHelper.isNotEmpty(collect)) {
                    SysUserInfoDetail userInfoDetail = collect.get(0);
                    appointPatientRecordVo.setDentistName(userInfoDetail.getName());
                } else {
                    appointPatientRecordVo.setDentistName("--");
                }
            } else {
                appointPatientRecordVo.setDentistName("--");
            }
            // 设置助手名字
            Integer assistantId = appointPatientRecordVo.getAssistantId();
            if (null != assistantId && StringHelper.isNotEmpty(dentistAndAssistantInfos)) {
                List<SysUserInfoDetail> collect = dentistAndAssistantInfos.stream().filter(item -> item.getUserId().equals(assistantId)).collect(Collectors.toList());
                if (StringHelper.isNotEmpty(collect)) {
                    SysUserInfoDetail userInfoDetail = collect.get(0);
                    appointPatientRecordVo.setAssistantName(userInfoDetail.getName());
                } else {
                    appointPatientRecordVo.setAssistantName("--");
                }
            } else {
                appointPatientRecordVo.setAssistantName("--");
            }
            // 设置门诊名称
            Integer orgId = appointPatientRecordVo.getOrgId();
            if (null != orgId && StringHelper.isNotEmpty(orgInfoInIds)) {
                List<OrganizationInfoDetail> collect = orgInfoInIds.stream().filter(item -> item.getId().equals(orgId)).collect(Collectors.toList());
                if (StringHelper.isNotEmpty(collect)) {
                    OrganizationInfoDetail organizationInfoDetail = collect.get(0);
                    appointPatientRecordVo.setOrgName(organizationInfoDetail.getName());
                } else {
                    appointPatientRecordVo.setOrgName("--");
                }
            } else {
                appointPatientRecordVo.setOrgName("--");
            }
            // 设置科室名称
            Integer deptRoomId = appointPatientRecordVo.getDeptRoomId();
            if (null != deptRoomId && StringHelper.isNotEmpty(departmentRoomByIds)) {
                List<DepartmentRoom> collect = departmentRoomByIds.stream().filter(item -> item.getId().equals(deptRoomId)).collect(Collectors.toList());
                if (StringHelper.isNotEmpty(collect)) {
                    DepartmentRoom departmentRoom = collect.get(0);
                    appointPatientRecordVo.setDeptRoomName(departmentRoom.getName());
                } else {
                    appointPatientRecordVo.setDeptRoomName("--");
                }
            } else {
                appointPatientRecordVo.setDeptRoomName("--");
            }
            // 设置设备编号
            Integer clinicDeviceItemId = appointPatientRecordVo.getClinicDeviceItemId();
            if (null != clinicDeviceItemId && StringHelper.isNotEmpty(deviceItemVos)) {
                List<DeviceItemVo> collect = deviceItemVos.stream().filter(item -> item.getId().equals(clinicDeviceItemId)).collect(Collectors.toList());
                if (StringHelper.isNotEmpty(collect)) {
                    DeviceItemVo deviceItemVo = collect.get(0);
                    appointPatientRecordVo.setClinicDeviceItemNumber(deviceItemVo.getNumber());
                } else {
                    appointPatientRecordVo.setClinicDeviceItemNumber("--");
                }
            } else {
                appointPatientRecordVo.setClinicDeviceItemNumber("--");
            }

        });
        // 按医生名字检索
        if (!StringHelper.isEmpty(query.getDentistName())) {
            appointPatientRecord = appointPatientRecord.stream().filter(
                    appointPatientRecordVo -> appointPatientRecordVo
                            .getDentistName().equals(query.getDentistName())).collect(Collectors.toList());
        }
        return ResponseUtil.success(new PageInfo<>(appointPatientRecord));
    }

    /**
     * 删除缓冲中的数据
     * @param postfix 键值后缀
     */
    private void deleteCache(String postfix) {
        String appointInfo = RedisConstants.REDIS_KEY_APPOINT_INFO + postfix;
        String appointList = RedisConstants.REDIS_KEY_APPOINT_LIST + postfix;
        String appointDentistDimension = RedisConstants.REDIS_KEY_APPOINT_DENTIST_DIMENSION + postfix;
        String appointPatientDimension = RedisConstants.REDIS_KEY_APPOINT_PATIENT_DIMENSION + postfix;
        String appointmentUnDone = RedisConstants.REDIS_KEY_APPOINTMENT_UN_DONE + postfix;
        // 删除预约信息(未加工信息)
        Boolean aBoolean = redisUtils.hasKey(appointInfo);
        if (aBoolean) {
            redisUtils.delete(appointInfo);
        }
        // 删除预约列表信息
        Boolean aBoolean1 = redisUtils.hasKey(appointList);
        if (aBoolean1) {
            redisUtils.delete(appointList);
        }
        // 删除预约医生维度信息
        Boolean aBoolean2 = redisUtils.hasKey(appointDentistDimension);
        if (aBoolean2) {
            redisUtils.delete(appointDentistDimension);
        }
        // 删除预约患者维度信息
        Boolean aBoolean3 = redisUtils.hasKey(appointPatientDimension);
        if (aBoolean3) {
            redisUtils.delete(appointPatientDimension);
        }
        // 删除预约未到患者信息
        Boolean aBoolean4 = redisUtils.hasKey(appointmentUnDone);
        if (aBoolean4) {
            redisUtils.delete(appointDentistDimension);
        }
    }

    /**
     * 根据条件查询预约列表
     *
     * @param query 查询条件
     * @return
     */
    public PageInfo<Appointment> findAppointmentList(AppAppointmentInfoQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<Appointment> resultList = mapper.selectAppointmentList(query);
        return new PageInfo<>(resultList);
    }

    /**
     * 根据预约ID查询预约列表
     * @param ids 预约ID集合
     * @return 预约列表
     */
    public List<Appointment> appointmentListByIds(List<Integer> ids) {
        if (StringHelper.isNotEmpty(ids)) {
            return mapper.appointmentListByIds(ids);
        }
        return new ArrayList<>();
    }

    /**
     * 计算后续指定患者的预约数量列表
     * @param patientIds 患者id列表
     * @return 返回患者后续列表
     */
    public List<NextAppointsVo> countNextAppoints(List<Integer> patientIds) {
        if (StringHelper.isNotEmpty(patientIds)) {
            return mapper.countNextAppoints(patientIds);
        }
        return new ArrayList<NextAppointsVo>();
    }

    /**
     * 发送预约短信
     *
     * @param templateId 短信模板id
     * @param models 短信预约提醒列表
     * @return ResponseResult
     */
    public ResponseResult<T> sendAppointmentBatchSms(Integer templateId, List<AppointmentSmsSendRecordModel> models) {
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        SmsTemplateSetVO smsTemplateSetVO = remoteSmsServiceFeign.findSmsTemplateById(templateId);
        if (smsTemplateSetVO == null) {
            throw new ClientServiceException("该短信模板不存在", DATA_NOT_EXIST);
        }
        String templateItem = smsTemplateSetVO.getTemplateItem();
        if (StringHelper.isNotEmpty(templateItem)) {
            String code2 = null;
            String code3 = null;
            String code4 = null;
            if (templateItem.contains(SmsTemplateItemEnum.CLINIC_NAME.getCode() + "")
                    || templateItem.contains(SmsTemplateItemEnum.CLINIC_PHONE.getCode() + "")
                    || templateItem.contains(SmsTemplateItemEnum.CLINIC_ADDRESS.getCode() + "")) {
                MedicalOrganizationInfoVO medicalOrganizationInfoVO = remoteSystemServiceFeign.clinicExtInfoByCompanyId(orgId);
                code2 = medicalOrganizationInfoVO.getAbbreviation();
                code3 = medicalOrganizationInfoVO.getTel();
                code4 = medicalOrganizationInfoVO.getAddress();
            }
            String[] items = templateItem.split(",");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (AppointmentSmsSendRecordModel model : models) {
                JSONObject object = new JSONObject();
                Map<String, Integer> repeat = new HashMap<>();
                for (String item : items) {
                    Integer reNum = repeat.get(item);
                    String key = SmsTemplateItemEnum.getAction(item);
                    if (reNum == null) {
                        reNum = 0;
                    } else {
                        key = "re" + reNum + key;
                    }
                    repeat.put(item, ++reNum);
                    Integer code = Integer.parseInt(item);
                    if (SmsTemplateItemEnum.PATIENT_NAME.getCode().equals(code)) {// 患者姓名
                        object.put(key, model.getSendObject());
                    } else if (SmsTemplateItemEnum.CLINIC_NAME.getCode().equals(code)) { // 诊所名称
                        object.put(key,code2);
                    } else if (SmsTemplateItemEnum.CLINIC_PHONE.getCode().equals(code)) {// 诊所电话
                        object.put(key,code3);
                    } else if (SmsTemplateItemEnum.CLINIC_ADDRESS.getCode().equals(code)) {// 诊所地址
                        object.put(key,code4);
                    } else if (SmsTemplateItemEnum.APPOINTMENT_DOCTOR.getCode().equals(code)) {// 预约医生姓名
                        object.put(key, model.getDentistName());
                    } else if (SmsTemplateItemEnum.APPOINTMENT.getCode().equals(code)) { // 预约时间
                        String code7 = model.getAppointDate() + model.getAppointTime();
                        object.put(key, code7);
                    } else if (SmsTemplateItemEnum.APPELLATION.getCode().equals(code)) { // 先生/女士/小朋友
                        Integer age = model.getAge();
                        String code7 = "先生";
                        Integer gener = model.getGender();
                        if (age != null) {
                            code7 = "小朋友";
                            if (age>15 && gener!=null && gener==1) {
                                code7 = "女生";
                            }
                        } else {
                            if (gener!=null && gener==1) {
                                code7 = "女生";
                            }
                        }
                        object.put(key, code7);
                    } else if (SmsTemplateItemEnum.MORNING_AFTERNOON.getCode().equals(code)) {// 上午/下午
                        String code7 = model.getAppointDate() + " " + model.getAppointTime() + ":59";
                        String middleStr = model.getAppointDate() + " 12:00:00";
                        String lastStr = model.getAppointDate() + " 00:00:00";
                        try {
                            Date appointDateTime = sdf.parse(code7);
                            Date middle = sdf.parse(middleStr);
                            Date last = sdf.parse(lastStr);
                            String code9 = "上午";
                            if (appointDateTime.after(middle) && appointDateTime.before(last)) {
                                code9 = "下午";
                            }
                            object.put(key, code9);
                        } catch (ParseException e) {
                            throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
                        }
                    } else { // 其他
                        throw new ClientServiceException("模板有误，模板参数与模板适用场景不匹配", OPERATION_NOT_ALLOW);
                    }
                }
                model.setTemplateParam(object);
            }
        }
        remoteSmsServiceFeign.batchSendModels(templateId, models);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件查询取消预约明细表
     *
     * @param query 查询条件
     * @return
     */
    public PageInfo<CancelAppointmentVO> cancelAppointmentList(CancelAppointmentQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<CancelAppointmentVO> list = mapper.cancelAppointmentList(query);
        if (StringHelper.isNotEmpty(list)) {
            list.forEach(vo->{
                PatientBaseInfo patient = remotePatientCentralServiceFeign.findPatientInfoById(vo.getPatientId());
                if (patient != null) {
                    vo.setPatientName(patient.getName());
                }
                SysEmployee employee = remoteSystemServiceFeign.findSysEmployeeById(vo.getDentistId());
                if (employee != null) {
                    vo.setDentistName(employee.getName());
                }
                OrganizationInfo org = remoteSystemServiceFeign.findOrgInfoByOrgId(vo.getOrgId());
                if (org != null) {
                    vo.setAbbreviation(org.getAbbreviation());
                }
            });
        }
        return new PageInfo<>(list);
    }

    /**
     * 根据条件导出取消预约明细表
     *
     * @param response 响应
     * @param query 查询条件
     * @return
     */
    public void cancelAppointmentExport(HttpServletResponse response, CancelAppointmentQuery query) throws IOException {
        query.setWhetherPage(false);
        List<CancelAppointmentVO> data = cancelAppointmentList(query).getList();
        ExcelUtil<CancelAppointmentVO> excelUtil = new ExcelUtil<>(CancelAppointmentVO.class);
        String startDate = query.getStartDate();
        String endDate = query.getEndDate();
        if (startDate.equals(endDate)) {
            endDate = null;
        }
        String abbreviation = remoteSystemServiceFeign.findOrgInfoByOrgId(query.getOrgIds()[0]).getAbbreviation();
        String fileName = excelUtil.getFileName(startDate, endDate, abbreviation, "取消预约明细表");
        excelUtil.exportExcel(response,data,"取消预约明细表",fileName);
    }
}
