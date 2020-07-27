package com.yunya.modules.patient_central.mapper;

import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.modules.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.modules.patient_central.domain.vo.PatientBaseInfoVo;
import com.yunya.modules.patient_central.domain.vo.PatientPublicInfoVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface PatientBaseInfoMapper extends Mapper<PatientBaseInfo> {
    /**
     * 通过姓名和手机号查询用户是否存在
     * @param patientBaseInfoQueryForm
     * @return PatientBaseInfoVo
     */
    PatientBaseInfoVo findUserExists(@Param("from") PatientBaseInfoQueryForm patientBaseInfoQueryForm);

    /**
     * 查询手机号是否纯在
     * @param mobile
     * @return Integer
     */
    Integer findUserExistsByMobile(@Param("mobile") String mobile);

    /**
     * 通过用户id查询患者公共字段
     * @param id
     * @return PatientPublicInfo
     */
    PatientPublicInfoVo findPatientPublicInfoById(@Param("id") Integer id);
}