package com.yunya.modules.appointment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.base.AppointmentSplitBaseInfo;
import com.yunya.feign.appointment.domain.base.AppointmentSplitUpdateBaseInfo;
import com.yunya.feign.appointment.domain.form.AppointmentBaseForm;
import com.yunya.feign.appointment.domain.form.AppointmentSplitForm;
import com.yunya.feign.appointment.domain.model.AppointOperationModel;
import com.yunya.feign.appointment.domain.model.AppointmentBaseModel;
import com.yunya.feign.appointment.domain.model.AppointmentSplitModel;
import com.yunya.feign.appointment.domain.query.*;
import com.yunya.feign.appointment.vo.*;
import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.vo.EmployeeScheduleResultVO;
import com.yunya.feign.employee_attend.vo.UserWorkVO;
import com.yunya.feign.employee_attend.vo.WorkDayVO;
import com.yunya.feign.expand.RemoteClinicEmployeeConfigFeign;
import com.yunya.feign.expand.model.response.EnableChooseEmployeeRes;
import com.yunya.feign.expand.model.response.EnableEmployeeRes;
import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.appointment.AppointmentOperateRecord;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    /** 患者就诊服务 */
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;

    /** 门诊设备服务 */
    @Autowired
    private ClinicDeviceItemBiz clinicDeviceItemBiz;

    /** 门诊员工设置服务 */
    @Autowired
    private RemoteClinicEmployeeConfigFeign clinicEmployeeConfigFeign;

    /** 注入redis缓冲服务 */
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 添加预约（检查预约是否冲突）
     * @param form  预约参数封装
     * @return ResponseResult
     * @throws ParseException 日期转换异常
     */
    public ResponseResult addAppointment(AppointmentBaseModel form) throws ParseException {
        // 检查预约当天预约的医生是否排班
        ResponseResult dentistSchedulingConflict = this.checkScheduling(form);
        if (null != dentistSchedulingConflict){
            return dentistSchedulingConflict;
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
        return ResponseUtil.success(appointment);
    }


    /**
     * 删除预约（取消预约）
     * @param id   预约id
     * @param cause  取消预约原因
     * @return ResponseResult
     */
    public ResponseResult appointmentCancel(Integer id, String cause){
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
        appointment.setInservice(false);
        appointment.setAppointStatus((byte) 2);
        appointment.setRemarks(cause);
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
        return ResponseUtil.success();
    }

    /**
     * 编辑预约（出现冲突）
     * @param form  预约表单
     * @return ResponseResult
     */
    public ResponseResult updateAppointment(AppointmentBaseForm form){
        AppointmentBaseModel appointBaseModel = EntityUtils.build(form, AppointmentBaseModel.class);
        // 检查预约当天预约的医生是否排班
        ResponseResult dentistSchedulingConflict = this.checkScheduling(appointBaseModel);
        if (dentistSchedulingConflict != null){
            return dentistSchedulingConflict;
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
     * 编辑预约（有冲突继续保存）
     * @param appointmentForm 更新预约信息form
     * @return ResponseResult
     */
    public ResponseResult continueUpdateAppointment(AppointmentBaseForm appointmentForm) {
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
        Appointment appointment = mapper.selectByPrimaryKey(appointmentForm.getId());

        int num = mapper.updateByPrimaryKeySelective(appointEntity);
        if (num <= 0){
            return ResponseUtil.fail(AppointmentError.APPOINT_EDIT_FAIL.getCode(),AppointmentError.APPOINT_EDIT_FAIL.getMessage(),null);
        }

        // 保存预约更新被修改的日期、医生
        appointmentModifyRecordBiz.saveAppointModify(mapper.selectByPrimaryKey(appointmentForm.getId()),appointmentForm);
        // 修改时长分解
        List<AppointmentSplitUpdateBaseInfo> splitList = appointmentForm.getSplitList();
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
    public List<AppointmentListItemVo> findAppointmentListByExample(AppointListQuery query){
        // 按条件检索之后的列表
        List<AppointmentListItemVo> collect = null;
        // 没有经过检索的列表
        List<AppointmentListItemVo> appointmentList = new ArrayList<>();
        AppointmentQuery appointmentQuery = new AppointmentQuery();
        appointmentQuery.setAppointDate(query.getAppointDate());
        appointmentQuery.setAppointType(query.getAppointType());
        appointmentQuery.setOrgId(query.getOrgId());
        List<AppointmentVo> appointmentVos = mapper.findAppointmentByExample(appointmentQuery);
        // 设置预约医生/助手信息
        appointmentVos.forEach(appointmentVo -> {
            // 组合预约列表信息
            AppointmentListItemVo itemVo = this.combinationAppointListItemVo(appointmentVo);
            appointmentList.add(itemVo);
        });
        // 匹配姓名
        String patientNameReg = "^[\\u4e00-\\u9fa5]{0,}$";
        // 匹配手机号
        String mobileReg = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
        // 匹配拼音名字
        String pinyinNameReg = "^[A-Za-z]+$";
        if (query.getAppointType() != null
                || !StringHelper.isEmpty(query.getDentistName())
                || !StringHelper.isEmpty(query.getMedicalNumber())
                || !StringHelper.isEmpty(query.getSearch())){
            collect = appointmentList.stream()
                    .filter(
                            appointmentListItemVo -> {
                                boolean result = false;
                                // 按预约类型检索
                                if (query.getAppointType() != null && (query.getAppointType() == 0 || query.getAppointType() == 1)){
                                    result = appointmentListItemVo.getAppointType().equals(query.getAppointType());
                                }
                                // 按病历号检索
                                if (!StringHelper.isEmpty(query.getMedicalNumber())){
                                    result = result | appointmentListItemVo.getMedicalNumber().equals(query.getMedicalNumber());
                                }
                                // 按预约医生检索
                                if (!StringHelper.isEmpty(query.getDentistName())) {
                                    result = result | appointmentListItemVo.getDentistName().equals(query.getDentistName());
                                }
                                // 按姓名/手机号/姓名拼音
                                if (!StringHelper.isEmpty(query.getSearch())){
                                    // 检索值
                                    String search = query.getSearch();
                                    String mobile = appointmentListItemVo.getMobile();
                                    String patientName = appointmentListItemVo.getPatientName();
                                    String pinyinName = appointmentListItemVo.getPinyinName();
                                    // 按姓名检索
                                    if (search.matches(patientNameReg) && !StringHelper.isEmpty(patientName)){
                                        result = result | patientName.contains(search);
                                    } else if (search.matches(mobileReg) && !StringHelper.isEmpty(mobile)) {
                                        // 按手机号检索
                                        result = result |  mobile.equals(search);
                                    } else if (search.matches(pinyinNameReg) && !StringHelper.isEmpty(pinyinName)){
                                        // 按拼音名字检索
                                        result = result |  pinyinName.contains(search);
                                    }
                                }
                                return result;
                            }
                    ).collect(Collectors.toList());
        }
        // 如果不为空，则有内容过滤，返回过滤之后的结果
        if (collect != null) {
            return collect;
        }
        return appointmentList;
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
        Integer[] enableDentistIds = this.enableAppointDentistIds(query.getOrgId());
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
    private Integer[] enableAppointDentistIds(Integer orgId) {
        // 获取可预约的医生
        EnableEmployeeRes enableEmployeeList = this.clinicEmployeeConfigFeign.getEnableEmployeeList(orgId);
        List<EnableChooseEmployeeRes> enableAppointList = enableEmployeeList.getEnableAppointList();
        if (!StringHelper.isEmpty(enableAppointList)) {
            Integer[] enableDentistIds = new Integer[enableAppointList.size()];
            for (int index = 0; index < enableAppointList.size();index++) {
                enableDentistIds[index] = enableAppointList.get(index).getEmployeeId();
            }
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
    private List<AppointmentDimensionVo> dimensionAppointInfo(PatientDimensionByDayQuery query, Integer... enableDentistIds) {
        // 预约可视图列表
        List<AppointmentDimensionVo> appointmentDimensionVoList = new ArrayList<>();
        // 可预约医生ID列表
        List<Integer> appointIdList = Arrays.asList(enableDentistIds);
        // 查询当前天有排班的员工列表
        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr = simpleDateFormat.format(query.getStartDate());
        String endDateStr = simpleDateFormat.format(query.getEndDate());
        employeeScheduleQueryForm.setStartDate(startDateStr);
        employeeScheduleQueryForm.setEndDate(endDateStr);
        employeeScheduleQueryForm.setClinicId(query.getOrgId());
        employeeScheduleQueryForm.setUserId(query.getDentistId());
        EmployeeScheduleResultVO scheduleResultVO = employeeAttendServiceFeign.findList(employeeScheduleQueryForm);
        if (scheduleResultVO == null || scheduleResultVO.getCount() <= 0) {
            return null;
        }
        // 获取当天的所有医生的排班（包含有排班和没有排班的医生）
        List<UserWorkVO> shiftWorkDatas = scheduleResultVO.getShiftWorkDatas();
        List<Integer> unScheduleIds = new ArrayList<>();
        // 如果是查找多天则进行不过滤操作（适配预约修改画面中预约医生一周的预约情况）
        if (startDateStr.equals(endDateStr)) {
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
                userWorkVO -> appointIdList.contains(userWorkVO.getCompEmpId())).collect(Collectors.toList());
        Integer orgId = query.getOrgId();
        Date startDate = query.getStartDate();
        Date endDate = query.getEndDate();
        for (UserWorkVO userWorkVO : filterAppointIds) {
            // 组合预约医生和患者信息（患者维度）
            List<AppointmentDimensionVo> dimensionVoList = this.combinationPatientDimensionVo(orgId, startDate, endDate, userWorkVO);
            // 将预约信息放入预约可视图列表
            if (!dimensionVoList.isEmpty()) {
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
        if (!StringHelper.isEmpty(appointmentDimensionVoList)) {
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
        Integer[] enableDentistIds = this.enableAppointDentistIds(query.getOrgId());
        // 组合预约中心预约信息（包含预约医生，护士的排班以及预约人数）
        List<AppointmentDimensionVo> appointmentDimensionVos = this.dimensionAppointInfo(query,enableDentistIds);
        // 最后进行排序
        List<AppointmentDimensionVo> appointmentDimensionVosSort = this.sort(appointmentDimensionVos,query.getOrder(),query.getOrderBy());

        if (StringHelper.isEmpty(appointmentDimensionVosSort)) {
            return ResponseUtil.fail(AppointmentError.DENTIST_NOT_SCHEDULE.getCode(),AppointmentError.DENTIST_NOT_SCHEDULE.getMessage(),null);
        }
        // 根据大医生id查询相关助手信息并且设置助手信息
        appointmentDimensionVos.forEach(appointmentDimensionVo -> {
            // 组合患者预约维度信息（预约患者信息+医生排班信息）
            AppointmentDimensionVo appointmentDimensionItem = this.combinationDentistDimensionVo(query.getOrgId(), appointmentDimensionVo);
            // 最后将分解之后的整个大医生+助手放入到视图模型中
            appointmentDentistDimensionVoList.add(appointmentDimensionItem);
        });

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
        PatientBaseInfo patientBaseInfo = this.patientCentralServiceFeign.findPatientInfoById(appointmentVo.getPatientId());
        if (null != patientBaseInfo) {
            // 设置患者名字
            String name = patientBaseInfo.getName();
            appointmentVo.setPatientName(name);
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
    public ResponseResult confirmAppointment(Integer id){
        Appointment appointment = mapper.selectByPrimaryKey(id);
        AppointOperationModel appointOperationModel = new AppointOperationModel();
        if (appointment == null){
            return ResponseUtil.fail(AppointmentError.APPOINT_DATA_NOT_EXIST.getCode(),AppointmentError.APPOINT_DATA_NOT_EXIST.getMessage(),null);
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
            if (null != responseResult) {
                return responseResult;
            }
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
                PatientBaseInfo patientInfo = this.patientCentralServiceFeign.findPatientInfoById(patientId);
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
            // 添加预约医生冲突检测
            dentisList = mapper.editCheckDentistConflict(id,dentistId,appointStartTime,appointEndTime);
        } else {
            // 修改预约医生冲突检测
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
                PatientBaseInfo patientInfo = this.patientCentralServiceFeign.findPatientInfoById(conflictPatientId);
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
                PatientBaseInfo patientInfo = this.patientCentralServiceFeign.findPatientInfoById(conflictPatientId);
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
                PatientBaseInfo patientInfo = this.patientCentralServiceFeign.findPatientInfoById(conflictPatientId);
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
                PatientBaseInfo patientInfo = this.patientCentralServiceFeign.findPatientInfoById(conflictPatientId);
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

        // 设置预约类型(0-初诊；1-复诊)
        // 根据患者是否有病历号来判断患者预约类型
        PatientBaseInfo patientBaseInfo = patientCentralServiceFeign.findPatientInfoById(appointment.getPatientId());
        String medicalNumber = patientBaseInfo.getMedicalNumber();
        if (StringHelper.isEmpty(medicalNumber)){
            // 病历号为空，初诊
            appointment.setAppointType((byte)0);
        } else {
            // 病历号不为空，复诊
            appointment.setAppointType((byte)1);
        }
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
        List<AppointmentSplitUpdateBaseInfo> splitList = appointmentForm.getSplitList();
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
            if (null != responseResult) {
                return responseResult;
            }
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
        List<AppointmentListItemVo> appointmentListItemVoList = this.findAppointmentListByExample(listQuery);
        // 预约列表信息
        List<AppointListExportVo> appointListExportVos = new ArrayList<>();
        if (appointmentListItemVoList != null && !appointmentListItemVoList.isEmpty()){
            List<AppointListExportVo> appointListExportVoList = new ArrayList<>();
            // 设置预约患者信息
            appointmentListItemVoList.forEach(appointmentListItemVo -> {
               AppointListExportVo appointListExportVo = appointListItemTransformExportEntity(appointmentListItemVo);
                appointListExportVoList.add(appointListExportVo);
            });
            // 对预约列表信息排序
            appointListExportVos = appointListExportVoList.stream().sorted(Comparator.comparingInt(AppointListExportVo::getDentistId)).collect(Collectors.toList());
        }

        Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());

        SimpleDateFormat exportAppointDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String exportAppointDate = exportAppointDateFormat.format(new Date(System.currentTimeMillis()));
        // 合并行
        List<CellRangeAddress> mergeCells = new ArrayList<>();
        // 将列表中第一个医生的名字作为初始值
        String firstDentistName = appointListExportVos.get(0).getDentistName();
        int firstRow = 1;
        int lastRow = 1;
        int firstCol = 0;
        int lastCol = 0;
        boolean isSameName = false;
        for (int index = 0; index < appointListExportVos.size() - 1; index++){
            String nextDentistName = appointListExportVos.get(index + 1).getDentistName();
            // 如果第一个医生和下一个医生是同一个医生，lastRow + 1
            if (!firstDentistName.equalsIgnoreCase(nextDentistName)){
                // 判断是否存在合并行， 如果lastRow - firstRow > 1 说明存在合并行 ，进行合并
                if (lastRow - firstRow >= 1 && isSameName) {
                    CellRangeAddress mergeCell = new CellRangeAddress(firstRow,lastRow,firstCol,lastCol);
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
        appointExcelExport.exportExcel(response,appointListExportVos,excelName,excelName);
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
        appointListExportVo.setAppointType(appointmentListItemVo.getAppointType());
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
                    operationRecordContent = "[" + crtTime + "]" +  "修改了【" + appointOperationRecordVo.getRemarks() +
                            "】，将\"" + appointOperationRecordVo.getBeforeOperation() + "\"改成了\"" +
                            appointOperationRecordVo.getAfterOperation() + "\"";
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
     *
     * @param queryForm 查询条件
     * @return list
     */
    public PageInfo<AppointmentUnDonePatientInfoVO> findUnComingAppointmentList(
            AppointmentCurrentListQuery queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        List<AppointmentUnDonePatientInfoVO> resultList =
                mapper.selectAppointmentUnDonePatientInfoList(queryForm);
        if (resultList.size() > 0) {
            String redisKeyAppointmentUnDone = RedisConstants.REDIS_KEY_APPOINTMENT_UN_DONE;
            resultList.forEach(
                    vo -> {
                        // 设置患者信息
                        setPatientInfo(vo);
                        // 设置预约信息
                        setAppointmentInfo(vo);
                        // 将预约未到患者信息设置到缓存
                        redisUtils.set(redisKeyAppointmentUnDone + vo.getId(), vo);
                    });
        }
        return new PageInfo<>(resultList);
    }

    /**
     * 设置候诊患者患者信息
     * @param vo 患者候诊
     */
    private void setPatientInfo(AppointmentUnDonePatientInfoVO vo) {
        Integer patientId = vo.getPatientId();
        PatientTotalInfoVo patientData = patientCentralServiceFeign.findPatientTotalInfo(patientId);
        if (null != patientData) {
            vo.setPatientName(patientData.getName());
            vo.setMobile(patientData.getMobile());
            vo.setGender(patientData.getGender());
            vo.setAge(patientData.getAge());
            vo.setBirthday(patientData.getBirthday());
            vo.setPatientRemark(patientData.getRemarks());
            String medicalNumber = patientData.getMedicalNumber();
            vo.setMedicalNumber(StringHelper.isNotBlank(medicalNumber) ? medicalNumber : "--");
            vo.setAllergenDescription(patientData.getAllergensDescriptions());
            vo.setAllergen(patientData.getAllergens());
            vo.setPatientKind(patientData.getPatientKind());
            Integer memberTypeId = patientData.getMemberTypeId();
            if (null != memberTypeId) {
                MemberType memberType = remoteSystemServiceFeign.findMemberTypeById(memberTypeId);
                if (null != memberType) {
                    vo.setMemberIcon(String.valueOf(memberType.getIcon()));
                }
            }
        }
    }

    /**
     * 设置预约未到患者相关的医生、科室信息
     * @param vo 预约未到患者信息
     */
    private void setAppointmentInfo(AppointmentUnDonePatientInfoVO vo) {
        Integer dentistId = vo.getAppointDentistId();
        SysUserInfoDetail dentistInfo =
                remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(dentistId);
        if (null != dentistInfo) {
            vo.setAppointDentistName(dentistInfo.getName());
        }

        Integer assistantId = vo.getAppointAssistantId();
        if (null != assistantId) {
            SysUserInfoDetail assistantInfo =
                    remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(assistantId);
            if (null != assistantInfo) {
                vo.setAppointAssistantName(assistantInfo.getName());
            }
        }
        Integer deptRoomId = vo.getAppointDeptRoomId();
        if (null != deptRoomId) {
            DepartmentRoom departmentRoom = remoteSystemServiceFeign.findDepartmentRoomById(deptRoomId);
            if (null != departmentRoom) {
                vo.setAppointDeptRoomName(departmentRoom.getName());
            }
        }
    }


    /**
     * 组合患者预约医生维度信息（预约患者信息+医生排班信息）
     * @param orgId 门诊id
     * @param appointmentDimensionVo 患者信息视图信息
     * @return AppointmentDentistDimensionVo
     */
    private AppointmentDimensionVo combinationDentistDimensionVo(Integer orgId,AppointmentDimensionVo appointmentDimensionVo) {
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
                List<AppointmentSplitVo> appointSplitVo = appointmentSplitBiz.findAppointmentSplitByExample(splitQuery);
                if (!StringHelper.isEmpty(appointSplitVo)) {
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
                            AppointmentDimensionVo assistantPatientInfo = this.setAppointAssistantInfo(appointmentPatientCardVo, appointmentSplitVo);
                            // 设置预约助手下的患者数量，每循环一次加1；
                            if(null != assistantPatientInfo) {
                                Integer patientNum = assistantPatientInfo.getPatientNum();
                                patientNum = (null == patientNum ? 0 : patientNum);
                                assistantPatientInfo.setPatientNum(++patientNum);

                                // 将助手信息放入助手预约信息列表中
                                assistantAppointList.add(assistantPatientInfo);
                            }
                        }
                    }
                    // 将助手预约信息设置到医生维度信息实体中
                    appointmentDimensionVo.setAppointmentAssistants(assistantAppointList);
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
                    Byte aByte = this.getTreatmentStation(appointmentPatientCardVo.getId(), appointmentPatientCardVo.getPatientId());
                    // 设置大医生下所有的患者就诊状态
                    appointmentPatientCardVo.setStation(aByte);
                }
            }
            System.out.println(appointmentDimensionVo.toString());
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
     * @return 医生维度视图模型
     */
    private AppointmentDimensionVo setAppointAssistantInfo(AppointmentPatientCardVo appointmentPatientCardVo,AppointmentSplitVo appointmentSplitVo) {
        // 通过feign查询助手详细信息
        SysUserInfoDetail assistantDetailInfo = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(appointmentSplitVo.getAssistantId());
        // 助手患者信息
        if (null != assistantDetailInfo) {
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
            Byte aByte = this.getTreatmentStation(appointmentPatientCardVo.getId(), appointmentPatientCardVo.getPatientId());
            // 设置助手下患者就诊流程的状态 0-待挂号状态；1-就诊中状态；2-就诊完成状态
            assistantPatientCardInfo.setStation(aByte);
            // 将助手患者信息设置到患者信息列表中
            assistantPatientCardList.add(assistantPatientCardInfo);
            // 将患者信息列表设置到医生维度信息实体中
            assistantPatientInfo.setAppointmentPatientCardVos(assistantPatientCardList);

            // 设置助手排班信息
            EmployeeScheduleQueryForm scheduleQueryForm = new EmployeeScheduleQueryForm();
            scheduleQueryForm.setUserId(assistantDetailInfo.getUserId());
            scheduleQueryForm.setName(assistantDetailInfo.getName());
            Date appointDate = appointmentPatientCardVo.getAppointDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format = dateFormat.format(appointDate);
            scheduleQueryForm.setStartDate(format);
            scheduleQueryForm.setEndDate(format);
            // 查询助手排班信息，查询预约当天助手排班
            EmployeeScheduleResultVO employeeScheduleResultVO = this.employeeAttendServiceFeign.findList(scheduleQueryForm);
            if (employeeScheduleResultVO.getCount() > 0) {
                List<UserWorkVO> shiftWorkDatas = employeeScheduleResultVO.getShiftWorkDatas();
                if (!StringHelper.isEmpty(shiftWorkDatas)) {
                    List<WorkDayVO> days = shiftWorkDatas.get(0).getDays();
                    assistantPatientInfo.setDentistScheduleVos(days);
                }
            }
            return assistantPatientInfo;
        }
        return null;
    }

    /**
     * 设置患者就诊流程的状态
     * @return 返回就诊状态 0-待挂号状态；1-就诊中状态；2-就诊完成状态
     */
    private Byte getTreatmentStation(Integer appointId, Integer patientId) {
        Registered registered = new Registered();
        registered.setAppointmentId(appointId);
        registered.setPatientId(patientId);
        // 查询挂号患者信息
        Registered registeredByExample = this.remoteTreatmentServiceFeign.findRegisteredByExample(registered);

        if (null != registeredByExample) {
            TreatmentRecord treatmentRecord = new TreatmentRecord();
            treatmentRecord.setAppointmentId(appointId);
            treatmentRecord.setPatientId(patientId);
            treatmentRecord.setRegisteredId(registeredByExample.getId());
            // 查询患者就诊信息
            TreatmentRecord treatmentRecordByExample = this.remoteTreatmentServiceFeign.findTreatmentRecordByExample(treatmentRecord);
            byte splitStatus = 2;
            if (null != treatmentRecordByExample) {
                // 诊疗状态(0-接诊中;1-已开单;2-接诊完成3-已结账)
                Byte type = treatmentRecordByExample.getStatus();
                // 诊疗状态为 “0-接诊中;1-已开单”时返回 1(就诊中状态)
                if (type < splitStatus) {
                    return 1;
                } else {
                    // 诊疗状态为 “2-接诊完成3-已结账”时返回 2(就诊完成状态)
                    return 2;
                }
            }
        }
        // 如果没有挂号信息则设置患者就诊状态为 0-待挂号
        return 0;
    }

    /**
     * 组合患者预约医生维度信息（预约患者信息+医生排班信息）
     * @param orgId 门诊id
     * @param appointmentDimensionVo 患者信息视图信息
     * @return AppointmentDentistDimensionVo
     */
    @Deprecated
    private AppointmentDimensionVo combinationDentistDimensionVo_Master(Integer orgId,AppointmentDimensionVo appointmentDimensionVo) {
        // 患者信息列表
        List<AppointmentDimensionVo> assistantInfoList = new ArrayList<>();
        AppointmentDimensionVo assistantSplitVo = EntityUtils.build(appointmentDimensionVo, AppointmentDimensionVo.class);
        AppointmentSplitQuery splitQuery = new AppointmentSplitQuery();

        List<AppointmentPatientCardVo> patientCardVos = assistantSplitVo.getAppointmentPatientCardVos();
        if (patientCardVos != null && !patientCardVos.isEmpty()){
            patientCardVos.forEach(appointmentPatientCardVo -> {
                // 设置预约id
                splitQuery.setAppointmentId(appointmentPatientCardVo.getId());
                // 设置门诊id
                splitQuery.setOrgId(orgId);
                // 设置预约时间
                splitQuery.setAppointDate(appointmentDimensionVo.getCurrentDate());
                // 查询本次预约相关的分解助手信息
                List<AppointmentSplitVo> appointSplitVo = appointmentSplitBiz.findAppointmentSplitByExample(splitQuery);
                // 设置分解助手的患者预约信息
                List<AppointmentPatientCardVo> appointmentPatientCardVos = new ArrayList<>();
                if (appointSplitVo != null && !appointSplitVo.isEmpty()){
                    AppointmentDimensionVo assistentPatientInfo = new AppointmentDimensionVo();
                    appointSplitVo.forEach(appointmentSplitVo -> {
                        // 设置助手id
                        assistentPatientInfo.setDentistId(appointmentSplitVo.getAssistantId());
                        // 通过feign查询助手详细信息
                        SysUserInfoDetail assistantDetailInfo = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(appointmentSplitVo.getAssistantId());
                        if (assistantDetailInfo != null){
                            // 设置助手名字
                            assistentPatientInfo.setName(assistantDetailInfo.getName());
                            String splitStartTime = appointmentSplitVo.getSplitStartTime();
                            String splitEndTime = appointmentSplitVo.getSplitEndTime();
                            // 助手预约时间段
                            String splitTime = splitStartTime + "-" + splitEndTime;
                            appointmentPatientCardVo.setAppointTime(splitTime);
                        }
                        // 将助手的患者信息放入列表
                        appointmentPatientCardVos.add(appointmentPatientCardVo);
                    });
                    // 设置助手信息所有患者信息
                    assistentPatientInfo.setAppointmentPatientCardVos(appointmentPatientCardVos);

                    assistantInfoList.add(assistentPatientInfo);
                    for (int index = 0; index < appointSplitVo.size(); index++){
                        AppointmentSplitVo appointmentSplitVo = appointSplitVo.get(index);
                        // 设置助手id
                        assistantSplitVo.setDentistId(appointmentSplitVo.getAssistantId());
                        // 通过feign查询助手详细信息
                        SysUserInfoDetail assistantDetailInfo = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(appointmentSplitVo.getAssistantId());
                        if (assistantDetailInfo != null){
                            // 设置助手名字
                            assistantSplitVo.setName(assistantDetailInfo.getName());
                            String splitStartTime = appointmentSplitVo.getSplitStartTime();
                            String splitEndTime = appointmentSplitVo.getSplitEndTime();
                            // 助手预约时间段
                            String splitTime = splitStartTime + "-" + splitEndTime;
                            appointmentPatientCardVo.setAppointTime(splitTime);
                        }
                    }
                }
                appointmentPatientCardVos.add(appointmentPatientCardVo);
            });
        }
        // 将分解之后的助手信息放入助手集合中
        assistantInfoList.add(assistantSplitVo);
        assistantSplitVo.setAppointmentAssistants(assistantInfoList);
        // 对助手信息进行排序   按患者数量排序
        List<AppointmentDimensionVo> sortByDescList = assistantInfoList.stream().sorted(Comparator.comparing(AppointmentDimensionVo::getPatientNum).reversed()).collect(Collectors.toList());
        appointmentDimensionVo.setAppointmentAssistants(sortByDescList);
        return appointmentDimensionVo;
    }

    /**
     * 组合预约医生和患者信息（患者维度）
     * @param orgId  门诊id
     * @param startDate  排班开始时间
     * @param endDate  排班结束时间
     * @param dentistWorkSchedule 排班表
     * @return  预约可视图vo
     */
    private List<AppointmentDimensionVo> combinationPatientDimensionVo(Integer orgId, Date startDate, Date endDate,UserWorkVO dentistWorkSchedule){
        List<AppointmentDimensionVo> appointmentDimensionVoList = new LinkedList<>();
        Integer userId = dentistWorkSchedule.getCompEmpId();
        String dentistName = dentistWorkSchedule.getName();
        // 根据排班日期和医生id查询患者信息 医生（一）-----> 患者（多）
        List<AppointmentDimensionVo> appointmentDimensionVos = mapper.findAppointmentDimensionInfoByDateAndDentistId(
                startDate,endDate,userId,orgId);
        // 如果医生有预约则设置该医生的预约信息
        if (appointmentDimensionVos != null && !appointmentDimensionVos.isEmpty()){
            appointmentDimensionVos.forEach(appointmentDimensionVo -> {
                appointmentDimensionVo.setName(dentistName);
                // 设置排班日期
                dentistWorkSchedule.getDays().forEach(workDayVO -> appointmentDimensionVo.setCurrentDate(workDayVO.getDate()));
                // 设置医生排班信息
                appointmentDimensionVo.setDentistScheduleVos(dentistWorkSchedule.getDays());
                // 组合患者基本信息
                List<AppointmentPatientCardVo> appointmentPatientCardVos = appointmentDimensionVo.getAppointmentPatientCardVos();
                appointmentPatientCardVos.forEach(appointmentPatientCardVo -> {
                    Integer appointId = appointmentPatientCardVo.getId();
                    Integer patientId = appointmentPatientCardVo.getPatientId();
                    PatientBaseInfo patientInfo = patientCentralServiceFeign.findPatientInfoById(patientId);
                    if (patientInfo != null) {
                        appointmentPatientCardVo.setAge(patientInfo.getAge());
                        appointmentPatientCardVo.setGender(patientInfo.getGender());
                        appointmentPatientCardVo.setName(patientInfo.getName());
                        // 获取大医生下患者当前处于哪个就诊状态
                        Byte aByte = this.getTreatmentStation(appointId, appointmentPatientCardVo.getPatientId());
                        // 设置大医生下所有的患者就诊状态
                        appointmentPatientCardVo.setStation(aByte);
                    }
                });
                appointmentDimensionVoList.add(appointmentDimensionVo);
            });
        } else {
            // 如果该医生在时间段内没有预约，则只设置医生信息和排班信息，不设置患者预约信息
            AppointmentDimensionVo appointmentDimensionVoNull = new AppointmentDimensionVo();
            appointmentDimensionVoNull.setDentistId(userId);
            appointmentDimensionVoNull.setName(dentistName);
            // 设置排班时间
            dentistWorkSchedule.getDays().forEach(workDayVO -> appointmentDimensionVoNull.setCurrentDate(workDayVO.getDate()));
            // 设置医生排班信息
            appointmentDimensionVoNull.setDentistScheduleVos(dentistWorkSchedule.getDays());
            appointmentDimensionVoList.add(appointmentDimensionVoNull);
        }
        return appointmentDimensionVoList;
    }

    /**
     * 组合预约列表信息
     * @param appointmentVo  患者预约信息
     * @return 预约列表Vo
     */
    private AppointmentListItemVo combinationAppointListItemVo(AppointmentVo appointmentVo){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        AppointmentListItemVo build = EntityUtils.build(appointmentVo, AppointmentListItemVo.class);
        // 查询预约医生信息
        Integer dentistId = build.getDentistId();
        if (dentistId != null) {
            SysUserInfoDetail dentistInfoDetail = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(dentistId);
            if (dentistInfoDetail != null){
                build.setDentistName(dentistInfoDetail.getName());
            }
        }

        // 查询预约助手信息
        Integer assistantId = build.getAssistantId();
        if (assistantId != null) {
            SysUserInfoDetail assistantInfoDetail = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(assistantId);
            if (assistantInfoDetail != null){
                build.setAssistantName(assistantInfoDetail.getName());
            }
        }

        // 设置患者详细信息
        Integer patientId = build.getPatientId();
        if (patientId != null){
            PatientTotalInfoVo patientInfo = patientCentralServiceFeign.findPatientTotalInfo(patientId);
            if (patientInfo != null){
                build.setAge(patientInfo.getAge());
                try {
                    Date parse = dateFormat.parse(patientInfo.getBirthday());
                    build.setBirthday(dateFormat.format(parse));
                } catch (ParseException e) {
                    throw new ClientServiceException("时间格式转化异常！",OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
                }
                build.setGender(patientInfo.getGender());
                build.setMedicalNumber(patientInfo.getMedicalNumber());
                build.setMobile(patientInfo.getMobile());
                build.setPatientId(patientInfo.getId());
                build.setPatientName(patientInfo.getName());
                build.setPatientRemark(patientInfo.getRemarks());
                build.setAllergen(patientInfo.getAllergens());
                build.setPinyinName(patientInfo.getPinyinName());
                // 设置会员卡图标类型
                Integer memberTypeId = patientInfo.getMemberTypeId();
                if (memberTypeId != null) {
                    MemberType memberType = remoteSystemServiceFeign.findMemberTypeById(memberTypeId);
                    if (memberType != null){
                        build.setMemberIcon(String.valueOf(memberType.getIcon()));
                    }
                }
            }
        }

        // 设置默认科室信息
        Integer deptRoomId = appointmentVo.getDeptRoomId();
        if (deptRoomId != null) {
            DepartmentRoom departmentRoom = remoteSystemServiceFeign.findDepartmentRoomById(deptRoomId);
            if (departmentRoom != null){
                build.setClinicDeptRoomName(departmentRoom.getName());
            }
        }

        // 欠费金额 服务还没做，先空着，后面补上 TODO

        return build;
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
        appointPatientRecord.forEach(appointPatientRecordVo -> {
            // 设置医生名字
            Integer dentistId = appointPatientRecordVo.getDentistId();
            if (null != dentistId) {
                SysEmployee dentistInfo = this.remoteSystemServiceFeign.findSysEmployeeById(dentistId);
                if (null != dentistInfo) {
                    appointPatientRecordVo.setDentistName(dentistInfo.getName());
                }
            }
            // 设置助手名字
            Integer assistantId = appointPatientRecordVo.getAssistantId();
            if (null != assistantId) {
                SysEmployee assistantInfo = this.remoteSystemServiceFeign.findSysEmployeeById(assistantId);
                if (null != assistantInfo) {
                    appointPatientRecordVo.setAssistantName(assistantInfo.getName());
                }
            }
            // 设置门诊名称
            Integer orgId = appointPatientRecordVo.getOrgId();
            if (null != orgId) {
                OrganizationInfo orgInfo = this.remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
                if (null != orgInfo) {
                    appointPatientRecordVo.setOrgName(orgInfo.getName());
                }
            }
            // 设置科室名称
            Integer deptRoomId = appointPatientRecordVo.getDeptRoomId();
            if (null != deptRoomId) {
                DepartmentRoom departmentRoomInfo = this.remoteSystemServiceFeign.findDepartmentRoomById(deptRoomId);
                if (null != departmentRoomInfo) {
                    appointPatientRecordVo.setDeptRoomName(departmentRoomInfo.getName());
                }
            }
            // 设置设备编号
            Integer clinicDeviceItemId = appointPatientRecordVo.getClinicDeviceItemId();
            if (null != clinicDeviceItemId) {
                DeviceItemVo deviceItemVo = this.clinicDeviceItemBiz.selectDeviceItemById(clinicDeviceItemId);
                if (null != deviceItemVo) {
                    appointPatientRecordVo.setClinicDeviceItemNumber(deviceItemVo.getNumber());
                }
            }

        });
        // 按医生名字检索
        if (!StringHelper.isEmpty(query.getDentistName())) {
            appointPatientRecord = appointPatientRecord.stream().filter(
                    appointPatientRecordVo -> appointPatientRecordVo
                            .getDentistName().contains(query.getDentistName())).collect(Collectors.toList());
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
}

