package com.yunya.modules.emr.controller;

import com.yunya.feign.emr.domain.form.*;
import com.yunya.feign.emr.domain.model.*;
import com.yunya.framework.common.annation.*;
import com.yunya.framework.common.model.*;
import com.yunya.framework.common.utils.*;
import com.yunya.modules.emr.biz.*;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.*;
import javax.validation.*;

/**
 * @author xiangyang
 * @date 2020/7/30
 */
@Api(tags = {"病例审批管理"})
@RestController
public class MedicalApprovalController {

    @Resource
    private MedicalApprovalBiz approvalBiz;

    @ApiOperation(value = "申请新增草稿病例")
    @PostMapping("medical/draft/add/apply")
    @CurrentUser
    public ResponseResult applyAddDraft(@Valid @RequestBody DraftMedicalApplyModel draftModel) {
        approvalBiz.applyAddDraftCase(draftModel);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "申请修改草稿病例")
    @PostMapping("medical/draft/update/apply")
    @CurrentUser
    public ResponseResult applyUpdateDraftCase(@Valid @RequestBody DraftMedicalApplyModel draftModel) {
        approvalBiz.applyUpdateDraftCase(draftModel);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "草稿病例审批通过")
    @PutMapping("medical/draft/pass/{id}")
    @CurrentUser
    public ResponseResult passDraft(@PathVariable(value = "id") Integer approveId,
                                    @Valid @RequestBody MedicalApprovePassForm passForm) {
        approvalBiz.passMedical(approveId, passForm);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "草稿病例审批拒绝")
    @PutMapping("medical/draft/reject/{id}")
    @CurrentUser
    public ResponseResult passDraft(@PathVariable(value = "id") Integer approveId,
                                    @Valid @RequestBody MedicalApproveRejectForm rejectForm) {
        approvalBiz.rejectMedical(approveId, rejectForm);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "申请新增病例变更")
    @PostMapping("medical/change/add/apply")
    @CurrentUser
    public ResponseResult applyAddDraft(@Valid @RequestBody ChangeMedicalApplyModel draftModel) {
        approvalBiz.applyAddChangeCase(draftModel);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "申请修改病例变更")
    @PostMapping("medical/change/update/apply")
    @CurrentUser
    public ResponseResult applyUpdateDraftCase(@Valid @RequestBody ChangeMedicalApplyModel draftModel) {
        approvalBiz.applyUpdateChangeCase(draftModel);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "申请新增病例变更通过")
    @PutMapping("medical/change/pass/{id}")
    @CurrentUser
    public ResponseResult passChange(@PathVariable(value = "id") Integer approveId,
                                     @Valid @RequestBody ChangeApprovePassForm passForm) {
        approvalBiz.passChange(approveId, passForm);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "申请修改病例变更拒绝")
    @PutMapping("medical/change/reject/{id}")
    @CurrentUser
    public ResponseResult passChange(@PathVariable(value = "id") Integer approveId,
                                     @Valid @RequestBody ChangeApproveRejectForm rejectForm) {
        approvalBiz.rejectChange(approveId, rejectForm);
        return ResponseUtil.success();
    }

    @ApiOperation("草稿病例分页查询")
    @PostMapping("medical/draft/page")
    public ResponseResult getDraftAuditPage() {
        return null;
    }
}
