package com.yunya.modules.treatment.controller.app;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.query.AppTreatListQuery;
import com.yunya.feign.treatment.domain.query.AppTreatmentQuery;
import com.yunya.feign.treatment.domain.query.TreatmentInfoForMonthForm;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentInfo4ListVO;
import com.yunya.feign.treatment.domain.vo.TreatmentInfo4AppVO;
import com.yunya.feign.treatment.domain.vo.TreatmentInfoForMonthVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.TreatmentProcess4AppBiz;
import com.yunya.modules.treatment.biz.TreatmentRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
  @Autowired
  private TreatmentProcess4AppBiz treatmentBiz4App;

  @ApiOperation("根据条件查询APP端患者就诊列表(开完成,可联调...)")
  @PostMapping(value = "/list", name = "根据条件查询APP端患者就诊列表")
  public ResponseResult<PageInfo<PatientTreatmentInfo4ListVO>> treatList(
      @RequestBody @Validated AppTreatListQuery query) {
    PageInfo<PatientTreatmentInfo4ListVO> resultList = treatmentRecordBiz.findAppTreatList(query);
    return ResponseUtil.success(resultList);
  }


  @ApiOperation("查询指定时间段内每个医生每天患者就诊人数")
  @PostMapping(value = "/everyday/count")
  @CurrentUser
  public ResponseResult<List<TreatmentInfoForMonthVO>> treatInfoForMonth(@RequestBody @Validated TreatmentInfoForMonthForm form) {
    List<TreatmentInfoForMonthVO> treatmentInfoForMonthVOS = treatmentRecordBiz.treatInfoForMonth(form);
    return ResponseUtil.success(treatmentInfoForMonthVOS);
  }

  @ApiOperation("查询患者就诊信息")
  @PostMapping("/info/detail/all")
  public ResponseResult<TreatmentInfo4AppVO> treatmentInfoDetail(@RequestBody AppTreatmentQuery query) {
    TreatmentInfo4AppVO result = this.treatmentBiz4App.treatmentInfoDetail(query);
    return ResponseUtil.success(result);
  }

}
