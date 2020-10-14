package com.yunya.modules.treatment.controller.app;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.query.AppTreatListQuery;
import com.yunya.feign.treatment.domain.vo.AppPatientTreatmentInfoVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.TreatmentRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: APP端患者就诊控制器
 *
 * @author: chow
 * @date: 2020/10/10 09:11
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "APP端患者就诊（列表查询、就诊详情查询）")
@RestController
@RequestMapping("treat")
public class PatientTreatController {

  /** 注入对象 */
  @Autowired private TreatmentRecordBiz treatmentRecordBiz;

  @ApiOperation("根据条件查询APP端患者就诊列表")
  @ApiImplicitParams({@ApiImplicitParam(name = "query", value = "app端患者就诊列表查询", required = true)})
  @PostMapping(value = "/list", name = "根据条件查询APP端患者就诊列表")
  public ResponseResult<PageInfo<AppPatientTreatmentInfoVO>> treatList(
      @RequestBody @Validated AppTreatListQuery query) {
    PageInfo<AppPatientTreatmentInfoVO> resultList = treatmentRecordBiz.findAppTreatList(query);
    return ResponseUtil.success(resultList);
  }
}
