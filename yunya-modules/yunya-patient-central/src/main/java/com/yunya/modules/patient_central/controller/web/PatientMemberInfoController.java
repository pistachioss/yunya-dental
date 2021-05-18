package com.yunya.modules.patient_central.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.form.CardRelationForm;
import com.yunya.feign.patient_central.domain.form.CardTypeForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.MemberExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.MemberReturnRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientMemberRelationQueryForm;
import com.yunya.feign.patient_central.domain.query.RechargeRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.report.domain.query.EmployeeWorkloadQuery;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientMemberInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简单介绍:</br> 患者会员卡信息 控制层
 *
 * @author: WY
 * @date 2020/7/30 13:22
 * @description: 患者会员卡信息增删改查
 * @since: 1.0.0
 */
@Api(value = "患者会员卡信息", description = "患者会员卡信息（增删查改）")
@RestController
@RequestMapping("patientMember")
public class PatientMemberInfoController {

  /** 注入对象 */
  private final PatientMemberInfoBiz patientMemberInfoBiz;

  public PatientMemberInfoController(PatientMemberInfoBiz patientMemberInfoBiz) {
    this.patientMemberInfoBiz = patientMemberInfoBiz;
  }

  /**
   * 会员基本信息
   *
   * @param id 患者id
   * @return ResponseResult<MemberBaseInfoVo>
   */
  @ApiOperation("会员基本信息")
  @GetMapping("/basicInformation/{id}")
  public ResponseResult<MemberBaseInfoVo> findMemberBaseInfo(@PathVariable("id") Integer id) {
    return ResponseUtil.success(patientMemberInfoBiz.findMemberBaseInfo(id));
  }

  /**
   * 会员卡关联查询
   *
   * @param patientMemberRelationQueryForm 患者会员卡关联关系
   * @return ResponseResult<MemberRelationVo>
   */
  @ApiOperation("会员卡关联查询")
  @PostMapping("/relatedInformation")
  public ResponseResult<MemberRelationVo> findMemberBindingRelation(
      @RequestBody @Validated PatientMemberRelationQueryForm patientMemberRelationQueryForm) {
    return ResponseUtil.success(
        patientMemberInfoBiz.findMemberBindingRelation(patientMemberRelationQueryForm));
  }

  /**
   * 患者可用会员卡列表查询
   *
   * @param patientId 患者id
   * @return PatientPrepaymentBalanceVo
   */
  @ApiOperation("患者可用会员卡列表查询")
  @GetMapping("/balancePayment/{patientId}")
  public ResponseResult<List<MemberBaseInfoVo>> balancePayment(
      @PathVariable("patientId") Integer patientId) {
    List<MemberBaseInfoVo> resultList = this.patientMemberInfoBiz.balancePayment(patientId);
    return ResponseUtil.success(resultList);
  }

  /**
   * 添加会员卡关联关系/共享值关联关系
   *
   * @param form 会员卡关联关系
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("添加会员卡关联关系/共享值关联关系")
  @PostMapping("/addMemberBindingRelation")
  public ResponseResult addMemberBindingRelation(@RequestBody MemberBindingRelationInfoModel form) {
    return patientMemberInfoBiz.addMemberBindingRelation(form);
  }

  /**
   * 删除会员卡关联关系
   *
   * @param cardRelationForm 会员卡关系删除
   * @return ResponseResult
   */
  @ApiOperation("删除会员卡关联关系")
  @DeleteMapping("/delete")
  public ResponseResult deleteById(@RequestBody CardRelationForm cardRelationForm) {
    patientMemberInfoBiz.deleteRelationById(cardRelationForm);
    return ResponseUtil.success();
  }

  /**
   * 开卡
   *
   * @param openCardModel 开卡Model
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("开卡")
  @PostMapping("/openCard")
  @RepeatSubmit
  public ResponseResult addMemberCard(@RequestBody OpenCardModel openCardModel) {
    return patientMemberInfoBiz.addMemberCard(openCardModel);
  }

  /**
   * 会员卡变更
   *
   * @param form 会员卡类型修改Form
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("会员卡变更")
  @PostMapping("/change")
  public ResponseResult changeType(@RequestBody @Validated CardTypeForm form) {
    patientMemberInfoBiz.changeType(form);
    return ResponseUtil.success();
  }

  /**
   * 变更记录
   *
   * @param cardNumber 会员卡号
   * @return ResponseResult<List<PatientMemberChangeLogVo>>
   */
  @ApiOperation("变更记录")
  @GetMapping("/changeLog/{cardNumber}")
  public ResponseResult<List<PatientMemberChangeLogVo>> changeLog(
      @PathVariable(value = "cardNumber") String cardNumber) {
    return ResponseUtil.success(patientMemberInfoBiz.changeLog(cardNumber));
  }

