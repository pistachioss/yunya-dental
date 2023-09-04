package com.yunya.modules.patient_central.rpc;

import com.yunya.feign.appointment.vo.AppointmentUnDonePatientInfoVO;
import com.yunya.feign.ivy_mini.domain.form.WxSaveFansForm;
import com.yunya.feign.patient_central.domain.form.UpdPassForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.vo.ClinicChargeItemVO;
import com.yunya.feign.treatment.domain.vo.TreatmentPatientInfoVO;
import com.yunya.feign.treatment.domain.vo.WaitingPatientInfoVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.*;
import com.yunya.modules.patient_central.biz.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BasePatient;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/3 15:22
 * @description:
 * @since: 1.0.0
 */
@Api("患者信息服务接口暴露")
@RestController
@RequestMapping("api")
@Slf4j
public class PatientServiceRest {
  /** 患者 */
  @Autowired private PatientBaseInfoBiz patientBaseInfoBiz;

  /** 会员卡信息 */
  @Autowired private PatientMemberInfoBiz patientMemberInfoBiz;

  /** 预付款关联 */
  @Autowired private PatientPrepaymentRelationBiz patientPrepaymentBiz;

  /** 回调中心 */
  @Autowired private InformationCallbackBiz informationCallbackBiz;

  @Autowired private RedisUtils redisUtils;

  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;

  @Autowired private WxFansBiz wxFansBiz;

  @ApiOperation("同步老数据UnionId")
  @RequestMapping(value = "/syncUnionId", method = RequestMethod.POST)
  public Integer syncUnionId(@RequestBody @Validated SyncUnionIdForm form) {
   return wxFansBiz.syncUnionId(form);
  }

  @ApiOperation("查询患者是否已被绑定")
  @RequestMapping(value = "/selectIsBind", method = RequestMethod.POST)
  public List<WaitingPatientInfoVO> selectIsBind(@RequestBody @Validated List<WaitingPatientInfoVO> registeredList) {
    return wxFansBiz.selectIsBind(registeredList);
  }

  @ApiOperation("查询患者是否已被绑定")
  @RequestMapping(value = "/selectIsBind2", method = RequestMethod.POST)
  public List<AppointmentUnDonePatientInfoVO> selectIsBind2(@RequestBody @Validated List<AppointmentUnDonePatientInfoVO> registeredList) {
    return wxFansBiz.selectIsBind2(registeredList);
  }

  @ApiOperation("查询患者是否已被绑定")
  @RequestMapping(value = "/selectIsBind3", method = RequestMethod.POST)
  public List<TreatmentPatientInfoVO> selectIsBind3(@RequestBody @Validated List<TreatmentPatientInfoVO> registeredList) {
    return wxFansBiz.selectIsBind3(registeredList);
  }

  @ApiOperation("保存公众号粉丝绑定")
  @RequestMapping(value = "/saveWxAndFansBind", method = RequestMethod.POST)
  public Integer saveWx(@RequestBody @Validated WxFansSaveForm wxFansSaveForm) {
    return wxFansBiz.save(wxFansSaveForm);
  }

  @ApiOperation("根据姓名/手机号/姓名拼音模糊查询患者")
  @RequestMapping(value = "/findPatientByNameAndMobile", method = RequestMethod.POST)
  public List<PatientBaseInfoVo> findPatientByNameAndMobile(
      @RequestBody @Validated PatientLikeFinleQueryForm patientBaseInfoQueryForm) {
    return patientBaseInfoBiz.findPatientByNameAndMobile(patientBaseInfoQueryForm);
  }

  @ApiOperation("根据患者id查询患者信息")
  @RequestMapping(value = "/findPatientInfoById/{id}", method = RequestMethod.GET)
  public PatientBaseInfo findPatientInfoById(@PathVariable Integer id) {
    return patientBaseInfoBiz.findPatientInfoById(id);
  }

  @ApiOperation("根据患者id集合查询患者list")
  @RequestMapping(value = "/findPatientInfoByIds", method = RequestMethod.POST)
  public List<PatientBaseInfoVo> findPatientInfoByIds(@RequestBody List<Integer> ids) {
    return patientBaseInfoBiz.findPatientInfoByIds(ids, null);
  }

  @ApiOperation("根据患者id集合查询患者list")
  @RequestMapping(value = "/findPatientInfoByIds/{hasDied}", method = RequestMethod.POST)
  public List<PatientBaseInfoVo> findPatientInfoByIds(@RequestBody List<Integer> ids, @PathVariable(value = "hasDied") Boolean hasDied) {
    return patientBaseInfoBiz.findPatientInfoByIds(ids, hasDied);
  }

