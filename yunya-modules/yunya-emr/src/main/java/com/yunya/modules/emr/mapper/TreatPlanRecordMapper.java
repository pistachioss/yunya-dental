package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.query.PlanTypeDetailQuery;
import com.yunya.feign.emr.domain.query.PlanTypeStatisticsQuery;
import com.yunya.feign.emr.domain.query.TreatPlanRecordQuery;
import com.yunya.feign.emr.domain.vo.TreatPlanTypeDetailVO;
import com.yunya.feign.emr.domain.vo.TreatPlanTypeStatisticsVO;
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

    /**
     * 条件查询治疗类型统计列表
     *
     * @param query
     * @return
     */
    List<TreatPlanTypeStatisticsVO> selectTreatPlanTypeStatistics(@Param("query") PlanTypeStatisticsQuery query);

    /**
     * 条件查询治疗计划类型明细
     *
     * @param query
     * @return
     */
    List<TreatPlanTypeDetailVO> selectTreatPlanTypeDetail(@Param("query") PlanTypeDetailQuery query);
}