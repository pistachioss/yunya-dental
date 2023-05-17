package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.PatientRecommendRelationChartQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientRecommendRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientKinRecomVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientRecommendRelationVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.patient_central.PatientRecommendRelation;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientRecommendRelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简单介绍:</br> 患者推荐关系 业务层
 *
 * @author: WY
 * @date 2020/7/29 13:29
 * @description: 患者推荐关系管理
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientRecommendRelationBiz
    extends BaseBiz<PatientRecommendRelationMapper, PatientRecommendRelation> {

  @Autowired private PatientBaseInfoMapper patientBaseInfoMapper;

  /**
   * 查询患者推荐关系
   *
   * @param form 患者推荐关系QueryForm
   * @return List<PatientRecommendRelationVo>
   */
  public PageInfo<PatientRecommendRelationVo> findList(PatientRecommendRelationQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<PatientRecommendRelationVo> resultList = patientBaseInfoMapper.selectListByPatientId(form);
    return new PageInfo<>(resultList);
  }

  public List<PatientKinRecomVo> findAll() {
    List<PatientKinRecomVo> resultList = patientBaseInfoMapper.selectKinRecomByPatientId();
    return resultList;
  }

  /**
   * 根据id查询患者推荐关系拓展图
   *
   * @param form 患者关系推荐图
   * @return List<PatientRecommendRelationVo>
   */
  public List<PatientRecommendRelationVo> findRecommendRelationById(
      PatientRecommendRelationChartQueryForm form) {
    return patientBaseInfoMapper.findRecommendRelationById(form);
  }
}
