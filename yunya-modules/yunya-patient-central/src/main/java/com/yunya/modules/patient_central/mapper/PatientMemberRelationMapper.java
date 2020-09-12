package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.model.MemberBindingRelationInfoModel;
import com.yunya.feign.patient_central.domain.query.PatientMemberInfoQueryForm;
import com.yunya.feign.patient_central.domain.vo.SecondaryMemberInfoVo;
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

    /**
     * 查询当前患者是否已是副卡人的副卡人
     * @param form
     * @return
     */
    PatientMemberRelation findMemberBindingRelation(@Param("form") MemberBindingRelationInfoModel form);

    /**
     * 查询会员卡绑定信息
     * @param form
     * @return  List<MemberInfoVo>
     */
    List<SecondaryMemberInfoVo> findMemberInfo(@Param("form") PatientMemberInfoQueryForm form);
}