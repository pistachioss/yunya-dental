package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.form.CardTypeForm;
import com.yunya.feign.patient_central.domain.model.MemberBindingRelationInfoModel;
import com.yunya.feign.patient_central.domain.model.MemberRechargeModel;
import com.yunya.feign.patient_central.domain.query.PatientMemberInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientMemberRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.*;
import com.yunya.models.patient_central.PatientMemberInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientMemberInfoMapper extends Mapper<PatientMemberInfo> {
    /**
     * 根据患者id查询会员基本信息(会员卡界面基本信息（非全部信息）)
     * @param id
     * @return
     */
    MemberBaseInfoVo findMemberBaseInfo(@Param("id") Integer id);

    /**
     * 查询会员卡关联关系
     * @param form
     * @return List<PatientMemberRelationVO>
     */
    List<PatientMemberRelationVo> findMemberBindingRelation(@Param("form") PatientMemberRelationQueryForm form);

    /**
     * 根据门诊id获取病历号后六位
     * @param id
     * @return
     */
    String generateCardNumber(@Param("orgId") Integer id ,@Param("from") String tableName,@Param("memberNo") String column);

    /**
     * 根据会员卡号查询
     * @param cardNumber
     * @return PatientMemberInfo
     */
    PatientMemberInfo selectOneByCardNumber(@Param("cardNumber") String cardNumber);


    /**
     * 通过会员卡号和患者id查询会员卡信息
     * @param memberId patientId
     * @return PatientMemberInfo
     */
    PatientMemberInfo selectCardNumber(@Param("memberId") String memberId,@Param("patientId") Integer patientId);

    /**
     * 通过患者id查询会员卡全部信息
     * @param patientId
     * @return PatientMemberInfo
     */
    PatientMemberInfo selectOneByPatientId(@Param("patientId") Integer patientId);

    /**
     * 查询主卡人信息
     * @param form
     * @return MasertMemberInfoVo
     */
    MasertMemberInfoVo selectMasertMemberInfo(PatientMemberInfoQueryForm form);
}