package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.form.ClinicAccountItemConfigureQueryForm;
import com.yunya.feign.system.vo.ClinicChargeItemVO;
import com.yunya.feign.system.vo.ClinicAccountItemListVO;
import com.yunya.feign.system.vo.ClinicAccountItemVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.ClinicAccountItemBiz;
import com.yunya.modules.system.domain.model.ClinicAccountItemModel;
import com.yunya.modules.system.domain.query.ClinicAccountItemQueryForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
  @ApiOperation("根据门诊入账方式ID查询门诊入账方式")
  @GetMapping("/clinic/one/{id}")
  public ResponseResult<ClinicAccountItemVO> findById(@PathVariable(value = "id") Integer id) {
    ClinicAccountItemVO resultData = clinicAccountItemBiz.findById(id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据门诊ID查询门诊可用的支付方式列表
   *
   * @param orgId 诊所ID
   * @return
   */
  @ApiOperation("根据门诊ID查询门诊可用的支付方式列表")
  @ApiImplicitParam(
      name = "orgId",
      value = "组织ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @GetMapping(value = "/clinic/list/{orgId}", name = "根据门诊ID查询门诊可用的支付方式列表")
  public ResponseResult<ClinicAccountItemListVO> clinicAccountItemList(
      @PathVariable(value = "orgId") Integer orgId) {
    ClinicAccountItemListVO resultData = clinicAccountItemBiz.findClinicAccountItemList(orgId);
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
  public ResponseResult<PageInfo<ClinicAccountItemVO>> findList(
      @RequestBody ClinicAccountItemQueryForm queryForm) {
    PageInfo<ClinicAccountItemVO> resultList = clinicAccountItemBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件查询门诊支付方式配置列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询门诊支付方式配置列表")
  @PostMapping(value = "/clinic/configure", name = "查询条件")
  public ResponseResult<PageInfo<ClinicAccountItemVO>> configureClinicAccountItem(
      @RequestBody @Validated ClinicAccountItemConfigureQueryForm queryForm) {
    PageInfo<ClinicAccountItemVO> resultList = clinicAccountItemBiz.configure(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增门诊入账方式
   *
   * @param model 参数模型
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("新增门诊入账方式")
  @PostMapping("/clinic/save")
  public ResponseResult<T> save(@RequestBody @Validated ClinicAccountItemModel model) {
    clinicAccountItemBiz.add(model);
    return ResponseUtil.success(null);
  }

  /**
   * 一键新增门诊入账方式
   *
   * @param accountItemId 入账方式ID
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("一键新增门诊入账方式")
  @GetMapping("/clinic/batch/{accountItemId}")
  public ResponseResult<T> oneClickAdd(
      @PathVariable(value = "accountItemId") Integer accountItemId) {
    clinicAccountItemBiz.batchSave(accountItemId);
    return ResponseUtil.success(null);
  }

  /**
   * 设置组织支付方式是否可用
   *
   * @param id 组织支付方式ID
   * @return
   */
  @CurrentUser
  @ApiOperation("设置组织支付方式是否可用")
  @GetMapping("/clinic/{id}")
  public ResponseResult<T> switchClinicAccountItem(@PathVariable(value = "id") Integer id) {
    clinicAccountItemBiz.switchClinicAccountItem(id);
    return ResponseUtil.success(null);
  }

  /**
   * 设置支付方式在门诊是否可用
   *
   * @param orgId 组织ID
   * @param accountItemId 支付方式ID
   * @return
   */
  @ApiOperation("设置支付方式在门诊是否可用")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "orgId", value = "组织ID", required = true),
    @ApiImplicitParam(name = "accountItemId", value = "支付方式ID", required = true)
  })
  @GetMapping(value = "/switch/item/{orgId}/{accountItemId}", name = "设置支付方式在门诊是否可用")
  @CurrentUser
  public ResponseResult<T> switchClinicAccountItem(
      @PathVariable(value = "orgId") Integer orgId,
      @PathVariable(value = "accountItemId") Integer accountItemId) {
    clinicAccountItemBiz.switchClinicAccountItem(orgId, accountItemId);
    return ResponseUtil.success(null);
  }

  /**
   * 根据门诊id查询门诊可用的其他入账方式（不包含会员卡、预付款、专项预付款）
   *
   * @param orgId 门诊id
   * @return
   */
  @ApiOperation("根据门诊id查询门诊可用的其他入账方式（不包含会员卡、预付款、专项预付款）")
  @ApiImplicitParam(
          name = "orgId",
          value = "组织ID",
          required = true,
          dataType = "int",
          paramType = "path")
  @GetMapping(value = "/clinic/other/{orgId}", name = "根据门诊id查询门诊可用的其他入账方式（不包含会员卡、预付款、专项预付款）")
  public ResponseResult<List<ClinicAccountItemVO>> findClinicAccountOtherCharge(
          @PathVariable(value = "orgId") Integer orgId) {
    List<ClinicAccountItemVO> result = clinicAccountItemBiz.findClinicAccountOtherCharge(orgId, new ArrayList<>());
    return ResponseUtil.success(result);
  }

  /**
   * 根据门诊id和患者id查询门诊可用的收费入账方式
   *
   * @param orgId 门诊id
   * @return
   */
  @ApiOperation("根据门诊id和患者id查询门诊可用的收费入账方式")
  @ApiImplicitParams(value = {
          @ApiImplicitParam(
            name = "orgId",
            value = "组织ID",
            required = true,
            dataType = "int",
            paramType = "path"),
          @ApiImplicitParam(
            name = "patientId",
            value = "患者id",
            required = true,
            dataType = "int",
            paramType = "path")})
  @GetMapping(value = "/clinic/charge/{orgId}/{patientId}", name = "根据门诊id和患者id查询门诊可用的收费入账方式")
  public ResponseResult<ClinicChargeItemVO> findClinicAccountCharge(
          @PathVariable(value = "orgId") Integer orgId, @PathVariable(value = "patientId") Integer patientId) {
    ClinicChargeItemVO result = clinicAccountItemBiz.findClinicAccountCharge(orgId, patientId);
    return ResponseUtil.success(result);
  }
}
