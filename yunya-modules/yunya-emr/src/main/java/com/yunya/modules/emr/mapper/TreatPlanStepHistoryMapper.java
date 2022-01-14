package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.vo.TreatPlanStepVO;
import com.yunya.models.emr.TreatPlanStepHistory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TreatPlanStepHistoryMapper extends Mapper<TreatPlanStepHistory> {
    /**
     * 批量添加
     * @param list
     */
    void insertBatch(@Param("list") List<TreatPlanStepHistory> list);

    List<TreatPlanStepVO> selectTreatPlanStepPreByPlanId(
            @Param("planId") Integer planId,
            @Param("status") Byte status);
}