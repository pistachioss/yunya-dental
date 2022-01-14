package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.vo.TreatPlanDetailVO;
import com.yunya.models.emr.TreatPlanDetailHistory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TreatPlanDetailHistoryMapper extends Mapper<TreatPlanDetailHistory> {
    /**
     * 批量添加
     *
     * @param list
     */
    void insertBatch(@Param("list") List<TreatPlanDetailHistory> list);

    List<TreatPlanDetailVO> selectTreatPlanDetailPreByPlanId(
            @Param("planId") Integer planId,
            @Param("status") Byte status);
}