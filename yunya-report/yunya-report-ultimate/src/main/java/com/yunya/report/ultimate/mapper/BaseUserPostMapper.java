package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.EmployeeMatchingRecordQuery;
import com.yunya.feign.treatment.domain.vo.AssistantMatchingStatisticsVO;
import com.yunya.models.report.BaseUserPost;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseUserPostMapper extends Mapper<BaseUserPost> {

  /**
   * 根据条件查询配诊统计列表(按日查询)
   *
   * @param query 查询条件
   * @return List<AssistantMatchingStatisticsVO>
   */
  List<AssistantMatchingStatisticsVO> selectAssistantMatchingStatisticsListByDay(
      @Param("query") EmployeeMatchingRecordQuery query);

  /**
   * 根据条件查询配诊统计列表(按月查询)
   *
   * @param query 查询条件
   * @return List<AssistantMatchingStatisticsVO>
   */
  List<AssistantMatchingStatisticsVO> selectAssistantMatchingStatisticsListMonth(
      @Param("query") EmployeeMatchingRecordQuery query);

  /**
   * 根据条件查询配诊统计列表（按年查询）
   *
   * @param query 查询条件
   * @return List<AssistantMatchingStatisticsVO>
   */
  List<AssistantMatchingStatisticsVO> selectAssistantMatchingStatisticsListByYear(
      @Param("query") EmployeeMatchingRecordQuery query);
}
