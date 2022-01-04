package com.yunya.middletable.controller.report;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseRefundBiz;
import com.yunya.middletable.service.StatEmpRefundBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/10/29 20:20
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "中间表退费控制层")
@RestController
@RequestMapping("refund")
public class BaseRefundController {

  @Autowired private BaseRefundBiz refundBiz;

  @Autowired private StatEmpRefundBiz statEmpRefundBiz;

  /**
   * 根据消息更新中间表退费信息
   *
   * @param msg 消息
   * @return
   */
  @ApiOperation("根据消息更新中间表退费信息")
  @PostMapping(value = "/operate", name = "根据消息更新中间表退费信息")
  public ResponseResult<T> operateRefund(@RequestBody @Validated MessageModel msg) {
    refundBiz.operateRefund(msg);
    return ResponseUtil.success(null);
  }

  /**
   * 根据时间段批量操作中间表退费记录
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表退费记录")
  @PostMapping(value = "/operate/batch", name = "form")
  public ResponseResult<T> pullRefundData(@RequestBody PullForm form) {
    refundBiz.pullRefundData(form);
    return ResponseUtil.success(null);
  }

  /**
   * 根据消息操作中间表退费时统计
   *
   * @param msg 消息
   * @return
   */
  @ApiOperation("根据消息操作中间表收费时统计")
  @PostMapping(value = "/refundDate/statistics", name = "根据消息操作中间表收费时统计")
  public ResponseResult<T> payDateStatistics(@RequestBody @Validated MessageModel msg) {
    msg.setOperateType(-1);
    refundBiz.operateRefund(msg);
    return ResponseUtil.success(null);
  }
  /**
   * 根据时间段批量操作中间表退费时统计账单数据
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表退费时统计账单数据")
  @PostMapping(value = "/refundDate/statistics/batch", name = "form")
  public ResponseResult pullRefundDateStatistics(PullForm form) throws InterruptedException {
    statEmpRefundBiz.pullRefundDateStatistics(form);
    return ResponseUtil.success(null);
  }
}
