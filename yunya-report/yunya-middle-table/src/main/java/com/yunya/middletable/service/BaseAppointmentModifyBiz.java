package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.appointment.AppointmentModifyRecordMapper;
import com.yunya.middletable.dao.report.BaseAppointmentModifyMapper;
import com.yunya.models.appointment.AppointmentModifyRecord;
import com.yunya.models.report.BaseAppointmentModify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 简介: 改约同步业务层
 *
 * @author: chow
 * @date: 2020/12/11 10:41
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseAppointmentModifyBiz extends BaseBiz<BaseAppointmentModifyMapper, BaseAppointmentModify> {

  /** 改约 */
  @Autowired private AppointmentModifyRecordMapper appointmentModifyRecordMapper;

  /**
   * 根据消息类型操作（新增/修改/删除）中间表改约记录
   *
   * @param msg 消息
   */
  public void operateAppointmentModify(MessageModel msg) {
    Integer dataId = (Integer) msg.getParamMap().get("id");
    BaseAppointmentModify baseAppointmentModify = generateBaseAppointmentModify(dataId);
    Integer operateType = msg.getOperateType();
    switch (operateType) {
      case 0:
        mapper.deleteByPrimaryKey(dataId);
        if (null != baseAppointmentModify) {
          mapper.insertSelective(baseAppointmentModify);
        }
        break;
      case 1:
        if (null != baseAppointmentModify) {
          BaseAppointmentModify result = mapper.selectByPrimaryKey(dataId);
          if (null == result) {
            mapper.deleteByPrimaryKey(dataId);
            mapper.insertSelective(baseAppointmentModify);
          } else {
            mapper.updateByPrimaryKeySelective(baseAppointmentModify);
          }
        } else {
          mapper.deleteByPrimaryKey(dataId);
        }
        break;
      case 2:
        if (null == baseAppointmentModify) {
          mapper.deleteByPrimaryKey(dataId);
        } else {
          mapper.insertSelective(baseAppointmentModify);
        }
        break;
      default:
        break;
    }
  }

  /**
   * 构建中间表改约记录
   *
   * @param id 主键
   * @return BaseAppointmentModify
   */
  private BaseAppointmentModify generateBaseAppointmentModify(Integer id) {
    AppointmentModifyRecord record = new AppointmentModifyRecord();
    record.setId(id);
    AppointmentModifyRecord appointmentModifyRecord = appointmentModifyRecordMapper.selectOne(record);
    return null != appointmentModifyRecord ? setBaseAppointmentModifyValue(id, appointmentModifyRecord) : null;
  }

  /**
   * 设置中间表改约记录属性
   *
   * @param id 主键
   * @param appointmentModifyRecord 原始改约记录
   * @return BaseAppointmentModify
   */
  private BaseAppointmentModify setBaseAppointmentModifyValue(Integer id, AppointmentModifyRecord appointmentModifyRecord) {
    BaseAppointmentModify baseAppointmentModify = new BaseAppointmentModify();
    baseAppointmentModify.setId(id);
    baseAppointmentModify.setAppointDate(appointmentModifyRecord.getAppointDate());
    baseAppointmentModify.setAppointmentId(appointmentModifyRecord.getAppointmentId());
    baseAppointmentModify.setCrtTime(appointmentModifyRecord.getCrtTime());
    baseAppointmentModify.setDentistId(appointmentModifyRecord.getDentistId());
    baseAppointmentModify.setOrgId(appointmentModifyRecord.getOrgId());
    baseAppointmentModify.setCrtId(appointmentModifyRecord.getCrtId());
    return baseAppointmentModify;
  }

  /**
   * 拉取某段时间内的改约记录数据并更新中间表
   *
   * @param form 拉取时间
   */
  public void pullAppointmentModify(PullForm form) {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    Example emp = new Example(AppointmentModifyRecord.class);
    emp.createCriteria().andBetween("crtTime", startDate, endDate);
    List<AppointmentModifyRecord> result = appointmentModifyRecordMapper.selectByExample(emp);
    if (StringHelper.isNotEmpty(result)) {
      result.forEach(
          entity -> {
            Integer id = entity.getId();
            mapper.deleteByPrimaryKey(id);
            BaseAppointmentModify baseAppointmentModify = setBaseAppointmentModifyValue(id, entity);
            mapper.insertSelective(baseAppointmentModify);
          });
    }
  }
}
