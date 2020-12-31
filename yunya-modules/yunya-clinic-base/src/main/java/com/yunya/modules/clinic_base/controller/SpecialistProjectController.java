package com.yunya.modules.clinic_base.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.domain.form.SpecialistProjectForm;
import com.yunya.feign.clinic_base.domain.form.SpecialistProjectReportForm;
import com.yunya.feign.clinic_base.domain.model.SpecialistProjectModel;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectNameVO;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectReportVO;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.clinic_base.biz.SpecialistProjectBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简介: 专科项目管理控制层
 *
 * @author: chow
 * @date: 2020/12/22 13:27
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "专科项目管理-增删改查")
@RestController
@RequestMapping("specialist")
public class SpecialistProjectController {

  /** 专科项目 */
  @Autowired private SpecialistProjectBiz specialistProjectBiz;

  /**
   * 根据条件查询专科项目列表
   *
   * @param query 查询条件
   * @return PageInfo<SpecialistProjectVO>
   */
  @ApiOperation("根据条件查询专科项目列表")
  @PostMapping(value = "/list", name = "根据条件查询专科项目列表")
  public ResponseResult<PageInfo<SpecialistProjectVO>> specialistProjectList(
      @RequestBody @Validated SpecialistProjectQuery query) {
    PageInfo<SpecialistProjectVO> pageInfo = specialistProjectBiz.findSpecialistProjectList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 专科项目名称列表
   *
   * @return void
   */
  @ApiOperation("专科项目名称列表(专科项目数量分解用)")
  @GetMapping(value = "/name/list", name = "专科项目名称列表")
  public ResponseResult<List<SpecialistProjectNameVO>> specialistProjectName() {
    List<SpecialistProjectNameVO> resultList = specialistProjectBiz.findSpecialistProjectName();
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增专科项目
   *
   * @param model 新增参数
   * @return void
   */
  @CurrentUser
  @ApiOperation("新增专科项目")
  @PostMapping(value = "/add", name = "新增专科项目")
  public ResponseResult<T> addSpecialistProject(
      @RequestBody @Validated SpecialistProjectModel model) {
    specialistProjectBiz.save(model);
    return ResponseUtil.success(null);
  }

  /**
   * 根据ID修改专科项目
   *
   * @param id 专科项目id
   * @param form 更新参数
   * @return
   */
  @CurrentUser
  @ApiOperation("根据ID修改专科项目")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "id",
        value = "专科项目ID",
        required = true,
        dataType = "int",
        paramType = "path")
  })
  @PutMapping(value = "/modify/{id}", name = "根据ID删除专科项目")
  public ResponseResult<T> modifySpecialistProject(
      @PathVariable(value = "id") Integer id, @RequestBody @Validated SpecialistProjectForm form) {
    specialistProjectBiz.updateSpecialistProject(id, form);
    return ResponseUtil.success(null);
  }

  /**
   * 根据专科项目ID删除
   *
   * @param id 专科项目ID
   * @return void
   */
  @ApiOperation("根据专科项目ID删除")
  @ApiImplicitParam(
      name = "id",
      value = "专科项目ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @DeleteMapping(value = "/delete/{id}", name = "根据专科项目ID删除")
  public ResponseResult<T> deleteSpecialistProject(@PathVariable(value = "id") Integer id) {
    specialistProjectBiz.deleteById(id);
    return ResponseUtil.success(null);
  }

  /**
   * 专科项目名称列表
   *
   * @return void
   */
  @ApiOperation("患者报表-就诊患者分析-专科项目")
  @PostMapping(value = "/specialistProject/report", name = "就诊患者分析-专科项目")
  public ResponseResult<List<SpecialistProjectReportVO>> specialistProjectReport(@RequestBody @Validated SpecialistProjectReportForm form) {
    List<SpecialistProjectReportVO> resultList = specialistProjectBiz.specialistProjectReport(form);
    return ResponseUtil.success(resultList);
  }

}
