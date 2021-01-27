package com.yunya.modules.employee.expand.service;

import com.google.common.base.Objects;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.EmployeeInfoQueryForm;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.EmployeeInfoVO;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.expand.ClinicEmployeeConfig;
import com.yunya.models.system.DepartmentRoom;
import com.yunya.modules.employee.expand.mapper.ClinicEmployeeConfigMapper;
import com.yunya.modules.employee.expand.model.request.ClinicEmployeeConfigQueryReq;
import com.yunya.modules.employee.expand.model.request.ClinicEmployeeConfigReq;
import com.yunya.modules.employee.expand.model.response.ClinicEmployeeConfigRes;
import com.yunya.modules.employee.expand.model.response.EnableChooseEmployeeRes;
import com.yunya.modules.employee.expand.model.response.EnableEmployeeRes;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.BusinessConstants.DENTIST_GROUP_ID;
import static com.yunya.framework.common.constant.BusinessConstants.USER_RESIGNATION_STATUS;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-05-27 9:59
 */
@Service
public class ClinicEmployeeConfigBiz
    extends BaseBiz<ClinicEmployeeConfigMapper, ClinicEmployeeConfig> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClinicEmployeeConfigBiz.class);

  @Resource private RemoteSystemServiceFeign systemServiceFeign;

  /**
   * 门诊端 员工配置
   *
   * @param employeeId
   * @param configRequest
   */
  public void modifyClinicEmployeeConfig(
      Integer employeeId, Integer clinicId, ClinicEmployeeConfigReq configRequest) {
    SysUserInfoDetail employee = systemServiceFeign.findSysUserEmployeeInfoByUserId(employeeId);
    if (USER_RESIGNATION_STATUS.equals(employee.getWorkStatus())) {
      throw new ClientServiceException("员工已离职", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    // 查询该员工对应扩展表主键
    ClinicEmployeeConfig clinicEmployeeConfig = new ClinicEmployeeConfig();
    clinicEmployeeConfig.setEmployeeId(employeeId);
    clinicEmployeeConfig.setClinicId(clinicId);
    clinicEmployeeConfig = mapper.selectOne(clinicEmployeeConfig);
    ClinicEmployeeConfig updateEmployee =
        EntityUtils.build(configRequest, ClinicEmployeeConfig.class);
    updateEmployee.setEmployeeId(employeeId);
    updateEmployee.setClinicId(clinicId);
    if (clinicEmployeeConfig == null) {
      mapper.insertSelective(updateEmployee);
    } else {
      BeanUtils.copyProperties(configRequest, clinicEmployeeConfig);
      mapper.updateByPrimaryKey(clinicEmployeeConfig);
    }
  }

  /**
   * 查询门诊员工配置信息
   *
   * @param req
   * @return
   */
  public ClinicEmployeeConfigRes getEmployeeConfig(ClinicEmployeeConfigQueryReq req) {
    ClinicEmployeeConfigRes result = new ClinicEmployeeConfigRes();
    Integer employeeId = req.getEmployeeId();
    Example example = new Example(ClinicEmployeeConfig.class);
    example
        .createCriteria()
        .andEqualTo("clinicId", req.getClinicId())
        .andEqualTo("employeeId", employeeId);
    ClinicEmployeeConfig config = mapper.selectOneByExample(example);
    if (config == null) {
      result.setEnableAppoint(1);
      result.setEnableRegistry(1);
      return result;
    }
    result = EntityUtils.build(config, ClinicEmployeeConfigRes.class);
    SysUserInfoDetail assistantEmployee =
        systemServiceFeign.findSysUserEmployeeInfoByUserId(employeeId);
    // 查询科室信息
    DepartmentRoom room =
        result.getClinicDepartmentRoomId() == null
            ? null
            : systemServiceFeign.findDepartmentRoomById(result.getClinicDepartmentRoomId());
    result.setAssistantName(assistantEmployee == null ? null : assistantEmployee.getName());
    result.setClinicDepartmentRoomName(room == null ? null : room.getName());
    return result;
  }

  /**
   * 查询可预约，可挂号医生
   *
   * @param clinicId 门诊id
   * @return
   */
  public EnableEmployeeRes getAllEnableEmployee(Integer clinicId) {
    LOGGER.info("查询可预约，挂号接口请求参数：clinic:{}", clinicId);
    Example example = new Example(ClinicEmployeeConfig.class);
    example.createCriteria().andEqualTo("clinicId", clinicId).andEqualTo("inservice", true);
    List<ClinicEmployeeConfig> allList = mapper.selectByExample(example);

    // 查询可预约可挂号医生，如果clinicEmployeeConfig表中没有对应数据，并且systemUser表中存在数据，则为可预约可挂号
    // 如果clinicEmployeeConfig表中有数据，则根据表中对应可预约字段和可挂号字段来判断是否可预约可挂号
    SysUserEmployeeModel sysUserQuery = new SysUserEmployeeModel();
    List<Integer> orgIds = new ArrayList();
    orgIds.add(clinicId);
    sysUserQuery.setOrgIds(orgIds);
    List<Integer> postGroupIds = new ArrayList<>();
    // 设置医生岗位组
    postGroupIds.add(DENTIST_GROUP_ID);
    sysUserQuery.setPostGroupId(postGroupIds);
    sysUserQuery.setWorkStatus(new Byte[] {0, 1, 3});
    sysUserQuery.setWhetherPage(false);
    List<SysUserInfoDetail> sysUserEmployeeInfoList =
        systemServiceFeign.findSysUserEmployeeInfoList(sysUserQuery);

    EnableEmployeeRes res = new EnableEmployeeRes();
    if (CollectionUtils.isNotEmpty(allList)) {
      List<SysUserInfoDetail> enableAppointRegisterUsers =
          sysUserEmployeeInfoList.stream()
              .filter(
                  sysUser -> {
                    Integer userId = sysUser.getUserId();
                    List<ClinicEmployeeConfig> collect =
                        allList.stream()
                            .filter(config -> config.getEmployeeId().equals(userId))
                            .collect(Collectors.toList());
                    if (StringHelper.isNotEmpty(collect)) {
                      ClinicEmployeeConfig clinicEmployeeConfig = collect.get(0);
                      Integer enableAppoint = clinicEmployeeConfig.getEnableAppoint();
                      Integer enableRegistry = clinicEmployeeConfig.getEnableRegistry();
                      if (enableAppoint.equals(1) && enableRegistry.equals(1)) {
                        return true;
                      } else {
                        return false;
                      }
                    } else {
                      return true;
                    }
                  })
              .collect(Collectors.toList());

      List<EnableChooseEmployeeRes> appointResList = null;
      List<EnableChooseEmployeeRes> registerResList = null;
      // 可预约医生
      if (StringHelper.isNotEmpty(enableAppointRegisterUsers)) {
        // 可预约医生
        List<ClinicEmployeeConfig> appointList =
            allList.stream()
                .filter(
                    config ->
                        Objects.equal(BusinessConstants.ENABLE_NUM, config.getEnableAppoint()))
                .collect(Collectors.toList());
        // 可挂号医生
        List<ClinicEmployeeConfig> registerList =
            allList.stream()
                .filter(
                    config ->
                        Objects.equal(BusinessConstants.ENABLE_NUM, config.getEnableRegistry()))
                .collect(Collectors.toList());
        appointResList =
            appointList.stream().map(this::assembleEnableEmployee).collect(Collectors.toList());
        registerResList =
            registerList.stream().map(this::assembleEnableEmployee).collect(Collectors.toList());
        for (SysUserInfoDetail user : enableAppointRegisterUsers) {
          List<ClinicEmployeeConfig> collect =
              allList.stream()
                  .filter(item -> item.getEmployeeId().equals(user.getUserId()))
                  .collect(Collectors.toList());
          if (StringHelper.isEmpty(collect)) {
            EnableChooseEmployeeRes enableChooseEmployeeRes = new EnableChooseEmployeeRes();
            enableChooseEmployeeRes.setEmployeeId(user.getUserId());
            enableChooseEmployeeRes.setEmployeeName(user.getName());
            appointResList.add(enableChooseEmployeeRes);
            registerResList.add(enableChooseEmployeeRes);
          }
        }
      }
      res.setEnableAppointList(
          StringHelper.isNotEmpty(appointResList) ? appointResList : new ArrayList<>());
      res.setEnableRegistryList(
          StringHelper.isNotEmpty(registerResList) ? registerResList : new ArrayList<>());
    } else {
      List<EnableChooseEmployeeRes> enableChooseEmployeeRes = new ArrayList<>();
      sysUserEmployeeInfoList.forEach(
          item -> {
            EnableChooseEmployeeRes entity = new EnableChooseEmployeeRes();
            entity.setEmployeeName(item.getName());
            entity.setEmployeeId(item.getUserId());
            enableChooseEmployeeRes.add(entity);
          });
      res.setEnableAppointList(enableChooseEmployeeRes);
      res.setEnableRegistryList(enableChooseEmployeeRes);
    }
    return res;
  }

  private EnableChooseEmployeeRes assembleEnableEmployee(ClinicEmployeeConfig config) {
    EnableChooseEmployeeRes configRes = new EnableChooseEmployeeRes();
    BeanCopier copier =
        BeanCopier.create(ClinicEmployeeConfig.class, EnableChooseEmployeeRes.class, false);
    copier.copy(config, configRes, null);
    if (config.getEmployeeId() != null) {
      SysUserInfoDetail employee =
          systemServiceFeign.findSysUserEmployeeInfoByUserId(config.getEmployeeId());
      configRes.setEmployeeName(employee == null ? null : employee.getName());
    }
    if (config.getAssistantEmployeeId() != null) {
      SysUserInfoDetail assist =
          systemServiceFeign.findSysUserEmployeeInfoByUserId(config.getAssistantEmployeeId());
      configRes.setAssistantName(assist == null ? null : assist.getName());
    }
    if (config.getClinicDepartmentRoomId() != null) {
      DepartmentRoom departmentRoom =
          systemServiceFeign.findDepartmentRoomById(config.getClinicDepartmentRoomId());
      configRes.setClinicDepartmentRoomName(
          departmentRoom == null ? null : departmentRoom.getName());
    }
    return configRes;
  }

  /**
   * 新增员工可预约可挂号配置
   *
   * @param clinicEmployeeConfig
   * @return
   */
  public Integer addEmployeeConfig(ClinicEmployeeConfig clinicEmployeeConfig) {
    ClinicEmployeeConfig entity = new ClinicEmployeeConfig();
    entity.setEmployeeId(clinicEmployeeConfig.getEmployeeId());
    entity.setClinicId(clinicEmployeeConfig.getClinicId());
    ClinicEmployeeConfig config = mapper.selectOne(entity);
    if (null != config) {
      mapper.deleteByPrimaryKey(config.getId());
    }
    return mapper.insertSelective(clinicEmployeeConfig);
  }

  /**
   * 编辑员工可预约配置
   *
   * @param clinicEmployeeConfigs
   * @return
   */
  public Integer editEmployeeConfig(List<ClinicEmployeeConfig> clinicEmployeeConfigs) {
    return mapper.updateBatch(clinicEmployeeConfigs);
  }

  /**
   * 根据员工ID查询该员工在所有门诊的可预约可挂号信息
   *
   * @param employeeId
   * @return
   */
  public List<ClinicEmployeeConfig> findClinicEmployeeConfigs(Integer employeeId) {
    if (employeeId != null) {
      ClinicEmployeeConfig clinicEmployeeConfig = new ClinicEmployeeConfig();
      clinicEmployeeConfig.setEmployeeId(employeeId);
      return mapper.select(clinicEmployeeConfig);
    }
    return new ArrayList<>();
  }

  /**
   * 根据员工ID和组织ID删除员工配置信息
   *
   * @param employeeId 员工ID
   * @param clinicId 门诊ID
   * @return 成功返回 删除行数，否则返回0
   */
  public Integer deleteClinicEmployeeConfig(Integer employeeId, Integer clinicId) {
    if (employeeId != null && clinicId != null) {
      return mapper.deleteEmployeeConfig(employeeId, clinicId);
    }
    return 0;
  }

  /**
   * 获取门诊可预约员工列表
   *
   * @param orgId 组织ID
   * @return List<EnableChooseEmployeeRes>
   */
  public List<EnableChooseEmployeeRes> findEnableAppointEmployeeList(Integer orgId) {
    List<EnableChooseEmployeeRes> resultList = new ArrayList<>();
    // 获取门诊可登陆员工列表
    EmployeeInfoQueryForm query = new EmployeeInfoQueryForm();
    query.setOrgIds(new Integer[] {orgId});
    List<EmployeeInfoVO> employeeList = systemServiceFeign.findEnableLoginEmployeeList(query);
    if (StringHelper.isNotEmpty(employeeList)) {
      ClinicEmployeeConfig entity = new ClinicEmployeeConfig();
      for (EmployeeInfoVO vo : employeeList) {
        if (!USER_RESIGNATION_STATUS.equals(vo.getWorkStatus())) {
          Integer userId = vo.getUserId();
          entity.setClinicId(orgId);
          entity.setEmployeeId(userId);
          ClinicEmployeeConfig employeeConfig = mapper.selectOne(entity);
          EnableChooseEmployeeRes employeeRes = new EnableChooseEmployeeRes();
          employeeRes.setEmployeeId(vo.getUserId());
          employeeRes.setEmployeeName(vo.getName());
          if (null == employeeConfig) {
            resultList.add(employeeRes);
          } else {
            Integer enableAppoint = employeeConfig.getEnableAppoint();
            if (1 == enableAppoint) {
              Integer assistantEmployeeId = employeeConfig.getAssistantEmployeeId();
              employeeRes.setAssistantEmployeeId(assistantEmployeeId);
              if (null != assistantEmployeeId) {
                SysUserInfoDetail userEmployeeInfo =
                        systemServiceFeign.findSysUserEmployeeInfoByUserId(assistantEmployeeId);
                employeeRes.setAssistantName(userEmployeeInfo.getName());
              }
              Integer clinicDepartmentRoomId = employeeConfig.getClinicDepartmentRoomId();
              employeeRes.setClinicDepartmentRoomId(clinicDepartmentRoomId);
              if (null != clinicDepartmentRoomId) {
                DepartmentRoom departmentRoom =
                        systemServiceFeign.findDepartmentRoomById(clinicDepartmentRoomId);
                if (null != departmentRoom) {
                  employeeRes.setClinicDepartmentRoomName(departmentRoom.getName());
                }
              }
              resultList.add(employeeRes);
            }
          }
        }
      }
    }
    return resultList;
  }

  /**
   * 根据条件查询门诊可挂号医生列表
   *
   * @param orgId 组织ID
   * @return List<EnableChooseEmployeeRes>
   */
  public List<EnableChooseEmployeeRes> findEnableRegistryEmployeeList(Integer orgId) {
    List<EnableChooseEmployeeRes> resultList = new ArrayList<>();
    // 获取门诊可登陆员工列表
    EmployeeInfoQueryForm query = new EmployeeInfoQueryForm();
    query.setOrgIds(new Integer[] {orgId});
    query.setPostGroupIds(new Integer[] {DENTIST_GROUP_ID});
    List<EmployeeInfoVO> employeeList = systemServiceFeign.findEnableLoginEmployeeList(query);
    if (StringHelper.isNotEmpty(employeeList)) {
      ClinicEmployeeConfig entity = new ClinicEmployeeConfig();
      for (EmployeeInfoVO vo : employeeList) {
        if (!USER_RESIGNATION_STATUS.equals(vo.getWorkStatus())) {
          Integer userId = vo.getUserId();
          entity.setClinicId(orgId);
          entity.setEmployeeId(userId);
          ClinicEmployeeConfig employeeConfig = mapper.selectOne(entity);
          EnableChooseEmployeeRes employeeRes = new EnableChooseEmployeeRes();
          employeeRes.setEmployeeId(vo.getUserId());
          employeeRes.setEmployeeName(vo.getName());
          if (null == employeeConfig) {
            resultList.add(employeeRes);
          } else {
            Integer enableRegistry = employeeConfig.getEnableRegistry();
            if (1 == enableRegistry) {
              Integer assistantEmployeeId = employeeConfig.getAssistantEmployeeId();
              employeeRes.setAssistantEmployeeId(assistantEmployeeId);
              if (null != assistantEmployeeId) {
                SysUserInfoDetail userEmployeeInfo =
                        systemServiceFeign.findSysUserEmployeeInfoByUserId(assistantEmployeeId);
                employeeRes.setAssistantName(userEmployeeInfo.getName());
              }
              Integer clinicDepartmentRoomId = employeeConfig.getClinicDepartmentRoomId();
              employeeRes.setClinicDepartmentRoomId(clinicDepartmentRoomId);
              if (null != clinicDepartmentRoomId) {
                DepartmentRoom departmentRoom =
                        systemServiceFeign.findDepartmentRoomById(clinicDepartmentRoomId);
                if (null != departmentRoom) {
                  employeeRes.setClinicDepartmentRoomName(departmentRoom.getName());
                }
              }
              resultList.add(employeeRes);
            }
          }
        }
      }
    }
    return resultList;

  }
}
