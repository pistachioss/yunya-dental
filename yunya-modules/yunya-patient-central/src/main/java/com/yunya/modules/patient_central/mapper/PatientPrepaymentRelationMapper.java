package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.model.PatientPrepaymentRelationModel;
import com.yunya.feign.patient_central.domain.vo.PatientPrepaymentRelationVo;
import com.yunya.models.patient_central.PatientPrepaymentRelation;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface PatientPrepaymentRelationMapper extends Mapper<PatientPrepaymentRelation> {

    /**
     * 查询预付款关联
     * @param id
     * @return List<PatientPrepaymentRelation>
     */
    List<PatientPrepaymentRelationVo> findPrepaymentLinkList(@Param("id") Integer id);

    /**
     * 预付款关联删除
     * @param patientPrepaymentRelation
     * @return
     */
    int deletePrepaymentRelation(@Param("form") PatientPrepaymentRelation patientPrepaymentRelation);

    /**
     * 绑定关系查询
     * @param model
     * @return PatientPrepaymentRelation
     */
    PatientPrepaymentRelation findBindingRelation(@Param("form") PatientPrepaymentRelationModel model);
}