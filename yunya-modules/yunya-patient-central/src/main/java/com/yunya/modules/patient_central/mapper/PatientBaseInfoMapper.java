package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientRecommendRelationChartQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientRecommendRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.*;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientBaseInfoMapper extends Mapper<PatientBaseInfo> {
    /**
     * 通过姓名和手机号查询用户是否存在
     * @param patientBaseInfoQueryForm
     * @return PatientBaseInfoVo
     */
    PatientBaseInfoVo findUserExists(@Param("form") PatientBaseInfoQueryForm patientBaseInfoQueryForm);

    /**
     * 查询手机号是否纯在
     * @param mobile
     * @return PatientBaseInfoVo
     */
    PatientBaseInfoVo findUserExistsByMobile(@Param("mobile") String mobile);

    /**
     * 通过用户id查询患者公共字段
     * @param id
     * @return PatientPublicInfo
     */
    PatientPublicInfoVo findPatientPublicInfoById(@Param("id") Integer id);

    /**
     * 根据患者id查询患者资料
     * @param id
     * @return PatientExtendInfoModel
     */
    //PatientExtendInfoVo findPatientDate(Integer id);

    /**
     * 查询推荐关系
     * @param form
     * @return List<PatientRecommendRelationVo>
     */
    List<PatientRecommendRelationVo> selectListByPatientId(@Param("form") PatientRecommendRelationQueryForm form);

    /**
     * 根据id查询患者推荐关系拓展图
     * @param form
     * @return List<PatientRecommendRelationVo>
     */
    List<PatientRecommendRelationVo> findRecommendRelationById(@Param("form") PatientRecommendRelationChartQueryForm form);

    /**
     * 根据姓名/手机号/姓名拼音模糊查询患者
     * @param form
     * @return List<PatientBaseInfoVo>
     */
    List<PatientBaseInfoVo> findPatientByNameAndMobile(@Param("form") PatientLikeFinleQueryForm form);

    /**
     * 根据患者id集合查询患者list
     * @param id
     * @return List<PatientBaseInfoVo>
     */
    List<PatientBaseInfoVo> selectPatientInfoByIdList(@Param("ids") List<Integer> id);

    /**
     * 根据患者id查询患者信息
     * @param id
     */
    PatientBaseInfo selectPatientById(@Param("id") Integer id);

    /**
     * 根据患者姓名、手机号、门诊号 查询患者信息
     * @param patientBaseInfo
     * @return PatientBaseInfoVo
     */
    PatientBaseInfoVo selectPatientInfoByNameAndMobileAndOrgId(@Param("form") PatientBaseInfo patientBaseInfo);

    /**
     * 根据患者ID查询患者全部信息
     * @param id 患者ID
     * @return PatientTotalInfoVo
     */
    PatientTotalInfoVo selectPatientDataById(@Param("id") Integer id);

    /**
     * 根据患者id查询患者回访所需信息
     * @return PatientVisitInfoVo
     */
    PatientVisitInfoVo findPatientVisitInfo(@Param("id") Integer id);


    /**
     * 根据门诊id获取病历号后六位
     *
     * @param orgId
     * @return String
     */
    String findMedicalNumberByOrgId(@Param("orgId") Integer orgId);

}