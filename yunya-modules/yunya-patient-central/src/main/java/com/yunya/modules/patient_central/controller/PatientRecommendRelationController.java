package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.query.PatientRecommendRelationChartQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientRecommendRelationQueryForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientRecommendRelationBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简单介绍:</br> 患者推荐控制器
 *
 * @author: WY
 * @date 2020/7/29 13:22
 * @description: 患者推荐信息管理
 * @since: 1.0.0
 */
@Api(value = "患者推荐",description = "患者推荐（查）")
@RestController
@RequestMapping("Recommend")
public class PatientRecommendRelationController {

    private PatientRecommendRelationBiz patientRecommendRelationBiz;

    public PatientRecommendRelationController(PatientRecommendRelationBiz patientRecommendRelationBiz) {
        this.patientRecommendRelationBiz = patientRecommendRelationBiz;
    }

    @ApiOperation("患者推荐列表查询")
    @GetMapping("/list")
    public ResponseResult findList(@RequestBody @Validated  PatientRecommendRelationQueryForm patientRecommendRelationQueryForm){
        return ResponseUtil.success(patientRecommendRelationBiz.findList(patientRecommendRelationQueryForm));
    }

    @ApiOperation("查询患者推荐关系拓展图")
    @GetMapping("/findRecommendRelation")
    public ResponseResult findRecommendRelationById(@RequestBody PatientRecommendRelationChartQueryForm form){
        return ResponseUtil.success(patientRecommendRelationBiz.findRecommendRelationById(form));
    }


}
