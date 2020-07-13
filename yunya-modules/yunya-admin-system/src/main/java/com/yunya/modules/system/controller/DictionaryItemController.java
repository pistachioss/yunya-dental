package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.DictionaryItemBiz;
import com.yunya.modules.system.entity.DictionaryItem;
import com.yunya.modules.system.form.DictForm;
import com.yunya.modules.system.form.query.DictQueryForm;
import com.yunya.modules.system.vo.DictionaryItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简单介绍:</br> 字典明细控制层
 *
 * @author: chow
 * @date: 2020/6/3 16:56
 * @description:
 * @since: 1.0.0
 */
@Api(value = "字典明细数据管理", description = "字典数据增删改查")
@RestController
@RequestMapping("dict/item")
public class DictionaryItemController {
  /** 注入对象 */
  private final DictionaryItemBiz dictionaryItemBiz;

  public DictionaryItemController(DictionaryItemBiz dictionaryItemBiz) {
    this.dictionaryItemBiz = dictionaryItemBiz;
  }

  /**
   * 根据条件查询字典明细列表
   *
   * @param queryForm 参数封装
   * @return list
   */
  @ApiOperation("根据条件查询字典明细列表（可分页）")
  @ApiImplicitParam(name = "form", value = "字典明细全局查询参数封装模型", dataType = "DictQueryForm")
  @PostMapping("/list")
  public ResponseResult findList(@RequestBody @Validated DictQueryForm queryForm) {
    PageInfo<DictionaryItemVO> resultList = dictionaryItemBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增字典数据
   *
   * @param resource 参数封装
   * @return map
   */
  @ApiOperation("新增字典明细")
  @PostMapping("/add")
  public ResponseResult add(@RequestBody @Validated DictionaryItem resource) {
    dictionaryItemBiz.add(resource);
    return ResponseUtil.success();
  }

  /**
   * 编辑字典明细
   *
   * @param id 明细ID
   * @param form 参数封装
   * @return
   */
  @ApiOperation("编辑字典明细")
  @ApiImplicitParam(
      name = "id",
      required = true,
      value = "字典明细ID",
      dataType = "int",
      paramType = "path")
  @PutMapping("/edit/{id}")
  public ResponseResult edit(@PathVariable Integer id, @RequestBody @Validated DictForm form) {
    dictionaryItemBiz.edit(id, form);
    return ResponseUtil.success();
  }

  /**
   * 删除字典明细数据
   *
   * @param id 字典明细ID
   * @return
   */
  @ApiOperation("删除字典明细")
  @ApiImplicitParam(
      name = "id",
      required = true,
      value = "字典明细ID",
      dataType = "int",
      paramType = "path")
  @DeleteMapping("/delete/{id}")
  public ResponseResult delete(@PathVariable Integer id) {
    dictionaryItemBiz.deleteDictItem(id);
    return ResponseUtil.success();
  }
}
