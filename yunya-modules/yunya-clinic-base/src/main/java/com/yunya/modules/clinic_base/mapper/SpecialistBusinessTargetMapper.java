package com.yunya.modules.clinic_base.mapper;

import com.yunya.feign.clinic_base.domain.query.SpecialistProjectTargetQuery;
import com.yunya.feign.clinic_base.domain.vo.TargetOfMonthVO;
import com.yunya.models.clinic_base.SpecialistBusinessTarget;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpecialistBusinessTargetMapper extends Mapper<SpecialistBusinessTarget> {

  /**
   * 根据条件查询专科项目月目标列表
   *
   * @param query 查询条件
   * @return List<TargetOfMonthVO>
   */
  List<TargetOfMonthVO> selectSpecialistProjectTargetList(
      @Param("query") SpecialistProjectTargetQuery query);

  /**
   * 根据条件查询门诊专科项目目标数量
   *
   * @param dateType 日期类型 0-月，1-年
   * @param date 日期
   * @param specialistProjectId 专科项目ID
   * @param belongIds 所属门诊ID列表
   * @return Integer
   */
  Integer selectClinicSpecialistProjectWorkGoal(
      @Param("dateType") Byte dateType,
      @Param("date") String date,
      @Param("specialistProjectId") Integer specialistProjectId,
      @Param("belongIds") Integer[] belongIds);

  /**
   * 根据条件查询门诊专科项目目标数量
   *
   * @param orgId 组织ID
   * @param specialistProjectId 专科项目ID
   * @param dateRange 时间范围
   * @return Integer
   */
  Integer selectSpecialistProjectGoalCount(
      @Param("orgId") Integer orgId,
      @Param("specialistProjectId") Integer specialistProjectId,
      @Param("dateRange") List<String> dateRange);
}
