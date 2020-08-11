package com.yunya.modules.emr.biz;

import com.github.pagehelper.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.yunya.feign.emr.domain.bo.*;
import com.yunya.feign.emr.domain.form.*;
import com.yunya.feign.emr.domain.model.*;
import com.yunya.feign.emr.domain.query.*;
import com.yunya.feign.emr.domain.vo.*;
import com.yunya.feign.patient_central.*;
import com.yunya.feign.patient_central.domain.vo.*;
import com.yunya.feign.system.*;
import com.yunya.feign.system.vo.*;
import com.yunya.framework.common.biz.*;
import com.yunya.framework.common.constant.*;
import com.yunya.framework.common.context.*;
import com.yunya.framework.common.exception.*;
import com.yunya.framework.common.utils.*;
import com.yunya.framework.redis.util.*;
import com.yunya.models.emr.*;
import com.yunya.modules.emr.enums.*;
import com.yunya.modules.emr.mapper.*;
import lombok.extern.slf4j.*;
import org.apache.commons.collections4.*;
import org.apache.commons.lang3.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import tk.mybatis.mapper.entity.*;

import javax.annotation.*;
import java.time.*;
import java.util.Objects;
import java.util.*;
import java.util.concurrent.*;

import static java.util.stream.Collectors.*;

/**
 * @author xiangyang
 * @date 2020/7/31
 */
@Service
@Slf4j
public class MedicalApprovalBiz extends BaseBiz<ApprovalRecordMapper, ApprovalRecord> {

    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private MedicalCommonRecordMapper medicalMapper;
    @Resource
    private MedicalCommonRecordBiz commonRecordBiz;
    @Resource
    private PatientCentralServiceFeign patientFeign;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 新增草稿病例申请
     *
     * @param draftModel 草稿病例申请
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyAddDraftCase(DraftMedicalApplyModel draftModel) {
        boolean locked = false;
        String lockKey = Joiner.on(":").join(RedisConstants.LOCK_DRAFT_APPLY_NS, String.valueOf(draftModel.getApplyBase().getEventId()));
        String lockVal = BaseContextHandler.getUserID();
        log.info("新增草稿病例申请开始提交：{}", lockKey);
        try {
            // 1. 锁定草稿病例
            locked = redisUtils.setLock(lockKey, lockVal, BusinessConstants.DRAFT_LOCK_SEC, TimeUnit.SECONDS);
            if (!locked) {
                log.info("【锁定失败】草稿病历无法提交新增申请");
                throw new ClientServiceException("草稿病例已被锁定，无法提交", OperationCodeConstants.KEY_IS_LOCKED);
            }
            log.info("【锁定成功】准备提交草稿病例新增申请...");
            //2.草稿病例提交申请
            applyAddDraftCaseSubmit(draftModel);
        } finally {
            if (locked) {
                log.info("【解锁】完成草稿病例新增申请");
                redisUtils.unlock(lockKey, lockVal);
            }
        }
    }

