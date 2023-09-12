package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.query.QcRecommondInfoQuery;
import com.yunya.feign.treatment_other.domain.vo.QcRecommondInfoVO;
import com.yunya.models.treatment_other.QcTreatmentRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface QcTreatmentRecordMapper extends Mapper<QcTreatmentRecord> {
    List<QcRecommondInfoVO> selectMallRecommondList(@Param("query") QcRecommondInfoQuery query);
}