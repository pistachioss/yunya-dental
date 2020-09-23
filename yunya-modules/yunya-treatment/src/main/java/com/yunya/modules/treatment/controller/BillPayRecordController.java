package com.yunya.modules.treatment.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BillPayRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
  @Autowired private BillPayRecordBiz billPayRecordBiz;

  /**
   * 根据账单收费记录ID撤销账单收费记录
   *
   * @param billPayRecordId 账单收费记录ID
   * @return
   */
  @ApiOperation("根据账单收费记录ID撤销账单收费记录")
  @ApiImplicitParam(name = "billPayRecordId", value = "账单收费记录ID", required = true)
  @GetMapping(value = "/revoke/{billPayRecordId}", name = "根据账单收费记录ID撤销账单收费记录")
  public ResponseResult revokeBillPayRecord(
      @PathVariable(value = "billPayRecordId") Integer billPayRecordId) {
    billPayRecordBiz.revoke(billPayRecordId);
    return ResponseUtil.success();
  }
}
