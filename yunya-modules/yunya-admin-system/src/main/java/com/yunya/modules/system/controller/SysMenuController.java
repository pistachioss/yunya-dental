package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.SysMenu;
import com.yunya.modules.system.biz.SysMenuBiz;
import com.yunya.modules.system.domain.form.MenuElementForm;
import com.yunya.modules.system.domain.form.UserResourceForm;
import com.yunya.modules.system.domain.query.SysMenuQueryForm;
import com.yunya.modules.system.vo.SysMenuVO;
import com.yunya.modules.system.vo.tree.SysMenuElementTreeVO;
import com.yunya.modules.system.vo.tree.SysMenuTreeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简单介绍:</br> 系统菜单控制器
 *
 * @author: chow
 * @date: 2020/6/24 09:11
 * @description:
 * @since: 1.0.0
 */
@Api(value = "系统菜单（目录）", description = "系统菜单（目录）管理")
@RestController
@RequestMapping("menu")
public class SysMenuController {

  /** 注入对象 */
  private final SysMenuBiz sysMenuBiz;

  public SysMenuController(SysMenuBiz sysMenuBiz) {
    this.sysMenuBiz = sysMenuBiz;
  }

  /**
   * 根据用户ID、登陆组织ID查询用户菜单权限列表
   *
   * @param resourceForm 参数封装
   * @return map
   */
  @ApiOperation("查询用户菜单权限树(选择用户可登陆组织)")
  @PostMapping("/user/list")
  public ResponseResult<List<SysMenu>> getUserMenuResourceList(
      @RequestBody @Validated UserResourceForm resourceForm) {
    List<SysMenu> resultList = sysMenuBiz.getUserMenuResourceList(resourceForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件查询菜单功能树列表
   *
   * @param queryForm 参数封装
   * @return
   */
  @ApiOperation("根据条件查询菜单及页面功能树列表")
  @PostMapping("/ele/tree")
  public ResponseResult<List<SysMenuElementTreeVO>> getMenuElementTree(
      @RequestBody MenuElementForm queryForm) {
    List<SysMenuElementTreeVO> resultList = sysMenuBiz.findMenuElementTree(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 获取菜单树列表
   *
   * @param title 菜单名称
   * @return
   */
  @ApiOperation("根据菜单名称获取菜单树列表")
  @GetMapping("/tree")
  public ResponseResult<List<SysMenuTreeVO>> getTree(String title) {
    List<SysMenuTreeVO> treeList = sysMenuBiz.getMenuTreeByExample(title);
    return ResponseUtil.success(treeList);
  }

  /**
   * 根据条件查询菜单树
   *
   * @param queryForm 参数封装
   * @return
   */
  @ApiOperation("根据条件查询菜单树")
  @PostMapping("/list")
  public ResponseResult<PageInfo<SysMenuVO>> findList(@RequestBody SysMenuQueryForm queryForm) {
    PageInfo<SysMenuVO> resultList = sysMenuBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增系统菜单
   *
   * @param resource 参数封装
   * @return map
   */
  @CurrentUser
  @RepeatSubmit
  @ApiOperation("新增（修改）系统菜单")
  @PostMapping("/add")
  public ResponseResult<T> add(@RequestBody @Validated SysMenu resource) {
    sysMenuBiz.addSysMenu(resource);
    return ResponseUtil.success(null);
  }

  /**
   * 编辑菜单
   *
   * @param id 菜单ID
   * @param resource 参数封装
   * @return
   */
  @ApiOperation("修改菜单")
  @PutMapping("/edit/{id}")
  public ResponseResult<T> edit(
      @PathVariable(value = "id") Integer id, @RequestBody @Validated SysMenu resource) {
    sysMenuBiz.edit(id, resource);
    return ResponseUtil.success(null);
  }

  /**
   * 删除菜单
   *
   * @param id 菜单ID
   * @return
   */
  @ApiOperation("删除菜单")
  @DeleteMapping("/delete/{id}")
  public ResponseResult<T> delete(@PathVariable(value = "id") Integer id) {
    sysMenuBiz.deleteSysMenu(id);
    return ResponseUtil.success(null);
  }
}
