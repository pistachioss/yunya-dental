package com.yunya.modules.tariff.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.tariff.domain.form.BaseOralTariffCategoryForm;
import com.yunya.feign.tariff.domain.model.BaseOralTariffCategoryModel;
import com.yunya.feign.tariff.domain.query.BaseOralTariffCategoryQueryForm;
import com.yunya.feign.tariff.domain.vo.BaseOralTariffCategoryVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.tariff.biz.BaseOralTariffCategoryBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 描述: 商品分类管理控制器
 *
 * @author GaoLuding
 * @create 2020-05-19 10:05
 */
@Api(tags = "公司商品分类管理接口")
@RestController
@RequestMapping("oral/category")
public class BaseOralTariffCategoryController {

  /** 注入对象 */
  private final BaseOralTariffCategoryBiz baseOralTariffCategoryBiz;

  public BaseOralTariffCategoryController(BaseOralTariffCategoryBiz baseOralTariffCategoryBiz) {
    this.baseOralTariffCategoryBiz = baseOralTariffCategoryBiz;
  }

  /**
   * 根据ID获取商品分类信息
   *
   * @param id 基础价目表ID
   * @return
   */
  @ApiOperation("根据ID获取商品分类信息")
  @GetMapping("/one/{id}")
  public ResponseResult findById(@PathVariable(value = "id") Integer id) {
    BaseOralTariffCategoryVO resultData = baseOralTariffCategoryBiz.findById(id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询商品分类列表
   *
   * @return
   */
  @ApiOperation("根据条件查询商品分类列表（可分页）")
  @PostMapping("/list")
  public ResponseResult findList(@RequestBody BaseOralTariffCategoryQueryForm queryForm) {
    PageInfo<BaseOralTariffCategoryVO> resultList = baseOralTariffCategoryBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增商品分类
   *
   * @param model 新增参数
   * @return
   */
  @ApiOperation("新增商品分类")
  @CurrentUser
  @PostMapping("/save")
  public ResponseResult save(@RequestBody @Validated BaseOralTariffCategoryModel model) {
    baseOralTariffCategoryBiz.add(model);
    return ResponseUtil.success();
  }

  /**
   * 修改商品分类
   *
   * @param id 商品分类ID
   * @param form 修改参数
   * @return
   */
  @CurrentUser
  @ApiOperation("修改商品分类")
  @PutMapping("/modify/{id}")
  public ResponseResult modify(
      @PathVariable(value = "id") Integer id,
      @RequestBody @Validated BaseOralTariffCategoryForm form) {
    baseOralTariffCategoryBiz.modify(id, form);
    return ResponseUtil.success();
  }

  /**
   * 根据ID删除商品分类
   *
   * @param id 商品分类ID
   * @return
   */
  @ApiOperation("根据ID（商品分类ID）删除商品分类")
  @DeleteMapping("/delete/{id}")
  public ResponseResult delete(@PathVariable(value = "id") Integer id) {
    baseOralTariffCategoryBiz.deleteBaseOralTariffCategoryById(id);
    return ResponseUtil.success();
  }
}
