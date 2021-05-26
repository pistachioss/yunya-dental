package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.model.MemberBindingRelationInfoModel;
import com.yunya.feign.patient_central.domain.query.PatientMemberInfoQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientCardOwnerInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.SecondaryMemberInfoVo;
import com.yunya.models.patient_central.PatientMemberRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WY
 */
@Repository
public interface PatientMemberRelationMapper extends Mapper<PatientMemberRelation> {
    /**
     * 根据患者id 查询患者是否有会员卡关联关系
     * @param form 会员卡关联关系
     * @return List<PatientMemberRelation>
     */
    List<PatientMemberRelation> FindMemberBindingRelation(@Param("form") MemberBindingRelationInfoModel form);

    /**
     * 根据患者id删除
     * @param form 会员卡关联关系
     * @return int
     */
    int deleteByPatientId(@Param("form") MemberBindingRelationInfoModel form);

    /**
     * 删除关系
     * @param secondaryCardId 副卡人id
     * @param masterCardId 主卡人id
     * @return int
     */
    int deleteMemberRelation(@Param("masterCardId") Integer secondaryCardId,@Param("secondaryCardId") Integer masterCardId,@Param("bindType") Integer bindType);

    /**
     * 查询关系id
     * @param secondaryCardId 副卡人id
     * @param masterCardId 主卡人id
     * @param bindType 绑定类型
     * @return int
     */
    int selectMemberRelationId(@Param("masterCardId") Integer secondaryCardId,@Param("secondaryCardId") Integer masterCardId,@Param("bindType") Integer bindType);

    /**
     * 查询关联关系是否已经存在
     * @param form 会员卡关联关系
     * @return MemberRelationVo
     */
    PatientMemberRelation findBindingRelation(@Param("form") MemberBindingRelationInfoModel form);

    /**
     * 查询当前患者是否已是副卡人的副卡人
     * @param form 会员卡关联关系
     * @return PatientMemberRelation
     */
    PatientMemberRelation findMemberBindingRelation(@Param("form") MemberBindingRelationInfoModel form);

    /**
     * 以副卡人身份查询主卡人集合
     * @param form 查询患者会员信息form
     * @return  List<MemberInfoVo>
     */
    List<SecondaryMemberInfoVo> findMemberInfo(@Param("form") PatientMemberInfoQueryForm form);

    /**
     * 根据患者id查询已绑定主卡信息
     * @param patientId 患者id
     * @return 已绑定主卡信息
     */
    List<PatientCardOwnerInfoVo> findPatientCardOwnerInfo(@Param("patientId") Integer patientId);

    /**
     * 查询是否存在绑定关系
     * @param masterCardId 主卡人id
     * @param patientId 副卡人id
     * @return 绑定关系
     */
    List<PatientMemberRelation> isBindMember(@Param("masterCardId") String masterCardId, @Param("patientId") Integer patientId);
}