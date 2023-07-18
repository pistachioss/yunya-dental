package com.yunya.feign.patient_central;

import com.yunya.feign.appointment.vo.AppointmentUnDonePatientInfoVO;
import com.yunya.feign.ivy_mini.domain.form.WxSaveFansForm;
import com.yunya.feign.patient_central.domain.form.UpdPassForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.patient_central.factory.RemotePatientCentralServiceFallBackFactory;
import com.yunya.feign.system.vo.ClinicChargeItemVO;
import com.yunya.feign.treatment.domain.vo.TreatmentPatientInfoVO;
import com.yunya.feign.treatment.domain.vo.WaitingPatientInfoVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.patient_central.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/** @author YK */
@FeignClient(
    name = YunyaServiceNameConstants.YUNYA_PATIENT,
    fallbackFactory = RemotePatientCentralServiceFallBackFactory.class)
    public interface RemotePatientCentralServiceFeign {

  /**
   * 同步老数据UnionId
   *
   */
  @RequestMapping(value = "/api/syncUnionId", method = RequestMethod.POST)
  void syncUnionId(
          @RequestBody SyncUnionIdForm form);
  /**
   * 同步老数据UnionId
   *
   */
  @RequestMapping(value = "/api/selectIsBind", method = RequestMethod.POST)
  List<WaitingPatientInfoVO> selectIsBind(
          @RequestBody List<WaitingPatientInfoVO> form);

  @RequestMapping(value = "/api/selectIsBind2", method = RequestMethod.POST)
  List<AppointmentUnDonePatientInfoVO> selectIsBind2(
          @RequestBody List<AppointmentUnDonePatientInfoVO> form);

  @RequestMapping(value = "/api/selectIsBind3", method = RequestMethod.POST)
  List<TreatmentPatientInfoVO> selectIsBind3(
          @RequestBody List<TreatmentPatientInfoVO> form);
  /**
   * 患者信息模糊查询暴露接口
   *
   * @param patientBaseInfoQueryForm 参数封装
   * @return List<PatientBaseInfoVo>
   */
  @RequestMapping(value = "/api/findPatientByNameAndMobile", method = RequestMethod.POST)
  List<PatientBaseInfoVo> findPatientByNameAndMobile(
      @RequestBody PatientLikeFinleQueryForm patientBaseInfoQueryForm);

  /**
   * 根据患者id查询患者信息
   *
   * @param id 条件
   * @return PatientBaseInfo
   */
  @RequestMapping(value = "/api/findPatientInfoById/{id}", method = RequestMethod.GET)
  PatientBaseInfo findPatientInfoById(@PathVariable(value = "id") Integer id);

  /**
   * 根据患者id集合查询患者list
   *
   * @param ids 条件
   * @return List<PatientBaseInfoVo>
   */
  @RequestMapping(value = "/api/findPatientInfoByIds", method = RequestMethod.POST)
  List<PatientBaseInfoVo> findPatientInfoByIds(@RequestBody List<Integer> ids);

  /**
   * 根据患者id集合查询患者list
   *
   * @param ids 条件
   * @param hasDied  是否去世
   * @return List<PatientBaseInfoVo>
   */
  @RequestMapping(value = "/api/findPatientInfoByIds/{hasDied}", method = RequestMethod.POST)
  List<PatientBaseInfoVo> findPatientInfoByIds(@RequestBody List<Integer> ids, @PathVariable(value = "hasDied") boolean hasDied);

  /**
   * 根据患者id查询患者资料
   *
   * @param id 条件
   * @return PatientTotalInfoVo
   */
  @RequestMapping(value = "/api/total/patientInfo/{id}", method = RequestMethod.GET)
  PatientTotalInfoVo findPatientTotalInfo(@PathVariable(value = "id") Integer id);

  /**
   * 根据患者id查询患者资料
   *
   * @param ids 患者ID列表
   * @return List<PatientTotalInfoVo>
   */
  @ApiOperation("根据患者id查询患者资料")
  @RequestMapping(value = "/api/total/patientInfoList", method = RequestMethod.POST)
  List<PatientTotalInfoVo> findPatientTotalInfo(@RequestBody List<Integer> ids);

  /**
   * 根据患者id查询患者资料
   *
   * @param patientMemberInfo 条件
   * @return List<PatientMemberInfo>
   */
  @RequestMapping(value = "/api/findPatientMemberInfo", method = RequestMethod.POST)
  List<PatientMemberInfo> findPatientMemberInfo(@RequestBody PatientMemberInfo patientMemberInfo);

  /**
   * 修改患者信息
   *
   * @param patientBaseInfo 条件
   */
  @RequestMapping(value = "/api/updatePatientInfo", method = RequestMethod.POST)
  void updatePatientInfo(@RequestBody PatientBaseInfo patientBaseInfo);

  /**
   * 查询患者信息
   *
   * @param patientBaseInfo 条件
   * @return PatientBaseInfo
   */
  @RequestMapping(value = "/api/findPatientList", method = RequestMethod.POST)
  List<PatientBaseInfo> findPatientInfo(@RequestBody PatientBaseInfo patientBaseInfo);

  /**
   * 查询患者信息列表
   *
   * @param patientBaseInfo 条件
   * @return List<PatientBaseInfo>
   */
  @RequestMapping(value = "/api/findPatientInfoList", method = RequestMethod.POST)
  List<PatientBaseInfo> findPatientInfoList(@RequestBody PatientBaseInfo patientBaseInfo);

  /**
   * 根据门诊id获取病历号后六位
   *
   * @param orgId 条件
   * @return String
   */
  @RequestMapping(value = "/api/medical/{orgId}", method = RequestMethod.GET)
  String findMedicalNumberByOrgId(@PathVariable(value = "orgId") Integer orgId);

  /**
   * 根据门诊编号获取可用病历号后六位
   *
   * @param clinNum 条件
   * @return String
   */
  @RequestMapping(value = "/api/medical2/{clinNum}", method = RequestMethod.GET)
  Integer findMedicalNumberByClinNum(@PathVariable(value = "clinNum") String clinNum);

  /**
   * 会员卡充值
   *
   * @param memberRechargeModel 会员卡充值Model
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("会员卡充值")
  @PostMapping("/api/patientMember/recharge")
  ResponseResult recharge(@RequestBody MemberRechargeModel memberRechargeModel);

  /**
   * 会员卡消费
   *
   * @param model 条件
   * @return ResponseResult
   */
  @RequestMapping(value = "/api/member/expend", method = RequestMethod.POST)
  ResponseResult expend(@RequestBody MemberExpendRecordModel model);

  /**
   * 预付款充值
   *
   * @param memberRechargeModel 预付款充值Model
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("预付款充值")
  @RequestMapping(value = "/api/prepayment/recharge", method = RequestMethod.POST)
  ResponseResult recharge(@RequestBody PrepaidRechargeModel memberRechargeModel);

  /**
   * 预付款消费
   *
   * @param model 条件
   * @return ResponseResult
   */
  @RequestMapping(value = "/api/prepaid/expend", method = RequestMethod.POST)
  ResponseResult expend(@RequestBody PrepaidExpendRecordModel model);

  /**
   * 修改硬件设备密码
   *
   * @param form 条件
   */
  @RequestMapping(value = "/api/updPass", method = RequestMethod.POST)
  void updPass(@RequestBody UpdPassForm form);

  /**
   * 查询会员卡绑定信息
   *
   * @param form 条件
   * @return List<MemberInfoVo>
   */
  @RequestMapping(value = "/api/findMemberInfo", method = RequestMethod.POST)
  MemberInfoVo findMemberInfo(@RequestBody PatientMemberInfoQueryForm form);

  /**
   * 获取端口号
   *
   * @return portNumberGet
   */
  @RequestMapping(value = "/api/portNumberGet", method = RequestMethod.GET)
  String portNumberGet();

  /**
   * 会员账单退费
   *
   * @param memberBillRechargeModel 会员账单退费model
   * @return ResponseResult
   */
  @RequestMapping(value = "/api/member/billRefund", method = RequestMethod.POST)
  ResponseResult billRefund(@RequestBody MemberBillRechargeModel memberBillRechargeModel);

  /**
   * 预付款账单退费
   *
   * @param prepaidBillRechargeModel 预付款账单退费model
   * @return ResponseResult
   */
  @PostMapping("/api/prepayment/billRefund")
  ResponseResult billRefund(@RequestBody PrepaidBillRechargeModel prepaidBillRechargeModel);

  /**
   * 根据会员卡号和账单记录ID查询支付详情（外部服务调用）
   *
   * @return 返回支付详情
   */
  @ApiOperation("根据会员卡号和账单记录ID查询支付详情（外部服务调用）")
  @RequestMapping(value = "/api/member/paymentRecord", method = RequestMethod.POST)
  MemberExpendRecord memberPaymentRecordDetail(@RequestBody PaymentRecordDetailQuery query);

  /**
   * 根据账单记录ID和预付款ID查询支付记录详细（外部服务调用）
   *
   * @return 预付款支付记录
   */
  @ApiOperation("根据账单记录ID和预付款ID查询支付记录详细（外部服务调用）")
  @RequestMapping(value = "/api/prepaid/paymentRecord", method = RequestMethod.POST)
  PrepaidExpendRecord prePaidPaymentRecordDetail(@RequestBody PaymentRecordDetailQuery query);

  /**
   * 会员卡撤销收费
   *
   * @param model 参数模型
   * @return ResponseResult
   */
  @RequestMapping(value = "/api/member/revocationFee", method = RequestMethod.POST)
  ResponseResult revocationFee(@RequestBody MemberRevocationFeeModel model);

  /**
   * 预付款撤销收费
   *
   * @param model 参数模型
   * @return ResponseResult
   */
  @RequestMapping(value = "/api/prepaid/revocationFee", method = RequestMethod.POST)
  ResponseResult revocationFee(@RequestBody PrepaidRevocationFeeModel model);

  /**
   * 根据会员卡类型查询该会员卡数量
   *
   * @param memberTypeId
   * @return 会员卡大于0 返回true;否则返回false
   */
  @ApiOperation("根据会员卡类型查询该会员卡是否有在使用")
  @RequestMapping(value = "/api/member/count/{memberTypeId}", method = RequestMethod.GET)
  boolean memberInfoCount(@PathVariable(value = "memberTypeId") Integer memberTypeId);

  /**
   * 根据支付方式统计会员充值和预付款充值的金额
   *
   * @param query
   * @return
   */
  @PostMapping(value = "/api/member/sumMemberAndPrepayRechargeCash")
  BigDecimal sumMemberAndPrepayRechargeCash(@RequestBody CashReceiptOrRefundQuery query);

  @ApiOperation("根据条件查询患者全部信息")
  @RequestMapping(value = "/api/total/findPatientTotalInfo", method = RequestMethod.POST)
  List<PatientTotalInfoVo> findPatientTotalInfo(@RequestBody PatientBaseInfoQueryForm queryForm);

  /**
   * 根据条件查询预付款、会员现金退费
   *
   * @param query
   * @return
   */
  @ApiOperation("根据支付方式统计会员充值和预付款退费金额")
  @RequestMapping(value = "/api/refund/cash", method = RequestMethod.POST)
  BigDecimal sumMemberAndPrepaidRefundCash(@RequestBody @Validated CashReceiptOrRefundQuery query);

  @ApiOperation("保存公众号粉丝绑定")
  @RequestMapping (value = "/api/saveWxAndFansBind",method = RequestMethod.POST)
  Integer saveWx(@RequestBody @Validated WxFansSaveForm wxFansSaveForm);

  @ApiOperation("查询微信用户是否注册")
  @GetMapping (value = "/api/count/register")
  WxFans countRegister(@RequestParam(value = "openId", required = true) String openId);

  @ApiOperation("根据Id查询患者信息公用信息")
  @GetMapping("/api/publicInformation/{id}")
  PatientPublicInfoVo findPatientPublicInfoById(@PathVariable("id") Integer id);

  @PostMapping("/api/wxFans/query")
  WxFans getWxFans(@RequestBody WxUserQuery query);

  @PostMapping("/api/wxFans/findListByName")
  List<WxFansVo> findListByName(@RequestBody WxFanByNameForm wxFanByNameForm);

  @PostMapping("/api/wxFans/detail")
  List<WxFansDetailVO> findDetail(@RequestBody @Validated WxFansDetailForm wxFansDetailForm);

  @ApiOperation("查询微信用户信息")
  @RequestMapping (value = "/api/wx/patient/{patientId}", method = RequestMethod.GET)
  WxPatientVo getWxPatientInfo(@PathVariable("patientId") Integer patientId);

  @ApiOperation("查询微信用户信息")
  @RequestMapping (value = "/api/wx/fansId/{fansId}", method = RequestMethod.GET)
  WxFans getWxfansInfo(@PathVariable("fansId") Integer fansId);

  @ApiOperation("查询微信用户的会员卡和预付款使用记录")
  @RequestMapping (value = "/api/wx/card/record", method = RequestMethod.GET)
  public List<WxCardUseVo> listPatientCardRecord(@RequestParam(value = "cardNumber", required = true) String cardNumber
          , @RequestParam(value = "type", required = true) Integer type);

  @ApiOperation("会员卡关联查询")
  @PostMapping("/api/relatedInformation")
 MemberRelationVo findMemberBindingRelation(
          @RequestBody @Validated PatientMemberRelationQueryForm patientMemberRelationQueryForm);

  @ApiOperation("查询推送消息的绑定人")
  @RequestMapping (value = "/api/wx/pusher/{patientId}", method = RequestMethod.GET)
  WxFans getWxPushUser(@PathVariable("patientId") Integer patientId);

  @ApiOperation("批量查询推送消息的绑定人")
  @RequestMapping (value = "/api/wx/pusher/batch", method = RequestMethod.POST)
  List<WxFans> listWxPushUser(@RequestBody List<Integer> patientIds);

  @ApiOperation("条件查询自助登记患者的人数")
  @PostMapping(value = "/api/count/selfRegistrationPatient")
  Integer countSelfRegistrationPatient(@RequestBody SelfRegistrationPatientQuery patientQuery);

  @ApiOperation("保存小程序登录信息")
  @PostMapping(value = "/api/mini/fans/save")
  void saveMiniAuth(@RequestBody WxSaveFansForm form);

  @ApiOperation("保存更新微信用户信息")
  @PostMapping(value = "/api/mini/fans/modify")
  void saveOrUpdate(@RequestBody WxFans wxFans);

  @ApiOperation("获取患者生日确认日期")
  @GetMapping(value = "/api/birthday/check/{patientId}")
  Date getPatientBirthdayCheck(@PathVariable("patientId") Integer patientId);

  @ApiOperation("查询患者储蓄账号（会员卡or预付款）信息列表")
  @PostMapping("/api/member/depositAccount")
  ClinicChargeItemVO findDepositAccountList(@RequestBody @Validated PatientDepositAccountQueryForm query);

  @ApiOperation("根据订单记录id查询会员卡or预付款账户账单收费消费列表")
  @GetMapping("/api/member/billExpend/{orderRecordId}")
  List<PatientDepositAccountVO> findDepositAccountBillPayExpendList(@PathVariable(value = "orderRecordId") Integer orderRecordId);

  @ApiOperation("账单返点至会员卡账户")
  @PostMapping("/api/member/rebate")
  ResponseResult billRebate2MemberAccount(@RequestBody @Validated BillRebate2MemberAccountModel model);
}
