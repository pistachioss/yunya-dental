package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseBillPayDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BaseBillPayDetailMapper extends Mapper<BaseBillPayDetail> {

  /**
   * 根据账单（开单记录）ID删除账单收费记录支付明细
   *
   * @param billId 账单记录ID（开单记录ID）
   */
  void deleteByBillId(@Param("billId") Integer billId);

  /**
   * 根据账单收费记录ID删除账单收费记录支付明细
   *
   * @param billPayId 收费记录ID
   */
  void deleteByBillPayId(@Param("billPayId") Integer billPayId);
}
