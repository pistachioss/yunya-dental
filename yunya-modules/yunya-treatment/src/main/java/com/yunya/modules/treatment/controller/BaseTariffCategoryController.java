package com.yunya.modules.treatment.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.form.BaseTariffCategoryForm;
import com.yunya.feign.treatment.domain.model.BaseTariffCategoryModel;
import com.yunya.feign.treatment.domain.query.BaseTariffCategoryQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseTariffCategoryVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BaseTariffCategoryBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 描述: 价目表分类管理控制器
 *
 * @author GaoLuding
 * @create 2020-05-19 10:05
 */
@Api(tags = "价目表分类管理接口")
@RestController
@RequestMapping("category")
public class BaseTariffCategoryController {

  /** 注入对象 */
  private final BaseTariffCategoryBiz baseTariffCategoryBiz;

  public BaseTariffCategoryController(BaseTariffCategoryBiz baseTariffCategoryBiz) {
    this.baseTariffCategoryBiz = baseTariffCategoryBiz;
  }

  /**
   * 根据ID获取价目表分类信息
   *
   * @param id 基础价目表ID
   * @return
   */
  @ApiOperation("根据ID获取价目表分类信息")
  @GetMapping("/one/{id}")
  public ResponseResult findById(@PathVariable(value = "id") Integer id) {
    BaseTariffCategoryVO resultData = baseTariffCategoryBiz.findById(id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询价目表分类列表
   *
   * @return
   */
  @ApiOperation("根据条件查询价目表分类列表（可分页）")
  @PostMapping("/list")
  public ResponseResult findList(@RequestBody BaseTariffCategoryQueryForm queryForm) {
    PageInfo<BaseTariffCategoryVO> resultList = baseTariffCategoryBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增价目表目录
   *
   * @param model 新增参数
   * @return
   */
  @ApiOperation("新增价目表分类")
  @CurrentUser
  @PostMapping("/save")
  public ResponseResult save(@RequestBody @Validated BaseTariffCategoryModel model) {
    baseTariffCategoryBiz.add(model);
    return ResponseUtil.success();
  }

  /**
   * 修改价目表目录
   *
   * @param id 价目表分类ID
   * @param form 修改参数
   * @return
   */
  @CurrentUser
  @ApiOperation("修改价目表分类")
  @PutMapping("/modify/{id}")
  public ResponseResult modify(
      @PathVariable(value = "id") Integer id, @RequestBody @Validated BaseTariffCategoryForm form) {
    baseTariffCategoryBiz.modify(id, form);
    return ResponseUtil.success();
  }

  /**
   * 根据ID删除价目表分类
   *
   * @param id 价目表分类ID
   * @return
   */
  @ApiOperation("根据ID删除价目表分类")
  @DeleteMapping("/delete/{id}")
  public ResponseResult delete(@PathVariable(value = "id") Integer id) {
    baseTariffCategoryBiz.deleteBaseTariffCategoryById(id);
    return ResponseUtil.success();
  }
}
