package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseBillDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

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

  List<BaseBillDetail> groupBillDetailByDateAndExecutorId(
          @Param("orgId") Integer orgId,
          @Param("billDate") Integer billDate,
          @Param("payeeDate") Integer payeeDate,
          @Param("executorIds") List<Integer> executorIds);
}
