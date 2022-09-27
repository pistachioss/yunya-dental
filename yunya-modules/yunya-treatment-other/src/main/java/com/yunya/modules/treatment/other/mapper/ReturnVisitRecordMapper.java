package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.query.PatientReturnVisitQuery;
import com.yunya.feign.treatment_other.domain.vo.ReturnVisitRecordVO;
import com.yunya.models.treatment_other.ReturnVisitRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ReturnVisitRecordMapper extends Mapper<ReturnVisitRecord> {
    /**
     * 根据就诊id查询最近一次回访记录
     *
     * @param treatmentId
     * @return
     */
    List<ReturnVisitRecord> selectReturnVisitByTreatmentId(@Param("treatmentId") Integer treatmentId);

    /**
     * 根据条件查询回访记录列表
     *
     * @param query
     * @return
     */
    List<ReturnVisitRecordVO> selectReturnVisitRecordList(@Param("query") PatientReturnVisitQuery query);
}