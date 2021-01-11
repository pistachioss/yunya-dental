package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.model.DebtAmountModel;
import com.yunya.feign.treatment_other.domain.form.FinishVisitingForm;
import com.yunya.feign.treatment_other.domain.form.VisitingRecordForm;
import com.yunya.feign.treatment_other.domain.model.VisitingContentModel;
import com.yunya.feign.treatment_other.domain.model.VisitingRecordModel;
import com.yunya.feign.treatment_other.domain.query.VisitingContentAfterCurrentQuery;
import com.yunya.feign.treatment_other.domain.query.VisitingForMonthInfo;
import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.feign.treatment_other.domain.vo.VisitingContentAfterCurrentVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingContentVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingForMonthVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.system.DepartmentRoom;
import com.yunya.models.system.MemberType;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.modules.treatment.other.code.TreatmentOtherError;
import com.yunya.modules.treatment.other.mapper.VisitingRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: yunya-dental
 * @description: 随访记录业务层
 * @author: LHB
 * @create: 2020-08-21 17:52
 **/
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class VisitingRecordBiz extends BaseBiz<VisitingRecordMapper, VisitingRecord> {

    @Autowired
    private RedisUtils redisUtils;

    /** 注入患者服务feign */
    @Autowired
    private RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;

    /** 注入系统基础服务feign */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /** 注入就诊服务feign */
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;

    /** 注入预约服务feign */
    @Autowired
    private RemoteAppointmentFeign remoteAppointmentFeign;

    /** 消息服务 */
    @Autowired private RemoteRabbitMqServiceFeign remoteRabbitMqServiceFeign;


    /**
     * 插入随访记录（外部服务调用）
     * @param visitingRecords
     * @return
     */
    public void insertEntity(List<VisitingRecord> visitingRecords) {
        log.info("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓插入随访记录↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        log.info("==> class: com.yunya.modules.treatment.other.biz.VisitingRecordBiz");
        log.info("==> visitingRecords:{}",visitingRecords);
        log.info("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        mapper.insertEntitys(visitingRecords);
    }

    /**
     * 插入随访记录
     * @param model  随访记录表单
     * @return  返回插入成功的条数
     */
    public ResponseResult insertVisitingRecord(VisitingRecordModel model){
        List<VisitingContentModel> visitingContents = model.getVisitingContents();
        if (!StringHelper.isEmpty(visitingContents)){
            // 判断同一天，同一个医生是否新建多条随访，如果是则返回异常信息
            boolean conflict = this.isVisitingRecordConflict(visitingContents);
            if (conflict) {
                return ResponseUtil.fail(TreatmentOtherError.VISITING_CONFIICT_EXP.getCode(),
                        TreatmentOtherError.VISITING_CONFIICT_EXP.getMessage(),null);
            }
            Integer patientId = model.getPatientId();
            String userID = BaseContextHandler.getUserID();
            for(VisitingContentModel visitingContentModel : visitingContents){
                Date visitingDate = visitingContentModel.getVisitingDate();
                VisitingRecordQuery query = new VisitingRecordQuery();
                query.setDentistId(Integer.valueOf(userID));
                query.setPatientId(patientId);
                query.setVisitingDate(visitingDate);
                List<VisitingRecordVo> visitingRecordByCondition = mapper.findVisitingRecordByCondition(query);
                if (visitingRecordByCondition != null && !visitingRecordByCondition.isEmpty()){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String format = dateFormat.format(query.getVisitingDate());
                    throw new ClientServiceException(format + " 的随访已经存在", OperationCodeConstants.DATA_EXIST);
                }
                VisitingRecord build = EntityUtils.build(model, VisitingRecord.class);
                String orgId = BaseContextHandler.getOrgId();
                if (StringHelper.isNotBlank(orgId)) {
                    build.setOrgId(Integer.parseInt(orgId));
                }
                Integer currentUserId = Integer.valueOf(BaseContextHandler.getUserID());
                build.setVisitingDate(visitingContentModel.getVisitingDate());
                build.setVisitingTime(visitingContentModel.getVisitingTime());
                build.setReason(visitingContentModel.getReason());
                build.setCrtId(currentUserId);
                build.setCrtName(BaseContextHandler.getName());
                build.setCrtTime(new Date(System.currentTimeMillis()));
                int result = mapper.insertSelective(build);
                if (result <= 0){
                    return ResponseUtil.fail(TreatmentOtherError.INSERT_VISITING_RECORD_ERR.getCode(),
                            TreatmentOtherError.INSERT_VISITING_RECORD_ERR.getMessage(),null);
                }
                // 发送消息-新建提醒
                remoteRabbitMqServiceFeign.sendMessage(build.getId(),0,0, MsgCategoryEnum.BaseVisitRemind);
            }
            return ResponseUtil.success();
        }
        throw new ClientServiceException("随访内容列表为空！",OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY);
    }

    /**
     * 检测随访是否冲突
     * @param visitingContents 随访列表
     * @return 冲突返回true,否则返回false
     */
    private boolean isVisitingRecordConflict(List<VisitingContentModel> visitingContents) {
        Map<Date, Long> collect = visitingContents.stream().collect(Collectors.groupingBy(VisitingContentModel::getVisitingDate, Collectors.counting()));
        for (Map.Entry<Date,Long> item : collect.entrySet()) {
            Long value = item.getValue();
            if (value > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据id删除随访记录
     * @param id  随访id
     */
    public ResponseResult deleteVisitingRecord(Integer id){
        VisitingRecord visitingRecord = mapper.selectByPrimaryKey(id);
        if (visitingRecord == null){
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"记录不存在！",null);
        }
        String lockStr = redisUtils.get(RedisConstants.LOCK_VISITING_RECORD);
        if (StringHelper.isEmpty(lockStr)) {
            redisUtils.setLock(RedisConstants.LOCK_VISITING_RECORD,String.valueOf(id),BusinessConstants.MEDICAL_APPLY_LOCK_SEC,TimeUnit.SECONDS);
            try {
                mapper.deleteByPrimaryKey(id);
                // 发送消息-新建提醒
                remoteRabbitMqServiceFeign.sendMessage(id,0,2, MsgCategoryEnum.BaseVisitRemind);
            } finally {
                redisUtils.unlock(RedisConstants.LOCK_VISITING_RECORD,String.valueOf(id));
            }
            return ResponseUtil.success();
        }
        return ResponseUtil.fail(OperationCodeConstants.DELETE_NOT_ALLOW,"该条记录正在编辑中，不能删除！",null);
    }

    /**
     * 根据就诊ID删除随访记录
     * @param id  就诊id
     */
    public void deleteVisitingRecordByTreatmentId(Integer id){
        if (null != id) {
            VisitingRecord visitingRecord = new VisitingRecord();
            visitingRecord.setTreatmentId(id);
            List<VisitingRecord> visitingRecordList = mapper.select(visitingRecord);
            mapper.deleteVisitingRecordByTreatmentId(id);
            if (visitingRecordList.size() > 0){
                visitingRecordList.forEach(
                        visitingRecordVo -> {
                            // 发送消息-删除提醒
                            remoteRabbitMqServiceFeign.sendMessage(visitingRecordVo.getId(),0,2, MsgCategoryEnum.BaseVisitRemind);
                        }
                );
            }

        }
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
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"记录不存在！",null);
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
                // 发送消息-删除提醒
                remoteRabbitMqServiceFeign.sendMessage(build.getId(),0,1, MsgCategoryEnum.BaseVisitRemind);
            } finally {
                // 释放锁
                redisUtils.unlock(RedisConstants.LOCK_VISITING_RECORD,updateId);
            }
        } else {
            return ResponseUtil.fail(TreatmentOtherError.EDIT_NOT_ALLOWED.getCode(),TreatmentOtherError.EDIT_NOT_ALLOWED.getMessage(),null);
        }
        return ResponseUtil.success();
    }

    /**
     * 根据随访id查询随访记录
     * @param id 随访id
     * @return ResponseResult
     */
    public ResponseResult<VisitingRecordVo> findVisitingRecordById(Integer id){
        VisitingRecordVo visitingRecordVo = mapper.findVisitingRecordById(id);
        if (visitingRecordVo == null){
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"没有数据",null);
        }
        visitingRecordVo = this.comboVisitingRecord(visitingRecordVo);
        return ResponseUtil.success(visitingRecordVo);
    }

    /**
     * 根据条件查询随访记录
     * @param query 查询条件
     * @return ResponseResult
     */
    public ResponseResult<PageInfo<VisitingRecordVo>> findVisitingRecordByCondition(VisitingRecordQuery query){
        // 设置分页
        if (query.getWhetherPage()){
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        Map<Integer, PatientTotalInfoVo> patientTotalInfoVoMap = new HashMap<>(16);
        if (StringHelper.isNotEmpty(query.getSearch()) || StringHelper.isNotEmpty(query.getMedicalNumber())) {//模糊匹配患者姓名和手机号，模糊匹配病历号
            PatientBaseInfoQueryForm queryForm = new PatientBaseInfoQueryForm();
            queryForm.setSearch(query.getSearch());
            queryForm.setLikeMedicalNumber(query.getMedicalNumber());
            List<PatientTotalInfoVo> patientInfos = remotePatientCentralServiceFeign.findPatientTotalInfo(queryForm);
            if (StringHelper.isNotEmpty(patientInfos)) {
                patientInfos.forEach(patientInfo->patientTotalInfoVoMap.put(patientInfo.getId(),patientInfo));
                query.setPatientIds(patientTotalInfoVoMap.keySet());
            }
        }
        Map<Integer, SysUserInfoDetail> sysUserInfoDetailMap = new HashMap<>(16);
        if (StringHelper.isNotEmpty(query.getDistentName())) {//精确匹配医生姓名
            SysUserEmployeeModel model = new SysUserEmployeeModel();
            model.setWhetherPage(false);
            model.setUserName(query.getDistentName());
            List<SysUserInfoDetail> sysUserInfoDetails = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
            if (StringHelper.isNotEmpty(sysUserInfoDetails)) {
                sysUserInfoDetails.forEach(sysUserInfo->sysUserInfoDetailMap.put(sysUserInfo.getUserId(),sysUserInfo));
                query.setDentistIds(sysUserInfoDetailMap.keySet());
            }
        }
        query.setSearchId(3);
        List<VisitingRecordVo> visitingRecordVos = mapper.findVisitingRecordByCondition(query);
        if (visitingRecordVos != null && !visitingRecordVos.isEmpty()){
            visitingRecordVos.forEach(visitingRecordVo->comboVisitingRecord(visitingRecordVo,patientTotalInfoVoMap,sysUserInfoDetailMap));
        }
        PageInfo<VisitingRecordVo> visitingRecordVoPageInfo = new PageInfo<>(visitingRecordVos);
        return ResponseUtil.success(visitingRecordVoPageInfo);
    }

//    public ResponseResult<PageInfo<VisitingRecordVo>> findVisitingRecordByCondition(VisitingRecordQuery query){
//        // 设置分页
//        if (query.getWhetherPage()){
//            PageHelper.startPage(query.getPageNum(),query.getPageSize());
//        }
//        List<VisitingRecordVo> searchVisitingRecordVo = null;
//        // 随访记录结果列表
//        List<VisitingRecordVo> visitingRecordVoList = new ArrayList<>();
//        // 按患者姓名、手机号、病历号、医生名字检索，并将检索之后的结果排序
//        String search = query.getSearch();
//        String medicalNumber = query.getMedicalNumber();
//        String distentName = query.getDistentName();
//        List<VisitingRecordVo> visitingRecordVos = mapper.findVisitingRecordByCondition(query);
//        PageInfo<VisitingRecordVo> visitingRecordVoPageInfo = new PageInfo<>(visitingRecordVos);
//        if (visitingRecordVos != null && !visitingRecordVos.isEmpty()){
//            // 组合随访记录信息
//            for(VisitingRecordVo visitingRecordVo : visitingRecordVos){
//                VisitingRecordVo recordVo = this.comboVisitingRecord(visitingRecordVo);
//                visitingRecordVoList.add(recordVo);
//            }
//            if (StringHelper.isEmpty(search) && StringHelper.isEmpty(medicalNumber) && StringHelper.isEmpty(distentName)) {
//                // 排序
//                searchVisitingRecordVo = this.sort(visitingRecordVoList);
//                visitingRecordVoPageInfo.setList(searchVisitingRecordVo);
//            } else {
//                // 按患者姓名、手机号、病历号、医生名字检索
//                searchVisitingRecordVo = this.searchAndOrder(visitingRecordVoList, search, medicalNumber, distentName);
//                // 将检索结果列表排序
//                searchVisitingRecordVo = this.sort(searchVisitingRecordVo);
//                visitingRecordVoPageInfo.setList(searchVisitingRecordVo);
//            }
//
//            // 设置分页参数
//            int size = searchVisitingRecordVo.size();
//            if (size == 0) {
//                visitingRecordVoPageInfo.setTotal(size);
//            }
//            long total = visitingRecordVoPageInfo.getTotal();
//            Integer pageSize = query.getPageSize();
//            int pages = (int) (total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1);
//            visitingRecordVoPageInfo.setPages(pages);
//            if (pages >= 1) {
//                int[] navPagesNum = new int[pages];
//                for (int i = 0;i < pages;i++) {
//                    navPagesNum[i] = i+1;
//                }
//                visitingRecordVoPageInfo.setNavigatepageNums(navPagesNum);
//            } else {
//                visitingRecordVoPageInfo.setNavigatepageNums(new int[0]);
//            }
//
//        }
//        return ResponseUtil.success(visitingRecordVoPageInfo);
//    }

    /**
     * 组合随访记录信息中的患者信息、会员图标信息、医生姓名、就诊信息
     * @param visitingRecordVo 随访记录
     * @return 返回组合之后的随访记录信息
     */
    public VisitingRecordVo comboVisitingRecord(VisitingRecordVo visitingRecordVo) {
        return comboVisitingRecord(visitingRecordVo,null,null);
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        System.out.println(map.get(null));
    }

    public VisitingRecordVo comboVisitingRecord(VisitingRecordVo visitingRecordVo, Map<Integer, PatientTotalInfoVo> patientMap, Map<Integer, SysUserInfoDetail> denstistNames){
        // 组合患者信息
        Integer patientId = visitingRecordVo.getPatientId();
        if (patientId != null) {
            PatientTotalInfoVo patientTotalInfo = null;
            if (StringHelper.isNotEmpty(patientMap)) {
                patientTotalInfo = patientMap.get(patientId);
            } else {
                patientTotalInfo = remotePatientCentralServiceFeign.findPatientTotalInfo(patientId);
            }
            if (patientTotalInfo != null){
                visitingRecordVo.setPatientName(patientTotalInfo.getName());
                visitingRecordVo.setMobile(patientTotalInfo.getMobile());
                visitingRecordVo.setGender(patientTotalInfo.getGender());
                visitingRecordVo.setBirthday(patientTotalInfo.getBirthday());
                visitingRecordVo.setAge(patientTotalInfo.getAge());
                visitingRecordVo.setAllergen(patientTotalInfo.getAllergens());
                visitingRecordVo.setPatientRemark(patientTotalInfo.getRemarks());
                visitingRecordVo.setMedicalNumber(patientTotalInfo.getMedicalNumber());
                visitingRecordVo.setPatientKind(patientTotalInfo.getPatientKindName());
                // 设置会员图标信息
                Integer memberTypeId = patientTotalInfo.getMemberTypeId();
                if (memberTypeId != null) {
                    MemberType memberType = remoteSystemServiceFeign.findMemberTypeById(memberTypeId);
                    if (memberType != null){
                        visitingRecordVo.setMemberIcon(memberType.getIcon());
                    }
                }
                // 欠费总额
                List<Integer> patientIds = new ArrayList<>();
                patientIds.add(patientId);
                List<DebtAmountModel> debtAmountList = remoteTreatmentServiceFeign.findDebtAmountList(patientIds);
                if (StringHelper.isNotEmpty(debtAmountList)) {
                    DebtAmountModel debtAmountModel = debtAmountList.get(0);
                    visitingRecordVo.setArrears(debtAmountModel.getDebtAmount());
                }
            }
        }

        // 设置医生姓名
        Integer dentistId = visitingRecordVo.getDentistId();
        if (dentistId != null){
            SysUserInfoDetail dentistInfo = null;
            if (StringHelper.isNotEmpty(denstistNames)) {
                dentistInfo = denstistNames.get(dentistId);
            } else {
                dentistInfo = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(dentistId);
            }
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
                // 设置初复诊
                visitingRecordVo.setFirstVisit(treatmentRecord.getType());
            }
        }
        // 设置患者过敏源
        PatientTotalInfoVo patientTotalInfo = this.remotePatientCentralServiceFeign.findPatientTotalInfo(patientId);
        if (patientTotalInfo != null) {
            visitingRecordVo.setAllergen(patientTotalInfo.getAllergensDescriptions());
        }
        return visitingRecordVo;
    }

    /**
     * 随访内容（执行随访按钮用）
     * @param id 随访记录id
     * @return 随访内容
     */
    public ResponseResult<VisitingContentVo> executeVisiting(Integer id){
        VisitingRecord visitingRecord = mapper.selectByPrimaryKey(id);
        VisitingContentVo build = EntityUtils.build(visitingRecord, VisitingContentVo.class);
        // 组合患者信息
        Integer patientId = visitingRecord.getPatientId();
        if (patientId != null) {
            PatientTotalInfoVo patientTotalInfo = remotePatientCentralServiceFeign.findPatientTotalInfo(patientId);
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
    public ResponseResult<PageInfo<VisitingContentAfterCurrentVo>> findAfterVisitingContent(VisitingContentAfterCurrentQuery query){
        List<VisitingContentAfterCurrentVo> visitingContentAfterCurrentVos = new ArrayList<>();
        if (query.getWhetherPage()){
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
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
                // 设置初复诊
                Integer treatmentId = visitingRecord.getTreatmentId();
                if (null != treatmentId) {
                    TreatmentRecord treatmentRecordById = remoteTreatmentServiceFeign.findTreatmentRecordById(treatmentId);
                    if (null != treatmentRecordById) {
                        build.setFirstVisit(treatmentRecordById.getType());
                    }
                }
                visitingContentAfterCurrentVos.add(build);
            });
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

        Integer currentUserId = Integer.valueOf(BaseContextHandler.getUserID());
        visitingRecord.setVisitingContent(form.getVisitingContent());
        visitingRecord.setStatus(true);
        visitingRecord.setUptId(currentUserId);
        visitingRecord.setUpdName(BaseContextHandler.getName());
        visitingRecord.setUpdTime(new Date(System.currentTimeMillis()));
        visitingRecord.setExecuteDate(new Date(System.currentTimeMillis()));
        visitingRecord.setExecutorId(currentUserId);
        visitingRecord.setExecutorName(BaseContextHandler.getName());
        int result = mapper.updateByPrimaryKeySelective(visitingRecord);
        if (result > 0){
            VisitingRecordQuery query = new VisitingRecordQuery();
            query.setPatientId(visitingRecord.getPatientId());
            query.setVisitingDate(visitingRecord.getVisitingDate());
            query.setWhetherPage(false);
            List<VisitingRecordVo> visitingRecordVos = mapper.findVisitingRecordByCondition(query);
            if (!StringHelper.isEmpty(visitingRecordVos)){
                final String visitingContentStr = visitingRecord.getVisitingContent();
                String name = BaseContextHandler.getName();
                String userID = BaseContextHandler.getUserID();
                Date date = new Date(System.currentTimeMillis());
                // 合并随访内容
                visitingRecordVos.forEach(visitingRecordVo -> {
                    VisitingRecord build = EntityUtils.build(visitingRecordVo, VisitingRecord.class);
                    build.setVisitingContent(visitingContentStr);
                    build.setUpdName(name);
                    build.setUptId(Integer.valueOf(userID));
                    build.setUpdTime(date);
                    build.setStatus(true);
                    build.setExecutorId(Integer.valueOf(userID));
                    build.setExecutorName(name);
                    build.setExecuteDate(date);
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
            visitingRecordVos.forEach(this::comboVisitingRecord);
        }
        return visitingRecordVos;
    }

    /**
     * 按患者姓名、手机号、病历号、医生名字检索，并将检索之后的结果排序
     * @param visitingRecordVoList 检索列表
     * @param searchStr 姓名患者/手机号
     * @param medicalNumberStr 病历号
     * @param distentNameStr 医生名字
     * @return 检索并且排序之后的列表
     */
    private List searchAndOrder(List<VisitingRecordVo> visitingRecordVoList, String searchStr, String medicalNumberStr, String distentNameStr) {
        // 匹配姓名
        String patientNameReg = "^[\\u4e00-\\u9fa5]{0,}$";
        // 匹配手机号
        String mobileReg = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|16[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
        List<VisitingRecordVo> searchVisitingRecordVo = visitingRecordVoList.stream().filter(visitingRecordVo -> {
            String patientName = visitingRecordVo.getPatientName();
            String mobile = visitingRecordVo.getMobile();
            String medicalNumber = visitingRecordVo.getMedicalNumber();
            String distentName = visitingRecordVo.getDentistName();
            boolean result = false;
            if (!StringHelper.isEmpty(searchStr)) {
                if (searchStr.matches(patientNameReg) && !StringHelper.isEmpty(patientName)) {
                    result = patientName.contains(searchStr);
                } else if (searchStr.matches(mobileReg) && !StringHelper.isEmpty(mobile)) {
                    result = mobile.contains(searchStr);
                }
            }

            if (!StringHelper.isEmpty(medicalNumber) && !StringHelper.isEmpty(medicalNumberStr)){
                result = result | medicalNumber.contains(medicalNumberStr);
            }
            if (!StringHelper.isEmpty(distentName) && !StringHelper.isEmpty(distentNameStr)) {
                result = result | distentName.equals(distentNameStr);
            }
            return result;
        }).collect(Collectors.toList());
        return searchVisitingRecordVo;
    }

    /**
     * 按时间对随访列表进行降序排序
     * @param visitingRecordVos 随访列表
     * @return 排序之后的列表
     */
    private List<VisitingRecordVo> sort(List<VisitingRecordVo> visitingRecordVos) {
        // 按随访时间排序
        return visitingRecordVos.stream().sorted((obj1, obj2)->{
           if (obj1==null || obj2==null) {
               return 0;
           }
           Date date1 = null;
           Date date2 = null;
           try {
              date1 = DateUtil.timeToDate(obj1.getVisitingDate(), obj1.getVisitingTime());
              date2 = DateUtil.timeToDate(obj2.getVisitingDate(), obj2.getVisitingTime());
           } catch (ParseException e) {
              throw new ClientServiceException("日期转换错误",OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
           }
           return date2.compareTo(date1);
        }).collect(Collectors.toList());
    }

    /**
     * 按时间对随访列表进行降序排序
     * @param visitingRecordVos 随访列表
     * @return 排序之后的列表
     */
//    private List<VisitingRecordVo> sort(List<VisitingRecordVo> visitingRecordVos) {
//        // 按随访时间排序
//        return visitingRecordVos.stream().sorted(
//                Comparator.comparing(VisitingRecordVo::getVisitingTime,(obj1,obj2)->{
//                    if (StringHelper.isEmpty(obj1) || StringHelper.isEmpty(obj2)){
//                        return -1;
//                    }
//                    String[] objSplit1 = obj1.trim().split(":");
//                    Integer objMinute1 = Integer.parseInt(objSplit1[0]) * 60 + Integer.parseInt(objSplit1[1]);
//                    String[] objSplit2 = obj2.trim().split(":");
//                    Integer objMinute2 = Integer.parseInt(objSplit2[0]) * 60 + Integer.parseInt(objSplit2[1]);
//                    return objMinute1.compareTo(objMinute2);
//                })).collect(Collectors.toList());
//    }

    /**
     * 根据时间段，医生ID查询这个时间段内每一天每个医生预约的患者数量
     * @param forMonthInfo 查询条件
     * @return 返回实体列表
     */
    public List<VisitingForMonthVo> findVisitingForMonth(VisitingForMonthInfo forMonthInfo) {
        Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
        List<VisitingForMonthVo> visitingForMonth = mapper.findVisitingForMonth(forMonthInfo.getDentistId(),
                forMonthInfo.getStartDate(),
                forMonthInfo.getEndDate(),
                orgId);
        return visitingForMonth;
    }


}
