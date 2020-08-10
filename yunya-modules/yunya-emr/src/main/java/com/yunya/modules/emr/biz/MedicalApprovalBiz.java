package com.yunya.modules.emr.biz;

import com.github.pagehelper.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.yunya.feign.emr.domain.form.*;
import com.yunya.feign.emr.domain.model.*;
import com.yunya.feign.emr.domain.query.*;
import com.yunya.feign.emr.domain.vo.*;
import com.yunya.feign.system.*;
import com.yunya.feign.system.vo.*;
import com.yunya.framework.common.biz.*;
import com.yunya.framework.common.constant.*;
import com.yunya.framework.common.context.*;
import com.yunya.framework.common.exception.*;
import com.yunya.framework.common.utils.*;
import com.yunya.models.emr.*;
import com.yunya.modules.emr.enums.*;
import com.yunya.modules.emr.mapper.*;
import org.apache.commons.collections4.*;
import org.apache.commons.lang3.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import tk.mybatis.mapper.entity.*;

import javax.annotation.*;
import java.time.*;
import java.util.Objects;
import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * @author xiangyang
 * @date 2020/7/31
 */
@Service
public class MedicalApprovalBiz extends BaseBiz<ApprovalRecordMapper, ApprovalRecord> {

    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private MedicalCommonRecordMapper medicalMapper;
    @Resource
    private MedicalCommonRecordBiz commonRecordBiz;

    /**
     * 新增草稿病例申请
     *
     * @param draftModel 草稿病例申请
     */
    public void applyAddDraftCase(DraftMedicalApplyModel draftModel) {
        int pendCount;
        ApplyBaseModel applyBase = draftModel.getApplyBase();
        //判断登录用户是否拥有助手权限
        checkPostPermission();
        //查询电子病历详情
        MedicalCommonRecord medicalCommonRecord = medicalMapper.selectByPrimaryKey(applyBase.getEventId());
        if (medicalCommonRecord != null) {
            ApprovalRecord treatmentRecord = mapper.findTreatmentRecord(medicalCommonRecord.getTreatmentId());
            //如果是新增变更申请 查询截止时间是否已经超时
            checkDeadTimeOut(treatmentRecord);
        }
        //查询该病历的审批情况
        pendCount = mapper.countByEventIdAndType(applyBase.getEventId(), EventTypeEnum.DRAFT_AUDIT.getCode(), null);
        if (pendCount > 0) {
            throw new ClientServiceException("该病例正在审批中，请勿重复申请", OperationCodeConstants.APPLY_APPROVE_PENDING);
        }
        //新增一条草稿病历新增审批
        constructCreateEntity(applyBase, EventTypeEnum.DRAFT_AUDIT.getCode(), ApplyTypeEnum.ADD.getCode(), null);
    }

    /**
     * 修改草稿病例申请
     *
     * @param draftModel 修改草稿病例申请
     */
    public void applyUpdateDraftCase(DraftMedicalApplyModel draftModel) {
        ApplyBaseModel applyBase = draftModel.getApplyBase();
        Integer eventId = applyBase.getEventId();
        //判断登录用户是否拥有助手权限
        checkPostPermission();
        //校验登录人是否有权限进行更改病例申请操作
        checkModifyPermission(eventId);
        //查询修改病例变更申请的最新记录
        ApprovalRecord medicalChangeRecord = mapper.findMedicalChangeRecord(eventId);
        //如果是修改变更申请 查询截止时间是否已经超时
        checkDeadTimeOut(medicalChangeRecord);
        //校验并且返回最新拒绝审批时间
        LocalDateTime approveTime = getAndCheckNewestApproveTime(eventId);
        //校验审批时间是否已经超过24h
        if (judgeRejectTimeout(approveTime)) {
            throw new ClientServiceException("该病例申请已超过24小时", OperationCodeConstants.NO_PERMISSION_OPERATION);
        }
        //新增一条草稿病历修改审批
        constructCreateEntity(applyBase, EventTypeEnum.DRAFT_AUDIT.getCode(), ApplyTypeEnum.UPDATE.getCode(), null);
    }

