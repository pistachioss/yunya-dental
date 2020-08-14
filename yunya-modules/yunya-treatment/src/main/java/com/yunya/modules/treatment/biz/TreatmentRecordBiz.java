package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.PatientTotalInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.query.TreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.TreatmentPatientInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.DepartmentRoom;
import com.yunya.models.system.MemberType;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.mapper.TreatmentRecordMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

  /** 注入对象 */
  @Autowired private RegisteredBiz registeredBiz;

  /**
   * 开始接诊
   *
   * @param regId 挂号ID
   */
  public void startTreatment(Integer regId) {
    Registered regResult = registeredBiz.selectById(regId);
    if (null == regResult || !regResult.getInservice()) {
      throw new ClientServiceException(
          "接诊失败，您当前未选择接诊患者或传入参数有误！", OperationCodeConstants.QUERY_RESULT_INVALID);
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
    PatientBaseInfo patientInfo = new PatientBaseInfo();
    patientInfo.setOrgId(orgId);
    String number = patientServiceFeign.findMedicalNumberByOrgId(orgId);
    String suffix = String.format("%06d", Integer.parseInt(number) + 1);
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
    return String.format("%04d", Integer.parseInt(orgInfo.getClinicNumber()))
        + new DateTime().toString("yyMMdd")
        + suffix;
  }

  /**
   * 根据条件查询就诊中患者信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<TreatmentPatientInfoVO> findTreatingList(TreatmentRecordQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }

    List<TreatmentPatientInfoVO> treatingList = mapper.selectTreatingList(queryForm);
    if (StringHelper.isNotEmpty(treatingList)) {
      treatingList.forEach(
          vo -> {
            // 设置患者信息
            setPatientInfo(vo);
            // 设置候诊患者预约信息
            setAppointmentInfo(vo);
            // 设置候诊患者挂号信息
            setRegisteredInfo(vo);
            // 设置候诊患者接诊信息
            setTreatingInfo(vo);
          });
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
        vo.setAppointAssistantId(appointAssistantId);
        SysUserInfoDetail assistantInfo =
            systemServiceFeign.findSysUserEmployeeInfoByUserId(appointAssistantId);
        vo.setAppointAssistantName(null != assistantInfo ? assistantInfo.getName() : "--");
        Integer appointDeptRoomId = vo.getAppointDeptRoomId();
        vo.setAppointDeptRoomId(appointDeptRoomId);
        DepartmentRoom departmentRoom = systemServiceFeign.findDepartmentRoomById(appointmentId);
        vo.setAppointDeptRoomName(null != departmentRoom ? departmentRoom.getName() : "--");
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
        SysUserInfoDetail regAssistantInfo =
            systemServiceFeign.findSysUserEmployeeInfoByUserId(regAssistantId);
        vo.setRegAssistantName(null != regAssistantInfo ? regAssistantInfo.getName() : "--");
      }

      Integer regDeptRoomId = registered.getDeptRoomId();
      if (null != regDeptRoomId) {
        vo.setRegDeptRoomId(regDeptRoomId);
        DepartmentRoom departmentRoom = systemServiceFeign.findDepartmentRoomById(regDeptRoomId);
        vo.setRegDeptRoomName(null != departmentRoom ? departmentRoom.getName() : "--");
      }
      vo.setRegTime(String.valueOf(registered.getRegTime()));
    }
  }

  /**
   * 设置接诊信息
   *
   * @param vo 候诊患者信息
   */
  private void setTreatingInfo(TreatmentPatientInfoVO vo) {
    Integer treatDentistId = vo.getTreatDentistId();
    SysUserInfoDetail treatDentistInfo =
        systemServiceFeign.findSysUserEmployeeInfoByUserId(treatDentistId);
    vo.setTreatDentistName(null != treatDentistInfo ? treatDentistInfo.getName() : "--");
  }
}
