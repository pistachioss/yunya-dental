package com.yunya.modules.emr.biz;

import com.yunya.feign.emr.domain.form.ApproveRejectForm;
import com.yunya.feign.emr.domain.form.MedicalApprovePassForm;
import com.yunya.feign.emr.domain.form.MedicalApproveRejectForm;
import com.yunya.feign.emr.domain.model.DraftMedicalApplyModel;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.emr.ApprovalRecord;
import com.yunya.modules.emr.enums.ApplyTypeEnum;
import com.yunya.modules.emr.enums.ApproveStatusEnum;
import com.yunya.modules.emr.enums.EventTypeEnum;
import com.yunya.modules.emr.mapper.ApprovalRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author xiangyang
 * @date 2020/7/31
 */
@Service
public class MedicalApprovalBiz extends BaseBiz<ApprovalRecordMapper, ApprovalRecord> {

    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;

    /**
     * 草稿病例申请
     *
     * @param draftModel
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyDraftCase(DraftMedicalApplyModel draftModel) {
        int pendCount = 0;
        Integer applyType = draftModel.getApplyType();
        Integer eventId = draftModel.getEventId();
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        //查询用户岗位信息
        SysUserInfoDetail employee = systemServiceFeign.findSysUserEmployeeInfoByUserId(loginUserId);
        //TODO 判断登录用户是否拥有助手权限
        //todo 调用电子病历详情接口
        if (ApplyTypeEnum.ADD.getCode().equals(applyType)) {
            //查询该病历的审批情况
            pendCount = mapper.countByEventIdAndType(eventId, null, null);
            if (pendCount > 0) {
                throw new ClientServiceException("该病例正在审批中，请勿重复申请", OperationCodeConstants.APPLY_APPROVE_PENDING);
            }
            //新增一条草稿病历新增审批
            constructCreateEntity(draftModel, loginUserId);
        }
        if (ApplyTypeEnum.UPDATE.getCode().equals(applyType)) {
            //查询该病历的待审批情况
            pendCount = mapper.countByEventIdAndType(eventId, EventTypeEnum.DRAFT_AUDIT.getCode(), ApproveStatusEnum.APPROVE_PENDING.getCode());
            if (pendCount > 0) {
                throw new ClientServiceException("该病例正在审批中，请勿重复申请", OperationCodeConstants.APPLY_APPROVE_PENDING);
            }
            ApprovalRecord newestDraft = mapper.findNewestReject(eventId);
            if (newestDraft == null) {
                throw new ClientServiceException("病例不存在", OperationCodeConstants.DATA_ERROR);
            }
            //校验审批时间是否已经超过24h
            LocalDateTime approveTime = newestDraft.getApproveTime();
            LocalDateTime now = LocalDateTime.now();
            long hourGap = Duration.between(approveTime, now).toHours();
            if (hourGap > BusinessConstants.HOUR_GAP) {
                throw new ClientServiceException("审核时间已超过24小时", OperationCodeConstants.DATA_ERROR);
            }
            //新增一条草稿病历修改审批
            constructCreateEntity(draftModel, loginUserId);
        }
    }

    public void pass(Integer approveId, MedicalApprovePassForm passForm) {
        int pendCount = 0;
        Integer eventId = passForm.getPassForm().getEventId();
        //查询该病历的审批情况
        pendCount = mapper.countByEventIdAndType(eventId, EventTypeEnum.DRAFT_AUDIT.getCode(), ApproveStatusEnum.APPROVE_PENDING.getCode());
        if (pendCount == 0) {
            throw new ClientServiceException("该病例不存在", OperationCodeConstants.DATA_ERROR);
        }
        //更新审批记录信息
        ApprovalRecord passEntity = new ApprovalRecord();
        passEntity.setStatus(Integer.valueOf(ApproveStatusEnum.AUDIT_PASS.getCode()));
        updateMedicalApprove(approveId, passEntity);
        //todo 更新电子病历信息
        //todo 插入电子病历历史记录
    }

    public void reject(Integer approveId, MedicalApproveRejectForm rejectForm) {
        int pendCount = 0;
        ApproveRejectForm appRejectForm = rejectForm.getRejectForm();
        //查询该病历的审批情况
        pendCount = mapper.countByEventIdAndType(appRejectForm.getEventId(), EventTypeEnum.DRAFT_AUDIT.getCode(), ApproveStatusEnum.APPROVE_PENDING.getCode());
        if (pendCount == 0) {
            throw new ClientServiceException("该病例不存在", OperationCodeConstants.DATA_ERROR);
        }
        //更新审批记录信息
        ApprovalRecord rejectEntity = new ApprovalRecord();
        rejectEntity.setApproveReason(appRejectForm.getRejectReason());
        rejectEntity.setStatus(Integer.valueOf(ApproveStatusEnum.AUDIT_REJECT.getCode()));
        updateMedicalApprove(approveId, rejectEntity);
        //todo 更新电子病历信息
    }

//    public void apply

    private void updateMedicalApprove(Integer approveId, ApprovalRecord record) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        LocalDateTime now = LocalDateTime.now();
        record.setApproverId(loginUserId);
        record.setApproveTime(now);
        record.setCrtId(loginUserId);
        record.setUpdId(loginUserId);
        record.setId(approveId);
        mapper.updateByPrimaryKeySelective(record);
    }

    private ApprovalRecord constructCreateEntity (DraftMedicalApplyModel draftModel, Integer loginUserId) {
        ApprovalRecord addApplyEntity = EntityUtils.build(draftModel, ApprovalRecord.class);
        addApplyEntity.setStatus(ApproveStatusEnum.APPROVE_PENDING.getCode());
        addApplyEntity.setCrtId(loginUserId);
        addApplyEntity.setUpdId(loginUserId);
        mapper.insertSelective(addApplyEntity);
        return addApplyEntity;
    }

}
