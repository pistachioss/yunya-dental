package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.vo.TreatPlanStepVO;
import com.yunya.models.emr.TreatPlanStep;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.List;

public interface TreatPlanStepMapper extends Mapper<TreatPlanStep> {
    /**
     * 根据治疗计划id查询治疗计划步骤列表
     *
     * @param planIds
     * @return
     */
    List<TreatPlanStepVO> selectTreatPlanStepByPlanId(@Param("planIds") Collection<Integer> planIds);
}