package com.yunya.modules.appointment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.base.AppointmentSplitBaseInfo;
import com.yunya.feign.appointment.domain.base.AppointmentSplitUpdateBaseInfo;
import com.yunya.feign.appointment.domain.form.AppointmentBaseForm;
import com.yunya.feign.appointment.domain.form.AppointmentCancelCauseForm;
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
import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.PatientTotalInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
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
import com.yunya.modules.appointment.mapper.AppointmentMapper;
import org.apache.poi.ss.util.CellRangeAddress;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(AppointmentBiz.class);

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
        Map<String, Object> dentistSchedulingConflict = this.checkScheduling(form);
        if (null != dentistSchedulingConflict){
            return ResponseUtil.success(dentistSchedulingConflict);
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

            List<AppointmentSplitBaseInfo> splitList = form.getSplitList();
            // 添加预约时长分解
            if (form.getSplitList() != null || !splitList.isEmpty()){
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
                Integer splitResult = appointmentSplitBiz.insertAppointSplit(splitModel);
                if (splitResult == null || splitResult <= 0){
                    throw new ClientServiceException( "分解时长失败！",OperationCodeConstants.OBJECT_EDIT_FAIL);
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
    public Appointment updateAppointStatus(Integer id, Byte appointState, String remarks) {
        Appointment appointment = mapper.selectByPrimaryKey(id);
        if (appointment == null){
            throw new ClientServiceException("预约不存在！",OperationCodeConstants.DATA_NOT_EXIST);
        }

        // 预约未到以外的情况不能编辑预约
        if (appointment.getAppointStatus() != 0){
            throw new ClientServiceException("【预约未到】以外的情况不允许编辑预约！",OperationCodeConstants.OBJECT_EDIT_FAIL);
        }

        // inservice 无效时预约不可以编辑
        if (!appointment.getInservice()){
            throw new ClientServiceException("无效预约，不能进行编辑！",OperationCodeConstants.OBJECT_EDIT_FAIL);
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
     * @return ResponseResult
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
     * @return ResponseResult
     */
    public ResponseResult updateAppointment(AppointmentBaseForm form){
        AppointmentBaseModel appointBaseModel = EntityUtils.build(form, AppointmentBaseModel.class);
        // 检查预约当天预约的医生是否排班
        Map<String, Object> dentistSchedulingConflict = this.checkScheduling(appointBaseModel);
        if (dentistSchedulingConflict.get("errMsg") != null){
            return ResponseUtil.success(dentistSchedulingConflict);
        }
        // 检查预约冲突（只检查医生预约冲突、设备预约冲突）
        Map<String, Object> objectMap = editCheckConflict(form.getId(), form);
        if (null == objectMap) {
            Appointment beforeModifyAppointment = mapper.selectByPrimaryKey(form.getId());
            appointmentModifyRecordBiz.saveAppointModify(beforeModifyAppointment,form);
            // 转换预约内容
            Appointment appointment = transferFormToEntity(appointBaseModel);
            appointment.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
            appointment.setUpdName(BaseContextHandler.getName());
            appointment.setUpdTime(new Date(System.currentTimeMillis()));
            appointment.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
            Appointment beforeModifyAppoints = mapper.selectByPrimaryKey(appointment.getId());
            if (beforeModifyAppoints == null){
                throw new ClientServiceException("已经存在相同的预约！",OperationCodeConstants.SAME_DATA_EXIST);
            }
            // 预约未到以外的情况不能编辑预约
            if (appointment.getAppointStatus() != 0){
                throw new ClientServiceException("【预约未到】以外的情况不允许编辑预约！",OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
            // inservice 无效时预约不可以编辑
            if (!appointment.getInservice()){
                throw new ClientServiceException("无效预约，不能进行编辑！",OperationCodeConstants.OBJECT_EDIT_FAIL);
            }

            // 生成修改预约操作记录
            appointOperateRecordBiz.saveAppointOperationRecord(beforeModifyAppoints,form);

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
            return ResponseUtil.success();
        }
        // 返回冲突数据
        return ResponseUtil.success(objectMap);
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
            throw new ClientServiceException("【预约未到】以外的情况不允许编辑预约！",OperationCodeConstants.OBJECT_EDIT_FAIL);
        }

        // inservice 无效时预约不可以编辑
        if (!appointEntity.getInservice()){
            throw new ClientServiceException("无效预约，不能进行编辑！",OperationCodeConstants.OBJECT_EDIT_FAIL);
        }

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


    /**
     * 根据条件查询预约列表
     * @param query  查询条件
     * @return  预约列表
     */
    public List<AppointmentListItemVo> findAppointmentListByExample(AppointListQuery query){
        // 没有经过检索的列表
        List<AppointmentListItemVo> appointmentList = new ArrayList<>();
        AppointmentQuery appointmentQuery = new AppointmentQuery();
        appointmentQuery.setAppointDate(query.getAppointDate());
        appointmentQuery.setAppointType(query.getAppointType());
        List<AppointmentVo> appointmentVos = mapper.findAppointmentByExample(appointmentQuery);
        // 设置预约医生/助手信息
        appointmentVos.forEach(appointmentVo -> {
            // 组合预约列表信息
            AppointmentListItemVo itemVo = this.combinationAppointListItemVo(appointmentVo);
            appointmentList.add(itemVo);
        });

        // 按条件检索之后的列表
        List<AppointmentListItemVo> collect = appointmentList;
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

            collect = collect.stream()
                    .filter(
                            appointmentListItemVo -> {
                                boolean result = false;
                                // 按预约类型检索
                                if (query.getAppointType() != null && (query.getAppointType() == 0 || query.getAppointType() == 1)){
                                    result = result | appointmentListItemVo.getAppointType().equals(query.getAppointType());
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
                                    // 按姓名检索
                                    if (search.matches(patientNameReg)){
                                        result = result | appointmentListItemVo.getPatientName().contains(search);
                                    } else if (search.matches(mobileReg)) {
                                        // 按手机号检索
                                        result = result |  appointmentListItemVo.getMobile().equals(search);
                                    } else if (search.matches(pinyinNameReg)){
                                        // 按拼音名字检索
                                        result = result |  appointmentListItemVo.getPinyinName().contains(search);
                                    }
                                }
                                return result;
                            }
                    ).collect(Collectors.toList());
        }
        // 如果不为空，则有内容过滤，返回过滤之后的结果
        if (collect != null){
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
        // 预约可视图列表
        List<AppointmentDimensionVo> appointmentDimensionVoList = new ArrayList<>();
        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        employeeScheduleQueryForm.setStartDate(simpleDateFormat.format(query.getStartDate()));
        employeeScheduleQueryForm.setEndDate(simpleDateFormat.format(query.getEndDate()));
        employeeScheduleQueryForm.setClinicId(query.getOrgId());
        // 查询当前天有排班的员工列表
        EmployeeScheduleResultVO scheduleResultVO = employeeAttendServiceFeign.findList(employeeScheduleQueryForm);
        if (scheduleResultVO == null || scheduleResultVO.getCount() <= 0){
            return null;
        }
        // 组合医生和预约信息
        List<UserWorkVO> shiftWorkDatas = scheduleResultVO.getShiftWorkDatas();
        shiftWorkDatas.forEach(userWorkVO -> {
            // 组合预约医生和患者信息（患者维度）
            List<AppointmentDimensionVo> dimensionVoList = this.combinationPatientDimensionVo(query.getOrgId(), query.getStartDate(), query.getEndDate(), userWorkVO);
            // 将预约信息放入预约可视图列表
            if (dimensionVoList != null && !dimensionVoList.isEmpty()){
                dimensionVoList.forEach(dimensionVo -> appointmentDimensionVoList.add(dimensionVo));
            }
        });

        // 按患者预约数量升序排列
        // 按照患者数量排序
        String ORDER_PATIENTNUM = "patientNum";
        // 按照日期排序
        String ORDER_DATE = "date";
        // 升序
        String ORDER_BY_ASC = "asc";
        // 降序
        String ORDER_BY_DESC = "desc";
        if (ORDER_PATIENTNUM.equals(query.getOrder()) && ORDER_BY_ASC.equals(query.getOrderBy())){
            return appointmentDimensionVoList
                    .stream()
                    .sorted(Comparator.comparing(AppointmentDimensionVo::getPatientNum,
                            Comparator.nullsLast(Integer::compareTo))).collect(Collectors.toList());

        } else if (ORDER_PATIENTNUM.equals(query.getOrder()) && ORDER_BY_DESC.equals(query.getOrderBy())){
            // 按患者预约数量降序排列
            return appointmentDimensionVoList
                    .stream()
                    .sorted(Comparator.comparing(AppointmentDimensionVo::getPatientNum,
                            Comparator.nullsFirst(Integer::compareTo)).reversed()).collect(Collectors.toList());

        } else if (ORDER_DATE.equals(query.getOrder()) && ORDER_BY_ASC.equals(query.getOrderBy())){
            // 按日期升序排列
            return appointmentDimensionVoList
                    .stream()
                    .sorted(Comparator.comparing(AppointmentDimensionVo::getCurrentDate,
                            Comparator.nullsLast(Date::compareTo))).collect(Collectors.toList());

        } else if (ORDER_DATE.equals(query.getOrder()) && ORDER_BY_DESC.equals(query.getOrderBy())){
            // 按日期降序排列
            return appointmentDimensionVoList
                    .stream()
                    .sorted(Comparator.comparing(AppointmentDimensionVo::getCurrentDate,
                            Comparator.nullsFirst(Date::compareTo)).reversed()).collect(Collectors.toList());
        }
        // 没有排序直接返回
        return appointmentDimensionVoList;
    }

    /**
     * 预约可视图列表（医生维度）
     * 通过日期、门诊id、查询医生预约情况
     * @param query
     * @return
     */
    public List<AppointmentDentistDimensionVo> findAppointmentDentistDimensionByExample(PatientDimensionByDayQuery query){
        List<AppointmentDentistDimensionVo> appointmentDentistDimensionVoList = new ArrayList<>();
        // 预约医生列表
        List<AppointmentDimensionVo> appointmentDimensionVos = this.findAppointmentPatientDimensionByExample(query);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // 根据大医生id查询相关助手信息并且设置助手信息
        appointmentDimensionVos.forEach(appointmentDimensionVo -> {
            // 组合患者预约维度信息（预约患者信息+医生排班信息）
            AppointmentDentistDimensionVo appointDentistDimensionVo = this.combinationDentistDimensionVo(query.getOrgId(), appointmentDimensionVo);
            // 最后将分解之后的整个大医生+助手放入到视图模型中
            appointmentDentistDimensionVoList.add(appointDentistDimensionVo);
        });

        // 按患者数量对医生降序排序
        Collections.sort(appointmentDentistDimensionVoList, (o1, o2) -> {
            if (o1.getAppointmentDentistInfoVo().getPatientNum() != null && o2.getAppointmentDentistInfoVo().getPatientNum() != null){
                if (o1.getAppointmentDentistInfoVo().getPatientNum() > o2.getAppointmentDentistInfoVo().getPatientNum()) {
                    return -1;
                }
                if (o1.getAppointmentDentistInfoVo().getPatientNum() < o2.getAppointmentDentistInfoVo().getPatientNum()) {
                    return 1;
                }
            }
            return 0;
        });
        return appointmentDentistDimensionVoList;
    }

    /**
     * 根据id查询预约
     * @param id 预约id
     * @return 返回预约视图
     */
    public AppointmentVo findAppointmentById(Integer id){
        return mapper.findAppointmentById(id);
    }

    /**
     * 确认预约
     * @param id  预约id
     * @return 返回结果
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
    private Map<String,Object> checkScheduling(AppointmentBaseModel appointmentBaseModel){
        // 获取预约医生Id
        Integer dentistId = appointmentBaseModel.getDentistId();
        Map<String,Object> errMap = new HashMap<>(16);

        if (dentistId != null){
            EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();

            employeeScheduleQueryForm.setUserId(dentistId);
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
            // 获取排班列表
            EmployeeScheduleResultVO employeeScheduleResult = employeeAttendServiceFeign.findList(employeeScheduleQueryForm);
            if (employeeScheduleResult == null){
                errMap.put("errMsg","员工排班服务异常！");
                errMap.put("errData",employeeScheduleResult);
                return errMap;
            }
            // 预约医生没有排班，返回空
            if (employeeScheduleResult.getShiftWorkDatas().size() <= 0){
                errMap.put("errMsg","预约医生在预约日期当天未排班，建议排班后再新增预约！");
                errMap.put("errData",employeeScheduleResult);
                return errMap;
            }
            // 成功返回null
            return null;
        } else {
            errMap.put("errMsg", "预约医生id不能为空！");
            errMap.put("errData", null);
        }
        return errMap;
    }

    /**
     * 添加预约时检查是否存在预约冲突
     * @param appointmentForm 表单
     * @return Map<String,Object>
     * @throws ParseException 异常抛出
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
                    if (organizationInfo != null){
                        appointConflictInfoVo.setClinicName(organizationInfo.getName());
                        appointConflictInfoVo.setClinicNumber(organizationInfo.getClinicNumber());
                        appointConflictInfoVo.setAbbreviation(organizationInfo.getAbbreviation());
                    }
                });
                // 预约冲突返回冲突信息
                responseMapResult.put("errMsg","患者预约冲突！");
                responseMapResult.put("errData",patientList);
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
                    if (organizatioinInfo != null){
                        appointConflictInfoVo.setClinicName(organizatioinInfo.getName());
                        appointConflictInfoVo.setClinicNumber(organizatioinInfo.getClinicNumber());
                        appointConflictInfoVo.setAbbreviation(organizatioinInfo.getAbbreviation());
                    }
                });
                // 预约冲突返回冲突信息
                responseMapResult.put("errMsg","医生预约冲突！");
                responseMapResult.put("errData",dentisList);
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
                    if (organizationInfo != null){
                        appointConflictInfoVo.setClinicName(organizationInfo.getName());
                        appointConflictInfoVo.setAbbreviation(organizationInfo.getAbbreviation());
                        appointConflictInfoVo.setClinicNumber(organizationInfo.getClinicNumber());
                    }
                });
                // 预约冲突返回冲突信息
                responseMapResult.put("errMsg","设备预约冲突！");
                responseMapResult.put("errData",deviceList);
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
    private Map<String, Object> editCheckConflict(Integer id,  AppointmentBaseForm appointmentForm) {
        // 获取患者id、医生id、设备id、预约日期、时间、时长
        Integer patientId = appointmentForm.getPatientId();
        Integer dentistId = appointmentForm.getDentistId();
        Integer deviceId = appointmentForm.getClinicDeviceItemId();
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

        Map<String,Object> responseMapResult = new HashMap<>(16);

        if (patientId != null) {
            // 判断患者预约是否存在冲突
            List<AppointConflictInfoVo> appointConflictInfoVos =
                    mapper.editCheckPatientConflict(id, appointmentForm.getPatientId(), appointStartTime, appointEndTime);
            if (!appointConflictInfoVos.isEmpty()) {
                // 存在患者预约冲突
                responseMapResult.put("errMsg","患者预约冲突！");
                responseMapResult.put("errData",appointConflictInfoVos);
                return responseMapResult;
            }
        }
        // 判断医生预约是否存在冲突
        if (dentistId != null) {
            List<AppointConflictInfoVo> appointConflictInfoVos =
                    mapper.editCheckDentistConflict(id, appointmentForm.getPatientId(), appointStartTime, appointEndTime);
            if (!appointConflictInfoVos.isEmpty()) {
                // 存在医生预约冲突
                responseMapResult.put("errMsg","医生预约冲突！");
                responseMapResult.put("errData",appointConflictInfoVos);
                return responseMapResult;
            }
        }
        // 判断设备预约是否存在冲突
        if (deviceId != null) {
            List<AppointConflictInfoVo> appointConflictInfoVos =
                    mapper.editCheckDeviceConflict(id, deviceId, appointStartTime, appointEndTime);
            if (!appointConflictInfoVos.isEmpty()) {
                // 存在设备预约冲突
                responseMapResult.put("errMsg","设备预约冲突！");
                responseMapResult.put("errData",appointConflictInfoVos);
                return responseMapResult;
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
            StringBuilder operationRecordContentBuilder = new StringBuilder();

            Byte operateType = appointOperationRecordVo.getOperateType();
            switch (operateType){
                case 0:
                    operationRecordContentBuilder.append("[" + crtTime + "]");
                    operationRecordContentBuilder.append(appointOperationRecordVo.getCrtName());
                    operationRecordContentBuilder.append("新建了这条预约");
                    break;
                case 1:
                    operationRecordContentBuilder.append("[" + crtTime + "]");
                    operationRecordContentBuilder.append("修改了【");
                    operationRecordContentBuilder.append(appointOperationRecordVo.getRemarks());
                    operationRecordContentBuilder.append("】，将\"");
                    operationRecordContentBuilder.append(appointOperationRecordVo.getBeforeOperation());
                    operationRecordContentBuilder.append("\"改成了\"");
                    operationRecordContentBuilder.append(appointOperationRecordVo.getAfterOperation());
                    operationRecordContentBuilder.append("\"");
                    break;
                case 2:
                    operationRecordContentBuilder.append("[" + crtTime + "]");
                    operationRecordContentBuilder.append(appointOperationRecordVo.getCrtName());
                    operationRecordContentBuilder.append("取消了这条预约");
                    // 设置取消预约原因
                    appointListExportVo.setCancleReasion(appointOperationRecordVo.getRemarks());
                    break;
                case 3:

                    operationRecordContentBuilder.append("[" + crtTime + "]");
                    operationRecordContentBuilder.append(appointOperationRecordVo.getCrtName());
                    operationRecordContentBuilder.append("确认了这条预约");
                    break;
                case 4:
                    operationRecordContentBuilder.append("[" + crtTime + "]");
                    operationRecordContentBuilder.append(appointOperationRecordVo.getCrtName());
                    operationRecordContentBuilder.append("取消了这条预约的确认");
                    break;
                default:
            }
            appointListExportVo.setAppointOperationRecord(operationRecordContentBuilder.toString());
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
    private AppointmentDentistDimensionVo combinationDentistDimensionVo(Integer orgId,AppointmentDimensionVo appointmentDimensionVo) {
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
        // 对助手信息进行排序   按患者数量排序
        List<AppointmentDimensionVo> sortByDescList = assistantInfoList.stream().sorted(Comparator.comparing(AppointmentDimensionVo::getPatientNum).reversed()).collect(Collectors.toList());

        AppointmentDentistDimensionVo appointDentistDimensionVo = new AppointmentDentistDimensionVo();
        // 将大医生信息设置到医生维度信息模板中
        appointDentistDimensionVo.setAppointmentDentistInfoVo(appointmentDimensionVo);
        // 将分解的助手列表设置到分解信息列表中
        appointDentistDimensionVo.setAppointmentAssistants(sortByDescList);

        return appointDentistDimensionVo;
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
                    Integer patientId = appointmentPatientCardVo.getPatientId();
                    PatientBaseInfo patientInfo = patientCentralServiceFeign.findPatientInfoById(patientId);
                    if (patientInfo != null){
                        appointmentPatientCardVo.setAge(patientInfo.getAge());
                        appointmentPatientCardVo.setGender(patientInfo.getGender());
                        appointmentPatientCardVo.setName(patientInfo.getName());
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

}

