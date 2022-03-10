package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentInfo4ListVO;
import com.yunya.feign.treatment.domain.vo.RegisteredVO;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordExtendVO;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya.feign.wechat.domain.vo.WxAppointConfirmPushVo;
import com.yunya.feign.wechat.enums.TemplateDataEnum;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.PageUtl;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.report.BaseOrganization;
import com.yunya.models.report.BasePatient;
import com.yunya.models.report.BasePatientOrigin;
import com.yunya.models.report.BaseTreatmentProcess;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.report.ultimate.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.feign.wechat.enums.TemplateEnum.APPOINT_CONFIRM;

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
  /** 组织信息 */
  @Autowired private BaseOrganizationMapper organizationMapper;
  /** 用户信息 */
  @Autowired private BaseEmployeeMapper employeeMapper;

  @Autowired private RemoteAppointmentFeign remoteAppointmentFeign;
  @Autowired private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;
  @Autowired private RemotePatientCentralServiceFeign patientCentralServiceFeign;
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  @Autowired private BaseOrganizationMapper baseOrganizationMapper;
  @Autowired private BasePatientMapper basePatientMapper;
  @Autowired private BasePatientOriginMapper basePatientOriginMapper;

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
    assemblyOriginType(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 装配患者来源类型和患者来源
   *
   * @param resultList
   */
  private void assemblyOriginType(List<TreatmentRecordReportVO> resultList) {
    if (StringHelper.isNotEmpty(resultList)) {
      Map<Integer, String> fromPatients = new HashMap<>(16);
      Map<Integer, String> fromEmployees = new HashMap<>(16);
      Map<Integer, String> fromSecondTypes = new HashMap<>(16);
      resultList.forEach(vo->{
        Integer originType = vo.getOriginType();
        Integer originId = vo.getOriginId();
        if (originType == null) {
          return;
        }
        if (originType == 1) {//员工转介绍
          if (!fromEmployees.containsKey(vo.getOriginId())) {
            fromEmployees.put(originId, null);
          }
        } else if (originType == 2) {//患者转介绍
          if (!fromPatients.containsKey(vo.getOriginId())) {
            fromPatients.put(originId, null);
          }
        } else {
          if (!fromSecondTypes.containsKey(vo.getOriginId())) {
            fromSecondTypes.put(originId, null);
          }
        }
      });
      if (StringHelper.isNotEmpty(fromEmployees)) {
        List<SysUserInfoDetail> employees = employeeMapper.selectUserInfoByIds(fromEmployees.keySet());
        employees.forEach(vo-> fromEmployees.put(vo.getUserId(), vo.getName()));
        resultList.forEach(vo->{
          Integer originType = vo.getOriginType();
          if (originType!=null && originType==1) {
            vo.setOriginSource(fromEmployees.get(vo.getOriginId()));
          }
        });
      }
      if (StringHelper.isNotEmpty(fromPatients)) {
        List<BasePatient> basePatients = basePatientMapper.findPatientInfoInId(fromPatients.keySet());
        basePatients.forEach(vo-> fromPatients.put(vo.getPatientId(), vo.getName()));
        resultList.forEach(vo->{
          Integer originType = vo.getOriginType();
          if (originType!=null && originType==2) {
            vo.setOriginSource(fromPatients.get(vo.getOriginId()));
          }
        });
      }
      if (StringHelper.isNotEmpty(fromSecondTypes)) {
        List<BasePatientOrigin> origins = basePatientOriginMapper.selectPatientOriginInIds(fromSecondTypes.keySet());
        origins.forEach(vo-> fromSecondTypes.put(vo.getId(), vo.getName()));
        resultList.forEach(vo->{
          Integer originType = vo.getOriginType();
          if (originType!=null && originType>2) {
            vo.setOriginSource(fromSecondTypes.get(vo.getOriginId()));
          }
        });
      }
    }
  }

  /**
   * 根据条件查询就诊列表并导出excel
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportTreatmentList(HttpServletResponse response, TreatmentRecordQuery query)
      throws IOException {
    query.setWhetherPage(false);
    List<TreatmentRecordReportVO> list = findTreatmentList(query).getList();
    ExcelUtil<TreatmentRecordReportVO> excelUtil = new ExcelUtil<>(TreatmentRecordReportVO.class);
    String fileName = "就诊记录明细";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (organization != null) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, list, "患者就诊记录", fileName);
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
    String fileName = "配诊记录表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (organization != null) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, list, "配诊记录表", fileName);
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
   * 患者档案-预约信息-履约次数/失约次数/改约次数/取消预约次数
   *
   * @param patientId 患者ID
   * @param query 扩展参数
   * @return
   */
  public ResponseResult<AppointmentCountVO> appointmentCount(
      Integer patientId, AppointmentCountQuery query) {
    AppointmentCountVO result = mapper.appointmentCount(patientId, query);
    return ResponseUtil.success(result);
  }

  /**
   * 查询APP端就诊列表
   *
   * @param query
   * @return
   */
  public PageInfo<PatientTreatmentInfo4ListVO> treatmentList4App(TreatmentList4AppQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<PatientTreatmentInfo4ListVO> patientTreatmentInfo4ListVOS = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    List<BaseTreatmentProcessVO> baseTreatmentProcessVOS =
        mapper.treatmentList4App(query.getOrgId(), query.getDentistId(), query.getQueryDate());
//    PageInfo pageInfo = null;
//    if (StringHelper.isEmpty(baseTreatmentProcessVOS)) {
//      return new PageInfo<>(baseTreatmentProcessVOS);
//    } else {
//      pageInfo = new PageInfo(baseTreatmentProcessVOS);
//    }
    // 获取患者ID集合
    List<Integer> patientIds =
        baseTreatmentProcessVOS.stream()
            .map(BaseTreatmentProcessVO::getPatientId)
            .collect(Collectors.toList());
    if (StringHelper.isEmpty(patientIds)) {
      throw new ClientServiceException("患者ID不能为空", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
    // 查询患者信息
    List<PatientTotalInfoVo> patientTotalInfo =
        patientCentralServiceFeign.findPatientTotalInfo(patientIds);
    //////////////////////////////////////////////////// 预约未到 ////////////////////////////////
    // 获取预约未到的预约ID
    List<Integer> appointIds = new ArrayList<>();
    baseTreatmentProcessVOS.stream()
        .filter(
            baseTreatmentProcessVO ->
                null != baseTreatmentProcessVO.getAppointmentId()
                    && null != baseTreatmentProcessVO.getAppointStatus() // 兼容一代数据
                    && baseTreatmentProcessVO.getAppointStatus() < 2
                    && baseTreatmentProcessVO.getRegisteredId() == null)
        .forEach(
            baseTreatmentProcessVO -> {
              appointIds.add(baseTreatmentProcessVO.getAppointmentId());
            });
    if (StringHelper.isNotEmpty(appointIds)) {
      // 查询预约助手
      List<Appointment> appointmentListByIds =
          remoteAppointmentFeign.findAppointmentListByIds(appointIds);
      // 获取预约助手ID
      List<Integer> assistantIds = new ArrayList<>();
      // 查询预约助手信息
      List<SysUserInfoDetail> assistantInfos = null;
      if (StringHelper.isNotEmpty(appointmentListByIds)) {
        appointmentListByIds.forEach(
            appointment -> assistantIds.add(appointment.getAssistantId()));
        // 查询预约助手信息
        if (StringHelper.isNotEmpty(assistantIds)) {
          assistantInfos =
              this.employeeMapper.selectUserInfoByIds(assistantIds);
        }
      }
      for (BaseTreatmentProcessVO appointmentUnDonePatientInfoVO : baseTreatmentProcessVOS) {
        Integer appointmentId = appointmentUnDonePatientInfoVO.getAppointmentId();
        if (appointIds.contains(appointmentId)) {
          PatientTreatmentInfo4ListVO entity = new PatientTreatmentInfo4ListVO();
          entity.setOrgId(appointmentUnDonePatientInfoVO.getOrgId());
          entity.setAppointId(appointmentUnDonePatientInfoVO.getAppointmentId());
          entity.setTreatStatus((byte) 1);
          Date appointStartTime = appointmentUnDonePatientInfoVO.getAppointStartTime();
          if (null != appointStartTime) {
            entity.setNodeTime(dateFormat.format(appointStartTime));
          }
          entity.setPatientId(appointmentUnDonePatientInfoVO.getPatientId());
          entity.setDentistId(appointmentUnDonePatientInfoVO.getAppointDentistId());
          entity.setDentistName(appointmentUnDonePatientInfoVO.getAppointDentistName());
          if (StringHelper.isNotEmpty(assistantInfos)) {
            List<Appointment> collect =
                appointmentListByIds.stream()
                    .filter(
                        appointment ->
                            appointment
                                .getId()
                                .equals(appointmentUnDonePatientInfoVO.getAppointmentId()))
                    .collect(Collectors.toList());
            if (StringHelper.isNotEmpty(collect)) {
              Appointment appointment = collect.get(0);
              // 获取预约助手信息
              List<SysUserInfoDetail> assistantInfoList =
                  assistantInfos.stream()
                      .filter(
                          sysUserInfoDetail ->
                              sysUserInfoDetail.getUserId().equals(appointment.getAssistantId()))
                      .collect(Collectors.toList());
              if (StringHelper.isNotEmpty(assistantInfoList)) {
                SysUserInfoDetail userInfoDetail = assistantInfoList.get(0);
                entity.setAssistantId(appointment.getAssistantId());
                entity.setAssistantName(
                    StringHelper.isBlank(userInfoDetail.getName())
                        ? "--"
                        : userInfoDetail.getName());
              }
            }
          }
          // 设置患者年龄
          List<PatientTotalInfoVo> collect =
              patientTotalInfo.stream()
                  .filter(
                      patientTotalInfoVo ->
                          patientTotalInfoVo
                              .getId()
                              .equals(appointmentUnDonePatientInfoVO.getPatientId()))
                  .collect(Collectors.toList());
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
    baseTreatmentProcessVOS.stream()
        .filter(
            baseTreatmentProcessVO ->
                null != baseTreatmentProcessVO.getRegisteredId()
                    && baseTreatmentProcessVO.getTreatStatus() == 0)
        .forEach(
            baseTreatmentProcessVO -> {
              registerIds.add(baseTreatmentProcessVO.getRegisteredId());
            });
    if (StringHelper.isNotEmpty(registerIds)) {
      List<RegisteredVO> registeredVOS =
          remoteTreatmentServiceFeign.registeredInfoDetails(registerIds);
      if (StringHelper.isNotEmpty(registeredVOS)) {
        registeredVOS.forEach(
            registeredVO -> {
              PatientTreatmentInfo4ListVO entity = new PatientTreatmentInfo4ListVO();
              entity.setOrgId(registeredVO.getOrgId());
              entity.setAppointId(registeredVO.getAppointmentId());
              entity.setTreatStatus((byte) 2);
              entity.setNodeTime(dateFormat.format(registeredVO.getRegTime()));
              entity.setPatientId(registeredVO.getPatientId());
              entity.setDentistId(registeredVO.getDentistId());
              entity.setDentistName(registeredVO.getDentistName());
              entity.setAssistantId(registeredVO.getAssistantId());
              entity.setAssistantName(
                  StringHelper.isBlank(registeredVO.getAssistantName())
                      ? "--"
                      : registeredVO.getAssistantName());
              // 设置患者年龄
              List<PatientTotalInfoVo> collect =
                  patientTotalInfo.stream()
                      .filter(
                          patientTotalInfoVo ->
                              patientTotalInfoVo.getId().equals(registeredVO.getPatientId()))
                      .collect(Collectors.toList());
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
    baseTreatmentProcessVOS.stream()
        .filter(item -> item.getTreatmentId() != null && (item.getTreatStatus() >= 1))
        .forEach(
            item -> {
              treatmentIds.add(item.getTreatmentId());
            });
    if (StringHelper.isNotEmpty(treatmentIds)) {
      List<TreatmentRecordExtendVO> treatmentRecordExtendVOS =
          remoteTreatmentServiceFeign.findTreatmentRecordByIds(new HashSet<>(treatmentIds));
      // 去重
      treatmentRecordExtendVOS =
          treatmentRecordExtendVOS.stream()
              .collect(
                  Collectors.collectingAndThen(
                      Collectors.toCollection(
                          () -> new TreeSet<>(Comparator.comparing(TreatmentRecord::getId))),
                      ArrayList::new));
      // 获取就诊记录ID
      List<Integer> registeredIds =
          treatmentRecordExtendVOS.stream()
              .map(TreatmentRecordExtendVO::getRegisteredId)
              .collect(Collectors.toList());
      // 获取挂号信息
      List<RegisteredVO> registeredVOS =
          this.remoteTreatmentServiceFeign.registeredInfoDetails(registeredIds);

      if (StringHelper.isNotEmpty(treatmentRecordExtendVOS)) {
        for (TreatmentRecordExtendVO patientTreatmentRecordVO : treatmentRecordExtendVOS) {
          PatientTreatmentInfo4ListVO entity = new PatientTreatmentInfo4ListVO();
          // 设置就诊状态
          Byte treatmentStatus = patientTreatmentRecordVO.getStatus();
          switch (treatmentStatus) {
            case 0:
            case 1:
              // 就诊中
              entity.setTreatStatus((byte) 3);
              break;
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
          List<RegisteredVO> registeredCollection =
              registeredVOS.stream()
                  .filter(registeredVO -> registeredVO.getId().equals(registeredId))
                  .collect(Collectors.toList());
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
            entity.setAssistantName(
                StringHelper.isBlank(registeredVOInfo.getAssistantName())
                    ? "--"
                    : registeredVOInfo.getAssistantName());
          }
          patientTreatmentInfo4ListVOS.add(entity);
        }
      }
    }
    return PageUtl.doPage(query, patientTreatmentInfo4ListVOS);
//    pageInfo.setList(patientTreatmentInfo4ListVOS);
//    return pageInfo;
  }

  /**
   * 根据条件查询初诊患者来源分布
   *
   * @param query 查询条件
   * @return PatientFirstTreatOriginInfoVO
   */
  public PatientFirstTreatOriginInfoVO findPatientFirstTreatOriginInfo(
      PatientFirstTreatOriginQuery query) {
    PatientFirstTreatOriginInfoVO resultData = new PatientFirstTreatOriginInfoVO();
    Integer totalCount = mapper.selectFirstTreatTotalCount(query);
    List<PatientFirstTreatOriginVO> firstTreatOrigins =
        mapper.selectPatientFirstTreatOriginList(query);
    if (StringHelper.isNotEmpty(firstTreatOrigins)) {
      for (PatientFirstTreatOriginVO treatOrigin : firstTreatOrigins) {
        Integer firstTreatCount = treatOrigin.getFirstTreatCount();
        if (null != firstTreatCount) {
          if (null != totalCount && 0 != totalCount) {
            treatOrigin.setFirstTreatPercentage(
                BigDecimal.valueOf((float) firstTreatCount / totalCount)
                    .multiply(new BigDecimal(100))
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
          } else {
            treatOrigin.setFirstTreatPercentage(
                BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
          }
        }
      }
    }
    resultData.setFirstTreatTotalCount(totalCount);
    resultData.setPatientFirstTreatOrigins(firstTreatOrigins);
    return resultData;
  }

  /**
   * 根据条件查询初诊记录报表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<FirstVisitVO> firstVisitRecord(FirstVisitQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<FirstVisitVO> resultList = mapper.firstVisitRecord(query);
    return new PageInfo<>(resultList);
  }


  /**
   * 根据条件导出初诊记录报表
   *
   * @param query 查询条件
   * @return
   */
  public void exportFirstVisitRecord(HttpServletResponse response,FirstVisitQuery query) throws IOException{
    List<FirstVisitVO>list = mapper.firstVisitRecord(query);
    ExcelUtil<FirstVisitVO> excelUtil = new ExcelUtil<>(FirstVisitVO.class);
    BaseOrganization baseOrganization = new BaseOrganization();
    baseOrganization.setOrgId(query.getOrgId());
    String orgName = baseOrganizationMapper.selectOne(baseOrganization).getAbbreviation();
    String fileName =
            excelUtil.getFileName(
                    query.getStartDate(),
                    query.getEndDate(),
                    orgName,
                    "初诊统计表");
    excelUtil.exportExcel(response, list, "初诊统计", fileName);
  }


  /**
   * 根据条件查询初诊记录报表患者合计数量
   *
   * @param query 查询条件
   * @return
   */
  public Integer firstVisitRecordCount(FirstVisitQuery query) {
    Integer count = mapper.firstVisitRecordCount(query);
    return count;
  }
  /**
   * 根据条件查询初诊记录报表查看明细
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<FirstVisitDetailVO> firstVisitRecordDetail(FirstVisitDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<FirstVisitDetailVO>list = mapper.firstVisitRecordDetail(query);
    return new PageInfo<>(list);
  }

  /**
   * 根据条件导出初诊记录报表查看明细
   *
   * @param query 查询条件
   * @return
   */
  public void exportFirstVisitRecordDetail(HttpServletResponse response,FirstVisitDetailQuery query) throws IOException{
    List<FirstVisitDetailVO>list = mapper.firstVisitRecordDetail(query);
    ExcelUtil<FirstVisitDetailVO> excelUtil = new ExcelUtil<>(FirstVisitDetailVO.class);
    BaseOrganization baseOrganization = new BaseOrganization();
    baseOrganization.setOrgId(query.getOrgId());
    String orgName = baseOrganizationMapper.selectOne(baseOrganization).getAbbreviation();
    String fileName =
            excelUtil.getFileName(
                    query.getStartDate(),
                    query.getEndDate(),
                    orgName,
                    "初诊统计明细表");
    excelUtil.exportExcel(response, list, "初诊统计明细表", fileName);
  }

  /**
   * 根据条件查询个人初诊记录报表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<FirstVisitPersonalVO> firstVisitRecordPersonalList(FirstVisitPersonalQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    query.setUserId(Integer.valueOf(BaseContextHandler.getUserID()));
    List<FirstVisitPersonalVO>list = mapper.firstVisitRecordPersonalList(query);
    return new PageInfo<>(list);
  }
  /**
   * 根据条件导出个人初诊记录报表
   *
   * @param query 查询条件
   * @return
   */
  public void exportFirstVisitRecordPersonalList(HttpServletResponse response,FirstVisitPersonalQuery query) throws IOException{
    query.setUserId(Integer.valueOf(BaseContextHandler.getUserID()));
    List<FirstVisitPersonalVO>list = mapper.firstVisitRecordPersonalList(query);
    ExcelUtil<FirstVisitPersonalVO> excelUtil = new ExcelUtil<>(FirstVisitPersonalVO.class);
    String fileName =
            excelUtil.getFileName(
                    query.getStartDate(),
                    query.getEndDate(),
                    BaseContextHandler.getUsername(),
                    "个人初诊统计表");
    excelUtil.exportExcel(response, list, "个人初诊统计表", fileName);
  }

  public List<WxTemplateMsgModel> listAppointConfirmPush() {
    List<WxAppointConfirmPushVo> appointList = mapper.listAppointConfirm();
    List<OrganizationInfoDetail> orgList = systemServiceFeign.findOrgInfoInIds(Lists.newArrayList(appointList.stream()
            .map(WxAppointConfirmPushVo::getOrgId).collect(Collectors.toSet())));
    Map<Integer, OrganizationInfoDetail> orgMap = orgList.stream()
            .collect(Collectors.toMap(OrganizationInfoDetail::getId, Function.identity()));
    return appointList.stream().map(obj -> {
      OrganizationInfoDetail org = orgMap.get(obj.getOrgId());
      WxTemplateMsgModel model = new WxTemplateMsgModel();
      Map<String, Object> map = Maps.newHashMapWithExpectedSize(16);
      model.setPatientId(obj.getPatientId());
      map.put(TemplateDataEnum.PATIENT_NAME.getArgName(), obj.getPatientName());
      map.put(TemplateDataEnum.APPOINT_DATE.getArgName(), getFormatDate(obj.getAppointDate()));
      map.put(TemplateDataEnum.APPOINT_ID.getArgName(), obj.getAppointId());
      model.setTemplateEnum(APPOINT_CONFIRM);
      map.put("keyword1", obj.getAppointDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")));
      map.put("keyword2", obj.getAppointDuration() + "分钟");
      map.put("keyword3", obj.getOrgName());
      map.put("keyword4", org.getAddress());
      map.put(TemplateDataEnum.LINK_MOBILE.getArgName(), org.getTel());
      model.setParamMap(map);
      return model;
    }).collect(Collectors.toList());
  }

  private String getFormatDate(LocalDateTime appointDate) {
    int i = appointDate.get(ChronoField.AMPM_OF_DAY);
    if (i == 0) {
      return appointDate.format(DateTimeFormatter.ofPattern("MM月dd日上午HH:mm"));
    }
    if (i == 1) {
      return appointDate.format(DateTimeFormatter.ofPattern("MM月dd日下午HH:mm"));
    }
    return "";
  }


  /**
   * 查询在条件门诊就诊过的患者
   * @param form 条件
   * @return
   */
    public List<PatientBaseInfoVo> findPatientLikePatientInfo(PatientLikeFinleQueryForm form) {
      if (form.getWhetherPage()) {
        PageHelper.startPage(form.getPageNum(), form.getPageSize());
      }
      return mapper.findPatientLikePatientInfo(form,34);
    }

  /**
   * 查询本月初诊且复诊
   * @return
   * @param query
   * @param groupByOrgId
   */
  public List<EmployeeCountVO> findInMonthReFirstVisit(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId, Integer treatType) {
    return mapper.selectInMonthReFirstVisit(query, groupByOrgId, treatType);
  }

  public List<PatientDateVO> findNextAppointDateByPatientId(List<Integer> patientIds) {
    return mapper.selectNextAppointDateByPatientId(patientIds);
  }

  public List<PatientDateVO> findFirstVisitDateByPatientId(List<Integer> patientIds) {
    return mapper.selectFirstVisitDateByPatientId(patientIds);
  }

  public List<PatientDateVO> findLastVisitDateByPatientId(List<Integer> patientIds) {
    return mapper.selectLastVisitDateByPatientId(patientIds);
  }

  public List<OrgPatientCountVO> findPatientTreatNum(PatientDimensionQueryForm query) {
    return mapper.selectPatientTreatNum(query);
  }

  public List<EmployeeCountVO> findPatientTreatNumGroupEmp(ClinicEmployeeWorkloadQuery query, Integer treatType, boolean groupByOrgId) {
    return mapper.selectPatientTreatNumGroupEmp(query, treatType, groupByOrgId);
  }

  public List<EmployeeCountVO> findTreatVisitsTimes(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) {
    return mapper.selectTreatVisitsTimes(query, groupByOrgId);
  }

  public List<EmployeeCountVO> findHasntAppointAndRemind(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) {
    return mapper.selectHasntAppointAndRemind(query, groupByOrgId);
  }

  public List<EmployeeFirstVisitOriginTypeVO> findFirstVisitPatientOriginType(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) {
    return mapper.selectFirstVisitPatientOriginType(query, groupByOrgId);
  }

  public List<StatTreatVO> findTreatVisitPatientList(MultiClinicDateRangeQueryForm query, List<Integer> employeeIds) {
    return mapper.selectTreatVisitPatientList(query, employeeIds);
  }
}
