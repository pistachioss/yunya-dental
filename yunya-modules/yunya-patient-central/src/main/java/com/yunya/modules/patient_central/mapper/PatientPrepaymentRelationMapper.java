package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.model.PatientPrepaymentRelationModel;
import com.yunya.feign.patient_central.domain.vo.web.PatientPrepaymentRelationVo;
import com.yunya.models.patient_central.PatientPrepaymentRelation;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * @author WY
 */
public interface PatientPrepaymentRelationMapper extends Mapper<PatientPrepaymentRelation> {

    /**
     * 查询预付款关联
     * @param masterCardId 卡主本人id
     * @return List<PatientPrepaymentRelation>
     */
    List<PatientPrepaymentRelationVo> findPrepaymentLinkList(@Param("masterCardId") Integer masterCardId);

    /**
     * 预付款关联删除
     * @param patientPrepaymentRelation 患者预付款关联
     * @return int
     */
    int deletePrepaymentRelation(@Param("form") PatientPrepaymentRelation patientPrepaymentRelation);

    /**
     * 反向查询预付款关联id
     * @param patientPrepaymentRelation 患者预付款关联
     * @return int
     */
    int selectPrepaymentRelationId(@Param("form") PatientPrepaymentRelation patientPrepaymentRelation);


    /**
     * 绑定关系查询
     * @param model 新增关联
     * @return PatientPrepaymentRelation
     */
    PatientPrepaymentRelation findBindingRelation(@Param("form") PatientPrepaymentRelationModel model);
}