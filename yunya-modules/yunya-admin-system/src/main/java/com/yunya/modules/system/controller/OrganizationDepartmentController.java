package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.CompanyDepartment;
import com.yunya.modules.system.biz.CompanyDepartmentBiz;
import com.yunya.modules.system.domain.form.CompanyDepartmentForm;
import com.yunya.modules.system.domain.model.CompanyDepartmentModel;
import com.yunya.modules.system.domain.query.OrgDeptQueryForm;
import com.yunya.modules.system.vo.OrgDeptTreeVO;
import com.yunya.modules.system.vo.OrgDeptVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简单介绍:</br> 组织部门管理控制层
 *
 * @author: chow
 * @date: 2020/6/2 19:39
 * @description:
 * @since: 1.0.0
 */
@Api(value = "组织部门管理", description = "组织部门增删改查")
@RestController
@RequestMapping("organization")
public class OrganizationDepartmentController {

  /** 注入对象 */
  private final CompanyDepartmentBiz organizationDepartmentBiz;

  public OrganizationDepartmentController(CompanyDepartmentBiz organizationDepartmentBiz) {
    this.organizationDepartmentBiz = organizationDepartmentBiz;
  }

  /**
   * 根据ID查询组织部门信息
   *
   * @param id 组织的部门ID
   * @return obj
   */
  @ApiOperation("根据ID查询组织部门信息")
  @GetMapping("/dept/one/{id}")
  public ResponseResult<CompanyDepartment> findById(@PathVariable(value = "id") Integer id) {
    CompanyDepartment companyDepartment = organizationDepartmentBiz.selectById(id);
    return ResponseUtil.success(companyDepartment);
  }

  /**
   * 根据组织ID获取该组织部门树
   *
   * @param companyId 组织ID
   * @return list
   */
  @ApiOperation("获取组织的部门树列表")
  @ApiImplicitParam(
      name = "companyId",
      required = true,
      dataType = "int",
      value = "组织ID",
      paramType = "path")
  @GetMapping("/dept/tree/{companyId}")
  public ResponseResult<List<OrgDeptTreeVO>> findTree(
      @PathVariable(value = "companyId") Integer companyId) {
    List<OrgDeptTreeVO> treeList = organizationDepartmentBiz.findDeptTree(companyId);
    return ResponseUtil.success(treeList);
  }

  /**
   * 根据条件查询组织的部门列表
   *
   * @param queryForm 参数封装
   * @return list
   */
  @ApiOperation("根据条件查询组织部门列表（可分页）")
  @PostMapping("/dept/list")
  public ResponseResult<PageInfo<OrgDeptVO>> findList(
      @RequestBody @Validated OrgDeptQueryForm queryForm) {
    PageInfo<OrgDeptVO> resultList = organizationDepartmentBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增组织部门
   *
   * @param resource 参数封装
   * @return map
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("新增组织部门")
  @PostMapping("/dept/add")
  public ResponseResult<T> add(@RequestBody @Validated CompanyDepartmentModel resource) {
    organizationDepartmentBiz.addCompanyDepartment(resource);
    return ResponseUtil.success(null);
  }

  /**
   * 修改组织部门
   *
   * @param id 组织部门ID
   * @param form 参数封装
   * @return map
   */
  @CurrentUser
  @ApiOperation("编辑组织部门")
  @ApiImplicitParam(
      name = "id",
      value = "组织部门ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @PutMapping("/dept/edit/{id}")
  public ResponseResult<T> edit(
      @PathVariable(value = "id") Integer id, @RequestBody @Validated CompanyDepartmentForm form) {
    organizationDepartmentBiz.edit(id, form);
    return ResponseUtil.success(null);
  }

  /**
   * 根据ID删除组织部门
   *
   * @param id 组织部门ID
   * @return void
   */
  @ApiOperation("删除组织部门")
  @ApiImplicitParam(
      name = "id",
      value = "组织部门ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @DeleteMapping("/dept/delete/{id}")
  public ResponseResult<T> delete(@PathVariable(value = "id") Integer id) {
    organizationDepartmentBiz.deleteOrganizationDepartment(id);
    return ResponseUtil.success(null);
  }
}
