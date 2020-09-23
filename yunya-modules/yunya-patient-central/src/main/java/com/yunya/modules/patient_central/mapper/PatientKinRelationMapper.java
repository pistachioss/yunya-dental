package com.yunya.modules.patient_central.mapper;

import com.yunya.models.patient_central.PatientKinRelation;
import com.yunya.feign.patient_central.domain.vo.web.PatientKinRelationVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WY
 */
@Repository
public interface PatientKinRelationMapper extends Mapper<PatientKinRelation> {

    /**
     * 根据患者id查询 患者亲属列表
     * @param id 患者id
     * @return List<PatientKinRelation>
     */
    List<PatientKinRelationVo> selectListByPatientId(@Param("id") Integer id);

    /**
     * 根据患者id 和推荐人id 查询推荐关系是否已经存在
     * @param patientKinRelation 患者推荐关系
     * @return PatientKinRelation
     */
    PatientKinRelation findPatientKinRelation(@Param("patientKinRelation") PatientKinRelation patientKinRelation);
}