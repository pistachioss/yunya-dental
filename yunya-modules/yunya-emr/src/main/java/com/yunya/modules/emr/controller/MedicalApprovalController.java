package com.yunya.modules.emr.controller;

import com.github.pagehelper.*;
import com.yunya.feign.emr.domain.form.*;
import com.yunya.feign.emr.domain.model.*;
import com.yunya.feign.emr.domain.query.*;
import com.yunya.feign.emr.domain.vo.*;
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

    @ApiOperation(value = "申请-新增草稿病例")
    @PostMapping("medical/draft/add/apply")
    @CurrentUser
    public ResponseResult applyAddDraft(@Valid @RequestBody DraftMedicalApplyModel draftModel) {
        return approvalBiz.applyAddDraftCase(draftModel);
    }

    @ApiOperation(value = "申请-修改草稿病例")
    @PostMapping("medical/draft/update/apply")
    @CurrentUser
    public ResponseResult applyUpdateDraftCase(@Valid @RequestBody DraftMedicalApplyModel draftModel) {
        return approvalBiz.applyUpdateDraftCase(draftModel);
    }

    @ApiOperation(value = "通过-草稿病例审批")
    @PutMapping("medical/draft/pass/{id}")
    @CurrentUser
    public ResponseResult passDraft(@PathVariable(value = "id") Integer approveId,
                                    @Valid @RequestBody MedicalApprovePassForm passForm) {
        return approvalBiz.passMedical(approveId, passForm);
    }

    @ApiOperation(value = "拒绝-草稿病例审批")
    @PutMapping("medical/draft/reject/{id}")
    @CurrentUser
    public ResponseResult passDraft(@PathVariable(value = "id") Integer approveId,
                                    @Valid @RequestBody MedicalApproveRejectForm rejectForm) {
        return approvalBiz.rejectMedical(approveId, rejectForm);
    }

    @ApiOperation(value = "申请-新增病例变更")
    @PostMapping("medical/change/add/apply")
    @CurrentUser
    public ResponseResult applyAddDraft(@Valid @RequestBody ChangeMedicalApplyModel draftModel) {
        return approvalBiz.applyAddChangeCase(draftModel);
    }

    @ApiOperation(value = "申请-修改病例变更")
    @PostMapping("medical/change/update/apply")
    @CurrentUser
    public ResponseResult applyUpdateDraftCase(@Valid @RequestBody ChangeMedicalApplyModel draftModel) {
        return approvalBiz.applyUpdateChangeCase(draftModel);
    }

    @ApiOperation(value = "通过-病例变更审批")
    @PutMapping("medical/change/pass/{id}")
    @CurrentUser
    public ResponseResult passChange(@PathVariable(value = "id") Integer approveId,
                                     @Valid @RequestBody ChangeApprovePassForm passForm) {
        return approvalBiz.passChange(approveId, passForm);
    }

    @ApiOperation(value = "拒绝-病例变更审批")
    @PutMapping("medical/change/reject/{id}")
    @CurrentUser
    public ResponseResult passChange(@PathVariable(value = "id") Integer approveId,
                                     @Valid @RequestBody ChangeApproveRejectForm rejectForm) {
        return approvalBiz.rejectChange(approveId, rejectForm);
    }

    @ApiOperation("草稿病例分页查询")
    @PostMapping("medical/draft/apply/page")
    @CurrentUser
    public ResponseResult<PageInfo<MedicalApplyPageVo>> getDraftApplyPage(@Valid @RequestBody MedicalApproveQuery query) {
        PageInfo<MedicalApplyPageVo> page = approvalBiz.getDraftApplyPage(query);
        return ResponseUtil.success(page);
    }

    @ApiOperation("病历审核分页查询")
    @PostMapping("medical/draft/audit/page")
    @CurrentUser
    public ResponseResult<PageInfo<MedicalApprovePageVo>> getDraftAuditPage(@Valid @RequestBody MedicalApproveQuery query) {
        PageInfo<MedicalApprovePageVo> page = approvalBiz.getDraftApprovePage(query);
        return ResponseUtil.success(page);
    }

    @ApiOperation("病历变更分页查询")
    @PostMapping("medical/change/apply/page")
    @CurrentUser
    public ResponseResult<PageInfo<MedicalChangeApplyPageVo>> getDraftApplyPage(@Valid @RequestBody ChangeApproveQuery query) {
        PageInfo<MedicalChangeApplyPageVo> page = approvalBiz.getChangeApplyPage(query);
        return ResponseUtil.success(page);
    }

    @ApiOperation("公司端-病历变更审核分页查询")
    @PostMapping("medical/change/audit/page")
    @CurrentUser
    public ResponseResult<PageInfo<MedicalChangeApprovePageVo>> getDraftAuditPage(@Valid @RequestBody ChangeApproveQuery query) {
        PageInfo<MedicalChangeApprovePageVo> page = approvalBiz.getChangeApprovePage(query);
        return ResponseUtil.success(page);
    }
}
