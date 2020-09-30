package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.form.ClinicDeptRoomConfigureQueryForm;
import com.yunya.feign.system.vo.ClinicDepartmentRoomVO;
import com.yunya.feign.system.vo.ClinicDeptRoomListVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.ClinicDepartmentRoomBiz;
import com.yunya.modules.system.domain.model.ClinicDepartmentRoomModel;
import com.yunya.modules.system.domain.query.ClinicDepartmentRoomQueryForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介: 门诊科室管理控制器
 *
 * @author: chow
 * @date: 2020/7/20 14:56
 * @description:
 * @since: 1.0.0
 */
@Api(value = "门诊科室管理", description = "门诊科室修改、删除、查询")
@RestController
@RequestMapping("deptRoom")
public class ClinicDepartmentRoomController {

  /** 注入对象 */
  private final ClinicDepartmentRoomBiz clinicDepartmentRoomBiz;

  public ClinicDepartmentRoomController(ClinicDepartmentRoomBiz clinicDepartmentRoomBiz) {
    this.clinicDepartmentRoomBiz = clinicDepartmentRoomBiz;
  }

  /**
   * 一键新增全部门诊科室
   *
   * @param deptRoomId 科室模板ID
   * @return
   */
  @CurrentUser
  @ApiOperation("一键添加门诊科室")
  @GetMapping("/clinic/batch/{deptRoomId}")
  public ResponseResult oneClickAdd(@PathVariable(value = "deptRoomId") Integer deptRoomId) {
    clinicDepartmentRoomBiz.batchSave(deptRoomId);
    return ResponseUtil.success();
  }

  /**
   * 设置门诊科室是否启用
   *
   * @param id 门诊科室ID
   * @return
   */
  @CurrentUser
  @ApiOperation("开启/关闭门诊科室启用状态")
  @GetMapping("/clinic/switch/{id}")
  public ResponseResult switchDeptRoomDisable(@PathVariable("id") Integer id) {
    clinicDepartmentRoomBiz.switchDeptRoomDisable(id);
    return ResponseUtil.success();
  }

  /**
   * 设置科室在门诊是否可用
   *
   * @param orgId 组织ID
   * @param deptRoomId 科室ID
   * @return
   */
  @CurrentUser
  @ApiOperation("设置科室在门诊是否启用")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "orgId", value = "组织ID", required = true),
    @ApiImplicitParam(name = "deptRoomId", value = "科室ID", required = true)
  })
  @GetMapping(value = "/switch/dept//{orgId}/{deptRoomId}", name = "设置科室在门诊是否启用")
  public ResponseResult switchClinicDept(
      @PathVariable(value = "orgId") Integer orgId,
      @PathVariable(value = "deptRoomId") Integer deptRoomId) {
    clinicDepartmentRoomBiz.switchClinicDept(orgId, deptRoomId);
    return ResponseUtil.success();
  }

  /**
   * 根据门诊科室ID查询门诊科室
   *
   * @param id 门诊科室ID
   * @return
   */
  @ApiOperation("根据门诊科室ID查询门诊科室")
  @GetMapping("/clinic/one/{id}")
  public ResponseResult findById(@PathVariable("id") Integer id) {
    ClinicDepartmentRoomVO departmentRoom = clinicDepartmentRoomBiz.findByClinicDeptRoomId(id);
    return ResponseUtil.success(departmentRoom);
  }

  /**
   * 根据条件查询门诊科室配置列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询门诊科室配置列表")
  @ApiImplicitParams({@ApiImplicitParam(name = "queryForm", value = "查询条件", required = true)})
  @PostMapping(value = "/clinic/configure", name = "查询条件")
  public ResponseResult<PageInfo<ClinicDepartmentRoomVO>> configureClinicDeptRoom(
      @RequestBody ClinicDeptRoomConfigureQueryForm queryForm) {
    PageInfo<ClinicDepartmentRoomVO> resultList = clinicDepartmentRoomBiz.configure(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件查询门诊科室列表
   *
   * @param queryForm 查询参数
   * @return
   */
  @ApiOperation("根据条件查询门诊科室列表")
  @PostMapping("/clinic/list")
  public ResponseResult<PageInfo<ClinicDepartmentRoomVO>> findList(
      @RequestBody ClinicDepartmentRoomQueryForm queryForm) {
    PageInfo<ClinicDepartmentRoomVO> resultList = clinicDepartmentRoomBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据门诊ID查询门诊可用的科室列表
   *
   * @param orgId 组织ID
   * @return
   */
  @ApiOperation("根据门诊ID查询门诊可用的科室列表")
  @ApiImplicitParams({@ApiImplicitParam(name = "orgId", value = "组织ID", required = true)})
  @GetMapping(value = "/clinic/list/{orgId}", name = "根据门诊ID查询门诊可用的科室列表")
  public ResponseResult<ClinicDeptRoomListVO> clinicDeptRoomList(
      @PathVariable(value = "orgId") Integer orgId) {
    ClinicDeptRoomListVO resultData = clinicDepartmentRoomBiz.findClinicDeptRoomList(orgId);
    return ResponseUtil.success(resultData);
  }

  /**
   * 新增门诊科室
   *
   * @param model 门诊科室参数模型
   * @return
   */
  @CurrentUser
  @ApiOperation("新增门诊科室")
  @PostMapping("/clinic/save")
  public ResponseResult save(@RequestBody @Validated ClinicDepartmentRoomModel model) {
    clinicDepartmentRoomBiz.add(model);
    return ResponseUtil.success();
  }

  /**
   * 根据门诊科室ID删除门诊科室
   *
   * @param id 门诊科室ID
   * @return
   */
  @ApiOperation("根据门诊科室ID删除门诊科室")
  @DeleteMapping("/clinic/delete/{id}")
  public ResponseResult deleteByClinicDeptRoomId(@PathVariable("id") Integer id) {
    clinicDepartmentRoomBiz.deleteByClinicDeptRoomId(id);
    return ResponseUtil.success();
  }
}
