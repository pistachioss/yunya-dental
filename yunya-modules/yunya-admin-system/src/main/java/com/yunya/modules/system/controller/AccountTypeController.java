package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.AccountTypeBiz;
import com.yunya.modules.system.domain.form.AccountTypeForm;
import com.yunya.modules.system.domain.model.AccountTypeModel;
import com.yunya.modules.system.domain.query.AccountTypeQueryForm;
import com.yunya.modules.system.vo.AccountTypeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介: 入账方式分类控制器
 *
 * @author: chow
 * @date: 2020/7/24 14:07
 * @description:
 * @since: 1.0.0
 */
@Api(value = "入账方式分类管理", description = "入账方式分类新增、修改、删除、查询")
@RestController
@RequestMapping("account")
public class AccountTypeController {
  /** 注入对象 */
  private final AccountTypeBiz accountTypeBiz;

  public AccountTypeController(AccountTypeBiz accountTypeBiz) {
    this.accountTypeBiz = accountTypeBiz;
  }

  /**
   * 根据ID查询入账方式分类信息
   *
   * @param id 入账方式分类ID
   * @return
   */
  @ApiOperation("根据ID查询入账方式分类信息")
  @GetMapping("/type/one/{id}")
  public ResponseResult findById(@PathVariable("id") Integer id) {
    AccountTypeVO resultData = accountTypeBiz.findById(id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询入账方式分类列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询入账方式分类列表(可分页)")
  @PostMapping("/type/list")
  public ResponseResult findList(@RequestBody AccountTypeQueryForm queryForm) {
    PageInfo<AccountTypeVO> resultList = accountTypeBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增入账方式分类
   *
   * @param model 参数模型
   * @return
   */
  @ApiOperation("新增入账方式分类")
  @PostMapping("/type/save")
  public ResponseResult save(@RequestBody @Validated AccountTypeModel model) {
    accountTypeBiz.add(model);
    return ResponseUtil.success();
  }

  /**
   * 修改入账方式分类
   *
   * @param id 入账方式分类ID
   * @param form 参数封装
   * @return
   */
  @ApiOperation("修改入账方式分类")
  @PutMapping("/type/modify/{id}")
  public ResponseResult modify(
      @PathVariable("id") Integer id, @RequestBody @Validated AccountTypeForm form) {
    accountTypeBiz.modify(id, form);
    return ResponseUtil.success();
  }

  /**
   * 根据ID删除入账方式分类
   *
   * @param id 入账方式分类ID
   * @return
   */
  @ApiOperation("根据ID删除入账方式分类")
  @DeleteMapping("/type/delete/{id}")
  public ResponseResult delete(@PathVariable("id") Integer id) {
    accountTypeBiz.deleteAccountTypeById(id);
    return ResponseUtil.success();
  }
}
