package com.yunya.report.ultimate.mapper;

import com.yunya.models.report.BaseRefundDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Set;

public interface BaseRefundDetailMapper extends Mapper<BaseRefundDetail> {
  /**
   * 退费工作量总和
   *
   * @param billIds 订单ID列表
   * @return BigDecimal
   */
  BigDecimal selectTotalRefundWorkload(@Param("billIds") Set<Integer> billIds);
}
