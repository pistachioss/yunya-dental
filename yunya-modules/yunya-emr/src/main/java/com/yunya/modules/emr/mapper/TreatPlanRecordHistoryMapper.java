package com.yunya.modules.emr.mapper;

import com.yunya.models.emr.TreatPlanRecord;
import com.yunya.models.emr.TreatPlanRecordHistory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface TreatPlanRecordHistoryMapper extends Mapper<TreatPlanRecordHistory> {
    /**
     * 上一次变更记录
     *
     * @param planId
     * @param status
     * @return
     */
    TreatPlanRecord selectTreatPlanHistoryPreOneById(
            @Param("planId") Integer planId,
            @Param("status") Byte status);
}