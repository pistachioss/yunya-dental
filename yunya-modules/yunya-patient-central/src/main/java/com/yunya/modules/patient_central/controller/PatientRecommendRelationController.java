//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.query.PatientRecommendRelationChartQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientRecommendRelationQueryForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientRecommendRelationBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(
        value = "患者推荐",
        description = "患者推荐（查）"
)
@RestController
@RequestMapping({"recommend"})
public class PatientRecommendRelationController {
    private PatientRecommendRelationBiz patientRecommendRelationBiz;

    public PatientRecommendRelationController(PatientRecommendRelationBiz patientRecommendRelationBiz) {
        this.patientRecommendRelationBiz = patientRecommendRelationBiz;
    }

    @ApiOperation("患者推荐列表查询")
    @PostMapping({"/list"})
    public ResponseResult findList(@RequestBody @Validated PatientRecommendRelationQueryForm patientRecommendRelationQueryForm) {
        return ResponseUtil.success(this.patientRecommendRelationBiz.findList(patientRecommendRelationQueryForm));
    }

    @ApiOperation("查询患者推荐关系拓展图")
    @PostMapping({"/findRecommendRelation"})
    public ResponseResult findRecommendRelationById(@RequestBody PatientRecommendRelationChartQueryForm form) {
        return ResponseUtil.success(this.patientRecommendRelationBiz.findRecommendRelationById(form));
    }
}
