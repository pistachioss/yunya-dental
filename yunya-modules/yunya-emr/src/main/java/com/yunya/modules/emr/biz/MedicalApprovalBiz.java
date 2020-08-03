package com.yunya.modules.emr.biz;

import com.yunya.feign.emr.domain.model.DraftMedicalApplyModel;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.emr.ApprovalRecord;
import com.yunya.modules.emr.enums.ApplyTypeEnum;
import com.yunya.modules.emr.enums.ApproveStatusEnum;
import com.yunya.modules.emr.mapper.ApprovalRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
     * @param draftModel
     */
    public void applyDraftCase(DraftMedicalApplyModel draftModel) {
        Integer applyType = draftModel.getApplyType();
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        SysUserInfoDetail employee = systemServiceFeign.findSysUserEmployeeInfoByUserId(loginUserId);
        //TODO 判断登录用户是否拥有助手权限
        if (ApplyTypeEnum.ADD.getCode().equals(applyType)) {
            int pendCount = mapper.countByEventIdAndType(draftModel.getEventId(), null, null);
            if (pendCount > 0) {
                throw new ClientServiceException("该病例正在审批中，请勿重复申请", OperationCodeConstants.APPLY_APPROVE_PENDING);
            }
            ApprovalRecord addApplyEntity = EntityUtils.build(draftModel, ApprovalRecord.class);
            addApplyEntity.setStatus(ApproveStatusEnum.APPROVE_PENDING.getCode());
            addApplyEntity.setCrtId(loginUserId);
            addApplyEntity.setUpdId(loginUserId);
            mapper.insertSelective(addApplyEntity);
        } else {

        }
    }
}
