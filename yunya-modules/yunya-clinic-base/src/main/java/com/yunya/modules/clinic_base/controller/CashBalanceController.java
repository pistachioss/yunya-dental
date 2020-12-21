package com.yunya.modules.clinic_base.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.domain.form.CashBalanceForm;
import com.yunya.feign.clinic_base.domain.model.CashBalanceModel;
import com.yunya.feign.clinic_base.domain.query.CashBalanceQuery;
import com.yunya.feign.clinic_base.domain.query.PeriodCashQuery;
import com.yunya.feign.clinic_base.domain.vo.CashBalanceDetailVO;
import com.yunya.feign.clinic_base.domain.vo.CashBalanceVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.clinic_base.biz.CashBalanceBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 简介: 现金结存管理控制器
 *
 * @author: chow
 * @date: 2020/12/18 16:31
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "现金结存管理：新增、修改、查询、删除")
@RestController
@RequestMapping("balance")
public class CashBalanceController {

  /** 注入现金结存业务层 */
  @Autowired private CashBalanceBiz cashBalanceBiz;

  /**
   * 获取门诊新增期初现金结余金额
   *
   * @param orgId 组织ID
   * @return BigDecimal
   */
  @ApiOperation("获取门诊新增期初现金结余金额")
  @ApiImplicitParam(
      name = "orgId",
      value = "组织ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @GetMapping(value = "/begin/cash/{orgId}", name = "获取门诊新增期初现金结余金额")
  public ResponseResult<BigDecimal> findBeginningCash(
      @PathVariable(value = "orgId") Integer orgId) {
    BigDecimal cash = cashBalanceBiz.findBeginningCash(orgId);
    return ResponseUtil.success(cash);
  }

  /**
   * 获取门诊当前天期间现金收款
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  @ApiOperation("获取门诊当前天期间现金收款")
  @PostMapping(value = "/period/cash", name = "获取门诊当前天期间现金收款")
  public ResponseResult<BigDecimal> periodCash(@RequestBody @Validated PeriodCashQuery query) {
    BigDecimal periodCash = cashBalanceBiz.findPeriodCash(query);
    return ResponseUtil.success(periodCash);
  }

  /**
   * 新增现金结存记录
   *
   * @param model 现金结存新增模型
   * @return void
   */
  @CurrentUser
  @ApiOperation("新增现金结存记录")
  @PostMapping(value = "/save", name = "新增结存记录")
  public ResponseResult<T> addCashBalance(@RequestBody @Validated CashBalanceModel model) {
    cashBalanceBiz.saveCashBalance(model);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询现金结存列表
   *
   * @param query 查询条件
   * @return PageInfo<CashBalanceVO>
   */
  @ApiOperation("根据条件查询现金结存列表")
  @PostMapping(value = "/list", name = "根据条件查询现金结存列表 ")
  public ResponseResult<PageInfo<CashBalanceVO>> cashBalanceList(
      @RequestBody @Validated CashBalanceQuery query) {
    PageInfo<CashBalanceVO> pageInfo = cashBalanceBiz.findCashBalanceList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据结存记录ID查询现金结存详情
   *
   * @param id 结存记录ID
   * @return CashBalanceDetailVO
   */
  @ApiOperation("根据结存记录ID查询现金结存详情")
  @ApiImplicitParam(
      name = "id",
      value = "结存记录ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @GetMapping(value = "/one/{id}", name = "根据结存记录ID查询现金结存详情")
  public ResponseResult<CashBalanceDetailVO> one(@PathVariable(value = "id") Integer id) {
    CashBalanceDetailVO resultData = cashBalanceBiz.findCashBalanceDetailById(id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据ID修改现金结存记录
   *
   * @param id 结存记录ID
   * @param form 修改参数
   * @return
   */
  @CurrentUser
  @ApiOperation("根据ID修改现金结存记录")
  @ApiImplicitParam(
      name = "id",
      value = "结存记录ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @PutMapping(value = "/modify/{id}", name = "修改现金结存记录")
  public ResponseResult<T> modifyCashBalanceBiz(
      @PathVariable(value = "id") Integer id, @RequestBody CashBalanceForm form) {
    cashBalanceBiz.modify(id, form);
    return ResponseUtil.success(null);
  }
}
