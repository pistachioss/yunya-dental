package com.yunya.modules.patient_central.mapper;

import com.yunya.models.patient_central.PatientKinRelation;
import com.yunya.feign.patient_central.domain.vo.PatientKinRelationVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientKinRelationMapper extends Mapper<PatientKinRelation> {

    /**
     * 根据患者id查询 患者亲属列表
     * @param id
     * @return List<PatientKinRelation>
     */
    List<PatientKinRelationVo> selectListByPatientId(@Param("id") Integer id);
}