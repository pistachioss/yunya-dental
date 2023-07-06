package com.yunya.modules.treatment.mapper;

import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.feign.report.domain.query.CategoryIncomeQuery;
import com.yunya.feign.treatment.domain.query.BillPayShareDetailQuery;
import com.yunya.feign.treatment.domain.vo.BillPayDetailRecordVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailInfoVO;
import com.yunya.models.treatment.BillPayDetailRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

public interface BillPayDetailRecordMapper extends Mapper<BillPayDetailRecord> {

  /**
   * 根据收费记录ID查询收费明细列表
   *
   * @param billPayRecordId 收费记录ID
   * @param inservice 是否有效
   * @return
   */
  List<BillPayDetailRecordVO> selectBillPayDetailRecord(
      @Param("billPayRecordId") Integer billPayRecordId,
      @Param("inservice") Boolean inservice);

  /**
   * 账单异常处理数据详情记录ID查询异常处理记录
   *
   * @param id 账单异常处理数据详情记录ID
   * @param inservice 是否有效
   * @return
   */
  BillPayDetailRecordVO selectPreBillPayDetailRecord(
      @Param("id") Integer id, @Param("inservice") Boolean inservice);

  /**
   * 根据支付方式统计账单的入账金额
   *
   * @param query
   * @return
   */
  BigDecimal sumBillPayAmount(@Param("query") CashReceiptOrRefundQuery query);

  /**
   * 根据条件查询门诊完成工作量
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectBusinessWorkloadCompletedCount(
      @Param("query") BusinessGoalCompletedInfoQuery query);

  /**
   * 查询支付方式收费金额
   *
   * @param billRecordId 账单记录ID
   * @param accountItemId 支付方式ID
   * @return BigDecimal
   */
  BigDecimal selectReceiptAmountOfPrepaid(
      @Param("billRecordId") Integer billRecordId, @Param("accountItemId") Integer accountItemId);

  /**
   * 根据账单ID查询免单金额
   *
   * @param billRecordId 账单ID
   * @return
   */
  BigDecimal selectTotalFreePayAmountByBillId(@Param("billRecordId") Integer billRecordId);

  /**
   * 撤销或调整免单合计
   *
   * @param payIds
   * @return
   */
  BigDecimal selectDeductionFreePayAmount(@Param("payIds") List<Integer> payIds);

  /**
   * 查询门诊账单的免单收费列表
   *
   * @param query
   * @param payIds 不属于的收费id
   * @return
   */
  List<OrderDetailInfoVO> selectBillPayDetailByFreePayment(
      @Param("query") CategoryIncomeQuery query,
      @Param("payIds") List<Integer> payIds,
      @Param("inservice") Boolean inservice);

  /**
   * 根据订单id或收费id查询收费入账明细，包含挂账0，以及调整入账方式、撤销收费
   *
   * @param query
   * @return
   */
  List<BillPayDetailRecordVO> selectBillPayDetailList(@Param("query") BillPayShareDetailQuery query);

  /**
   * 根据订单记录id查询可退入账方式列表
   *
   * @param orderRecordId
   * @return
   */
  List<BillPayDetailRecordVO> selectBillRefundableAccountItemList(
          @Param("orderRecordId") Integer orderRecordId);
}