    /**
     * 病例审批通过
     *
     * @param approveId 审批id
     * @param passForm  审批通过
     */
    @Transactional(rollbackFor = Exception.class)
    public void passMedical(Integer approveId, MedicalApprovePassForm passForm) {
        int pendCount;
        Integer eventId = passForm.getPassForm().getEventId();
        //查询该病历的审批情况
        pendCount = mapper.countByEventIdAndType(eventId, EventTypeEnum.DRAFT_AUDIT.getCode(), ApproveStatusEnum.APPROVE_PENDING.getCode());
        if (pendCount == 0) {
            throw new ClientServiceException("该病例申请不存在", OperationCodeConstants.DATA_ERROR);
        }
        //检查是否有权限审批病例
        checkApprovePermission(approveId);
        //更新审批记录信息
        updateMedicalApprove(approveId, ApproveStatusEnum.AUDIT_PASS.getCode(), null, null);
        //更新电子病历信息
        passForm.getMedicalCommonRecordForm().setStatus(2);
        commonRecordBiz.updateMedicalApproval(passForm.getMedicalCommonRecordForm());
    }

    /**
     * 病例审核拒绝
     *
     * @param approveId  审批Id
     * @param rejectForm 审批拒绝
     */
    @Transactional(rollbackFor = Exception.class)
    public void rejectMedical(Integer approveId, MedicalApproveRejectForm rejectForm) {
        int pendCount;
        ApproveRejectForm appRejectForm = rejectForm.getRejectForm();
        //查询该病历是否存在待审批记录，不存在非法操作
        pendCount = mapper.countByEventIdAndType(appRejectForm.getEventId(), EventTypeEnum.DRAFT_AUDIT.getCode(), ApproveStatusEnum.APPROVE_PENDING.getCode());
        if (pendCount == 0) {
            throw new ClientServiceException("该病例不存在", OperationCodeConstants.DATA_ERROR);
        }
        //检查是否有权限审批病例
        checkApprovePermission(approveId);
        //更新审批记录信息
        updateMedicalApprove(approveId, ApproveStatusEnum.AUDIT_REJECT.getCode(), appRejectForm.getRejectReason(), null);
        //更新电子病历信息
        rejectForm.getMedicalCommonRecordForm().setStatus(3);
        commonRecordBiz.updateMedicalApproval(rejectForm.getMedicalCommonRecordForm());

    }

    /**
     * 申请新增病例变更
     *
     * @param changeModel 申请病例变更新增参数
     */
    public void applyAddChangeCase(ChangeMedicalApplyModel changeModel) {
        //申请基础信息
        ApplyBaseModel applyBase = changeModel.getApplyBase();
        //审批事件id 就诊id或电子病例id
        Integer eventId = applyBase.getEventId();
        //查询就诊记录是否已经存在电子病例
        List<MedicalCommonRecord> list = this.findByTreatmentId(eventId);
        if (CollectionUtils.isNotEmpty(list)) {
            throw new ClientServiceException("选择的就诊记录已写病历，不允许申请新增！", OperationCodeConstants.DATA_EXIST);
        }
        //todo 查询就诊记录是否超过当天
        //查询该条就诊记录是否存在待审批记录
        int countAuditPend = mapper.countByEventIdAndType(eventId, EventTypeEnum.MEDICAL_CHANGE_AUDIT.getCode(),
                ApproveStatusEnum.APPROVE_PENDING.getCode());
        if (countAuditPend > 0) {
            throw new ClientServiceException("申请变更正在审批中，请勿重复申请", OperationCodeConstants.APPLY_APPROVE_PENDING);
        }
        //病例新增变更申请进入审批
        constructCreateEntity(applyBase, EventTypeEnum.MEDICAL_CHANGE_AUDIT.getCode(), ApplyTypeEnum.ADD.getCode(), changeModel.getApplyReason());
    }

    /**
     * 申请修改病例变更
     *
     * @param changeModel 申请病例变更修改参数
     */
    public void applyUpdateChangeCase(ChangeMedicalApplyModel changeModel) {

        ApplyBaseModel applyBase = changeModel.getApplyBase();
        Integer eventId = applyBase.getEventId();
        //校验登录人是否有权限进行更改病例申请操作
        checkModifyPermission(eventId);
        //校验并且返回最新拒绝审批时间
        LocalDateTime approveTime = getAndCheckNewestApproveTime(eventId);
        if (!judgeRejectTimeout(approveTime)) {
            throw new ClientServiceException("“该病历已审核拒绝且可以修改，无须申请修改", OperationCodeConstants.NO_PERMISSION_OPERATION);
        }
        //病例修改变更申请进入审批
        constructCreateEntity(applyBase, EventTypeEnum.MEDICAL_CHANGE_AUDIT.getCode(), ApplyTypeEnum.UPDATE.getCode(), changeModel.getApplyReason());
    }

