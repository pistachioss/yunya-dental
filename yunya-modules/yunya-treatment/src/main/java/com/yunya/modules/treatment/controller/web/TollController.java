package com.yunya.modules.treatment.controller.web;

import com.yunya.feign.treatment.domain.model.TollDebtModel;
import com.yunya.feign.treatment.domain.model.TollModel;
import com.yunya.feign.treatment.domain.model.TreatTollModel;
import com.yunya.feign.treatment.domain.query.BillPayShareDetailQuery;
import com.yunya.feign.treatment.domain.query.OrderPrivilegeQuery;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.feign.treatment.domain.vo.TollConfirmVO;
import com.yunya.feign.treatment.domain.vo.TreatOrderRecordVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BillPayShareDetailBiz;
import com.yunya.modules.treatment.biz.TollBiz;
import com.yunya.modules.treatment.biz.TreatTollBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简介: 收费控制器
 *
 * @author: chow
 * @date: 2020/8/21 20:45
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "收费管理（匹配订单优惠、收费、收欠费）")
@RestController
@RequestMapping("charge")
public class TollController {

  /** 注入对象 */
  @Autowired private TollBiz tollBiz;

  @Autowired private BillPayShareDetailBiz billPayShareDetailBiz;
  @Autowired private TreatTollBiz treatTollBiz;

