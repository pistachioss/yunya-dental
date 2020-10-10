package com.yunya.modules.treatment.controller.web;

import com.yunya.feign.treatment.domain.model.BillRefundModel;
import com.yunya.feign.treatment.domain.vo.BillDetailGroupVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BillRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介: 账单详情信息控制器
 *
 * @author: chow
 * @date: 2020/9/11 17:02
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "账单、开单详情查询")
@RestController
@RequestMapping("bill")
public class BillRecordController {

  /** 注入对象 */
  @Autowired private BillRecordBiz billRecordBiz;

  /**
   * 根据开单记录ID查询开单详情与账单详情信息
   *
   * @param orderRecordId 就诊记录ID
   * @return
   */
  @ApiOperation("根据开单ID查询开单详情与账单详情信息")
  @ApiImplicitParam(
      name = "orderRecordId",
      value = "开单记录ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @GetMapping(value = "/detail/{orderRecordId}", name = "根据开单记录ID查询开单详情与账单详情信息")
  public ResponseResult<BillDetailGroupVO> findOrderDetailAndBillDetailByOrderRecordId(
      @PathVariable(value = "orderRecordId") Integer orderRecordId) {
    BillDetailGroupVO resultList = billRecordBiz.findOrderDetailAndBillDetail(orderRecordId);
    return ResponseUtil.success(resultList);
  }

  /**
   * 账单退费
   *
   * @param model 账单退费参数模型
   * @return
   */
  @CurrentUser
  @ApiOperation("billRecordBiz")
  @ApiImplicitParams({@ApiImplicitParam(name = "model", value = "账单退费参数模型", required = true)})
  @PostMapping(value = "/refund", name = "账单退费")
  public ResponseResult billRefund(@RequestBody @Validated BillRefundModel model) {
    billRecordBiz.refund(model);
    return ResponseUtil.success();
  }
}
