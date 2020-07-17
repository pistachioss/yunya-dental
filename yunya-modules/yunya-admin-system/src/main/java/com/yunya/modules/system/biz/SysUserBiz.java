package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.vo.SysUserEmployeeInfo;
import com.yunya.feign.system.vo.UserInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.constant.UserConstant;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.system.DictionaryItem;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.system.SysUser;
import com.yunya.models.system.SysUserPost;
import com.yunya.modules.system.form.LoginOrganizationForm;
import com.yunya.modules.system.form.SysUserForm;
import com.yunya.modules.system.form.query.SysEmployeeQueryForm;
import com.yunya.modules.system.mapper.SysEmployeeMapper;
import com.yunya.modules.system.mapper.SysUserMapper;
import com.yunya.modules.system.vo.SysEmployeeVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 简单介绍:</br> 系统用户业务层
 *
 * @author: chow
 * @date: 2020/6/6 16:46
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysUserBiz extends BaseBiz<SysUserMapper, SysUser> {

  /** 用户的员工信息 */
  @Autowired private SysEmployeeMapper sysEmployeeMapper;
  /** 用户可登陆组织 */
  @Autowired private SysUserPostBiz sysUserPostBiz;
  /** 字典明细 */
  @Autowired private DictionaryItemBiz dictionaryItemBiz;
  /** 缓存 */
  @Autowired private RedisUtils redisUtils;

  /**
   * 新增用户
   *
   * @param resource 参数封装
   */
  public void add(SysUserForm resource) {
    checkUserNameUnique(resource.getName());
    checkIdentityUnique(resource.getIdentity());
    checkMobileUnique(resource.getMobilePhone());
    SysUser sysUser = EntityUtils.build(resource, SysUser.class);
    sysUser.setUsername(resource.getMobilePhone());
    // 密码加密，加盐
    sysUser.setPassword(
        new BCryptPasswordEncoder(UserConstant.PW_ENCODER_SALT)
            .encode(UserConstant.DEFAULT_USER_PASSWORD));
    // 新增用户基础信息
    int result = mapper.insertUser(sysUser);
    if (result > 0) {
      Integer userId = sysUser.getId();
      SysEmployee sysEmployee = EntityUtils.build(resource, SysEmployee.class);
      sysEmployee.setUserId(userId);
      // 新增员工就职状态为离职处理
      if (BusinessConstants.USER_RESIGNATION_STATUS.equals(resource.getWorkStatus())) {
        sysEmployee.setLeaveTime(
            null == resource.getLeaveDate()
                ? new Date(System.currentTimeMillis())
                : resource.getLeaveDate());
      }
      // 新增用户扩展信息（员工信息）
      sysEmployeeMapper.insertSelective(sysEmployee);
      List<LoginOrganizationForm> organizationForms = resource.getLoginOrganizationForms();
      if (null != organizationForms && organizationForms.size() > 0) {
        // 新增用户与组织、部门、岗位的关系
        insertUserLoginOrganization(userId, organizationForms);
      }
    }
  }

  /**
   * 新增用户可登陆组织
   *
   * @param userId 用户ID
   * @param organizationForms 可登录组织列表
   */
  private void insertUserLoginOrganization(
      Integer userId, List<LoginOrganizationForm> organizationForms) {
    SysUserPost entity;
    for (LoginOrganizationForm form : organizationForms) {
      entity = new SysUserPost();
      entity.setCompanyId(form.getOrgId());
      entity.setDepartmentId(form.getOrgDeptId());
      entity.setPostId(form.getPostId());
      entity.setUserId(userId);
      sysUserPostBiz.add(entity);
    }
  }

  /**
   * 检查手机号是否存在
   *
   * @param mobile 手机号
   */
  private void checkMobileUnique(String mobile) {
    Integer count = mapper.checkMobileUnique(mobile);
    if (count > 0) {
      throw new ClientServiceException(
          "身份证号'" + mobile + "'已存在", OperationCodeConstants.SAME_DATA_EXIST);
    }
  }

  /**
   * 检查身份证号是否存在
   *
   * @param identity 身份证号
   */
  private void checkIdentityUnique(String identity) {
    Integer count = mapper.checkIdentityUnique(identity);
    if (count > 0) {
      throw new ClientServiceException(
          "身份证号'" + identity + "'已存在", OperationCodeConstants.SAME_DATA_EXIST);
    }
  }

  /**
   * 检查员工姓名是否唯一
   *
   * @param name 员工姓名
   */
  private void checkUserNameUnique(String name) {
    Integer count = mapper.checkUserNameUnique(name);
    if (count > 0) {
      throw new ClientServiceException(
          "员工姓名'" + name + "'已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
  }

  /**
   * 修改用户信息
   *
   * @param userId 用户ID
   * @param form 修改参数封装
   */
  public void edit(Integer userId, SysUserForm form) {
    SysUser sysUser = checkUserExist(userId);
    SysUser sysUserEntity = EntityUtils.build(form, SysUser.class);
    // 检查用户名是否更换
    sysUserEntity.setId(sysUser.getId());
    sysUserEntity.setUsername(form.getMobilePhone());
    int result = mapper.updateByPrimaryKeySelective(sysUserEntity);
    if (result > 0) {
      SysEmployee employeeResult = sysEmployeeMapper.selectByUserId(userId);
      SysEmployee sysEmployeeEntity = EntityUtils.build(form, SysEmployee.class);
      sysEmployeeEntity.setId(employeeResult.getId());
      sysEmployeeMapper.updateByPrimaryKeySelective(sysEmployeeEntity);
    }
    // 用户名被修改或就职状态改为离职
    if (!sysUser.getUsername().equals(form.getMobilePhone())
        || BusinessConstants.USER_RESIGNATION_STATUS.equals(form.getWorkStatus())) {
      // 获取被修改用户的token
      String token = redisUtils.get(RedisConstants.REDIS_KEY_USER_ID + userId);
      if (StringUtils.isNotBlank(token)) {
        // 移除缓存中被修改用户的信息
        redisUtils.delete(RedisConstants.REDIS_KEY_USER_TOKEN + token);
        redisUtils.delete(RedisConstants.REDIS_KEY_USER_ID + userId);
      }
    }
  }

  /**
   * 校验用户是否存在
   *
   * @param userId 用户ID
   */
  private SysUser checkUserExist(Integer userId) {
    SysUser sysUser = mapper.selectByPrimaryKey(userId);
    if (null == sysUser) {
      throw new ClientServiceException(
          "修改用户失败，用户ID为'" + userId + "'的用户不存在", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    return sysUser;
  }

  /**
   * 根据用户名查询用户信息
   *
   * @param username 用户名
   * @return SysUser
   */
  public SysUser getUserByUsername(String username) {
    SysUser user = mapper.selectSysUserByUsername(username);
    return user;
  }

  /**
   * 查询员工信息
   *
   * @param form 用户信息封装
   * @return SysUser
   */
  public PageInfo<SysEmployeeVO> getEmployeeByCondition(SysEmployeeQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<SysEmployeeVO> sysEmployeeVOs = mapper.selectSysEmployeeByCondition(form);
    return new PageInfo<>(sysEmployeeVOs);
  }

  /**
   * 根据用户名查询用户信息
   *
   * @param username 用户名
   * @return
   */
  public UserInfo findUserInfoByUserName(String username) {
    return mapper.selectUserInfoByUserName(username);
  }

  /**
   * 根据用户ID查询用户（员工信息）
   *
   * @param userId 用户ID
   * @return
   */
  public SysUserEmployeeInfo findUserInfoByUserId(Integer userId) {
    SysUserEmployeeInfo info = mapper.selectSysUserEmployeeInfoByUserId(userId);
    if (null != info) {
      // 查询员工学历
      DictionaryItem item = dictionaryItemBiz.selectById(Integer.parseInt(info.getEducation()));
      info.setEducation(item.getName());
    }
    return info;
  }
}
