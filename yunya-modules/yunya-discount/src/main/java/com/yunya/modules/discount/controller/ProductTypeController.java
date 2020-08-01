package com.yunya.modules.discount.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.discount.domain.form.ProductTypeForm;
import com.yunya.feign.discount.domain.model.ProductTypeModel;
import com.yunya.feign.discount.domain.query.ProductTypeQueryForm;
import com.yunya.feign.discount.domain.vo.ProductTypeVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.modules.discount.biz.ProductTypeBiz;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 描述: 产品分类控制器
 *
 * @author GaoLuding
 * @create 2020-07-09 12:32
 */
@Api(tags = "产品分类")
@RestController
@RequestMapping("productType")
public class ProductTypeController {

  /** 注入对象 */
  private final ProductTypeBiz productTypeBiz;

  public ProductTypeController(ProductTypeBiz productTypeBiz) {
    this.productTypeBiz = productTypeBiz;
  }

  /**
   * 获取产品分类
   *
   * @param id 产品分类ID
   * @return
   */
  @ApiOperation("根据ID获取产品分类")
  @GetMapping("/one/{id}")
  public ResponseResult findById(@PathVariable("id") Integer id) {
    ProductTypeVO resultData = productTypeBiz.findById(id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询产品分类列表（可分页）")
  @PostMapping("/list")
  public ResponseResult findList(@RequestBody ProductTypeQueryForm queryForm) {
    PageInfo<ProductTypeVO> resultList = productTypeBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增产品分类
   *
   * @param model 新增参数
   * @return
   */
  @CurrentUser
  @ApiOperation("新增产品分类")
  @PostMapping("/save")
  public ResponseResult save(@RequestBody @Validated ProductTypeModel model) {
    productTypeBiz.saveMarketProductType(model);
    return ResponseUtil.success();
  }

  /**
   * 修改产品分类
   *
   * @param id 产品分类ID
   * @param form 修改参数
   * @return
   */
  @CurrentUser
  @ApiOperation("修改产品分类")
  @PutMapping("/modify/{id}")
  public ResponseResult update(
      @PathVariable("id") Integer id, @RequestBody @Validated ProductTypeForm form) {
    productTypeBiz.modify(id, form);
    return ResponseUtil.success();
  }

  /**
   * 根据ID删除产品分类
   *
   * @param id 产品分类ID
   * @return
   */
  @ApiOperation("根据ID删除产品分类")
  @DeleteMapping("/delete/{id}")
  public ResponseResult delete(@PathVariable("id") Integer id) {
    productTypeBiz.deleteProductTypeById(id);
    return ResponseUtil.success();
  }
}
