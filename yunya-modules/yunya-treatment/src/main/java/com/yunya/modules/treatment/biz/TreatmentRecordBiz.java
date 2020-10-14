package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.appointment.domain.query.AppAppointmentInfoQuery;
import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.query.AppTreatListQuery;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.query.TreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.AppPatientTreatmentInfoVO;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.feign.treatment.domain.vo.TreatmentPatientInfoVO;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordVO;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.DepartmentRoom;
import com.yunya.models.system.MemberType;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.treatment.*;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.modules.treatment.mapper.AssistantMatchingRecordMapper;
import com.yunya.modules.treatment.mapper.OrderDetailMapper;
import com.yunya.modules.treatment.mapper.OrderRecordMapper;
import com.yunya.modules.treatment.mapper.TreatmentRecordMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.yunya.framework.common.constant.BusinessConstants.TREATMENT_PROCESSING_STATUS;
import static com.yunya.framework.common.constant.BusinessConstants.TREATMENT_PROCESS_ORDER_STATUS;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 简介: 就诊记录管理业务层
 *
 * @author: chow
 * @date: 2020/8/12 14:55
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TreatmentRecordBiz extends BaseBiz<TreatmentRecordMapper, TreatmentRecord> {

  /** 患者服务调用 */
  @Autowired private PatientCentralServiceFeign patientServiceFeign;

  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;

  /** 预约服务调用 */
  @Autowired private RemoteAppointmentFeign appointmentFeign;

  /** 就诊其他信息服务调用 */
  @Autowired private RemoteTreatmentOtherFeign treatmentOtherFeign;

  /** 基础价目表 */
  @Autowired private BaseTariffBiz baseTariffBiz;

  /** 挂号 */
  @Autowired private RegisteredBiz registeredBiz;

  /** 开单 */
  @Autowired private OrderRecordMapper orderRecordMapper;

  /** 开单明细 */
  @Autowired private OrderDetailMapper orderDetailMapper;

  /** 就诊关联助手 */
  @Autowired private AssistantMatchingRecordMapper assistantMatchingRecordMapper;

  /**
   * 开始接诊
   *
   * @param regId 挂号ID
   */
  public void startTreatment(Integer regId) {
    TreatmentRecord record = new TreatmentRecord();
    record.setRegisteredId(regId);
    int count = mapper.selectCount(record);
    if (count > 0) {
      throw new ClientServiceException("接诊失败，该挂号已被接诊，无法再次接诊！", DATA_EXIST);
    }

    Registered regResult = registeredBiz.selectById(regId);
    if (null == regResult || !regResult.getInservice()) {
      throw new ClientServiceException("接诊失败，您当前未选择接诊患者或传入参数有误！", QUERY_RESULT_INVALID);
    }

    TreatmentRecord entity = new TreatmentRecord();
    Integer orgId = regResult.getOrgId();
    entity.setOrgId(orgId);
    entity.setAppointmentId(regResult.getAppointmentId());
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
    entity.setTreatStartTime(new Date(System.currentTimeMillis()));
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    mapper.insertSelective(entity);

    regResult.setStatus((byte) 1);
    registeredBiz.updateSelectiveById(regResult);
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
  public List<TreatmentRecord> selectByIds(Set<Integer> ids) {
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

    List<TreatmentPatientInfoVO> treatingList = mapper.selectTreatingList(queryForm);
    Byte status = queryForm.getTreatmentStatus();
    switch (status) {
      case 0:
      case 1:
      case 2:
        if (StringHelper.isNotEmpty(treatingList)) {
          treatingList.forEach(
              vo -> {
                // 设置患者信息
                setPatientInfo(vo);
                // 设置预约信息
                setAppointmentInfo(vo);
                // 设置挂号信息
                setRegisteredInfo(vo);
                // 设置接诊信息
                setTreatingInfo(vo);
                // 设置账单信息
                setOrderInfo(vo);
              });
        } else {
          treatingList = new ArrayList<>();
        }
        break;
      case 3:
        if (StringHelper.isNotEmpty(treatingList)) {
          treatingList.forEach(
              vo -> {
                // 设置患者信息
                setPatientInfo(vo);
                // 设置预约信息
                setAppointmentInfo(vo);
                // 设置挂号信息
                setRegisteredInfo(vo);
                // 设置接诊信息
                setTreatingInfo(vo);
                // 设置账单信息
                setOrderInfo(vo);
                // 设置收费信息
                setChargeInfo(vo);
              });
        } else {
          treatingList = new ArrayList<>();
        }
        break;
      default:
        break;
    }

    return new PageInfo<>(treatingList);
  }

  /**
   * 设置候诊患者患者信息
   *
   * @param vo 患者候诊
   */
  private void setPatientInfo(TreatmentPatientInfoVO vo) {
    Integer patientId = vo.getPatientId();
    PatientTotalInfoVo patientData = patientServiceFeign.findPatientTotalInfo(patientId);
    if (null != patientData) {
      vo.setPatientName(patientData.getName());
      vo.setMobile(patientData.getMobile());
      vo.setGender(patientData.getGender());
      vo.setAge(patientData.getAge());
      vo.setBirthday(patientData.getBirthday());
      vo.setPatientRemark(patientData.getRemarks());
      String medicalNumber = patientData.getMedicalNumber();
      vo.setMedicalNumber(StringHelper.isNotBlank(medicalNumber) ? medicalNumber : "--");
      vo.setAllergen(patientData.getAllergens());
      Integer memberTypeId = patientData.getMemberTypeId();
      if (null != memberTypeId) {
        MemberType memberType = systemServiceFeign.findMemberTypeById(memberTypeId);
        if (null != memberType) {
          vo.setMemberIcon(String.valueOf(memberType.getIcon()));
        }
      }
    }
  }

  /**
   * 设置候诊患者预约信息
   *
   * @param vo 患者候诊信息
   */
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
          DepartmentRoom departmentRoom = systemServiceFeign.findDepartmentRoomById(appointmentId);
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
   * 设置候诊患者挂号信息
   *
   * @param vo 患者候诊信息
   */
  private void setRegisteredInfo(TreatmentPatientInfoVO vo) {
    Integer registeredId = vo.getRegisteredId();
    Registered registered = registeredBiz.selectById(registeredId);
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
   * 设置接诊信息
   *
   * @param vo 候诊患者信息
   */
  private void setTreatingInfo(TreatmentPatientInfoVO vo) {
    Integer treatDentistId = vo.getTreatDentistId();
    // todo 从缓存中查询用户信息
    SysUserInfoDetail treatDentistInfo =
        systemServiceFeign.findSysUserEmployeeInfoByUserId(treatDentistId);
    vo.setTreatDentistName(null != treatDentistInfo ? treatDentistInfo.getName() : "--");
  }

  /**
   * 设置开单信息（账单）
   *
   * @param vo 就诊患者信息
   */
  private void setOrderInfo(TreatmentPatientInfoVO vo) {
    Integer id = vo.getId();
    OrderRecord orderRecord = new OrderRecord();
    orderRecord.setTreatmentRecordId(id);
    OrderRecord orderRecordResult = orderRecordMapper.selectOne(orderRecord);
    if (null != orderRecordResult) {
      vo.setOrderRecordId(orderRecordResult.getId());
      vo.setOriginalPrice(orderRecordResult.getTotalAmount());
      vo.setOrderStatus(orderRecordResult.getStatus());
    }
  }

  /**
   * todo 设置收费信息
   *
   * @param vo 就诊患者信息
   */
  private void setChargeInfo(TreatmentPatientInfoVO vo) {}

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
    orderRecordMapper.updateByPrimaryKeySelective(orderRecord);
    treatmentRecord.setTreatEndTime(new Date(System.currentTimeMillis()));
    treatmentRecord.setStatus((byte) 2);
    mapper.updateByPrimaryKeySelective(treatmentRecord);
    // 新增开单处置的随访
    detail.setType((byte) 0);
    List<OrderDetail> orderDetails = orderDetailMapper.select(detail);
    if (StringHelper.isNotEmpty(orderDetails)) {
      treatmentOtherFeign.deleteVisitingRecordByTreatmentIdRest(treatmentRecordId);
      orderDetails.forEach(
          orderDetail -> saveOrderDetailVisitRecord(treatmentRecordId, orderDetail));
    }
  }

  /**
   * 保存开单处置随访计划
   *
   * @param treatmentRecordId 就诊记录ID
   * @param detail 开单详情
   */
  public void saveOrderDetailVisitRecord(Integer treatmentRecordId, OrderDetail detail) {
    BaseTariff baseTariff = baseTariffBiz.selectById(detail.getBillingItemId());
    if (null != baseTariff) {
      String fellowUp = baseTariff.getFellowUp();
      if (StringHelper.isNotBlank(fellowUp)) {
        String[] nums = fellowUp.replaceAll("-","").split(",");
        if (nums.length > 0) {
          Arrays.stream(nums)
              .filter(StringHelper::isNotBlank)
              .forEach(
                  num -> {
                    int nn;
                    try{
                      nn = Integer.parseInt(num);
                    }
                    catch (Exception ex){
                      throw new ClientServiceException("价目表的随访字段有非数字！", DATA_ERROR);
                    }
                    VisitingRecord visitRecord = new VisitingRecord();
                    TreatmentRecord treatmentRecord = mapper.selectByPrimaryKey(treatmentRecordId);
                    if (null != treatmentRecord) {
                      visitRecord.setPatientId(treatmentRecord.getPatientId());
                      visitRecord.setOrgId(treatmentRecord.getOrgId());
                      visitRecord.setTreatmentDate(treatmentRecord.getTreatStartTime());
                      Registered registered =
                          registeredBiz.selectById(treatmentRecord.getRegisteredId());
                      if (null != registered) {
                        visitRecord.setDentistId(registered.getDentistId());
                        visitRecord.setDeptRoomId(registered.getDeptRoomId());
                      }
                    }
                    visitRecord.setCrtId(detail.getCrtId());
                    visitRecord.setCrtName(detail.getCrtName());
                    visitRecord.setTreatmentId(treatmentRecordId);
                    visitRecord.setVisitingDate(
                        DateUtils.addDays(
                            new Date(System.currentTimeMillis()), nn));
                    treatmentOtherFeign.insertVisitingRecord(visitRecord);
                  });
        }
      }
    }
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
  public PageInfo<AppPatientTreatmentInfoVO> findAppTreatList(AppTreatListQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    Integer orgId = query.getOrgId();
    String queryDate = query.getQueryDate();
    Integer dentistId = query.getDentistId();

    AppAppointmentInfoQuery form = new AppAppointmentInfoQuery();
    form.setOrgId(orgId);
    form.setQueryDate(queryDate);
    form.setWhetherPage(false);
    List<Appointment> appointments = appointmentFeign.findAppointmentList(form);
    if (StringHelper.isNotEmpty(appointments)) {

    }
    return null;
  }
}
