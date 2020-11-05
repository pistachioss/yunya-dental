//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunya.modules.patient_central.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.PatientRecommendRelationChartQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientRecommendRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientRecommendRelationVo;
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

import java.util.List;

/**
 * 简单介绍:</br> 患者推荐 控制层
 *
 * @author: WY
 * @date 2020/7/30 13:22
 * @description: 患者推荐增删改查
 * @since: 1.0.0
 */
@Api(value = "患者推荐", description = "患者推荐（查）")
@RestController
@RequestMapping("recommend")
public class PatientRecommendRelationController {

  /** 注入对象 */
  private final PatientRecommendRelationBiz patientRecommendRelationBiz;

  public PatientRecommendRelationController(
      PatientRecommendRelationBiz patientRecommendRelationBiz) {
    this.patientRecommendRelationBiz = patientRecommendRelationBiz;
  }

  /**
   * 患者推荐列表查询
   *
   * @param patientRecommendRelationQueryForm 患者关系推荐信息查询模型类
   * @return ResponseResult<PageInfo<PatientRecommendRelationVo>>
   */
  @ApiOperation("患者推荐列表查询")
  @PostMapping("/list")
  public ResponseResult<PageInfo<PatientRecommendRelationVo>> findList(
      @RequestBody @Validated PatientRecommendRelationQueryForm patientRecommendRelationQueryForm) {
    return ResponseUtil.success(
        this.patientRecommendRelationBiz.findList(patientRecommendRelationQueryForm));
  }

  /**
   * 查询患者推荐关系拓展图
   *
   * @param form 患者关系推荐图
   * @return ResponseResult<List<PatientRecommendRelationVo>>
   */
  @ApiOperation("查询患者推荐关系拓展图")
  @PostMapping("/findRecommendRelation")
  public ResponseResult<List<PatientRecommendRelationVo>> findRecommendRelationById(
      @RequestBody PatientRecommendRelationChartQueryForm form) {
    return ResponseUtil.success(this.patientRecommendRelationBiz.findRecommendRelationById(form));
  }
}
