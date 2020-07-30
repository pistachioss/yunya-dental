package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.DictionaryType;
import com.yunya.modules.system.biz.DictionaryTypeBiz;
import com.yunya.modules.system.domain.form.DictForm;
import com.yunya.modules.system.domain.model.DictionaryTypeModel;
import com.yunya.modules.system.domain.query.DictQueryForm;
import com.yunya.modules.system.vo.DictionaryTypeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简单介绍:</br> 字典类型控制层
 *
 * @author: chow
 * @date: 2020/6/3 16:55
 * @description:
 * @since: 1.0.0
 */
@Api(value = "字典类型管理", description = "字典类型增删改查")
@RestController
@RequestMapping("dict")
public class DictionaryTypeController {

  /** 注入对象 */
  private final DictionaryTypeBiz dictionaryTypeBiz;

  public DictionaryTypeController(DictionaryTypeBiz dictionaryTypeBiz) {
    this.dictionaryTypeBiz = dictionaryTypeBiz;
  }

  /**
   * 根据ID查询字典类型
   *
   * @param id 字典类型ID
   * @return
   */
  @ApiOperation("根据ID查询字典类型")
  @GetMapping("/type/{id}")
  public ResponseResult findById(@PathVariable("id") Integer id) {
    DictionaryTypeVO vo = new DictionaryTypeVO();
    DictionaryType dictionaryType = dictionaryTypeBiz.selectById(id);
    BeanUtils.copyProperties(dictionaryType, vo);
    return ResponseUtil.success(vo);
  }

  /**
   * 查询字典类型列表（可分页）
   *
   * @param queryForm 参数封装
   * @return list
   */
  @ApiOperation("根据条件查询字典类型列表（可分页）")
  @PostMapping("/type/list")
  public ResponseResult findList(@RequestBody @Validated DictQueryForm queryForm) {
    PageInfo<DictionaryTypeVO> resultList = dictionaryTypeBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 添加字典类型
   *
   * @param resource 参数封装
   * @return map
   */
  @CurrentUser
  @ApiOperation("新增字典类型")
  @PostMapping("/type/add")
  public ResponseResult addDictType(@RequestBody @Validated DictionaryTypeModel resource) {
    dictionaryTypeBiz.add(resource);
    return ResponseUtil.success();
  }

  /**
   * 编辑字典类型
   *
   * @param id 字典类型ID
   * @param form 参数封装
   * @return
   */
  @CurrentUser
  @ApiOperation("编辑字典类型")
  @ApiImplicitParam(
      name = "id",
      required = true,
      value = "字典明细ID",
      dataType = "int",
      paramType = "path")
  @PutMapping("/type/edit/{id}")
  public ResponseResult edit(@PathVariable Integer id, @RequestBody @Validated DictForm form) {
    dictionaryTypeBiz.edit(id, form);
    return ResponseUtil.success();
  }

  /**
   * 删除字典类型
   *
   * @param id 字典ID
   * @return
   */
  @ApiOperation("删除字典类型")
  @ApiImplicitParam(
      name = "id",
      required = true,
      value = "字典类型ID",
      dataType = "int",
      paramType = "path")
  @DeleteMapping("/type/delete/{id}")
  public ResponseResult delete(@PathVariable Integer id) {
    dictionaryTypeBiz.deleteDict(id);
    return ResponseUtil.success();
  }
}
