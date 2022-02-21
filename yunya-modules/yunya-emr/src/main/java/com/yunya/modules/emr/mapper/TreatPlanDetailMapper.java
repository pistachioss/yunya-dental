package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.vo.TreatPlanDetailVO;
import com.yunya.models.emr.TreatPlanDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TreatPlanDetailMapper extends Mapper<TreatPlanDetail> {
    /**
     * 根据治疗计划id查询明细列表
     *
     * @param query
     * @param onlySelectStatus 是否只查询状态
     * @return
     */
    List<TreatPlanDetailVO> selectTreatPlanDetailByPlanId(
            @Param("query") TreatPlanDetail query,
            @Param("onlySelectStatus") boolean onlySelectStatus);
}