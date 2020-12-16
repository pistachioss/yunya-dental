package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.InboundAndOutboundStatementQuery;
import com.yunya.feign.report.domain.vo.BaseAccountItemVO;
import com.yunya.feign.report.domain.vo.BoundPaymentVO;
import com.yunya.models.report.BaseAccountItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

public interface BaseAccountItemMapper extends Mapper<BaseAccountItem> {

  /**
   * 获取全部支付方式表头
   *
   * @return List<BaseAccountItemVO>
   */
  List<BaseAccountItemVO> selectAllPaymentList();

  /**
   * 根据条件查询门诊账单收费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  List<BoundPaymentVO> selectBillChargePaymentInfo(
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊收欠费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  List<BoundPaymentVO> selectCollectArrearsPaymentInfo(
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊会员卡充值的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  List<BoundPaymentVO> selectMemberChargePaymentInfo(
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊会员卡充值的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  List<BoundPaymentVO> selectPaidChargePaymentInfo(
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊产品售出的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  List<BoundPaymentVO> selectProductSoldPaymentInfo(
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊代收的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  List<BoundPaymentVO> selectClinicCollectionPaymentInfo(
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊退费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  List<BoundPaymentVO> selectBillRefundPaymentInfo(
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊会员卡退费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  List<BoundPaymentVO> selectMemberRefundPaymentInfo(
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊预付款退费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  List<BoundPaymentVO> selectPrepaidRefundPaymentInfo(
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊被代收的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  List<BoundPaymentVO> selectClinicIsAcceptedPaymentInfo(
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊账单收费会员卡/预付款本金入账金额
   *
   * @param accountItemId 支付方式ID
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectBillChargePrincipal(
      @Param("accountItemId") Integer accountItemId,
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊账单收费会员卡/预付款赠金入账金额
   *
   * @param accountItemId 支付方式ID
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectBillChargeBonus(
      @Param("accountItemId") Integer accountItemId,
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊收欠费会员卡/预付款本金入账金额
   *
   * @param accountItemId 支付方式ID
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectCollectArrearsPrincipal(
      @Param("accountItemId") Integer accountItemId,
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊收欠费会员卡/预付款赠金入账金额
   *
   * @param accountItemId 支付方式ID
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectCollectArrearsBonus(
      @Param("accountItemId") Integer accountItemId,
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊代收会员卡/预付款本金入账金额
   *
   * @param accountItemId 支付方式ID
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectClinicCollectionPrincipal(
      @Param("accountItemId") Integer accountItemId,
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊代收会员卡/预付款赠金入账金额
   *
   * @param accountItemId 支付方式ID
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectClinicCollectionBonus(
      @Param("accountItemId") Integer accountItemId,
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊账单退费会员卡/预付款本金入账金额
   *
   * @param accountItemId 支付方式ID
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectBillRefundPrincipal(
      @Param("accountItemId") Integer accountItemId,
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊账单退费会员卡/预付款赠金入账金额
   *
   * @param accountItemId 支付方式ID
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectBillRefundBonus(
      @Param("accountItemId") Integer accountItemId,
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊被代收会员卡/预付款本金入账金额
   *
   * @param accountItemId 支付方式ID
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectClinicIsAcceptedPrincipal(
      @Param("accountItemId") Integer accountItemId,
      @Param("query") InboundAndOutboundStatementQuery query);

  /**
   * 根据条件查询门诊被代收会员卡/预付款赠金入账金额
   *
   * @param accountItemId 支付方式ID
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectClinicIsAcceptedBonus(
      @Param("accountItemId") Integer accountItemId,
      @Param("query") InboundAndOutboundStatementQuery query);
}
