package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.DentistArrearsCallForQuery;
import com.yunya.feign.report.domain.query.DentistArrearsDetailQuery;
import com.yunya.feign.report.domain.query.PatientArrearsCallForQuery;
import com.yunya.feign.report.domain.vo.DentistArrearsCallForVO;
import com.yunya.feign.report.domain.vo.DentistArrearsDetailVO;
import com.yunya.feign.report.domain.vo.PatientArrearsCallForVO;
import com.yunya.feign.report.domain.vo.PatientArrearsDetailVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.BaseBillBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简介: 公司端客服中心报表控制器
 *
 * @author: chow
 * @date: 2020/12/10 09:24
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端-客服中心报表")
@RestController
@RequestMapping("central")
public class CompanyReportOfCustomerCentralController {

  /** 账单 */
  @Autowired private BaseBillBiz billBiz;

  /**
   * 根据条件查询患者催缴欠费列表
   *
   * @param query 查询条件
   * @return PageInfo<PatientArrearsCallForVO> pageInfo
   */
  @ApiOperation("公司端报表-客服中心报表-患者催缴欠费")
  @PostMapping(value = "/arrears/list", name = "公司端报表-客服中心报表-患者催缴欠费")
  public ResponseResult<PageInfo<PatientArrearsCallForVO>> patientArrearsList(
      @RequestBody @Validated PatientArrearsCallForQuery query) {
    PageInfo<PatientArrearsCallForVO> pageInfo = billBiz.findPatientArrearsList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出患者催缴欠费列表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-客服中心报表-患者催缴欠费-导出")
  @PostMapping(value = "/arrears/list/export", name = "根据条件导出患者催缴欠费列表")
  public ResponseResult<T> exportPatientArrearsList(
      HttpServletResponse response, @RequestBody @Validated PatientArrearsCallForQuery query)
      throws IOException {
    billBiz.exportPatientArrearsList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据患者ID查询患者欠款明细列表
   *
   * @param patientId 患者ID
   * @return List<PatientArrearsDetailVO>
   */
  @ApiOperation("公司端报表-客服中心报表-患者催缴欠费-查看明细")
  @GetMapping(value = "/arrears/detail/list/{patientId}", name = "公司端报表-客服中心报表-患者催缴欠费-查看明细")
  public ResponseResult<List<PatientArrearsDetailVO>> patientArrearsDetailList(
      @PathVariable(value = "patientId") Integer patientId) {
    List<PatientArrearsDetailVO> resultList = billBiz.findPatientArrearsDetailList(patientId);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件查询医生催缴欠费列表
   *
   * @param query 查询条件
   * @return PageInfo<DentistArrearsCallForVO>
   */
  @ApiOperation("公司端报表-客服中心报表-医生催缴欠费")
  @PostMapping(value = "/dentist/arrears/list", name = "根据条件查询医生催缴欠费列表")
  public ResponseResult<PageInfo<DentistArrearsCallForVO>> dentistArrearsList(
      @RequestBody DentistArrearsCallForQuery query) {
    PageInfo<DentistArrearsCallForVO> pageInfo = billBiz.findDentistArrearsList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询医生所属欠费明细列表
   *
   * @param query 查询条件
   * @return PageInfo<DentistArrearsDetailVO>
   */
  @ApiOperation("公司端报表-客服中心报表-医生催缴欠费-欠费明细列表")
  @PostMapping(value = "/dentist/arrears/detail/list", name = "根据条件查询医生所属欠费明细列表")
  public ResponseResult<PageInfo<DentistArrearsDetailVO>> dentistArrearsDetailList(
      @RequestBody @Validated DentistArrearsDetailQuery query) {
    PageInfo<DentistArrearsDetailVO> pageInfo = billBiz.findDentistArrearsDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出医生所属欠费明细列表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-客服中心报表-医生催缴欠费-欠费明细列表-导出")
  @PostMapping(value = "/dentist/arrears/detail/export", name = "根据条件导出医生所属欠费明细列表")
  public ResponseResult<T> exportDentistArrearsDetailList(
          HttpServletResponse response, @RequestBody @Validated DentistArrearsDetailQuery query)
          throws IOException {
    billBiz.exportDentistArrearsDetailList(response, query);
    return ResponseUtil.success(null);
  }
}
