package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.BillPayRecordQuery;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.StatementStatisticQuery;
import com.yunya.feign.report.domain.vo.BillOfPayRecordVO;
import com.yunya.feign.report.domain.vo.TollDataStatisticsVO;
import com.yunya.models.report.BaseBillPay;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

public interface BaseBillPayMapper extends Mapper<BaseBillPay> {

  /**
   * 根据条件查询账单支付记录列表
   *
   * @param query 查询条件
   * @return list
   */
  List<BillOfPayRecordVO> selectBillRecordOfPayList(@Param("query") BillPayRecordQuery query);

  /**
   * 根据条件查询门诊收费数据总览
   *
   * @param query 查询条件
   * @return TollDataStatisticsVO
   */
  TollDataStatisticsVO selectClinicTollDataStatistic(@Param("query") DataStatisticsQuery query);

  /**
   * 根据条件查询门诊当前月账单当前月收费总额
   *
   * @param query 查询条件
   * @return
   */
  BigDecimal selectCurrentMonthTotalReceivedAmount(@Param("query") StatementStatisticQuery query);
}
