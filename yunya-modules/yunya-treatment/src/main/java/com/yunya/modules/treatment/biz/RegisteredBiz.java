package com.yunya.modules.treatment.biz;

import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.PatientExtendInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.model.RegisteredModel;
import com.yunya.feign.treatment.domain.query.RegisteredQueryForm;
import com.yunya.feign.treatment.domain.vo.WaitingPatientInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientExpInfo;
import com.yunya.models.patient_central.PatientExtInfo;
import com.yunya.models.patient_central.PatientMemberInfo;
import com.yunya.models.system.DepartmentRoom;
import com.yunya.models.system.DictionaryItem;
import com.yunya.models.system.MemberType;
import com.yunya.models.treatment.Registered;
import com.yunya.modules.treatment.mapper.RegisteredMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

  /**
   * 新增患者挂号
   *
   * @param model 挂号信息
   */
  public void save(RegisteredModel model) {
    Integer appointmentId = model.getAppointmentId();
    if (null != appointmentId) {
      Appointment appointment = appointmentFeign.findAppointmentById(appointmentId);
      if (null != appointment) {
        appointment.setAppointStatus((byte) 1);
        appointmentFeign.updateAppointment(appointment);
      }
    }
    Registered entity = new Registered();
    BeanUtils.copyProperties(model, entity);
    entity.setRegTime(new DateTime(System.currentTimeMillis()));
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
      throw new ClientServiceException(
          "取消挂号失败！ID为'" + id + "'的患者挂号记录不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
    }

    Byte status = resultData.getStatus();
    if (status != 0) {
      throw new ClientServiceException(
          "取消挂号失败！ID为'" + id + "'的患者挂号处于就诊中，无法取消！！", OperationCodeConstants.OBJECT_EDIT_FAIL);
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

    List<WaitingPatientInfoVO> registeredList = mapper.selectRegisteredList(queryForm);
    if (registeredList.size() > 0) {
      for (WaitingPatientInfoVO vo : registeredList) {
        // 患者信息
        Integer patientId = vo.getPatientId();
        PatientExtendInfoVo patientData = patientCentralServiceFeign.findPatientData(patientId);
        if (null != patientData) {
          PatientBaseInfo patientBaseInfo = patientData.getPatientBaseInfo();
          if (null != patientBaseInfo) {
            vo.setPatientName(patientBaseInfo.getName());
            vo.setMobile(patientBaseInfo.getMobile());
            vo.setGender(patientBaseInfo.getGender());
            vo.setAge(patientBaseInfo.getAge());
            vo.setBirthday(patientBaseInfo.getBirthday().toString());
            vo.setPatientRemark(patientBaseInfo.getRemarks());
            String medicalNumber = patientBaseInfo.getMedicalNumber();
            vo.setMedicalNumber(StringHelper.isNotBlank(medicalNumber) ? medicalNumber : "--");
            vo.setFirstVisit(StringHelper.isNotBlank(medicalNumber) ? (byte) 1 : (byte) 0);
          }

          PatientExpInfo patientExpInfo = patientData.getPatientExpInfo();
          if (null != patientExpInfo) {
            Integer patientKind = patientExpInfo.getPatientKind();
            DictionaryItem dictionaryItem = systemServiceFeign.findDictionaryItemById(patientKind);
            vo.setPatientKind(null != dictionaryItem ? dictionaryItem.getName() : "--");
          }

          StringBuilder allergen = new StringBuilder();
          List<PatientExtInfo> extInfoList = patientData.getPatientExtInfoList();
          if (StringHelper.isNotEmpty(extInfoList)) {
            for (PatientExtInfo extInfo : extInfoList) {
              if (extInfo.getType() == 2) {
                DictionaryItem dictionaryItem =
                    systemServiceFeign.findDictionaryItemById(extInfo.getDictItemId());
                if (null != dictionaryItem) {
                  allergen.append(dictionaryItem.getName());
                }
              }
            }
          }
          vo.setAllergen(allergen.toString());

          PatientMemberInfo memberInfo = new PatientMemberInfo();
          memberInfo.setPatientId(patientId);
          List<PatientMemberInfo> memberInfos =
              patientCentralServiceFeign.findPatientMemberInfo(memberInfo);
          if (memberInfos.size() > 0) {
            for (PatientMemberInfo info : memberInfos) {
              Integer memberTypeId = info.getMemberTypeId();
              MemberType memberType = systemServiceFeign.findMemberTypeById(memberTypeId);
              if (null != memberType) {
                vo.setMemberIcon(String.valueOf(memberType.getIcon()));
              }
            }
          }
        }

        // 设置预约信息
        setAppointmentInfo(vo);

        // 设置挂号信息
        setRegisteredInfo(vo);
      }
    }
    return new PageInfo<>(registeredList);
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
}
