//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunya.modules.patient_central.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.PrepaidExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PrepaidMeturnRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PrepaidRechargeRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientPrepaymentRelationBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简单介绍:</br> 患者预付款 控制层
 *
 * @author: WY
 * @date 2020/7/30 13:22
 * @description: 患者预付款增删改查
 * @since: 1.0.0
 */
@Api(value = "患者预付款", description = "患者预付款（增删查改）")
@RestController
@RequestMapping("prepayment")
public class PatientPrepaymentRelationController {

  /** 注入对象 */
  private final PatientPrepaymentRelationBiz patientPrepaymentBiz;

  public PatientPrepaymentRelationController(PatientPrepaymentRelationBiz patientPrepaymentBiz) {
    this.patientPrepaymentBiz = patientPrepaymentBiz;
  }

  /**
   * 账户基本信息
   * @param id 患者id
   * @return ResponseResult<PatientPrepaymentsInfoVo>
   */
  @ApiOperation("账户基本信息")
  @GetMapping("/prepaidAccountBaseInfo/{id}")
  public ResponseResult<PatientPrepaymentsInfoVo> findPrepaymentInfo(@PathVariable("id") Integer id) {
    return ResponseUtil.success(this.patientPrepaymentBiz.findPrepaymentInfo(id));
  }

  /**
   * 新增关联
   * @param model 新增关联Model
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("新增关联")
  @PostMapping("/prepaymentLink")
  public ResponseResult addPrepaymentLink(@RequestBody PatientPrepaymentRelationModel model) {
    return this.patientPrepaymentBiz.addPrepaymentLink(model);
  }

  /**
   * 查询关联
   * @param id 卡主本人id
   * @return ResponseResult<List<PatientPrepaymentRelationVo>>
   */
  @ApiOperation("查询关联")
  @GetMapping("/findPrepaymentLink/{id}")
  public ResponseResult<List<PatientPrepaymentRelationVo>> findPrepaymentLink(@PathVariable("id") Integer id) {
    return ResponseUtil.success(this.patientPrepaymentBiz.findPrepaymentLink(id));
  }

  /**
   * 患者可用预付款列表查询
   * @param patientId 患者id
   * @return PatientPrepaymentBalanceVo
   */
  @ApiOperation("患者可用预付款列表查询")
  @GetMapping("/balancePayment/{patientId}")
  public ResponseResult<List<PatientPrepaymentsInfoVo>> balancePayment(@PathVariable("patientId") Integer patientId) {
    List<PatientPrepaymentsInfoVo> resultList = this.patientPrepaymentBiz.balancePayment(patientId);
    return ResponseUtil.success(resultList);
  }

  /**
   * 删除
   * @param id 关联关系id
   * @return ResponseResult
   */
  @ApiOperation("删除")
  @DeleteMapping("/delete/{id}")
  public ResponseResult deletePrepaymentLink(@PathVariable("id") Integer id) {
    this.patientPrepaymentBiz.deletePrepaymentLink(id);
    return ResponseUtil.success();
  }

  /**
   * 充值
   * @param memberRechargeModel 预付款充值
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("充值")
  @PostMapping("/recharge")
  public ResponseResult recharge(@RequestBody PrepaidRechargeModel memberRechargeModel) {
    return this.patientPrepaymentBiz.recharge(memberRechargeModel);
  }


  /**
   * 充值记录
   * @param form 充值记录queryForm
   * @return ResponseResult<PageInfo<PrepaidRechargeRecordVo>>
   */
  @CurrentUser
  @ApiOperation("充值记录")
  @PostMapping("/rechargeRecord")
  public ResponseResult<PageInfo<PrepaidRechargeRecordVo>> rechargeRecord(@RequestBody PrepaidRechargeRecordQueryForm form) {
    return ResponseUtil.success(this.patientPrepaymentBiz.rechargeRecord(form));
  }

  /**
   * 账单退费
   *
   * @param prepaidBillRechargeModel 账单退费
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("账单退费")
  @PostMapping("/billRefund")
  public ResponseResult billRefund(@RequestBody PrepaidBillRechargeModel prepaidBillRechargeModel) {
    patientPrepaymentBiz.billRefund(prepaidBillRechargeModel);
    return ResponseUtil.success();
  }


  /**
   * 退费
   * @param model 预付款退费
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("退费")
  @PostMapping("/refund")
  public ResponseResult refund(@RequestBody PrepaidMeturnRecordModel model) {
    patientPrepaymentBiz.refund(model);
    return ResponseUtil.success();
  }

  @CurrentUser
  @ApiOperation("预付款消费")
  @RequestMapping(value = "/prepaid/expend",method = RequestMethod.POST)
  public ResponseResult expend(@RequestBody PrepaidExpendRecordModel model ){
    return patientPrepaymentBiz.expend(model);
  }

  /**
   * 退费记录
   * @param queryForm 预付款退费记录列表
   * @return ResponseResult<PageInfo<PrepaidMeturnRecordVo>>
   */
  @CurrentUser
  @ApiOperation("退费记录")
  @PostMapping("/refundList")
  public ResponseResult<PageInfo<PrepaidMeturnRecordVo>> refundList(@RequestBody PrepaidMeturnRecordQueryForm queryForm) {
    return ResponseUtil.success(patientPrepaymentBiz.refundList(queryForm));
  }

  /**
   * 消费记录
   * @param queryForm 预付款消费QueryForm
   * @return ResponseResult
   */
  @ApiOperation("消费记录")
  @PostMapping("/expendList")
  public ResponseResult expendList(@RequestBody PrepaidExpendRecordQueryForm queryForm) {
    return ResponseUtil.success(patientPrepaymentBiz.expendList(queryForm));
  }

  /**
   * 预付款撤销收费
   * @param model 撤销收费参数模型
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("预付款撤销收费")
  @RequestMapping(value = "prepaid/revocationFee", method = RequestMethod.POST)
  public ResponseResult revocationFee(@RequestBody PrepaidRevocationFeeModel model) {
    return patientPrepaymentBiz.revocationFee(model);
  }


}
