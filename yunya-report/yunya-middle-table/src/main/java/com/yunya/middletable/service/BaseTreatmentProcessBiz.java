package com.yunya.middletable.service;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.appointment.AppointmentMapper;
import com.yunya.middletable.dao.appointment.AppointmentModifyRecordMapper;
import com.yunya.middletable.dao.report.BaseTreatmentProcessMapper;
import com.yunya.middletable.dao.treatment.AssistantMatchingRecordMapper;
import com.yunya.middletable.dao.treatment.RegisteredMapper;
import com.yunya.middletable.dao.treatment.TreatmentRecordMapper;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.appointment.AppointmentModifyRecord;
import com.yunya.models.report.BaseTreatmentProcess;
import com.yunya.models.treatment.AssistantMatchingRecord;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 简介: 中间表就诊流程业务处理
 *
 * @author: chow
 * @date: 2020/10/17 12:51
 * @description:
 * @since: 1.0.0
 */
@Slf4j
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
  /** 助手配诊 */
  @Autowired private AssistantMatchingRecordMapper assistantMatchingRecordMapper;
  /** 多线程 */
  @Resource(name = "customizeThreadPool")
  private ExecutorService importExcelThreadPool;

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
    switch (type) {
        // 预约
      case 0:
        Appointment appointment = appointmentMapper.selectByPrimaryKey(dataId);
        treatmentProcess = generateBaseTreatmentProcess(appointment);
        if (null != treatmentProcess) {
          mapper.deleteByAppointmentId(dataId);
          mapper.insertSelective(treatmentProcess);
        }
        break;
        // 挂号
      case 1:
        Registered registered = registeredMapper.selectByPrimaryKey(dataId);
        treatmentProcess = generateBaseTreatmentProcess(registered);
        if (null != treatmentProcess) {
          mapper.deleteByRegisteredId(dataId);
          mapper.insertSelective(treatmentProcess);
        }
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
      setTreatmentProcessAppointmentValue(treatmentProcess, appointment);
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
      treatmentProcess.setRegisteredId(registered.getId());
      treatmentProcess.setTreatStatus((byte) 0);
      treatmentProcess.setOrgId(registered.getOrgId());
      treatmentProcess.setPatientId(registered.getPatientId());
      treatmentProcess.setTreatType(registered.getFirstVisit());
      treatmentProcess.setRegisteredDentistId(registered.getDentistId());
      treatmentProcess.setRegisteredTime(registered.getRegTime());
      treatmentProcess.setRegisteredDate(registered.getCrtTime());
      return treatmentProcess;
    }
    return null;
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
   * 根据预约ID更新就诊流程
   *
   * @param appointmentId 预约ID
   */
  private void updateTreatmentProcessByAppointmentId(Integer appointmentId) {
    Appointment appointment = appointmentMapper.selectByPrimaryKey(appointmentId);
    log.info("====================【APP就诊主页面】============");
    log.info("==> user.dir:{}", System.getProperty("user.dir"));
    log.info("==> appointment:{}", appointment);
    if (null != appointment) {
      BaseTreatmentProcess treatmentProcess = mapper.selectOneByAppointmentId(appointmentId);
      log.info("==> treatmentProcess:{}", treatmentProcess);
      if (null != treatmentProcess) {
        setTreatmentProcessAppointmentValue(treatmentProcess, appointment);
        setTreatmentProcessRegisteredValue(treatmentProcess, appointmentId);
        TreatmentRecord treatmentRecord = new TreatmentRecord();
        treatmentRecord.setAppointmentId(appointmentId);
        setTreatmentProcessTreatmentValue(treatmentProcess, treatmentRecord);
        mapper.updateByAppointmentId(appointmentId, treatmentProcess);
      } else {
        treatmentProcess = new BaseTreatmentProcess();
        setTreatmentProcessAppointmentValue(treatmentProcess, appointment);
        setTreatmentProcessRegisteredValue(treatmentProcess, appointmentId);
        TreatmentRecord treatmentRecord = new TreatmentRecord();
        treatmentRecord.setAppointmentId(appointmentId);
        setTreatmentProcessTreatmentValue(treatmentProcess, treatmentRecord);
        mapper.insertSelective(treatmentProcess);
      }
    } else {
      mapper.deleteByAppointmentId(appointmentId);
    }
  }

  /**
   * 根据挂号ID更新就诊流程
   *
   * @param registeredId 挂号ID
   */
  private void updateTreatProcessByRegisteredId(Integer registeredId) {
    Registered registered = registeredMapper.selectByPrimaryKey(registeredId);
    if (null != registered) {
      BaseTreatmentProcess treatmentProcess = mapper.selectOneByRegisteredId(registeredId);
      if (registered.getInservice()) {
        if (treatmentProcess != null) {
          setTreatmentProcessRegisteredValue(treatmentProcess, registered);
          TreatmentRecord treatmentRecord = new TreatmentRecord();
          treatmentRecord.setRegisteredId(registeredId);
          setTreatmentProcessTreatmentValue(treatmentProcess, treatmentRecord);
          mapper.updateByRegisteredId(registeredId, treatmentProcess);
        } else {
          treatmentProcess = new BaseTreatmentProcess();
          setTreatmentProcessRegisteredValue(treatmentProcess, registered);
          TreatmentRecord treatmentRecord = new TreatmentRecord();
          treatmentRecord.setRegisteredId(registeredId);
          setTreatmentProcessTreatmentValue(treatmentProcess, treatmentRecord);
          mapper.insertSelective(treatmentProcess);
        }
      } else {
        if (null != treatmentProcess.getAppointmentId()) {
          treatmentProcess.setRegisteredId(null);
          treatmentProcess.setRegisteredDentistId(null);
          treatmentProcess.setTreatType(null);
          treatmentProcess.setRegisteredTime(null);
          treatmentProcess.setTreatStatus(null);
          treatmentProcess.setRegisteredDate(null);
          mapper.updateRegisteredValueByAppointmentId(treatmentProcess.getAppointmentId());
          mapper.updateByRegisteredId(registeredId, treatmentProcess);
        } else {
          mapper.deleteByRegisteredId(registeredId);
        }
      }
    } else {
      mapper.deleteByRegisteredId(registeredId);
    }
  }

  /**
   * 设置就诊流程就诊属性
   *
   * @param treatmentProcess 就诊流程
   * @param treatmentRecord 挂号
   */
  private void setTreatmentProcessTreatmentValue(
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
      setBaseTreatmentProcessAssistantValue(treatmentRecordResult.getId(), treatmentProcess);
    }
  }

  /**
   * 设置中间表账单关联助手
   *
   * @param treatmentRecordId 就诊ID
   * @param treatmentProcess 中间表就诊流程
   */
  private void setBaseTreatmentProcessAssistantValue(
      Integer treatmentRecordId, BaseTreatmentProcess treatmentProcess) {
    AssistantMatchingRecord assistantMatchRecord = new AssistantMatchingRecord();
    assistantMatchRecord.setTreatmentRecordId(treatmentRecordId);
    List<AssistantMatchingRecord> matchingRecords =
        assistantMatchingRecordMapper.select(assistantMatchRecord);
    if (StringHelper.isNotEmpty(matchingRecords)) {
      for (AssistantMatchingRecord record : matchingRecords) {
        Byte type = record.getType();
        Integer assistantId = record.getAssistantId();
        switch (type) {
          case 0:
            treatmentProcess.setAssistant1(assistantId);
            break;
          case 1:
            treatmentProcess.setAssistant2(assistantId);
            break;
          default:
            treatmentProcess.setAssistant3(assistantId);
            break;
        }
      }
    } else {
      treatmentProcess.setAssistant1(null);
      treatmentProcess.setAssistant2(null);
      treatmentProcess.setAssistant3(null);
      mapper.updateAssistantValueByTreatmentId(treatmentProcess);
    }
  }

  /**
   * 设置就诊流程挂号信息
   *
   * @param treatmentProcess 就诊流程
   * @param registered 挂号信息
   */
  private void setTreatmentProcessRegisteredValue(
      BaseTreatmentProcess treatmentProcess, Registered registered) {
    treatmentProcess.setRegisteredId(registered.getId());
    treatmentProcess.setTreatStatus((byte) 0);
    treatmentProcess.setTreatType(registered.getFirstVisit());
    treatmentProcess.setRegisteredDentistId(registered.getDentistId());
    treatmentProcess.setRegisteredTime(registered.getRegTime());
    treatmentProcess.setRegisteredDate(registered.getCrtTime());
  }

  /**
   * 设置就诊流程预约属性
   *
   * @param treatmentProcess 就诊流程
   * @param appointment 预约
   */
  private void setTreatmentProcessAppointmentValue(
      BaseTreatmentProcess treatmentProcess, Appointment appointment) {
    Integer appointmentId = appointment.getId();
    treatmentProcess.setOrgId(appointment.getOrgId());
    treatmentProcess.setPatientId(appointment.getPatientId());
    treatmentProcess.setAppointmentId(appointmentId);
    setTreatmentProcessAppointmentStatusAndAppointmentModifyTime(
        appointment, treatmentProcess, appointmentId);
    treatmentProcess.setAppointDentistId(appointment.getDentistId());
    treatmentProcess.setAppointStartTime(appointment.getAppointStartTime());
    treatmentProcess.setAppointDuration(appointment.getAppointDuration());
    treatmentProcess.setAppointContent(appointment.getAppointContent());
  }

  /**
   * 删除就诊流程
   *
   * @param dataId 删除数据ID
   * @param type 数据类型（0-预约；1-挂号）
   */
  private void deleteTreatmentProcess(Integer dataId, Integer type) {
    switch (type) {
        // 预约
      case 0:
        deleteTreatmentProcessByAppointmentId(dataId);
        break;
        // 挂号
      case 1:
        deleteTreatmentProcessByRegistered(dataId);
        break;
      default:
        break;
    }
  }

  /**
   * 根据预约ID删除就诊流程
   *
   * @param registeredId 预约ID
   */
  private void deleteTreatmentProcessByRegistered(Integer registeredId) {
    Registered registered = registeredMapper.selectByPrimaryKey(registeredId);
    if (null != registered) {
      if (registered.getInservice()) {
        BaseTreatmentProcess treatmentProcess = generateBaseTreatmentProcess(registered);
        TreatmentRecord treatmentRecord = new TreatmentRecord();
        treatmentRecord.setRegisteredId(registeredId);
        setTreatmentProcessTreatmentValue(treatmentProcess, treatmentRecord);
        mapper.insertSelective(treatmentProcess);
      } else {
        BaseTreatmentProcess entity = new BaseTreatmentProcess();
        entity.setRegisteredId(registeredId);
        mapper.delete(entity);
      }
    } else {
      mapper.deleteByRegisteredId(registeredId);
    }
  }

  /**
   * 根据预约ID删除就诊流程
   *
   * @param appointmentId 预约ID
   */
  private void deleteTreatmentProcessByAppointmentId(Integer appointmentId) {
    Appointment appointment = appointmentMapper.selectByPrimaryKey(appointmentId);
    if (null == appointment) {
      mapper.deleteByAppointmentId(appointmentId);
    } else {
      BaseTreatmentProcess process = mapper.selectOneByAppointmentId(appointmentId);
      if (null != process) {
        setTreatmentProcessAppointmentValue(process, appointment);
        setTreatmentProcessRegisteredValue(process, appointmentId);
        TreatmentRecord treatmentRecord = new TreatmentRecord();
        treatmentRecord.setAppointmentId(appointmentId);
        setTreatmentProcessTreatmentValue(process, treatmentRecord);
        mapper.updateByAppointmentId(appointmentId, process);
      } else {
        process = generateBaseTreatmentProcess(appointment);
        setTreatmentProcessRegisteredValue(process, appointmentId);
        TreatmentRecord treatmentRecord = new TreatmentRecord();
        treatmentRecord.setAppointmentId(appointmentId);
        setTreatmentProcessTreatmentValue(process, treatmentRecord);
        mapper.insertSelective(process);
      }
    }
  }

  /**
   * 根据时间段拉取数据
   *
   * @param form 拉取时间
   */
  public void pullTreatmentProcessData(PullForm form) throws InterruptedException {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    List<String> dateRanges = DateUtil.sliceUpDateRange(startDate, endDate);
    if (StringHelper.isNotEmpty(dateRanges)) {
      CountDownLatch latch = new CountDownLatch(dateRanges.size());
      List<Future> resultFutures =
          dateRanges.stream()
              .map(
                  date ->
                      importExcelThreadPool.submit(
                          () -> {
                            try {
                              // 预约-就诊
                              Example appointEmp = new Example(Appointment.class);
                              appointEmp
                                  .createCriteria()
                                  .andEqualTo("inservice", true)
                                  .andBetween(
                                      "crtTime",
                                      new DateTime(date).toDate(),
                                      new DateTime(date).plusDays(1).toDate());
                              List<Appointment> appointments =
                                  appointmentMapper.selectByExample(appointEmp);
                              if (StringHelper.isNotEmpty(appointments)) {
                                CountDownLatch latch_1 = new CountDownLatch(appointments.size());
                                List<Future> resultFutures_1;
                                List<BaseTreatmentProcess> treatmentProcesses =
                                    Lists.newArrayList();
                                resultFutures_1 =
                                    appointments.stream()
                                        .map(
                                            appointment ->
                                                importExcelThreadPool.submit(
                                                    () -> {
                                                      try {
                                                        BaseTreatmentProcess process =
                                                            new BaseTreatmentProcess();
                                                        Integer appointmentId = appointment.getId();
                                                        process.setOrgId(appointment.getOrgId());
                                                        process.setPatientId(
                                                            appointment.getPatientId());
                                                        process.setAppointmentId(appointmentId);
                                                        process.setAppointDentistId(
                                                            appointment.getDentistId());
                                                        process.setAppointStartTime(
                                                            appointment.getAppointStartTime());
                                                        process.setAppointDuration(
                                                            appointment.getAppointDuration());
                                                        process.setAppointContent(
                                                            appointment.getAppointContent());
                                                        // 设置就诊流程预约状态和改约次数
                                                        setTreatmentProcessAppointmentStatusAndAppointmentModifyTime(
                                                            appointment, process, appointmentId);
                                                        // 设置就诊流程挂号信息
                                                        setTreatmentProcessRegisteredValue(
                                                            process, appointmentId);
                                                        // 设置就诊流程就诊信息
                                                        TreatmentRecord treatmentRecord =
                                                            new TreatmentRecord();
                                                        treatmentRecord.setAppointmentId(
                                                            appointmentId);
                                                        setTreatmentProcessTreatmentValue(
                                                            process, treatmentRecord);
                                                        treatmentProcesses.add(process);
                                                      } finally {
                                                        latch_1.countDown();
                                                      }
                                                    }))
                                        .collect(Collectors.toList());
                                try {
                                  latch_1.await();
                                } catch (InterruptedException e) {
                                  e.printStackTrace();
                                }
                                printExceptionLog(resultFutures_1, log);
                                if (StringHelper.isNotEmpty(treatmentProcesses)) {
                                  mapper.batchInsertSelective(treatmentProcesses);
                                }

                                // 挂号-就诊
                                Example registeredEmp = new Example(Registered.class);
                                registeredEmp
                                    .createCriteria()
                                    .andEqualTo("inservice", true)
                                    .andBetween(
                                        "crtTime",
                                        new DateTime(date).toDate(),
                                        new DateTime(date).plusDays(1).toDate());
                                List<Registered> registeredList =
                                    registeredMapper.selectByExample(registeredEmp);
                                if (StringHelper.isNotEmpty(registeredList)) {
                                  CountDownLatch latch_2 = new CountDownLatch(appointments.size());
                                  List<Future> resultFutures_2;
                                  List<BaseTreatmentProcess> tempList = Lists.newArrayList();
                                  resultFutures_2 =
                                      registeredList.stream()
                                          .map(
                                              registered ->
                                                  importExcelThreadPool.submit(
                                                      () -> {
                                                        try {
                                                          if (null
                                                              == registered.getAppointmentId()) {
                                                            Integer registeredId =
                                                                registered.getId();
                                                            BaseTreatmentProcess entity =
                                                                new BaseTreatmentProcess();
                                                            entity.setRegisteredId(registeredId);
                                                            int count = mapper.selectCount(entity);
                                                            if (0 >= count) {
                                                              BaseTreatmentProcess process =
                                                                  generateBaseTreatmentProcess(
                                                                      registered);
                                                              if (null != process) {
                                                                TreatmentRecord treatmentRecord =
                                                                    new TreatmentRecord();
                                                                treatmentRecord.setRegisteredId(
                                                                    registered.getId());
                                                                setTreatmentProcessTreatmentValue(
                                                                    process, treatmentRecord);
                                                                tempList.add(process);
                                                              }
                                                            }
                                                          }
                                                        } finally {
                                                          latch_2.countDown();
                                                        }
                                                      }))
                                          .collect(Collectors.toList());
                                  try {
                                    latch_2.await();
                                  } catch (InterruptedException e) {
                                    e.printStackTrace();
                                  }
                                  printExceptionLog(resultFutures_2, log);
                                  if (StringHelper.isNotEmpty(tempList)) {
                                    mapper.batchInsertSelective(tempList);
                                  }
                                }
                              }
                            } finally {
                              latch.countDown();
                            }
                          }))
              .collect(Collectors.toList());
      latch.await();
      printExceptionLog(resultFutures, log);
    }
  }

  /**
   * 打印错误信息
   *
   * @param resultFutures 异常信息
   * @param log
   */
  static void printExceptionLog(List<Future> resultFutures, Logger log) {
    if (StringHelper.isNotEmpty(resultFutures)) {
      resultFutures.forEach(
          future -> {
            try {
              Object o = future.get();
              if (o instanceof ClientServiceException) {
                ClientServiceException cexp = (ClientServiceException) o;
                log.info(cexp.getMessage());
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          });
    }
  }

  /**
   * 设置就诊流程挂号信息
   *
   * @param process 就诊流程
   * @param appointmentId 预约ID
   */
  private void setTreatmentProcessRegisteredValue(
      BaseTreatmentProcess process, Integer appointmentId) {
    Registered registered = new Registered();
    registered.setAppointmentId(appointmentId);
    registered.setInservice(true);
    Registered registeredResult = registeredMapper.selectOne(registered);
    if (null != registeredResult) {
      process.setTreatType(registeredResult.getFirstVisit());
      process.setRegisteredId(registeredResult.getId());
      process.setTreatStatus((byte) 0);
      process.setRegisteredDentistId(registeredResult.getDentistId());
      process.setRegisteredTime(registeredResult.getRegTime());
      process.setRegisteredDate(registeredResult.getCrtTime());
    } else { // 将挂号信息清空
      process.setTreatType(null);
      process.setRegisteredId(null);
      process.setTreatStatus(null);
      process.setRegisteredDentistId(null);
      process.setRegisteredTime(null);
      process.setRegisteredDate(null);
      mapper.updateRegisteredValueByAppointmentId(appointmentId);
    }
  }

  /**
   * 设置就诊流程预约状态
   *
   * @param appointment 预约信息
   * @param process 就诊流程
   * @param appointmentId 预约ID
   */
  private void setTreatmentProcessAppointmentStatusAndAppointmentModifyTime(
      Appointment appointment, BaseTreatmentProcess process, Integer appointmentId) {
    AppointmentModifyRecord modifyAppointment = new AppointmentModifyRecord();
    modifyAppointment.setAppointmentId(appointmentId);
    int count = appointmentModifyRecordMapper.selectCount(modifyAppointment);
    process.setAppointModifyTime(count);
    Byte appointStatus = appointment.getAppointStatus();
    switch (appointStatus) {
      case 0:
        process.setAppointStatus(appointment.getConfirmStatus() ? (byte) 1 : (byte) 0);
        break;
      case 2:
        process.setAppointStatus((byte) 2);
        break;
      default:
        break;
    }
  }
}
