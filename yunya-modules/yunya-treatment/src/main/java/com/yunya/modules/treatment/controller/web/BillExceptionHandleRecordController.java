package com.yunya.modules.treatment.controller.web;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BillExceptionHandleRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 简介: 账单异常处理控制器
 *
 * @author: chow
 * @date: 2020/9/24 20:27
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "账单异常处理")
@RestController
@RequestMapping("handle")
public class BillExceptionHandleRecordController {

  /** 账单异常处理 */
  @Autowired private BillExceptionHandleRecordBiz billExceptionHandleRecordBiz;

  /**
   * 根据账单异常处理记录ID查询账单处理详情
   *
   * @param billHandleRecordId 账单异常处理记录ID
   * @return
   */
  @ApiOperation("账单异常处理记录详情查询")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "billHandleRecordId", value = "账单异常处理记录ID", required = true)
  })
  @GetMapping(value = "/detail/{billHandleRecordId}", name = "账单异常处理记录详情查询")
  public ResponseResult<Map<String, Object>> findBillHandleDetailById(
      @PathVariable(value = "billHandleRecordId") Integer billHandleRecordId) {
    Map<String, Object> resultMap =
        billExceptionHandleRecordBiz.findBillHandleDetail(billHandleRecordId);
    return ResponseUtil.success(resultMap);
  }
}
