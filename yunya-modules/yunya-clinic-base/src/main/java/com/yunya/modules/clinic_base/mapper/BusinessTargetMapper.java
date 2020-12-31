package com.yunya.modules.clinic_base.mapper;

import com.yunya.feign.clinic_base.domain.query.BusinessTargetQuery;
import com.yunya.feign.clinic_base.domain.query.BusinessWorkGoalQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessWorkGoalVO;
import com.yunya.feign.clinic_base.domain.vo.TargetOfMonthVO;
import com.yunya.models.clinic_base.BusinessTarget;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BusinessTargetMapper extends Mapper<BusinessTarget> {

  /**
   * 根据条件查询月业务目标列表
   *
   * @param query 查询条件
   * @return List<BusinessTargetOfMonthVO>
   */
  List<TargetOfMonthVO> selectBusinessTargetList(@Param("query") BusinessTargetQuery query);

  /**
   * 根据条件查询门诊业务目标列表
   *
   * @param query 查询条件
   * @return List<BusinessWorkGoalVO>
   */
  List<BusinessWorkGoalVO> selectBusinessWorkGoalList(@Param("query") BusinessWorkGoalQuery query);
}
