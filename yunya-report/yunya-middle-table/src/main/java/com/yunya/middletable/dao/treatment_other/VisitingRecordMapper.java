package com.yunya.middletable.dao.treatment_other;

import com.yunya.feign.treatment_other.domain.query.VisitingContentAfterCurrentQuery;
import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import com.yunya.models.treatment_other.VisitingRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Repository
public interface VisitingRecordMapper extends Mapper<VisitingRecord> {

    /**
     * 根据随访id查询随访记录（附带后续随访个数）
     * @return  随访记录
     */
    VisitingRecordVo findVisitingRecordById(@Param("id") Integer id);

    /**
     * 根据条件查询随访列表
     * @param query 查询条件
     * @return List<VisitingRecordVo>
     */
    List<VisitingRecordVo> findVisitingRecordByCondition(@Param("query") VisitingRecordQuery query);

    /**
     * 根据患者id、时间查询随访列表
     * @param query 查询参数
     * @return  随访列表
     */
    List<VisitingRecord> findAfterVisitingContentByPatientIdAndDate(
            @Param("query")VisitingContentAfterCurrentQuery query);

    /**
     * 根据就诊记录ID删除随访
     * @param treatmentId 就诊记录ID
     * @return
     */
    void deleteVisitingRecordByTreatmentId(@Param("treatmentId") Integer treatmentId);
}