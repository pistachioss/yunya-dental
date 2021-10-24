package com.yunya.modules.emr.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.emr.domain.bo.ApproveChangePageBo;
import com.yunya.feign.emr.domain.bo.ApprovePageBo;
import com.yunya.feign.emr.domain.bo.AuditMedicalBo;
import com.yunya.feign.emr.domain.bo.ChangeCountBo;
import com.yunya.feign.emr.domain.bo.MedicalTreatmentBo;
import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.emr.domain.form.ChangeApprovePassForm;
import com.yunya.feign.emr.domain.form.ChangeApproveRejectForm;
import com.yunya.feign.emr.domain.form.MedicalApprovePassForm;
import com.yunya.feign.emr.domain.form.MedicalApproveRejectForm;
import com.yunya.feign.emr.domain.model.ApplyBaseModel;
import com.yunya.feign.emr.domain.model.ChangeMedicalApplyModel;
import com.yunya.feign.emr.domain.model.DraftMedicalApplyModel;
import com.yunya.feign.emr.domain.query.ChangeApproveQuery;
import com.yunya.feign.emr.domain.query.MedicalApproveQuery;
import com.yunya.feign.emr.domain.vo.MedicalApplyPageVo;
import com.yunya.feign.emr.domain.vo.MedicalApprovePageVo;
import com.yunya.feign.emr.domain.vo.MedicalChangeApplyPageVo;
import com.yunya.feign.emr.domain.vo.MedicalChangeApprovePageVo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.emr.ApprovalRecord;
import com.yunya.models.emr.MedicalCommonRecord;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.emr.mapper.ApprovalRecordMapper;
import com.yunya.modules.emr.mapper.MedicalCommonRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.modules.emr.enums.ApplyTypeEnum.ADD;
import static com.yunya.modules.emr.enums.ApplyTypeEnum.UPDATE;
import static com.yunya.modules.emr.enums.ApproveStatusEnum.*;
import static com.yunya.modules.emr.enums.EmrError.*;
import static com.yunya.modules.emr.enums.EventTypeEnum.*;
import static com.yunya.modules.emr.enums.TrueFalseEnum.*;
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
    private RemotePatientCentralServiceFeign patientFeign;
    @Resource
    private RemoteTreatmentServiceFeign treatmentFeign;
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
        log.info("新增草稿病例申请开始提交：[{}]", eventId);
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
            RestErrorBo errorBo = checkAuth(medical, loginUserId);
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError());
            }
            //3. 检查草稿电子病例状态
            if (!MEDICAL_AUDIT_PENDING_STATUS.equals(medical.getStatus())) {
                log.warn("【申请失败】：病例[{}]状态异常，电子病例状态不是待审核", eventId);
                return ResponseUtil.error(MEDICAL_STATUS_ERROR);
            }
            //4. 检查草稿电子病例审批信息
            if (isExistDraftApply(eventId)) {
                log.warn("【新增草稿病例申请失败】：病例[{}]病例已申请审批", eventId);
                return ResponseUtil.error(DATA_IS_EXISTED);
            }
            //5. 检查新增变更
            ApprovalRecord treatmentRecord = mapper.findTreatmentRecord(medical.getTreatmentId());
            if (treatmentRecord != null) {
                log.info("【新增草稿病例申请】就诊审核记录，详情：{}", JSONObject.toJSONString(treatmentRecord));
                if (!loginUserId.equals(treatmentRecord.getProposerId())) {
                    log.warn("【新增草稿病例申请失败】：无权限申请");
                    return ResponseUtil.error(NO_PERMISSION_OPERATION);
                }
                //是否超过截止时间
                if (isTimeOutOfDead(treatmentRecord)) {
                    log.warn("【新增草稿病例申请失败】：病例[{}]申请已超过变更截止时间", eventId);
                    return ResponseUtil.error(NO_PERMISSION_OPERATION);
                }
                updateChangeTime(draftModel.getId());
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
            RestErrorBo errorBo = checkAuth(medical, loginUserId);
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError());
            }
            //3. 检查病例审批信息
            ApprovalRecord record = mapper.selectByPrimaryKey(draftModel.getId());
            errorBo = checkApproveRecord(record, loginUserId);
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError());
            }
            //4. 草稿病例
            if (DRAFT_AUDIT.equals(record.getEventType())) {
                if (AUDIT_PASS.equals(record.getStatus())) {
                    log.warn("【修改草稿病例申请失败】：草稿病例[{}]审批已通过", eventId);
                    return ResponseUtil.error(AUDIT_IS_PASS);
                }
                if (APPROVE_PENDING.equals(record.getStatus())) {
                    log.warn("【修改草稿病例申请失败】：草稿病例[{}]正在审批中", eventId);
                    return ResponseUtil.error(APPLY_APPROVE_PENDING);
                }
                //拒绝审批是否超时
                if (AUDIT_REJECT.equals(record.getStatus()) && judgeRejectTimeout(record.getApproveTime())) {
                    log.warn("【修改草稿病例申请失败】：草稿病例[{}]已超过拒绝审批时间24h", eventId);
                    return ResponseUtil.error(MODIFY_APPLY_TIMEOUT);
                }
                //5. 检查变更病例
            } else {
                ApprovalRecord newestDraft = mapper.findNewestDraft(eventId);
                //待审核草稿不能申请变更
                if (APPROVE_PENDING.equals(newestDraft.getStatus())) {
                    log.warn("【修改草稿病例申请失败】：电子病例[{}]状态异常，待审核状态不能申请", eventId);
                    return ResponseUtil.error(AUDIT_PENDING);
                }
                if (!AUDIT_PASS.equals(record.getStatus())) {
                    log.warn("【修改草稿病例申请失败】：申请变更病例[{}]被拒绝", eventId);
                    return ResponseUtil.error(CHANGE_APPLY_REJECTED);
                }
                //是否超过截止时间
                if (isTimeOutOfDead(record)) {
                    log.warn("【修改草稿病例申请失败】：病例[{}]申请已超过变更截止时间", eventId);
                    return ResponseUtil.error(NO_PERMISSION_OPERATION);
                }
                updateChangeTime(draftModel.getId());
            }
            //6.草稿病例提交申请
            constructCreateEntity(draftModel.getApplyBase(), DRAFT_AUDIT.getCode(), UPDATE.getCode(), null);
            return ResponseUtil.success();
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
        Integer eventId = changeModel.getEventId();
        String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CHANGE_APPLY_NS, String.valueOf(eventId));
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
                log.warn("【申请变更新增失败】：无权限操作");
                return ResponseUtil.error(NO_PERMISSION_OPERATION);
            }
            //检查就诊信息（权限，时间）
            RestErrorBo restErrorBo = checkTreatmentInfo(eventId);
            if (restErrorBo.getError() != null) {
                return ResponseUtil.error(restErrorBo.getError());
            }
            //3. 检查电子病例
            MedicalCommonRecord medical = findByTreatmentId(eventId);
            if (medical != null) {
                log.warn("【申请变更新增失败】：就诊记录已生成电子病例：[{}]", medical.getId());
                return ResponseUtil.error(MEDICAL_IS_EXIST);
            }
            //检查变更申请记录，申请未通过时，不可多次重复申请变更
            ChangeCountBo applyCount = mapper.countMedicalChange(eventId, ADD.getCode());
            if (applyCount != null) {
                if (applyCount.getPendingCount() > 0) {
                    log.warn("【申请变更新增失败】：该病历已申请过修改且处于待审核状态，请勿重复申请！");
                    return ResponseUtil.error(CHANGE_PENDING_NOT_REPEAT_SUBMIT);
                }
                if (applyCount.getPassCount() > 0) {
                    log.warn("【申请变更新增失败】：该病历已申请过修改且处于审核通过状态，请勿重复申请！");
                    return ResponseUtil.error(ADD_CHANGE_PASS_NOT_REPEAT_SUBMIT);
                }
            }
            //4. 检查变更审批
            if (isExistChangeToAudit(eventId)) {
                log.warn("【申请变更新增失败】：审核状态异常，申请已在待审批：[{}]", eventId);
                return ResponseUtil.error(APPLY_APPROVE_PENDING);
            }
            //5. 新增变更提交申请
            constructCreateEntity(buildApplyBaseModel(changeModel, loginUserId), MEDICAL_CHANGE_AUDIT.getCode(), ADD.getCode(), changeModel.getApplyReason());
            return ResponseUtil.success();
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
        Integer eventId = changeModel.getEventId();
        String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CHANGE_APPLY_NS, String.valueOf(eventId));
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
                log.warn("【申请变更修改失败】：无权限操作");
                return ResponseUtil.error(NO_PERMISSION_OPERATION);
            }
            //3. 检查电子病历
            MedicalCommonRecord medical = medicalMapper.selectByPrimaryKey(eventId);
            if (medical == null || !loginUserId.equals(medical.getCrtId())) {
                log.warn("【申请变更修改失败】：无权限申请修改此病历，请联系新增病历医生申请修改！");
                return ResponseUtil.error(NO_AUTH_MODIFY_MED);
            }
            if (MEDICAL_AUDIT_PENDING_STATUS.equals(medical.getStatus())) {
                log.warn("【申请失败】：病例[{}]状态异常，该病历未经过主诊医生审核，不能申请修改", eventId);
                return ResponseUtil.error(AUDIT_PENDING);
            }
            //检查变更申请记录，申请未通过时，不可多次重复申请变更
            ChangeCountBo applyCount = mapper.countMedicalChange(eventId, UPDATE.getCode());
            if (applyCount != null) {
                if (applyCount.getPendingCount() > 0) {
                    log.warn("【申请变更修改失败】：该病历已申请过修改且处于待审核状态，请勿重复申请！");
                    return ResponseUtil.error(CHANGE_PENDING_NOT_REPEAT_SUBMIT);
                }
                if (applyCount.getPassCount() > 0) {
                    log.warn("【申请变更修改失败】：该病历已申请过修改且处于审核通过状态，请勿重复申请！");
                    return ResponseUtil.error(UPDATE_CHANGE_PASS_REPEAT_SUBMIT);
                }
            }
            //4. 检查病历审批
            if (loginUserId.equals(medical.getMajorDentistId())) {
                //检查就诊信息（权限，时间）
                RestErrorBo restErrorBo = checkTreatmentInfo(medical.getTreatmentId());
                if (restErrorBo.getError() != null) {
                    return ResponseUtil.error(restErrorBo.getError());
                }
            } else {
                ApprovalRecord newestDraft = mapper.findNewestDraft(eventId);
                if (newestDraft == null || !loginUserId.equals(newestDraft.getProposerId())) {
                    log.warn("【申请变更修改失败】：无权限申请修改此病历，请联系新增病历医生申请修改！");
                    return ResponseUtil.error(NO_AUTH_MODIFY_MED);
                }
                //待审核草稿不能申请变更
                if (APPROVE_PENDING.equals(newestDraft.getStatus())) {
                    log.warn("【申请变更修改失败】：电子病例[{}]状态异常，待审核状态不能变更申请", eventId);
                    return ResponseUtil.error(AUDIT_PENDING);
                }
                if (AUDIT_REJECT.equals(newestDraft.getStatus())) {
                    //校验最新拒绝审批时间
                    if (!judgeRejectTimeout(newestDraft.getApproveTime())) {
                        log.warn("【申请变更修改失败】：电子病历[{}]可以直接修改，无需申请", eventId);
                        return ResponseUtil.error(REJECTED_NO_NEED_APPLY);
                    }
                }
            }
            //5. 提交修改变更申请
            constructCreateEntity(buildApplyBaseModel(changeModel, loginUserId), MEDICAL_CHANGE_AUDIT.getCode(), UPDATE.getCode(), changeModel.getApplyReason());
            return ResponseUtil.success();
        } finally {
            if (locked) {
                log.info("【解锁成功】");
                redisUtils.unlock(lockKey, lockVal);
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
            log.warn("【审批通过失败】：选择的允许变更截止时间不能早于操作当天时间");
            return ResponseUtil.error(DEADLINE_BEYOND_NOW);
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
        PageInfo<MedicalApplyPageVo> pageInfo = new PageInfo<>(assembleDraftApplyVos(draftApplyBo));
        pageInfo.setPageNum(draftApplyBo.getPageNum());
        pageInfo.setTotal(draftApplyBo.getTotal());
        return pageInfo;
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
        PageInfo<MedicalApprovePageVo> pageInfo = new PageInfo<>(assembleDraftApproveVos(draftApproveBo));
        pageInfo.setPageNum(draftApproveBo.getPageNum());
        pageInfo.setTotal(draftApproveBo.getTotal());
        return pageInfo;
    }

    public PageInfo<MedicalChangeApplyPageVo> getChangeApplyPage(ChangeApproveQuery query) {
        //条件查询变更申请
        ApproveChangePageBo changeApplyBo = getChangeApproveBo(query, 0);
        PageInfo<MedicalChangeApplyPageVo> pageInfo = new PageInfo<>(assembleChangeApplyVos(changeApplyBo));
        pageInfo.setPageNum(changeApplyBo.getPageNum());
        pageInfo.setTotal(changeApplyBo.getTotal());
        return pageInfo;
    }

    public PageInfo<MedicalChangeApprovePageVo> getChangeApprovePage(ChangeApproveQuery query) {
        //条件查询变更审批
        ApproveChangePageBo changeApproveBo = getChangeApproveBo(query, 1);
        PageInfo<MedicalChangeApprovePageVo> pageInfo = new PageInfo<>(assembleChangeApproveVos(changeApproveBo));
        pageInfo.setPageNum(changeApproveBo.getPageNum());
        pageInfo.setTotal(changeApproveBo.getTotal());
        return pageInfo;
    }

    private ApprovePageBo getApproveBo(MedicalApproveQuery query, Integer auditStatus) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        String submitTime = query.getSubmitTime();
        String startTime = query.getStartTime();
        String endTime = query.getEndTime();
        String keyword = query.getKeyword();
        List<ApprovalRecord> list = new ArrayList<>();
        //初始化bo
        ApprovePageBo approveBo = ApprovePageBo.getInstance();
        Page<ApprovalRecord> page = new Page<>();
        //关键字模糊查询条件为空，先查审批相关信息
        if (StringUtils.isBlank(keyword)) {
            page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
            //根据病例提交时间查询审批数据
            list = mapper.listMedicalNewByParam(null, startTime, endTime,loginUserId, DRAFT_AUDIT.getCode(), auditStatus);
            if (CollectionUtils.isNotEmpty(list)) {
                //构建bo
                buildPatientAndTreatment(list, loginUserId, approveBo);
            }
        } else {
            //根据条件查询登录人所有的申请记录集合
            List<ApprovalRecord> loginUserApproveList = mapper.listMedicalNewByParam(null, startTime, endTime, loginUserId, DRAFT_AUDIT.getCode(), auditStatus);
            if (CollectionUtils.isNotEmpty(loginUserApproveList)) {
                //查询登录人草稿审批的患者ids映射
                AuditMedicalBo auditMedicalBo = getDraftPatientIdsByApplyType(loginUserApproveList);
                //根据查询关键字获取电子病例ids集合
                List<Integer> medicalIds = getQueryMedicalIdsAndSetBo(auditMedicalBo, loginUserId, keyword, approveBo);
                //根据电子病例Ids和病例提交时间查询审批数据
                if (CollectionUtils.isNotEmpty(medicalIds)) {
                    page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
                    list = mapper.listMedicalNewByParam(medicalIds, startTime, endTime, loginUserId, DRAFT_AUDIT.getCode(), auditStatus);
                }
            }
        }
        //数据存入bo对象
        approveBo.setAuditList(list);
        //设置分页信息
        approveBo.setPageNum(page.getPageNum());
        approveBo.setTotal(page.getTotal());
        return approveBo;
    }

    private ApproveChangePageBo getChangeApproveBo(ChangeApproveQuery query, Integer auditStatus) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        String keyword = query.getKeyword();
        List<ApprovalRecord> list = Lists.newArrayList();
        //初始化bo
        ApproveChangePageBo approveBo = ApproveChangePageBo.getInstance();
        Page<ApprovalRecord> page = new Page<>();
        //关键字模糊查询条件为空，先查审批相关信息
        if (StringUtils.isBlank(keyword)) {
            page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
            //查询变更审批数据
            list = mapper.listMedicalByParam(null, null, loginUserId, MEDICAL_CHANGE_AUDIT.getCode(), auditStatus);
            if (CollectionUtils.isNotEmpty(list)) {
                //构建就诊的信息映射
                buildOfNoParamChangeMap(list, ADD.getCode(), loginUserId, approveBo);
                //构建电子病历的信息映射
                buildOfNoParamChangeMap(list, UPDATE.getCode(), loginUserId, approveBo);
            }
        } else {
            //查询变更审批数据
            List<ApprovalRecord> loginUserApproveList = mapper.listMedicalByParam(null, null, loginUserId, MEDICAL_CHANGE_AUDIT.getCode(), auditStatus);
            if (CollectionUtils.isNotEmpty(loginUserApproveList)) {
                //构建就诊和电子病例的信息
                List<Integer> treatmentIds = buildChangeMapOfKeyword(loginUserApproveList, ADD.getCode(), keyword, null, approveBo);
                List<Integer> medicalIds = buildChangeMapOfKeyword(loginUserApproveList, UPDATE.getCode(), keyword, loginUserId, approveBo);
                List<Integer> eventIds = Lists.newArrayListWithCapacity(treatmentIds.size() + medicalIds.size());
                eventIds.addAll(treatmentIds);
                eventIds.addAll(medicalIds);
                //根据电子病例Ids和病例提交时间查询审批数据
                if (CollectionUtils.isNotEmpty(eventIds)) {
                    page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
                    list = mapper.listMedicalByParam(eventIds, null, loginUserId, MEDICAL_CHANGE_AUDIT.getCode(), auditStatus);
                }
            }
        }
        approveBo.setAuditList(list);
        //设置分页信息
        approveBo.setPageNum(page.getPageNum());
        approveBo.setTotal(page.getTotal());
        return approveBo;
    }

    private RestErrorBo checkAuthAndApprove(Integer loginUserId, Integer eventId, Integer approveId) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        //1. 检查操作人权限
        List<String> postPermission = getPostPermission(loginUserId);
        log.info("审批人权限：{}", postPermission);
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
        if (NORMAL_MEDICAL_STATUS.equals(medical.getStatus())) {
            log.warn("【审批通过失败】：病历[{}]审核状态异常", eventId);
            errorBo.setError(NORMAL_MEDICAL_NO_PERMISSION);
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
     * @param list 源数据
     * @return 患者ids
     */
    public AuditMedicalBo getDraftPatientIdsByApplyType(List<ApprovalRecord> list) {
        AuditMedicalBo bo = AuditMedicalBo.getInstance();
        //审批结果的所有电子病例id集合
        List<Integer> medicalIds = list.stream().map(ApprovalRecord::getEventId).collect(toList());
        if (CollectionUtils.isNotEmpty(medicalIds)) {
            //根据审批的事件Ids查询电子病例信息集合
            List<MedicalCommonRecord> commonRecords = findByMedicalIds(medicalIds);
            //患者ids
            List<Integer> patientIds = commonRecords.stream().map(MedicalCommonRecord::getPatientId).collect(toList());
            //就诊ids
            List<Integer> treatmentIds = commonRecords.stream().map(MedicalCommonRecord::getTreatmentId).collect(toList());
            //患者映射 <medicalId, patientId>
            Map<Integer, Integer> patientIdMap = commonRecords.stream().collect(toMap(MedicalCommonRecord::getId, MedicalCommonRecord::getPatientId));
            //就诊映射 <medicalId, treatmentId>
            Map<Integer, Integer> treatmentIdMap = commonRecords.stream().collect(toMap(MedicalCommonRecord::getId, MedicalCommonRecord::getTreatmentId));
            bo.setPatientMap(patientIdMap);
            bo.setTreatmentMap(treatmentIdMap);
            bo.setPatientIds(patientIds);
            bo.setTreatmentIds(treatmentIds);
        }
        return bo;
    }

    /**
     * 根据查询关键字获取电子病例ids集合
     *
     * @param auditMedicalBo 包含患者，就诊信息的bo
     * @param loginUserId    登录人
     * @param keyword        关键字
     * @return list
     */
    private List<Integer> getQueryMedicalIdsAndSetBo(AuditMedicalBo auditMedicalBo, Integer loginUserId, String keyword
            , ApprovePageBo approveBo) {
        List<Integer> patientIds = Lists.newArrayList(auditMedicalBo.getPatientIds());
        List<Integer> medicalIds = Lists.newArrayList();
        //调用患者接口服务，查询条件下的所有患者信息
        List<PatientBaseInfoVo> filterPatientList = getPatientsByKeyword(patientIds, keyword);
        if (CollectionUtils.isNotEmpty(filterPatientList)) {
            //取出筛选结果中的患者id集合
            patientIds = filterPatientList.stream().map(PatientBaseInfoVo::getId).collect(toList());
            //查询该登录助手的对应的患者ids的电子病例集合
            List<MedicalCommonRecord> commonRecords = findByPatientIds(patientIds, loginUserId);
            if (CollectionUtils.isNotEmpty(commonRecords)) {
                medicalIds = commonRecords.stream().map(MedicalCommonRecord::getId).collect(toList());
                //筛选后的患者信息映射 <patientId, patient>
                Map<Integer, PatientBaseInfoVo> patientMap = filterPatientList.stream().collect(toMap(PatientBaseInfoVo::getId, Function.identity()));
                //获取就诊相关信息
                List<Integer> treatmentIds = commonRecords.stream().map(MedicalCommonRecord::getTreatmentId).collect(toList());
                //获取就诊相关信息 <treatmentId, bo>
                Map<Integer, MedicalTreatmentBo> treatmentMap = buildTreatmentMap(treatmentIds);
                //构建就诊映射 <medicalId, bo>
                Map<Integer, MedicalTreatmentBo> treatmentInfoMap = Maps.newHashMapWithExpectedSize(treatmentMap.size());
                //构建患者映射 <medicalId, patient>
                Map<Integer, PatientBaseInfoVo> patientInfoMap = Maps.newHashMapWithExpectedSize(filterPatientList.size());
                commonRecords.forEach(obj -> {
                    treatmentInfoMap.put(obj.getId(), treatmentMap.get(obj.getTreatmentId()));
                    patientInfoMap.put(obj.getId(), patientMap.get(obj.getPatientId()));
                });
                //构建bo
                approveBo.assignMember(patientInfoMap, treatmentInfoMap);
            }
        }
        return medicalIds;
    }

    private List<MedicalApplyPageVo> assembleDraftApplyVos(ApprovePageBo draftApplyBo) {
        //查询的审批结果
        List<ApprovalRecord> auditList = draftApplyBo.getAuditList();
        //查询的患者信息映射
        Map<Integer, PatientBaseInfoVo> patientInfoMap = draftApplyBo.getPatientInfoMap();
        Map<Integer, MedicalTreatmentBo> treatmentBoMap = draftApplyBo.getTreatmentBoMap();
        //返回Vo集合
        List<MedicalApplyPageVo> resultList = Lists.newArrayListWithExpectedSize(auditList.size());
        //取出所有审批人id（主治医生id）
        Set<Integer> doctorIds = auditList.stream().map(ApprovalRecord::getApproverId).collect(toSet());
        //生成user信息映射
        Map<Integer, SysUserInfoDetail> doctorInfoMap = generateUserMap(doctorIds);
        auditList.forEach(obj -> {
            //电子病例对应的患者信息
            PatientBaseInfoVo patientInfo = patientInfoMap.get(obj.getEventId());
            MedicalTreatmentBo medicalTreatmentBo = treatmentBoMap.get(obj.getEventId());
            //主治医生信息
            SysUserInfoDetail majorDoctorInfo = doctorInfoMap.get(obj.getApproverId());
            MedicalApplyPageVo vo = new MedicalApplyPageVo();
            vo.setId(obj.getId());
            vo.setEventId(obj.getEventId());
            vo.setPatientId(patientInfo == null ? null : patientInfo.getId());
            vo.setPatientName(patientInfo == null ? null : patientInfo.getName());
            vo.setMedicalNum(patientInfo == null ? null : patientInfo.getMedicalNumber());
            vo.setTreatmentClinicName(medicalTreatmentBo == null ? null : medicalTreatmentBo.getTreatmentClinicName());
            vo.setMajorDentistName(majorDoctorInfo == null ? null : majorDoctorInfo.getName());
            vo.setTreatmentDate(medicalTreatmentBo == null ? null : medicalTreatmentBo.getTreatmentDate());
            vo.setSubmitTime(obj.getCrtTime());
            vo.setApproveStatus(obj.getStatus());
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
        //电子病例--查询的就诊集合
        Map<Integer, MedicalTreatmentBo> treatmentBoMap = draftApproveBo.getTreatmentBoMap();
        List<MedicalApprovePageVo> resultList = Lists.newArrayListWithExpectedSize(auditList.size());
        //取出所有申请人Id（助手医生id）
        Set<Integer> proposerIds = auditList.stream().map(ApprovalRecord::getProposerId).collect(toSet());
        //生成user信息映射
        Map<Integer, SysUserInfoDetail> doctorInfoMap = generateUserMap(proposerIds);
        auditList.forEach(obj -> {
            //电子病例对应的患者信息
            PatientBaseInfoVo patientInfo = patientInfoMap.get(obj.getEventId());
            MedicalTreatmentBo medicalTreatmentBo = treatmentBoMap.get(obj.getEventId());
            //助理医生信息
            SysUserInfoDetail assistantDoctorInfo = doctorInfoMap.get(obj.getProposerId());
            MedicalApprovePageVo vo = new MedicalApprovePageVo();
            vo.setId(obj.getId());
            vo.setEventId(obj.getEventId());
            vo.setPatientId(patientInfo == null ? null : patientInfo.getId());
            vo.setPatientName(patientInfo == null ? null : patientInfo.getName());
            vo.setMedicalNum(patientInfo == null ? null : patientInfo.getMedicalNumber());
            vo.setTreatmentClinicName(medicalTreatmentBo == null ? null : medicalTreatmentBo.getTreatmentClinicName());
            vo.setAssistantDentistName(assistantDoctorInfo == null ? null : assistantDoctorInfo.getName());
            vo.setTreatmentDate(medicalTreatmentBo == null ? null : medicalTreatmentBo.getTreatmentDate());
            vo.setSubmitTime(obj.getCrtTime());
            vo.setApproveStatus(obj.getStatus());
            vo.setRejectReason(obj.getApproveReason());
            resultList.add(vo);
        });
        return resultList;
    }

    private List<MedicalChangeApplyPageVo> assembleChangeApplyVos(ApproveChangePageBo changeApplyBo) {
        //查询的审批结果
        List<ApprovalRecord> auditList = changeApplyBo.getAuditList();
        //电子病例--查询的患者集合
        Map<Integer, PatientBaseInfoVo> patientInfoMap = changeApplyBo.getPatientInfoMap();
        //电子病例--查询的就诊集合
        Map<Integer, MedicalTreatmentBo> treatmentBoMap = changeApplyBo.getTreatmentBoMap();
        //就诊记录--查询的患者集合
        Map<Integer, PatientBaseInfoVo> trePatientInfoMap = changeApplyBo.getTrePatientInfoMap();
        //就诊记录--查询的就诊集合
        Map<Integer, MedicalTreatmentBo> treTreatmentBoMap = changeApplyBo.getTreTreatmentBoMap();
        List<MedicalChangeApplyPageVo> resultList = Lists.newArrayListWithExpectedSize(auditList.size());
        auditList.forEach(obj -> {
            PatientBaseInfoVo patientBaseInfoVo;
            MedicalTreatmentBo medicalTreatmentBo;
            if (ADD.equals(obj.getApplyType())) {
                patientBaseInfoVo = trePatientInfoMap.get(obj.getEventId());
                medicalTreatmentBo = treTreatmentBoMap.get(obj.getEventId());
            } else {
                patientBaseInfoVo = patientInfoMap.get(obj.getEventId());
                medicalTreatmentBo = treatmentBoMap.get(obj.getEventId());
            }
            MedicalChangeApplyPageVo vo = new MedicalChangeApplyPageVo();
            vo.setId(obj.getId());
            vo.setEventId(obj.getEventId());
            vo.setPatientId(patientBaseInfoVo == null ? null : patientBaseInfoVo.getId());
            vo.setPatientName(patientBaseInfoVo == null ? null : patientBaseInfoVo.getName());
            vo.setMedicalNum(patientBaseInfoVo == null ? null : patientBaseInfoVo.getMedicalNumber());
            vo.setTreatmentClinicName(medicalTreatmentBo == null ? null : medicalTreatmentBo.getTreatmentClinicName());
            vo.setTreatmentDate(medicalTreatmentBo == null ? null : medicalTreatmentBo.getTreatmentDate());
            vo.setApplyType(obj.getApplyType());
            vo.setApplyReason(obj.getApplyReason());
            vo.setApproveStatus(obj.getStatus());
            vo.setChangeDeadTime(obj.getDeadTime());
            vo.setRejectReason(obj.getApproveReason());
            if (obj.getApproveTime() != null) {
                //新时间是否大于审批时间
                int result = obj.getUpdTime().compareTo(obj.getApproveTime());
                vo.setWhetherOperated(result > 0 ? TRUE.getCode() : FALSE.getCode());
            }
            resultList.add(vo);
        });
        return resultList;
    }

    private List<MedicalChangeApprovePageVo> assembleChangeApproveVos(ApproveChangePageBo changeApproveBo) {
        //查询的审批结果
        List<ApprovalRecord> auditList = changeApproveBo.getAuditList();
        //查询的患者集合
        List<MedicalChangeApprovePageVo> resultList = Lists.newArrayListWithExpectedSize(auditList.size());
        //电子病例--查询的患者集合
        Map<Integer, PatientBaseInfoVo> patientInfoMap = changeApproveBo.getPatientInfoMap();
        //就诊记录--查询的患者集合
        Map<Integer, PatientBaseInfoVo> trePatientInfoMap = changeApproveBo.getTrePatientInfoMap();
        auditList.forEach(obj -> {
            PatientBaseInfoVo patientBaseInfoVo;
            if (ADD.equals(obj.getApplyType())) {
                patientBaseInfoVo = trePatientInfoMap.get(obj.getEventId());
            } else {
                patientBaseInfoVo = patientInfoMap.get(obj.getEventId());
            }
            SysUserInfoDetail applyDentist = systemServiceFeign.findSysUserEmployeeInfoByUserId(obj.getProposerId());
            MedicalChangeApprovePageVo vo = new MedicalChangeApprovePageVo();
            vo.setId(obj.getId());
            vo.setEventId(obj.getEventId());
            vo.setPatientName(patientBaseInfoVo == null ? null : patientBaseInfoVo.getName());
            vo.setMedicalNum(patientBaseInfoVo == null ? null : patientBaseInfoVo.getMedicalNumber());
            vo.setApplyTypeDate(LocalDate.of(obj.getCrtTime().getYear(), obj.getCrtTime().getMonth(), obj.getCrtTime().getDayOfMonth()));
            vo.setApplyType(obj.getApplyType());
            vo.setApplyDentistName(applyDentist == null ? null : applyDentist.getName());
            vo.setApplyReason(obj.getApplyReason());
            vo.setChangeDeadTime(obj.getDeadTime());
            vo.setApproveStatus(obj.getStatus());
            vo.setRejectReason(obj.getApproveReason());
            resultList.add(vo);
        });
        return resultList;
    }

    private Map<Integer, SysUserInfoDetail> generateUserMap(Set<Integer> userIds) {
        Map<Integer, SysUserInfoDetail> userInfoMap = Maps.newHashMapWithExpectedSize(userIds.size());
        //查询所有主治医师信息 存入映射
        userIds.stream().forEach(doctorId -> {
            SysUserInfoDetail doctorInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(doctorId);
            if (doctorInfo != null) {
                userInfoMap.put(doctorId, doctorInfo);
            }
        });
        return userInfoMap;
    }

    private ApplyBaseModel buildApplyBaseModel(ChangeMedicalApplyModel changeModel, Integer loginUserId) {
        ApplyBaseModel applyBase = new ApplyBaseModel();
        applyBase.setEventId(changeModel.getEventId());
        applyBase.setProposerId(loginUserId);
        return applyBase;
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
        record.setUpdTime(now);
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

    private RestErrorBo checkApproveRecord(ApprovalRecord record, Integer loginUserId) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        if (record == null) {
            log.warn("【修改草稿病例申请失败】：审批数据不存在");
            errorBo.setError(APPROVE_RECORD_NOT_EXIST);
            return errorBo;
        }
        if (!loginUserId.equals(record.getProposerId())) {
            log.warn("【修改草稿病例申请申请失败】：无权限操作，操作人:[{}]", loginUserId);
            errorBo.setError(NO_PERMISSION_OPERATION);
            return errorBo;
        }
        return errorBo;
    }

    private MedicalCommonRecord findByTreatmentId(Integer eventId) {
        Example example = new Example(MedicalCommonRecord.class);
        example.createCriteria().andEqualTo("treatmentId", eventId);
        return medicalMapper.selectOneByExample(example);
    }

    private List<MedicalCommonRecord> findByMedicalIds(List<Integer> medicalIds) {
        Example example = new Example(MedicalCommonRecord.class);
        example.createCriteria().andIn("id", medicalIds);
        return medicalMapper.selectByExample(example);
    }

    private List<MedicalCommonRecord> findByPatientIds(List<Integer> patientIds, Integer loginUserId) {
        Example example = new Example(MedicalCommonRecord.class);
        example.createCriteria().andIn("patientId", patientIds);
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
     * 查询截止时间是否已经超时
     *
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
        log.info("操作人权限岗位组：{}", permission);
        return !CollectionUtils.isEmpty(permission) && permission.contains("助手");
    }

    private RestErrorBo checkAuth(MedicalCommonRecord medicalCommonRecord, Integer loginUserId) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        //检查登录人权限
        if (!checkPostPermission(loginUserId) || medicalCommonRecord == null ||
                !loginUserId.equals(medicalCommonRecord.getCrtId())) {
            log.warn("无权限操作，操作人:[{}]", loginUserId);
            errorBo.setError(NO_PERMISSION_OPERATION);
            return errorBo;
        }
        return errorBo;
    }

    private void buildPatientAndTreatment(List<ApprovalRecord> list, Integer loginUserId, ApprovePageBo pageBo) {
        Map<Integer, PatientBaseInfoVo> patientInfoMap = Maps.newHashMap();
        Map<Integer, MedicalTreatmentBo> treatmentInfoMap = Maps.newHashMap();
        //查询登录人草稿审批的患者ids映射
        AuditMedicalBo auditMedicalBo = getDraftPatientIdsByApplyType(list);
        if (CollectionUtils.isNotEmpty(auditMedicalBo.getPatientIds())) {
            //调用患者接口服务，查询条件下的所有患者信息
            List<PatientBaseInfoVo> filterPatientList = patientFeign.findPatientInfoByIds(auditMedicalBo.getPatientIds());
            if (CollectionUtils.isNotEmpty(filterPatientList)) {
                Map<Integer, PatientBaseInfoVo> patientMap = filterPatientList.stream().collect(toMap(PatientBaseInfoVo::getId, Function.identity()));
                //构建患者映射
                auditMedicalBo.getPatientMap().forEach((k, v) -> patientInfoMap.put(k, patientMap.get(v)));
            }
            //获取就诊相关信息
            Map<Integer, MedicalTreatmentBo> treatmentMap = buildTreatmentMap(auditMedicalBo.getTreatmentIds());
            //构建就诊映射
            auditMedicalBo.getTreatmentMap().forEach((k, v) -> treatmentInfoMap.put(k, treatmentMap.get(v)));
            //构建bo
            pageBo.assignMember(patientInfoMap, treatmentInfoMap);
        }
    }

    /**
     * 构建就诊信息历映射
     *
     * @param treatmentIds 就诊ids
     * @return map
     */
    private Map<Integer, MedicalTreatmentBo> buildTreatmentMap(List<Integer> treatmentIds) {
        List<MedicalTreatmentBo> medTreatMedicalBos = Lists.newArrayListWithExpectedSize(treatmentIds.size());
        for (Integer treatmentId : treatmentIds) {
            //查询就诊
            TreatmentRecord treatment = treatmentFeign.findTreatmentRecordById(treatmentId);
            if (treatment == null) {
                continue;
            }
            MedicalTreatmentBo bo = MedicalTreatmentBo.getInstance();
            if (treatment.getRegisteredId() != null) {
                //查询挂号
                Registered register = treatmentFeign.findRegisteredById(treatment.getRegisteredId());
                if (register != null && register.getAssistantId() != null) {
                    //查询助手
                    SysUserInfoDetail assistant = systemServiceFeign.findSysUserEmployeeInfoByUserId(register.getAssistantId());
                    bo.setAssistantDentistName(assistant == null ? null : assistant.getName());
                }
            }
            if (treatment.getOrgId() != null) {
                //查询就诊门诊
                OrganizationInfo clinic = systemServiceFeign.findOrgInfoByOrgId(treatment.getOrgId());
                bo.setTreatmentClinicName(clinic == null ? null : clinic.getName());
            }
            bo.setTreatmentDate(treatment.getTreatStartTime() == null ? null : treatment.getTreatStartTime().toInstant()
                    .atOffset(ZoneOffset.ofHours(8)).toLocalDate());
            bo.setTreatmentId(treatmentId);
            bo.setPatientId(treatment.getPatientId());
            medTreatMedicalBos.add(bo);
        }
        return medTreatMedicalBos.stream().collect(toMap(MedicalTreatmentBo::getTreatmentId, Function.identity()));
    }

    private void buildOfNoParamChangeMap(List<ApprovalRecord> list, Integer applyType, Integer loginUserId, ApproveChangePageBo pageBo) {
        Map<Integer, PatientBaseInfoVo> trePatientInfoMap = Maps.newHashMap();
        Map<Integer, MedicalTreatmentBo> treTreatmentBoMap = Maps.newHashMap();
        //根据审批查询结果，生成申请类型映射
        Map<Integer, List<ApprovalRecord>> eventMap = list.stream().collect(groupingBy(ApprovalRecord::getApplyType));
        List<ApprovalRecord> approveList = eventMap.get(applyType);
        if (CollectionUtils.isNotEmpty(approveList)) {
            if (ADD.equals(applyType)) {
                //取出新增变类型变更的就诊ids
                List<Integer> treatmentIds = approveList.stream().map(ApprovalRecord::getEventId).collect(toList());
                //查询就诊接口，构建就诊映射 <treatmentId, treatment>
                Map<Integer, MedicalTreatmentBo> treatmentMap = buildTreatmentMap(treatmentIds);
                //取出查询就诊记录中的患者ids
                List<Integer> patientIds = treatmentMap.values().stream().map(MedicalTreatmentBo::getPatientId).collect(toList());
                List<PatientBaseInfoVo> patientInfos = patientFeign.findPatientInfoByIds(patientIds);
                if (CollectionUtils.isNotEmpty(patientInfos)) {
                    //构建患者映射 <patientId, PatientBaseInfoVo>
                    Map<Integer, PatientBaseInfoVo> patientMap = patientInfos.stream().collect(toMap(PatientBaseInfoVo::getId,
                            Function.identity()));
                    //数据构建最终结构
                    treatmentIds.forEach(treatmentId -> {
                        //构建就诊映射 <treatmentId, MedicalTreatmentBo>
                        MedicalTreatmentBo medicalTreatmentBo = treatmentMap.get(treatmentId);
                        treTreatmentBoMap.put(treatmentId, medicalTreatmentBo);
                        //构建患者映射 <treatmentId, PatientBaseInfoVo>
                        PatientBaseInfoVo patientBaseInfoVo = null;
                        if (medicalTreatmentBo != null) {
                            Integer patientId = medicalTreatmentBo.getPatientId();
                            patientBaseInfoVo = patientMap.get(patientId);
                        }
                        trePatientInfoMap.put(treatmentId, patientBaseInfoVo);
                    });
                    pageBo.setTrePatientInfoMap(trePatientInfoMap);
                    pageBo.setTreTreatmentBoMap(treTreatmentBoMap);
                }
            } else {
                //构建bo
                buildPatientAndTreatment(list, loginUserId, pageBo);
            }
        }
    }

    /**
     * 申请变更带参数查询查询
     *
     * @param list        源审批list
     * @param applyType   变更类型
     * @param keyword     关键字
     * @param loginUserId 登录人
     * @param pageBo      bo
     */
    private List<Integer> buildChangeMapOfKeyword(List<ApprovalRecord> list, Integer applyType, String keyword,
                                                  Integer loginUserId, ApproveChangePageBo pageBo) {
        List<Integer> eventIds = Lists.newArrayList();
        //根据审批查询结果，生成申请类型映射
        Map<Integer, List<ApprovalRecord>> eventMap = list.stream().collect(groupingBy(ApprovalRecord::getApplyType));
        List<ApprovalRecord> approveList = eventMap.get(applyType);
        if (CollectionUtils.isNotEmpty(approveList)) {
            if (ADD.equals(applyType)) {
                //取出新增变类型变更的就诊ids
                List<Integer> treatmentIds = approveList.stream().map(ApprovalRecord::getEventId).collect(toList());
                //查询就诊接口，构建就诊映射 <treatmentId, treatment>
                Map<Integer, MedicalTreatmentBo> treatmentMap = buildTreatmentMap(treatmentIds);
                //取出查询就诊记录中的患者ids
                List<Integer> patientIds = treatmentMap.values().stream().map(MedicalTreatmentBo::getPatientId).collect(toList());
                //调用患者接口服务，查询条件下的所有患者信息
                List<PatientBaseInfoVo> filterPatients = getPatientsByKeyword(patientIds, keyword);
                if (CollectionUtils.isNotEmpty(filterPatients)) {
                    //构建患者映射 <patientId, PatientBaseInfoVo>
                    Map<Integer, PatientBaseInfoVo> patientMap = filterPatients.stream().collect(toMap(PatientBaseInfoVo::getId,
                            Function.identity()));
                    //筛选过滤患者集合对应的就诊映射
                    Map<Integer, MedicalTreatmentBo> treatmentFilterMap = treatmentMap.values().stream().filter(obj -> patientMap.get(obj.getPatientId()) != null)
                            .collect(toMap(MedicalTreatmentBo::getTreatmentId, Function.identity()));
                    //构建患者映射 <treatmentId, PatientBaseInfoVo>
                    Map<Integer, PatientBaseInfoVo> trePatientMap = Maps.newHashMap();
                    treatmentMap.forEach((k, v) -> {
                        trePatientMap.put(k, patientMap.get(v.getPatientId()));
                    });
                    pageBo.setTrePatientInfoMap(trePatientMap);
                    pageBo.setTreTreatmentBoMap(treatmentFilterMap);
                    eventIds.addAll(treatmentFilterMap.keySet());
                }
            } else {
                //查询登录人草稿审批的患者ids映射
                AuditMedicalBo auditMedicalBo = getDraftPatientIdsByApplyType(approveList);
                //根据查询关键字获取电子病例ids集合
                eventIds = getQueryMedicalIdsAndSetBo(auditMedicalBo, loginUserId, keyword, pageBo);
            }
        }
        return eventIds;
    }

    private List<PatientBaseInfoVo> getPatientsByKeyword(List<Integer> patientIds, String keyword) {
        //调用患者接口服务，查询条件下的所有患者信息
        List<PatientBaseInfoVo> filterPatientList = patientFeign.findPatientInfoByIds(patientIds);
        if (CollectionUtils.isNotEmpty(filterPatientList)) {
            //从患者接口中筛选出模糊查询关键字的患者集合
            filterPatientList = filterPatientList.stream().filter(obj ->
                    (!StringUtils.isBlank(obj.getName()) && obj.getName().contains(keyword))
                            || (!StringUtils.isBlank(obj.getMedicalNumber()) && obj.getMedicalNumber().contains(keyword)))
                    .collect(toList());
        }
        return filterPatientList;
    }

    /**
     * 就诊时间是否在当前时间之内
     *
     * @param treatDate treatDate
     * @return bolean
     */
    private boolean checkTreatDateIsBeforeNow(Date treatDate) {
        if (treatDate != null) {
            LocalDate now = LocalDate.now();
            LocalDate treatLocalDate = treatDate.toInstant().atOffset(ZoneOffset.ofHours(8)).toLocalDate();
            return now.isAfter(treatLocalDate);
        }
        return false;
    }

    private RestErrorBo checkTreatmentInfo(Integer treatmentId) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        //查询就诊信息（就诊时间是否在当天）
        TreatmentRecord treatment = treatmentFeign.findTreatmentRecordById(treatmentId);
        if (treatment == null) {
            log.warn("【申请失败】：就诊记录[{}]不存在", treatmentId);
            errorBo.setError(TREATMENT_NOT_EXIST);
            return errorBo;
        }
        if (!checkTreatDateIsBeforeNow(treatment.getTreatStartTime())) {
            log.warn("【申请失败】：电子病历[{}]不需要申请修改，可以直接修改此病历！", treatmentId);
            errorBo.setError(NOT_NEED_APPLY);
            return errorBo;
        }
        return errorBo;
    }

    /**
     * 当新增病例和修改病例时，更新申请变更的更新时间
     * @param id
     */
    private void updateChangeTime(Integer id) {
        ApprovalRecord approvalRecord = new ApprovalRecord();
        approvalRecord.setId(id);
        approvalRecord.setUpdTime(LocalDateTime.now());
        mapper.updateByPrimaryKeySelective(approvalRecord);
    }

    /**
     * 修改医生的申请新增病例的修改时间
     *
     * @param treatId
     */
    public void updateDocApplyChangeTime(Integer treatId) {
        ApprovalRecord treatmentRecord = mapper.findTreatmentRecord(treatId);
        if (treatmentRecord != null) {
            log.info("医生申请变更新增病例，就诊id：{}", treatId);
            this.updateChangeTime(treatmentRecord.getId());
        }
    }
}
