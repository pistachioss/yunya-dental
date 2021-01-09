package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.StatementStatisticQuery;
import com.yunya.feign.report.domain.vo.CurrentMonthBillStatisticVO;
import com.yunya.models.report.CurrentMonthBillStatistics;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface CurrentMonthBillStatisticsMapper extends Mapper<CurrentMonthBillStatistics> {

  /**
   * 根据条件查询账单存档统计
   *
   * @param query 查询条件
   * @return CurrentMonthBillStatisticVO
   */
  CurrentMonthBillStatisticVO selectCurrentMonthBillStatistics(
      @Param("query") StatementStatisticQuery query);
}
