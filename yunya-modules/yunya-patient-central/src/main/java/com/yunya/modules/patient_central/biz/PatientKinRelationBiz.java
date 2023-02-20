package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.form.PatientKinRelationForm;
import com.yunya.feign.patient_central.domain.model.PatientKinRelationModel;
import com.yunya.feign.patient_central.domain.query.PatientKinRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientKinRelationVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.PageUtl;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientKinRelation;
import com.yunya.modules.patient_central.mapper.PatientKinRelationMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
  /** 患者基础信息biz */
  @Autowired private PatientBaseInfoBiz patientBaseInfoBiz;

  /**
   * 根据患者id 查询患者亲属列表
   *
   * @param query 患者亲属关系信息查询QueryFrom
   * @return List<PatientKinRelation>
   */
  public PageInfo<PatientKinRelationVo> findList(PatientKinRelationQueryForm query) {
    Integer patientId = query.getPatientId();
    List<PatientKinRelationVo> resultList = patientKinRelationMapper.selectListByPatientId(patientId);
    // 转介绍人
    PatientBaseInfo introducer = patientBaseInfoBiz.findPatientIntroducerByPatientId(patientId);
    resultList = mergeIntroducer(patientId, introducer, resultList);
    return PageUtl.doPage(query, resultList);
  }

  /**
   * 将转介绍人数据合并到亲属关系列表中
   *
   * @param patientId
   * @param introducer
   * @param resultList
   */
  private List<PatientKinRelationVo> mergeIntroducer(Integer patientId, PatientBaseInfo introducer, List<PatientKinRelationVo> resultList) {
    List<PatientKinRelationVo> result = new ArrayList<>();
    // 是否需要添加到亲属关系列表：
    // 1、必须存在转介绍患者，
    // 2、亲属关系列表中不存在该转介绍患者的亲属关系记录（包括已删除的记录）
    Boolean needAddIntro = StringHelper.isNotNull(introducer);
    for (PatientKinRelationVo vo : resultList) {
      if (needAddIntro) {
        Integer introducerId = introducer.getId();
        Integer linkedPatientId = vo.getLinkedPatientId();
        if (introducerId.equals(linkedPatientId)) {
          needAddIntro = false;
          break;
        }
      }
    }
    // 过滤掉已删除记录
    resultList = resultList.stream().filter(vo->vo.getInservice()).collect(Collectors.toList());
    // 添加转介绍关系的数据（置顶）
    if (needAddIntro) {
      result.add(intro2PatientKin(patientId, introducer));
    }
    // 添加亲属关系列表
    if (StringHelper.isNotEmpty(resultList)) {
      result.addAll(resultList);
    }
    return result;
  }

  /**
   * 将转介绍患者转换成 转介绍关系
   *
   * @param patientId
   * @param introducer
   * @return
   */
  private PatientKinRelationVo intro2PatientKin(Integer patientId, PatientBaseInfo introducer) {
    PatientKinRelationVo kin = new PatientKinRelationVo();
    kin.setId(-1);
    kin.setPatientId(patientId);
    kin.setLinkedPatientId(introducer.getId());
    kin.setRelationName(introducer.getName());
    kin.setGender(introducer.getGender());
    kin.setMobile(introducer.getMobile());
    kin.setCrtTime(introducer.getCrtTime());
    kin.setInservice(true);
    return kin;
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
      mapper.insertSelective(patientKinRelation);
      PatientKinRelation linkedPatient = new PatientKinRelation();
      BeanUtils.copyProperties(patientKinRelation, linkedPatient);
      linkedPatient.setPatientId(patientKinRelation.getLinkedPatientId());
      linkedPatient.setLinkedPatientId(patientKinRelation.getPatientId());
      mapper.insertSelective(linkedPatient);
    }
    return ResponseUtil.success();
  }

  /**
   * 修改患者亲属关系
   *
   * @param patientKinRelationForm 患者亲属关系修改模板
   */
  public void update(PatientKinRelationForm patientKinRelationForm) {
    Integer id = patientKinRelationForm.getId();
    if (id != -1) {
      PatientKinRelation patientKinRelation = new PatientKinRelation();
      BeanUtils.copyProperties(patientKinRelationForm, patientKinRelation);
      patientKinRelation.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientKinRelation.setUpdName(BaseContextHandler.getName());
      patientKinRelation.setUpdTime(new Date());
      mapper.updateByPrimaryKeySelective(patientKinRelation);
      PatientKinRelation linkedPatient = new PatientKinRelation();
      linkedPatient.setPatientId(patientKinRelation.getLinkedPatientId());
      linkedPatient.setLinkedPatientId(patientKinRelation.getPatientId());
      linkedPatient.setInservice(true);
      PatientKinRelation kinRelation = mapper.selectOne(linkedPatient);
      if (kinRelation != null){
        kinRelation.setKinshipId(patientKinRelation.getKinshipId());
        kinRelation.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        kinRelation.setUpdName(BaseContextHandler.getName());
        kinRelation.setUpdTime(new Date());
        mapper.updateByPrimaryKeySelective(linkedPatient);
      }
    } else {
      // id == -1表示当前修改，是对列表中的转介绍患者进行修改
      PatientKinRelationModel model = new PatientKinRelationModel();
      model.setKinshipId(patientKinRelationForm.getKinshipId());
      model.setPatientId(patientKinRelationForm.getPatientId());
      model.setLinkedPatientId(patientKinRelationForm.getLinkedPatientId());
      model.setRemarks(patientKinRelationForm.getRemarks());
      add(model);
    }
  }

  /**
   * 根据id逻辑删除
   *
   * @param id
   */
  public void tombstone(Integer id) {
    PatientKinRelation patientKinRelation = selectById(id);
    if (patientKinRelation == null){
      throw new ClientServiceException("未查询到亲属关系", OperationCodeConstants.RETURN_MOBILE_ISNULL);
    }
    tombstone(patientKinRelation);

    PatientKinRelation linkedPatient = new PatientKinRelation();
    linkedPatient.setPatientId(patientKinRelation.getLinkedPatientId());
    linkedPatient.setLinkedPatientId(patientKinRelation.getPatientId());
    linkedPatient.setInservice(true);
    PatientKinRelation kinRelation = mapper.selectOne(linkedPatient);
    tombstone(kinRelation);
  }

  /**
   * 逻辑删除
   *
   * @param entity
   */
  private void tombstone(PatientKinRelation entity) {
    entity.setInservice(false);
    mapper.updateByPrimaryKeySelective(entity);
  }
}