    /**
     * 通过变更申请
     *
     * @param approveId 主键
     * @param passForm  通过对象
     */
    public void passChange(Integer approveId, ChangeApprovePassForm passForm) {
        LocalDate changeDeadTime = passForm.getChangeDeadTime();
        LocalDate now = LocalDate.now();
        if (now.isAfter(changeDeadTime)) {
            throw new ClientServiceException("选择的允许变更截止时间不能早于操作当天时间", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        //更新审批记录信息
        updateMedicalApprove(approveId, ApproveStatusEnum.AUDIT_PASS.getCode(), null, changeDeadTime);
    }

    /**
     * 拒绝变更对象
     *
     * @param approveId  主键
     * @param rejectForm 拒绝对象
     */
    public void rejectChange(Integer approveId, ChangeApproveRejectForm rejectForm) {
        updateMedicalApprove(approveId, ApproveStatusEnum.AUDIT_REJECT.getCode(), rejectForm.getRejectReason(), null);
    }

    /**
     * 查询草稿病例申请分页
     *
     * @param query 查询条件
     * @return list
     */
    public PageInfo<MedicalApplyPageVo> getDraftApplyPage(MedicalApproveQuery query) {
        List<ApprovalRecord> list = getApproveList(query);
        //组装结果
        return new PageInfo<>(assembleDraftApproveVos(list));
    }

    /**
     * 查询病例审核分页
     *
     * @param query 查询条件
     * @return list
     */
    public PageInfo<MedicalApprovePageVo> getDraftApprovePage(MedicalApproveQuery query) {
        //条件查询审批
        List<ApprovalRecord> list = getApproveList(query);
        return new PageInfo<>(assembleApproveVos(list));
    }

    public PageInfo<MedicalChangeApplyPageVo> getChangeApplyPage(ChangeApproveQuery query) {
        return null;
    }

    public PageInfo<MedicalChangeApprovePageVo> getChangeApprovePage(ChangeApproveQuery query) {
        return null;
    }

    private List<ApprovalRecord> getApproveList(MedicalApproveQuery query) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        String submitTime = query.getSubmitTime();
        String keyword = query.getKeyword();
        List<Integer> patientIds = null;
        List<MedicalCommonRecord> commonRecords;
        List<ApprovalRecord> list = Lists.newArrayList();
        //关键字模糊查询条件为空，先查审批相关信息
        if (StringUtils.isBlank(keyword)) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            //根据病例提交时间查询审批数据
            list = mapper.listMedicalByParam(null, submitTime, loginUserId, EventTypeEnum.DRAFT_AUDIT.getCode());
            List<Integer> medicalIds = list.stream().map(ApprovalRecord::getEventId).collect(toList());
            if (CollectionUtils.isNotEmpty(medicalIds)) {
                //根据审批的事件Ids查询电子病例信息集合
                commonRecords = findByMedicalIds(medicalIds, loginUserId);
                //电子病例id 患者id映射
                Map<Integer, Integer> patientMap = commonRecords.stream().collect(toMap(MedicalCommonRecord::getId, MedicalCommonRecord::getPatientId));
                //患者Id集合
                patientIds = commonRecords.stream().map(MedicalCommonRecord::getPatientId).collect(toList());
                //todo 调用就诊和患者
            }
        } else {
            /**
             * todo
             * 根据keyword查询患者接口得到患者信息集合（患者Id,病历号），再查询电子病例返回(电子病历Id,就诊Id)，再查询就诊记录的门诊信息，
             * 最后查询审批记录
             */
            //如果查询条件关键字不为空，查询当前登录助手的电子病例患者信息集合
            List<Integer> doctorPatentIds = getPatientIdsByLoginUser(loginUserId);
            //todo 调用患者接口 参数doctorPatentIds
            patientIds = null;
            if (CollectionUtils.isNotEmpty(patientIds)) {
                //查询该登录助手的对应的患者ids的电子病例集合
                commonRecords = findByPatientIds(patientIds, loginUserId);
                if (CollectionUtils.isNotEmpty(commonRecords)) {
                    List<Integer> medicalIds = commonRecords.stream().map(MedicalCommonRecord::getId).collect(toList());
                    //根据电子病例Ids和病例提交时间查询审批数据
                    list = mapper.listMedicalByParam(medicalIds, submitTime, loginUserId, EventTypeEnum.DRAFT_AUDIT.getCode());
                }
            }
        }
        return list;
    }