  @ApiOperation("根据患者id查询患者资料")
  @RequestMapping(value = "/total/patientInfo/{id}", method = RequestMethod.GET)
  public PatientTotalInfoVo findPatientTotalInfo(@PathVariable(value = "id") Integer id) {
    return patientBaseInfoBiz.findPatientTotalInfo(id);
  }

  @ApiOperation("根据患者id查询患者资料")
  @RequestMapping(value = "/total/patientInfoList", method = RequestMethod.POST)
  public List<PatientTotalInfoVo> findPatientTotalInfo(@RequestBody List<Integer> ids) {
    if (StringHelper.isNotEmpty(ids)) {
      return patientBaseInfoBiz.findPatientTotalInfoList(ids);
    }
    return new ArrayList<>();
  }

  @ApiOperation("根据患者id查询患者资料")
  @RequestMapping(value = "/findPatientMemberInfo", method = RequestMethod.POST)
  public List<PatientMemberInfo> findPatientMemberInfo(
      @RequestBody PatientMemberInfo patientMemberInfo) {
    return patientMemberInfoBiz.selectList(patientMemberInfo);
  }

  @ApiOperation("初始化患者病历号")
  @RequestMapping(value = "/generateMedicalNumber", method = RequestMethod.POST)
  public String generateMedicalNumber(@RequestBody PatientBaseInfo patientBaseInfo) {
    return patientBaseInfoBiz.generateMedicalNumber(patientBaseInfo.getOrgId(), patientBaseInfo);
  }

  @ApiOperation("查询患者信息")
  @RequestMapping(value = "/findPatientList", method = RequestMethod.POST)
  public List<PatientBaseInfo> findPatientInfo(@RequestBody PatientBaseInfo patientBaseInfo) {
    return patientBaseInfoBiz.selectList(patientBaseInfo);
  }

  @ApiOperation("查询患者信息列表")
  @RequestMapping(value = "/findPatientInfoList", method = RequestMethod.POST)
  public List<PatientBaseInfo> findPatientInfoList(@RequestBody PatientBaseInfo patientBaseInfo) {
    return patientBaseInfoBiz.selectList(patientBaseInfo);
  }

  @ApiOperation("根据门诊编号获取可用的病历号后六位")
  @RequestMapping(value = "/medical2/{clinNum}", method = RequestMethod.GET)
  public Integer findMedicalNumberByClinNum(@PathVariable(value = "clinNum") String clinNum) {
    return patientBaseInfoBiz.findMedicalNumberByClinNum(clinNum);
  }

  @CurrentUser
  @ApiOperation("会员卡充值")
  @RequestMapping(value = "/patientMember/recharge", method = RequestMethod.POST)
  public ResponseResult recharge(@RequestBody MemberRechargeModel memberRechargeModel) {
    patientMemberInfoBiz.recharge(memberRechargeModel);
    return ResponseUtil.success();
  }

  @CurrentUser
  @ApiOperation("会员卡消费")
  @RequestMapping(value = "/member/expend", method = RequestMethod.POST)
  public ResponseResult expend(@RequestBody MemberExpendRecordModel model) {
    return patientMemberInfoBiz.expend(model);
  }

  @CurrentUser
  @ApiOperation("会员卡撤销收费")
  @RequestMapping(value = "/member/revocationFee", method = RequestMethod.POST)
  public ResponseResult revocationFee(@RequestBody MemberRevocationFeeModel model) {
    return patientMemberInfoBiz.revocationFee(model);
  }

  @CurrentUser
  @ApiOperation("会员账单退费")
  @RequestMapping(value = "/member/billRefund", method = RequestMethod.POST)
  public ResponseResult billRefund(@RequestBody MemberBillRechargeModel memberBillRechargeModel) {
    patientMemberInfoBiz.billRefund(memberBillRechargeModel);
    return ResponseUtil.success();
  }

  @CurrentUser
  @ApiOperation("预付款充值")
  @RequestMapping(value = "/prepayment/recharge", method = RequestMethod.POST)
  public ResponseResult recharge(@RequestBody PrepaidRechargeModel memberRechargeModel) {
    this.patientPrepaymentBiz.recharge(memberRechargeModel);
    return ResponseUtil.success();
  }

  @CurrentUser
  @ApiOperation("预付款账单退费")
  @PostMapping("/prepayment/billRefund")
  public ResponseResult billRefund(@RequestBody PrepaidBillRechargeModel prepaidBillRechargeModel) {
    patientPrepaymentBiz.billRefund(prepaidBillRechargeModel);
    return ResponseUtil.success();
  }

  @CurrentUser
  @ApiOperation("预付款消费")
  @RequestMapping(value = "/prepaid/expend", method = RequestMethod.POST)
  public ResponseResult expend(@RequestBody PrepaidExpendRecordModel model) {
    return patientPrepaymentBiz.expend(model);
  }