  /**
   * 充值
   *
   * @param memberRechargeModel 会员卡充值Model
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("充值")
  @PostMapping("/recharge")
  public ResponseResult recharge(@RequestBody MemberRechargeModel memberRechargeModel) {
    return patientMemberInfoBiz.recharge(memberRechargeModel);
  }

  /**
   * 账单退费
   *
   * @param memberBillRechargeModel 账单退费
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("账单退费")
  @PostMapping("/billRefund")
  public ResponseResult billRefund(@RequestBody MemberBillRechargeModel memberBillRechargeModel) {
    patientMemberInfoBiz.billRefund(memberBillRechargeModel);
    return ResponseUtil.success();
  }

  /**
   * 充值记录
   *
   * @param queryFormform 充值记录QueryForm
   * @return ResponseResult<PageInfo<RechargeRecordVo>>
   */
  @CurrentUser
  @ApiOperation("充值记录")
  @PostMapping("/rechargeRecord")
  public ResponseResult<PageInfo<RechargeRecordVo>> rechargeRecord(
      @RequestBody RechargeRecordQueryForm queryFormform) {
    return ResponseUtil.success(patientMemberInfoBiz.rechargeRecord(queryFormform));
  }

  /**
   * 退费
   *
   * @param model 会员卡退费Model
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("退费")
  @PostMapping("/refund")
  public ResponseResult refund(@RequestBody MemberReturnRecordModel model) {
    patientMemberInfoBiz.refund(model);
    return ResponseUtil.success();
  }

  /**
   * 退费记录
   *
   * @param queryForm 退费记录QueryForm
   * @return ResponseResult<PageInfo<MemberReturnRecordVo>>
   */
  @CurrentUser
  @ApiOperation("退费记录")
  @PostMapping("/refundList")
  public ResponseResult<PageInfo<MemberReturnRecordVo>> refundList(
      @RequestBody MemberReturnRecordQueryForm queryForm) {
    return ResponseUtil.success(patientMemberInfoBiz.refundList(queryForm));
  }

  /**
   * 消费记录
   *
   * @param queryForm 消费记录查询QueryForm
   * @return ResponseResult<PageInfo<MemberExpendRecordVo>>
   */
  @CurrentUser
  @ApiOperation("消费记录")
  @PostMapping("/expendList")
  public ResponseResult<PageInfo<MemberExpendRecordVo>> expendList(
      @RequestBody MemberExpendRecordQueryForm queryForm) {
    return ResponseUtil.success(patientMemberInfoBiz.expendList(queryForm));
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
          HttpServletResponse response, @RequestBody MemberExpendRecordQueryForm queryForm) throws IOException {
    patientMemberInfoBiz.expendExport(response, queryForm);
    return ResponseUtil.success(null);
  }

  /**
   * 会员卡消费
   *
   * @param model 消费model
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("会员卡消费")
  @RequestMapping(value = "/member/expend", method = RequestMethod.POST)
  public ResponseResult expend(@RequestBody MemberExpendRecordModel model) {
    return patientMemberInfoBiz.expend(model);
  }

  /**
   * 会员卡撤销收费
   *
   * @param model 撤销收费参数模型
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("会员卡撤销收费")
  @RequestMapping(value = "/member/revocationFee", method = RequestMethod.POST)
  public ResponseResult revocationFee(@RequestBody MemberRevocationFeeModel model) {
    return patientMemberInfoBiz.revocationFee(model);
  }

  /**
   * 查询已绑定主卡信息
   * @param patientId 患者id
   * @return 主卡人信息集合
   */
  @ApiOperation("查询已绑定主卡信息")
  @GetMapping("/bindMembershipCard/{patientId}")
  public ResponseResult<List<PatientCardOwnerInfoVo>> bindMembershipCard(@PathVariable(value = "patientId") Integer patientId) {
    List<PatientCardOwnerInfoVo> patientCardOwnerInfoVos = patientMemberInfoBiz.findPatientCardOwnerInfo(patientId);
    return ResponseUtil.success(patientCardOwnerInfoVos);
  }
}
