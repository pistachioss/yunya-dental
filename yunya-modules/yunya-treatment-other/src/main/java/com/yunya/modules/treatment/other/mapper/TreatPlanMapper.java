package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.query.TreatPlanQuery;
import com.yunya.feign.treatment_other.domain.vo.TreatPlanVO;
import com.yunya.models.treatment_other.TreatPlan;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TreatPlanMapper extends Mapper<TreatPlan> {
    /**
     * 根据条件查询治疗计划列表
     *
     * @param query
     * @return
     */
    List<TreatPlanVO> selectTreatPlanList(@Param("query") TreatPlanQuery query);
}