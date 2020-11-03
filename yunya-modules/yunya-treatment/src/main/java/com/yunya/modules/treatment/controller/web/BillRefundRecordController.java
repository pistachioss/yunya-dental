package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.query.BillRefundQuery;
import com.yunya.feign.treatment.domain.vo.BillRefundGroupInfoVO;
import com.yunya.feign.treatment.domain.vo.BillRefundRecordVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BillRefundRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介: 账单退费记录控制器
 *
 * @author: chow
 * @date: 2020/11/3 13:32
 * @description:
 * @since: 1.0.0
 */
@Api("账单退费记录控制器")
@RestController
@RequestMapping("refund")
public class BillRefundRecordController {

  /** 账单退费 */
  @Autowired private BillRefundRecordBiz billRefundRecordBiz;

  /**
   * 根据患者ID查询患者退费记录列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("账单退费列表查询")
  @PostMapping(value = "/patient/list", name = "账单退费列表查询")
  public ResponseResult<PageInfo<BillRefundRecordVO>> billRefundList(
      @RequestBody @Validated BillRefundQuery query) {
    PageInfo<BillRefundRecordVO> resultList = billRefundRecordBiz.findBillRefundList(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据账单退费记录ID查询退费详情
   *
   * @param billRefundRecordId 账单退费记录ID
   * @return
   */
  @ApiOperation("根据账单退费记录ID查询退费详情")
  @GetMapping(value = "/one/{billRefundRecordId}", name = "根据账单退费记录ID查询退费详情")
  public ResponseResult<BillRefundGroupInfoVO> findBillRefundDetail(
      @PathVariable(value = "billRefundRecordId") Integer billRefundRecordId) {
    BillRefundGroupInfoVO resultData =
        billRefundRecordBiz.findBillRefundDetailByBillRefundRecordId(billRefundRecordId);
    return ResponseUtil.success(resultData);
  }
}
