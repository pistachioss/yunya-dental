package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.appointment.domain.form.AppointmentForMonthForm;
import com.yunya.feign.appointment.domain.query.AppointmentCurrentListQuery;
import com.yunya.feign.appointment.vo.NextAppointsVo;
import com.yunya.feign.emr.RemoteEmrServiceFeign;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.RemoteMiddleTableServiceFeign;
import com.yunya.feign.report.domain.query.TreatmentList4AppQuery;
import com.yunya.feign.report.domain.vo.BaseTreatmentProcessVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.model.TreatmentModel;
import com.yunya.feign.treatment.domain.query.*;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.feign.treatment_other.domain.vo.NextVisitingRecordVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.DepartmentRoom;
import com.yunya.models.system.MemberType;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.treatment.*;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.models.treatment_other.XRayFilm;
import com.yunya.modules.treatment.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseBill;
import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseTreatmentProcess;
import static com.yunya.framework.common.constant.BusinessConstants.TREATMENT_PROCESSING_STATUS;
import static com.yunya.framework.common.constant.BusinessConstants.TREATMENT_PROCESS_ORDER_STATUS;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.REDIS_KEY_TREATMENT_ING;

/**
 * 简介: 就诊记录管理业务层
 *
 * @author: chow
 * @date: 2020/8/12 14:55
 * @description:
 * @since: 1.0.0
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class TreatmentRecordBiz extends BaseBiz<TreatmentRecordMapper, TreatmentRecord> {

  /** 缓存 */
  @Autowired private RedisUtils redisUtils;
  /** 患者服务调用 */
  @Autowired private RemotePatientCentralServiceFeign patientServiceFeign;
  /** 预约服务调用 */
  @Autowired private RemoteAppointmentFeign appointmentFeign;
  /** 消息中间件调用 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 就诊其他信息服务调用 */
  @Autowired private RemoteTreatmentOtherFeign treatmentOtherFeign;
  /** 基础价目表 */
  @Autowired private BaseTariffBiz baseTariffBiz;
  /** 挂号 */
  @Autowired private RegisteredMapper registeredMapper;
  /** 开单 */
  @Autowired private OrderRecordMapper orderRecordMapper;
  /** 开单明细 */
  @Autowired private OrderDetailMapper orderDetailMapper;
  /** 账单记录 */
  @Autowired private BillRecordMapper billRecordMapper;
  /** 就诊关联助手 */
  @Autowired private AssistantMatchingRecordMapper assistantMatchingRecordMapper;
  /** 随访提醒，图片影像 */
  @Autowired private RemoteTreatmentOtherFeign remoteTreatmentOther;
  /** 电子病历 */
  @Autowired private RemoteEmrServiceFeign remoteEmrServiceFeign;
  /** 挂号服务 */
  @Autowired
  private RegisteredBiz registeredBiz;
  @Autowired
  private RemoteMiddleTableServiceFeign remoteMiddleTableServiceFeign;

  /**
   * 开始接诊
   *
   * @param model 挂号ID
   */
  public void startTreatment(TreatmentModel model) {
    Integer regId = model.getRegId();
    Byte postType = model.getPostType();
    Registered regResult = registeredMapper.selectByPrimaryKey(regId);
    if (null == regResult || !regResult.getInservice()) {
      throw new ClientServiceException("接诊失败，您当前未选择接诊患者或传入参数有误！", QUERY_RESULT_INVALID);
    }
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    Integer dentistId = regResult.getDentistId();
    if (!dentistId.equals(userId)) {
      throw new ClientServiceException("接诊失败，当前挂号医生与接诊医生不是同一个人！", PARAMETERS_IS_ILLEGAL);
    }
    String treatingKey = REDIS_KEY_TREATMENT_ING + regId;
    String treatingValue = redisUtils.get(treatingKey);
    if (StringHelper.isNotBlank(treatingValue)) {
      throw new ClientServiceException("接诊失败，当前挂号正在被操作，请稍后再试！", SAME_DATA_EXIST);
    }
    int count = mapper.selectCountByRegisteredId(regId);
    if (count > 0) {
      throw new ClientServiceException("接诊失败，该挂号已被接诊，无法再次接诊！", DATA_EXIST);
    }
    redisUtils.set(treatingKey, regId, 5);

    TreatmentRecord entity = new TreatmentRecord();
    Integer orgId = regResult.getOrgId();
    entity.setOrgId(orgId);
    Integer appointmentId = regResult.getAppointmentId();
    entity.setAppointmentId(appointmentId);
    entity.setRegisteredId(regId);
    entity.setDentistId(regResult.getDentistId());
    Integer patientId = regResult.getPatientId();
    entity.setPatientId(patientId);
    PatientBaseInfo patientBaseInfo = patientServiceFeign.findPatientInfoById(patientId);
    if (null != patientBaseInfo) {
      String medicalNumber = patientBaseInfo.getMedicalNumber();
      if (StringHelper.isNotBlank(medicalNumber)) {
        entity.setType((byte) 1);
      } else {
        entity.setType((byte) 0);
        // 患者初诊，初始化病历号
        medicalNumber = generateMedicalRecordNumber(orgId);
        patientBaseInfo.setMedicalNumber(medicalNumber);
        patientServiceFeign.updatePatientInfo(patientBaseInfo);
      }
    }
    String name = BaseContextHandler.getName();
    entity.setTreatStartTime(new Date(System.currentTimeMillis()));
    entity.setCrtId(userId);
    entity.setCrtName(name);

    int i = mapper.insertSelective(entity);
    redisUtils.delete(treatingKey);

    if (postType == 0) {
      AssistantMatchingRecord matchingRecord = new AssistantMatchingRecord();
      matchingRecord.setOrgId(orgId);
      matchingRecord.setTreatmentRecordId(entity.getId());
      matchingRecord.setAssistantId(userId);
      matchingRecord.setType((byte) 0);
      matchingRecord.setOperatorPostType((byte) 0);
      matchingRecord.setCrtId(userId);
      matchingRecord.setCrtName(name);
      assistantMatchingRecordMapper.insertSelective(matchingRecord);
    }

    regResult.setStatus((byte) 1);
    regResult.setUpdId(userId);
    regResult.setUpdName(name);
    registeredMapper.updateByPrimaryKeySelective(regResult);
    // todo 发送消息更新患者数据
    if (i > 0) {
      if (null != appointmentId) {
        rabbitMqServiceFeign.sendMessage(appointmentId, 0, 1, BaseTreatmentProcess);
      } else {
        rabbitMqServiceFeign.sendMessage(regId, 1, 1, BaseTreatmentProcess);
      }
    }
  }

  /**
   * 初始化患者病历号
   *
   * @param orgId 组织ID
   * @return
   */
  private String generateMedicalRecordNumber(Integer orgId) {
    String number = patientServiceFeign.findMedicalNumberByOrgId(orgId);
    String suffix = String.format("%06d", Integer.parseInt(number) + 1);
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
    return String.format("%03d", Integer.parseInt(orgInfo.getClinicNumber()))
        + new DateTime().toString("yyMMdd")
        + suffix;
  }

  /**
   * 根据就诊记录ID列表查询就诊记录列表
   *
   * @param ids 就诊记录ID列表
   * @return
   */
  public List<TreatmentRecordExtendVO> selectByIds(Set<Integer> ids) {
    return mapper.selectByIds(ids);
  }

  /**
   * 根据就诊记录ID查询就诊信息
   *
   * @param id 就诊记录ID
   * @return
   */
  public TreatmentRecordVO findTreatmentInfoById(Integer id) {
    TreatmentRecordVO resultData = mapper.selectTreatmentInfoById(id);
    if (null != resultData) {
      Integer orgId = resultData.getOrgId();
      // todo 从缓存中查询组织
      OrganizationInfo organizationInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
      if (null != organizationInfo) {
        resultData.setOrgName(organizationInfo.getAbbreviation());
      }
      Integer patientId = resultData.getPatientId();
      // todo 从缓存中查询患者
      PatientBaseInfo patientBaseInfo = patientServiceFeign.findPatientInfoById(patientId);
      if (null != patientBaseInfo) {
        resultData.setPatientName(patientBaseInfo.getName());
      }
      Integer dentistId = resultData.getDentistId();
      // todo 从缓存中查询员工
      SysEmployee employee = systemServiceFeign.findSysEmployeeById(dentistId);
      if (null != employee) {
        resultData.setDentistName(employee.getName());
      }
    }
    return resultData;
  }

  /**
   * 根据条件查询就诊中患者信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<TreatmentPatientInfoVO> findTreatList(TreatmentRecordQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    String currentDate = queryForm.getCurrentDate();
    List<TreatmentPatientInfoVO> treatingList = mapper.selectTreatingList(queryForm);

    List<Integer> patientIds = new ArrayList<>();
    List<Integer> appointIds = new ArrayList<>();
    List<Integer> registerIds = new ArrayList<>();
    List<Integer> treatmentIds = new ArrayList<>();
    List<Integer> dentistAndAssistantIds = new ArrayList<>();
    List<Integer> deptRoomIds = new ArrayList<>();

    if (StringHelper.isNotEmpty(treatingList)) {
      for (TreatmentPatientInfoVO vo : treatingList) {
        if (null != vo.getPatientId()) {
          patientIds.add(vo.getPatientId());
        }
        if (null != vo.getAppointmentId()) {
          appointIds.add(vo.getAppointmentId());
        }
        if (null != vo.getRegisteredId()) {
          registerIds.add(vo.getRegisteredId());
        }
        if (null != vo.getTreatDentistId()) {
          dentistAndAssistantIds.add(vo.getTreatDentistId());
        }
        if (null != vo.getAppointDentistId()) {
          dentistAndAssistantIds.add(vo.getAppointDentistId());
        }
        if (null != vo.getAppointAssistantId()) {
          dentistAndAssistantIds.add(vo.getAppointAssistantId());
        }
        if (null != vo.getId()) {
          treatmentIds.add(vo.getId());
        }
      }

      // 1.根据患者ID集合查询患者信息列表
      List<PatientTotalInfoVo> patientTotalInfos =
          patientServiceFeign.findPatientTotalInfo(patientIds);
      // 2.根据预约ID集合查询患者预约信息列表
      List<Appointment> appointments = appointmentFeign.findAppointmentListByIds(appointIds);
      // 3.根据挂号ID集合查询患者挂号信息列表
      List<Registered> registereds = registeredMapper.selectRegisteredListByIds(registerIds);
      // 3.1从挂号信息列表中查询挂号科室ID和挂号助手ID和挂号医生ID
      registereds.forEach(
          registered -> {
            if (null != registered.getDeptRoomId()) {
              deptRoomIds.add(registered.getDeptRoomId());
            }
            if (null != registered.getDentistId()) {
              dentistAndAssistantIds.add(registered.getDentistId());
            }
            if (null != registered.getAssistantId()) {
              dentistAndAssistantIds.add(registered.getAssistantId());
            }
          });
      // 4.根据医生ID集合和助手ID信息列表
      List<SysUserInfoDetail> dentistAndAssistantInfo =
          systemServiceFeign.findSysUserEmployeeInfoByUserIds(dentistAndAssistantIds);
      // 5.根据就诊记录ID查询订单详情列表
      List<OrderRecord> orderRecords =
          orderRecordMapper.selectOrderRecordByTreatmentIds(treatmentIds);
      // 6.根据患者ID集合查询患者后续预约列表
      List<NextAppointsVo> nextAppointsVos = appointmentFeign.countNextAppoints(patientIds);
      // 7.根据患者ID集合和当前日期查询后续随访信息列表(不包含当天)
      List<NextVisitingRecordVo> nextVisitingRecordVos =
          remoteTreatmentOther.countNextVisitingListByIds(patientIds, currentDate);
      // 8.根据预约科室ID集合查询科室信息
      List<DepartmentRoom> departmentRoomInfos =
          systemServiceFeign.findDepartmentRoomByIds(deptRoomIds);
      // 9.根据患者ID查询患者账单统计数据列表
      List<PatientBillStatistics> patientBillStatistics =
          billRecordMapper.selectPatientBillStatisticsByPatientIds(patientIds);
      // 10.根据患者就诊记录ID查询账单支付记录
      List<BillRecord> billRecords = billRecordMapper.selectBillRecordsByTreatmentIds(treatmentIds);

      for (TreatmentPatientInfoVO vo : treatingList) {
        switch (vo.getTreatmentStatus()) {
          case 0:
          case 1:
          case 2:
            // 设置患者信息
            setPatientInfo(vo, patientTotalInfos, nextAppointsVos, nextVisitingRecordVos);
            // 设置预约信息
            setApppointmentInfo(vo, appointments, dentistAndAssistantInfo, departmentRoomInfos);
            // 设置挂号信息
            setRegisteredInfo(vo, registereds, dentistAndAssistantInfo, departmentRoomInfos);
            // 设置接诊信息
            setTreatingInfo(vo, dentistAndAssistantInfo, patientBillStatistics);
            // 设置账单信息
            setOrderInfo(vo, orderRecords);
            break;
          case 3:
            // 设置患者信息
            setPatientInfo(vo, patientTotalInfos, nextAppointsVos, nextVisitingRecordVos);
            // 设置预约信息
            setApppointmentInfo(vo, appointments, dentistAndAssistantInfo, departmentRoomInfos);
            // 设置挂号信息
            setRegisteredInfo(vo, registereds, dentistAndAssistantInfo, departmentRoomInfos);
            // 设置接诊信息
            setTreatingInfo(vo, dentistAndAssistantInfo, patientBillStatistics);
            // 设置账单信息
            setOrderInfo(vo, orderRecords);
            // 设置收费信息
            setChargeInfo(vo, billRecords);
            break;
          default:
            break;
        }
      }

    } else {
      treatingList = new ArrayList<>();
    }
    return new PageInfo<>(treatingList);
  }

  /**
   * 设置候诊患者患者信息
   *
   * @param vo 患者候诊
   * @param patientTotalInfos 患者信息列表
   * @param nextAppointsVos 后续预约列表
   * @param nextVisitingRecordVos 后续随访你列表
   */
  private void setPatientInfo(
      TreatmentPatientInfoVO vo,
      List<PatientTotalInfoVo> patientTotalInfos,
      List<NextAppointsVo> nextAppointsVos,
      List<NextVisitingRecordVo> nextVisitingRecordVos) {
    Integer patientId = vo.getPatientId();
    if (StringHelper.isNotEmpty(patientTotalInfos)) {
      // 根据患者ID检索患者信息
      List<PatientTotalInfoVo> collect =
          patientTotalInfos.stream()
              .filter(patientTotalInfoVo -> patientTotalInfoVo.getId().equals(patientId))
              .collect(Collectors.toList());
      if (StringHelper.isNotEmpty(collect)) {
        PatientTotalInfoVo patientData = collect.get(0);
        if (null != patientData) {
          vo.setPatientName(patientData.getName());
          vo.setMobile(patientData.getMobile());
          vo.setGender(patientData.getGender());
          vo.setAge(patientData.getAge());
          vo.setBirthday(patientData.getBirthday());
          vo.setPatientRemark(patientData.getRemarks());
          vo.setPatientKind(patientData.getPatientKindName());
          String medicalNumber = patientData.getMedicalNumber();
          vo.setMedicalNumber(StringHelper.isNotBlank(medicalNumber) ? medicalNumber : "--");
          vo.setAllergen(patientData.getAllergens());
          // 设置后续预约未到数量
          if (StringHelper.isNotEmpty(nextAppointsVos)) {
            List<NextAppointsVo> nextAppoints =
                nextAppointsVos.stream()
                    .filter(nextAppointsVo -> nextAppointsVo.getPatientId().equals(patientId))
                    .collect(Collectors.toList());
            if (StringHelper.isNotEmpty(nextAppoints)) {
              NextAppointsVo nextAppointsVo = nextAppoints.get(0);
              vo.setNextAppointment(nextAppointsVo.getCount());
            }
          }
          // 设置后续随访数量
          if (StringHelper.isNotEmpty(nextVisitingRecordVos)) {
            List<NextVisitingRecordVo> nextVisitingRecords =
                nextVisitingRecordVos.stream()
                    .filter(
                        nextVisitingRecordVo ->
                            nextVisitingRecordVo.getPatientId().equals(patientId))
                    .collect(Collectors.toList());
            if (StringHelper.isNotEmpty(nextVisitingRecords)) {
              NextVisitingRecordVo nextVisitingRecordVo = nextVisitingRecords.get(0);
              vo.setNextInterview(nextVisitingRecordVo.getCount());
            }
          }
          // 设置会员类型
          Integer memberTypeId = patientData.getMemberTypeId();
          if (null != memberTypeId) {
            MemberType memberType = systemServiceFeign.findMemberTypeById(memberTypeId);
            if (null != memberType) {
              vo.setMemberIcon(String.valueOf(memberType.getIcon()));
              vo.setMemberCardName(memberType.getName());
            }
          }
        }
      }
    }
  }

  /**
   * 设置候诊患者预约信息
   *
   * @param vo 患者候诊信息
   * @param appointments 预约信息列表
   * @param dentistAndAssistantInfo 医生和患者信息列表
   * @param departmentRoomInfos 科室信息列表
   */
  private void setApppointmentInfo(
      TreatmentPatientInfoVO vo,
      List<Appointment> appointments,
      List<SysUserInfoDetail> dentistAndAssistantInfo,
      List<DepartmentRoom> departmentRoomInfos) {
    if (StringHelper.isNotEmpty(appointments)) {
      Integer appointmentId = vo.getAppointmentId();
      if (null != appointmentId) {
        List<Appointment> collect =
            appointments.stream()
                .filter(appointment -> appointment.getId().equals(appointmentId))
                .collect(Collectors.toList());
        if (StringHelper.isNotEmpty(collect)) {
          Appointment appointment = collect.get(0);
          if (null != appointment) {
            // 设置预约医生信息
            Integer appointmentDentistId = appointment.getDentistId();
            if (null != appointmentDentistId) {
              List<SysUserInfoDetail> dentistInfo =
                  dentistAndAssistantInfo.stream()
                      .filter(
                          sysUserInfoDetail ->
                              sysUserInfoDetail.getUserId().equals(appointmentDentistId))
                      .collect(Collectors.toList());
              if (StringHelper.isNotEmpty(dentistInfo)) {
                SysUserInfoDetail userInfoDetail = dentistInfo.get(0);
                vo.setAppointDentistName(null != userInfoDetail ? userInfoDetail.getName() : "--");
              }
            }
            // 设置助手信息
            Integer appointAssistantId = vo.getAppointAssistantId();
            if (null != appointAssistantId) {
              vo.setAppointAssistantId(appointAssistantId);
              List<SysUserInfoDetail> appointAssistantInfo =
                  dentistAndAssistantInfo.stream()
                      .filter(
                          sysUserInfoDetail ->
                              sysUserInfoDetail.getUserId().equals(appointAssistantId))
                      .collect(Collectors.toList());
              if (StringHelper.isNotEmpty(appointAssistantInfo)) {
                SysUserInfoDetail userInfoDetail = appointAssistantInfo.get(0);
                vo.setAppointAssistantName(
                    null != userInfoDetail ? userInfoDetail.getName() : "--");
              }
            }
            // 设置科室信息
            Integer appointDeptRoomId = vo.getAppointDeptRoomId();
            if (null != appointDeptRoomId) {
              vo.setAppointDeptRoomId(appointDeptRoomId);
              List<DepartmentRoom> departmentRoomInfo =
                  departmentRoomInfos.stream()
                      .filter(departmentRoom -> departmentRoom.getId().equals(appointDeptRoomId))
                      .collect(Collectors.toList());
              if (StringHelper.isNotEmpty(departmentRoomInfo)) {
                DepartmentRoom departmentRoom = departmentRoomInfo.get(0);
                vo.setAppointDeptRoomName(null != departmentRoom ? departmentRoom.getName() : "--");
              }
            }
            vo.setAppointTime(appointment.getAppointTime());
            vo.setAppointDuration(appointment.getAppointDuration());
            vo.setAppointContent(appointment.getAppointContent());
            vo.setAppointRemark(appointment.getRemarks());
            vo.setAppointType(appointment.getAppointType());
            vo.setAppointStatus(appointment.getAppointStatus());
            vo.setConfirmStatus(appointment.getConfirmStatus());
          }
        }
      }
    }
  }

  /**
   * 设置候诊患者挂号信息
   *
   * @param vo 患者候诊信息
   * @param registereds 挂号信息列表
   * @param dentistAndAssistantInfo 医生助手信息列表
   * @param departmentRoomInfos 科室信息列表
   */
  private void setRegisteredInfo(
      TreatmentPatientInfoVO vo,
      List<Registered> registereds,
      List<SysUserInfoDetail> dentistAndAssistantInfo,
      List<DepartmentRoom> departmentRoomInfos) {
    Integer registeredId = vo.getRegisteredId();
    if (StringHelper.isNotEmpty(registereds)) {
      List<Registered> collect =
          registereds.stream()
              .filter(registered -> registered.getId().equals(registeredId))
              .collect(Collectors.toList());
      if (StringHelper.isNotEmpty(collect)) {
        Registered registered = collect.get(0);
        if (null != registered) {
          Integer regDentistId = registered.getDentistId();
          vo.setRegDentistId(regDentistId);
          List<SysUserInfoDetail> regDentistInfo =
              dentistAndAssistantInfo.stream()
                  .filter(sysUserInfoDetail -> sysUserInfoDetail.getUserId().equals(regDentistId))
                  .collect(Collectors.toList());
          if (StringHelper.isNotEmpty(regDentistInfo)) {
            SysUserInfoDetail userInfoDetail = regDentistInfo.get(0);
            vo.setRegDentistName(null != userInfoDetail ? userInfoDetail.getName() : "--");
          }
          Integer regAssistantId = registered.getAssistantId();
          if (null != regAssistantId) {
            vo.setRegAssistantId(regAssistantId);
            List<SysUserInfoDetail> regAssistantInfo =
                dentistAndAssistantInfo.stream()
                    .filter(
                        sysUserInfoDetail -> sysUserInfoDetail.getUserId().equals(regAssistantId))
                    .collect(Collectors.toList());
            if (StringHelper.isNotEmpty(regAssistantInfo)) {
              SysUserInfoDetail userInfoDetail = regAssistantInfo.get(0);
              vo.setRegAssistantName(null != userInfoDetail ? userInfoDetail.getName() : "--");
            }
          }

          Integer regDeptRoomId = registered.getDeptRoomId();
          if (null != regDeptRoomId) {
            vo.setRegDeptRoomId(regDeptRoomId);
            List<DepartmentRoom> departmentRooms =
                departmentRoomInfos.stream()
                    .filter(departmentRoom -> departmentRoom.getId().equals(regDeptRoomId))
                    .collect(Collectors.toList());
            if (StringHelper.isNotEmpty(departmentRooms)) {
              DepartmentRoom departmentRoom = departmentRooms.get(0);
              vo.setRegDeptRoomName(null != departmentRoom ? departmentRoom.getName() : "--");
            }
          }
          vo.setRegDate(new DateTime(registered.getCrtTime()).toString("yyyy-MM-dd"));
          vo.setRegTime(new DateTime(registered.getRegTime()).toString("HH:mm"));
          vo.setFirstVisit(registered.getFirstVisit());
        }
      }
    }
  }

  /**
   * 设置接诊信息
   *
   * @param vo 候诊患者信息
   * @param dentistAndAssistantInfo 医生和助手信息列表
   * @param patientBillStatistics 账单记录列表
   */
  private void setTreatingInfo(
      TreatmentPatientInfoVO vo,
      List<SysUserInfoDetail> dentistAndAssistantInfo,
      List<PatientBillStatistics> patientBillStatistics) {
    Integer treatDentistId = vo.getTreatDentistId();
    if (StringHelper.isNotEmpty(dentistAndAssistantInfo)) {
      List<SysUserInfoDetail> collect =
          dentistAndAssistantInfo.stream()
              .filter(sysUserInfoDetail -> treatDentistId.equals(sysUserInfoDetail.getUserId()))
              .collect(Collectors.toList());
      if (StringHelper.isNotEmpty(collect)) {
        SysUserInfoDetail userInfoDetail = collect.get(0);
        vo.setTreatDentistName(null != userInfoDetail ? userInfoDetail.getName() : "--");
      }
      Integer patientId = vo.getPatientId();
      if (StringHelper.isNotEmpty(patientBillStatistics)) {
        List<PatientBillStatistics> patientBillStatisticsInfo =
            patientBillStatistics.stream()
                .filter(item -> patientId.equals(item.getPatientId()))
                .collect(Collectors.toList());
        if (StringHelper.isNotEmpty(patientBillStatisticsInfo)) {
          PatientBillStatistics patientArrearInfo = patientBillStatisticsInfo.get(0);
          vo.setArrears(patientArrearInfo.getBillTotalArrears());
        }
      }
    }
  }

  /**
   * 设置开单信息（账单）
   *
   * @param vo 就诊患者信息
   */
  private void setOrderInfo(TreatmentPatientInfoVO vo, List<OrderRecord> orderRecords) {
    Integer id = vo.getId();
    if (StringHelper.isNotEmpty(orderRecords)) {
      List<OrderRecord> collect =
          orderRecords.stream()
              .filter(orderRecord -> orderRecord.getTreatmentRecordId().equals(id))
              .collect(Collectors.toList());
      if (StringHelper.isNotEmpty(collect)) {
        OrderRecord orderRecord = collect.get(0);
        vo.setOrderRecordId(orderRecord.getId());
        vo.setOriginalPrice(orderRecord.getTotalAmount());
        vo.setOrderStatus(orderRecord.getStatus());
      }
    }
  }

  /**
   * 设置收费信息(收费金额)
   *
   * @param vo 就诊患者信息
   */
  private void setChargeInfo(TreatmentPatientInfoVO vo, List<BillRecord> billRecords) {
    Integer id = vo.getId();
    if (StringHelper.isNotEmpty(billRecords)) {
      List<BillRecord> collect =
          billRecords.stream()
              .filter(billRecord -> billRecord.getTreatmentRecordId().equals(id))
              .collect(Collectors.toList());
      if (StringHelper.isNotEmpty(collect)) {
        BillRecord billRecord = collect.get(0);
        vo.setPrivilegeAmount(billRecord.getPrivilegeAmount());
        vo.setReceivedAmount(billRecord.getReceivedAmount());
        vo.setCheckOutTime(new DateTime(billRecord.getCrtTime()).toString("HH:mm"));
        vo.setBillNumber(billRecord.getBillNumber());
      } else {
        vo.setPrivilegeAmount(BigDecimal.valueOf(0));
        vo.setReceivedAmount(BigDecimal.valueOf(0));
        vo.setCheckOutTime("--");
      }
    }
  }

  /**
   * 设置候诊患者预约信息（优化前）
   *
   * @param vo 患者候诊信息
   */
  @Deprecated
  private void setAppointmentInfo(TreatmentPatientInfoVO vo) {
    Integer appointmentId = vo.getAppointmentId();
    if (null != appointmentId) {
      Appointment appointment = appointmentFeign.findAppointmentById(appointmentId);
      if (null != appointment) {
        Integer appointmentDentistId = appointment.getDentistId();
        vo.setAppointDentistId(appointmentDentistId);
        SysUserInfoDetail dentistInfo =
            systemServiceFeign.findSysUserEmployeeInfoByUserId(appointmentDentistId);
        vo.setAppointDentistName(null != dentistInfo ? dentistInfo.getName() : "--");
        Integer appointAssistantId = vo.getAppointAssistantId();
        if (null != appointAssistantId) {
          vo.setAppointAssistantId(appointAssistantId);
          // todo 从缓存中查询用户信息
          SysUserInfoDetail assistantInfo =
              systemServiceFeign.findSysUserEmployeeInfoByUserId(appointAssistantId);
          vo.setAppointAssistantName(null != assistantInfo ? assistantInfo.getName() : "--");
        }
        Integer appointDeptRoomId = vo.getAppointDeptRoomId();
        if (null != appointDeptRoomId) {
          vo.setAppointDeptRoomId(appointDeptRoomId);
          // todo 从缓存中查询科室信息
          DepartmentRoom departmentRoom =
              systemServiceFeign.findDepartmentRoomById(appointDeptRoomId);
          vo.setAppointDeptRoomName(null != departmentRoom ? departmentRoom.getName() : "--");
        }
        vo.setAppointTime(appointment.getAppointTime());
        vo.setAppointDuration(appointment.getAppointDuration());
        vo.setAppointContent(appointment.getAppointContent());
        vo.setAppointRemark(appointment.getRemarks());
        vo.setAppointType(appointment.getAppointType());
        vo.setAppointStatus(appointment.getAppointStatus());
        vo.setConfirmStatus(appointment.getConfirmStatus());
      }
    }
  }

  /**
   * 设置候诊患者挂号信息（优化前）
   *
   * @param vo 患者候诊信息
   */
  @Deprecated
  private void setRegisteredInfo(TreatmentPatientInfoVO vo) {
    Integer registeredId = vo.getRegisteredId();
    Registered registered = registeredMapper.selectByPrimaryKey(registeredId);
    if (null != registered) {
      Integer regDentistId = registered.getDentistId();
      vo.setRegDentistId(regDentistId);
      SysUserInfoDetail regDentistInfo =
          systemServiceFeign.findSysUserEmployeeInfoByUserId(regDentistId);
      vo.setRegDentistName(null != regDentistInfo ? regDentistInfo.getName() : "--");

      Integer regAssistantId = registered.getAssistantId();
      if (null != regAssistantId) {
        vo.setRegAssistantId(regAssistantId);
        // todo 从缓存中查询用户信息
        SysUserInfoDetail regAssistantInfo =
            systemServiceFeign.findSysUserEmployeeInfoByUserId(regAssistantId);
        vo.setRegAssistantName(null != regAssistantInfo ? regAssistantInfo.getName() : "--");
      }

      Integer regDeptRoomId = registered.getDeptRoomId();
      if (null != regDeptRoomId) {
        vo.setRegDeptRoomId(regDeptRoomId);
        // todo 从缓存中查询科室信息
        DepartmentRoom departmentRoom = systemServiceFeign.findDepartmentRoomById(regDeptRoomId);
        vo.setRegDeptRoomName(null != departmentRoom ? departmentRoom.getName() : "--");
      }
      vo.setRegDate(new DateTime(registered.getCrtTime()).toString("yyyy-MM-dd"));
      vo.setRegTime(new DateTime(registered.getRegTime()).toString("HH:mm"));
      vo.setFirstVisit(registered.getFirstVisit());
    }
  }

  /**
   * 设置接诊信息(优化前)
   *
   * @param vo 候诊患者信息
   */
  @Deprecated
  private void setTreatingInfo(TreatmentPatientInfoVO vo) {
    Integer treatDentistId = vo.getTreatDentistId();
    // todo 从缓存中查询用户信息
    SysUserInfoDetail treatDentistInfo =
        systemServiceFeign.findSysUserEmployeeInfoByUserId(treatDentistId);
    vo.setTreatDentistName(null != treatDentistInfo ? treatDentistInfo.getName() : "--");
    Integer patientId = vo.getPatientId();
    // 查询患者欠费总额
    PatientBillStatistics billStatistics = billRecordMapper.selectPatientBillStatistics(patientId);
    vo.setArrears(billStatistics.getBillTotalArrears());
  }

  /**
   * 设置开单信息（账单）--(优化前)
   *
   * @param vo 就诊患者信息
   */
  @Deprecated
  private void setOrderInfo(TreatmentPatientInfoVO vo) {
    Integer id = vo.getId();
    OrderRecord orderRecord = new OrderRecord();
    orderRecord.setTreatmentRecordId(id);
    orderRecord.setInservice(true);
    OrderRecord orderRecordResult = orderRecordMapper.selectOne(orderRecord);
    if (null != orderRecordResult) {
      vo.setOrderRecordId(orderRecordResult.getId());
      vo.setOriginalPrice(orderRecordResult.getTotalAmount());
      vo.setOrderStatus(orderRecordResult.getStatus());
    }
  }

  /**
   * 设置收费信息(收费金额)---(优化前)
   *
   * @param vo 就诊患者信息
   */
  @Deprecated
  private void setChargeInfo(TreatmentPatientInfoVO vo) {
    Integer id = vo.getId();
    BillRecord billRecord = new BillRecord();
    billRecord.setTreatmentRecordId(id);
    billRecord.setInservice(true);
    BillRecord record = billRecordMapper.selectOne(billRecord);
    if (null != record) {
      vo.setPrivilegeAmount(record.getPrivilegeAmount());
      vo.setReceivedAmount(record.getReceivedAmount());
      vo.setCheckOutTime(new DateTime(record.getCrtTime()).toString("HH:mm"));
      vo.setBillNumber(record.getBillNumber());
    } else {
      vo.setPrivilegeAmount(BigDecimal.valueOf(0));
      vo.setReceivedAmount(BigDecimal.valueOf(0));
      vo.setCheckOutTime("--");
    }
  }

  /**
   * 更新就诊记录电子病历书写状态
   *
   * @param id 就诊记录ID
   */
  public void modifyTreatmentRecord(Integer id) {
    TreatmentRecord record = mapper.selectByPrimaryKey(id);
    record.setMedicalRecordCompleted(true);
    mapper.updateByPrimaryKeySelective(record);
  }

  /**
   * 治疗完成
   *
   * @param treatmentRecordId 接诊记录ID
   */
  public void completeTreatment(Integer treatmentRecordId) {
    TreatmentRecord treatmentRecord = mapper.selectByPrimaryKey(treatmentRecordId);
    if (null == treatmentRecord) {
      throw new ClientServiceException("结束治疗失败，传入参数有误，未查询到相关就诊记录！", PARAMETERS_IS_ILLEGAL);
    }
    Byte status = treatmentRecord.getStatus();
    if (!status.equals(TREATMENT_PROCESSING_STATUS)
        && !status.equals(TREATMENT_PROCESS_ORDER_STATUS)) {
      throw new ClientServiceException("结束治疗失败，当前就诊已完成或已结账！", PARAMETERS_IS_ILLEGAL);
    }
    OrderRecord order = new OrderRecord();
    order.setTreatmentRecordId(treatmentRecordId);
    OrderRecord orderRecord = orderRecordMapper.selectOne(order);
    if (null == orderRecord) {
      throw new ClientServiceException("结束治疗失败，传入参数有误，未查询到相关开单记录！", PARAMETERS_IS_ILLEGAL);
    }
    OrderDetail detail = new OrderDetail();
    detail.setTreatmentRecordId(treatmentRecordId);
    int count = orderDetailMapper.selectCount(detail);
    if (count <= 0) {
      throw new ClientServiceException("结束治疗失败,当前就诊未进行开单，请至少开单一个项目！", DATA_NOT_EXIST);
    }
    orderRecord.setStatus((byte) 1);
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    orderRecord.setUpdId(userId);
    orderRecord.setUpdName(name);
    int i1 = orderRecordMapper.updateByPrimaryKeySelective(orderRecord);
    treatmentRecord.setTreatEndTime(new Date(System.currentTimeMillis()));
    treatmentRecord.setStatus((byte) 2);
    treatmentRecord.setUpdId(userId);
    treatmentRecord.setUpdName(name);
    int i = mapper.updateByPrimaryKeySelective(treatmentRecord);
    if (i > 0) {
      // 发送消息更新账单
      rabbitMqServiceFeign.sendMessage(orderRecord.getId(), 1, BaseBill);
      Integer appointmentId = treatmentRecord.getAppointmentId();
      if (null != appointmentId) {
        rabbitMqServiceFeign.sendMessage(appointmentId, 0, 1, BaseTreatmentProcess);
      } else {
        Integer registeredId = treatmentRecord.getRegisteredId();
        rabbitMqServiceFeign.sendMessage(registeredId, 1, 1, BaseTreatmentProcess);
      }
    }
    // 新增开单处置的随访
    detail.setType((byte) 0);
    detail.setInservice(true);
    List<OrderDetail> orderDetails = orderDetailMapper.select(detail);
    List<VisitingRecord> visitingRecordList = new ArrayList<>();
    if (StringHelper.isNotEmpty(orderDetails)) {
      orderDetails.forEach(
          orderDetail -> {
            List<VisitingRecord> orderDetailVisitRecord =
                createOrderDetailVisitRecord(treatmentRecordId, orderDetail);
            orderDetailVisitRecord.stream()
                .sequential()
                .collect(Collectors.toCollection(() -> visitingRecordList));
          });
      this.saveOrderDetailVisitRecord(visitingRecordList);
    }
  }

  /**
   * 保存开单处置随访计划
   *
   * @param visitingRecordList 随访计划列表
   */
  public void saveOrderDetailVisitRecord(List<VisitingRecord> visitingRecordList) {
    Map<String, VisitingRecord> groupVisitRecordMap = new HashMap<>();
    // 按照随访日期和就诊ID对随访计划分组
    visitingRecordList.forEach(
        visitingRecord -> {
          Integer treatmentId = visitingRecord.getTreatmentId();
          String visitingDate =
              new SimpleDateFormat("yyyyMMdd").format(visitingRecord.getVisitingDate());
          String key = treatmentId + "_" + visitingDate;
          if (groupVisitRecordMap.containsKey(key)) {
            VisitingRecord visitingRecordCache = groupVisitRecordMap.get(key);
            String currentReason = visitingRecord.getReason();
            String newReason = visitingRecordCache.getReason() + "," + currentReason;
            visitingRecordCache.setReason(newReason);
            groupVisitRecordMap.put(key, visitingRecordCache);
          } else {
            groupVisitRecordMap.put(key, visitingRecord);
          }
        });
    // 设置分组计划
    List<VisitingRecord> collect = new ArrayList<>(groupVisitRecordMap.values());
    log.info("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓系统新建随访↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    log.info("==> groupVisitRecordMap.values():{}", groupVisitRecordMap.values());
    log.info("==> collect:{}", collect);
    log.info("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
    treatmentOtherFeign.insertVisitingRecord(collect);
  }

  /**
   * 创建开单处置随访计划
   *
   * @param treatmentRecordId 就诊记录ID
   * @param detail 开单详情
   */
  public List<VisitingRecord> createOrderDetailVisitRecord(
      Integer treatmentRecordId, OrderDetail detail) {
    List<VisitingRecord> visitRecordPlanList = new ArrayList<>();
    BaseTariff baseTariff = baseTariffBiz.selectById(detail.getBillingItemId());
    if (null != baseTariff) {
      String fellowUp = baseTariff.getFellowUp();
      if (StringHelper.isNotBlank(fellowUp)) {
        String[] nums = fellowUp.replaceAll("-", "").split(",");
        if (nums.length > 0) {
          Arrays.stream(nums)
              .filter(StringHelper::isNotBlank)
              .forEach(
                  num -> {
                    int nn;
                    try {
                      nn = Integer.parseInt(num);
                    } catch (Exception ex) {
                      throw new ClientServiceException("价目表的随访字段有非数字！", DATA_ERROR);
                    }
                    VisitingRecord visitRecord = new VisitingRecord();
                    TreatmentRecord treatmentRecord = mapper.selectByPrimaryKey(treatmentRecordId);
                    if (null != treatmentRecord) {
                      visitRecord.setPatientId(treatmentRecord.getPatientId());
                      visitRecord.setOrgId(treatmentRecord.getOrgId());
                      visitRecord.setTreatmentDate(treatmentRecord.getTreatStartTime());
                      Registered registered =
                          registeredMapper.selectByPrimaryKey(treatmentRecord.getRegisteredId());
                      if (null != registered) {
                        visitRecord.setDentistId(registered.getDentistId());
                        visitRecord.setDeptRoomId(registered.getDeptRoomId());
                      }
                    }
                    visitRecord.setCrtId(detail.getCrtId());
                    visitRecord.setCrtName(detail.getCrtName());
                    visitRecord.setCrtTime(new Date(System.currentTimeMillis()));
                    visitRecord.setTreatmentId(treatmentRecordId);
                    visitRecord.setVisitingTime("09:00");
                    visitRecord.setReason(baseTariff.getName());
                    visitRecord.setStatus(false);
                    visitRecord.setInservice(true);
                    visitRecord.setVisitingDate(
                        DateUtils.addDays(new Date(System.currentTimeMillis()), nn));
                    visitRecordPlanList.add(visitRecord);
                  });
        }
      }
    }
    return visitRecordPlanList;
  }

  /**
   * 根据条件查询患者就诊记录列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<PatientTreatmentRecordVO> findPatientTreatList(
      PatientTreatmentRecordQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<PatientTreatmentRecordVO> resultList = mapper.selectPatientTreatmentRecordList(queryForm);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            Integer orgId = vo.getOrgId();
            // todo 从缓存中查询组织信息
            OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
            if (null != orgInfo) {
              vo.setOrgName(orgInfo.getAbbreviation());
            }
            Integer dentistId = vo.getDentistId();
            // todo 从缓存中查询用户
            SysEmployee employee = systemServiceFeign.findSysEmployeeById(dentistId);
            if (null != employee) {
              vo.setDentistName(employee.getName());
            }
            Integer treatmentRecordId = vo.getTreatmentRecordId();
            AssistantMatchingRecord assistantMatchingRecord = new AssistantMatchingRecord();
            assistantMatchingRecord.setTreatmentRecordId(treatmentRecordId);
            List<AssistantMatchingRecord> assistantMatchingRecords =
                assistantMatchingRecordMapper.select(assistantMatchingRecord);
            if (StringHelper.isNotEmpty(assistantMatchingRecords)) {
              setTreatmentRecordAssistantInfo(vo, assistantMatchingRecords);
            }
          });
    } else {
      resultList = new ArrayList<>();
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 设置就诊记录助手信息
   *
   * @param vo 就诊记录
   * @param assistantMatchingRecords 就诊助手列表
   */
  private void setTreatmentRecordAssistantInfo(
      PatientTreatmentRecordVO vo, List<AssistantMatchingRecord> assistantMatchingRecords) {
    for (AssistantMatchingRecord matchingRecord : assistantMatchingRecords) {
      Integer assistantId = matchingRecord.getAssistantId();
      // todo 从缓存中查询用户
      SysEmployee assistant = systemServiceFeign.findSysEmployeeById(assistantId);
      if (null != assistant) {
        String assistantName = assistant.getName();
        Byte type = matchingRecord.getType();
        switch (type) {
          case 0:
            vo.setAssistantId1(assistantId);
            vo.setAssistantName1(assistantName);
            break;
          case 1:
            vo.setAssistantId2(assistantId);
            vo.setAssistantName2(assistantName);
            break;
          default:
            vo.setAssistantId3(assistantId);
            vo.setAssistantName3(assistantName);
            break;
        }
      }
    }
  }

  /**
   * 根据条件查询APP端就诊列表
   *
   * @param query 查询条件
   * @return list
   */
  public PageInfo<PatientTreatmentInfo4ListVO> findAppTreatList(AppTreatListQuery query) {


    Integer orgId = query.getOrgId();
    String queryDate = query.getQueryDate();
    Integer dentistId = query.getDentistId();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    List<PatientTreatmentInfo4ListVO> patientTreatmentInfo4ListVOList = new ArrayList<>();

    TreatmentList4AppQuery treatmentList4AppQuery = new TreatmentList4AppQuery();
    treatmentList4AppQuery.setQueryDate(queryDate);
    treatmentList4AppQuery.setWhetherPage(true);
    treatmentList4AppQuery.setPageSize(query.getPageSize());
    treatmentList4AppQuery.setPageNum(query.getPageNum());
    treatmentList4AppQuery.setOrgId(orgId);
    treatmentList4AppQuery.setDentistId(dentistId);
    PageInfo<BaseTreatmentProcessVO> pageInfoList = remoteMiddleTableServiceFeign.treatmentList4App(treatmentList4AppQuery);
    // 预约未到
    List<BaseTreatmentProcessVO> treatmentList = pageInfoList.getList();
    if (StringHelper.isNotEmpty(treatmentList)) {
      // 获取预约未到的预约ID
      List<Integer> appointIds = new ArrayList<>();
      treatmentList.stream().filter(
              baseTreatmentProcessVO -> {
                return null != baseTreatmentProcessVO.getAppointmentId() && baseTreatmentProcessVO.getAppointStatus() !=2 && baseTreatmentProcessVO.getRegisteredId() == null;
              }).forEach(baseTreatmentProcessVO -> {
        appointIds.add(baseTreatmentProcessVO.getAppointmentId());
      });
      // 查询预约助手
      List<Appointment> appointmentListByIds = appointmentFeign.findAppointmentListByIds(appointIds);
      // 获取预约助手ID
      List<Integer> assistantIds = new ArrayList<>();
      // 查询预约助手信息
      List<SysUserInfoDetail> assistantInfos=null;
      if (StringHelper.isNotEmpty(appointmentListByIds)) {
        appointmentListByIds.forEach(appointment -> {
          assistantIds.add(appointment.getAssistantId());
        });
        // 查询预约助手信息
        if (StringHelper.isNotEmpty(assistantIds)) {
          assistantInfos = this.systemServiceFeign.findSysUserEmployeeInfoByUserIds(assistantIds);
        }
      }

      for (BaseTreatmentProcessVO appointmentUnDonePatientInfoVO : treatmentList) {
        PatientTreatmentInfo4ListVO entity = new PatientTreatmentInfo4ListVO();
        entity.setAppointId(appointmentUnDonePatientInfoVO.getAppointmentId());
        entity.setTreatStatus(appointmentUnDonePatientInfoVO.getAppointStatus());
        entity.setNodeTime(dateFormat.format(appointmentUnDonePatientInfoVO.getAppointStartTime()));
        entity.setPatientId(appointmentUnDonePatientInfoVO.getPatientId());
        entity.setPatientName(appointmentUnDonePatientInfoVO.getPatientName());
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
        entity.setAge(appointmentUnDonePatientInfoVO.getAge());
        entity.setGender(appointmentUnDonePatientInfoVO.getGender());
        entity.setOrgId(appointmentUnDonePatientInfoVO.getOrgId());
        patientTreatmentInfo4ListVOList.add(entity);
      }
    }
    // 候诊中
    List<Integer> registerIds = new ArrayList<>();
    if (StringHelper.isNotEmpty(treatmentList)) {
      treatmentList.stream().filter(baseTreatmentProcessVO -> {
        return baseTreatmentProcessVO.getRegisteredId() != null && baseTreatmentProcessVO.getTreatStatus() == 0;
      }).forEach(baseTreatmentProcessVO -> {
        registerIds.add(baseTreatmentProcessVO.getRegisteredId());
      });
      if (StringHelper.isNotEmpty(registerIds)) {
        List<RegisteredVO> registeredVOS = registeredBiz.registeredInfoDetails(registerIds);
        if (StringHelper.isNotEmpty(registeredVOS)) {
          registeredVOS.forEach(registeredVO -> {
            PatientTreatmentInfo4ListVO entity = new PatientTreatmentInfo4ListVO();
            entity.setAppointId(registeredVO.getAppointmentId());
            entity.setTreatStatus((byte) 2);
            entity.setNodeTime(dateFormat.format(registeredVO.getRegTime()));
            entity.setPatientId(registeredVO.getPatientId());
            entity.setPatientName(registeredVO.getPatientName());
            entity.setDentistId(registeredVO.getDentistId());
            entity.setDentistName(registeredVO.getDentistName());
            entity.setAssistantId(registeredVO.getAssistantId());
            entity.setAssistantName(StringHelper.isBlank(registeredVO.getAssistantName()) ?
                    "--" : registeredVO.getAssistantName());
            entity.setAge(registeredVO.getAge());
            entity.setGender(registeredVO.getGender());
            entity.setOrgId(registeredVO.getOrgId());
            entity.setRegistedId(registeredVO.getId());
            patientTreatmentInfo4ListVOList.add(entity);
          });
        }
      }
    }
    // 就诊中/就诊完成/已结账
    List<Integer> treatmentIds = new ArrayList<>();
    treatmentList.forEach(baseTreatmentProcessVO -> {
      Integer treatmentId = baseTreatmentProcessVO.getTreatmentId();
      if (null != treatmentId) {
        treatmentIds.add(treatmentId);
      }
    });


    if (StringHelper.isNotEmpty(treatmentIds)) {
      List<TreatmentRecordExtendVO> treatmentRecordExtendVOS = mapper.selectByIds(treatmentIds.stream().collect(Collectors.toSet()));
      if (StringHelper.isNotEmpty(treatmentRecordExtendVOS)) {
        List<Integer> regAssistantIds = new ArrayList<>();
        treatmentRecordExtendVOS.forEach(item -> {regAssistantIds.add(item.getRegAssistantId());});
        List<SysUserInfoDetail> regAssistantInfos = null;
        if (StringHelper.isNotEmpty(regAssistantIds)) {
          regAssistantInfos = systemServiceFeign.findSysUserEmployeeInfoByUserIds(regAssistantIds);
        }



        for (BaseTreatmentProcessVO patientTreatmentRecordVO : treatmentList) {
          PatientTreatmentInfo4ListVO entity = new PatientTreatmentInfo4ListVO();
          // 设置就诊状态
          Byte treatmentStatus = patientTreatmentRecordVO.getTreatStatus();
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
          entity.setTreatmentId(patientTreatmentRecordVO.getTreatmentId());
          entity.setAppointId(patientTreatmentRecordVO.getAppointmentId());
          entity.setNodeTime(dateFormat.format(patientTreatmentRecordVO.getTreatStartTime()));
          entity.setPatientId(patientTreatmentRecordVO.getPatientId());
          entity.setPatientName(patientTreatmentRecordVO.getPatientName());
          entity.setDentistId(patientTreatmentRecordVO.getRegisteredDentistId());
          entity.setDentistName(patientTreatmentRecordVO.getRegDentistName());
          if (StringHelper.isNotEmpty(treatmentRecordExtendVOS)) {
            Integer treatmentId = patientTreatmentRecordVO.getTreatmentId();
            List<TreatmentRecordExtendVO> treatmentRecordExtendVOList = treatmentRecordExtendVOS.stream().filter(item -> item.getId().equals(treatmentId)).collect(Collectors.toList());
            if (StringHelper.isNotEmpty(treatmentRecordExtendVOList)) {
              TreatmentRecordExtendVO treatmentRecordExtendVO = treatmentRecordExtendVOList.get(0);
              Integer regAssistantId = treatmentRecordExtendVO.getRegAssistantId();
              entity.setAssistantId(regAssistantId);
              if (StringHelper.isNotEmpty(regAssistantInfos)) {

                List<SysUserInfoDetail> regAssistantInfoList = regAssistantInfos.stream().filter(item -> item.getUserId().equals(regAssistantId)).collect(Collectors.toList());
                if (StringHelper.isNotEmpty(regAssistantInfoList)) {
                  SysUserInfoDetail userInfoDetail = regAssistantInfoList.get(0);
                  entity.setAssistantName(StringHelper.isBlank(userInfoDetail.getName()) ?
                          "--" : userInfoDetail.getName());
                }
              }
            }
          }

          entity.setAge(patientTreatmentRecordVO.getAge());
          entity.setGender(patientTreatmentRecordVO.getGender());
          entity.setOrgId(patientTreatmentRecordVO.getOrgId());
          entity.setRegistedId(patientTreatmentRecordVO.getRegisteredId());
          patientTreatmentInfo4ListVOList.add(entity);
        }
      }
    }
    PageInfo resultPage = new PageInfo();
    BeanUtils.copyProperties(pageInfoList,resultPage);
    resultPage.setList(patientTreatmentInfo4ListVOList);
    return resultPage;
  }

  /**
   * 末次就诊信息
   *
   * @param patientId 患者ID
   * @return 返回末次就诊实体对象
   */
  public LastTreatmentInfoVO lastTreatmentInfo(Integer patientId) {
    LastTreatmentInfoVO lastTreatmentInfoVO = mapper.lastTreatmentInfo(patientId);
    return lastTreatmentInfoVO;
  }

  /**
   * 查询指定时间段内每个医生每天患者就诊人数
   *
   * @param form 查询条件表单
   * @return 返回实体列表
   */
  public List<TreatmentInfoForMonthVO> treatInfoForMonth(TreatmentInfoForMonthForm form) {
    Integer dentistId = form.getDentistId();
    Date startDate = form.getStartDate();
    Date endDate = form.getEndDate();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    List<TreatmentInfoForMonthVO> treatmentInfoForMonthVOS = mapper.treatInfoForMonth(dentistId, startDate, endDate,orgId);
    AppointmentForMonthForm queryForm = new AppointmentForMonthForm();
    queryForm.setDentistId(dentistId);
    queryForm.setEndDate(endDate);
    queryForm.setStartDate(startDate);
    List<TreatmentInfoForMonthVO> appointmentForMonthVos = this.appointmentFeign.appointmentForMonth(queryForm);
    appointmentForMonthVos.stream().sequential().collect(Collectors.toCollection(() ->treatmentInfoForMonthVOS));
    return treatmentInfoForMonthVOS;
  }

  /**
   * 根据条件统计就诊列表数量
   *
   * @param query 查询参数
   * @return
   */
  public Map<String, Integer> countTreatList(TreatmentCountQuery query) {
    Map<String, Integer> resultMap = new HashMap<>(16);

    Integer orgId = query.getOrgId();
    Integer userId = query.getUserId();
    String queryDate = query.getQueryDate();
    // 查询就诊中列表
    RegisteredQueryForm regQuery = new RegisteredQueryForm();
    regQuery.setWhetherPage(false);
    regQuery.setOrgId(orgId);
    regQuery.setCurrentDate(queryDate);
    regQuery.setDentistId(userId);
    regQuery.setInservice(true);
    List<WaitingPatientInfoVO> waitingForTreat =
        registeredMapper.selectRegisteredList((byte) 0, regQuery);

    TreatmentRecordQueryForm queryForm = new TreatmentRecordQueryForm();
    queryForm.setWhetherPage(false);
    queryForm.setOrgId(orgId);
    queryForm.setDentistId(userId);
    queryForm.setCurrentDate(queryDate);
    queryForm.setInservice(true);
    // 就诊中
    queryForm.setTreatmentStatus(new Byte[] {0});
    List<TreatmentPatientInfoVO> treatReceiving = mapper.selectTreatingList(queryForm);
    // 接诊完成
    queryForm.setTreatmentStatus(new Byte[] {2});
    List<TreatmentPatientInfoVO> treatCompleted = mapper.selectTreatingList(queryForm);
    // 已结账
    queryForm.setTreatmentStatus(new Byte[] {3});
    queryForm.setDentistId(query.getDentistId());
    List<TreatmentPatientInfoVO> treatmentPatientInfos = mapper.selectTreatingList(queryForm);
    // 预约未到数量
    AppointmentCurrentListQuery form = new AppointmentCurrentListQuery();
    form.setWhetherPage(false);
    form.setOrgId(orgId);
    form.setDentistId(userId);
    form.setCurrentDate(queryDate);
    Integer appointNotArrived = appointmentFeign.countAppointNotArrived(form);
    resultMap.put("appointNotArrived", appointNotArrived);
    resultMap.put("waitingForTreat", waitingForTreat.size());
    resultMap.put("treatReceiving", treatReceiving.size());
    resultMap.put("treatCompleted", treatCompleted.size());
    resultMap.put("checkedOut", treatmentPatientInfos.size());
    return resultMap;
  }

  /**
   * PC照片影像小程序就诊中/治疗完成/已结账请求接口
   *
   * @param queryForm
   * @return
   */
  public PageInfo<DesktopMiniProgramVO> desktopTreatList(TreatmentRecordQueryForm queryForm) {
    String currentDate = queryForm.getCurrentDate();
    Integer orgId = queryForm.getOrgId();
    // 0-就诊中;1-已开单;2-治疗完成;3-已结账
    Byte[] treatmentStatusArr = queryForm.getTreatmentStatus();
    if (treatmentStatusArr.length != 1) {
      ResponseUtil.fail(PARAMETERS_IS_ILLEGAL, "PC照片影像小程序只允许查询单状态查询", null);
    }
    Byte aByte = treatmentStatusArr[0];
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<DesktopMiniProgramVO> desktopMiniProgramVOS = null;
    if (aByte.intValue() == 0) {
      desktopMiniProgramVOS = mapper.desktopTreatingList(aByte, currentDate, orgId);
    } else if (aByte.intValue() == 1 || aByte.intValue() == 2) {
      desktopMiniProgramVOS = mapper.desktopTreatingList((byte) 2, currentDate, orgId);
    } else if (aByte.intValue() == 3) {
      desktopMiniProgramVOS = billRecordMapper.desktopBillingList(currentDate, orgId);
    }
    // 将信息注入PC照片印象小程序列表
    this.setDesktopMiniProgramVOInfo(desktopMiniProgramVOS, queryForm.getCurrentDate());

    return new PageInfo<>(desktopMiniProgramVOS);
  }

  /**
   * 根据患者姓名、手机号、病历号查询影像小程序列表中患者信息
   *
   * @param queryForm 查询参数封装
   * @return 返回实体列表
   */
  public PageInfo<DesktopMiniProgramVO> findDesktopTreatListItem(
      TreatmentRecordQueryForm queryForm) {
    // 0-就诊中;1-已开单;2-治疗完成;3-已结账
    Byte[] treatmentStatusArr = queryForm.getTreatmentStatus();
    if (treatmentStatusArr.length > 1 || treatmentStatusArr.length < 1) {
      ResponseUtil.fail(PARAMETERS_IS_ILLEGAL, "PC照片影像小程序只允许查询单状态查询", null);
    }
    Byte aByte = treatmentStatusArr[0];
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }

    PatientLikeFinleQueryForm query = new PatientLikeFinleQueryForm();
    query.setCondition(queryForm.getSearch());
    List<PatientBaseInfoVo> patientByNameAndMobile =
        patientServiceFeign.findPatientByNameAndMobile(query);
    if (StringHelper.isNotEmpty(patientByNameAndMobile)) {
      List<Integer> patientIds = new ArrayList<>();
      patientByNameAndMobile.forEach(
          patientBaseInfoVo -> {
            patientIds.add(patientBaseInfoVo.getId());
          });
      List<DesktopMiniProgramVO> desktopMiniProgramVOS =
          mapper.desktopTreatingListItem(
              patientIds, queryForm.getCurrentDate(), queryForm.getOrgId());
      this.setDesktopMiniProgramVOInfo(desktopMiniProgramVOS, queryForm.getCurrentDate());
      return new PageInfo<>(desktopMiniProgramVOS);
    }
    return new PageInfo<>(new ArrayList<>());
  }

  /**
   * 将信息注入PC照片印象小程序列表
   *
   * @param desktopMiniProgramVOS 列表信息
   * @param currentDate 当前时间
   */
  private void setDesktopMiniProgramVOInfo(
      List<DesktopMiniProgramVO> desktopMiniProgramVOS, String currentDate) {
    if (StringHelper.isNotEmpty(desktopMiniProgramVOS)) {
      List<Integer> patientIds = new ArrayList<>();
      List<Integer> dentistIds = new ArrayList<>();
      List<Integer> treatmentIds = new ArrayList<>();
      if (StringHelper.isNotEmpty(desktopMiniProgramVOS)) {
        desktopMiniProgramVOS.forEach(
            desktopMiniProgramVO -> {
              patientIds.add(desktopMiniProgramVO.getPatientId());
              dentistIds.add(desktopMiniProgramVO.getDentistId());
              treatmentIds.add(desktopMiniProgramVO.getId());
            });
        List<PatientBaseInfoVo> patientInfoByIds =
            patientServiceFeign.findPatientInfoByIds(patientIds);
        List<SysUserInfoDetail> sysUserEmployeeInfoByUserIds =
            systemServiceFeign.findSysUserEmployeeInfoByUserIds(dentistIds);
        List<XRayFilm> xRayFilmListByPatientIds =
            remoteTreatmentOther.findXRayFilmListByPatientIds(patientIds, currentDate);
        desktopMiniProgramVOS.forEach(
            desktopMiniProgramVO -> {
              // 设置医生信息
              Integer dentistId = desktopMiniProgramVO.getDentistId();
              List<SysUserInfoDetail> collect =
                  sysUserEmployeeInfoByUserIds.stream()
                      .filter(sysUserInfoDetail -> sysUserInfoDetail.getUserId().equals(dentistId))
                      .collect(Collectors.toList());
              if (StringHelper.isNotEmpty(collect)) {
                SysUserInfoDetail userInfoDetail = collect.get(0);
                desktopMiniProgramVO.setDentistName(userInfoDetail.getName());
              }
              // 设置患者信息
              Integer patientId = desktopMiniProgramVO.getPatientId();
              List<PatientBaseInfoVo> patientBaseInfoVos =
                  patientInfoByIds.stream()
                      .filter(patientBaseInfoVo -> patientBaseInfoVo.getId().equals(patientId))
                      .collect(Collectors.toList());
              if (StringHelper.isNotEmpty(patientBaseInfoVos)) {
                PatientBaseInfoVo patientBaseInfoVo = patientBaseInfoVos.get(0);
                desktopMiniProgramVO.setPatientName(patientBaseInfoVo.getName());
                desktopMiniProgramVO.setMedicalNumber(patientBaseInfoVo.getMedicalNumber());
                desktopMiniProgramVO.setBirthday(patientBaseInfoVo.getBirthday());
              }
              // 设置是否上传图片
              List<XRayFilm> xRayFilms =
                  xRayFilmListByPatientIds.stream()
                      .filter(xRayFilm -> xRayFilm.getPatientId().equals(patientId))
                      .collect(Collectors.toList());
              if (StringHelper.isNotEmpty(xRayFilms)) {
                desktopMiniProgramVO.setHasImg(true);
              } else {
                desktopMiniProgramVO.setHasImg(false);
              }
            });
      }
    }
  }
}
