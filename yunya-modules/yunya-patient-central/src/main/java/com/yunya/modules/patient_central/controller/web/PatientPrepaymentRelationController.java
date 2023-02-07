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
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.modules.patient_central.biz.PatientPrepaymentRelationBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.yunya.framework.common.enums.PatientDepositAccountTypeEnum.NORMAL_PREPAYMENT;

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
  @ApiImplicitParams(value = {
          @ApiImplicitParam(
                  name = "id",
                  value = "患者id",
                  required = true,
                  dataType = "int",
                  paramType = "path"),
          @ApiImplicitParam(
                  name = "prepaymentType",
                  value = "预付款账号类型",
                  required = true,
                  defaultValue = "1",
                  dataType = "int")})
  @GetMapping("/prepaidAccountBaseInfo/{id}")
  public ResponseResult<PatientPrepaymentsInfoVo> findPrepaymentInfo(@PathVariable("id") Integer id, Integer prepaymentType) {
    if (StringHelper.isNull(prepaymentType)) {
      prepaymentType = NORMAL_PREPAYMENT.getType();
    }
    return ResponseUtil.success(this.patientPrepaymentBiz.findPrepaymentInfo(id, prepaymentType));
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
   * 充值记录-导出
   * @param response 请求
   * @param queryForm 条件
   * @return 导出集合
   * @throws IOException
   */
  @ApiOperation("充值记录-导出")
  @PostMapping(value = "/rechargeRecord/export", name = "导出充值记录")
  public ResponseResult<T> expendExportRechargeRecord(
          HttpServletResponse response, @RequestBody PrepaidRechargeRecordQueryForm queryForm) throws IOException {
    patientPrepaymentBiz.expendExportRechargeRecord(response, queryForm);
    return ResponseUtil.success(null);
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
   * 退费记录-导出
   * @param response 请求
   * @param queryForm 条件
   * @return 导出集合
   * @throws IOException
   */
  @ApiOperation("退费记录-导出")
  @PostMapping(value = "/refundList/export", name = "导出退费记录")
  public ResponseResult<T> expendExportRefundList(
          HttpServletResponse response, @RequestBody PrepaidMeturnRecordQueryForm queryForm) throws IOException {
    patientPrepaymentBiz.expendExportRefundList(response, queryForm);
    return ResponseUtil.success(null);
  }


  /**
   * 消费记录
   * @param queryForm 预付款消费QueryForm
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("消费记录")
  @PostMapping("/expendList")
  public ResponseResult expendList(@RequestBody PrepaidExpendRecordQueryForm queryForm) {
    return ResponseUtil.success(patientPrepaymentBiz.expendList(queryForm));
  }

  /**
   * 消费记录-导出
   * @param response 请求
   * @param queryForm 条件
   * @return 导出集合
   * @throws IOException
   */
  @ApiOperation("消费记录-导出")
  @PostMapping(value = "/expendList/export", name = "导出消费记录")
  public ResponseResult<T> expendExport(
          HttpServletResponse response, @RequestBody PrepaidExpendRecordQueryForm queryForm) throws IOException {
    patientPrepaymentBiz.expendExport(response, queryForm);
    return ResponseUtil.success(null);
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

  /**
   * 查询共享帐户绑定信息
   * @param patientId 患者id
   * @return 主卡人信息集合
   */
  @ApiOperation("查询共享帐户绑定信息")
  @GetMapping("/sharedAccount/{patientId}")
  public ResponseResult<List<PatientPrepaymentsOwnerInfoVo>> sharedAccount(@PathVariable(value = "patientId") Integer patientId) {
    List<PatientPrepaymentsOwnerInfoVo> patientCardOwnerInfoVos = patientPrepaymentBiz.finishedAccount(patientId);
    return ResponseUtil.success(patientCardOwnerInfoVos);
  }

  /**
   * 查询预付款账户类型类别
   *
   * @return
   */
  @ApiOperation("查询预付款账户类型")
  @GetMapping("/type/list")
  public ResponseResult<List<PatientPrepaymentTypeVO>> findPatientPrepaymentTypeList() {
    return ResponseUtil.success(patientPrepaymentBiz.findPatientPrepaymentTypeList());
  }
}
