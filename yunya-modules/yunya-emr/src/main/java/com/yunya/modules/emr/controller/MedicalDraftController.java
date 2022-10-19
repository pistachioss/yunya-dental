package com.yunya.modules.emr.controller;

import com.yunya.feign.emr.domain.model.MedicalCommonRecordModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.emr.biz.MedicalCommonRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @description:
 * @author: xy
 * @date 2022/10/19 13:40
 **/
@Api(tags = {"病例临时草稿"})
@RestController
public class MedicalDraftController {
    @Resource
    private MedicalCommonRecordBiz medicalCommonRecordBiz;

    @ApiOperation("保存临时病例")
    @PostMapping("/medical/temp/draft")
    @CurrentUser
    public ResponseResult<Boolean> draftTemp( @Valid @RequestBody MedicalCommonRecordModel model) {
        medicalCommonRecordBiz.draft(model);
        return ResponseUtil.success();
    }

    @ApiOperation("临时病例详情")
    @GetMapping("/medical/temp/draft")
    @CurrentUser
    public ResponseResult<MedicalCommonRecordModel> draftTempDetail(@RequestParam Integer treatmentId) {
        return ResponseUtil.success(medicalCommonRecordBiz.draftDetail(treatmentId));
    }

    @ApiOperation("移除临时病例")
    @DeleteMapping("/medical/temp/draft")
    @CurrentUser
    public ResponseResult<Boolean> removeDraftTemp(@RequestParam Integer treatmentId) {
        medicalCommonRecordBiz.removeDraftTemp(treatmentId);
        return ResponseUtil.success();
    }
}
