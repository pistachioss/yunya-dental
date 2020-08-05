package com.yunya.modules.emr.controller;

import com.yunya.feign.emr.domain.form.MedicalApprovePassForm;
import com.yunya.feign.emr.domain.form.MedicalApproveRejectForm;
import com.yunya.feign.emr.domain.model.DraftMedicalApplyModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.emr.biz.MedicalApprovalBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author xiangyang
 * @date 2020/7/30
 */
@Api(tags = {"病例审批管理"})
@RestController
public class MedicalApprovalController {

    @Resource
    private MedicalApprovalBiz approvalBiz;

    @ApiOperation(value = "草稿病例申请")
    @PostMapping("medical/draft/apply")
    @CurrentUser
    public ResponseResult applyDraft(@Valid @RequestBody DraftMedicalApplyModel draftModel) {
        approvalBiz.applyDraftCase(draftModel);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "草稿病例审批通过")
    @PutMapping("medical/draft/pass/{approveId}")
    @CurrentUser
    public ResponseResult passDraft(@PathVariable("approveId") Integer approveId,
                                    @Valid @RequestBody MedicalApprovePassForm passForm) {
        approvalBiz.passMedical(approveId, passForm);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "草稿病例审批拒绝")
    @PutMapping("medical/draft/reject/{approveId}")
    @CurrentUser
    public ResponseResult passDraft(@PathVariable("approveId") Integer approveId,
                                    @Valid @RequestBody MedicalApproveRejectForm rejectForm) {
        approvalBiz.rejectMedical(approveId, rejectForm);
        return ResponseUtil.success();
    }
}
