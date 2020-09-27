package com.yunya.modules.treatment.mapper;

import com.yunya.models.treatment.BillPayRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BillPayRecordMapper extends Mapper<BillPayRecord> {

  /**
   * 根据账单记录ID查询最早的账单支付记录
   *
   * @param billRecordId 账单记录ID
   * @return
   */
  BillPayRecord selectEarliestBillPayRecord(@Param("billPayRecordId") Integer billRecordId);
}