    private List<ApprovalRecord> getChangeApproveList(ChangeApproveQuery query) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        String keyword = query.getKeyword();
        List<Integer> patientIds = null;
        List<MedicalCommonRecord> commonRecords;
        List<ApprovalRecord> list = Lists.newArrayList();
        //关键字模糊查询条件为空，先查审批相关信息
        if (StringUtils.isBlank(keyword)) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            //根据病例提交时间查询审批数据
            list = mapper.listMedicalByParam(null, null, loginUserId, EventTypeEnum.MEDICAL_CHANGE_AUDIT.getCode());
            Map<Integer, List<ApprovalRecord>> eventMap = list.stream().collect(groupingBy(ApprovalRecord::getApplyType));
            if (!org.springframework.util.CollectionUtils.isEmpty(eventMap)) {
                //取出所有就诊Id集合
                List<Integer> treatmentIds = eventMap.get(ApplyTypeEnum.ADD.getCode()).stream().map(ApprovalRecord::getEventId).collect(toList());
                //取出所有电子病例Id集合
                List<Integer> medicalIds = eventMap.get(ApplyTypeEnum.UPDATE.getCode()).stream().map(ApprovalRecord::getEventId).collect(toList());
                //根据审批的事件Ids查询电子病例信息集合
                commonRecords = findByMedicalIds(medicalIds, loginUserId);
                //电子病例id 患者id映射
                Map<Integer, Integer> patientMap = commonRecords.stream().collect(toMap(MedicalCommonRecord::getId, MedicalCommonRecord::getPatientId));
                //患者Id集合
                patientIds = commonRecords.stream().map(MedicalCommonRecord::getPatientId).collect(toList());
                //todo 调用就诊和患者
            }
        } else {
            /**
             * todo
             * 根据keyword查询患者接口得到患者信息集合（患者Id,病历号），再查询就诊记录的门诊信息得到就诊ids集合，用就诊ids查询电子病历得到电子病历ids
             * 最后查询审批表 event_id in (就诊ids，电子病历ids)
             */
            //todo 调用患者接口
            patientIds = null;
            if (CollectionUtils.isNotEmpty(patientIds)) {
                //查询该登录助手的对应的患者ids的电子病例集合
                commonRecords = findByPatientIds(patientIds, loginUserId);
                if (CollectionUtils.isNotEmpty(commonRecords)) {
                    List<Integer> medicalIds = commonRecords.stream().map(MedicalCommonRecord::getId).collect(toList());
                    //合并就诊ids和电子病历ids
                    medicalIds.addAll(patientIds);
                    //根据电子病例Ids和病例提交时间查询审批数据
                    list = mapper.listMedicalByParam(medicalIds, null, loginUserId, EventTypeEnum.MEDICAL_CHANGE_AUDIT.getCode());
                }
            }
        }
        return list;
    }

    private List<MedicalApplyPageVo> assembleDraftApproveVos(List<ApprovalRecord> list) {
        List<MedicalApplyPageVo> resultList = Lists.newArrayListWithExpectedSize(list.size());
        //取出所有审批人id（主治医生id）
        Set<Integer> doctorIds = list.stream().map(ApprovalRecord::getApproverId).collect(toSet());
        //生成user信息映射
        Map<Integer, SysUserInfoDetail> doctorInfoMap = generateUserMap(doctorIds);
        list.forEach(obj -> {
            //主治医生信息
            SysUserInfoDetail majorDoctorInfo = doctorInfoMap.get(obj.getApproverId());
            MedicalApplyPageVo vo = new MedicalApplyPageVo();
            vo.setId(obj.getId());
            vo.setEventId(obj.getEventId());
            //todo
            vo.setPatientName(null);
            vo.setMedicalNum(null);
            vo.setTreatmentClinicName(null);
            vo.setMajorDentistName(majorDoctorInfo == null ? null : majorDoctorInfo.getName());
            vo.setTreatmentDate(null);
            vo.setSubmitTime(obj.getCrtTime());
            vo.setApproveStatus(ApproveStatusEnum.getValue(obj.getStatus()));
            vo.setRejectReason(obj.getApproveReason());
            vo.setModifyDeadTime(Objects.equals(ApproveStatusEnum.AUDIT_REJECT.getCode(), obj.getStatus()) ? null : obj.getApproveTime().plusDays(1));
        });
        return resultList;
    }

    private List<MedicalApprovePageVo> assembleApproveVos(List<ApprovalRecord> list) {
        List<MedicalApprovePageVo> resultList = Lists.newArrayListWithExpectedSize(list.size());
        //取出所有申请人Id（助手医生id）
        Set<Integer> proposerIds = list.stream().map(ApprovalRecord::getProposerId).collect(toSet());
        //生成user信息映射
        Map<Integer, SysUserInfoDetail> doctorInfoMap = generateUserMap(proposerIds);
        list.forEach(obj -> {
            //助理医生信息
            SysUserInfoDetail assistantDoctorInfo = doctorInfoMap.get(obj.getApproverId());
            MedicalApprovePageVo vo = new MedicalApprovePageVo();
            vo.setId(obj.getId());
            vo.setEventId(obj.getEventId());
            //todo
            vo.setPatientName(null);
            vo.setMedicalNum(null);
            vo.setTreatmentClinicName(null);
            vo.setAssistantDentistName(assistantDoctorInfo == null ? null : assistantDoctorInfo.getName());
            vo.setTreatmentDate(null);
            vo.setSubmitTime(obj.getCrtTime());
            vo.setApproveStatus(ApproveStatusEnum.getValue(obj.getStatus()));
            vo.setRejectReason(obj.getApproveReason());
        });
        return resultList;
    }

    private Map<Integer, SysUserInfoDetail> generateUserMap(Set<Integer> userIds) {
        Map<Integer, SysUserInfoDetail> userInfoMap = Maps.newHashMapWithExpectedSize(userIds.size());
        //查询所有主治医师信息 存入映射
        userIds.stream().filter(doctorId -> !userInfoMap.containsKey(doctorId)).forEach(doctorId -> {
            SysUserInfoDetail doctorInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(doctorId);
            userInfoMap.put(doctorId, doctorInfo);
        });
        return userInfoMap;
    }

    /**
     * 通过/拒绝审批
     *
     * @param approveId     审批id 主键
     * @param status        审批状态
     * @param approveReason 审批原因
     * @param deadTime      操作截止时间
     */
    private void updateMedicalApprove(Integer approveId, Integer status, String approveReason, LocalDate deadTime) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        LocalDateTime now = LocalDateTime.now();
        ApprovalRecord record = new ApprovalRecord();
        record.setId(approveId);
        record.setApproverId(loginUserId);
        record.setApproveTime(now);
        record.setApproveReason(StringUtils.isBlank(approveReason) ? null : approveReason);
        record.setDeadTime(deadTime);
        record.setStatus(status);
        record.setCrtId(loginUserId);
        record.setUpdId(loginUserId);
        mapper.updateByPrimaryKeySelective(record);
    }

    /**
     * 插入申请记录
     *
     * @param applyBase   基础对象
     * @param eventType   事件类型
     * @param applyType   申请类型（新增或修改）
     * @param applyReason 申请原因
     */
    private void constructCreateEntity(ApplyBaseModel applyBase, Integer eventType, Integer applyType, String applyReason) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        ApprovalRecord addApplyEntity = EntityUtils.build(applyBase, ApprovalRecord.class);
        addApplyEntity.setEventType(eventType);
        addApplyEntity.setApplyType(applyType);
        addApplyEntity.setApplyReason(StringUtils.isBlank(applyReason) ? null : applyReason);
        addApplyEntity.setStatus(ApproveStatusEnum.APPROVE_PENDING.getCode());
        addApplyEntity.setCrtId(loginUserId);
        addApplyEntity.setUpdId(loginUserId);
        mapper.insertSelective(addApplyEntity);
    }

    private List<MedicalCommonRecord> findByTreatmentId(Integer eventId) {
        Example example = new Example(MedicalCommonRecord.class);
        example.createCriteria().andEqualTo("treatmentId", eventId);
        return medicalMapper.selectByExample(example);
    }

    private List<MedicalCommonRecord> findByMedicalIds(List<Integer> medicalIds, Integer loginUserId) {
        Example example = new Example(MedicalCommonRecord.class);
        example.createCriteria().andIn("id", medicalIds).andEqualTo("crtId", loginUserId);
        return medicalMapper.selectByExample(example);
    }

    private List<MedicalCommonRecord> findByPatientIds(List<Integer> patientIds, Integer loginUserId) {
        Example example = new Example(MedicalCommonRecord.class);
        example.createCriteria().andIn("patientId", patientIds)
                .andEqualTo("crtId", loginUserId);
        return medicalMapper.selectByExample(example);
    }

    private List<Integer> getPatientIdsByLoginUser(Integer loginUserId) {
        Example example = new Example(MedicalCommonRecord.class);
        example.createCriteria().andEqualTo("crtId", loginUserId);
        List<MedicalCommonRecord> list = medicalMapper.selectByExample(example);
        return list.stream().map(MedicalCommonRecord::getPatientId).collect(toList());
    }

    /**
     * 校验草稿病例审批拒绝时间是否已经超过24h
     *
     * @param approveTime 审批拒绝时间
     * @return 是否超时
     */
    private boolean judgeRejectTimeout(LocalDateTime approveTime) {
        LocalDateTime now = LocalDateTime.now();
        long hourGap = Duration.between(approveTime, now).toHours();
        return hourGap > BusinessConstants.HOUR_GAP;
    }

    private LocalDateTime getAndCheckNewestApproveTime(Integer eventId) {
        int pendCount;
        //查询草稿病例是否存在待审批记录
        pendCount = mapper.countByEventIdAndType(eventId, EventTypeEnum.DRAFT_AUDIT.getCode(),
                ApproveStatusEnum.APPROVE_PENDING.getCode());
        if (pendCount > 0) {
            throw new ClientServiceException("该病历未经过主诊医生审核，不能申请修改！", OperationCodeConstants.APPLY_APPROVE_PENDING);
        }
        //查询最新一条拒绝审批的草稿病例
        ApprovalRecord newestDraft = mapper.findNewestReject(eventId);
        if (newestDraft == null) {
            throw new ClientServiceException("病例不存在", OperationCodeConstants.NO_PERMISSION_OPERATION);
        }
        //最新拒绝草稿病例申请的审批时间
        return newestDraft.getApproveTime();
    }

    private void checkDeadTimeOut(ApprovalRecord record) {
        if (record != null) {
            //病例变更截止时间
            LocalDate deadTime = record.getDeadTime();
            LocalDate now = LocalDate.now();
            if (now.isAfter(deadTime)) {
                throw new ClientServiceException("已超过变更截止时间，不能新增病例", OperationCodeConstants.NO_PERMISSION_OPERATION);
            }
        }
    }

    /**
     * 检查是否有权限修改病例
     *
     * @param eventId 电子病例Id
     */
    private void checkModifyPermission(Integer eventId) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        //查询电子病历详情
        MedicalCommonRecord medicalCommonRecord = medicalMapper.selectByPrimaryKey(eventId);
        //查询申请变更该病例的操作人是否是新增该病例的医生或助手
        if (!Objects.equals(loginUserId, medicalCommonRecord.getCrtId())) {
            throw new ClientServiceException("无权限申请修改此病历，请联系新增病历医生申请修改！", OperationCodeConstants.NO_PERMISSION_OPERATION);
        }
    }

    /**
     * 检查是否有权限审批病例
     *
     * @param approveId 审批id
     */
    private void checkApprovePermission(Integer approveId) {
        ApprovalRecord approvalRecord = mapper.selectByPrimaryKey(approveId);
        if (approvalRecord != null) {
            if (!Objects.equals(String.valueOf(approvalRecord.getApproverId()), BaseContextHandler.getUserID())) {
                throw new ClientServiceException("无权限审批", OperationCodeConstants.NO_PERMISSION_OPERATION);
            }
        }
    }

    private void checkPostPermission() {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        //查询用户岗位信息
        SysUserInfoDetail loginUser = systemServiceFeign.findSysUserEmployeeInfoByUserId(loginUserId);
        if (loginUser != null) {
            String postGroups = loginUser.getPostGroups();
            List<String> postGroupList = Splitter.on(",").splitToList(postGroups);
            if (!postGroupList.contains("助手")) {
                throw new ClientServiceException("无权限操作", OperationCodeConstants.NO_PERMISSION_OPERATION);
            }
        }
    }
}
