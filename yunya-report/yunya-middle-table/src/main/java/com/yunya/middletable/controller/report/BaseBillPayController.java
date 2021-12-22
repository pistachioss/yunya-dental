package com.yunya.middletable.controller.report;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseBillPayBiz;
import com.yunya.middletable.service.StatEmpPayBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 中间表账单同步
 *
 * @author: chow
 * @date: 2020/12/11 20:30
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("pay")
public class BaseBillPayController {

  /** 账单收费记录 */
  @Autowired private BaseBillPayBiz billPayBiz;
  /** 账单收费记录 */
  @Autowired private StatEmpPayBiz statEmpPayBiz;

  /**
   * 根据消息操作中间表账单收费记录
   *
   * @param msg 消息
   * @return
   */
  @ApiOperation("根据消息操作中间表账单收费记录")
  @PostMapping(value = "/operate", name = "根据消息操作中间表账单收费记录")
  public ResponseResult<T> operateBillPay(@RequestBody @Validated MessageModel msg) {
    billPayBiz.operateBillPay(msg);
    return ResponseUtil.success(null);
  }

  /**
   * 根据时间段批量操作中间表账单收费记录
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表账单收费记录")
  @PostMapping(value = "/operate/batch", name = "根据时间段批量操作中间表账单收费记录")
  public ResponseResult<T> pullBillPayData(@RequestBody PullForm form) throws InterruptedException {
    billPayBiz.pullBillPayData(form);
    return ResponseUtil.success(null);
  }

  /**
   * 根据消息操作中间表收费时统计
   *
   * @param msg 消息
   * @return
   */
  @ApiOperation("根据消息操作中间表收费时统计")
  @PostMapping(value = "/payDate/statistics", name = "根据消息操作中间表收费时统计")
  public ResponseResult<T> payDateStatistics(@RequestBody @Validated MessageModel msg) {
    msg.setOperateType(-1);
    billPayBiz.operateBillPay(msg);
    return ResponseUtil.success(null);
  }


  /**
   * 根据时间段批量操作中间表账单收费时统计
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表账单收费时统计")
  @PostMapping(value = "/payDate/statistics/batch", name = "根据时间段批量操作中间表账单收费时统计")
  public ResponseResult<T> pullPayDateStatistics(@RequestBody PullForm form) throws InterruptedException {
    statEmpPayBiz.pullPayDateStatistics(form);
    return ResponseUtil.success(null);
  }
}
