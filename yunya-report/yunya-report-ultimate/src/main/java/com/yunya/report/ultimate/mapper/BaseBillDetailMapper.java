package com.yunya.report.ultimate.mapper;

import com.yunya.feign.patient_central.domain.vo.web.ReceivedWorkloadDetailsVo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BaseBillDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/** @author YK */
public interface BaseBillDetailMapper extends Mapper<BaseBillDetail> {
  /**
   * 根据条件查询账单项目收入明细列表
   *
   * @param query 查询条件
   * @return list
   */
  List<BillTariffIncomeDetailVO> selectBillDetailIncomeList(
      @Param("query") BillDetailIncomeDetailQuery query);

  /**
   * 根据条件查询员工工作量列表（人事报表）
   *
   * @param query 查询条件
   * @return List<EmployeeWorkloadOfPersonnelVO>
   */
  List<EmployeeWorkloadOfPersonnelVO> selectEmployeeWorkloadListOfPersonnel(
      @Param("query") EmployeeWorkloadQuery query);

  /**
   * 根据条件查询员工工作量列表（运营报表）
   *
   * @param query 查询条件
   * @return List<EmployeeWorkloadOfOperationVO>
   */
  List<EmployeeWorkloadOfOperationVO> selectEmployeeWorkloadListOfOperation(
      @Param("query") EmployeeWorkloadQuery query);

  /**
   * 根据条件查询项目分类收入汇总列表
   *
   * @param query 查询条件
   * @return List<CategoryInfoIncomeVO>
   */
  List<CategoryInfoIncomeVO> selectCategoryIncomeList(
      @Param("query") BillCategoryIncomeQuery query);

  /**
   * 根据条件查询员工个人实收工作量明细列表
   *
   * @param query 查询条件
   * @return List<EmployeePersonalActualWorkloadDetailVO>
   */
  List<EmployeePersonalActualWorkloadDetailVO> selectEmployeePersonalActualWorkloadDetailList(
      @Param("query") EmployeePersonalWorkloadDetailQuery query);

  /**
   * 根据条件查询员工工作量开单明细列表
   *
   * @param query 查询条件
   * @return List<EmployeeOrderDetailWorkloadVO>
   */
  List<EmployeeOrderDetailWorkloadVO> selectEmployeeOrderDetailWorkloadList(
      @Param("query") EmployeeWorkloadDetailQuery query);

  /**
   * 根据条件查询员工个人已收工作量明细列表
   *
   * @param query 查询条件
   * @return List<EmployeePersonalReceivedWorkloadDetailVO>
   */
  List<EmployeePersonalReceivedWorkloadDetailVO> selectEmployeePersonalReceivedWorkloadDetailList(
      @Param("query") EmployeePersonalWorkloadDetailQuery query);

  /**
   * 根据条件查询员工已收工作量明细列表
   *
   * @param query 查询参数
   * @return List<EmployeeReceivedDetailWorkloadVO>
   */
  List<EmployeeReceivedDetailWorkloadVO> selectEmployeeReceivedDetailList(
      @Param("query") EmployeeWorkloadDetailQuery query);

  /**
   * 根据条件查询员工已收工作量明细列表
   *
   * @param query 查询参数
   * @return List<EmployeeReceivedDetailWorkloadVO>
   */
  List<EmployeeReceivedDetailWorkloadVO> selectEmployeeFreePaymentDetailList(
      @Param("query") EmployeeFreePaymentWorkloadDetailQuery query);

  /**
   * 根据条件查询员工补入工作量明细列表
   *
   * @param query 查询条件
   * @return List<EmployeePersonalSupplyWorkloadDetailVO>
   */
  List<EmployeePersonalSupplyWorkloadDetailVO> selectEmployeePersonalSupplyWorkloadDetailList(
      @Param("query") EmployeePersonalWorkloadDetailQuery query);

  /**
   * 根据条件查询员工补入工作量开单明细列表
   *
   * @param query 查询条件
   * @return List<EmployeeSupplyDetailWorkloadVO>
   */
  List<EmployeeSupplyDetailWorkloadVO> selectEmployeeSupplyDetailList(
      @Param("query") EmployeeWorkloadDetailQuery query);

  /**
   * 根据条件查询助手实收工作量明细列表
   *
   * @param query 查询条件
   * @return List<AssistantActualWorkloadDetailVO>
   */
  List<AssistantActualWorkloadDetailVO> selectAssistantActualWorkloadDetailList(
      @Param("query") AssistantActualWorkloadDetailQuery query);

  /**
   * 根据条件查询开单项目数量信息列表
   *
   * @param query 查询条件
   * @return List<BillingItemInfoVO>
   */
  List<BillingItemInfoVO> selectBillingItemInfoList(
      @Param("query") BillingItemStatisticsQuery query);

  /**
   * 根据条件查询开单项目统计明细列表
   *
   * @param query 查询条件
   * @return List<BillingItemDetailVO>
   */
  List<BillingItemDetailVO> selectBillingItemDetailList(
      @Param("query") BillingItemDetailQuery query);

  /**
   * 根据条件查询门诊工作量总览
   *
   * @param query 查询条件
   * @return WorkloadStatisticsVO
   */
  WorkloadStatisticsVO selectClinicWorkloadStatistic(@Param("query") DataStatisticsQuery query);

  /**
   * 根据条件查询门诊补入工作量列表
   *
   * @param query 查询条件
   * @return list
   */
  List<BillRecordWorkloadVO> selectCouponWorkloadList(@Param("query") DataStatisticsQuery query,
                                                      @Param("column") String column);

