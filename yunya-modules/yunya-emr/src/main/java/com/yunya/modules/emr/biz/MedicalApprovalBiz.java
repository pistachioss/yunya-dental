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
import com.yunya.framework.common.model.*;
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

import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.modules.emr.constant.EmrError.*;
import static com.yunya.modules.emr.enums.ApplyTypeEnum.*;
import static com.yunya.modules.emr.enums.ApproveStatusEnum.*;
import static com.yunya.modules.emr.enums.EventTypeEnum.*;
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
    public ResponseResult applyAddDraftCase(DraftMedicalApplyModel draftModel) {
        boolean locked = false;
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer eventId = draftModel.getApplyBase().getEventId();
        String lockKey = Joiner.on(":").join(RedisConstants.LOCK_DRAFT_APPLY_NS, String.valueOf(eventId));
        String lockVal = BaseContextHandler.getUserID();
        log.info("新增草稿病例申请开始提交：[{}  ]", eventId);
        try {
            // 1. 锁定草稿病例
            locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("【锁定失败】草稿病历无法提交新增申请：[{}]", eventId);
                return ResponseUtil.error(KEY_IS_LOCKED);
            }
            log.info("【锁定成功】准备提交草稿病例新增申请...");

            //2. 检查登录人权限信息
            MedicalCommonRecord medical = medicalMapper.selectByPrimaryKey(eventId);
            RestErrorBo errorBo = checkAuth(medical, loginUserId, eventId);
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError());
            }
            //3. 检查草稿电子病例状态
            if (!MEDICAL_AUDIT_PENDING_STATUS.equals(medical.getStatus())) {
                log.warn("【申请失败】：病例[{}]状态异常，待审核状态不允许重复申请", eventId);
                return ResponseUtil.error(NO_ALLOW_REPEAT_APPLY);
            }
            //4. 检查草稿电子病例审批信息
            if (isExistDraftApply(eventId)) {
                log.warn("【申请失败】：病例[{}]病例已申请审批", eventId);
                return ResponseUtil.error(DATA_IS_EXISTED);
            }
            //5. 检查新增变更
            ApprovalRecord treatmentRecord = mapper.findTreatmentRecord(medical.getTreatmentId());
            if (treatmentRecord != null) {
                if (!loginUserId.equals(treatmentRecord.getProposerId())) {
                    log.warn("【申请失败】：无权限申请");
                    return ResponseUtil.error(NO_PERMISSION_OPERATION);
                }
                //是否超过截止时间
                if (isTimeOutOfDead(treatmentRecord)) {
                    log.warn("【申请失败】：病例[{}]申请已超过变更截止时间", eventId);
                    return ResponseUtil.error(NO_PERMISSION_OPERATION);
                }
            }
            //6. 提交申请新增草稿
            constructCreateEntity(draftModel.getApplyBase(), DRAFT_AUDIT.getCode(), ADD.getCode(), null);
            return ResponseUtil.success();
        } finally {
            if (locked) {
                log.info("【解锁成功】");
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
    public ResponseResult applyUpdateDraftCase(DraftMedicalApplyModel draftModel) {
        boolean locked = false;
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer eventId = draftModel.getApplyBase().getEventId();
        String lockKey = Joiner.on(":").join(RedisConstants.LOCK_DRAFT_APPLY_NS, String.valueOf(draftModel.getApplyBase().getEventId()));
        String lockVal = BaseContextHandler.getUserID();
        log.info("修改草稿病例申请开始提交：[{}]", lockKey);
        try {
            // 1. 锁定草稿病例
            locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("【锁定失败】草稿病历[{}]无法提交修改申请", eventId);
                return ResponseUtil.error(KEY_IS_LOCKED);
            }
            log.info("【锁定成功】准备提交草稿病例修改申请...");

            //2. 检查登录人权限信息，检查草稿电子病例状态
            MedicalCommonRecord medical = medicalMapper.selectByPrimaryKey(eventId);
            RestErrorBo errorBo = checkAuth(medical, loginUserId, eventId);
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError());
            }
            //3. 检查草稿电子病例状态
            if (MEDICAL_AUDIT_PENDING_STATUS.equals(medical.getStatus())) {
                log.warn("【申请失败】：病例[{}]状态异常，待审核状态不允许重复申请", eventId);
                return ResponseUtil.error(NO_ALLOW_REPEAT_APPLY);
            }
            //4. 检查草稿电子病例审批信息
            ApprovalRecord record = mapper.selectByPrimaryKey(draftModel.getId());
            if (record == null || !loginUserId.equals(record.getProposerId())) {
                log.warn("【申请失败】：无权限操作");
                return ResponseUtil.error(NO_PERMISSION_OPERATION);
            }
            //5. 草稿病例
            if (DRAFT_AUDIT.equals(record.getEventType())) {
                if (AUDIT_PASS.equals(record.getStatus())) {
                    log.warn("【申请失败】：草稿病例[{}]审批已通过", eventId);
                    return ResponseUtil.error(AUDIT_IS_PASS);
                }
                if (APPROVE_PENDING.equals(record.getStatus())) {
                    log.warn("【申请失败】：草稿病例[{}]正在审批中", eventId);
                    return ResponseUtil.error(APPLY_APPROVE_PENDING);
                }
                //拒绝审批是否超时
                if (AUDIT_REJECT.equals(record.getStatus()) && judgeRejectTimeout(record.getApproveTime())) {
                    log.warn("【申请失败】：草稿病例[{}]已超过拒绝审批时间24h", eventId);
                    return ResponseUtil.error(MODIFY_APPLY_TIMEOUT);
                }
             //6. 检查变更病例
            } else {
                if (!AUDIT_PASS.equals(record.getStatus())) {
                    log.warn("【申请失败】：申请变更病例[{}]被拒绝", eventId);
                    return ResponseUtil.error(CHANGE_APPLY_REJECTED);
                }
                //是否超过截止时间
                if (isTimeOutOfDead(record)) {
                    log.warn("【申请失败】：病例[{}]申请已超过变更截止时间", eventId);
                    return ResponseUtil.error(NO_PERMISSION_OPERATION);
                }
            }
            //7.草稿病例提交申请
            constructCreateEntity(draftModel.getApplyBase(), DRAFT_AUDIT.getCode(), UPDATE.getCode(), null);
            return ResponseUtil.error(OK);
        } finally {
            if (locked) {
                log.info("【解锁成功】");
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
    public ResponseResult applyAddChangeCase(ChangeMedicalApplyModel changeModel) {
        boolean locked = false;
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer eventId = changeModel.getApplyBase().getEventId();
        String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CHANGE_APPLY_NS, String.valueOf(changeModel.getApplyBase().getEventId()));
        String lockVal = BaseContextHandler.getUserID();
        log.info("新增病例变更申请开始提交：{}", eventId);
        try {
            // 1. 锁定就诊变更申请
            locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("【锁定失败】无法提交新增变更申请：{}", eventId);
                return ResponseUtil.error(KEY_IS_LOCKED);
            }
            log.info("【锁定成功】准备提交病例新增变更申请...");

            //2. 检查登录人权限
            List<String> permission = getPostPermission(loginUserId);
            if (!(permission.contains("助手") || permission.contains("医生"))) {
                log.warn("【申请失败】：无权限操作");
                return ResponseUtil.error(NO_PERMISSION_OPERATION);
            }
            //todo 检查就诊信息（权限，时间）

            //3. 检查电子病例
            MedicalCommonRecord treatment = findByTreatmentId(eventId);
            if (treatment != null) {
                log.warn("【申请失败】：就诊记录已生成电子病例：[{}]", treatment.getId());
                return ResponseUtil.error(MEDICAL_IS_EXIST);
            }
            //4. 检查变更审批
            if (isExistChangeToAudit(eventId)) {
                log.warn("【申请失败】：审核状态异常，申请已在待审批：[{}]", eventId);
                return ResponseUtil.error(APPLY_APPROVE_PENDING);
            }
            //5. 新增变更提交申请
            constructCreateEntity(changeModel.getApplyBase(), MEDICAL_CHANGE_AUDIT.getCode(), ADD.getCode(), changeModel.getApplyReason());
            return ResponseUtil.error(OK);
        } finally {
            if (locked) {
                log.info("【解锁成功】");
                redisUtils.unlock(lockKey, lockVal);
            }
        }
    }

    /**
     * 申请修改病例变更
     *
     * @param changeModel 申请病例变更修改参数
     */
    public ResponseResult applyUpdateChangeCase(ChangeMedicalApplyModel changeModel) {
        boolean locked = false;
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        String lockVal = BaseContextHandler.getUserID();
        Integer eventId = changeModel.getApplyBase().getEventId();
        String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CHANGE_APPLY_NS, String.valueOf(changeModel.getApplyBase().getEventId()));
        log.info("修改病例变更申请开始提交：{}", eventId);
        try {
            // 1. 锁定就诊变更申请
            locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("【锁定失败】无法提交新增变更申请：{}", eventId);
                return ResponseUtil.error(KEY_IS_LOCKED);
            }
            log.info("【锁定成功】准备提交病例新增变更申请...");
            //2. 检查登录人权限
            List<String> permission = getPostPermission(loginUserId);
            if (!(permission.contains("助手") || permission.contains("医生"))) {
                log.warn("【申请失败】：无权限操作");
                return ResponseUtil.error(NO_PERMISSION_OPERATION);
            }
            //3. 检查电子病历
            MedicalCommonRecord medical = medicalMapper.selectByPrimaryKey(eventId);
            if (medical == null || !loginUserId.equals(medical.getCrtId())) {
                log.warn("【申请失败】：无权限申请修改此病历，请联系新增病历医生申请修改！");
                return ResponseUtil.error(NO_AUTH_MODIFY_MED);
            }
            if (MEDICAL_AUDIT_PENDING_STATUS.equals(medical.getStatus())) {
                log.warn("【申请失败】：电子病例[{}]状态异常，待审核状态不能变更申请", eventId);
                return ResponseUtil.error(AUDIT_PENDING);
            }
            //4. 检查病历审批
            if (loginUserId.equals(medical.getMajorDentistId())) {
                //todo 查询就诊信息（就诊时间是否在当天）
                if (false) {
                    log.warn("【申请失败】：电子病历[{}]不需要申请修改，可以直接修改此病历！", eventId);
                    return ResponseUtil.error(NOT_NEED_APPLY);
                }
            } else {
                ApprovalRecord newestDraft = mapper.findNewestDraft(eventId);
                if (newestDraft == null || !loginUserId.equals(newestDraft.getProposerId())) {
                    log.warn("【申请失败】：无权限申请修改此病历，请联系新增病历医生申请修改！");
                    return ResponseUtil.error(NO_AUTH_MODIFY_MED);
                }
                //待审核草稿不能申请变更
                if (APPROVE_PENDING.equals(newestDraft.getStatus())) {
                    log.warn("【申请失败】：电子病例[{}]状态异常，待审核状态不能变更申请", eventId);
                    return ResponseUtil.error(AUDIT_PENDING);
                }
                if (AUDIT_REJECT.equals(newestDraft.getStatus())) {
                    //校验最新拒绝审批时间
                    if (!judgeRejectTimeout(newestDraft.getApproveTime())) {
                        log.warn("【申请失败】：电子病历[{}]可以直接修改，无需申请", eventId);
                        return ResponseUtil.error(REJECTED_NO_NEED_APPLY);
                    }
                }
            }
            //5. 提交修改变更申请
            constructCreateEntity(changeModel.getApplyBase(), MEDICAL_CHANGE_AUDIT.getCode(), UPDATE.getCode(), changeModel.getApplyReason());
            return ResponseUtil.error(OK);
        } finally {
            if (locked) {
                log.info("【解锁成功】");
                redisUtils.unlock(lockKey,lockVal);
            }
        }
    }

    /**
     * 病例审批通过
     *
     * @param approveId 审批id
     * @param passForm  审批通过
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult passMedical(Integer approveId, MedicalApprovePassForm passForm) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer eventId = passForm.getPassForm().getEventId();
        //1. 检查权限，审批状态
        RestErrorBo errorBo = checkAuthAndApprove(loginUserId, eventId, approveId);
        if (errorBo.getError() != null) {
            return ResponseUtil.error(errorBo.getError());
        }
        //2. 更新审批记录信息
        updateMedicalApprove(approveId, AUDIT_PASS.getCode(), null, null);
        //3. 更新电子病历信息
        passForm.getMedicalCommonRecordForm().setStatus(2);
        passForm.getMedicalCommonRecordForm().setApprovalTime(new Date());
        commonRecordBiz.updateMedicalApproval(passForm.getMedicalCommonRecordForm());
        return ResponseUtil.success();
    }

    /**
     * 病例审核拒绝
     *
     * @param approveId  审批Id
     * @param rejectForm 审批拒绝
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult rejectMedical(Integer approveId, MedicalApproveRejectForm rejectForm) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer eventId = rejectForm.getRejectForm().getEventId();
        //1. 检查权限，审批状态
        RestErrorBo errorBo = checkAuthAndApprove(loginUserId, eventId, approveId);
        if (errorBo.getError() != null) {
            return ResponseUtil.error(errorBo.getError());
        }
        //更新审批记录信息
        updateMedicalApprove(approveId, AUDIT_REJECT.getCode(), rejectForm.getRejectForm().getRejectReason(), null);
        //更新电子病历信息
        rejectForm.getMedicalCommonRecordForm().setStatus(3);
        rejectForm.getMedicalCommonRecordForm().setApprovalTime(new Date());
        commonRecordBiz.updateMedicalApproval(rejectForm.getMedicalCommonRecordForm());
        return ResponseUtil.success();
    }

    /**
     * 通过变更申请
     *
     * @param approveId 主键
     * @param passForm  通过对象
     */
    public ResponseResult passChange(Integer approveId, ChangeApprovePassForm passForm) {
        LocalDate changeDeadTime = passForm.getChangeDeadTime();
        LocalDate now = LocalDate.now();
        if (now.isAfter(changeDeadTime)) {
            throw new ClientServiceException("选择的允许变更截止时间不能早于操作当天时间", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        // 检查审批状态
        ApprovalRecord approvalRecord = mapper.selectByPrimaryKey(approveId);
        if (approvalRecord == null || !APPROVE_PENDING.getCode().equals(approvalRecord.getStatus())) {
            log.warn("【审批通过失败】：病历[{}]审核状态异常", passForm.getEventId());
            return ResponseUtil.error(MEDICAL_ALREADY_AUDITED);
        }
        //更新审批记录信息
        updateMedicalApprove(approveId, AUDIT_PASS.getCode(), null, changeDeadTime);
        return ResponseUtil.success();
    }

    /**
     * 拒绝变更对象
     *
     * @param approveId  主键
     * @param rejectForm 拒绝对象
     */
    public ResponseResult rejectChange(Integer approveId, ChangeApproveRejectForm rejectForm) {
        // 检查审批状态
        ApprovalRecord approvalRecord = mapper.selectByPrimaryKey(approveId);
        if (approvalRecord == null || !APPROVE_PENDING.getCode().equals(approvalRecord.getStatus())) {
            log.warn("【审批拒绝失败】：病历[{}]审核状态异常", rejectForm.getEventId());
            return ResponseUtil.error(MEDICAL_ALREADY_AUDITED);
        }
        //更新审批状态
        updateMedicalApprove(approveId, AUDIT_REJECT.getCode(), rejectForm.getRejectReason(), null);
        return ResponseUtil.success();
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
        List<ApprovalRecord> list = new ArrayList<>();
        Map<Integer, PatientBaseInfoVo> patientInfoMap = Maps.newHashMap();
        //初始化bo
        ApprovePageBo approveBo = ApprovePageBo.getInstance();
        //关键字模糊查询条件为空，先查审批相关信息
        if (StringUtils.isBlank(keyword)) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            //根据病例提交时间查询审批数据
            list = mapper.listMedicalByParam(null, submitTime, loginUserId, DRAFT_AUDIT.getCode(), auditStatus);
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
            List<ApprovalRecord> loginUserApproveList = mapper.listMedicalByParam(null, submitTime, loginUserId, DRAFT_AUDIT.getCode(), auditStatus);
            //查询登录人草稿审批的患者ids映射
            patientIdMap = getDraftPatientIdsByApplyType(loginUserApproveList, loginUserId);
            //根据查询关键字获取电子病例ids集合
            List<Integer> medicalIds = getQueryMedicalIds(patientIdMap, loginUserId, keyword, patientInfoMap);
            //根据电子病例Ids和病例提交时间查询审批数据
            if (CollectionUtils.isNotEmpty(medicalIds)) {
                PageHelper.startPage(query.getPageNum(), query.getPageSize());
                list = mapper.listMedicalByParam(medicalIds, submitTime, loginUserId, DRAFT_AUDIT.getCode(), auditStatus);
            }
        }
        //数据存入bo对象
        approveBo.assignMember(list, patientInfoMap);
        return approveBo;
    }

    private ApprovePageBo getChangeApproveBo(ChangeApproveQuery query, Integer auditStatus) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        String keyword = query.getKeyword();
        List<Integer> patientIds = null;
        List<ApprovalRecord> list = Lists.newArrayList();
        List<PatientBaseInfoVo> filterPatientList;
        Map<Integer, Integer> patientIdMap;
        Map<Integer, PatientBaseInfoVo> patientInfoMap = Maps.newHashMap();
        //初始化bo
        ApprovePageBo approveBo = ApprovePageBo.getInstance();
        //关键字模糊查询条件为空，先查审批相关信息
        if (StringUtils.isBlank(keyword)) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            //查询变更审批数据
            list = mapper.listMedicalByParam(null, null, loginUserId, MEDICAL_CHANGE_AUDIT.getCode(), auditStatus);
            //根据审批查询结果，生成申请类型映射
            Map<Integer, List<ApprovalRecord>> eventMap = list.stream().collect(groupingBy(ApprovalRecord::getApplyType));
            if (!org.springframework.util.CollectionUtils.isEmpty(eventMap)) {
                //取出新增变类型变更的审批结果
                List<ApprovalRecord> treatmentList = eventMap.get(ADD.getCode());
                if (CollectionUtils.isNotEmpty(treatmentList)) {
                    List<Integer> treatmentIds = treatmentList.stream().map(ApprovalRecord::getEventId).collect(toList());
                }
                //查询登录人变更审批的患者ids映射
                patientIdMap = getChangePatientIdsByApplyType(list, UPDATE.getCode(), loginUserId);
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
            List<ApprovalRecord> loginUserApproveList = mapper.listMedicalByParam(null, null, loginUserId, MEDICAL_CHANGE_AUDIT.getCode(), auditStatus);
            //查询登录人变更审批的患者ids映射
            patientIdMap = getChangePatientIdsByApplyType(loginUserApproveList, UPDATE.getCode(), loginUserId);
            //根据查询关键字获取电子病例ids集合
            List<Integer> medicalIds = getQueryMedicalIds(patientIdMap, loginUserId, keyword, patientInfoMap);
            //todo 合并就诊ids和电子病历ids
//            medicalIds.addAll(patientIds);
            //根据电子病例Ids和病例提交时间查询审批数据
            if (CollectionUtils.isNotEmpty(medicalIds)) {
                PageHelper.startPage(query.getPageNum(), query.getPageSize());
                list = mapper.listMedicalByParam(medicalIds, null, loginUserId, MEDICAL_CHANGE_AUDIT.getCode(), auditStatus);
            }
        }
        approveBo.assignMember(list, patientInfoMap);
        return approveBo;
    }

    private RestErrorBo checkAuthAndApprove(Integer loginUserId, Integer eventId, Integer approveId) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        //1. 检查操作人权限
        List<String> postPermission = getPostPermission(loginUserId);
        if (!postPermission.contains("医生")) {
            errorBo.setError(NO_PERMISSION_OPERATION);
            return errorBo;
        }
        //2. 检查电子病历
        MedicalCommonRecord medical = medicalMapper.selectByPrimaryKey(eventId);
        if (medical == null || !loginUserId.equals(medical.getMajorDentistId())) {
            log.warn("【审批通过失败】：无权限审批");
            errorBo.setError(NO_PERMISSION_OPERATION);
            return errorBo;
        }
        if (MEDICAL_AUDIT_PENDING_STATUS.equals(medical.getStatus())) {
            log.warn("【审批通过失败】：病历[{}]审核状态异常", eventId);
            errorBo.setError(MEDICAL_ALREADY_AUDITED);
            return errorBo;
        }
        //3. 检查审批记录
        ApprovalRecord record = mapper.selectByPrimaryKey(approveId);
        if (record == null || !loginUserId.equals(record.getApproverId())) {
            log.warn("【审批通过失败】：无权限审批");
            errorBo.setError(NO_PERMISSION_OPERATION);
            return errorBo;
        }
        if (!APPROVE_PENDING.equals(record.getStatus())) {
            errorBo.setError(MEDICAL_ALREADY_AUDITED);
            return errorBo;
        }
        return errorBo;
    }


    /**
     * 查询登录人草稿审批的患者ids
     *
     * @param list        源数据
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
            //<患者Id,电子病例id>映射
            patientMap = commonRecords.stream().collect(toMap(MedicalCommonRecord::getPatientId, MedicalCommonRecord::getId));
        }
        return patientMap;
    }

    /**
     * 查询登录人变更审批的患者ids
     *
     * @param list        源数据
     * @param applyType   变更申请类型
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
                patientMap = commonRecords.stream().collect(toMap(MedicalCommonRecord::getPatientId, MedicalCommonRecord::getId));
            }
        }
        return patientMap;
    }

    /**
     * 根据查询关键字获取电子病例ids集合
     *
     * @param patientIdMap 根据条件查询的登录人对应的<患者id，电子病例id>映射
     * @param loginUserId  登录人
     * @param keyword      关键字
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
            vo.setModifyDeadTime(Objects.equals(AUDIT_REJECT.getCode(), obj.getStatus()) ? obj.getApproveTime().plusDays(1) : null);
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
        addApplyEntity.setStatus(APPROVE_PENDING.getCode());
        addApplyEntity.setCrtId(loginUserId);
        addApplyEntity.setUpdId(loginUserId);
        mapper.insertSelective(addApplyEntity);
    }

    private MedicalCommonRecord findByTreatmentId(Integer eventId) {
        Example example = new Example(MedicalCommonRecord.class);
        example.createCriteria().andEqualTo("treatmentId", eventId);
        return medicalMapper.selectOneByExample(example);
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
        return hourGap > HOUR_GAP;
    }

    /**
     *  查询截止时间是否已经超时
     * @param record record
     * @return boolean
     */
    private boolean isTimeOutOfDead(ApprovalRecord record) {
        //病例变更截止时间
        LocalDate deadTime = record.getDeadTime();
        LocalDate now = LocalDate.now();
        return now.isAfter(deadTime);
    }

    private List<String> getPostPermission(Integer loginUserId) {
        List<String> postGroupList = Lists.newArrayList();
        //查询用户岗位信息
        SysUserInfoDetail loginUser = systemServiceFeign.findSysUserEmployeeInfoByUserId(loginUserId);
        if (loginUser != null) {
            String postGroups = loginUser.getPostGroups();
            if (StringUtils.isNotBlank(postGroups)) {
                postGroupList = Splitter.on(",").splitToList(postGroups);
            }
        }
        return postGroupList;
    }

    private boolean isExistDraftApply(Integer eventId) {
        int count = mapper.countDraftByEventId(eventId);
        return count > 0;
    }

    private boolean isExistChangeToAudit(Integer eventId) {
        int count = mapper.countToAuditChangeByEventId(eventId);
        return count > 0;
    }

    private boolean checkPostPermission(Integer loginUserId) {
        List<String> permission = getPostPermission(loginUserId);
        return !CollectionUtils.isEmpty(permission) && permission.contains("助手");
    }

    private RestErrorBo checkAuth(MedicalCommonRecord medicalCommonRecord, Integer loginUserId, Integer eventId) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        //检查登录人权限
        if (!checkPostPermission(loginUserId) || medicalCommonRecord == null ||
                            !loginUserId.equals(medicalCommonRecord.getCrtId())) {
            log.warn("【申请失败】：无权限操作");
            errorBo.setError(NO_PERMISSION_OPERATION);
            return errorBo;
        }
        return errorBo;
    }
}
