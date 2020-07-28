package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.ClinicAccountItemBiz;
import com.yunya.modules.system.domain.model.ClinicAccountItemModel;
import com.yunya.modules.system.domain.query.ClinicAccountItemQueryForm;
import com.yunya.modules.system.vo.ClinicAccountItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介: 门诊入账方式控制器
 *
 * @author: chow
 * @date: 2020/7/27 11:12
 * @description:
 * @since: 1.0.0
 */
@Api(value = "门诊入账方式关联", description = "门诊入账方式新增、设置、查询")
@RestController
@RequestMapping("account")
public class ClinicAccountItemController {

  /** 注入对象 */
  private final ClinicAccountItemBiz clinicAccountItemBiz;

  public ClinicAccountItemController(ClinicAccountItemBiz clinicAccountItemBiz) {
    this.clinicAccountItemBiz = clinicAccountItemBiz;
  }

  /**
   * 根据ID查询门诊入账方式
   *
   * @param id 门诊入账方式ID
   * @return
   */
  @ApiOperation("根据ID查询门诊入账方式")
  @GetMapping("/clinic/one/{id}")
  public ResponseResult findById(@PathVariable("id") Integer id) {
    ClinicAccountItemVO resultData = clinicAccountItemBiz.findById(id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询门诊入账方式列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询门诊入账方式列表(可分页)")
  @PostMapping("/clinic/list")
  public ResponseResult findList(@RequestBody ClinicAccountItemQueryForm queryForm) {
    PageInfo<ClinicAccountItemVO> resultList = clinicAccountItemBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增门诊入账方式
   *
   * @param model 参数模型
   * @return
   */
  @ApiOperation("新增门诊入账方式")
  @PostMapping("/clinic/save")
  public ResponseResult save(@RequestBody @Validated ClinicAccountItemModel model) {
    clinicAccountItemBiz.add(model);
    return ResponseUtil.success();
  }

  /**
   * 一键新增门诊入账方式
   *
   * @param accountItemId 入账方式ID
   * @return
   */
  @ApiOperation("一键新增门诊入账方式")
  @GetMapping("/clinic/batch/{accountItemId}")
  public ResponseResult oneClickAdd(@PathVariable("accountItemId") Integer accountItemId) {
    clinicAccountItemBiz.batchSave(accountItemId);
    return ResponseUtil.success();
  }

  /**
   * 设置组织支付方式是否可用
   *
   * @param id 组织支付方式ID
   * @return
   */
  @GetMapping("/clinic/{id}")
  public ResponseResult switchClinicAccountItem(@PathVariable("id") Integer id) {
    clinicAccountItemBiz.switchClinicAccountItem(id);
    return ResponseUtil.success();
  }
}