  @CurrentUser
  @ApiOperation("预付款撤销收费")
  @RequestMapping(value = "prepaid/revocationFee", method = RequestMethod.POST)
  public ResponseResult revocationFee(@RequestBody PrepaidRevocationFeeModel model) {
    return patientPrepaymentBiz.revocationFee(model);
  }

  @ApiOperation("修改硬件设备密码")
  @RequestMapping(value = "/updPass", method = RequestMethod.POST)
  public void updPass(@RequestBody UpdPassForm form) {
    patientBaseInfoBiz.updPass(form);
  }

  @ApiOperation("查询会员卡绑定信息")
  @RequestMapping(value = "/findMemberInfo", method = RequestMethod.POST)
  public MemberInfoVo findMemberInfo(@RequestBody PatientMemberInfoQueryForm form) {
    return patientMemberInfoBiz.findMemberInfo(form);
  }

  @ApiOperation("获取端口号")
  @RequestMapping(value = "/portNumberGet", method = RequestMethod.GET)
  public String portNumberGet() {
    return informationCallbackBiz.portNumberGet();
  }

  /**
   * 根据会员卡号和账单记录ID查询支付详情（外部服务调用）
   *
   * @param query
   * @return 返回支付详情
   */
  @ApiOperation("根据会员卡号和账单记录ID查询支付详情（外部服务调用）")
  @RequestMapping(value = "/member/paymentRecord", method = RequestMethod.POST)
  public MemberExpendRecord memberPaymentRecordDetail(@RequestBody PaymentRecordDetailQuery query) {
    return patientMemberInfoBiz.memberPaymentRecordDetail(query);
  }

  /**
   * 根据账单记录ID和预付款ID查询支付记录详细（外部服务调用）
   *
   * @param query
   * @return 预付款支付记录
   */
  @ApiOperation("根据账单记录ID和预付款ID查询支付记录详细（外部服务调用）")
  @RequestMapping(value = "/prepaid/paymentRecord", method = RequestMethod.POST)
  public PrepaidExpendRecord prePaidPaymentRecordDetail(
      @RequestBody PaymentRecordDetailQuery query) {
    return patientPrepaymentBiz.prePaidPaymentRecordDetail(query);
  }

  /**
   * 根据会员卡类型查询该会员卡数量
   *
   * @param memberTypeId
   * @return 会员卡大于0 返回true;否则返回false
   */
  @ApiOperation("根据会员卡类型查询该会员卡是否有在使用")
  @RequestMapping(value = "/member/count/{memberTypeId}", method = RequestMethod.GET)
  public boolean memberInfoCount(@PathVariable(value = "memberTypeId") Integer memberTypeId) {
    PatientMemberInfo patientMemberInfo = new PatientMemberInfo();
    patientMemberInfo.setMemberTypeId(memberTypeId);
    Long aLong = patientMemberInfoBiz.selectCount(patientMemberInfo);
    return aLong > 0;
  }

  /**
   * 根据支付方式统计会员充值和预付款充值的金额
   *
   * @param query
   * @return
   */
  @PostMapping(value = "/member/sumMemberAndPrepayRechargeCash")
  public BigDecimal sumMemberAndPrepayRechargeCash(
      @RequestBody @Validated CashReceiptOrRefundQuery query) {
    return patientMemberInfoBiz.sumMemberAndPrepayRechargeCash(query);
  }

  /**
   * 根据条件查询预付款、会员现金退费
   *
   * @param query
   * @return
   */
  @ApiOperation("根据条件查询患者全部信息")
  @RequestMapping(value = "/refund/cash", method = RequestMethod.POST)
  BigDecimal sumMemberAndPrepaidRefundCash(@RequestBody @Validated CashReceiptOrRefundQuery query) {
    return patientMemberInfoBiz.sumMemberAndPrepaidRefundCash(query);
  }

  /**
   * 根据条件查询患者全部信息
   *
   * @param queryForm
   * @return
   */
  @ApiOperation("根据条件查询患者全部信息")
  @RequestMapping(value = "/total/findPatientTotalInfo", method = RequestMethod.POST)
  public List<PatientTotalInfoVo> findPatientTotalInfo(
      @RequestBody PatientBaseInfoQueryForm queryForm) {
    return patientBaseInfoBiz.findPatientTotalInfo(queryForm);
  }

  @ApiOperation("查询微信用户是否注册")
  @GetMapping(value = "/count/register")
  public WxFans countRegister(@RequestParam(value = "openId", required = true) String openId) {
    return wxFansBiz.getRegister(openId);
  }

