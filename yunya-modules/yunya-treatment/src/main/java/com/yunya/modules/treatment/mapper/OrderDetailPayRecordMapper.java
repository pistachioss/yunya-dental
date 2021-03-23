package com.yunya.modules.treatment.mapper;

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
   * 根据主键id更新received_amount值
   * @param list
   */
  void uptReceivedAmountById(@Param("list") List<OrderDetailPayRecord> list);
}
