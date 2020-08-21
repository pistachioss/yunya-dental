package com.yunya.modules.treatment.controller;

import com.yunya.feign.treatment.domain.form.OrderDetailForm;
import com.yunya.feign.treatment.domain.model.OrderRecordModel;
import com.yunya.feign.treatment.domain.vo.OrderDetailInfoVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.OrderRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简介: 患者就诊开单管理
 *
 * @author: chow
 * @date: 2020/8/17 20:13
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "患者就诊开单管理（开单、账单解锁、账单（开单）详情）")
@RestController
@RequestMapping("order")
public class OrderRecordController {

  /** 注入对象 */
  private final OrderRecordBiz orderRecordBiz;

  public OrderRecordController(OrderRecordBiz orderRecordBiz) {
    this.orderRecordBiz = orderRecordBiz;
  }

  /**
   * 根据就诊ID查询开单信息
   *
   * @param treatmentRecordId 就诊记录ID
   * @return
   */
  @ApiOperation("根据就诊记录ID查询开单信息")
  @GetMapping("/list/{treatmentRecordId}")
  public ResponseResult findOrderInfoByTreatmentId(
      @PathVariable(value = "treatmentRecordId") Integer treatmentRecordId) {
    OrderDetailInfoVO resultData = orderRecordBiz.findOrderDetailInfoVO(treatmentRecordId);
    return ResponseUtil.success(resultData);
  }

  /**
   * 暂存开单信息
   *
   * @param model 开单信息
   * @return
   */
  @CurrentUser
  @ApiOperation("暂存账单")
  @PostMapping("/hold")
  public ResponseResult holdOrder(@RequestBody @Validated OrderRecordModel model) {
    orderRecordBiz.storage(model);
    return ResponseUtil.success();
  }

  /**
   * 提交并完成接诊
   *
   * @param model 开单信息
   * @return
   */
  @CurrentUser
  @ApiOperation("提交并治疗完成")
  @PostMapping("/submit")
  public ResponseResult submitAndCompleteOrder(@RequestBody @Validated OrderRecordModel model) {
    orderRecordBiz.submitAndCompleteOrder(model);
    return ResponseUtil.success();
  }

  /**
   * 根据开单记录ID账单解锁
   *
   * @param orderRecordId 开单记录ID
   * @return
   */
  @CurrentUser
  @ApiOperation("根据开单记录ID账单解锁")
  @GetMapping("/unlock/{orderRecordId}")
  public ResponseResult unlockOrder(@PathVariable(value = "orderRecordId") Integer orderRecordId) {
    orderRecordBiz.unlockOrder(orderRecordId);
    return ResponseUtil.success();
  }

  /**
   * 根据开单记录ID修改订单明细并提交
   *
   * @param orderRecordId 开单记录ID
   * @param detailForms 开单明细
   * @return
   */
  @CurrentUser
  @ApiOperation("根据开单记录ID修改订单明细并提交")
  @PutMapping("/modify/{orderRecordId}")
  public ResponseResult modifyAndCommitOrder(
      @PathVariable(value = "orderRecordId") Integer orderRecordId,
      @RequestBody @Validated List<OrderDetailForm> detailForms) {
    orderRecordBiz.modifyAndCommitOrder(orderRecordId, detailForms);
    return ResponseUtil.success();
  }
}
