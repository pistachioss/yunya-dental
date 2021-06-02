package com.yunya.report.ultimate.controller;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.PatientSearchQuery;
import com.yunya.feign.report.domain.query.PatientManageQuery;
import com.yunya.feign.report.domain.vo.CouponActiveVo;
import com.yunya.feign.report.domain.vo.PatientDataVo;
import com.yunya.feign.report.domain.vo.PatientInfoVO;
import com.yunya.feign.report.domain.vo.PatientManageVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.PatientBaseInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2020/11/24 13:29
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端-患者资料-患者预约信息")
@RestController
@RequestMapping("patient")
public class PatientBaseInfoController {

  /** 注入服务 */
  @Autowired private PatientBaseInfoBiz patientBaseInfoBiz;

  /**
   * 根据关键字搜索患者信息
   *
   * @param query 查询条件
   * @return 患者信息
   */
  @ApiOperation("根据条件搜索患者信息")
  @PostMapping(value = "/full/search", name = "根据条件搜索患者信息")
  public ResponseResult<PageInfo<PatientInfoVO>> patientSearch(
      @RequestBody PatientSearchQuery query) {
    PageInfo<PatientInfoVO> patientInfo = patientBaseInfoBiz.findPatientInfoByExample(query);
    return ResponseUtil.success(patientInfo);
  }

  /**
   * 查询患者资料信息
   *
   * @param patientId 患者id
   * @return 患者资料信息
   */
  @ApiOperation("查询患者资料就诊信息")
  @GetMapping("/patientInfo/{patientId}")
  public ResponseResult<PatientDataVo> patientInfo(@PathVariable("patientId") Integer patientId) {
    PatientDataVo patientDataVo = patientBaseInfoBiz.patientDataVo(patientId);
    return ResponseUtil.success(patientDataVo);
  }

  @ApiOperation("客服中心-患者管理")
  @PostMapping("/manage/page")
  public ResponseResult<PageInfo<PatientManageVo>> patientInfo(@RequestBody PatientManageQuery query) {
    PageInfo<PatientManageVo> page = patientBaseInfoBiz.getPatientManagePage(query);
    return ResponseUtil.success(page);
  }

  @ApiOperation(value = "客服中心-患者管理-导出")
  @PostMapping("/manage/page/export")
  public void exportCouponActivation(HttpServletResponse response, @RequestBody PatientManageQuery query) throws IOException {
    patientBaseInfoBiz.buildResponse(response, "患者报表");
    EasyExcel.write(response.getOutputStream(), CouponActiveVo.class)
            .sheet("sheet").doWrite(patientBaseInfoBiz.listPatientManage(query));

  }

}
