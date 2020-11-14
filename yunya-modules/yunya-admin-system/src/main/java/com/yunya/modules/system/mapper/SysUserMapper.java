package com.yunya.modules.system.mapper;

import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.system.vo.UserInfo;
import com.yunya.models.system.SysUser;
import com.yunya.modules.system.domain.query.SysUserInfoDetailQueryFrom;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SysUserMapper extends Mapper<SysUser> {

  /**
   * 根据条件查询员工详细信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<SysUserInfoDetail> selectSysUserInfoDetailList(
      @Param("queryForm") SysUserInfoDetailQueryFrom queryForm);

  /**
   * 根据条件查询员工组织信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<SysUserInfoDetail> selectSysEmployeeWithOrgList(
      @Param("queryForm") SysUserInfoDetailQueryFrom queryForm);

  /**
   * 校验用户名是否存在
   *
   * @param name 用户名
   * @return int
   */
  Integer checkUserNameUnique(@Param("name") String name);

  /**
   * 校验身份证号是否存在
   *
   * @param identity 身份证号
   * @return int
   */
  Integer checkIdentityUnique(@Param("identity") String identity);

  /**
   * 校验手机号是否存在
   *
   * @param mobile 手机号
   * @return int
   */
  Integer checkMobileUnique(@Param("mobile") String mobile);

  /**
   * 新增用户
   *
   * @param sysUser 用户信息
   * @return int
   */
  int insertUser(SysUser sysUser);

  /**
   * 根据用户名查询用户
   *
   * @param username 用户名
   * @return SysUser
   */
  SysUser selectSysUserByUsername(@Param("username") String username);

  /**
   * 根据用户名查询用户信息
   *
   * @param username 用户名
   * @return
   */
  UserInfo selectUserInfoByUserName(@Param("username") String username);

  /**
   * 根据用户ID查询用户（员工）信息
   *
   * @param userId 用户ID
   * @return
   */
  SysUserInfoDetail selectSysUserEmployeeInfoByUserId(@Param("userId") Integer userId);

  /**
   * 根据用户ID删除员工信息
   *
   * @param id 用户ID
   */
  void deleteEmployeeByUserId(@Param("id") Integer id);
}
