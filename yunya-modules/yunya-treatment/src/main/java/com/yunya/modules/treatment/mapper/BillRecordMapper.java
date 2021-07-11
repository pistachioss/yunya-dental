package com.yunya.modules.treatment.mapper;

import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.report.domain.query.BillOfReceivableQuery;
import com.yunya.feign.report.domain.query.StatementStatisticQuery;
import com.yunya.feign.report.domain.vo.BillRestReceivableAmountVO;
import com.yunya.feign.treatment.domain.model.DebtAmountModel;
import com.yunya.feign.treatment.domain.query.CompletedWorkGoalQuery;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.models.treatment.BillRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface BillRecordMapper extends Mapper<BillRecord> {

  /**
   * 查询门诊某天最大的账单编号
   *
   * @param orgId 组织ID
   * @param date 日期
   * @return
   */
  String selectBillNumberByOrgId(@Param("orgId") Integer orgId, @Param("date") Date date);

  /**
   * 根据开单记录ID查询订单支付记录列表信息
   *
   * @param orderRecordId 开单记录ID
   * @return
   */
  List<BillPayRecordVO> selectBillPayRecord(@Param("orderRecordId") Integer orderRecordId);

  /**
   * 通过患者ID批量查询患者欠费总额
   *
   * @param patientIds 患者ID集合
   * @return 返回欠费总额集合
   */
  List<DebtAmountModel> selectPatientDebtAmountList(@Param("patientIds") List<Integer> patientIds);

  /**
   * 根据患者ID查询患者账单统计数据
   *
   * @param patientId 患者ID
   * @return
   */
  PatientBillStatistics selectPatientBillStatistics(
      @Param("patientId") Integer patientId, @Param("payIds") Collection<Integer> payIds);

  /**
   * 根据患者ID查询患者账单统计数据列表
   *
   * @param patientIds 患者ID集合
   * @return 实体数据列表
   */
  List<PatientBillStatistics> selectPatientBillStatisticsByPatientIds(
      @Param("patientIds") List<Integer> patientIds);

  /**
   * 根据患者就诊记录ID查询订单支付记录
   *
   * @param treatmentIds 患者就诊记录ID
   * @return 实例对象集合
   */
  List<BillRecord> selectBillRecordsByTreatmentIds(
      @Param("treatmentIds") List<Integer> treatmentIds);

  /**
   * PC照片影像小程序已结账列表
   *
   * @param currentDate 当前时间
   * @param orgId 门诊ID
   * @return 返回实体列表
   */
  List<DesktopMiniProgramVO> desktopBillingList(
      @Param("currentDate") String currentDate, @Param("orgId") Integer orgId);

  /**
   * 根据就诊ID查询账单和订单信息
   *
   * @param treatmentId 就诊ID
   * @return 返回实体
   */
  OrderBill4AppVO findOrderAndBill4App(@Param("treatmentId") Integer treatmentId);

  /**
   * 查询完成实收金额
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectCompletedActualReceivedAmount(@Param("query") CompletedWorkGoalQuery query);

  /**
   * 查询完成工作量
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectCompletedWorkloadAmount(@Param("query") CompletedWorkGoalQuery query);

  /**
   * 根据条件查询门诊完成营业收入
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectBusinessIncomeCompletedCount(
      @Param("query") BusinessGoalCompletedInfoQuery query);

  /**
   * 根据账单ID列表查询账单记录
   *
   * @param patientId 患者ID
   * @param billRecordIds 账单ID列表
   * @return list
   */
  List<PatientBillPrintInfoVO> selectBillDetailListByIds(
      @Param("patientId") Integer patientId, @Param("billRecordIds") Integer[] billRecordIds);

  /**
   * 根据条件查询应收账款余额列表
   *
   * @param query 查询条件
   * @return list
   */
  List<BillRestReceivableAmountVO> selectDebtList(@Param("query") BillOfReceivableQuery query);

  /**
   * 撤销优惠合计
   *
   * @param query
   * @return
   */
  BigDecimal selectBillAdjustDiscountAmount(@Param("query") StatementStatisticQuery query);

  /**
   * 优惠合计
   *
   * @param query
   * @return
   */
  BigDecimal selectBillDiscountAmount(@Param("query") StatementStatisticQuery query);

  /**
   * 撤销原价合计
   *
   * @param query
   * @return
   */
  BigDecimal selectBillAdjustOriginalAmount(@Param("query") StatementStatisticQuery query);

  /**
   * 原价合计
   *
   * @param query
   * @return
   */
  BigDecimal selectBillOriginalAmount(@Param("query") StatementStatisticQuery query);

  /**
   * 撤销实收合计
   *
   * @param query
   * @return
   */
  BigDecimal selectBillRevokeReceivedAmount(@Param("query") StatementStatisticQuery query);

  /**
   * 实收合计
   *
   * @param query
   * @return
   */
  BigDecimal selectBillReceivedAmount(@Param("query") StatementStatisticQuery query);

  /**
   * 免单合计
   *
   * @param query
   * @return
   */
  BigDecimal selectBillFreePayAmount(@Param("query") StatementStatisticQuery query);
}
