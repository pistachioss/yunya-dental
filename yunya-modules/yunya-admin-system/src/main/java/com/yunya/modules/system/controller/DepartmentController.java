package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.Department;
import com.yunya.modules.system.biz.DepartmentBiz;
import com.yunya.modules.system.domain.base.BaseForm;
import com.yunya.modules.system.domain.model.DepartmentModel;
import com.yunya.modules.system.domain.query.DepartmentQueryForm;
import com.yunya.modules.system.vo.DepartmentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简单介绍:</br> 部门模板控制层
 *
 * @author: chow
 * @date: 2020/5/29 13:34
 * @description:
 * @since: 1.0.0
 */
@Api(value = "部门模版管理", description = "部门增删改查")
@RestController
@RequestMapping("department")
public class DepartmentController {

  private final DepartmentBiz departmentBiz;

  public DepartmentController(DepartmentBiz departmentBiz) {
    this.departmentBiz = departmentBiz;
  }

  /**
   * 根据ID查询部门信息
   *
   * @param id 部门ID
   * @return
   */
  @ApiOperation("根据ID查询部门模版")
  @GetMapping("/one/{id}")
  public ResponseResult<DepartmentVO> findById(@PathVariable(value = "id") Integer id) {
    DepartmentVO vo = new DepartmentVO();
    Department department = departmentBiz.selectById(id);
    BeanUtils.copyProperties(department, vo);
    return ResponseUtil.success(vo);
  }

  /**
   * 部门模版列表查询（可分页）
   *
   * @param queryForm 查询参数封装
   * @return list
   */
  @ApiOperation("根据条件查询部门模版列表（可分页）")
  @ApiImplicitParam(name = "form", value = "部门模版列表查询参数模型", dataType = "DepartmentQueryForm")
  @PostMapping("/list")
  public ResponseResult<PageInfo<DepartmentVO>> findDepartmentList(
      @RequestBody DepartmentQueryForm queryForm) {
    PageInfo<DepartmentVO> departments = departmentBiz.findAll(queryForm);
    return ResponseUtil.success(departments);
  }

  /**
   * 新增部门模版
   *
   * @param resource 参数封装
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("新增部门模版")
  @PostMapping("/add")
  public ResponseResult<T> addDepartment(@Validated @RequestBody DepartmentModel resource) {
    departmentBiz.add(resource);
    return ResponseUtil.success(null);
  }

  /**
   * 编辑部门
   *
   * @param id 部门ID
   * @param form 参数封装
   * @return
   */
  @CurrentUser
  @ApiOperation("编辑部门模版")
  @ApiImplicitParam(
      name = "id",
      value = "部门模版ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @PutMapping("/edit/{id}")
  public ResponseResult<T> editDepartment(
      @PathVariable(value = "id") Integer id, @Validated @RequestBody BaseForm form) {
    departmentBiz.modifyDepartment(id, form);
    return ResponseUtil.success(null);
  }

  /**
   * 删除部门
   *
   * @param id 根据ID删除部门
   * @return void
   */
  @ApiOperation("删除部门")
  @ApiImplicitParam(name = "id", value = "部门模版ID", dataType = "int", paramType = "path")
  @DeleteMapping("/delete/{id}")
  public ResponseResult<T> deleteDepartment(@PathVariable(value = "id") Integer id) {
    departmentBiz.deleteDepartment(id);
    return ResponseUtil.success(null);
  }
}
