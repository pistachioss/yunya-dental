package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.PatientMemberRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.MemberBaseInfoVO;
import com.yunya.feign.patient_central.domain.vo.PatientMemberRelationVO;
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
    MemberBaseInfoVO findMemberBaseInfo(@Param("id") Integer id);

    /**
     * 查询会员卡关联关系
     * @param form
     * @return List<PatientMemberRelationVO>
     */
    List<PatientMemberRelationVO> findMemberBindingRelation(@Param("form") PatientMemberRelationQueryForm form);
}