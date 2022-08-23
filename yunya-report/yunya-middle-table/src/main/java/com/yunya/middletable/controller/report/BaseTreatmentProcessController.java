package com.yunya.middletable.controller.report;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.*;
import com.yunya.models.report.StatEmpPrivilege;
import com.yunya.models.report.StatEmpRefund;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 中间表就诊流程控制器
 *
 * @author: chow
 * @date: 2020/10/17 12:49
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("treatment_process")
public class BaseTreatmentProcessController {

  @Autowired private BaseTreatmentProcessBiz treatmentProcessBiz;
  @Autowired private StatEmpTreatBiz statEmpTreatBiz;
  @Autowired private StatEmpBillBiz statEmpBillBiz;
  @Autowired private StatEmpPayBiz statEmpPayBiz;
  @Autowired private StatEmpPrivilegeBiz statEmpPrivilegeBiz;
  @Autowired private StatEmpRefundBiz statEmpRefundBiz;

  /**
   * 根据消息操作中间表就诊流程
   *
   * @param msg 消息
   * @return
   */
  @ApiOperation("根据消息操作中间表就诊流程")
  @PostMapping(value = "/operate", name = "根据消息操作中间表就诊流程")
  public ResponseResult<T> operateTreatmentProcess(@RequestBody @Validated MessageModel msg) {
    log.info("根据消息操作中间表就诊流程========> {}", msg);
    treatmentProcessBiz.operateTreatmentProcess(msg);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件拉取就诊流程数据并更新中间表
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表就诊流程")
  @PostMapping(value = "/operate/batch", name = "form")
  public ResponseResult<T> pullTreatmentProcessData(@RequestBody PullForm form)
      throws InterruptedException {
    treatmentProcessBiz.pullTreatmentProcessData(form);
    return ResponseUtil.success(null);
  }

  /**
   * 拉取就诊完成的数据并更新中间表base_treatment_process.in_month_next_id
   *
   * @return
   */
  @ApiOperation("拉取就诊完成的数据并更新中间表base_treatment_process.in_month_next_id")
  @PostMapping(value = "/operate/inMonthNextId", name = "form")
  public ResponseResult<T> pullUpdateInMonthNextId()
      throws Exception {
    treatmentProcessBiz.pullUpdateInMonthNextId();
    return ResponseUtil.success(null);
  }

  /**
   * 根据消息操作中间表就诊完成时统计
   *
   * @param msg 消息
   * @return
   */
  @ApiOperation("根据消息操作中间表就诊完成时统计")
  @PostMapping(value = "/treatDate/statistics", name = "根据消息操作中间表就诊完成时统计")
  public ResponseResult<T> treatDateStatistics(@RequestBody @Validated MessageModel msg) {
    log.info("根据消息操作中间表就诊完成时统计========> {}", msg);
    msg.setOperateType(1);
    treatmentProcessBiz.operateTreatmentProcess(msg);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件拉取就诊完成时统计并更新中间表
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表就诊完成时统计")
  @PostMapping(value = "/treatDate/statistics/batch", name = "form")
  public ResponseResult<T> pullTreatDateStatistics(@RequestBody PullForm form)
          throws InterruptedException {
    statEmpTreatBiz.pullTreatDateStatistics(form);
    return ResponseUtil.success(null);
  }

  /**
   * 根据时间段批量操作中间表一键统计：
   *  就诊完成时统计就诊、
   *  账单生成时统计账单、
   *  账单收费时统计收费、
   *  账单优惠时统计优惠、
   *  账单退费时统计退费
   *
   * @param form
   * @return
   * @throws Exception
   */
  @ApiOperation("根据时间段批量操作中间表一键统计：就诊完成时统计就诊、账单生成时统计账单、账单收费时统计收费、账单优惠时统计优惠、账单退费时统计退费")
  @PostMapping(value = "/dateRange/statistics/batch", name = "form")
  public ResponseResult pullDateRangeStatistics(@RequestBody PullForm form) throws Exception {
    statEmpTreatBiz.pullTreatDateStatistics(form);
    statEmpBillBiz.pullBillDateStatistics(form);
    statEmpPayBiz.pullPayDateStatistics(form);
    statEmpPrivilegeBiz.pullPrivilegeDateStatistics(form);
    statEmpRefundBiz.pullRefundDateStatistics(form);
    return ResponseUtil.success(null);
  }
}
