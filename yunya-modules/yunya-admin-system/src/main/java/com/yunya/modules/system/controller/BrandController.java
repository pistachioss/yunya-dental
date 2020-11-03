package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.Brand;
import com.yunya.modules.system.biz.BrandBiz;
import com.yunya.modules.system.domain.base.BaseForm;
import com.yunya.modules.system.domain.model.BrandModel;
import com.yunya.modules.system.domain.query.BrandQueryForm;
import com.yunya.modules.system.vo.BrandVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简单介绍:</br> 品牌控制层
 *
 * @author: chow
 * @date: 2020/5/28 14:18
 * @description:
 * @since: 1.0.0
 */
@Api(value = "品牌管理", description = "品牌管理（增删改查）")
@RestController
@RequestMapping("brand")
public class BrandController {

  /** 注入服务 */
  private final BrandBiz brandBiz;

  public BrandController(BrandBiz brandBiz) {
    this.brandBiz = brandBiz;
  }

  /**
   * 根据ID查询品牌
   *
   * @param id 品牌ID
   * @return
   */
  @ApiOperation("根据ID查询品牌信息")
  @GetMapping("/one/{id}")
  public ResponseResult<BrandVO> findById(@PathVariable(value = "id") Integer id) {
    BrandVO vo = new BrandVO();
    Brand brand = brandBiz.selectById(id);
    BeanUtils.copyProperties(brand, vo);
    return ResponseUtil.success(vo);
  }

  /**
   * 品牌列表查询（可分页)
   *
   * @param queryForm 查询参数封装
   * @return
   */
  @ApiOperation("条件查询品牌列表（可分页)")
  @PostMapping("/list")
  public ResponseResult<PageInfo<BrandVO>> findBrandList(@RequestBody BrandQueryForm queryForm) {
    PageInfo<BrandVO> brands = brandBiz.findAll(queryForm);
    return ResponseUtil.success(brands);
  }

  /**
   * 新增品牌
   *
   * @param resource 参数封装
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("新增品牌")
  @PostMapping("/add")
  public ResponseResult<T> addBrand(@Validated @RequestBody BrandModel resource) {
    brandBiz.add(resource);
    return ResponseUtil.success(null);
  }

  /**
   * 编辑品牌
   *
   * @param id 品牌ID
   * @param form 参数封装
   * @return
   */
  @CurrentUser
  @ApiOperation("编辑品牌")
  @ApiImplicitParam(name = "id", value = "品牌ID", dataType = "number", paramType = "path")
  @PutMapping("/edit/{id}")
  public ResponseResult<T> editBrand(
      @PathVariable(value = "id") Integer id, @Validated @RequestBody BaseForm form) {
    brandBiz.modifyBrand(id, form);
    return ResponseUtil.success(null);
  }

  /**
   * 删除品牌
   *
   * @param id 根据ID删除品牌
   * @return
   */
  @ApiOperation("根据ID删除品牌")
  @ApiImplicitParam(
      name = "id",
      value = "品牌ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @DeleteMapping("/delete/{id}")
  public ResponseResult<T> deleteBrand(@PathVariable(value = "id") Integer id) {
    brandBiz.deleteBrand(id);
    return ResponseUtil.success(null);
  }
}
