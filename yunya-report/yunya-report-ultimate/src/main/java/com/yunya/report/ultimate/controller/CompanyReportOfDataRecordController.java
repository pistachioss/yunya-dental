package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.TreatmentRecordQuery;
import com.yunya.feign.report.domain.vo.TreatmentRecordReportVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.BaseTreatmentProcessBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 简介: 公司端报表-数据统计控制器
 *
 * @author: chow
 * @date: 2020/10/26 14:03
 * @description:
 * @since: 1.0.0
 */
@Api("数据统计（就诊记录；账单记录；配诊记录）")
@RestController
@RequestMapping("record")
public class CompanyReportOfDataRecordController {

  @Autowired private BaseTreatmentProcessBiz treatmentProcessBiz;

  /**
   * 根据条件查询就诊记录列表
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("根据条件查询就诊记录列表")
  @PostMapping(value = "/treatment/list", name = "根据条件查询就诊记录")
  public ResponseResult<PageInfo<TreatmentRecordReportVO>> findTreatmentList(
      @RequestBody TreatmentRecordQuery query) {
    PageInfo<TreatmentRecordReportVO> resultList = treatmentProcessBiz.findTreatmentList(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件查询就诊列表并导出Excel
   *
   * @param response 响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("根据条件查询就诊列表并导出Excel(请设置分页条件为false)")
  @PostMapping(value = "/treatment/export", name = "根据条件查询就诊列表并导出Excel")
  public ResponseResult<T> exportTreatmentList(
      HttpServletResponse response, @RequestBody TreatmentRecordQuery query) throws IOException {
    treatmentProcessBiz.exportTreatmentList(response, query);
    return ResponseUtil.success(null);
  }
}
