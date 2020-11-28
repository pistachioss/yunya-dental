package com.yunya.modules.treatment.controller.web;

import com.yunya.feign.treatment.domain.model.TollDebtModel;
import com.yunya.feign.treatment.domain.model.TollModel;
import com.yunya.feign.treatment.domain.query.OrderPrivilegeQuery;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.TollBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

  /**
   * 匹配订单列表优惠信息
   *
   * @param query 匹配条件
   * @return
   */
  @CurrentUser
  @ApiOperation("匹配订单列表优惠信息")
  @PostMapping(value = "/privilege/match", name = "匹配订单列表优惠信息")
  public ResponseResult<List<OrderDetailChargeVO>> matchOrderTailPrivilegeList(
      @RequestBody @Validated OrderPrivilegeQuery query) {
    List<OrderDetailChargeVO> resultList = tollBiz.matchOrderTailPrivilege(query);
    return ResponseUtil.success(resultList);
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
  public ResponseResult<T> confirmCharge(@RequestBody @Validated TollModel model) {
    tollBiz.confirmCharge(model);
    return ResponseUtil.success(null);
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
  public ResponseResult<T> collectDebt(@RequestBody @Validated TollDebtModel model) {
    tollBiz.collectDebt(model);
    return ResponseUtil.success(null);
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
}
