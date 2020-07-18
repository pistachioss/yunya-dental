package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.SysUserBiz;
import com.yunya.modules.system.form.SysUserForm;
import com.yunya.modules.system.form.query.SysEmployeeQueryForm;
import com.yunya.modules.system.form.query.SysUserInfoDetailQueryFrom;
import com.yunya.modules.system.vo.SysEmployeeVO;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
   * 根据用户ID获取用户信息（包含员工信息）
   *
   * @param id 用户ID
   * @return obj
   */
  @GetMapping("/one/{id}")
  public ResponseResult findById(@PathVariable Integer id) {
    SysUserInfoDetail info = sysUserBiz.findUserInfoByUserId(id);
    return ResponseUtil.success(info);
  }

  /**
   * 根据条件查询用户(员工)详情信息列表（可分页）
   *
   * @param queryFrom 参数封装
   * @return list
   */
  @PostMapping("/list")
  public ResponseResult findList(@RequestBody SysUserInfoDetailQueryFrom queryFrom) {
    PageInfo<SysUserInfoDetail> resultList = sysUserBiz.findUserDetailInfoList(queryFrom);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增用户
   *
   * @param resource 参数封装
   * @return void
   */
  @PostMapping("/add")
  public ResponseResult add(@RequestBody @Validated SysUserForm resource) {
    sysUserBiz.add(resource);
    return ResponseUtil.success();
  }

  /**
   * 用户修改
   *
   * @param userId 用户ID
   * @param form 参数封装
   * @return map
   */
  @PutMapping("/edit/{userId}")
  public ResponseResult edit(
      @PathVariable Integer userId, @RequestBody @Validated SysUserForm form) {
    sysUserBiz.edit(userId, form);
    return ResponseUtil.success();
  }

  /**
   * 根据条件查询员工信息
   *
   * @param queryForm 参数封装
   * @return
   */
  @PostMapping("/employee")
  public ResponseResult findEmployee(@RequestBody @Validated SysEmployeeQueryForm queryForm) {
    PageInfo<SysEmployeeVO> sysEmployeeVO = sysUserBiz.getEmployeeByCondition(queryForm);
    return ResponseUtil.success(sysEmployeeVO);
  }

  /**
   * 用户删除
   *
   * @param id 用户ID
   * @return map
   */
  public ResponseResult delete(Integer id) {
    return ResponseUtil.success();
  }
}
