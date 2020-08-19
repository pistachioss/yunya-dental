package com.yunya.modules.treatment.controller;

import com.yunya.feign.treatment.domain.model.OrderRecordModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.OrderRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
   * 暂存开单信息
   *
   * @param model 开单参数
   * @return
   */
  @CurrentUser
  @ApiOperation("开单")
  @PostMapping("/hold")
  public ResponseResult holdOrder(@RequestBody @Validated OrderRecordModel model) {
    orderRecordBiz.storage(model);
    return ResponseUtil.success();
  }
}
