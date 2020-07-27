package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.AccountItemBiz;
import com.yunya.modules.system.domain.form.AccountItemForm;
import com.yunya.modules.system.domain.model.AccountItemModel;
import com.yunya.modules.system.domain.query.AccountItemQueryForm;
import com.yunya.modules.system.vo.AccountItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介: 入账方式控制器
 *
 * @author: chow
 * @date: 2020/7/27 09:48
 * @description:
 * @since: 1.0.0
 */
@Api(value = "入账方式管理", description = "入账方式新增、修改、删除、查询")
@RestController
@RequestMapping("account")
public class AccountItemController {

  /** 注入对象 */
  private final AccountItemBiz accountItemBiz;

  public AccountItemController(AccountItemBiz accountItemBiz) {
    this.accountItemBiz = accountItemBiz;
  }

  /**
   * 根据ID查询入账方式
   *
   * @param id 入账方式ID
   * @return
   */
  @ApiOperation("根据ID查询入账方式")
  @GetMapping("/item/one/{id}")
  public ResponseResult findById(@PathVariable("id") Integer id) {
    AccountItemVO resultData = accountItemBiz.findById(id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询入账方式列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询入账方式列表(可分页)")
  @PostMapping("/item/list")
  public ResponseResult findList(@RequestBody AccountItemQueryForm queryForm) {
    PageInfo<AccountItemVO> resultList = accountItemBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增入账方式
   *
   * @param model 新增参数模型
   * @return
   */
  @ApiOperation("新增入账方式")
  @PostMapping("/item/save")
  public ResponseResult save(@RequestBody @Validated AccountItemModel model) {
    accountItemBiz.add(model);
    return ResponseUtil.success();
  }

  /**
   * 修改入账方式
   *
   * @param id 入账方式ID
   * @param form 修改参数模型
   * @return
   */
  @ApiOperation("修改入账方式")
  @PutMapping("/item/edit/{id}")
  public ResponseResult edit(
      @PathVariable("id") Integer id, @RequestBody @Validated AccountItemForm form) {
    accountItemBiz.modify(id, form);
    return ResponseUtil.success();
  }

  /**
   * 根据ID删除入账方式
   *
   * @param id 入账方式ID
   * @return
   */
  @ApiOperation("根据ID删除入账方式")
  @DeleteMapping("/item/{id}")
  public ResponseResult deleteById(@PathVariable("id") Integer id) {
    accountItemBiz.deleteAccountItemById(id);
    return ResponseUtil.success();
  }
}
