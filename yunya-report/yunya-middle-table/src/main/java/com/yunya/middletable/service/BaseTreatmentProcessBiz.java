package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.middletable.dao.appointment.AppointmentMapper;
import com.yunya.middletable.dao.appointment.AppointmentModifyRecordMapper;
import com.yunya.middletable.dao.report.BaseTreatmentProcessMapper;
import com.yunya.middletable.dao.treatment.RegisteredMapper;
import com.yunya.middletable.dao.treatment.TreatmentRecordMapper;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.appointment.AppointmentModifyRecord;
import com.yunya.models.middletable.BaseTreatmentProcess;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 简介: 中间表就诊流程业务处理
 *
 * @author: chow
 * @date: 2020/10/17 12:51
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseTreatmentProcessBiz
    extends BaseBiz<BaseTreatmentProcessMapper, BaseTreatmentProcess> {

  /** 预约 */
  @Autowired private AppointmentMapper appointmentMapper;
  /** 预约修改 */
  @Autowired private AppointmentModifyRecordMapper appointmentModifyRecordMapper;
  /** 挂号 */
  @Autowired private RegisteredMapper registeredMapper;
  /** 接诊 */
  @Autowired private TreatmentRecordMapper treatmentRecordMapper;

  /**
   * 根据消息操作中间表就诊流程信息
   *
   * @param msg 消息
   */
  public void operateTreatmentProcess(MessageModel msg) {
    Map<String, Object> paramMap = msg.getParamMap();
    Integer dataId = (Integer) paramMap.get("id");
    Integer type = (Integer) paramMap.get("type");
    Integer operateType = msg.getOperateType();
    switch (operateType) {
        // 新增
      case 0:
        createTreatmentProcess(dataId, type);
        break;
        // 修改
      case 1:
        updateTreatmentProcess(dataId, type);
        break;
      case 2:
        // 删除
        deleteTreatmentProcess(dataId, type);
        break;
      default:
        break;
    }
  }

  /**
   * 添加就诊流程
   *
   * @param dataId 数据ID
   * @param type 元数据类型
   */
  private void createTreatmentProcess(Integer dataId, Integer type) {
    BaseTreatmentProcess treatmentProcess;
    BaseTreatmentProcess process = new BaseTreatmentProcess();
    switch (type) {
        // 预约
      case 0:
        Appointment appointment = appointmentMapper.selectByPrimaryKey(dataId);
        treatmentProcess = generateBaseTreatmentProcess(appointment);
        process.setAppointmentId(dataId);
        mapper.delete(process);
        mapper.insertSelective(treatmentProcess);
        break;
        // 挂号
      case 1:
        Registered registered = registeredMapper.selectByPrimaryKey(dataId);
        treatmentProcess = generateBaseTreatmentProcess(registered);
        process.setRegisteredId(dataId);
        mapper.delete(process);
        mapper.insertSelective(treatmentProcess);
        break;
      default:
        break;
    }
  }

  /**
   * 初始化就诊流程预约信息属性
   *
   * @param appointment 预约
   * @return
   */
  private BaseTreatmentProcess generateBaseTreatmentProcess(Appointment appointment) {
    if (null != appointment) {
      BaseTreatmentProcess treatmentProcess = new BaseTreatmentProcess();
      setAppointmentValue(treatmentProcess, appointment);
      return treatmentProcess;
    }
    return null;
  }

  /**
   * 初始化就诊流程挂号信息(无预约)
   *
   * @param registered 挂号信息
   * @return
   */
  private BaseTreatmentProcess generateBaseTreatmentProcess(Registered registered) {
    if (null != registered) {
      BaseTreatmentProcess treatmentProcess = new BaseTreatmentProcess();
      treatmentProcess.setAppointmentId(registered.getAppointmentId());
      treatmentProcess.setRegisteredId(registered.getId());
      treatmentProcess.setTreatStatus((byte) 0);
      treatmentProcess.setOrgId(registered.getOrgId());
      treatmentProcess.setPatientId(registered.getPatientId());
      treatmentProcess.setTreatType(registered.getFirstVisit());
      treatmentProcess.setRegisteredDentistId(registered.getDentistId());
      treatmentProcess.setRegisteredTime(registered.getRegTime());
      return treatmentProcess;
    }
    return null;
  }

  /**
   * 删除就诊流程
   *
   * @param dataId 删除数据ID
   * @param type 数据类型（0-预约；1-挂号）
   */
  private void deleteTreatmentProcess(Integer dataId, Integer type) {
    BaseTreatmentProcess entity = new BaseTreatmentProcess();
    switch (type) {
        // 预约
      case 0:
        entity.setAppointStatus((byte) 4);
        mapper.updateByAppointmentId(dataId, entity);
        break;
        // 挂号
      case 1:
        entity.setRegisteredId(dataId);
        mapper.delete(entity);
        break;
      default:
        break;
    }
  }

  /**
   * 更新就诊流程
   *
   * @param dataId 更新数据ID
   * @param type 元数据类型
   */
  private void updateTreatmentProcess(Integer dataId, Integer type) {
    switch (type) {
        // 预约
      case 0:
        updateTreatmentProcessByAppointmentId(dataId);
        break;
        // 挂号
      case 1:
        updateTreatProcessByRegisteredId(dataId);
        break;
      default:
        break;
    }
  }

  /**
   * 根据挂号ID更新就诊流程
   *
   * @param registeredId 挂号ID
   */
  private void updateTreatProcessByRegisteredId(Integer registeredId) {
    BaseTreatmentProcess treatmentProcess = mapper.selectOneByRegisteredId(registeredId);
    if (null != treatmentProcess) {
      Registered registered = registeredMapper.selectByPrimaryKey(registeredId);
      if (null != registered) {
        if (registered.getInservice()) {
          treatmentProcess.setRegisteredId(registered.getId());
          treatmentProcess.setTreatStatus((byte) 0);
          treatmentProcess.setRegisteredDentistId(registered.getDentistId());
          treatmentProcess.setRegisteredTime(registered.getRegTime());
          TreatmentRecord treatmentRecord = new TreatmentRecord();
          treatmentRecord.setRegisteredId(registeredId);
          setTreatmentValue(treatmentProcess, treatmentRecord);
          mapper.updateByRegisteredId(registeredId, treatmentProcess);
        } else {
          BaseTreatmentProcess entity = new BaseTreatmentProcess();
          entity.setRegisteredId(registeredId);
          mapper.delete(entity);
        }
      }
    }
  }

  /**
   * 根据预约ID更新就诊流程
   *
   * @param appointmentId 预约ID
   */
  private void updateTreatmentProcessByAppointmentId(Integer appointmentId) {
    BaseTreatmentProcess treatmentProcess = mapper.selectOneByAppointmentId(appointmentId);
    if (null != treatmentProcess) {
      Appointment appointment = appointmentMapper.selectByPrimaryKey(appointmentId);
      if (null != appointment) {
        setAppointmentValue(treatmentProcess, appointment);
      }
      Registered registered = new Registered();
      registered.setAppointmentId(appointmentId);
      Registered registeredResult = registeredMapper.selectOne(registered);
      if (null != registeredResult) {
        if (registeredResult.getInservice()) {
          treatmentProcess.setTreatType(registeredResult.getFirstVisit());
          treatmentProcess.setRegisteredId(registeredResult.getId());
          treatmentProcess.setTreatStatus((byte) 0);
          treatmentProcess.setRegisteredDentistId(registeredResult.getDentistId());
          treatmentProcess.setRegisteredTime(registeredResult.getRegTime());
        } else {
          treatmentProcess.setTreatType(null);
          treatmentProcess.setRegisteredId(null);
          treatmentProcess.setTreatStatus(null);
          treatmentProcess.setRegisteredDentistId(null);
          treatmentProcess.setRegisteredTime(null);
        }
      }
      TreatmentRecord treatmentRecord = new TreatmentRecord();
      treatmentRecord.setAppointmentId(appointmentId);
      setTreatmentValue(treatmentProcess, treatmentRecord);
      mapper.updateByAppointmentId(appointmentId, treatmentProcess);
    }
  }

  /**
   * 设置就诊流程就诊属性
   *
   * @param treatmentProcess 就诊流程
   * @param treatmentRecord 挂号
   */
  private void setTreatmentValue(
      BaseTreatmentProcess treatmentProcess, TreatmentRecord treatmentRecord) {
    TreatmentRecord treatmentRecordResult = treatmentRecordMapper.selectOne(treatmentRecord);
    if (null != treatmentRecordResult) {
      treatmentProcess.setTreatmentId(treatmentRecordResult.getId());
      Byte status = treatmentRecordResult.getStatus();
      switch (status) {
        case 0:
          treatmentProcess.setTreatStatus((byte) 1);
          break;
        case 1:
          treatmentProcess.setTreatStatus((byte) 2);
          break;
        case 2:
          treatmentProcess.setTreatStatus((byte) 3);
          break;
        case 3:
          treatmentProcess.setTreatStatus((byte) 4);
          break;
        default:
          break;
      }
      treatmentProcess.setTreatStartTime(treatmentRecordResult.getTreatStartTime());
      treatmentProcess.setTreatEndTime(treatmentRecordResult.getTreatEndTime());
    }
  }

  /**
   * 设置就诊流程预约属性
   *
   * @param treatmentProcess 就诊流程
   * @param appointment 预约
   */
  private void setAppointmentValue(BaseTreatmentProcess treatmentProcess, Appointment appointment) {
    Integer appointmentId = appointment.getId();
    treatmentProcess.setOrgId(appointment.getOrgId());
    treatmentProcess.setPatientId(appointment.getPatientId());
    treatmentProcess.setAppointmentId(appointmentId);
    AppointmentModifyRecord modifyAppointment = new AppointmentModifyRecord();
    modifyAppointment.setAppointmentId(appointmentId);
    int count = appointmentModifyRecordMapper.selectCount(modifyAppointment);
    treatmentProcess.setAppointModifyTime(count);
    Byte appointStatus = appointment.getAppointStatus();
    if (count > 0) {
      treatmentProcess.setAppointStatus(appointment.getConfirmStatus() ? (byte) 3 : (byte) 2);
    } else {
      switch (appointStatus) {
        case 0:
          treatmentProcess.setAppointStatus(appointment.getConfirmStatus() ? (byte) 1 : (byte) 0);
          break;
        case 2:
          treatmentProcess.setAppointStatus((byte) 4);
          break;
        default:
          break;
      }
    }
    treatmentProcess.setAppointDentistId(appointment.getDentistId());
    treatmentProcess.setAppointStartTime(appointment.getAppointStartTime());
    treatmentProcess.setAppointDuration(appointment.getAppointDuration());
    treatmentProcess.setAppointContent(appointment.getAppointContent());
  }

  /**
   * 根据时间段拉取数据
   *
   * @param form 拉取时间
   */
  public void pullTreatmentProcessData(PullForm form) {}
}
