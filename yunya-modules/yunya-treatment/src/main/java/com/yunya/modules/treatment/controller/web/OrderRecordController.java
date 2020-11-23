package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.form.OrderRecordForm;
import com.yunya.feign.treatment.domain.model.BillAdjustDetailModel;
import com.yunya.feign.treatment.domain.model.OrderRecordModel;
import com.yunya.feign.treatment.domain.query.OrderProcessQuery;
import com.yunya.feign.treatment.domain.vo.OrderDetailInfoVO;
import com.yunya.feign.treatment.domain.vo.OrderProcessVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.OrderRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
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
  public ResponseResult<OrderDetailInfoVO> findOrderInfoByTreatmentId(
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
  public ResponseResult<T> holdOrder(@RequestBody @Validated OrderRecordModel model) {
    orderRecordBiz.storage(model);
    return ResponseUtil.success(null);
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
  public ResponseResult<T> submitAndCompleteOrder(@RequestBody @Validated OrderRecordModel model) {
    orderRecordBiz.submitAndCompleteOrder(model);
    return ResponseUtil.success(null);
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
  public ResponseResult<T> unlockOrder(
      @PathVariable(value = "orderRecordId") Integer orderRecordId) {
    orderRecordBiz.unlockOrder(orderRecordId);
    return ResponseUtil.success(null);
  }

  /**
   * 根据开单记录ID修改订单明细并提交
   *
   * @param orderRecordId 开单记录ID
   * @param form 开单明细
   * @return
   */
  @CurrentUser
  @ApiOperation("根据开单记录ID修改订单明细并提交")
  @PutMapping("/modify/{orderRecordId}")
  public ResponseResult<T> modifyAndCommitOrder(
      @PathVariable(value = "orderRecordId") Integer orderRecordId,
      @RequestBody @Validated OrderRecordForm form) {
    orderRecordBiz.modifyAndCommitOrder(orderRecordId, form);
    return ResponseUtil.success(null);
  }

  /**
   * 调整账单
   *
   * @param model 账单调整参数封装
   * @return
   */
  @CurrentUser
  @ApiOperation("调整账单")
  @PostMapping(value = "/adjust", name = "调整账单")
  public ResponseResult<T> adjustBill(@RequestBody @Validated BillAdjustDetailModel model) {
    orderRecordBiz.adjust(model);
    return ResponseUtil.success(null);
  }

  /**
   * 订单处理（门诊端-订单处理）
   *
   * @param query 订单处理参数模型
   * @return 返回订单处理列表
   */
  @ApiOperation("订单处理（门诊端-订单处理）")
  @PostMapping("/process")
  public ResponseResult<PageInfo<OrderProcessVO>> orderProcess(
      @RequestBody OrderProcessQuery query) {
    List<OrderProcessVO> orderProcessList = orderRecordBiz.orderProcess(query);
    if (query.getWhetherPage()) {
      Integer pageNum = query.getPageNum();
      Integer pageSize = query.getPageSize();
      int total = orderProcessList.size();
      PageInfo<OrderProcessVO> pageInfo = new PageInfo<>();
      pageInfo.setPageNum(pageNum);
      pageInfo.setPageSize(pageSize);
      pageInfo.setTotal(total);
      List<OrderProcessVO> list =
          orderProcessList.subList(
              pageSize * (pageNum - 1), (Math.min((pageSize * pageNum), total)));
      pageInfo.setList(list);
      return ResponseUtil.success(pageInfo);
    }
    return ResponseUtil.success(new PageInfo<>(orderProcessList));
  }
}
