package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.QztOrgVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.OrganizationBiz;
import com.yunya.modules.system.domain.form.OrganizationForm;
import com.yunya.modules.system.domain.query.OrganizationQueryForm;
import com.yunya.modules.system.vo.OrganizationInfoVO;
import com.yunya.modules.system.vo.tree.OrganizationTreeVO;
import io.swagger.annotations.*;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简单介绍:</br> 组织架构控制层
 *
 * @author: chow
 * @date: 2020/5/30 16:36
 * @description:
 * @since: 1.0.0
 */
@Api(value = "组织管理", description = "组织增删改查")
@RestController
@RequestMapping("organization")
public class OrganizationController {

  /** 注入服务 */
  private final OrganizationBiz organizationBiz;

  public OrganizationController(OrganizationBiz organizationBiz) {
    this.organizationBiz = organizationBiz;
  }

  /**
   * 根据ID获取组织信息
   *
   * @param id 组织ID
   * @return
   */
  @ApiOperation("根据ID获取组织信息")
  @GetMapping("/one/{id}")
  public ResponseResult<OrganizationInfo> findById(@PathVariable(value = "id") Integer id) {
    OrganizationInfo vo = organizationBiz.findOrgInfoById(id);
    return ResponseUtil.success(vo);
  }

  /**
   * 获取组织树列表
   *
   * @return list
   */
  @ApiOperation("获取组织树列表")
  @GetMapping("/tree")
  public ResponseResult<List<OrganizationTreeVO>> initOrganizationTree() {
    List<OrganizationTreeVO> resultList = organizationBiz.initOrganizationTree();
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件查询组织列表
   *
   * @param queryForm 参数封装
   * @return list
   */
  @ApiOperation("根据条件查询组织列表（可分页）")
  @PostMapping("/list")
  public ResponseResult<PageInfo<OrganizationInfoVO>> findList(
      @RequestBody OrganizationQueryForm queryForm) {
    PageInfo<OrganizationInfoVO> resultList = organizationBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增组织
   *
   * @param resource 参数封装
   * @return map
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("新增组织")
  @PostMapping("/add")
  public ResponseResult<T> addOrganization(@Validated @RequestBody OrganizationForm resource) {
    organizationBiz.addOrganization(resource);
    return ResponseUtil.success(null);
  }

  /**
   * 编辑组织信息
   *
   * @param id 组织ID
   * @param form 参数封装
   * @return map
   */
  @CurrentUser
  @ApiOperation("编辑组织")
  @ApiImplicitParam(
      name = "id",
      value = "组织ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @PutMapping("/edit/{id}")
  public ResponseResult<T> editOrganization(
      @PathVariable(value = "id") Integer id, @Validated @RequestBody OrganizationForm form) {
    organizationBiz.editOrganization(id, form);
    return ResponseUtil.success(null);
  }

  /**
   * 删除组织
   *
   * @param id 组织ID
   * @return
   */
  @ApiOperation("根据组织ID删除组织")
  @ApiImplicitParam(
      name = "id",
      value = "组织ID",
      dataType = "int",
      required = true,
      paramType = "path")
  @DeleteMapping("/delete/{id}")
  public ResponseResult<T> deleteOrganization(@PathVariable(value = "id") Integer id) {
    organizationBiz.deleteOrganization(id);
    return ResponseUtil.success(null);
  }

  @ApiOperation("查询已开启全诊通认证门诊列表")
  @GetMapping("/qzt/list")
  public ResponseResult<List<QztOrgVO>> qzOrgList() {
    List<QztOrgVO> resultList = organizationBiz.qzOrgList();
    return ResponseUtil.success(resultList);
  }
}
