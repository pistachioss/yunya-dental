package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BaseBillDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

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
}