    /**
     * 修改草稿病例申请
     *
     * @param draftModel 修改草稿病例申请
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyUpdateDraftCase(DraftMedicalApplyModel draftModel) {
        boolean locked = false;
        String lockKey = Joiner.on(":").join(RedisConstants.LOCK_DRAFT_APPLY_NS, String.valueOf(draftModel.getApplyBase().getEventId()));
        String lockVal = BaseContextHandler.getUserID();
        log.info("修改草稿病例申请开始提交：{}", lockKey);
        try {
            // 1. 锁定草稿病例
            locked = redisUtils.setLock(lockKey, lockVal, BusinessConstants.DRAFT_LOCK_SEC, TimeUnit.SECONDS);
            if (!locked) {
                log.info("【锁定失败】草稿病历无法提交修改申请");
                throw new ClientServiceException("草稿病例已被锁定，无法提交", OperationCodeConstants.KEY_IS_LOCKED);
            }
            log.info("【锁定成功】准备提交草稿病例修改申请...");
            //2.草稿病例提交申请
            applyUpdateDraftCaseSubmit(draftModel);
        } finally {
            if (locked) {
                log.info("【解锁】完成草稿病例修改申请");
                redisUtils.unlock(lockKey, lockVal);
            }
        }
    }

    /**
     * 申请新增病例变更
     *
     * @param changeModel 申请病例变更新增参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyAddChangeCase(ChangeMedicalApplyModel changeModel) {
        boolean locked = false;
        String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CHANGE_APPLY_NS, String.valueOf(changeModel.getApplyBase().getEventId()));
        String lockVal = BaseContextHandler.getUserID();
        log.info("新增病例变更申请开始提交：{}", lockKey);
        try {
            // 1. 锁定就诊变更申请
            locked = redisUtils.setLock(lockKey, lockVal, BusinessConstants.DRAFT_LOCK_SEC, TimeUnit.SECONDS);
            if (!locked) {
                log.info("【锁定失败】无法提交新增变更申请：{}", lockKey);
                throw new ClientServiceException("病例申请已被锁定，无法提交", OperationCodeConstants.KEY_IS_LOCKED);
            }
            log.info("【锁定成功】准备提交病例新增变更申请...");
            //2.新增变更提交申请
            applyAddChangeCaseSubmit(changeModel);
        } finally {
            if (locked) {
                log.info("【解锁】完成病例新增变更申请");
                redisUtils.unlock(lockKey, lockVal);
            }
        }
    }

    /**
     * 新增草稿病例申请
     *
     * @param draftModel 草稿病例申请
     */
    private void applyAddDraftCaseSubmit(DraftMedicalApplyModel draftModel) {
        int pendCount;
        ApplyBaseModel applyBase = draftModel.getApplyBase();
        Integer eventId = applyBase.getEventId();
        //判断登录用户是否拥有助手权限
        checkPostPermission();
        //校验登录人是否有权限进行更改病例申请操作
        checkModifyPermission(eventId);
        //查询电子病历详情
        MedicalCommonRecord medicalCommonRecord = medicalMapper.selectByPrimaryKey(eventId);
        if (medicalCommonRecord != null) {
            //查询新增变更申请的审批记录（就诊Id）
            ApprovalRecord treatmentRecord = mapper.findTreatmentRecord(medicalCommonRecord.getTreatmentId());
            //如果是新增变更申请 查询截止时间是否已经超时
            checkDeadTimeOut(treatmentRecord);
        }
        //查询该病历的审批情况
        pendCount = mapper.countByEventIdAndType(applyBase.getEventId(), EventTypeEnum.DRAFT_AUDIT.getCode(), null);
        if (pendCount > 0) {
            throw new ClientServiceException("病例已在审批，请勿重复提交申请", OperationCodeConstants.APPLY_APPROVE_PENDING);
        }
        //新增一条草稿病历新增审批
        constructCreateEntity(applyBase, EventTypeEnum.DRAFT_AUDIT.getCode(), ApplyTypeEnum.ADD.getCode(), null);
    }

    /**
     * 修改草稿病例申请
     *
     * @param draftModel 修改草稿病例申请
     */
    private void applyUpdateDraftCaseSubmit(DraftMedicalApplyModel draftModel) {
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
        //校验最新一条草稿病例审批状态
        checkNewestDraftStatus(eventId);
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
        //查询该病历是否存在待审批记录，不存在非法操作
        isExistApply(approveId);
        //检查是否有权限审批病例
        checkApprovePermission(approveId);
        //更新审批记录信息
        updateMedicalApprove(approveId, ApproveStatusEnum.AUDIT_PASS.getCode(), null, null);
        //更新电子病历信息
        passForm.getMedicalCommonRecordForm().setStatus(2);
        passForm.getMedicalCommonRecordForm().setApprovalTime(new Date());
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
        ApproveRejectForm appRejectForm = rejectForm.getRejectForm();
        //查询该病历是否存在待审批记录，不存在非法操作
        isExistApply(approveId);
        //检查是否有权限审批病例
        checkApprovePermission(approveId);
        //更新审批记录信息
        updateMedicalApprove(approveId, ApproveStatusEnum.AUDIT_REJECT.getCode(), appRejectForm.getRejectReason(), null);
        //更新电子病历信息
        rejectForm.getMedicalCommonRecordForm().setStatus(3);
        rejectForm.getMedicalCommonRecordForm().setApprovalTime(new Date());
        commonRecordBiz.updateMedicalApproval(rejectForm.getMedicalCommonRecordForm());
    }

