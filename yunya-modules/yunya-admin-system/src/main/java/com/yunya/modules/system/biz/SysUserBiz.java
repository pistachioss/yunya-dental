package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.sms.RemoteSmsServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.system.vo.UserInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.SmsAutosendEventEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.system.SysUser;
import com.yunya.models.system.SysUserPost;
import com.yunya.modules.system.domain.form.ForgetPasswordForm;
import com.yunya.modules.system.domain.form.LoginOrganizationForm;
import com.yunya.modules.system.domain.form.ModificationPasswordForm;
import com.yunya.modules.system.domain.form.SysUserForm;
import com.yunya.modules.system.domain.query.SysUserInfoDetailQueryFrom;
import com.yunya.modules.system.mapper.SysEmployeeMapper;
import com.yunya.modules.system.mapper.SysUserMapper;
import com.yunya.modules.system.mapper.SysUserPostMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseEmployee;
import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseUserPost;
import static com.yunya.framework.common.constant.BusinessConstants.USER_RESIGNATION_STATUS;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.REDIS_KEY_USER_ID;
import static com.yunya.framework.common.constant.RedisConstants.REDIS_KEY_USER_TOKEN;
import static com.yunya.framework.common.constant.UserConstant.DEFAULT_USER_PASSWORD;
import static com.yunya.framework.common.constant.UserConstant.PW_ENCODER_SALT;

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

  /** 消息中间件调用 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
  /** 用户的员工信息 */
  @Autowired private SysEmployeeMapper sysEmployeeMapper;
  /** 用户可登陆组织 */
  @Autowired private SysUserPostMapper sysUserPostMapper;
  /** 缓存 */
  @Autowired private RedisUtils redisUtils;
  /** 短信服务调用 */
  @Autowired private RemoteSmsServiceFeign remoteSmsServiceFeign;

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
   * 根据条件查询员工组织信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<SysUserInfoDetail> findUserDetailWithOrgList(SysUserInfoDetailQueryFrom queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<SysUserInfoDetail> result = mapper.selectSysEmployeeWithOrgList(queryForm);
    return new PageInfo<>(result);
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
    sysUser.setPassword(new BCryptPasswordEncoder(PW_ENCODER_SALT).encode(DEFAULT_USER_PASSWORD));
    sysUser.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    sysUser.setCrtName(BaseContextHandler.getName());
    // 新增用户基础信息
    int result = mapper.insertUser(sysUser);
    if (result > 0) {
      Integer userId = sysUser.getId();
      SysEmployee sysEmployee = EntityUtils.build(resource, SysEmployee.class);
      sysEmployee.setUserId(userId);
      // 新增员工就职状态为离职处理
      if (USER_RESIGNATION_STATUS.equals(resource.getWorkStatus())) {
        String leaveTime = resource.getLeaveTime();
        if (StringHelper.isBlank(leaveTime)) {
          SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
          String systemTime = format.format(new Date(System.currentTimeMillis()));
          sysEmployee.setLeaveTime(systemTime);
        } else {
          sysEmployee.setLeaveTime(leaveTime);
        }

      }
      sysEmployee.setPinyin(HanyuPinyinHelper.getFirstLettersLo(resource.getName()));
      sysEmployee.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      sysEmployee.setCrtName(BaseContextHandler.getName());
      // 新增用户扩展信息（员工信息）
      sysEmployeeMapper.insertSelective(sysEmployee);
      List<LoginOrganizationForm> organizationForms = resource.getLoginOrganizationForms();
      if (StringHelper.isNotEmpty(organizationForms)) {
        // 新增用户与组织、部门、岗位的关系
        insertUserLoginOrganization(userId, organizationForms);
      }
      // 发送消息同步员工信息
//      rabbitMqServiceFeign.sendMessage(userId, 0, BaseEmployee);
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
      int i = sysUserPostMapper.insertSelective(entity);
      if (i > 0) {
        // 发送消息同步员工信息
        rabbitMqServiceFeign.sendMessage(entity.getId(), 0, BaseUserPost);
      }
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
      throw new ClientServiceException("员工手机号'" + mobile + "'已存在", SAME_DATA_EXIST);
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
      throw new ClientServiceException("员工身份证号'" + identity + "'已存在", SAME_DATA_EXIST);
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
      throw new ClientServiceException("员工姓名'" + name + "'已存在", NAME_IS_OCCUPIED);
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
      throw new ClientServiceException("系统管理员账号不允许修改用户名", OBJECT_EDIT_FAIL);
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
      // 发送消息同步员工信息
//      rabbitMqServiceFeign.sendMessage(userId, 1, BaseEmployee);
    }
    // 用户名被修改或就职状态改为离职,将当前用户从缓存中移除
    if (!currentUsername.equals(form.getMobilePhone())
        || USER_RESIGNATION_STATUS.equals(form.getWorkStatus())) {
      // 获取被修改用户的token
      String token = redisUtils.get(REDIS_KEY_USER_ID + userId);
      if (StringUtils.isNotBlank(token)) {
        // 移除缓存中被修改用户的信息
        redisUtils.delete(REDIS_KEY_USER_TOKEN + token);
        redisUtils.delete(REDIS_KEY_USER_ID + userId);
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
      throw new ClientServiceException("修改用户失败，用户ID为'" + userId + "'的用户不存在", QUERY_RESULT_INVALID);
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
   * 根据用户ID集合查询用户信息
   *
   * @param userIds 用户ID集合
   * @return 用户信息集合
   */
  public List<SysUserInfoDetail> findUserInfoByUserIds(List<Integer> userIds) {
    return mapper.selectSysUserEmployeeInfoByUserIds(userIds);
  }


  /**
   * 根据用户ID删除用户及关联员工信息
   *
   * @param id 用户ID
   */
  public void deleteUserAndEmployeeByUserId(Integer id) {
    if (id == 1) {
      throw new ClientServiceException("管理员账号，不允许删除！", DELETE_NOT_ALLOW);
    }
    SysUser sysUser = mapper.selectByPrimaryKey(id);
    if (null == sysUser) {
      throw new ClientServiceException("用户删除失败，ID为'" + id + "'的用户不存在", QUERY_RESULT_INVALID);
    }
    SysUserPost userPost = new SysUserPost();
    userPost.setUserId(id);
    List<SysUserPost> userPosts = sysUserPostMapper.select(userPost);
    if (StringHelper.isNotEmpty(userPosts)) {
      throw new ClientServiceException(
          "姓名为'" + sysUser.getName() + "'已产生其他关联数据，不允许删除！", DELETE_NOT_ALLOW);
    }
    int i = mapper.deleteByPrimaryKey(id);
    mapper.deleteEmployeeByUserId(id);
    if (i > 0) {
      // 发送消息同步员工信息
      rabbitMqServiceFeign.sendMessage(id, 2, BaseEmployee);
    }
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

  /**
   * 修改用户密码
   * @param form 密码表单
   * @return
   */
  public ResponseResult modificationPassword(ModificationPasswordForm form) {
    String oldPwd = form.getOldPwd();
    String newPwd = form.getNewPwd();
    String confirmPwd = form.getConfirmPwd();
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    // 通过userID查询用户信息
    SysUser sysUser = mapper.selectByPrimaryKey(userId);
    String originPwd = sysUser.getPassword();

    // 校验旧密码是否正确
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    if (!encoder.matches(oldPwd,originPwd)) {
      return ResponseUtil.fail(PASSWORD_ERROR,"旧密码输入错误",null);
    }
    // 校验新密码和确认密码是否正确
    if (!newPwd.equals(confirmPwd)) {
      return ResponseUtil.fail(PASSWORD_ERROR,"新密码和确认密码不一致",null);
    }
    // 密码加密，加盐，设置默认密码
    sysUser.setPassword(new BCryptPasswordEncoder(PW_ENCODER_SALT).encode(newPwd));
    mapper.updateByPrimaryKey(sysUser);
    return ResponseUtil.success();
  }

  /**
   * 忘记密码
   * @param form 忘记密码表单
   * @return 返回状态
   */
  public ResponseResult forgetPassword(ForgetPasswordForm form) {
    String newPwd = form.getNewPwd();
    String confirmPwd = form.getConfirmPwd();
    String authCode = form.getAuthCode();
    String userid = BaseContextHandler.getUserID();
    String mobile = form.getMobile();

    // 验证新密码和确认密码是否相同
    if (!newPwd.equals(confirmPwd)) {
      return ResponseUtil.fail(PARAMETERS_IS_ILLEGAL,"新密码和确认密码不一致",null);
    }
    // 短信验证码是否正确
    String key = RedisConstants.FORGET_PWD_AUTHORIZATION + mobile;
    if (!redisUtils.hasKey(key)) {
      return ResponseUtil.fail(DATA_NOT_EXIST,"验证码过期,请重新发送",null);
    }
    String authCordCache = redisUtils.get(key);
    if (!authCordCache.equals(authCode)) {
      return ResponseUtil.fail(PARAMETERS_IS_ILLEGAL,"验证码无效,请重新发送",null);
    }
    SysUser sysUser = mapper.selectByPrimaryKey(Integer.valueOf(userid));
    if (null != sysUser) {
      // 密码加密，加盐，设置默认密码
      sysUser.setPassword(new BCryptPasswordEncoder(PW_ENCODER_SALT).encode(newPwd));
      mapper.updateByPrimaryKey(sysUser);
    }
    return ResponseUtil.success();
  }


  /**
   * @param mobile 手机号
   * 获取修改密码短信验证码
   */
  public ResponseResult authorizationCode(String mobile) {
    String  messageCode = this.messageCodeGenerator();
    // 发送短信验证码
    String key = RedisConstants.FORGET_PWD_AUTHORIZATION + mobile;
    if (redisUtils.hasKey(key)) {
      return ResponseUtil.fail(OBJECT_EDIT_FAIL,"消息已发送, 请稍后再试",null);
    }
    redisUtils.set(key, messageCode,60);
    ResponseResult responseResult = remoteSmsServiceFeign.sendVerifyCode(mobile, messageCode, SmsAutosendEventEnum.FORGET_PASSWORD.getCode());
    if (responseResult==null) {
      return ResponseUtil.fail(OPERATION_FAIL,"短信验证码发送失败",null);
    }
    if (responseResult.getStatus() != 0) {
      return ResponseUtil.fail(OPERATION_FAIL, responseResult.getMsg(),null);
    }
    return ResponseUtil.success("短信验证码已发送");
  }

  /**
   * 随机生成六位数，并且每位数都不重复
   * @return 返回短信验证码
   */
  private String messageCodeGenerator() {
    int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    Random rand = new Random();
    for (int i = 10; i > 1; i--) {
      int index = rand.nextInt(i);
      int tmp = array[index];
      array[index] = array[i - 1];
      array[i - 1] = tmp;
    }
    int result = 0;
    for (int i = 0; i < 6; i++) {
      result = result * 10 + array[i];
    }
    if (String.valueOf(result).length() == 6) {
      return String.valueOf(result);
    } else {
      return String.valueOf(messageCodeGenerator());
    }
  }

}