  /**
   * 根据条件查询门诊当月账单明细列表
   *
   * @param query 查询条件
   * @return List<CurrentMonthBillDetailVO>
   */
  List<CurrentMonthBillDetailVO> selectCurrentMonthBillDetail(
      @Param("query") CurrentMonthBillInfoQuery query);

  /**
   * 根据条件查询门诊当月账单当月收费记录列表
   *
   * @param query 查询条件
   * @return List<CurrentMonthBillDetailVO>
   */
  List<CurrentMonthBillPayRecordVO> selectCurrentMonthBillPayRecord(
      @Param("query") CurrentMonthBillInfoQuery query);

  /**
   * 查询账单详情
   *
   * @param billId 账单ID
   * @return
   */
  List<BaseBillDetailVO> selectBillDetailByBillId(@Param("billId") Integer billId);

  /**
   * 根据条件查询账单详情列表
   *
   * @param query
   * @return
   */
  List<BaseBillDetail> selectBillDetailByQuery(@Param("query") EmployeeWorkloadQuery query);

  /**
   * 根据账单id查询账单详情列表
   *
   * @param billIds
   * @return
   */
  List<BaseBillDetail> selectBillDetailByBillIds(@Param("billIds") Collection<Integer> billIds);

  List<EmployeeFreepaymentWorkloadDetailVO> selectEmployeeFreepaymentWorkloadDetailList(
      @Param("query") EmployeePersonalWorkloadDetailQuery query,
      @Param("billIds") Collection<Integer> billIds,
      @Param("payIds") Collection<Integer> payIds);

  /**
   * 运营综合信息
   *
   * @param query 查询条件
   * @return
   */
  List<OperationDataBusinessGoalVO> selectWorkloadCompletedList(
      @Param("query") DataStatisticsQuery query);

  /**
   * 个人工作量列表
   *
   * @param query 查询条件
   * @return
   */
  List<PersonalWorkloadVO> selectPersonalWorkloadList(@Param("query") EmployeeWorkloadQuery query);

  /**
   * 根据推荐人id和优惠时间 查询患者补入工作量总合
   *
   * @param originId 推荐人id
   * @param startDate 开始时间
   * @param endDate 结束时间
   * @return 补入工作量
   */
  BigDecimal findMakeUpWorkload(
      @Param("originId") Integer originId,
      @Param("startDate") String startDate,
      @Param("endDate") String endDate);

  BigDecimal findMakeUpWorkload(
      @Param("originId") Integer originId,
      @Param("startDate") String startDate,
      @Param("endDate") String endDate,
      @Param("originType") Integer originType);

  /**
   * 根据订单id查询项目实收金额
   *
   * @param billId 订单id
   * @param originId 推荐人id
   * @return 订单项目实收金额
   */
  List<ReceivedWorkloadDetailsVo> selectEreceiverkLoad(
      @Param("billId") Integer billId, @Param("originId") Integer originId);

  /**
   * 开单项目统计
   *
   * @param query 查询条件
   * @return
   */
  List<BillItemStatisticsVO> billItemStatistics(@Param("query") BillItemInfoQuery query);

  /**
   * 开单明细统计
   *
   * @param query 查询条件
   * @return
   */
  List<BillItemStatisticsDetailVO> billItemStatisticsDetail(
      @Param("query") BillItemDetailQuery query);

  /**
   * 根据条件查询补入工作量总和
   *
   * @param billIds 订单ID列表
   * @return BigDecimal
   */
  BigDecimal selectTotalCouponWorkload(@Param("billIds") Set<Integer> billIds);

  /**
   * 根据订单ID列表查询工作量总和
   *
   * @param billIds 订单ID列表
   * @return BigDecimal
   */
  List<BillRecordWorkloadVO> selectBillTotalWorkload(@Param("billIds") Collection<Integer> billIds);

  /**
   * 根据订单ID列表查询非工作量总和
   *
   * @param billIds 订单ID列表
   * @return BigDecimal
   */
  List<BillRecordWorkloadVO> selectBillTotalNotWorkload(@Param("billIds") Set<Integer> billIds);

  /**
   * 根据订单ID查询计算工作量订单明细
   *
   * @param billId 订单ID
   * @return list
   */
  List<BaseBillDetailToWorkloadVO> selectBillDetailForWorkload(@Param("billId") Integer billId);

  /**
   * 根据订单ID查询计算非工作量订单明细
   *
   * @param billId 订单ID
   * @return list
   */
  List<BaseBillDetailToWorkloadVO> selectBillDetailForNotWorkload(@Param("billId") Integer billId);

  /**
   * 根据查询条件产品使用报表
   *
   * @param query
   * @return
   */
  List<CouponExecutoredVO> couponExecutoredList(@Param("query") CouponExecutoredQuery query);

  /**
   * 根据条件查询产品使用报表明细
   *
   * @param query
   * @return
   */
  List<CouponExecutoredDetailVO> couponExecutoredDetails(
      @Param("query") CouponExecutoredDetailQuery query);

  /**
   * 根据条件查询开单项目工作量信息
   *
   * @param query 查询条件
   * @return list
   */
  List<BillItemTollAndWorkloadVO> selectTariffWorkloadInfo(
      @Param("query") BillItemTollAndWorkloadQuery query);

  /**
   * 统计每组开单项目的数量
   * @param query
   * @param column
   * @return
   */
  List<BillItemStatisticsVO> billItemStatisticsGroupByOrgId(@Param("query") ClinicPerformanceBusinessQuery query,
                                                            @Param("column") String column);

  List<MonthCategoryVO> monthCategoryList(@Param("query") ClinicPerformanceBusinessQuery query, @Param("cur") Integer cur);

  List<NonMonthCategoryVO> nonMonthCategoryList(@Param("query") ClinicPerformanceBusinessQuery query);
}
