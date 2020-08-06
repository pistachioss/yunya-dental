package com.yunya.modules.system.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.SysUserPost;
import com.yunya.modules.system.biz.SysUserPostBiz;
import com.yunya.modules.system.domain.form.LoginOrganizationForm;
import com.yunya.modules.system.domain.model.SysUserPostModel;
import com.yunya.modules.system.vo.SysUserLoginOrgVO;
import com.yunya.modules.system.vo.SysUserPostOrgVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
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
  public ResponseResult getUserLoginList(@PathVariable Integer userId) {
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
  public ResponseResult getUserPostList(@PathVariable Integer userId) {
    List<SysUserPostOrgVO> loginList = sysUserPostBiz.getUserPostListByUserId(userId);
    return ResponseUtil.success(loginList);
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
  public ResponseResult add(@RequestBody @Validated SysUserPostModel resource) {
    sysUserPostBiz.add(resource);
    return ResponseUtil.success();
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
  public ResponseResult edit(
      @PathVariable Integer userPostId, @RequestBody @Validated LoginOrganizationForm form) {
    sysUserPostBiz.edit(userPostId, form);
    return ResponseUtil.success();
  }

  /**
   * 根据ID删除用户可登陆组织
   *
   * @param userPostId 可登陆组织ID
   * @return map
   */
  @ApiOperation("根据ID删除用户可登陆组织")
  @DeleteMapping("/delete/{userPostId}")
  public ResponseResult remove(@PathVariable Integer userPostId) {
    sysUserPostBiz.remove(userPostId);
    return ResponseUtil.success();
  }
}
