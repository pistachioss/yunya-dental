package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.query.TreatPlanRecordQuery;
import com.yunya.models.emr.TreatPlanRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TreatPlanRecordMapper extends Mapper<TreatPlanRecord> {
    /**
     * 分页条件查询
     *
     * @param query
     * @return
     */
    List<TreatPlanRecord> selectTreatPlanRecordInfoList(@Param("query") TreatPlanRecordQuery query);
}