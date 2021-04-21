package com.yunya.modules.treatment.controller.web;

import com.yunya.feign.report.domain.query.CurrentMonthBillInfoQuery;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.OrderDetailPayRecordBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 简介: 账单明细控制器
 *
 * @author: chow
 * @date: 2021/4/20 15:28
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("finance")
public class OrderDetailPayRecordController {
  /** 注入对象 */
  private final OrderDetailPayRecordBiz orderDetailPayRecordBiz;

  public OrderDetailPayRecordController(OrderDetailPayRecordBiz orderDetailPayRecordBiz) {
    this.orderDetailPayRecordBiz = orderDetailPayRecordBiz;
  }

  /**
   * 根据条件查询门诊当月账单明细列表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("财务报表-对账单-本月账单明细导出")
  @PostMapping(value = "/bill/detail/export", name = "本月账单明细列表导出")
  public ResponseResult<T> orderDetailPayRecordBiz(
      HttpServletResponse response, @RequestBody @Validated CurrentMonthBillInfoQuery query)
      throws IOException {
    orderDetailPayRecordBiz.exportCurrentMonthBillDetail(response, query);
    return ResponseUtil.success(null);
  }
}
