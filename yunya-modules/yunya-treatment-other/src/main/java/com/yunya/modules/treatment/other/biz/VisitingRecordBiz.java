package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.PatientTotalInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment_other.domain.form.FinishVisitingForm;
import com.yunya.feign.treatment_other.domain.form.VisitingRecordForm;
import com.yunya.feign.treatment_other.domain.model.VisitingContentModel;
import com.yunya.feign.treatment_other.domain.model.VisitingRecordModel;
import com.yunya.feign.treatment_other.domain.query.VisitingContentAfterCurrentQuery;
import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.feign.treatment_other.domain.vo.VisitingContentAfterCurrentVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingContentVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.system.DepartmentRoom;
import com.yunya.models.system.MemberType;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.modules.treatment.other.mapper.VisitingRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: yunya-dental
 * @description: 随访记录业务层
 * @author: LHB
 * @create: 2020-08-21 17:52
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class VisitingRecordBiz extends BaseBiz<VisitingRecordMapper, VisitingRecord> {

    @Autowired
    private RedisUtils redisUtils;

    /** 注入患者服务feign */
    @Autowired
    private PatientCentralServiceFeign patientCentralServiceFeign;

    /** 注入系统基础服务feign */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /** 注入就诊服务feign */
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;

    /** 注入预约服务feign */
    @Autowired
    private RemoteAppointmentFeign remoteAppointmentFeign;


    /**
     * 插入随访记录
     * @param model  随访记录表单
     * @return  返回插入成功的条数
     */
    public ResponseResult insertVisitingRecord(VisitingRecordModel model){
        List<VisitingContentModel> visitingContents = model.getVisitingContents();
        if (!StringHelper.isEmpty(visitingContents)){
            Integer patientId = model.getPatientId();
            for(VisitingContentModel visitingContentModel : visitingContents){
                Date visitingDate = visitingContentModel.getVisitingDate();
                VisitingRecordQuery query = new VisitingRecordQuery();
                query.setPatientId(patientId);
                query.setVisitingDate(visitingDate);
                List<VisitingRecordVo> visitingRecordByCondition = mapper.findVisitingRecordByCondition(query);
                if (visitingRecordByCondition != null && !visitingRecordByCondition.isEmpty()){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String format = dateFormat.format(query.getVisitingDate());
                    throw new ClientServiceException(format + " 的随访已经存在", OperationCodeConstants.DATA_EXIST);
                }
                VisitingRecord build = EntityUtils.build(model, VisitingRecord.class);
                build.setVisitingDate(visitingContentModel.getVisitingDate());
                build.setVisitingTime(visitingContentModel.getVisitingTime());
                build.setReason(visitingContentModel.getReason());
                build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                build.setCrtName(BaseContextHandler.getName());
                build.setCrtTime(new Date(System.currentTimeMillis()));
                int result = mapper.insertSelective(build);
                if (result <= 0){
                    return ResponseUtil.success("新增随访失败！");
                }
            }
            return ResponseUtil.success();
        }
        throw new ClientServiceException("随访内容列表为空！",OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY);
    }

    /**
     * 根据id删除随访记录
     * @param id  随访id
     */
    public ResponseResult deleteVisitingRecord(Integer id){
        VisitingRecord visitingRecord = mapper.selectByPrimaryKey(id);
        if (visitingRecord == null){
            return ResponseUtil.success("记录不存在！");
        }
        String lockStr = redisUtils.get(RedisConstants.LOCK_VISITING_RECORD);
        if (StringHelper.isEmpty(lockStr)) {
            redisUtils.setLock(RedisConstants.LOCK_VISITING_RECORD,String.valueOf(id),BusinessConstants.MEDICAL_APPLY_LOCK_SEC,TimeUnit.SECONDS);
            try {
                mapper.deleteByPrimaryKey(id);
            } finally {
                redisUtils.unlock(RedisConstants.LOCK_VISITING_RECORD,String.valueOf(id));
            }
            return ResponseUtil.success();
        }
        return ResponseUtil.success("该条记录正在编辑中，不能删除！");
    }

    /**
     * 根据就诊ID删除随访记录
     * @param id  就诊id
     */
    public void deleteVisitingRecordByTreatmentId(Integer id){
        mapper.deleteVisitingRecordByTreatmentId(id);
    }

    /**
     * 修改随访记录
     * @param form  修改表单
     * @return 修改条数
     */
    public ResponseResult updateVisitingRecord(VisitingRecordForm form){
        String updateId = String.valueOf(form.getId());
        String lockIdStr = redisUtils.get(RedisConstants.LOCK_VISITING_RECORD);
        VisitingRecord visitingRecord = mapper.selectByPrimaryKey(Integer.valueOf(updateId));
        if (visitingRecord == null){
            return ResponseUtil.success("记录不存在！");
        }

        // 检测随访记录是否有其他人在修改
        if (StringHelper.isEmpty(lockIdStr)){
            // 排他加锁
            redisUtils.setLock(RedisConstants.LOCK_VISITING_RECORD,updateId, BusinessConstants.MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
            try{
                VisitingRecord build = EntityUtils.build(form, VisitingRecord.class);
                build.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
                build.setUpdName(BaseContextHandler.getName());
                build.setUpdTime(new Date(System.currentTimeMillis()));
                mapper.updateByPrimaryKeySelective(build);
            } finally {
                // 释放锁
                redisUtils.unlock(RedisConstants.LOCK_VISITING_RECORD,updateId);
            }
        } else {
            return ResponseUtil.success("该条记录正在被修改中！");
        }
        return ResponseUtil.success();
    }

    /**
     * 根据随访id查询随访记录
     * @param id 随访id
     * @return ResponseResult
     */
    public ResponseResult findVisitingRecordById(Integer id){
        VisitingRecordVo visitingRecordVo = mapper.findVisitingRecordById(id);
        if (visitingRecordVo == null){
            return ResponseUtil.success();
        }
        visitingRecordVo = this.comboVisitingRecord(visitingRecordVo);
        return ResponseUtil.success(visitingRecordVo);
    }

    /**
     * 根据条件查询随访记录
     * @param query 查询条件
     * @return ResponseResult
     */
    public ResponseResult findVisitingRecordByCondition(VisitingRecordQuery query){
        // 随访记录结果列表
        List<VisitingRecordVo> visitingRecordVoList = new ArrayList<>();
        // 按指定条件检索之后的列表
        List<VisitingRecordVo> searchVisitingRecordVo = null;
        List<VisitingRecordVo> visitingRecordVos = mapper.findVisitingRecordByCondition(query);
        if (visitingRecordVos != null && !visitingRecordVos.isEmpty()){
            // 组合随访记录信息
            for(VisitingRecordVo visitingRecordVo : visitingRecordVos){
                VisitingRecordVo recordVo = this.comboVisitingRecord(visitingRecordVo);
                visitingRecordVoList.add(recordVo);
            }
            // 按患者姓名、手机号、病历号检索
            // 匹配姓名
            String patientNameReg = "^[\\u4e00-\\u9fa5]{0,}$";
            // 匹配手机号
            String mobileReg = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|16[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";

            String searchQuery = query.getSearch();
            String medicalNumberQuery = query.getMedicalNumber();
            if (!StringHelper.isEmpty(searchQuery) || !StringHelper.isEmpty(medicalNumberQuery)){
                searchVisitingRecordVo = visitingRecordVoList.stream().filter(visitingRecordVo -> {
                    String patientName = visitingRecordVo.getPatientName();
                    String mobile = visitingRecordVo.getMobile();
                    String medicalNumber = visitingRecordVo.getMedicalNumber();
                    boolean result = false;
                    if (searchQuery.matches(patientNameReg)){
                        result = result | patientName.contains(searchQuery);
                    } else if (searchQuery.matches(mobileReg)){
                        result = result | mobile.equals(searchQuery);
                    }
                    if (!StringHelper.isEmpty(medicalNumber)){
                        result = result | medicalNumber.equals(medicalNumberQuery);
                    }
                    return result;
                }).collect(Collectors.toList());
            } else {
                searchVisitingRecordVo = visitingRecordVoList;
            }
            // 按随访时间排序
            searchVisitingRecordVo = searchVisitingRecordVo.stream().sorted(
                    Comparator.comparing(VisitingRecordVo::getVisitingTime,(obj1,obj2)->{
                if (StringHelper.isEmpty(obj1) || StringHelper.isEmpty(obj2)){
                    return -1;
                }
                String[] objSplit1 = obj1.trim().split(":");
                Integer objMinute1 = Integer.parseInt(objSplit1[0]) * 60 + Integer.parseInt(objSplit1[1]);
                String[] objSplit2 = obj2.trim().split(":");
                Integer objMinute2 = Integer.parseInt(objSplit2[0]) * 60 + Integer.parseInt(objSplit2[1]);
                if (objMinute1 < objMinute2){
                    return -1;
                } else if (objMinute1 > objMinute2){
                    return 1;
                } else {
                    return 0;
                }
            })).collect(Collectors.toList());
        } else {
            return ResponseUtil.success();
        }
        if (query.getWhetherPage()){
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        return ResponseUtil.success(new PageInfo<>(searchVisitingRecordVo));
    }

    /**
     * 组合随访记录信息中的患者信息、会员图标信息、医生姓名、就诊信息
     * @param visitingRecordVo 随访记录
     * @return 返回组合之后的随访记录信息
     */
    public VisitingRecordVo comboVisitingRecord(VisitingRecordVo visitingRecordVo){
        // 组合患者信息
        Integer patientId = visitingRecordVo.getPatientId();
        if (patientId != null) {
            PatientTotalInfoVo patientTotalInfo = patientCentralServiceFeign.findPatientTotalInfo(patientId);
            if (patientTotalInfo != null){
                visitingRecordVo.setPatientName(patientTotalInfo.getName());
                visitingRecordVo.setMobile(patientTotalInfo.getMobile());
                visitingRecordVo.setGender(patientTotalInfo.getGender());
                visitingRecordVo.setBirthday(patientTotalInfo.getBirthday());
                visitingRecordVo.setAge(patientTotalInfo.getAge());
                visitingRecordVo.setAllergen(patientTotalInfo.getAllergens());
                visitingRecordVo.setPatientRemark(patientTotalInfo.getRemarks());
                visitingRecordVo.setMedicalNumber(patientTotalInfo.getMedicalNumber());
                // 设置会员图标信息
                Integer memberTypeId = patientTotalInfo.getMemberTypeId();
                if (memberTypeId != null) {
                    MemberType memberType = remoteSystemServiceFeign.findMemberTypeById(memberTypeId);
                    if (memberType != null){
                        visitingRecordVo.setMemberIcon(memberType.getIcon());
                    }
                }

                // 欠费总额 TODO
            }
        }

        // 设置医生姓名
        Integer dentistId = visitingRecordVo.getDentistId();
        if (dentistId != null){
            SysUserInfoDetail dentistInfo = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(dentistId);
            if (dentistInfo != null){
                visitingRecordVo.setDentistName(dentistInfo.getName());
            }
        }

        // 设置末诊科室信息
        Integer deptRoomId = visitingRecordVo.getDeptRoomId();
        if (deptRoomId != null) {
            DepartmentRoom departmentRoomInfo = remoteSystemServiceFeign.findDepartmentRoomById(deptRoomId);
            if (departmentRoomInfo != null){
                visitingRecordVo.setDeptRoomName(departmentRoomInfo.getName());
            }
        }

        // 设置就诊信息
        Integer treatmentId = visitingRecordVo.getTreatmentId();
        if (treatmentId != null) {
            TreatmentRecord treatmentRecord = remoteTreatmentServiceFeign.findTreatmentRecordById(treatmentId);
            if (treatmentRecord != null){
                visitingRecordVo.setTreatmentDate(treatmentRecord.getTreatEndTime());
            }
        }

        return visitingRecordVo;
    }

    /**
     * 随访内容（执行随访按钮用）
     * @param id 随访记录id
     * @return 随访内容
     */
    public ResponseResult executeVisiting(Integer id){
        VisitingRecord visitingRecord = mapper.selectByPrimaryKey(id);
        VisitingContentVo build = EntityUtils.build(visitingRecord, VisitingContentVo.class);
        // 组合患者信息
        Integer patientId = visitingRecord.getPatientId();
        if (patientId != null) {
            PatientTotalInfoVo patientTotalInfo = patientCentralServiceFeign.findPatientTotalInfo(patientId);
            if (patientTotalInfo != null){
                build.setPatientName(patientTotalInfo.getName());
                build.setMobile(patientTotalInfo.getMobile());
            }
            // 设置末次预约时间
            List<Appointment> patientAppointList = remoteAppointmentFeign.findAppointmentByPatientId(patientId);
            if (patientAppointList != null && !patientAppointList.isEmpty()){
                List<Appointment> collect = patientAppointList.stream().sorted(Comparator.comparing(Appointment::getAppointDate).reversed()).collect(Collectors.toList());
                Appointment lastAppointment = collect.get(0);
                build.setEndAppointDate(lastAppointment.getAppointDate());
            }
        }

        // 设置医生姓名
        Integer dentistId = visitingRecord.getDentistId();
        if (dentistId != null) {
            SysUserInfoDetail dentistInfo = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(dentistId);
            if (dentistInfo != null){
                build.setDentistName(dentistInfo.getName());
            }
        }

        // 设置末诊科室信息
        Integer deptRoomId = visitingRecord.getDeptRoomId();
        if (deptRoomId != null) {
            DepartmentRoom departmentRoomInfo = remoteSystemServiceFeign.findDepartmentRoomById(deptRoomId);
            if (departmentRoomInfo != null){
                build.setDeptRoomName(departmentRoomInfo.getName());
            }
        }

        // 设置就诊信息
        Integer treatmentId = visitingRecord.getTreatmentId();
        if (treatmentId != null){
            TreatmentRecord treatmentRecord = remoteTreatmentServiceFeign.findTreatmentRecordById(treatmentId);
            if (treatmentRecord != null){
                build.setTreatmentDate(treatmentRecord.getTreatEndTime());
                build.setFirstVisit(treatmentRecord.getType());
            }
        }
        return ResponseUtil.success(build);
    }

    /**
     * 后续随访查询（随访管理-执行随访-随访-后续随访）
     * @param query 查询条件
     * @return ResponseResult
     */
    public ResponseResult findAfterVisitingContent(VisitingContentAfterCurrentQuery query){
        List<VisitingContentAfterCurrentVo> visitingContentAfterCurrentVos = new ArrayList<>();
        List<VisitingRecord> visitingRecords = mapper.findAfterVisitingContentByPatientIdAndDate(query);
        if (visitingRecords != null && !visitingRecords.isEmpty()){
            visitingRecords.forEach(visitingRecord -> {
                VisitingContentAfterCurrentVo build = EntityUtils.build(visitingRecord, VisitingContentAfterCurrentVo.class);
                // 设置医生姓名
                Integer dentistId = visitingRecord.getDentistId();
                if (dentistId != null) {
                    SysUserInfoDetail dentistInfo = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(dentistId);
                    if (dentistInfo != null){
                        build.setDentistName(dentistInfo.getName());
                    }
                }

                // 设置末诊科室信息
                Integer deptRoomId = visitingRecord.getDeptRoomId();
                if (deptRoomId != null) {
                    DepartmentRoom departmentRoomInfo = remoteSystemServiceFeign.findDepartmentRoomById(deptRoomId);
                    if (departmentRoomInfo != null){
                        build.setDeptRoomName(departmentRoomInfo.getName());
                    }
                }
                visitingContentAfterCurrentVos.add(build);
            });
        }
        if (query.getWhetherPage()){
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        return ResponseUtil.success(new PageInfo<>(visitingContentAfterCurrentVos));
    }

    /**
     * 完成随访
     * @param form 随访内容
     * @return ResponseResult
     */
    public ResponseResult finishVisiting(FinishVisitingForm form) {
        Integer id = form.getId();
        VisitingRecord visitingRecord = mapper.selectByPrimaryKey(id);
        if (visitingRecord == null){
            return ResponseUtil.success("该随访不存在");
        }

        visitingRecord.setVisitingContent(form.getVisitingContent());
        visitingRecord.setStatus(true);
        visitingRecord.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        visitingRecord.setUpdName(BaseContextHandler.getName());
        visitingRecord.setUpdTime(new Date(System.currentTimeMillis()));
        int result = mapper.updateByPrimaryKeySelective(visitingRecord);
        if (result > 0){
            VisitingRecordQuery query = new VisitingRecordQuery();
            query.setPatientId(visitingRecord.getPatientId());
            query.setVisitingDate(visitingRecord.getVisitingDate());
            List<VisitingRecordVo> visitingRecordVos = mapper.findVisitingRecordByCondition(query);
            if (!StringHelper.isEmpty(visitingRecordVos)){
                final String visitingContentStr = visitingRecord.getVisitingContent();
                // 合并随访内容
                visitingRecordVos.forEach(visitingRecordVo -> {
                    VisitingRecord build = EntityUtils.build(visitingRecordVo, VisitingRecord.class);
                    build.setVisitingContent(visitingContentStr);
                    build.setStatus(true);
                    mapper.updateByPrimaryKeySelective(build);
                });
            }
            return ResponseUtil.success();
        }
        return ResponseUtil.success("随访状态更新失败");


    }

    /**
     * 根据条件查询随访记录（外部服务调用接口）
     * @param query 查找条件
     * @return List<VisitingRecordVo>
     */
    public List<VisitingRecordVo> findVisitingRecordByConditionRest(VisitingRecordQuery query) {
        // 随访记录结果列表
        List<VisitingRecordVo> visitingRecordVos = mapper.findVisitingRecordByCondition(query);
        if (visitingRecordVos != null && !visitingRecordVos.isEmpty()) {
            // 组合随访记录信息
            visitingRecordVos.forEach(visitingRecordVo -> {
                this.comboVisitingRecord(visitingRecordVo);
            });
        }
        return visitingRecordVos;
    }

    /**
     * 格式化日期时间
     * @param date  日期
     * @param time  时间
     * @return  yyyy-MM-dd HH:mm
     */
    private Date formatDateTime(Date date,String time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(date);
        String dateTime = dateStr + " " + time;
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parse = null;
        try {
            parse = dateTimeFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

}
