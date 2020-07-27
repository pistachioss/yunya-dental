package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.DepartmentRoomBiz;
import com.yunya.modules.system.domain.form.DepartmentRoomForm;
import com.yunya.modules.system.domain.model.DepartmentRoomModel;
import com.yunya.modules.system.domain.query.DepartmentRoomQueryForm;
import com.yunya.modules.system.vo.DepartmentRoomVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简介: 科室控制器
 *
 * @author: chow
 * @date: 2020/7/20 14:29
 * @description:
 * @since: 1.0.0
 */
@Api(value = "科室模版管理", description = "科室模版新增、修改、删除、查询")
@RestController
@RequestMapping("deptRoom")
public class DepartmentRoomController {

  /** 注入对象 */
  private final DepartmentRoomBiz departmentRoomBiz;

  public DepartmentRoomController(DepartmentRoomBiz departmentRoomBiz) {
    this.departmentRoomBiz = departmentRoomBiz;
  }

  /**
   * 根据科室ID查询科室
   *
   * @param id 科室ID
   * @return obj
   */
  @ApiOperation("根据科室ID查询科室")
  @GetMapping("/one/{id}")
  public ResponseResult findById(@PathVariable("id") Integer id) {
    DepartmentRoomVO resultData = departmentRoomBiz.findById(id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询科室列表
   *
   * @param resource 查询条件
   * @return list
   */
  @ApiOperation("根据条件查询科室列表(可分页)")
  @PostMapping("/list")
  public ResponseResult findList(@RequestBody DepartmentRoomQueryForm resource) {
    List<DepartmentRoomVO> resultList = departmentRoomBiz.findList(resource);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增科室
   *
   * @param resource 参数封装
   * @return
   */
  @ApiOperation("新增科室")
  @PostMapping("/save")
  public ResponseResult add(@RequestBody @Validated DepartmentRoomModel resource) {
    departmentRoomBiz.saveDepartmentRoom(resource);
    return ResponseUtil.success();
  }

  /**
   * 修改科室
   *
   * @param id 科室ID
   * @param form 参数封装
   * @return
   */
  @ApiOperation("修改科室")
  @PutMapping("/modify/{id}")
  public ResponseResult modify(
      @PathVariable("id") Integer id, @RequestBody @Validated DepartmentRoomForm form) {
    departmentRoomBiz.modify(id, form);
    return ResponseUtil.success();
  }

  /**
   * 根据ID删除科室
   *
   * @param id 科室ID
   * @return
   */
  @ApiOperation("根据ID删除科室")
  @DeleteMapping("/delete/{id}")
  public ResponseResult delete(@PathVariable("id") Integer id) {
    departmentRoomBiz.deleteDeptRoomById(id);
    return ResponseUtil.success();
  }
}
