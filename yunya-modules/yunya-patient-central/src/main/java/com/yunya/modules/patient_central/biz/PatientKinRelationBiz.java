package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.form.PatientKinRelationForm;
import com.yunya.feign.patient_central.domain.model.PatientKinRelationModel;
import com.yunya.feign.patient_central.domain.query.PatientKinRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientKinRelationVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.PatientKinRelation;
import com.yunya.modules.patient_central.mapper.PatientKinRelationMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 简单介绍:</br> 患者亲属关系 业务层
 *
 * @author: WY
 * @date 2020/7/28 20:56
 * @description: 患者亲属关系 增删查改
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientKinRelationBiz extends BaseBiz<PatientKinRelationMapper, PatientKinRelation> {

  /** 注入患者亲属Mapper */
  @Autowired private PatientKinRelationMapper patientKinRelationMapper;

  /**
   * 根据患者id 查询患者亲属列表
   *
   * @param form 患者亲属关系信息查询QueryFrom
   * @return List<PatientKinRelation>
   */
  public PageInfo<PatientKinRelationVo> findList(PatientKinRelationQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<PatientKinRelationVo> resultList =
        patientKinRelationMapper.selectListByPatientId(form.getPatientId());
    return new PageInfo<>(resultList);
  }

  /**
   * 添加患者亲属关系
   *
   * @param patientKinRelationModel 患者亲属关系Model
   * @return ResponseResult
   */
  public ResponseResult add(PatientKinRelationModel patientKinRelationModel) {
    if (patientKinRelationModel.getPatientId().equals(patientKinRelationModel.getLinkedPatientId())){
      return ResponseUtil.fail(OperationCodeConstants.OPERATION_NOT_ALLOW, "不可添加自己", null);
    }
    PatientKinRelation patientKinRelation = new PatientKinRelation();
    BeanUtils.copyProperties(patientKinRelationModel, patientKinRelation);
    PatientKinRelation patientKinRelationvo =
        patientKinRelationMapper.findPatientKinRelation(patientKinRelation);
    if (patientKinRelationvo != null) {
      return ResponseUtil.fail(OperationCodeConstants.DATA_EXIST, "患者关系已存在", patientKinRelationvo);
    }
    if (patientKinRelationModel.getId() == null) {
      patientKinRelation.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientKinRelation.setCrtName(BaseContextHandler.getName());
      patientKinRelation.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      mapper.insert(patientKinRelation);
      PatientKinRelation linkedPatient = new PatientKinRelation();
      BeanUtils.copyProperties(patientKinRelation, linkedPatient);
      linkedPatient.setPatientId(patientKinRelation.getLinkedPatientId());
      linkedPatient.setLinkedPatientId(patientKinRelation.getPatientId());
      mapper.insert(linkedPatient);
    }
    return ResponseUtil.success();
  }

  /**
   * 修改患者亲属关系
   *
   * @param patientKinRelationForm 患者亲属关系修改模板
   */
  public void update(PatientKinRelationForm patientKinRelationForm) {
    PatientKinRelation patientKinRelation = new PatientKinRelation();
    BeanUtils.copyProperties(patientKinRelationForm, patientKinRelation);
    patientKinRelation.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientKinRelation.setUpdName(BaseContextHandler.getName());
    patientKinRelation.setUpdTime(new Date());
    mapper.updateByPrimaryKeySelective(patientKinRelation);
    PatientKinRelation linkedPatient = new PatientKinRelation();
    linkedPatient.setPatientId(patientKinRelation.getLinkedPatientId());
    linkedPatient.setLinkedPatientId(patientKinRelation.getPatientId());
    PatientKinRelation kinRelation = mapper.selectOne(linkedPatient);
    if (kinRelation != null){
      kinRelation.setKinshipId(patientKinRelation.getKinshipId());
      kinRelation.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      kinRelation.setUpdName(BaseContextHandler.getName());
      kinRelation.setUpdTime(new Date());
      mapper.updateByPrimaryKeySelective(linkedPatient);
    }
  }
}