  /**
   * 匹配订单列表优惠信息
   *
   * @param query 匹配条件
   * @return
   */
  @CurrentUser
  @ApiOperation("匹配订单列表优惠信息")
  @PostMapping(value = "/privilege/match/old", name = "匹配订单列表优惠信息")
  @Deprecated
  public ResponseResult<List<OrderDetailChargeVO>> matchOrderTailPrivilegeListOld(
      @RequestBody @Validated OrderPrivilegeQuery query) {
    List<OrderDetailChargeVO> resultList = tollBiz.matchOrderTailPrivilege(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 匹配订单列表优惠信息
   *
   * @param query 匹配条件
   * @return
   */
  @CurrentUser
  @ApiOperation("匹配订单列表优惠信息")
  @PostMapping(value = "/privilege/match", name = "匹配订单列表优惠信息")
  public ResponseResult<TreatOrderRecordVO> matchOrderTailPrivilegeList(
      @RequestBody @Validated OrderPrivilegeQuery query) {
    TreatOrderRecordVO resultList = treatTollBiz.matchOrderTailPrivilege(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 一键结账
   *
   * @param treatmentRecordId 就诊记录ID
   * @return R
   */
  @CurrentUser
  @ApiOperation("一键免单")
  @GetMapping(value = "/autoCheck/{treatmentRecordId}", name = "自动结账")
  public ResponseResult<T> autoCheckOut(
      @PathVariable(value = "treatmentRecordId") Integer treatmentRecordId) {
    tollBiz.autoCheckOut(treatmentRecordId);
    return ResponseUtil.success(null);
  }

  /**
   * 确认收费
   *
   * @param model 收费参数
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("确认收费")
  @PostMapping("/confirm/old")
  public ResponseResult<TollConfirmVO> confirmCharge(@RequestBody @Validated TollModel model) {
    TollConfirmVO tollConfirmVO = tollBiz.confirmCharge(model);
    return ResponseUtil.success(tollConfirmVO);
  }

  /**
   * 查询账单剩余可用预付款支付金额
   *
   * @param billRecordId 账单记录ID
   * @return
   */
  @ApiOperation("根据账单ID查询该订单可用预付款支付金额")
  @ApiImplicitParam(
      name = "billRecordId",
      value = "账单记录ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @GetMapping(value = "/prepaid/amount/{billRecordId}", name = "根据账单ID查询该订单可用预付款支付金额")
  public ResponseResult<BigDecimal> restPrepaidAmount(
      @PathVariable(value = "billRecordId") Integer billRecordId) {
    BigDecimal amount = tollBiz.findRestPrepaidAmount(billRecordId);
    return ResponseUtil.success(amount);
  }

  /**
   * 收欠费
   *
   * @param model 收费参数
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("收欠费")
  @PostMapping(value = "/collect/debt", name = "收欠费")
  public ResponseResult<TollConfirmVO> collectDebt(@RequestBody @Validated TollDebtModel model) {
    TollConfirmVO tollConfirmVO = tollBiz.collectDebt(model);
    return ResponseUtil.success(tollConfirmVO);
  }

  /**
   * 点击收费修改账单状态
   *
   * @param orderRecordId 开单记录ID
   * @return
   */
  @Deprecated
  @ApiOperation("点击收费修改账单状态为收费中-（接口作废）")
  @GetMapping(value = "/change/{orderRecordId}", name = "点击收费修改账单状态")
  public ResponseResult<T> changeOrderRecordStatus(
      @PathVariable(value = "orderRecordId") Integer orderRecordId) {
    tollBiz.changeOrderRecordStatus(orderRecordId);
    return ResponseUtil.success(null);
  }

  /**
   * 取消账单收费
   *
   * @param orderRecordId 开单记录ID
   * @return
   */
  @ApiOperation("取消收费，修改账单状态为锁定")
  @GetMapping(value = "/cancel/{orderRecordId}", name = "开单记录ID")
  public ResponseResult<T> cancelCharge(
      @PathVariable(value = "orderRecordId") Integer orderRecordId) {
    tollBiz.cancelCharge(orderRecordId);
    return ResponseUtil.success(null);
  }

  /**
   * 查询当前订单可用预付款支付金额
   *
   * @param orderRecordId 订单记录ID
   * @return
   */
  @ApiOperation("查询当前订单可用预付款支付金额")
  @GetMapping(value = "/enable/prepayment/{orderRecordId}")
  public ResponseResult<Map<String, Object>> currentOrderEnablePrepayment(
      @PathVariable("orderRecordId") @Validated @NotNull(message = "订单记录ID不能为空")
          Integer orderRecordId) {
    Map<String, Object> result = new HashMap<>();
    result.put("enablePrepaymentAmount", this.tollBiz.currentOrderEnablePrepayment(orderRecordId));
    return ResponseUtil.success(result);
  }

  @ApiOperation("根据条件在treatment库生成项目收费分摊明细，不同步中间表")
  @PostMapping("/generate/sharedDetail")
  public ResponseResult generateItemPaySharedDetail(@RequestBody BillPayShareDetailQuery query) {
    DateUtil.dur("bill_pay_share_detail分摊数据生成", o->{
      billPayShareDetailBiz.deleteByBillDateRange(query);
      billPayShareDetailBiz.shullfeItemPaySharedDetail(query);
      return null;
    });
    return ResponseUtil.success();
  }

  /**
   * 确认收费
   *
   * @param model 收费参数
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("确认收费")
  @PostMapping("/confirm")
  public ResponseResult<TollConfirmVO> confirmCharge(@RequestBody @Validated TreatTollModel model) {
    treatTollBiz.clear(model.getOrderRecordId());
    TollConfirmVO tollConfirmVO = treatTollBiz.confirmCharge(model, (byte) 2);
    return ResponseUtil.success(tollConfirmVO);
  }

  /**
   * 挂账
   *
   * @param model 收费参数
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("挂账")
  @PostMapping("/credit")
  public ResponseResult<TollConfirmVO> chargeOnCredit(@RequestBody @Validated TreatTollModel model) {
    treatTollBiz.clear(model.getOrderRecordId());
    TollConfirmVO tollConfirmVO = treatTollBiz.chargeOnCredit(model);
    return ResponseUtil.success(tollConfirmVO);
  }

  /**
   * 收欠费
   *
   * @param model 收费参数
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("收欠费")
  @PostMapping("/collectDebt")
  public ResponseResult<TollConfirmVO> collectDebt(@RequestBody @Validated TreatTollModel model) {
    TollConfirmVO tollConfirmVO = treatTollBiz.collectDebt(model);
    return ResponseUtil.success(tollConfirmVO);
  }
}
