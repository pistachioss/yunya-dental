package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.CompanyDepartment;
import com.yunya.modules.system.biz.CompanyDepartmentBiz;
import com.yunya.modules.system.form.CompanyDepartmentForm;
import com.yunya.modules.system.form.query.OrgDeptQueryForm;
import com.yunya.modules.system.vo.OrgDeptTreeVO;
import com.yunya.modules.system.vo.OrgDeptVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
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
  @GetMapping("/dept/one{id}")
  public ResponseResult findById(@PathVariable Integer id) {
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
  public ResponseResult findTree(@PathVariable Integer companyId) {
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
  public ResponseResult findList(@RequestBody @Validated OrgDeptQueryForm queryForm) {
    PageInfo<OrgDeptVO> resultList = organizationDepartmentBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增组织部门
   *
   * @param resource 参数封装
   * @return map
   */
  @ApiOperation("新增组织部门")
  @PostMapping("/dept/add")
  public ResponseResult add(@RequestBody @Validated CompanyDepartment resource) {
    organizationDepartmentBiz.addCompanyDepartment(resource);
    return ResponseUtil.success();
  }

  /**
   * 修改组织部门
   *
   * @param id 组织部门ID
   * @param form 参数封装
   * @return map
   */
  @ApiOperation("编辑组织部门")
  @ApiImplicitParam(
      name = "id",
      value = "组织部门ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @PutMapping("/dept/edit/{id}")
  public ResponseResult edit(
      @PathVariable Integer id, @RequestBody @Validated CompanyDepartmentForm form) {
    organizationDepartmentBiz.edit(id, form);
    return ResponseUtil.success();
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
  public ResponseResult delete(@PathVariable Integer id) {
    organizationDepartmentBiz.deleteOrganizationDepartment(id);
    return ResponseUtil.success();
  }
}
