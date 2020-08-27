package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.form.CardTypeForm;
import com.yunya.feign.patient_central.domain.model.MemberBindingRelationInfoModel;
import com.yunya.feign.patient_central.domain.query.PatientMemberRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.MemberBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.MemberRelationVo;
import com.yunya.feign.patient_central.domain.vo.PatientMemberChangeLogVo;
import com.yunya.feign.patient_central.domain.vo.PatientMemberRelationVo;
import com.yunya.models.patient_central.PatientMemberInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientMemberInfoMapper extends Mapper<PatientMemberInfo> {
    /**
     * 根据患者id查询会员基本信息
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
     * 根据门诊id获取病历号后八位
     * @param id
     * @return
     */
    String generateCardNumber(@Param("orgId") Integer id ,@Param("from") String tableName,@Param("memberNo") String column);

    /**
     *
     * @param form
     */
    int changeType(CardTypeForm form);


    /**
     * 根据会员卡号查询
     * @param cardNumber
     * @return PatientMemberInfo
     */
    PatientMemberInfo selectOneByCardNumber(@Param("cardNumber") String cardNumber);


}