    /**
     * 申请新增病例变更
     *
     * @param changeModel 申请病例变更新增参数
     */
    private void applyAddChangeCaseSubmit(ChangeMedicalApplyModel changeModel) {
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
        //查询草稿病例审核状态（草稿通过状态可以变更、拒绝24h之外可以变更、医生超过24h可以变更，待审核或者拒绝时间24h之内不能变更，医生就诊当天不能变更）
        checkCanChangeUpdate(eventId);
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
        //查询该病历是否存在待审批记录，不存在非法操作
        isExistApply(approveId);
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
        //查询该病历是否存在待审批记录，不存在非法操作
        isExistApply(approveId);
        //更新审批状态
        updateMedicalApprove(approveId, ApproveStatusEnum.AUDIT_REJECT.getCode(), rejectForm.getRejectReason(), null);
    }

    /**
     * 查询草稿病例申请分页
     *
     * @param query 查询条件
     * @return list
     */
    public PageInfo<MedicalApplyPageVo> getDraftApplyPage(MedicalApproveQuery query) {
        //条件查询草稿申请
        ApprovePageBo draftApplyBo = getApproveBo(query, 0);
        //组装结果
        return new PageInfo<>(assembleDraftApplyVos(draftApplyBo));
    }

    /**
     * 查询病例审核分页
     *
     * @param query 查询条件
     * @return list
     */
    public PageInfo<MedicalApprovePageVo> getDraftApprovePage(MedicalApproveQuery query) {
        //条件查询草稿审批
        ApprovePageBo draftApproveBo = getApproveBo(query, 1);
        return new PageInfo<>(assembleDraftApproveVos(draftApproveBo));
    }

    public PageInfo<MedicalChangeApplyPageVo> getChangeApplyPage(ChangeApproveQuery query) {
        //条件查询变更申请
        ApprovePageBo changeApplyBo = getChangeApproveBo(query, 0);
        return new PageInfo<>(assembleChangeApplyVos(changeApplyBo));
    }

    public PageInfo<MedicalChangeApprovePageVo> getChangeApprovePage(ChangeApproveQuery query) {
        //条件查询变更审批
        ApprovePageBo changeApproveBo = getChangeApproveBo(query, 1);
        return new PageInfo<>(assembleChangeApproveVos(changeApproveBo));
    }

