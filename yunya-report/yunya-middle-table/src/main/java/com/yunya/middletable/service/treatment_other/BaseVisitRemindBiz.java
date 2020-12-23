package com.yunya.middletable.service.treatment_other;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseVisitRemindMapper;
import com.yunya.middletable.dao.treatment_other.VisitingRecordMapper;
import com.yunya.middletable.dao.treatment_other.VisitingRemindMapper;
import com.yunya.models.report.BaseVisitRemind;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.models.treatment_other.VisitingRemind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2020/10/20 19:12
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseVisitRemindBiz extends BaseBiz<BaseVisitRemindMapper, BaseVisitRemind> {

  /** 随访mapper */
  @Autowired VisitingRecordMapper visitingRecordMapper;
  /** 提醒mapper */
  @Autowired VisitingRemindMapper visitingRemindMapper;

  /**
   * 随访提醒中间表-操作
   *
   * @param msg 消息
   */
  public void operate(MessageModel msg) {
    Integer operateType = msg.getOperateType();
    Integer id = (Integer) msg.getParamMap().get("id");
    Integer type = (Integer) msg.getParamMap().get("type");
    switch (operateType) {
      case 0:
        BaseVisitRemind insertBaseVisitRemind = getBaseVisitRemindInfo(id, type);
        if (StringHelper.isNotNull(insertBaseVisitRemind)) {
          mapper.delete(insertBaseVisitRemind);
          mapper.insertSelective(insertBaseVisitRemind);
        }
        break;
      case 1:
        BaseVisitRemind updBaseVisitRemind = getBaseVisitRemindInfo(id, type);
        if (StringHelper.isNotNull(updBaseVisitRemind)) {
          mapper.updateByPrimaryKeySelective(updBaseVisitRemind);
        }
        break;
      case 2:
          BaseVisitRemind baseVisitRemind = new BaseVisitRemind();
          baseVisitRemind.setRecordId(id);
          baseVisitRemind.setType(type.byteValue());
          mapper.delete(baseVisitRemind);
        break;
      default:
        break;
    }
  }

  /**
   * 批量拉取-醒/随访-基础信息
   *
   * @param form 拉取时间
   */
  public void pullPatientData(PullForm form) {
    Integer type = form.getDataType();
    // 随访
    if (type == 0) {
      String startDate = form.getStartDate();
      String endDate = form.getEndDate();
      Example emp = new Example(VisitingRecord.class);
      emp.createCriteria().andBetween("updTime", startDate, endDate);
      List<VisitingRecord> visitingRecordList = visitingRecordMapper.selectByExample(emp);
      if (StringHelper.isNotEmpty(visitingRecordList)) {
        visitingRecordList.forEach(
            visitingRecord -> {
              Integer id = visitingRecord.getId();
              mapper.deleteByPrimaryKeyAndType(id, type);
              BaseVisitRemind baseVisitRemindInfo = getBaseVisitRemindInfo(id, type);
              if (baseVisitRemindInfo != null) {
                mapper.insertSelective(baseVisitRemindInfo);
              }
            });
      }
    }

    // 提醒
    if (type == 1) {
      String startDate = form.getStartDate();
      String endDate = form.getEndDate();
      Example emp = new Example(VisitingRemind.class);
      emp.createCriteria().andBetween("updTime", startDate, endDate);
      List<VisitingRecord> visitingRecordList = visitingRecordMapper.selectByExample(emp);
      if (StringHelper.isNotEmpty(visitingRecordList)) {
        visitingRecordList.forEach(
            visitingRecord -> {
              Integer id = visitingRecord.getId();
              mapper.deleteByPrimaryKeyAndType(id, type);
              BaseVisitRemind baseVisitRemindInfo = getBaseVisitRemindInfo(id, type);
              if (baseVisitRemindInfo != null) {
                mapper.insertSelective(baseVisitRemindInfo);
              }
            });
      }
    }
  }

  /**
   * 获取-提醒/随访-基础信息-封装到中间表-返回
   *
   * @param id 提醒 or 随访 id
   * @param type 提醒/随访
   * @return BaseVisitRemind
   */
  private BaseVisitRemind getBaseVisitRemindInfo(Integer id, Integer type) {
    if (type == 0) {
      VisitingRecord visitingRecord = visitingRecordMapper.selectByPrimaryKey(id);
      if (StringHelper.isNotNull(visitingRecord)) {
        BaseVisitRemind baseVisitRemind = new BaseVisitRemind();
        baseVisitRemind.setRecordId(visitingRecord.getId());
        baseVisitRemind.setOrgId(visitingRecord.getOrgId());
        baseVisitRemind.setPatientId(visitingRecord.getPatientId());
        baseVisitRemind.setType((byte) type.intValue());
        baseVisitRemind.setUserId(visitingRecord.getCrtId());
        baseVisitRemind.setTime(visitingRecord.getVisitingDate());
        baseVisitRemind.setContent(visitingRecord.getReason());
        baseVisitRemind.setCrtTime(visitingRecord.getCrtTime());
        return baseVisitRemind;
      }
      return null;
    }
    if (type == 1) {
      VisitingRemind visitingRemind = visitingRemindMapper.selectByPrimaryKey(id);
      if (StringHelper.isNotNull(visitingRemind)) {
        BaseVisitRemind baseVisitRemind = new BaseVisitRemind();
        baseVisitRemind.setRecordId(visitingRemind.getId());
        baseVisitRemind.setOrgId(visitingRemind.getOrgId());
        baseVisitRemind.setPatientId(visitingRemind.getPatientId());
        baseVisitRemind.setType((byte) type.intValue());
        baseVisitRemind.setUserId(visitingRemind.getCrtId());
        baseVisitRemind.setTime(visitingRemind.getRemindDate());
        baseVisitRemind.setContent(visitingRemind.getRemindContent());
        baseVisitRemind.setCrtTime(visitingRemind.getCrtTime());
        return baseVisitRemind;
      }
      return null;
    }
    return null;
  }

  public Object getVisitingInfo(Integer id, Integer type){
    if (type == 0){
      VisitingRecord visitingRecord = visitingRecordMapper.selectByPrimaryKey(id);
      return visitingRecord;
    }
    if (type == 1){
      VisitingRemind visitingRemind = visitingRemindMapper.selectByPrimaryKey(id);
      return visitingRemind;
    }
    return null;
  }
}