  @ApiOperation("根据Id查询患者信息公用信息")
  @GetMapping("/publicInformation/{id}")
  public PatientPublicInfoVo findPatientPublicInfoById(@PathVariable("id") Integer id) {
    return this.patientBaseInfoBiz.findPatientPublicInfoById(id);
  }

  @PostMapping("/wxFans/query")
  public WxFans getWxFans(@RequestBody WxUserQuery query) {
    return this.wxFansBiz.getOwnWxFans(query);
  }

  @ApiOperation("客服中心-用户管理列表-查看详情")
  @PostMapping("/wxFans/detail")
  public List<WxFansDetailVO> findDetail(
      @RequestBody @Validated WxFansDetailForm wxFansDetailForm) {
    return wxFansBiz.findDetail(wxFansDetailForm);
  }


  @PostMapping("/wxFans/findListByName")
  public List<WxFansVo> findListByName(
          @RequestBody @Validated WxFanByNameForm wxFanByNameForm) {
    return wxFansBiz.findListByName(wxFanByNameForm);
  }

  @ApiOperation("根据患者id查询微信用户信息")
  @RequestMapping(value = "/wx/patient/{patientId}", method = RequestMethod.GET)
  public WxPatientVo getWxPatientInfo(@PathVariable("patientId") Integer patientId) {
    return wxFansBiz.getWxPatientInfo(patientId);
  }

  @ApiOperation("根据fansId查询微信用户信息")
  @RequestMapping(value = "/wx/fansId/{fansId}", method = RequestMethod.GET)
  public WxFans getWxfansInfo(@PathVariable("fansId") Integer fansId) {
    return wxFansBiz.selectById(fansId);
  }

  @ApiOperation("查询微信用户的会员卡和预付款使用记录")
  @RequestMapping(value = "/wx/card/record", method = RequestMethod.GET)
  public List<WxCardUseVo> listPatientCardRecord(
      @RequestParam(value = "cardNumber", required = true) String cardNumber,
      @RequestParam(value = "type", required = true) Integer type) {
    return wxFansBiz.listPatientCardRecord(cardNumber, type);
  }

  @ApiOperation("会员卡关联查询")
  @PostMapping("/relatedInformation")
  public MemberRelationVo findMemberBindingRelation(
      @RequestBody @Validated PatientMemberRelationQueryForm patientMemberRelationQueryForm) {
    return patientMemberInfoBiz.findMemberBindingRelation(patientMemberRelationQueryForm);
  }

  @ApiOperation("查询推送消息的绑定人")
  @RequestMapping(value = "/wx/pusher/{patientId}", method = RequestMethod.GET)
  public WxFans getWxPushUser(@PathVariable("patientId") Integer patientId) {
    return wxFansBiz.getPushWxUser(patientId);
  }

  @ApiOperation("批量查询推送消息的绑定人")
  @RequestMapping(value = "/wx/pusher/batch", method = RequestMethod.POST)
  List<WxFans> listWxPushUser(@RequestBody List<Integer> patientIds) {
    return wxFansBiz.getPushWxUser(patientIds);
  }


  @ApiOperation("条件查询自助登记患者的人数")
  @PostMapping(value = "/count/selfRegistrationPatient")
  public Integer countSelfRegistrationPatient(@RequestBody SelfRegistrationPatientQuery patientQuery) {
    return patientBaseInfoBiz.countSelfRegistrationPatient(patientQuery);
  }

  @ApiOperation("保存小程序登录信息")
  @PostMapping(value = "/mini/fans/save")
  public void saveMiniAuth(@RequestBody WxSaveFansForm form) {
    wxFansBiz.saveMiniAuth(form);
  }

  @ApiOperation("保存更新微信用户信息")
  @PostMapping(value = "/mini/fans/modify")
  public void saveOrUpdate(@RequestBody WxFans wxFans) {
    wxFansBiz.saveOrUpdate(wxFans);
  }


  @ApiOperation("获取患者生日确认日期")
  @GetMapping(value = "/birthday/check/{patientId}")
  public Date getPatientBirthdayCheck(@PathVariable("patientId") Integer patientId) {
    PatientBaseInfo patientBaseInfo = patientBaseInfoBiz.selectById(patientId);
    if (patientBaseInfo == null) {
      return null;
    }
    return patientBaseInfo.getBirthdayCheck();
  }




  @ApiOperation("查询患者储蓄账号（会员卡or预付款）信息列表")
  @PostMapping("/member/depositAccount")
  public ClinicChargeItemVO findDepositAccountList(@RequestBody @Validated PatientDepositAccountQueryForm query) {
    return patientMemberInfoBiz.findDepositAccountList(query);
  }
}
