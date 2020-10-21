package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.model.RegisteredModel;
import com.yunya.feign.treatment.domain.query.RegisteredQueryForm;
import com.yunya.feign.treatment.domain.vo.WaitingPatientInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.system.DepartmentRoom;
import com.yunya.models.system.MemberType;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.mapper.RegisteredMapper;
import com.yunya.modules.treatment.mapper.TreatmentRecordMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 简介: 患者挂号业务层
 *
 * @author: chow
 * @date: 2020/8/11 11:24
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RegisteredBiz extends BaseBiz<RegisteredMapper, Registered> {

  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;

  /** 患者服务调用 */
  @Autowired private PatientCentralServiceFeign patientCentralServiceFeign;

  /** 预约服务调用 */
  @Autowired private RemoteAppointmentFeign appointmentFeign;

  /** 就诊记录 */
  @Autowired private TreatmentRecordMapper treatmentRecordMapper;

  /**
   * 新增患者挂号
   *
   * @param model 挂号信息
   */
  public void save(RegisteredModel model) {
    Registered entity = new Registered();
    Integer appointmentId = model.getAppointmentId();
    if (null != appointmentId) {
      // todo 锁定该预约，防止重复对该预约挂号
      entity.setAppointmentId(appointmentId);
      int regCount = mapper.selectCount(entity);
      if (regCount > 0) {
        throw new ClientServiceException("挂号失败，当前预约已被挂号，请勿重复挂号！", PARAMETERS_IS_ILLEGAL);
      }
      Appointment appointment = appointmentFeign.findAppointmentById(appointmentId);
      if (null != appointment) {
        appointment.setAppointStatus((byte) 1);
        appointmentFeign.updateAppointment(appointment);
      }
    }
    BeanUtils.copyProperties(model, entity);
    Integer patientId = model.getPatientId();
    TreatmentRecord treatmentrecord = new TreatmentRecord();
    treatmentrecord.setPatientId(patientId);
    int count = treatmentRecordMapper.selectCount(treatmentrecord);
    if (count >= 1) {
      entity.setFirstVisit((byte) 1);
    }
    entity.setRegTime(new Date(System.currentTimeMillis()));
    entity.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    mapper.insertSelective(entity);
  }

  /**
   * 取消患者挂号
   *
   * @param id 挂号ID
   */
  public void cancelRegistered(Integer id) {
    Registered resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException("取消挂号失败！ID为'" + id + "'的患者挂号记录不存在！", QUERY_RESULT_INVALID);
    }

    Byte status = resultData.getStatus();
    if (status != 0) {
      throw new ClientServiceException("取消挂号失败！ID为'" + id + "'的患者挂号处于就诊中，无法取消！！", OBJECT_EDIT_FAIL);
    }

    Integer appointmentId = resultData.getAppointmentId();
    if (null != appointmentId) {
      Appointment appointment = appointmentFeign.findAppointmentById(appointmentId);
      if (null != appointment) {
        appointment.setAppointStatus((byte) 0);
        appointmentFeign.updateAppointment(appointment);
      }
    }

    resultData.setInservice(false);
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 根据条件查询候诊中患者信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<WaitingPatientInfoVO> findRegisteredList(RegisteredQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }

    List<WaitingPatientInfoVO> registeredList = mapper.selectRegisteredList((byte) 0, queryForm);
    if (StringHelper.isNotEmpty(registeredList)) {
      registeredList.forEach(
          vo -> {
            // 设置候诊患者个人信息
            setPatientInfo(vo);
            // 设置候诊患者预约信息
            setAppointmentInfo(vo);
            // 设置候诊患者挂号信息
            setRegisteredInfo(vo);
          });
    }
    return new PageInfo<>(registeredList);
  }

  /**
   * 设置候诊患者患者信息
   *
   * @param vo 患者候诊
   */
  private void setPatientInfo(WaitingPatientInfoVO vo) {
    Integer patientId = vo.getPatientId();
    PatientTotalInfoVo patientData = patientCentralServiceFeign.findPatientTotalInfo(patientId);
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
  private void setAppointmentInfo(WaitingPatientInfoVO vo) {
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
          // todo 从缓存中查询用户
          SysUserInfoDetail assistantInfo =
              systemServiceFeign.findSysUserEmployeeInfoByUserId(appointAssistantId);
          vo.setAppointAssistantName(null != assistantInfo ? assistantInfo.getName() : "--");
        }
        Integer appointDeptRoomId = vo.getAppointDeptRoomId();
        if (null != appointDeptRoomId) {
          vo.setAppointDeptRoomId(appointDeptRoomId);
          // todo 从缓存中查询科室
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
  private void setRegisteredInfo(WaitingPatientInfoVO vo) {
    Integer regDentistId = vo.getRegDentistId();
    SysUserInfoDetail regDentistInfo =
        systemServiceFeign.findSysUserEmployeeInfoByUserId(regDentistId);
    vo.setRegDentistName(null != regDentistInfo ? regDentistInfo.getName() : "--");
    Integer regAssistantId = vo.getRegAssistantId();
    if (null != regAssistantId) {
      SysUserInfoDetail regAssistantInfo =
          systemServiceFeign.findSysUserEmployeeInfoByUserId(regAssistantId);
      vo.setRegAssistantName(null != regAssistantInfo ? regAssistantInfo.getName() : "--");
    }
    Integer regDeptRoomId = vo.getRegDeptRoomId();
    if (null != regDeptRoomId) {
      DepartmentRoom departmentRoom = systemServiceFeign.findDepartmentRoomById(regDeptRoomId);
      vo.setRegDeptRoomName(null != departmentRoom ? departmentRoom.getName() : "--");
    }
  }
}
