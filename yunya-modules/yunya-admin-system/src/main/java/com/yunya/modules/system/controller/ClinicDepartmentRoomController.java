package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.ClinicDepartmentRoomBiz;
import com.yunya.modules.system.form.ClinicDepartmentRoomModel;
import com.yunya.modules.system.form.query.ClinicDepartmentRoomQueryForm;
import com.yunya.modules.system.vo.ClinicDepartmentRoomVO;
import io.swagger.annotations.Api;
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
   * 根据门诊科室ID查询门诊科室
   *
   * @param id 门诊科室ID
   * @return
   */
  @GetMapping("/clinic/one/{id}")
  public ResponseResult findById(@PathVariable("id") Integer id) {
    ClinicDepartmentRoomVO departmentRoom = clinicDepartmentRoomBiz.findByClinicDeptRoomId(id);
    return ResponseUtil.success(departmentRoom);
  }

  /**
   * 根据条件查询门诊科室列表
   *
   * @param queryForm 查询参数
   * @return
   */
  @PostMapping("/clinic/list")
  public ResponseResult findList(@RequestBody ClinicDepartmentRoomQueryForm queryForm) {
    PageInfo<ClinicDepartmentRoomVO> resultList = clinicDepartmentRoomBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 设置科室在某门诊不可用
   *
   * @param model 参数模型
   * @return
   */
  @PostMapping("/clinic/switch")
  public ResponseResult switchDeptRoomDisable(
      @RequestBody @Validated ClinicDepartmentRoomModel model) {
    clinicDepartmentRoomBiz.switchDeptRoomDisable(model);
    return ResponseUtil.success();
  }

  /**
   * 新增门诊科室
   *
   * @param model 门诊科室参数模型
   * @return
   */
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
  @DeleteMapping("/clinic/delete/{id}")
  public ResponseResult deleteByClinicDeptRoomId(@PathVariable("id") Integer id) {
    clinicDepartmentRoomBiz.deleteByClinicDeptRoomId(id);
    return ResponseUtil.success();
  }
}
