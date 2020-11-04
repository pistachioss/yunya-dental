package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.BillPayRecordQuery;
import com.yunya.feign.report.domain.vo.BillOfPayRecordVO;
import com.yunya.models.report.BaseBillPay;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBillPayMapper extends Mapper<BaseBillPay> {

  /**
   * 根据条件查询账单支付记录列表
   *
   * @param query 查询条件
   * @return list
   */
  List<BillOfPayRecordVO> selectBillRecordOfPayList(@Param("query") BillPayRecordQuery query);
}
