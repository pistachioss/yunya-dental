package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.model.MemberBindingRelationInfoModel;
import com.yunya.feign.patient_central.domain.vo.MemberRelationVo;
import com.yunya.models.patient_central.PatientMemberRelation;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientMemberRelationMapper extends Mapper<PatientMemberRelation> {
    /**
     * 根据患者id 查询患者是否有会员卡关联关系
     * @param form
     * @return List<PatientMemberRelation>
     */
    List<PatientMemberRelation> FindMemberBindingRelation(@Param("form") MemberBindingRelationInfoModel form);

    /**
     * 根据患者id删除
     * @param form
     * @return
     */
    int deleteByPatientId(@Param("form") MemberBindingRelationInfoModel form);

    /**
     * 删除关系
     * @param secondaryCardId
     * @param masterCardId
     */
    int deleteMemberRelation(@Param("masterCardId") Integer secondaryCardId,@Param("secondaryCardId") Integer masterCardId);

    /**
     * 查询关联关系是否已经存在
     * @param form
     * @return MemberRelationVo
     */
    PatientMemberRelation findBindingRelation(@Param("form") MemberBindingRelationInfoModel form);
}