package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.BillRefundRecordQuery;
import com.yunya.feign.report.domain.vo.BillOfRefundRecordVO;
import com.yunya.models.report.BaseRefund;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseRefundMapper extends Mapper<BaseRefund> {

  /**
   * 根据条件查询账单退费记录列表
   *
   * @param query 查询条件
   * @return
   */
  List<BillOfRefundRecordVO> selectBillRefundRecord(@Param("query") BillRefundRecordQuery query);
}
