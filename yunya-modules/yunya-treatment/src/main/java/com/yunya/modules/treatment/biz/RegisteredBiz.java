package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.model.DebtAmountModel;
import com.yunya.feign.treatment.domain.model.RegisteredModel;
import com.yunya.feign.treatment.domain.query.RegisteredQueryForm;
import com.yunya.feign.treatment.domain.vo.WaitingPatientInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseTreatmentProcess;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.REDIS_KEY_REGISTERED;

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

  /** 消息中间件 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 患者服务调用 */
  @Autowired private RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;
  /** 预约服务调用 */
  @Autowired private RemoteAppointmentFeign appointmentFeign;
  /** 就诊记录 */
  @Autowired private TreatmentRecordMapper treatmentRecordMapper;
  /** 缓存 */
  @Autowired private RedisUtils redisUtils;
  /** 账单记录 */
  @Autowired private BillRecordBiz billRecordBiz;

  /**
   * 新增患者挂号
   *
   * @param model 挂号信息
   */
  public void save(RegisteredModel model) {
    Registered entity = new Registered();
    Integer appointmentId = model.getAppointmentId();
    if (null != appointmentId) {
      // 从redis中获取该预约是否在操作
      String regKey = REDIS_KEY_REGISTERED + appointmentId;
      String regValue = redisUtils.get(regKey);
      if (StringHelper.isNotBlank(regValue)) {
        throw new ClientServiceException("挂号失败，当前预约正在被操作，请稍后再试！", PARAMETERS_IS_ILLEGAL);
      }
      entity.setAppointmentId(appointmentId);
      entity.setInservice(true);
      int regCount = mapper.selectCount(entity);
      if (regCount > 0) {
        throw new ClientServiceException("挂号失败，当前预约已被挂号，请勿重复挂号！", PARAMETERS_IS_ILLEGAL);
      }
      Appointment appointment = appointmentFeign.findAppointmentById(appointmentId);
      if (null != appointment) {
        appointment.setAppointStatus((byte) 1);
        appointmentFeign.updateAppointment(appointment);
      }
      redisUtils.set(regKey, appointmentId, 5);
      buildRegistered(model, entity);
      int i = mapper.insertSelective(entity);
      if (i > 0) {
        rabbitMqServiceFeign.sendMessage(appointmentId, 0, 1, BaseTreatmentProcess);
      }
      redisUtils.delete(regKey);
    } else {
      buildRegistered(model, entity);
      int i = mapper.insertSelective(entity);
      if (i > 0) {
        rabbitMqServiceFeign.sendMessage(entity.getId(), 1, 0, BaseTreatmentProcess);
      }
    }
  }

  /**
   * 构建挂号模型
   *
   * @param model 参数模型
   * @param entity 挂号
   */
  private void buildRegistered(RegisteredModel model, Registered entity) {
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
    int i = mapper.updateByPrimaryKeySelective(resultData);
    // 发送消息同步中间表就诊流程数据
    if (i > 0) {
      if (null != appointmentId) {
        rabbitMqServiceFeign.sendMessage(appointmentId, 0, 1, BaseTreatmentProcess);
      }
      rabbitMqServiceFeign.sendMessage(id, 1, 2, BaseTreatmentProcess);
    }
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
      // 预约ID集合
      List<Integer> appointIds = new ArrayList<>();
      List<Integer> appointAssistentIds = new ArrayList<>();
      List<Integer> appointDentistId = new ArrayList<>();
      List<Integer> appointDeptRoomIds = new ArrayList<>();
      List<Integer> patientIds = new ArrayList<>();
      List<Integer> registeredAssistentIds = new ArrayList<>();
      List<Integer> registeredDeptRoomIds = new ArrayList<>();
      List<Integer> registeredDentistIds = new ArrayList<>();
      registeredList.forEach(vo -> {
        patientIds.add(vo.getPatientId());
        appointDeptRoomIds.add(vo.getAppointDeptRoomId());
        appointDentistId.add(vo.getAppointDentistId());
        appointIds.add(vo.getAppointmentId());
        appointAssistentIds.add(vo.getAppointAssistantId());
        registeredAssistentIds.add(vo.getRegAssistantId());
        registeredDeptRoomIds.add(vo.getRegDeptRoomId());
        registeredDentistIds.add(vo.getRegDentistId());
      });
      // 设置患者信息
      setPatientInfo(registeredList,
              patientIds,
              appointIds,
              appointDentistId,
              appointAssistentIds,
              appointDeptRoomIds,
              registeredDentistIds,
              registeredAssistentIds,
              registeredDeptRoomIds);
    }
    return new PageInfo<>(registeredList);
  }

  /**
   * 设置候诊患者患者信息
   * @param vos 患者候诊
   * @param patientIds 患者ID集合
   * @param appointIds 预约ID集合
   * @param appointDentistIds 预约医生ID集合
   * @param appointAssistentIds 预约助手ID集合
   * @param appointDeptRoomIds  预约科室ID
   * @param registeredDentistIds 挂号医生ID集合
   * @param registeredAssistentIds 挂号助手ID集合
   * @param registeredDeptRoomIds 挂号科室ID
   */
  private void setPatientInfo(List<WaitingPatientInfoVO> vos,
                              List<Integer> patientIds,
                              List<Integer> appointIds,
                              List<Integer> appointDentistIds,
                              List<Integer> appointAssistentIds,
                              List<Integer> appointDeptRoomIds,
                              List<Integer> registeredDentistIds,
                              List<Integer> registeredAssistentIds,
                              List<Integer> registeredDeptRoomIds) {
    // 患者信息列表
    List<PatientTotalInfoVo> patientInfoByIds = remotePatientCentralServiceFeign.findPatientTotalInfo(patientIds);
    // 欠费金额列表
    List<DebtAmountModel> debtAmountList1 = billRecordBiz.selectDebtAmountList(patientIds);
    // 会员卡类型ID
    List<Integer> memberTypeIds = new ArrayList<>();
    patientInfoByIds.forEach(patientTotalInfoVo -> {
      memberTypeIds.add(patientTotalInfoVo.getMemberTypeId());
    });
    // 获取会员类型信息
    List<MemberType> memberTypeByIds = systemServiceFeign.findMemberTypeByIds(memberTypeIds);
    // 获取预约患者
    List<Appointment> appointmentList = appointmentFeign.findAppointmentListByIds(appointIds);
    // 将dentistIds合并到assistentIds中
    appointDentistIds.stream().sequential().collect(Collectors.toCollection(()->appointAssistentIds));
    // 根据预约医生ID和预约助手ID列表
    List<SysUserInfoDetail> appointDentistAndAssistentInfoList = systemServiceFeign.findSysUserEmployeeInfoByUserIds(appointAssistentIds);
    // 根据预约科室ID集合查询预约科室信息列表
    List<DepartmentRoom> appointDepartmentRooms = systemServiceFeign.findDepartmentRoomByIds(appointDeptRoomIds);
    // 根据挂号医生ID集合和挂号助手ID集合查询挂号医生、助手信息列表
    // 将dentistIds合并到assistentIds中
    registeredDentistIds.stream().sequential().collect(Collectors.toCollection(()->registeredAssistentIds));
    List<SysUserInfoDetail> registeredDentistAndAssistentInfoList = systemServiceFeign.findSysUserEmployeeInfoByUserIds(registeredAssistentIds);
    // 根据挂号科室ID集合查询挂号科室信息列表
    List<DepartmentRoom> registeredDepartmentRooms = systemServiceFeign.findDepartmentRoomByIds(registeredDeptRoomIds);

    // 注入患者基本信息
    vos.forEach(patientEntity -> {
      Integer patientId1 = patientEntity.getPatientId();
      // 设置患者基本信息
      if (StringHelper.isNotEmpty(patientInfoByIds)) {
        List<PatientTotalInfoVo> collect = patientInfoByIds.stream().filter(
                patientTotalInfoVo -> patientTotalInfoVo.getId().equals(patientId1)).collect(Collectors.toList());
        if (StringHelper.isNotEmpty(collect)) {
          PatientTotalInfoVo patientTotalInfoVo = collect.get(0);
          patientEntity.setPatientName(patientTotalInfoVo.getName());
          patientEntity.setMobile(patientTotalInfoVo.getMobile());
          patientEntity.setGender(patientTotalInfoVo.getGender());
          patientEntity.setAge(patientTotalInfoVo.getAge());
          patientEntity.setBirthday(patientTotalInfoVo.getBirthday());
          patientEntity.setPatientRemark(patientTotalInfoVo.getRemarks());
          String medicalNumber = patientTotalInfoVo.getMedicalNumber();
          patientEntity.setMedicalNumber(StringHelper.isNotBlank(medicalNumber) ? medicalNumber : "--");
          patientEntity.setAllergen(patientTotalInfoVo.getAllergens());
          patientEntity.setPatientKind(patientTotalInfoVo.getPatientKindName());
          // 设置会员类型图标
          if (StringHelper.isNotEmpty(memberTypeByIds)) {
            List<MemberType> collect1 = memberTypeByIds.stream().filter(
                    memberType -> memberType.getId().equals(patientTotalInfoVo.getMemberTypeId())).collect(Collectors.toList());
            if (StringHelper.isNotEmpty(collect1)) {
              MemberType memberType = collect1.get(0);
              patientEntity.setMemberIcon(memberType.getIcon());
            }
          }
        }
      }
      // 设置候诊患者预约信息
      setAppointmentInfo(patientEntity,
              appointmentList,
              appointDentistAndAssistentInfoList,
              appointDepartmentRooms,
              patientEntity.getAppointmentId());
      // 设置候诊患者挂号信息
      setRegisteredInfo(patientEntity,registeredDentistAndAssistentInfoList,registeredDepartmentRooms);
      // 欠费金额
      if (StringHelper.isNotEmpty(debtAmountList1)) {
        List<DebtAmountModel> collect = debtAmountList1.stream().filter(
                debtAmountModel -> debtAmountModel.getPatientId().equals(patientId1)).collect(Collectors.toList());
        if (StringHelper.isNotEmpty(collect)) {
          DebtAmountModel debtAmountModel = collect.get(0);
          patientEntity.setArrears(debtAmountModel.getDebtAmount());
        }
      }

    });
  }

  /**
   * 设置候诊患者预约信息
   * @param vo  患者候诊信息
   * @param appointmentList 预约信息列表
   * @param dentistAndAssistentInfoList 预约医生和预约助手信息列表
   * @param departmentRooms  预约科室信息列表
   * @param appointId  预约ID
   */
  private void setAppointmentInfo(WaitingPatientInfoVO vo,
                                  List<Appointment> appointmentList,
                                  List<SysUserInfoDetail> dentistAndAssistentInfoList,
                                  List<DepartmentRoom> departmentRooms,
                                  Integer appointId) {
    if (StringHelper.isNotEmpty(appointmentList)) {
      // 根据appointId从预约列表中检索预约
      List<Appointment> collect = appointmentList.stream().filter(appointment -> appointment.getId().equals(appointId)).collect(Collectors.toList());
      if (StringHelper.isNotEmpty(collect)) {
        Appointment appointment = collect.get(0);
        vo.setAppointDentistId(appointment.getDentistId());
        // 根据医生ID从医生信息列表中查询医生名字
        Integer dentistId = appointment.getDentistId();
        List<SysUserInfoDetail> sysUserInfoDetailList = dentistAndAssistentInfoList.stream().filter(
                sysUserInfoDetail -> sysUserInfoDetail.getUserId().equals(dentistId)).collect(Collectors.toList());
        if (StringHelper.isNotEmpty(sysUserInfoDetailList)) {
          SysUserInfoDetail dentistInfo = sysUserInfoDetailList.get(0);
          vo.setAppointDentistName(null != dentistInfo ? dentistInfo.getName() : "--");
        }
        // 根据助手ID从医生信息列表中查询助手名字
        Integer assistantId = appointment.getAssistantId();
        List<SysUserInfoDetail> assistantInfos = dentistAndAssistentInfoList.stream().filter(
                sysUserInfoDetail -> sysUserInfoDetail.getUserId().equals(assistantId)).collect(Collectors.toList());
        if (StringHelper.isNotEmpty(assistantInfos)) {
          SysUserInfoDetail assistantInfo = assistantInfos.get(0);
          vo.setAppointAssistantName(null != assistantInfo ? assistantInfo.getName() : "--");
        }
        // 根据科室ID从科室信息列表中查询科室名字
        Integer deptRoomId = appointment.getDeptRoomId();
        List<DepartmentRoom> departmentRoomInfos = departmentRooms.stream().filter(
                departmentRoom -> departmentRoom.getId().equals(deptRoomId)).collect(Collectors.toList());
        if (StringHelper.isNotEmpty(departmentRoomInfos)) {
          vo.setAppointDeptRoomId(appointment.getDeptRoomId());
          DepartmentRoom departmentRoom = departmentRoomInfos.get(0);
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
   * @param vo 患者候诊信息
   * @param registeredDentistAndAssistentInfoList  挂号医生和挂号助手信息列表
   * @param registeredDepartmentRooms  挂号科室信息列表
   */
  private void setRegisteredInfo(WaitingPatientInfoVO vo,
                                 List<SysUserInfoDetail> registeredDentistAndAssistentInfoList,
                                 List<DepartmentRoom> registeredDepartmentRooms) {
    // 设置挂号医生、挂号助手信息
    if (StringHelper.isNotEmpty(registeredDentistAndAssistentInfoList)) {
      // 设置挂号医生
      Integer regDentistId = vo.getRegDentistId();
      List<SysUserInfoDetail> collect = registeredDentistAndAssistentInfoList.stream().filter(
              sysUserInfoDetail -> sysUserInfoDetail.getUserId().equals(regDentistId)).collect(Collectors.toList());
      if (StringHelper.isNotEmpty(collect)) {
        SysUserInfoDetail regDentistInfo = collect.get(0);
        vo.setRegDentistName(null != regDentistInfo ? regDentistInfo.getName() : "--");
      }
      // 设置挂号助手
      Integer regAssistantId = vo.getRegAssistantId();
      List<SysUserInfoDetail> regAssistantInfos = registeredDentistAndAssistentInfoList.stream().filter(
              sysUserInfoDetail -> sysUserInfoDetail.getUserId().equals(regAssistantId)).collect(Collectors.toList());
      if (StringHelper.isNotEmpty(regAssistantInfos)) {
        SysUserInfoDetail regAssistantInfo = regAssistantInfos.get(0);
        vo.setRegAssistantName(null != regAssistantInfo ? regAssistantInfo.getName() : "--");
      }
    }
    // 设置科室信息
    if (StringHelper.isNotEmpty(registeredDepartmentRooms)) {
      Integer regDeptRoomId = vo.getRegDeptRoomId();
      List<DepartmentRoom> collect = registeredDepartmentRooms.stream().filter(
              departmentRoom -> departmentRoom.getId().equals(regDeptRoomId)).collect(Collectors.toList());
      if (StringHelper.isNotEmpty(collect)) {
        DepartmentRoom departmentRoom = collect.get(0);
        vo.setRegDeptRoomName(null != departmentRoom ? departmentRoom.getName() : "--");
      }
    }
  }
}
