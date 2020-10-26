package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseBillPay;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BaseBillPayMapper extends Mapper<BaseBillPay> {

  /**
   * 根据账单ID删除账单收费记录
   *
   * @param billId 账单ID（订单ID）
   */
  void deleteByBillId(@Param("billId") Integer billId);
}
