package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.form.EmployeeInfoQueryForm;
import com.yunya.feign.system.vo.EmployeeInfoVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.SysUserPostBiz;
import com.yunya.modules.system.domain.form.LoginOrganizationForm;
import com.yunya.modules.system.domain.model.SysUserPostModel;
import com.yunya.modules.system.vo.PostVO;
import com.yunya.modules.system.vo.SysUserLoginOrgVO;
import com.yunya.modules.system.vo.SysUserPostOrgVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简单介绍:</br> 用户可登陆组织控制器
 *
 * @author: chow
 * @date: 2020/6/18 11:21
 * @description:
 * @since: 1.0.0
 */
@Api(value = "用户可登录组织", description = "用户可登录组织管理")
@RestController
@RequestMapping("user_post")
public class SysUserPostController {

  /** 注入对象 */
  private final SysUserPostBiz sysUserPostBiz;

  public SysUserPostController(SysUserPostBiz sysUserPostBiz) {
    this.sysUserPostBiz = sysUserPostBiz;
  }

  /**
   * 根据用户ID获取用户可登陆组织列表
   *
   * @param userId 用户ID
   * @return map
   */
  @ApiOperation("获取用户可登陆组织列表(用户登陆)")
  @GetMapping("/list/{userId}")
  public ResponseResult<List<SysUserLoginOrgVO>> getUserLoginList(@PathVariable Integer userId) {
    List<SysUserLoginOrgVO> loginList = sysUserPostBiz.getUserLoginListByUserId(userId);
    return ResponseUtil.success(loginList);
  }

  /**
   * 根据用户ID获取用户可登陆组织列表
   *
   * @param userId 用户ID
   * @return map
   */
  @ApiOperation("获取用户可登陆组织列表(员工信息管理用)")
  @GetMapping("/userPostlist/{userId}")
  public ResponseResult<List<SysUserPostOrgVO>> getUserPostList(@PathVariable Integer userId) {
    List<SysUserPostOrgVO> loginList = sysUserPostBiz.getUserPostListByUserId(userId);
    return ResponseUtil.success(loginList);
  }

  /**
   * 根据条件查询员工信息
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询用户信息")
  @PostMapping(value = "/employee/list", name = "根据条件查询用户信息")
  public ResponseResult<PageInfo<EmployeeInfoVO>> findEmployeeByExample(
      @RequestBody EmployeeInfoQueryForm queryForm) {
    PageInfo<EmployeeInfoVO> resultList = sysUserPostBiz.findEmployeeList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据用户ID、组织ID查询用户岗位列表
   *
   * @param userId 用户ID
   * @param orgId 组织ID
   * @return
   */
  @ApiOperation("根据用户ID、组织ID查询用户岗位列表")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "orgId", value = "组织ID", required = true),
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true)
  })
  @GetMapping(value = "/post/list/{orgId}/{userId}", name = "根据用户ID、组织ID查询用户岗位列表")
  public ResponseResult<List<PostVO>> postInfoList(
      @PathVariable(value = "orgId") Integer orgId,
      @PathVariable(value = "userId") Integer userId) {
    List<PostVO> resultList = sysUserPostBiz.findUserPostList(orgId, userId);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增用户可登录组织信息
   *
   * @param resource 参数封装
   * @return map
   */
  @CurrentUser
  @ApiOperation("新增用户可登陆组织")
  @PostMapping("/add")
  public ResponseResult<T> add(@RequestBody @Validated SysUserPostModel resource) {
    sysUserPostBiz.add(resource);
    return ResponseUtil.success(null);
  }

  /**
   * 修改用户可登陆组织
   *
   * @param userPostId 可登录组织ID
   * @param form 参数封装
   * @return map
   */
  @CurrentUser
  @ApiOperation("修改用户可登陆组织")
  @ApiImplicitParam(
      name = "userPostId",
      value = "用户可登陆组织ID",
      dataType = "number",
      paramType = "path")
  @PutMapping("/edit/{userPostId}")
  public ResponseResult<T> edit(
      @PathVariable Integer userPostId, @RequestBody @Validated LoginOrganizationForm form) {
    sysUserPostBiz.edit(userPostId, form);
    return ResponseUtil.success(null);
  }

  /**
   * 根据ID删除用户可登陆组织
   *
   * @param userPostId 可登陆组织ID
   * @return map
   */
  @ApiOperation("根据ID删除用户可登陆组织")
  @DeleteMapping("/delete/{userPostId}")
  public ResponseResult<T> remove(@PathVariable Integer userPostId) {
    sysUserPostBiz.remove(userPostId);
    return ResponseUtil.success(null);
  }
}
