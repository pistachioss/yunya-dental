package com.yunya.report.ultimate.mapper;

import com.yunya.feign.patient_central.domain.query.ReceiverkLoadQuery;
import com.yunya.feign.report.domain.query.BillPayRecordQuery;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.StatementBillChargeDetailInfoQuery;
import com.yunya.feign.report.domain.query.StatementStatisticQuery;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BaseBillPay;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

public interface BaseBillPayMapper extends Mapper<BaseBillPay> {

  /**
   * 根据账单ID查询收费记录列表
   *
   * @param billId 账单ID
   * @return List<BaseBillPayVO>
   */
  List<BaseBillPayVO> selectBillPayList(@Param("billId") Integer billId);

  /**
   * 根据条件查询账单支付记录列表
   *
   * @param query 查询条件
   * @return list
   */
  List<BillOfPayRecordVO> selectBillRecordOfPayList(@Param("query") BillPayRecordQuery query);

  /**
   * 根据条件查询门诊收费数据总览
   *
   * @param query 查询条件
   * @return TollDataStatisticsVO
   */
  TollDataStatisticsVO selectClinicTollDataStatistic(@Param("query") DataStatisticsQuery query);

  /**
   * 根据条件查询门诊当前月账单当前月收费总额
   *
   * @param query 查询条件
   * @return
   */
  BigDecimal selectCurrentMonthTotalReceivedAmount(@Param("query") StatementStatisticQuery query);

  /**
   * 根据条件查询账单收费（本月账单本月首次收费）详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  List<StatementBillChargeDetailVO> selectBillChargeDetailInfoList(
      @Param("query") StatementBillChargeDetailInfoQuery query);

  /**
   * 根据条件查询账单收欠费费（本月账单本月非首次收费）详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  List<StatementBillChargeDetailVO> selectBillCurrentChargeDebtDetailList(
      @Param("query") StatementBillChargeDetailInfoQuery query);

  /**
   * 根据条件查询账单收欠费费（非本月账单本月非首次收费）详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  List<StatementBillChargeDetailVO> selectBillOtherChargeDebtDetailList(
      @Param("query") StatementBillChargeDetailInfoQuery query);

  /**
   * 根据条件查询门诊代收（当月）账单详情列表
   *
   * @param query 查询条件
   * @return List<StatementBillCollectionDetailVO>
   */
  List<StatementBillChargeDetailVO> selectCurrentBillCollectionDetailList(
      @Param("query") StatementBillChargeDetailInfoQuery query);

  /**
   * 根据条件查询门诊代收（非当月）账单详情列表
   *
   * @param query 查询条件
   * @return List<StatementBillCollectionDetailVO>
   */
  List<StatementBillChargeDetailVO> selectOtherBillCollectionDetailList(
      @Param("query") StatementBillChargeDetailInfoQuery query);

  /**
   * 根据条件查询门诊被代收账单（当月）明细列表
   *
   * @param query 查询条件
   * @return List<StatementBillCollectionDetailVO>
   */
  List<StatementBillChargeDetailVO> selectCurrentBillIsAcceptedDetailList(
      @Param("query") StatementBillChargeDetailInfoQuery query);

  /**
   * 根据条件查询门诊被代收账单（非当月）明细列表
   *
   * @param query 查询条件
   * @return List<StatementBillCollectionDetailVO>
   */
  List<StatementBillChargeDetailVO> selectOtherBillIsAcceptedDetailList(
      @Param("query") StatementBillChargeDetailInfoQuery query);

  /**
   * 根据条件查询门诊全部已收金额（本门诊账单）
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectTotalReceivedAmount(@Param("query") DataStatisticsQuery query);

  /**
   * 根据条件查询门诊首次收费总额（本门诊账单）
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectClinicFirstReceivedAmount(@Param("query") DataStatisticsQuery query);

  /**
   * 根据条件查询本门诊收欠费收费总额（本门诊账单）
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectClinicArrearsReceivedAmount(@Param("query") DataStatisticsQuery query);

  /**
   * 根据条件查询门诊被代收收费总额（本门诊账单）
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectClinicBeCollectedReceivedAmount(@Param("query") DataStatisticsQuery query);

  /**
   * 根据条件查询收费记录ID、订单ID列表
   *
   * @param query 查询条件
   * @return list
   */
  List<BillIdAndBillPayIdVO> selectBillIdsAndBillPayIds(@Param("query") DataStatisticsQuery query);

  /**
   * 根据条件查询账单首次收费收费收费记录ID列表
   *
   * @param query 查询条件
   * @return list
   */
  List<BillIdAndBillPayIdVO> selectBillFirstPayIdList(@Param("query") DataStatisticsQuery query);

  /**
   * 根据条件查询账单收欠费收费收费记录ID列表
   *
   * @param query 查询条件
   * @return list
   */
  List<BillIdAndBillPayIdVO> selectBillArrearsPayIdList(@Param("query") DataStatisticsQuery query);

  /**
   * 根据条件查询账单被代收收费收费记录ID列表
   *
   * @param query 查询条件
   * @return list
   */
  List<BillIdAndBillPayIdVO> selectBillBeCollectedPayIdList(
      @Param("query") DataStatisticsQuery query);

  /**
   * 查询订单支付记录
   *
   * @param billIdList 账单id集合
   * @param startDate 开始时间
   * @param endDate 结束时间
   * @param itemIds 支付方式类型
   * @return 账单支付记录集合
   */
  List<BaseBillPay> findBaseBillPayInfoList(
      @Param("billIdList") List<BillIdVo> billIdList,
      @Param("startDate") String startDate,
      @Param("endDate") String endDate,
      @Param("itemIds") List<Integer> itemIds);

  /**
   * 根据订单id查询支付记录
   *
   * @param baseBillIdList 订单id集合
   * @param query 支付时间条件
   * @param itemIds 支付方式
   * @return 支付记录
   */
  List<BaseBillPay> selectBaseBillPayInfoList(
      @Param("billIdList") List<Integer> baseBillIdList,
      @Param("query") ReceiverkLoadQuery query,
      @Param("itemIds") List<Integer> itemIds);

  /**
   * 根据条件查询门诊当月免单收费金额
   *
   * @param query 查询条件
   * @return BigDecimal - 当月免单收费金额
   */
  BigDecimal selectCurrentMonthTotalFreePayAmount(@Param("query") StatementStatisticQuery query);
}
