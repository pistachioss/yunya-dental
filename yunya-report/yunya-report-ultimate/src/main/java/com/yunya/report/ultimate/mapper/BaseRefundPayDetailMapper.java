package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.vo.StatementPaymentVO;
import com.yunya.models.report.BaseRefundPayDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseRefundPayDetailMapper extends Mapper<BaseRefundPayDetail> {

  /**
   * 根据退费记录ID查询退费支付方式列表
   *
   * @param refundId 退费记录ID
   * @return List<StatementPaymentVO>
   */
  List<StatementPaymentVO> selectBillRefundPaymentList(@Param("refundId") Integer refundId);
}