    private ApprovePageBo getApproveBo(MedicalApproveQuery query, Integer auditStatus) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        String submitTime = query.getSubmitTime();
        String keyword = query.getKeyword();
        List<PatientBaseInfoVo> filterPatientList;
        Map<Integer, Integer> patientIdMap;
        List<ApprovalRecord> list;
        Map<Integer, PatientBaseInfoVo> patientInfoMap = Maps.newHashMap();
        //初始化bo
        ApprovePageBo approveBo = ApprovePageBo.getInstance();
        //关键字模糊查询条件为空，先查审批相关信息
        if (StringUtils.isBlank(keyword)) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            //根据病例提交时间查询审批数据
            list = mapper.listMedicalByParam(null, submitTime, loginUserId, EventTypeEnum.DRAFT_AUDIT.getCode(), auditStatus);
            //查询登录人草稿审批的患者ids映射
            patientIdMap = getDraftPatientIdsByApplyType(list, loginUserId);
            //调用患者接口服务，查询条件下的所有患者信息
            filterPatientList = patientFeign.findPatientInfoByIds(Lists.newArrayList(patientIdMap.keySet()));
            //构建病例和患者映射
            filterPatientList.forEach(obj -> patientInfoMap.put(patientIdMap.get(obj.getId()), obj));
            //todo 调用就诊接口

        } else {
            /**
             * todo
             * 根据keyword查询患者接口得到患者信息集合（患者Id,病历号），再查询电子病例返回(电子病历Id,就诊Id)，再查询就诊记录的门诊信息，
             * 最后查询审批记录
             */
            //根据条件查询登录人所有的申请记录集合
            list = mapper.listMedicalByParam(null, submitTime, loginUserId, EventTypeEnum.DRAFT_AUDIT.getCode(), auditStatus);
            //查询登录人草稿审批的患者ids映射
            patientIdMap = getDraftPatientIdsByApplyType(list, loginUserId);
            //根据查询关键字获取电子病例ids集合
            List<Integer> medicalIds = getQueryMedicalIds(patientIdMap, loginUserId, keyword, patientInfoMap);
            //根据电子病例Ids和病例提交时间查询审批数据
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            list = mapper.listMedicalByParam(medicalIds, submitTime, loginUserId, EventTypeEnum.DRAFT_AUDIT.getCode(), auditStatus);
        }
        //数据存入bo对象
        approveBo.assignMember(list, patientInfoMap);
        return approveBo;
    }

    private ApprovePageBo getChangeApproveBo(ChangeApproveQuery query, Integer auditStatus) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        String keyword = query.getKeyword();
        List<Integer> patientIds = null;
        List<ApprovalRecord> list;
        List<PatientBaseInfoVo> filterPatientList;
        Map<Integer, Integer> patientIdMap;
        Map<Integer, PatientBaseInfoVo> patientInfoMap = Maps.newHashMap();
        //初始化bo
        ApprovePageBo approveBo = ApprovePageBo.getInstance();
        //关键字模糊查询条件为空，先查审批相关信息
        if (StringUtils.isBlank(keyword)) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            //查询变更审批数据
            list = mapper.listMedicalByParam(null, null, loginUserId, EventTypeEnum.MEDICAL_CHANGE_AUDIT.getCode(), auditStatus);
            //根据审批查询结果，生成申请类型映射
            Map<Integer, List<ApprovalRecord>> eventMap = list.stream().collect(groupingBy(ApprovalRecord::getApplyType));
            if (!org.springframework.util.CollectionUtils.isEmpty(eventMap)) {
                //取出新增变类型变更的审批结果
                List<ApprovalRecord> treatmentList = eventMap.get(ApplyTypeEnum.ADD.getCode());
                if (CollectionUtils.isNotEmpty(treatmentList)) {
                    List<Integer> treatmentIds = treatmentList.stream().map(ApprovalRecord::getEventId).collect(toList());
                }
                //查询登录人变更审批的患者ids映射
                patientIdMap = getChangePatientIdsByApplyType(list, ApplyTypeEnum.UPDATE.getCode(), loginUserId);
                //调用患者接口服务，查询条件下的所有患者信息
                filterPatientList = patientFeign.findPatientInfoByIds(Lists.newArrayList(patientIdMap.keySet()));
                //构建病例和患者映射
                filterPatientList.forEach(obj -> patientInfoMap.put(patientIdMap.get(obj.getId()), obj));
                //todo 调用就诊
            }
        } else {
            /**
             * 查询逻辑
             * 根据keyword查询患者接口得到患者信息集合（患者Id,病历号），再查询就诊记录的门诊信息得到就诊ids集合，用就诊ids查询电子病历得到电子病历ids
             * 最后查询审批表 event_id in (就诊ids，电子病历ids)
             */
            //查询变更审批数据
            list = mapper.listMedicalByParam(null, null, loginUserId, EventTypeEnum.MEDICAL_CHANGE_AUDIT.getCode(), auditStatus);
            //查询登录人变更审批的患者ids映射
            patientIdMap = getChangePatientIdsByApplyType(list, ApplyTypeEnum.UPDATE.getCode(), loginUserId);
            //根据查询关键字获取电子病例ids集合
            List<Integer> medicalIds = getQueryMedicalIds(patientIdMap, loginUserId, keyword, patientInfoMap);
            //todo 合并就诊ids和电子病历ids
            medicalIds.addAll(patientIds);
            //根据电子病例Ids和病例提交时间查询审批数据
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            list = mapper.listMedicalByParam(medicalIds, null, loginUserId, EventTypeEnum.MEDICAL_CHANGE_AUDIT.getCode(), auditStatus);
        }
        approveBo.assignMember(list, patientInfoMap);
        return approveBo;
    }

    /**
     * 查询登录人草稿审批的患者ids
     * @param list 源数据
     * @param loginUserId 登录人
     * @return 患者ids
     */
    public Map<Integer, Integer> getDraftPatientIdsByApplyType(List<ApprovalRecord> list, Integer loginUserId) {
        Map<Integer, Integer> patientMap = Maps.newHashMap();
        List<MedicalCommonRecord> commonRecords;
        //审批结果的所有电子病例id集合
        List<Integer> medicalIds = list.stream().map(ApprovalRecord::getEventId).collect(toList());
        if (CollectionUtils.isNotEmpty(medicalIds)) {
            //根据审批的事件Ids查询电子病例信息集合
            commonRecords = findByMedicalIds(medicalIds, loginUserId);
            //患者Id集合
            patientMap = commonRecords.stream().collect(toMap(MedicalCommonRecord::getPatientId, MedicalCommonRecord::getPatientId));
        }
        return patientMap;
    }

    /**
     * 查询登录人变更审批的患者ids
     * @param list 源数据
     * @param applyType 变更申请类型
     * @param loginUserId 登录人
     * @return 患者ids
     */
    private Map<Integer, Integer> getChangePatientIdsByApplyType(List<ApprovalRecord> list, Integer applyType, Integer loginUserId) {
        Map<Integer, Integer> patientMap = Maps.newHashMap();
        List<MedicalCommonRecord> commonRecords;
        //根据审批查询结果，生成申请类型映射
        Map<Integer, List<ApprovalRecord>> eventMap = list.stream().collect(groupingBy(ApprovalRecord::getApplyType));
        if (!org.springframework.util.CollectionUtils.isEmpty(eventMap)) {
            //取出修改变类型变更的审批结果
            List<ApprovalRecord> medicalList = eventMap.get(applyType);
            if (CollectionUtils.isNotEmpty(medicalList)) {
                List<Integer> medicalIds = medicalList.stream().map(ApprovalRecord::getEventId).collect(toList());
                //根据审批的事件Ids查询电子病例信息集合
                commonRecords = findByMedicalIds(medicalIds, loginUserId);
                //电子病例对应的患者map映射
                patientMap = commonRecords.stream().collect(toMap(MedicalCommonRecord::getPatientId, MedicalCommonRecord::getPatientId));
            }
        }
        return patientMap;
    }

    /**
     * 根据查询关键字获取电子病例ids集合
     * @param patientIdMap 根据条件查询的登录人对应的<患者id，电子病例id>映射
     * @param loginUserId 登录人
     * @param keyword 关键字
     * @return list
     */
    private List<Integer> getQueryMedicalIds(Map<Integer, Integer> patientIdMap, Integer loginUserId, String keyword
            , Map<Integer, PatientBaseInfoVo> patientInfoMap) {
        List<Integer> patientIds = Lists.newArrayList(patientIdMap.keySet());
        List<PatientBaseInfoVo> filterPatientList;
        List<MedicalCommonRecord> commonRecords;
        List<Integer> medicalIds = Lists.newArrayList();
        //调用患者接口服务，查询条件下的所有患者信息
        filterPatientList = patientFeign.findPatientInfoByIds(patientIds);
        if (CollectionUtils.isNotEmpty(filterPatientList)) {
            //从患者接口中筛选出模糊查询关键字的患者集合
            filterPatientList = filterPatientList.stream().filter(obj ->
                    (!StringUtils.isBlank(obj.getName()) && obj.getName().contains(keyword))
                            || (!StringUtils.isBlank(obj.getMedicalNumber()) && obj.getMedicalNumber().contains(keyword)))
                    .collect(toList());
        }
        if (CollectionUtils.isNotEmpty(filterPatientList)) {
            //取出筛选结果中的患者id集合
            patientIds = filterPatientList.stream().map(PatientBaseInfoVo::getId).collect(toList());
            //查询该登录助手的对应的患者ids的电子病例集合
            commonRecords = findByPatientIds(patientIds, loginUserId);
            if (CollectionUtils.isNotEmpty(commonRecords)) {
                medicalIds = commonRecords.stream().map(MedicalCommonRecord::getId).collect(toList());
                //构建病例和患者映射
                filterPatientList.forEach(obj -> patientInfoMap.put(patientIdMap.get(obj.getId()), obj));
            }
        }
        return medicalIds;
    }

    private List<MedicalApplyPageVo> assembleDraftApplyVos(ApprovePageBo draftApllyBo) {
        //查询的审批结果
        List<ApprovalRecord> auditList = draftApllyBo.getAuditList();
        //查询的患者信息映射
        Map<Integer, PatientBaseInfoVo> patientInfoMap = draftApllyBo.getPatientInfoMap();
        //返回Vo集合
        List<MedicalApplyPageVo> resultList = Lists.newArrayListWithExpectedSize(auditList.size());
        //取出所有审批人id（主治医生id）
        Set<Integer> doctorIds = auditList.stream().map(ApprovalRecord::getApproverId).collect(toSet());
        //生成user信息映射
        Map<Integer, SysUserInfoDetail> doctorInfoMap = generateUserMap(doctorIds);
        auditList.forEach(obj -> {
            //电子病例对应的患者信息
            PatientBaseInfoVo patientInfo = patientInfoMap.get(obj.getEventId());
            //主治医生信息
            SysUserInfoDetail majorDoctorInfo = doctorInfoMap.get(obj.getApproverId());
            MedicalApplyPageVo vo = new MedicalApplyPageVo();
            vo.setId(obj.getId());
            vo.setEventId(obj.getEventId());
            //todo
            vo.setPatientName(patientInfo == null ? null : patientInfo.getName());
            vo.setMedicalNum(patientInfo == null ? null : patientInfo.getMedicalNumber());
            vo.setTreatmentClinicName(null);
            vo.setMajorDentistName(majorDoctorInfo == null ? null : majorDoctorInfo.getName());
            vo.setTreatmentDate(null);
            vo.setSubmitTime(obj.getCrtTime());
            vo.setApproveStatus(ApproveStatusEnum.getValue(obj.getStatus()));
            vo.setRejectReason(obj.getApproveReason());
            vo.setModifyDeadTime(Objects.equals(ApproveStatusEnum.AUDIT_REJECT.getCode(), obj.getStatus()) ? obj.getApproveTime().plusDays(1) : null);
            resultList.add(vo);
        });
        return resultList;
    }

    private List<MedicalApprovePageVo> assembleDraftApproveVos(ApprovePageBo draftApproveBo) {
        //查询的审批结果
        List<ApprovalRecord> auditList = draftApproveBo.getAuditList();
        //查询的患者信息映射
        Map<Integer, PatientBaseInfoVo> patientInfoMap = draftApproveBo.getPatientInfoMap();
        List<MedicalApprovePageVo> resultList = Lists.newArrayListWithExpectedSize(auditList.size());
        //取出所有申请人Id（助手医生id）
        Set<Integer> proposerIds = auditList.stream().map(ApprovalRecord::getProposerId).collect(toSet());
        //生成user信息映射
        Map<Integer, SysUserInfoDetail> doctorInfoMap = generateUserMap(proposerIds);
        auditList.forEach(obj -> {
            //电子病例对应的患者信息
            PatientBaseInfoVo patientInfo = patientInfoMap.get(obj.getEventId());
            //助理医生信息
            SysUserInfoDetail assistantDoctorInfo = doctorInfoMap.get(obj.getApproverId());
            MedicalApprovePageVo vo = new MedicalApprovePageVo();
            vo.setId(obj.getId());
            vo.setEventId(obj.getEventId());
            //todo
            vo.setPatientName(patientInfo == null ? null : patientInfo.getName());
            vo.setMedicalNum(patientInfo == null ? null : patientInfo.getMedicalNumber());
            vo.setTreatmentClinicName(null);
            vo.setAssistantDentistName(assistantDoctorInfo == null ? null : assistantDoctorInfo.getName());
            vo.setTreatmentDate(null);
            vo.setSubmitTime(obj.getCrtTime());
            vo.setApproveStatus(ApproveStatusEnum.getValue(obj.getStatus()));
            vo.setRejectReason(obj.getApproveReason());
            resultList.add(vo);
        });
        return resultList;
    }

    private List<MedicalChangeApplyPageVo> assembleChangeApplyVos(ApprovePageBo changeApplyBo) {
        //查询的审批结果
        List<ApprovalRecord> auditList = changeApplyBo.getAuditList();
        //查询的患者集合
        Map<Integer, PatientBaseInfoVo> patientInfoMap = changeApplyBo.getPatientInfoMap();
        List<MedicalChangeApplyPageVo> resultList = Lists.newArrayListWithExpectedSize(auditList.size());
        auditList.forEach(obj -> {
            MedicalChangeApplyPageVo vo = new MedicalChangeApplyPageVo();
            vo.setId(obj.getId());
            vo.setEventId(obj.getEventId());
            //todo
            vo.setPatientName(null);
            vo.setMedicalNum(null);
            vo.setTreatmentClinicName(null);
            vo.setTreatmentDate(null);
            vo.setApplyTypeName(ApplyTypeEnum.getValue(obj.getApplyType()));
            vo.setApplyReason(obj.getApplyReason());
            vo.setApproveStatus(ApproveStatusEnum.getValue(obj.getStatus()));
            vo.setChangeDeadTime(obj.getDeadTime());
            vo.setRejectReason(obj.getApproveReason());
            resultList.add(vo);
        });
        return resultList;
    }

    private List<MedicalChangeApprovePageVo> assembleChangeApproveVos(ApprovePageBo changeApproveBo) {
        //查询的审批结果
        List<ApprovalRecord> auditList = changeApproveBo.getAuditList();
        //查询的患者集合
        Map<Integer, PatientBaseInfoVo> patientInfoMap = changeApproveBo.getPatientInfoMap();
        List<MedicalChangeApprovePageVo> resultList = Lists.newArrayListWithExpectedSize(auditList.size());
        auditList.forEach(obj -> {
            SysUserInfoDetail applyDentist = systemServiceFeign.findSysUserEmployeeInfoByUserId(obj.getProposerId());
            MedicalChangeApprovePageVo vo = new MedicalChangeApprovePageVo();
            vo.setId(obj.getId());
            vo.setEventId(obj.getEventId());
            //todo
            vo.setPatientName(null);
            vo.setMedicalNum(null);
            vo.setApplyTypeDate(LocalDate.of(obj.getCrtTime().getYear(), obj.getCrtTime().getMonth(), obj.getCrtTime().getDayOfMonth()));
            vo.setApplyTypeName(ApplyTypeEnum.getValue(obj.getApplyType()));
            vo.setApplyDentistName(applyDentist == null ? null : applyDentist.getName());
            vo.setApplyReason(obj.getApplyReason());
            vo.setChangeDeadTime(obj.getDeadTime());
            vo.setApproveStatus(ApproveStatusEnum.getValue(obj.getStatus()));
            vo.setRejectReason(obj.getApproveReason());
            resultList.add(vo);
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
        record.setApproveReason(approveReason);
        record.setDeadTime(deadTime);
        record.setStatus(status);
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

    private void checkNewestDraftStatus(Integer eventId) {
        //查询最新一条审批的草稿病例
        ApprovalRecord newestDraft = mapper.findNewestDraft(eventId);
        if (newestDraft == null) {
            throw new ClientServiceException("病例不存在", OperationCodeConstants.NO_PERMISSION_OPERATION);
        }
        if (ApproveStatusEnum.APPROVE_PENDING.getCode().equals(newestDraft.getStatus())) {
            throw new ClientServiceException("该病历未经过主诊医生审核，不能申请修改！", OperationCodeConstants.APPLY_APPROVE_PENDING);
        }
        //如果最新草稿状态是通过，变更申请同意数必须等于草稿审批同意数，才能修改
        if (ApproveStatusEnum.AUDIT_PASS.getCode().equals(newestDraft.getStatus())) {
            int countDraftPass = mapper.countDraftPassByEventId(eventId);
            int countChangePass = mapper.countChangePassByEventId(eventId);
            if (countChangePass != countDraftPass) {
                throw new ClientServiceException("该草稿病历审核已通过，无法修改", OperationCodeConstants.DATA_ERROR);
            }
        }
        if (ApproveStatusEnum.AUDIT_REJECT.getCode().equals(newestDraft.getStatus())) {
            //最新拒绝草稿病例申请的审批时间
            LocalDateTime approveTime = newestDraft.getApproveTime();
            //校验审批时间是否已经超过24h
            if (judgeRejectTimeout(approveTime)) {
                throw new ClientServiceException("该病例申请已超过24小时", OperationCodeConstants.NO_PERMISSION_OPERATION);
            }
        }
    }

    /**
     * 校验是否可以变更修改申请
     * 草稿通过状态可以变更、拒绝24h之外可以变更、医生超过当天就诊24点可以变更，待审核或者拒绝时间24h之内不能变更，医生就诊当天不能变更）
     *
     * @param eventId
     */
    private void checkCanChangeUpdate(Integer eventId) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        MedicalCommonRecord medical = medicalMapper.selectByPrimaryKey(eventId);
        //查询最新一条草稿病例
        ApprovalRecord newestDraft = mapper.findNewestDraft(eventId);
        if (medical == null) {
            throw new ClientServiceException("该病例不存在", OperationCodeConstants.DATA_ERROR);
        }
        //如果是主治医生创建的病例
        if (medical.getCrtId().equals(medical.getMajorDentistId())) {
            //是否在就诊当天之内 todo
        } else {
            //助理医生变更申请校验
            if (newestDraft == null) {
                throw new ClientServiceException("该病例不存在", OperationCodeConstants.DATA_ERROR);
            }
            //待审核草稿不能申请变更
            if (ApproveStatusEnum.APPROVE_PENDING.getCode().equals(newestDraft.getStatus())) {
                throw new ClientServiceException("该病历未经过主诊医生审核，不能申请修改！", OperationCodeConstants.APPLY_APPROVE_PENDING);
            }
            if (ApproveStatusEnum.AUDIT_REJECT.getCode().equals(newestDraft.getStatus())) {
                //校验最新拒绝审批时间
                if (!judgeRejectTimeout(newestDraft.getApproveTime())) {
                    throw new ClientServiceException("该病历已审核拒绝且可以修改，无须申请修改", OperationCodeConstants.DATA_EXIST);
                }
            }
        }
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
            if (StringUtils.isBlank(postGroups)) {
                throw new ClientServiceException("无权限操作", OperationCodeConstants.NO_PERMISSION_OPERATION);
            }
            List<String> postGroupList = Splitter.on(",").splitToList(postGroups);
            if (!postGroupList.contains("助手")) {
                throw new ClientServiceException("无权限操作", OperationCodeConstants.NO_PERMISSION_OPERATION);
            }
        }
    }

    private void isExistApply(Integer approveId) {
        //查询该病历是否存在待审批记录，不存在非法操作
        ApprovalRecord approvalRecord = mapper.selectByPrimaryKey(approveId);
        if (approvalRecord == null || !ApproveStatusEnum.APPROVE_PENDING.getCode().equals(approvalRecord.getStatus())) {
            throw new ClientServiceException("该病例申请不存在", OperationCodeConstants.DATA_ERROR);
        }
    }
}
