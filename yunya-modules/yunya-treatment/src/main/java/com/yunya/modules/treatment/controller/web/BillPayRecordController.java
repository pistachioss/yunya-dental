package com.yunya.modules.treatment.controller.web;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BillPayRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 账单收费记录管理（撤销账单收费记录）
 *
 * @author: chow
 * @date: 2020/9/14 10:54
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "账单收费记录管理（撤销账单收费记录）")
@RestController
@RequestMapping("bill")
public class BillPayRecordController {

  /** 注入对象 */
  private final BillPayRecordBiz billPayRecordBiz;

  public BillPayRecordController(BillPayRecordBiz billPayRecordBiz) {
    this.billPayRecordBiz = billPayRecordBiz;
  }

  /**
   * 根据账单收费记录ID撤销账单收费记录
   *
   * @param billPayRecordId 账单收费记录ID
   * @return
   */
  @CurrentUser
  @ApiOperation("根据账单收费记录ID撤销账单收费记录")
  @GetMapping(value = "/revoke/{billPayRecordId}", name = "根据账单收费记录ID撤销账单收费记录")
  public ResponseResult<T> revokeBillPayRecord(
      @PathVariable(value = "billPayRecordId") Integer billPayRecordId) {
    billPayRecordBiz.revoke(billPayRecordId);
    return ResponseUtil.success(null);
  }
}
