package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.EmployeeDiagnosisQuery;
import com.yunya.feign.report.domain.query.EmployeeMatchingRecordQuery;
import com.yunya.feign.report.domain.vo.EmployeeDiagnosisInfoVO;
import com.yunya.feign.treatment.domain.vo.AssistantMatchingStatisticsVO;
import com.yunya.models.report.BaseUserPost;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseUserPostMapper extends Mapper<BaseUserPost> {

  /**
   * 根据条件查询配诊统计列表
   *
   * @param query 查询条件
   * @return List<AssistantMatchingStatisticsVO>
   */
  List<AssistantMatchingStatisticsVO> selectAssistantMatchingStatisticsByAssistant(
      @Param("query") EmployeeMatchingRecordQuery query);

  /**
   * 根据条件查询员工看诊情况列表
   *
   * @param query 查询条件
   * @return List<EmployeeDiagnosisInfoVO>
   */
  List<EmployeeDiagnosisInfoVO> selectEmployeeDiagnosisInfoList(
      @Param("query") EmployeeDiagnosisQuery query);

  /**
   * 根据条件查询助手配诊统计列表
   *
   * @param assistant 助手
   * @param query 查询条件
   * @return List<AssistantMatchingStatisticsVO>
   */
  List<AssistantMatchingStatisticsVO> selectAssistantMatchingStatisticsByAssistant(
      @Param("assistant") String assistant, @Param("query") EmployeeMatchingRecordQuery query);
}
