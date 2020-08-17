package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.system.vo.UserInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.constant.UserConstant;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.system.Post;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.system.SysUser;
import com.yunya.models.system.SysUserPost;
import com.yunya.modules.system.domain.form.LoginOrganizationForm;
import com.yunya.modules.system.domain.form.SysUserForm;
import com.yunya.modules.system.domain.query.SysUserInfoDetailQueryFrom;
import com.yunya.modules.system.mapper.SysEmployeeMapper;
import com.yunya.modules.system.mapper.SysUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
  /** 缓存 */
  @Autowired private RedisUtils redisUtils;

  /**
   * 根据条件查询用户信息详情列表
   *
   * @param queryForm 查询条件
   * @return list
   */
  public PageInfo<SysUserInfoDetail> findUserDetailInfoList(SysUserInfoDetailQueryFrom queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<SysUserInfoDetail> resultList = mapper.selectSysUserInfoDetailList(queryForm);
    return new PageInfo<>(resultList);
  }

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
    // 密码加密，加盐，设置默认密码
    sysUser.setPassword(
        new BCryptPasswordEncoder(UserConstant.PW_ENCODER_SALT)
            .encode(UserConstant.DEFAULT_USER_PASSWORD));
    sysUser.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    sysUser.setCrtName(BaseContextHandler.getName());
    // 新增用户基础信息
    int result = mapper.insertUser(sysUser);
    if (result > 0) {
      Integer userId = sysUser.getId();
      SysEmployee sysEmployee = EntityUtils.build(resource, SysEmployee.class);
      sysEmployee.setUserId(userId);
      // 新增员工就职状态为离职处理
      if (BusinessConstants.USER_RESIGNATION_STATUS.equals(resource.getWorkStatus())) {
        sysEmployee.setLeaveTime(
            null == resource.getLeaveTime()
                ? new Date(System.currentTimeMillis())
                : resource.getLeaveTime());
      }
      sysEmployee.setPinyin(HanyuPinyinHelper.getFirstLettersLo(resource.getName()));
      sysEmployee.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      sysEmployee.setCrtName(BaseContextHandler.getName());
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
      entity.setUserId(userId);
      entity.setCompanyId(form.getOrgId());
      entity.setDepartmentId(form.getOrgDeptId());
      entity.setPostId(form.getPostId());
      entity.setGroupId(form.getPostGroupId());
      entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      entity.setCrtName(BaseContextHandler.getName());
      sysUserPostBiz.insertSelective(entity);
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
          "员工手机号'" + mobile + "'已存在", OperationCodeConstants.SAME_DATA_EXIST);
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
          "员工身份证号'" + identity + "'已存在", OperationCodeConstants.SAME_DATA_EXIST);
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
    String currentUsername = sysUser.getUsername();
    // 不允许修改管理员登陆账号
    if (userId == 1 && !currentUsername.equals(form.getMobilePhone())) {
      throw new ClientServiceException("系统管理员账号不允许修改用户名", OperationCodeConstants.OBJECT_EDIT_FAIL);
    }
    SysUser sysUserEntity = EntityUtils.build(form, SysUser.class);
    // 更新用户信息
    sysUserEntity.setId(sysUser.getId());
    sysUserEntity.setUsername(form.getMobilePhone());
    sysUserEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    sysUserEntity.setUpdName(BaseContextHandler.getName());
    sysUserEntity.setUpdTime(new Date(System.currentTimeMillis()));
    int result = mapper.updateByPrimaryKeySelective(sysUserEntity);
    if (result > 0) {
      SysEmployee employeeResult = sysEmployeeMapper.selectByUserId(userId);
      SysEmployee sysEmployeeEntity = EntityUtils.build(form, SysEmployee.class);
      sysEmployeeEntity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(form.getName()));
      sysEmployeeEntity.setId(employeeResult.getId());
      sysEmployeeEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
      sysEmployeeEntity.setUpdName(BaseContextHandler.getName());
      sysEmployeeEntity.setUpdTime(new Date(System.currentTimeMillis()));
      sysEmployeeMapper.updateByPrimaryKeySelective(sysEmployeeEntity);
    }
    // 用户名被修改或就职状态改为离职,将当前用户从缓存中移除
    if (!currentUsername.equals(form.getMobilePhone())
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
  public SysUserInfoDetail findUserInfoByUserId(Integer userId) {
    SysUserInfoDetail info = mapper.selectSysUserEmployeeInfoByUserId(userId);
    return info;
  }

  /**
   * 根据用户ID删除用户及关联员工信息
   *
   * @param id 用户ID
   */
  public void deleteUserAndEmployeeByUserId(Integer id) {
    if (id == 1) {
      throw new ClientServiceException("管理员账号，不允许删除！", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    SysUser sysUser = mapper.selectByPrimaryKey(id);
    if (null == sysUser) {
      throw new ClientServiceException(
          "用户删除失败，ID为'" + id + "'的用户不存在", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    SysUserPost userPost = new SysUserPost();
    userPost.setUserId(id);
    List<SysUserPost> userPosts = sysUserPostBiz.selectList(userPost);
    if (userPosts.size() > 0) {
      throw new ClientServiceException(
          "姓名为'" + sysUser.getName() + "'已产生其他关联数据，不允许删除！",
          OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(id);
    mapper.deleteEmployeeByUserId(id);
  }

  /**
   * 根据条件导出员工信息列表
   *
   * @param response 响应
   * @param queryFrom 查询条件
   */
  public void exportUserInfo(HttpServletResponse response, SysUserInfoDetailQueryFrom queryFrom)
      throws IOException {
    List<SysUserInfoDetail> details = mapper.selectSysUserInfoDetailList(queryFrom);
    ExcelUtil<SysUserInfoDetail> excelUtil = new ExcelUtil<>(SysUserInfoDetail.class);
    excelUtil.exportExcel(response, details, "员工信息列表");
  }
}
