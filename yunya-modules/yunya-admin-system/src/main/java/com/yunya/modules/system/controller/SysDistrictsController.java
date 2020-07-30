package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.SysDistricts;
import com.yunya.modules.system.biz.SysDistrictsBiz;
import com.yunya.modules.system.domain.query.SysDistrictsQueryForm;
import com.yunya.modules.system.vo.SysDistrictsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简单介绍:</br> 全国地区控制器
 *
 * @author: chow
 * @date: 2020/6/13 16:25
 * @description:
 * @since: 1.0.0
 */
@Api(value = "地区管理", description = "地区管理")
@RestController
@RequestMapping("districts")
@CrossOrigin
public class SysDistrictsController {

  /** 注入对象 */
  private final SysDistrictsBiz sysDistrictsBiz;

  public SysDistrictsController(SysDistrictsBiz sysDistrictsBiz) {
    this.sysDistrictsBiz = sysDistrictsBiz;
  }

  /**
   * 查询地区列表
   *
   * @param queryForm 参数封装
   * @return map
   */
  @ApiOperation("根据条件查询地区列表（可分页）")
  @PostMapping("/list")
  public ResponseResult list(@RequestBody @Validated SysDistrictsQueryForm queryForm) {
    PageInfo<SysDistrictsVO> resultList = sysDistrictsBiz.findDistrictsList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增保存地区
   *
   * @param districts 参数封装
   * @return map
   */
  @ApiOperation("新增保存地区")
  @PostMapping("/save")
  public ResponseResult addSave(@RequestBody @Validated SysDistricts districts) {
    sysDistrictsBiz.add(districts);
    return ResponseUtil.success();
  }

  /**
   * 修改保存地区
   *
   * @param districts 参数封装
   * @return map
   */
  @ApiOperation("修改保存地区")
  @PostMapping("/edit")
  public ResponseResult editSave(@RequestBody @Validated SysDistricts districts) {
    sysDistrictsBiz.edit(districts);
    return ResponseUtil.success();
  }

  /**
   * 删除地区
   *
   * @param ids 地区编号
   * @return map
   */
  @ApiOperation("删除地区")
  @PostMapping("/remove")
  public ResponseResult remove(String[] ids) {
    sysDistrictsBiz.remove(ids);
    return ResponseUtil.success();
  }
}
