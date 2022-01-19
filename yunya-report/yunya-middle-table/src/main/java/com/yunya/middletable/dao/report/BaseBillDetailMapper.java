package com.yunya.middletable.dao.report;

import com.yunya.feign.report.domain.query.StatisticsEmployeeQueryForm;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.models.report.BaseBillDetail;
import com.yunya.models.report.BaseBillPayDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.List;

public interface BaseBillDetailMapper extends Mapper<BaseBillDetail> {

  /**
   * 根据账单ID删除账单明细
   *
   * @param billId 账单ID
   */
  void deleteByBillId(@Param("billId") Integer billId);

  /**
   * 批量插入中间表订单明细
   *
   * @param baseBillDetails 订单明细列表
   */
  void batchInsertSelective(@Param("baseBillDetails") List<BaseBillDetail> baseBillDetails);

  List<BillExecutorItemVO> selectBillDetailByDateAndExecutorId(
          @Param("orgId") Integer orgId,
          @Param("billDate") Integer billDate,
          @Param("payeeDate") Integer payeeDate,
          @Param("privilegeDate") Integer privilegeDate,
          @Param("executorIds") Collection<Integer> executorIds);

  List<BaseBillPayDetail> selectFreePaymentAmountByDate(
          @Param("orgId") Integer orgId,
          @Param("payeeDate") Integer payeeDate,
          @Param("executorIds") Collection<Integer> executorIds);

  List<BillExecutorItemVO> groupBillItemDetailListByDate(
          @Param("startDate") String startDate,
          @Param("endDate") String endDate,
          @Param("dateInt") int dateInt);

  List<BillExecutorItemVO> groupBillItemDetailListByPayDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

  List<BillExecutorItemVO> selectFreePaymentAmount(@Param("query")StatisticsEmployeeQueryForm query);
}
