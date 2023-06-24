package com.yunya.modules.treatment.mapper;

import com.yunya.feign.report.domain.query.CurrentMonthBillInfoQuery;
import com.yunya.feign.report.domain.vo.CurrentMonthBillDetailVO;
import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.models.treatment.OrderDetailPayRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

public interface OrderDetailPayRecordMapper extends Mapper<OrderDetailPayRecord> {

  /**
   * 查询账单优惠为0的明细总额
   *
   * @param billRecordId 账单记录ID
   * @return BigDecimal
   */
  BigDecimal selectNoDiscountAmount(@Param("billRecordId") Integer billRecordId);

  /**
   * 根据条件查询门诊当月账单明细列表
   *
   * @param query 查询条件
   * @return
   */
  List<CurrentMonthBillDetailVO> selectCurrentMonthBillDetail(
      @Param("query") CurrentMonthBillInfoQuery query);

  /**
   * 批量插入开单明细收费记录
   *
   * @param orderDetailPayRecords 开单明细收费记录
   */
  void batchInsert(@Param("list") List<OrderDetailPayRecord> orderDetailPayRecords);

  /**
   * 根据订单id查询订单在billPayId截止之前的项目收费明细
   *
   * @param orderRecordId
   * @param billPayId 查询条件同时传递值到返回列表中
   * @return
   */
  List<BillPayShareDetailVO> selectItemPayDetailDeadlineBillPayId(
          @Param("orderRecordId") Integer orderRecordId,
          @Param("billPayId") Integer billPayId);

  /**
   * 根据订单id统计并更新项目总已收（含免单）、总免单
   *
   * @param orderRecordId
   */
  void statOrderDetailPayItemTotal(@Param("orderRecordId") Integer orderRecordId);
}
