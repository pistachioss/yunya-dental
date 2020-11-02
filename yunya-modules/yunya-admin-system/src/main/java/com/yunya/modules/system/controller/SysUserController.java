package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.SysUser;
import com.yunya.modules.system.biz.SysUserBiz;
import com.yunya.modules.system.domain.form.ModificationPasswordForm;
import com.yunya.modules.system.domain.form.SysUserForm;
import com.yunya.modules.system.domain.query.SysUserInfoDetailQueryFrom;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 简单介绍:</br> 系统用户控制层
 *
 * @author: chow
 * @date: 2020/6/6 16:44
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("user")
@Api(value = "用户管理", description = "用户管理（用户信息增删改查）")
public class SysUserController {

  /** 注入对象 */
  private final SysUserBiz sysUserBiz;

  public SysUserController(SysUserBiz sysUserBiz) {
    this.sysUserBiz = sysUserBiz;
  }

  /**
   * 根据用户名查询用户信息
   *
   * @param username 用户名
   * @return
   */
  @ApiOperation("根据用户名(手机号)查询用户信息")
  @GetMapping("/{username}")
  public ResponseResult<SysUser> findByUsername(@PathVariable String username) {
    SysUser user = sysUserBiz.getUserByUsername(username);
    return ResponseUtil.success(user);
  }

  /**
   * 根据用户ID获取用户信息（包含员工信息）
   *
   * @param id 用户ID
   * @return obj
   */
  @ApiOperation("根据用户ID获取用户信息（包含员工信息）")
  @GetMapping("/one/{id}")
  public ResponseResult<SysUserInfoDetail> findById(@PathVariable Integer id) {
    SysUserInfoDetail info = sysUserBiz.findUserInfoByUserId(id);
    return ResponseUtil.success(info);
  }

  /**
   * 根据条件查询用户(员工)详情信息列表（可分页）
   *
   * @param queryFrom 参数封装
   * @return list
   */
  @ApiOperation("根据条件查询用户(员工)详情信息列表（可分页）")
  @PostMapping("/list")
  public ResponseResult<PageInfo<SysUserInfoDetail>> findList(
      @RequestBody SysUserInfoDetailQueryFrom queryFrom) {
    PageInfo<SysUserInfoDetail> resultList = sysUserBiz.findUserDetailInfoList(queryFrom);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增用户
   *
   * @param resource 参数封装
   * @return void
   */
  @CurrentUser
  @ApiOperation("新增用户")
  @PostMapping("/add")
  public ResponseResult<T> add(@RequestBody @Validated SysUserForm resource) {
    sysUserBiz.add(resource);
    return ResponseUtil.success(null);
  }

  /**
   * 用户修改
   *
   * @param userId 用户ID
   * @param form 参数封装
   * @return map
   */
  @CurrentUser
  @ApiOperation("用户修改")
  @PutMapping("/edit/{userId}")
  public ResponseResult<T> edit(
      @PathVariable Integer userId, @RequestBody @Validated SysUserForm form) {
    sysUserBiz.edit(userId, form);
    return ResponseUtil.success(null);
  }

  /**
   * 用户删除
   *
   * @param id 用户ID
   * @return map
   */
  @ApiOperation("根据用户ID删除用户")
  @DeleteMapping("/delete/{id}")
  public ResponseResult<T> delete(@PathVariable Integer id) {
    sysUserBiz.deleteUserAndEmployeeByUserId(id);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工信息列表并导出
   *
   * @param response 响应
   * @param queryFrom 查询条件
   * @return
   */
  @ApiOperation("根据条件查询员工信息列表并导出列表")
  @PostMapping("/export")
  public ResponseResult<T> exportUserInfo(
      HttpServletResponse response, @RequestBody SysUserInfoDetailQueryFrom queryFrom)
      throws IOException {
    sysUserBiz.exportUserInfo(response, queryFrom);
    return ResponseUtil.success(null);
  }

  /**
   * 修改用户密码
   * @param form 修改用户密码表单
   * @return 返回状态
   */
  @ApiOperation("修改用户密码")
  @PostMapping("/modification/password")
  @CurrentUser
  public ResponseResult<T> modificationPassword(ModificationPasswordForm form) {
    return sysUserBiz.modificationPassword(form);
  }

  /**
   * 获取修改密码短信验证码
   * @return 返回短信验证码
   */
  @ApiOperation("获取修改密码短信验证码")
  @GetMapping("/authorization/code")
  @CurrentUser
  public ResponseResult<T> authorizationCode() {
    return sysUserBiz.authorizationCode();
  }

}
