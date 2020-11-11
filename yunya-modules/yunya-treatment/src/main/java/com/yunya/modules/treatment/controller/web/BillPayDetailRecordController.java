package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.form.BillPayDetailForm;
import com.yunya.feign.treatment.domain.query.PaymentRecordQuery;
import com.yunya.feign.treatment.domain.vo.BillPayRecordVO;
import com.yunya.feign.treatment.domain.vo.PaymentRecordVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BillPayDetailRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简介: 账单支付明细记录控制器
 *
 * @author: chow
 * @date: 2020/9/15 10:51
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "账单支付明细记录管理（调整入账方式）")
@RestController
@RequestMapping("payment")
public class BillPayDetailRecordController {

  /** 注入对象 */
  @Autowired private BillPayDetailRecordBiz billPayDetailRecordBiz;

  /**
   * 根据收费记录ID查询入账明细列表
   *
   * @param billPayRecordId 收费记录ID
   * @return
   */
  @ApiOperation("根据收费记录ID查询入账明细列表")
  @ApiImplicitParam(name = "billPayRecordId", value = "收费记录ID", required = true)
  @GetMapping(value = "/detail/list/{billPayRecordId}", name = "根据收费记录ID查询入账明细列表")
  public ResponseResult<BillPayRecordVO> findList(
      @PathVariable(value = "billPayRecordId") Integer billPayRecordId) {
    BillPayRecordVO resultData = billPayDetailRecordBiz.findBillPayDetailList(billPayRecordId);
    return ResponseUtil.success(resultData);
  }

  /**
   * 调整账单支付记录入账明细
   *
   * @param billPayRecordId 收费记录ID
   * @param form 调整入账明细
   * @return
   */
  @CurrentUser
  @ApiOperation("调整账单支付记录入账明细")
  @PutMapping(value = "/detail/adjust/{billPayRecordId}", name = "调整账单入账明细")
  public ResponseResult<T> adjustBillPayDetail(
      @PathVariable(value = "billPayRecordId") Integer billPayRecordId,
      @RequestBody @Validated BillPayDetailForm form) {
    billPayDetailRecordBiz.adjustDetail(billPayRecordId, form);
    return ResponseUtil.success(null);
  }

  @ApiOperation("会员卡信息查询(患者档案-就诊记录-账单详情-收费信息-预付款/会员卡")
  @GetMapping(value = "/member/account")
  public ResponseResult memberAccountInfo(PaymentRecordQuery query) {
    List<PaymentRecordVO> memberAccountVOList = billPayDetailRecordBiz.memberAccountPaymentRecordInfo(query);
    return ResponseUtil.success(memberAccountVOList);
  }
}
