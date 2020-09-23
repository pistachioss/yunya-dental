package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientRecommendRelationChartQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientRecommendRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.app.AppPatientArchivesVo;
import com.yunya.feign.patient_central.domain.vo.app.AppPatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WY
 */
@Repository
public interface PatientBaseInfoMapper extends Mapper<PatientBaseInfo> {

  /**
   * 通过姓名和手机号查询用户是否存在
   *
   * @param patientBaseInfoQueryForm 患者信息查询QueryFrom
   * @return PatientBaseInfoVo
   */
  PatientBaseInfoVo findUserExists(
      @Param("form") PatientBaseInfoQueryForm patientBaseInfoQueryForm);

  /**
   * 查询手机号是否纯在
   *
   * @param mobile 手机号
   * @return PatientBaseInfoVo
   */
  PatientBaseInfoVo findUserExistsByMobile(@Param("mobile") String mobile);

  /**
   * 通过用户id查询患者公共字段
   *
   * @param id 患者id
   * @return PatientPublicInfo
   */
  PatientPublicInfoVo findPatientPublicInfoById(@Param("id") Integer id);

  /**
   * 查询推荐关系
   *
   * @param form 患者推荐关系QueryForm
   * @return List<PatientRecommendRelationVo>
   */
  List<PatientRecommendRelationVo> selectListByPatientId(
      @Param("form") PatientRecommendRelationQueryForm form);

  /**
   * 根据id查询患者推荐关系拓展图
   *
   * @param form 患者关系推荐图 QueryForm
   * @return List<PatientRecommendRelationVo>
   */
  List<PatientRecommendRelationVo> findRecommendRelationById(
      @Param("form") PatientRecommendRelationChartQueryForm form);

  /**
   * 根据姓名/手机号/姓名拼音模糊查询患者
   *
   * @param form 模糊查询对象
   * @return List<PatientBaseInfoVo>
   */
  List<PatientBaseInfoVo> findPatientByNameAndMobile(@Param("form") PatientLikeFinleQueryForm form);

  /**
   * 根据患者id集合查询患者list
   *
   * @param id 患者id
   * @return List<PatientBaseInfoVo>
   */
  List<PatientBaseInfoVo> selectPatientInfoByIdList(@Param("ids") List<Integer> id);

    /**
     * 根据患者id查询患者信息
     * @param id 患者id
     * @return PatientBaseInfo
     */
  PatientBaseInfo selectPatientById(@Param("id") Integer id);

  /**
   * 根据患者姓名、手机号、门诊号 查询患者信息
   *
   * @param patientBaseInfo 患者信息
   * @return PatientBaseInfoVo
   */
  PatientBaseInfoVo selectPatientInfoByNameAndMobileAndOrgId(
      @Param("form") PatientBaseInfo patientBaseInfo);

  /**
   * 根据患者ID查询患者全部信息
   *
   * @param id 患者ID
   * @return PatientTotalInfoVo
   */
  PatientTotalInfoVo selectPatientDataById(@Param("id") Integer id);

  /**
   * 根据患者id查询患者回访所需信息
   *
   * @param id 患者id
   * @return PatientVisitInfoVo
   */
  PatientVisitInfoVo findPatientVisitInfo(@Param("id") Integer id);

  /**
   * 根据门诊id获取病历号后六位
   *
   * @param orgId 门诊id
   * @return String
   */
  String findMedicalNumberByOrgId(@Param("orgId") Integer orgId);

  /**
   * 根据患者id查询患者信息
   *
   * @param id 患者id
   * @return PatientBaseInfoVo
   */
  PatientBaseInfoVo selectOneById(Integer id);

  /**
   * 修改患者头像
   *
   * @param patientBaseInfo 患者信息
   */
  void updatePhoto(@Param("form") PatientBaseInfo patientBaseInfo);

  /**
   * 根据人员id 查询患者信息
   *
   * @param personId 人员id
   * @return PatientBaseInfo
   */
  PatientBaseInfoVo selectOneByPersonId(@Param("personId") String personId);

  /**
   * App端模糊查询患者信息
   * @param form
   * @return List<AppPatientBaseInfoVo>
   */
  List<AppPatientBaseInfoVo> appFindPatientByNameAndMobile(@Param("form") PatientLikeFinleQueryForm form);

  /**
   * App端患者档案
   * @param patientId 患者id
   * @return AppPatientArchivesVo
   */
  AppPatientArchivesVo appPatientArchives(@Param("patientId") Integer patientId);

  /**
   * 查询判断手机号是否已经存在
   * @param patientBaseInfoQueryForm 患者信息查询参数模型
   * @return List<PatientBaseInfoVo>
   */
  List<PatientBaseInfoVo> findUserExistsList(@Param("form") PatientBaseInfoQueryForm patientBaseInfoQueryForm);

  /**
   * app端查询手机号是否已经存在
   * @param mobile
   * @return
   */
  List<PatientBaseInfoVo> findUserExistsByMobileList(@Param("mobile") String mobile);
}
