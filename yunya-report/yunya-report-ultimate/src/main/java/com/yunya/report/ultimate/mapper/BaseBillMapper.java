package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BaseBill;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBillMapper extends Mapper<BaseBill> {

  /**
   * 根据条件查询开单记录列表
   *
   * @param query 查询条件
   * @return List<BillOfOrderRecordVO>
   */
  List<BillOfOrderRecordVO> selectBillRecordOfOrderList(@Param("query") OrderRecordQuery query);

  /**
   * 欠费查询
   *
   * @param form 欠费查询
   * @param patientIds 患者id
   * @return List<ArrearsVo>
   */
  List<ArrearsVo> arrears(
      @Param("form") ArrearsQueryForm form, @Param("patientIds") List<Integer> patientIds);

  /**
   * 根据条件查询账单优惠明细列表
   *
   * @param query 查询条件
   * @return List<BillOfDiscountDetailVO>
   */
  List<BillOfDiscountDetailVO> selectBillDiscountDetailList(
      @Param("query") BillOfDiscountDetailQuery query);

  /**
   * 根据条件查询应收账款余额表
   *
   * @param query 查询条件
   * @return List<BillRestReceivableAmountVO>
   */
  List<BillRestReceivableAmountVO> selectBillReceivableAmountList(
      @Param("query") BillOfReceivableQuery query);

  ArrearsStatisticsVo selectArrears(@Param("orgId") Integer orgId);

  /**
   * 根据条件查询门诊账单数据总览
   *
   * @param query 查询条件
   * @return BillDataStatisticsVO
   */
  BillDataStatisticsVO selectClinicBillDataStatistic(@Param("query") DataStatisticsQuery query);

  /**
   * 根据条件查询患者催缴欠费列表
   *
   * @param query 查询条件
   * @return List<PatientArrearsCallForVO>
   */
  List<PatientArrearsCallForVO> selectPatientArrearsList(
      @Param("query") PatientArrearsCallForQuery query);

  /**
   * 根据患者ID查询患者欠款明细列表
   *
   * @param patientId 患者ID
   * @return List<PatientArrearsDetailVO>
   */
  List<PatientArrearsDetailVO> selectPatientArrearsDetailList(
      @Param("patientId") Integer patientId);

  /**
   * 根据条件查询医生催缴欠费列表
   *
   * @param query 查询条件
   * @return List<DentistArrearsCallForVO>
   */
  List<DentistArrearsCallForVO> selectDentistArrearsList(
      @Param("query") DentistArrearsCallForQuery query);

  /**
   * 根据条件所属查询医生催缴欠费明细列表
   *
   * @param query 查询条件
   * @return List<DentistArrearsDetailVO>
   */
  List<DentistArrearsDetailVO> selectDentistArrearsDetailList(
      @Param("query") DentistArrearsDetailQuery query);

  /**
   * 查询全部账单欠费统计
   *
   * @return BillArrearsStatisticVO
   */
  BillArrearsStatisticVO selectBillArrearsStatistic();

  /**
   * 根据条件查询本月对账单账单收支统计信息
   *
   * @param query 查询条件
   * @return StatementBillIncomeStatisticVO
   */
  StatementBillIncomeStatisticVO selectStatementStatistic(
      @Param("query") StatementStatisticQuery query);

  /**
   * 根据账单ID查询账单优惠明细
   *
   * @param billId 账单ID
   * @return 返回实体列表
   */
  BillDiscountVO selectBillDiscountDetailInfo(@Param("billId") Integer billId);

  /**
   * 根据条件查询账单实时统计
   *
   * @param query 查询条件
   * @return CurrentMonthBillStatisticVO
   */
  CurrentMonthBillStatisticVO selectRealBillStatistic(
      @Param("query") StatementStatisticQuery query);

  /**
   * 根据条件查询收欠费（使用优惠）账单列表
   *
   * @param query 查询条件
   * @return List<CurrentMonthBillCollectionDebtVO>
   */
  List<CurrentMonthBillCollectionDebtVO> selectCurrentMonthBillCollectionDebtList(
      @Param("query") CurrentMonthBillInfoQuery query);

  /**
   * 查询患者消费信息
   *
   * @param patientId 患者ID
   * @return PatientCostInfoVO
   */
  PatientCostInfoVO selectPatientCostInfo(@Param("patientId") Integer patientId);

  /**
   * 根据billId查询所有优惠的项目
   *
   * @param billId 订单ID
   * @return
   */
  List<BaseBenefitInfoVO> selectBaseBenefitInfoByBillId(@Param("billId") Integer billId);
}
