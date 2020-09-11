package com.yunya.modules.treatment.controller;

import com.yunya.feign.treatment.domain.model.TollDebtModel;
import com.yunya.feign.treatment.domain.model.TollModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.TollBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 收费控制器
 *
 * @author: chow
 * @date: 2020/8/21 20:45
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "收费管理（收费、收欠费）")
@RestController
@RequestMapping("charge")
public class TollController {

  /** 注入对象 */
  @Autowired private TollBiz tollBiz;

  /**
   * 确认收费
   *
   * @param model 收费参数
   * @return
   */
  @CurrentUser
  @ApiOperation("确认收费")
  @PostMapping("/confirm")
  public ResponseResult confirmCharge(@RequestBody @Validated TollModel model) {
    tollBiz.confirmCharge(model);
    return ResponseUtil.success();
  }

  /**
   * 收欠费
   *
   * @param model 收费参数
   * @return
   */
  @CurrentUser
  @ApiOperation("收欠费")
  @ApiImplicitParams({@ApiImplicitParam(name = "model", value = "收欠费参数模型", required = true)})
  @PostMapping(value = "/collect/debt", name = "收欠费")
  public ResponseResult collectDebt(@RequestBody @Validated TollDebtModel model) {
    tollBiz.collectDebt(model);
    return ResponseUtil.success();
  }
}
