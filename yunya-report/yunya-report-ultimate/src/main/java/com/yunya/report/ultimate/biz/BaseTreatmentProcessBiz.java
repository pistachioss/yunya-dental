package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.report.domain.query.AssistantMatchingDetailQuery;
import com.yunya.feign.report.domain.query.TreatmentList4AppQuery;
import com.yunya.feign.report.domain.query.TreatmentMatchingRecordQuery;
import com.yunya.feign.report.domain.query.TreatmentRecordQuery;
import com.yunya.feign.report.domain.vo.BaseTreatmentProcessVO;
import com.yunya.feign.report.domain.vo.EmployeeTreatMatchingDetailVO;
import com.yunya.feign.report.domain.vo.TreatmentMatchingRecordVO;
import com.yunya.feign.report.domain.vo.TreatmentRecordReportVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentInfo4ListVO;
import com.yunya.feign.treatment.domain.vo.RegisteredVO;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordExtendVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.report.BaseTreatmentProcess;
import com.yunya.report.ultimate.mapper.BaseTreatmentProcessMapper;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 简介: 就诊流程业务层
 *
 * @author: chow
 * @date: 2020/10/26 15:46
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseTreatmentProcessBiz
    extends BaseBiz<BaseTreatmentProcessMapper, BaseTreatmentProcess> {

  @Autowired
  private RemoteSystemServiceFeign remoteSystemServiceFeign;
  @Autowired
  private RemoteAppointmentFeign remoteAppointmentFeign;
  @Autowired
  private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;
  @Autowired
  private RemotePatientCentralServiceFeign patientCentralServiceFeign;

  /**
   * 根据条件查询就诊记录报表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<TreatmentRecordReportVO> findTreatmentList(TreatmentRecordQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<TreatmentRecordReportVO> resultList = mapper.selectTreatmentRecordReportVOList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询就诊列表并导出excel
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportTreatmentList(HttpServletResponse response, TreatmentRecordQuery query)
      throws IOException {
    List<TreatmentRecordReportVO> list = mapper.selectTreatmentRecordReportVOList(query);
    ExcelUtil<TreatmentRecordReportVO> excelUtil = new ExcelUtil<>(TreatmentRecordReportVO.class);
    excelUtil.exportExcel(response, list, "患者就诊记录");
  }

  /**
   * 根据条件查询就诊配诊记录列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<TreatmentMatchingRecordVO> findTreatmentMatchingRecord(
      TreatmentMatchingRecordQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<TreatmentMatchingRecordVO> resultList = mapper.selectTreatmentMatchingRecord(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出配诊记录列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportTreatmentMatchingRecord(
      HttpServletResponse response, TreatmentMatchingRecordQuery query) throws IOException {
    List<TreatmentMatchingRecordVO> list = mapper.selectTreatmentMatchingRecord(query);
    ExcelUtil<TreatmentMatchingRecordVO> excelUtil =
        new ExcelUtil<>(TreatmentMatchingRecordVO.class);
    excelUtil.exportExcel(response, list, "配诊记录表");
  }

  /**
   * 根据条件查询助手配诊明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeTreatMatchingDetailVO>
   */
  public PageInfo<EmployeeTreatMatchingDetailVO> findAssistantMatchingDetailList(
      AssistantMatchingDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeTreatMatchingDetailVO> resultList =
        mapper.selectAssistantMatchingDetailList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 查询APP端就诊列表
   * @param query
   * @return
   */
  public PageInfo<BaseTreatmentProcessVO> treatmentList4App(TreatmentList4AppQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(),query.getPageSize());
    }
    List<PatientTreatmentInfo4ListVO> patientTreatmentInfo4ListVOS = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    List<BaseTreatmentProcessVO> baseTreatmentProcessVOS = mapper.treatmentList4App(query.getOrgId(),query.getDentistId(),query.getQueryDate());
    PageInfo pageInfo = new PageInfo(baseTreatmentProcessVOS);
    if (StringHelper.isEmpty(baseTreatmentProcessVOS)) {
      return pageInfo;
    }
    // 获取患者ID集合
    List<Integer> patientIds = baseTreatmentProcessVOS.stream().map(BaseTreatmentProcessVO::getPatientId).collect(Collectors.toList());
    if (StringHelper.isEmpty(patientIds)) {
      throw new ClientServiceException("患者ID不能为空", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
    // 查询患者信息
    List<PatientTotalInfoVo> patientTotalInfo = patientCentralServiceFeign.findPatientTotalInfo(patientIds);
    //////////////////////////////////////////////////// 预约未到 ////////////////////////////////
    // 获取预约未到的预约ID
    List<Integer> appointIds = new ArrayList<>();
    baseTreatmentProcessVOS.stream().filter(
            baseTreatmentProcessVO -> {
              return null != baseTreatmentProcessVO.getAppointmentId() && baseTreatmentProcessVO.getAppointStatus() < 4 && baseTreatmentProcessVO.getRegisteredId() == null;
            }).forEach(baseTreatmentProcessVO -> {
      appointIds.add(baseTreatmentProcessVO.getAppointmentId());
    });
    if (StringHelper.isNotEmpty(appointIds)) {
      // 查询预约助手
      List<Appointment> appointmentListByIds = remoteAppointmentFeign.findAppointmentListByIds(appointIds);
      // 获取预约助手ID
      List<Integer> assistantIds = new ArrayList<>();
      // 查询预约助手信息
      List<SysUserInfoDetail> assistantInfos = null;
      if (StringHelper.isNotEmpty(appointmentListByIds)) {
        appointmentListByIds.forEach(appointment -> {
          assistantIds.add(appointment.getAssistantId());
        });
        // 查询预约助手信息
        if (StringHelper.isNotEmpty(assistantIds)) {
          assistantInfos = this.remoteSystemServiceFeign.findSysUserEmployeeInfoByUserIds(assistantIds);
        }
      }
      for (BaseTreatmentProcessVO appointmentUnDonePatientInfoVO : baseTreatmentProcessVOS) {
        Integer appointmentId = appointmentUnDonePatientInfoVO.getAppointmentId();
        if (appointIds.contains(appointmentId)) {
          PatientTreatmentInfo4ListVO entity = new PatientTreatmentInfo4ListVO();
          entity.setOrgId(appointmentUnDonePatientInfoVO.getOrgId());
          entity.setAppointId(appointmentUnDonePatientInfoVO.getAppointmentId());
          entity.setTreatStatus(appointmentUnDonePatientInfoVO.getAppointStatus());
          Date appointStartTime = appointmentUnDonePatientInfoVO.getAppointStartTime();
          if (null != appointStartTime) {
            entity.setNodeTime(dateFormat.format(appointStartTime));
          }
          entity.setPatientId(appointmentUnDonePatientInfoVO.getPatientId());
          entity.setDentistId(appointmentUnDonePatientInfoVO.getAppointDentistId());
          entity.setDentistName(appointmentUnDonePatientInfoVO.getAppointDentistName());
          if (StringHelper.isNotEmpty(assistantInfos)) {
            List<Appointment> collect = appointmentListByIds.stream().filter(appointment -> appointment.getId().equals(appointmentUnDonePatientInfoVO.getAppointmentId())).collect(Collectors.toList());
            if (StringHelper.isNotEmpty(collect)) {
              Appointment appointment = collect.get(0);
              // 获取预约助手信息
              List<SysUserInfoDetail> assistantInfoList = assistantInfos.stream().filter(sysUserInfoDetail -> sysUserInfoDetail.getUserId().equals(appointment.getAssistantId())).collect(Collectors.toList());
              if (StringHelper.isNotEmpty(assistantInfoList)) {
                SysUserInfoDetail userInfoDetail = assistantInfoList.get(0);
                entity.setAssistantId(appointment.getAssistantId());
                entity.setAssistantName(
                        StringHelper.isBlank(userInfoDetail.getName()) ?
                                "--" : userInfoDetail.getName());
              }
            }
          }
          // 设置患者年龄
          List<PatientTotalInfoVo> collect = patientTotalInfo.stream().filter(patientTotalInfoVo -> patientTotalInfoVo.getId().equals(appointmentUnDonePatientInfoVO.getPatientId())).collect(Collectors.toList());
          if (StringHelper.isNotEmpty(collect)) {
            PatientTotalInfoVo patientTotalInfoVo = collect.get(0);
            if (null != patientTotalInfoVo) {
              // 患者姓名暂时从患者服务查询，目前报表同步患者信息不成功
              entity.setPatientName(patientTotalInfoVo.getName());
              entity.setAge(patientTotalInfoVo.getAge());
              entity.setGender(patientTotalInfoVo.getGender());
            }
          }
          entity.setOrgId(appointmentUnDonePatientInfoVO.getOrgId());
          patientTreatmentInfo4ListVOS.add(entity);
        }
      }
    }

    //////////////////////////////////////////////////// 候诊中 ////////////////////////////////
    // 获取挂号ID
    List<Integer> registerIds = new ArrayList<>();
    baseTreatmentProcessVOS.stream().filter(
            baseTreatmentProcessVO -> {
              return null != baseTreatmentProcessVO.getRegisteredId() && baseTreatmentProcessVO.getTreatStatus() == 0;
            }).forEach(baseTreatmentProcessVO -> {
      registerIds.add(baseTreatmentProcessVO.getRegisteredId());
    });
    if (StringHelper.isNotEmpty(registerIds)) {
      List<RegisteredVO> registeredVOS = remoteTreatmentServiceFeign.registeredInfoDetails(registerIds);
      if (StringHelper.isNotEmpty(registeredVOS)) {
        registeredVOS.forEach(registeredVO -> {
          PatientTreatmentInfo4ListVO entity = new PatientTreatmentInfo4ListVO();
          entity.setOrgId(registeredVO.getOrgId());
          entity.setAppointId(registeredVO.getAppointmentId());
          entity.setTreatStatus((byte) 2);
          entity.setNodeTime(dateFormat.format(registeredVO.getRegTime()));
          entity.setPatientId(registeredVO.getPatientId());
          entity.setDentistId(registeredVO.getDentistId());
          entity.setDentistName(registeredVO.getDentistName());
          entity.setAssistantId(registeredVO.getAssistantId());
          entity.setAssistantName(StringHelper.isBlank(registeredVO.getAssistantName()) ?
                  "--" : registeredVO.getAssistantName());
          // 设置患者年龄
          List<PatientTotalInfoVo> collect = patientTotalInfo.stream().filter(
                  patientTotalInfoVo -> patientTotalInfoVo.getId().equals(registeredVO.getPatientId())).collect(Collectors.toList());
          if (StringHelper.isNotEmpty(collect)) {
            PatientTotalInfoVo patientTotalInfoVo = collect.get(0);
            if (null != patientTotalInfoVo) {
              entity.setPatientName(patientTotalInfoVo.getName());
              entity.setAge(patientTotalInfoVo.getAge());
              entity.setGender(registeredVO.getGender());
            }
          }
          entity.setOrgId(registeredVO.getOrgId());
          entity.setRegistedId(registeredVO.getId());
          patientTreatmentInfo4ListVOS.add(entity);
        });
      }
    }

    ///////////////////////////////////////// 就诊中/就诊完成/已结账/////////////////////////
    List<Integer> treatmentIds = new ArrayList<>();
    baseTreatmentProcessVOS.stream().filter(item -> item.getTreatmentId() != null && (item.getTreatStatus() >= 1))
            .forEach(item -> {
              treatmentIds.add(item.getTreatmentId());
            });
    if (StringHelper.isNotEmpty(treatmentIds)) {
      List<TreatmentRecordExtendVO> treatmentRecordExtendVOS = remoteTreatmentServiceFeign.findTreatmentRecordByIds(treatmentIds.stream().collect(Collectors.toSet()));
      // 去重
      treatmentRecordExtendVOS = treatmentRecordExtendVOS.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(item -> item.getId()))), ArrayList::new));
      // 获取就诊记录ID
      List<Integer> registeredIds = treatmentRecordExtendVOS.stream().map(TreatmentRecordExtendVO::getRegisteredId).collect(Collectors.toList());
      // 获取挂号信息
      List<RegisteredVO> registeredVOS = this.remoteTreatmentServiceFeign.registeredInfoDetails(registeredIds);

      if (StringHelper.isNotEmpty(treatmentRecordExtendVOS)) {
        for (TreatmentRecordExtendVO patientTreatmentRecordVO : treatmentRecordExtendVOS) {
          PatientTreatmentInfo4ListVO entity = new PatientTreatmentInfo4ListVO();
          // 设置就诊状态
          Byte treatmentStatus = patientTreatmentRecordVO.getStatus();
          switch (treatmentStatus) {
            case 0:
              // 就诊中
              entity.setTreatStatus((byte) 3);
              break;
            case 1:
            case 2:
              // 就诊完成
              entity.setTreatStatus((byte) 4);
              break;
            case 3:
              // 已结账
              entity.setTreatStatus((byte) 5);
              break;
            default:
              break;
          }
          Integer registeredId = patientTreatmentRecordVO.getRegisteredId();
          List<RegisteredVO> registeredCollection = registeredVOS.stream().filter(registeredVO -> registeredVO.getId().equals(registeredId)).collect(Collectors.toList());
          if (StringHelper.isNotEmpty(registeredCollection)) {
            RegisteredVO registeredVOInfo = registeredCollection.get(0);
            entity.setOrgId(patientTreatmentRecordVO.getOrgId());
            entity.setTreatmentId(patientTreatmentRecordVO.getId());
            entity.setAppointId(registeredVOInfo.getAppointmentId());
            entity.setRegistedId(registeredVOInfo.getId());
            entity.setNodeTime(dateFormat.format(patientTreatmentRecordVO.getTreatStartTime()));
            entity.setPatientId(registeredVOInfo.getPatientId());
            entity.setPatientName(registeredVOInfo.getPatientName());
            entity.setAge(registeredVOInfo.getAge());
            entity.setGender(registeredVOInfo.getGender());
            entity.setDentistId(registeredVOInfo.getDentistId());
            entity.setDentistName(registeredVOInfo.getDentistName());
            entity.setAssistantId(registeredVOInfo.getAssistantId());
            entity.setAssistantName(StringHelper.isBlank(registeredVOInfo.getAssistantName()) ?
                    "--" : registeredVOInfo.getAssistantName());
          }
          patientTreatmentInfo4ListVOS.add(entity);
        }
      }
    }
    pageInfo.setList(patientTreatmentInfo4ListVOS);
    return pageInfo;
  }


}
