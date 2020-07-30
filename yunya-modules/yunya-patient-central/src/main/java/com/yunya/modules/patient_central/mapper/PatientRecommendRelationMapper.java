package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.vo.PatientRecommendRelationVo;
import com.yunya.models.patient_central.PatientRecommendRelation;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientRecommendRelationMapper extends Mapper<PatientRecommendRelation> {

}