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
}
