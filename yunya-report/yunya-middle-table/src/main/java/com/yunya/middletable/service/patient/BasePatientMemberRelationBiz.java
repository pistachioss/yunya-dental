package com.yunya.middletable.service.patient;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.patient.PatientMemberRelationMapper;
import com.yunya.middletable.dao.patient.PatientPrepaymentRelationMapper;
import com.yunya.middletable.dao.report.BasePatientMemberRelationMapper;
import com.yunya.models.middletable.BasePatientMember;
import com.yunya.models.middletable.BasePatientMemberRelation;
import com.yunya.models.patient_central.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 简介: 报表服务患者会员/预付款关联关系同步
 *
 * @author: WY
 * @date: 2020/10/16 16:15
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BasePatientMemberRelationBiz
    extends BaseBiz<BasePatientMemberRelationMapper, BasePatientMemberRelation> {

  /** 注入会员关联关系mapper */
  @Autowired PatientMemberRelationMapper patientMemberRelationMapper;

  /** 注入预付款关联关系mapper */
  @Autowired PatientPrepaymentRelationMapper patientPrepaymentRelationMapper;

  /**
   * 患者会员/预付款信息操作
   *
   * @param msg 消息
   */
  public void operate(MessageModel msg) {
    Integer operateType =  msg.getOperateType();
    Integer id = (Integer) msg.getParamMap().get("id");
    Integer type = (Integer) msg.getParamMap().get("type");
    switch (operateType) {
      case 0:
        addPatientMemberInfo(msg);
        break;
      case 1:
        BasePatientMemberRelation patientMemberInfo = getPatientMemberRelationInfo(id, type);
        if (StringHelper.isNotNull(patientMemberInfo)) {
          mapper.updateByPrimaryKeySelective(patientMemberInfo);
        }
        break;
      case 2:
        if (type == 0){
          BasePatientMemberRelation memberRelation = getPatientMemberRelationInfo(id, type);
          PatientMemberRelation patientMemberRelation = patientMemberRelationMapper.selectByPrimaryKey(id);
          if (StringHelper.isNotNull(patientMemberRelation) && StringHelper.isNotNull(memberRelation)){
              mapper.delete(memberRelation);
              mapper.insert(memberRelation);
          }
          mapper.delete(memberRelation);
        }
        if (type == 1){
          BasePatientMemberRelation memberRelation = getPatientMemberRelationInfo(id, type);
          PatientPrepaymentRelation patientPrepaymentRelation = patientPrepaymentRelationMapper.selectByPrimaryKey(id);
          if (StringHelper.isNotNull(patientPrepaymentRelation) && StringHelper.isNotNull(memberRelation)){
            mapper.delete(memberRelation);
            mapper.insert(memberRelation);
          }
          mapper.delete(memberRelation);
        }
        break;
      default:
        break;
    }
  }

  private void addPatientMemberInfo(MessageModel msg) {
    Integer id = (Integer) msg.getParamMap().get("id");
    Integer type = (Integer) msg.getParamMap().get("type");
    if (type == 0) {
      PatientMemberRelation patientMemberRelation =
          patientMemberRelationMapper.selectByPrimaryKey(id);
      if (StringHelper.isNotNull(patientMemberRelation)) {
        BasePatientMemberRelation basePatientMemberRelation = new BasePatientMemberRelation();
        basePatientMemberRelation.setRelationId(patientMemberRelation.getId());
        basePatientMemberRelation.setMasterCardId(patientMemberRelation.getMasterCardId());
        basePatientMemberRelation.setSecondaryCardId(patientMemberRelation.getSecondaryCardId());
        basePatientMemberRelation.setType((byte)type.intValue());
        basePatientMemberRelation.setBindType(patientMemberRelation.getBindType());
        BasePatientMemberRelation memberRelation = mapper.selectOne(basePatientMemberRelation);
        if (StringHelper.isNotNull(memberRelation)) {
          mapper.delete(basePatientMemberRelation);
        }
        mapper.insert(basePatientMemberRelation);
      }
    }
    if (type == 1) {
      PatientPrepaymentRelation patientPrepaymentRelation = patientPrepaymentRelationMapper.selectByPrimaryKey(id);
      if (StringHelper.isNotNull(patientPrepaymentRelation)) {
        BasePatientMemberRelation basePatientMemberRelation = new BasePatientMemberRelation();
        basePatientMemberRelation.setRelationId(patientPrepaymentRelation.getId());
        basePatientMemberRelation.setMasterCardId(patientPrepaymentRelation.getMasterCardId());
        basePatientMemberRelation.setSecondaryCardId(patientPrepaymentRelation.getSecondaryCardId());
        basePatientMemberRelation.setType((byte)type.intValue());
        BasePatientMemberRelation memberRelation = mapper.selectOne(basePatientMemberRelation);
        if (StringHelper.isNotNull(memberRelation)) {
          mapper.delete(basePatientMemberRelation);
        }
        mapper.insert(basePatientMemberRelation);
      }
    }
  }

  /**
   * 查询会员或预付款关联关系
   * @param id 关联关系id
   * @param type 类型id
   * @return BasePatientMemberRelation
   */
  private BasePatientMemberRelation getPatientMemberRelationInfo(Integer id, Integer type) {
    //查询会员关联关系
    if (type == 0) {
      PatientMemberRelation patientMemberRelation = patientMemberRelationMapper.selectByPrimaryKey(id);
      if (StringHelper.isNotNull(patientMemberRelation)) {
        BasePatientMemberRelation basePatientMemberRelation = new BasePatientMemberRelation();
        basePatientMemberRelation.setRelationId(patientMemberRelation.getId());
        basePatientMemberRelation.setMasterCardId(patientMemberRelation.getMasterCardId());
        basePatientMemberRelation.setSecondaryCardId(patientMemberRelation.getSecondaryCardId());
        basePatientMemberRelation.setType((byte) type.intValue());
        basePatientMemberRelation.setBindType(patientMemberRelation.getBindType());
        return basePatientMemberRelation;
      }
    }
    //查询预付款关联关系
    if (type == 1){
      PatientPrepaymentRelation patientPrepaymentRelation = patientPrepaymentRelationMapper.selectByPrimaryKey(id);
      if (StringHelper.isNotNull(patientPrepaymentRelation)) {
        BasePatientMemberRelation basePatientMemberRelation = new BasePatientMemberRelation();
        basePatientMemberRelation.setRelationId(patientPrepaymentRelation.getId());
        basePatientMemberRelation.setMasterCardId(patientPrepaymentRelation.getMasterCardId());
        basePatientMemberRelation.setSecondaryCardId(patientPrepaymentRelation.getSecondaryCardId());
        basePatientMemberRelation.setType((byte) type.intValue());
        basePatientMemberRelation.setBindType((byte) type.intValue());
        return basePatientMemberRelation;
      }
    }
      return null;
    }

  /**
   * 拉取某段时间内的组织数据并更新中间表
   * @param form 拉取时间
   */
  public void pullMemberRelationData(PullForm form) {
    Integer dataType = form.getDataType();
    if (dataType == 0){
      String startDate = form.getStartDate();
      String endDate = form.getEndDate();
      Example emp = new Example(PatientBaseInfo.class);
      emp.createCriteria().andBetween("updTime",startDate,endDate);
      List<PatientMemberRelation> patientMemberRelationList = patientMemberRelationMapper.selectByExample(emp);
      if (StringHelper.isNotEmpty(patientMemberRelationList)) {
        patientMemberRelationList.forEach(
                patientMemberRelation -> {
                  Integer memberRelationId = patientMemberRelation.getId();
                  mapper.deleteByPrimaryKeyAndType(memberRelationId,dataType);
                  BasePatientMemberRelation basePatientMember = getPatientMemberRelationInfo(memberRelationId,dataType);
                  if (basePatientMember != null){
                    mapper.insertSelective(basePatientMember);
                  }
                }
        );
      }
    }

    if (dataType == 1){
      String startDate = form.getStartDate();
      String endDate = form.getEndDate();
      Example emp = new Example(PatientBaseInfo.class);
      emp.createCriteria().andBetween("updTime",startDate,endDate);
      List<PatientPrepaymentRelation> prepaymentRelationList = patientPrepaymentRelationMapper.selectByExample(emp);
      if (StringHelper.isNotEmpty(prepaymentRelationList)) {
        prepaymentRelationList.forEach(
                patientPrepaymentRelation -> {
                  Integer prepaymentRelationId = patientPrepaymentRelation.getId();
                  mapper.deleteByPrimaryKeyAndType(prepaymentRelationId,dataType);
                  BasePatientMemberRelation basePatientMember = getPatientMemberRelationInfo(prepaymentRelationId,dataType);
                  if (basePatientMember != null){
                    mapper.insertSelective(basePatientMember);
                  }
                }
        );
      }
    }
  }
}
