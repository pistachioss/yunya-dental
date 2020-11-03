package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.SysElement;
import com.yunya.modules.system.biz.SysElementBiz;
import com.yunya.modules.system.domain.form.UserResourceForm;
import com.yunya.modules.system.domain.query.SysElementQueryForm;
import com.yunya.modules.system.vo.SysElementVO;
import com.yunya.modules.system.vo.tree.SysElementTreeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简单介绍:</br> 系统菜单按钮控制器
 *
 * @author: chow
 * @date: 2020/6/24 13:52
 * @description:
 * @since: 1.0.0
 */
@Api(value = "系统页面功能", description = "系统页面按钮（功能）管理")
@RestController
@RequestMapping("element")
public class SysElementController {

  /** 注入对象 */
  private final SysElementBiz sysElementBiz;

  public SysElementController(SysElementBiz sysElementBiz) {
    this.sysElementBiz = sysElementBiz;
  }

  /**
   * 获取用户功能权限列表
   *
   * @param resourceForm 参数封装
   * @return map
   */
  @ApiOperation("获取用户功能权限列表")
  @PostMapping("/user/list")
  public ResponseResult<List<SysElementVO>> findElementResourceList(
      @RequestBody @Validated UserResourceForm resourceForm) {
    List<SysElementVO> resultList = sysElementBiz.findElementResourceList(resourceForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增系统功能按钮
   *
   * @param resource 参数封装
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("新增（修改）系统功能按钮")
  @PostMapping("/add")
  public ResponseResult<T> add(@RequestBody @Validated SysElement resource) {
    sysElementBiz.add(resource);
    return ResponseUtil.success(null);
  }

  /**
   * 修改系统菜单按钮
   *
   * @param id 系统功能ID
   * @param resource 参数封装
   * @return
   */
  @ApiOperation("修改系统菜单按钮")
  @PutMapping("/edit/{id}")
  public ResponseResult<T> edit(
      @PathVariable(value = "id") String id, @RequestBody @Validated SysElement resource) {
    sysElementBiz.edit(id, resource);
    return ResponseUtil.success(null);
  }

  /**
   * 删除系统功能按钮
   *
   * @param id 功能ID
   * @return
   */
  @ApiOperation("删除系统功能按钮")
  @DeleteMapping("/delete/{id}")
  public ResponseResult<T> delete(@PathVariable(value = "id") String id) {
    sysElementBiz.deleteSysElement(id);
    return ResponseUtil.success(null);
  }

  /**
   * 根据菜单ID查询
   *
   * @param queryForm 查询参数封装
   * @return
   */
  @ApiOperation("根据菜单ID查询菜单按钮列表（可分页）")
  @PostMapping("/list")
  public ResponseResult<PageInfo<SysElementVO>> findListByMenuId(
      @RequestBody @Validated SysElementQueryForm queryForm) {
    PageInfo<SysElementVO> resultList = sysElementBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 获取按钮树列
   *
   * @return
   */
  @ApiOperation("获取按钮树列表")
  @GetMapping("/tree")
  public ResponseResult<List<SysElementTreeVO>> getTree() {
    List<SysElementTreeVO> treeList = sysElementBiz.getElementTreeByExample();
    return ResponseUtil.success(treeList);
  }
}
