package com.yunya.feign.patient_central;

import com.yunya.feign.patient_central.domain.form.UpdPassForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.feign.patient_central.domain.vo.web.MemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.patient_central.factory.RemotePatientCentralServiceFallBackFactory;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.patient_central.MemberExpendRecord;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientMemberInfo;
import com.yunya.models.patient_central.PrepaidExpendRecord;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/** @author YK */
@FeignClient(
    name = YunyaServiceNameConstants.YUNYA_PATIENT,
    fallbackFactory = RemotePatientCentralServiceFallBackFactory.class)
    public interface RemotePatientCentralServiceFeign {

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
  @RequestMapping (value = "/saveWxAndFansBind",method = RequestMethod.POST)
  Integer saveWx(@RequestBody @Validated WxFansSaveForm wxFansSaveForm);
}
