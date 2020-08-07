package com.yunya.modules.tariff.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.tariff.domain.form.ClinicOralTariffForm;
import com.yunya.feign.tariff.domain.form.ClinicOralTariffUniteDiscountForm;
import com.yunya.feign.tariff.domain.query.ClinicOralTariffQueryForm;
import com.yunya.feign.tariff.domain.vo.ClinicOralTariffVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.tariff.biz.ClinicOralTariffBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 描述: 门诊商品项目管理控制器
 *
 * @author GaoLuding
 * @create 2020-05-27 14:08
 */
@Api(tags = "门诊商品项目管理接口")
@RestController
@RequestMapping("clinicOralTariffs")
public class ClinicOralTariffController {

  /** 注入对象 */
  private final ClinicOralTariffBiz clinicOralTariffBiz;

  public ClinicOralTariffController(ClinicOralTariffBiz clinicOralTariffBiz) {
    this.clinicOralTariffBiz = clinicOralTariffBiz;
  }

  /**
   * 根据门诊商品项目ID获取门诊商品项目信息
   *
   * @param orgId 组织ID
   * @param id 门诊商品项目ID
   * @return
   */
  @ApiOperation("根据ID获取信息")
  @GetMapping("/one/{orgId}/{id}")
  public ResponseResult findById(
      @PathVariable(value = "orgId") Integer orgId, @PathVariable("id") Integer id) {
    ClinicOralTariffVO resultData = clinicOralTariffBiz.findById(orgId, id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询门诊商品项目信息列表（可分页）
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询门诊商品项目信息列表（可分页）")
  @PostMapping("/list")
  public ResponseResult findList(@RequestBody @Validated ClinicOralTariffQueryForm queryForm) {
    PageInfo<ClinicOralTariffVO> resultList = clinicOralTariffBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 修改门诊商品项目价格信息
   *
   * @param id 门诊商品项目ID
   * @param form 修改参数
   * @return
   */
  @CurrentUser
  @ApiOperation("修改门诊商品项目价格信息")
  @PutMapping("/modify/{id}")
  public ResponseResult modify(
      @PathVariable("id") Integer id, @RequestBody @Validated ClinicOralTariffForm form) {
    clinicOralTariffBiz.modify(id, form);
    return ResponseUtil.success();
  }

  /**
   * 设置门诊商品项目是否启用
   *
   * @param id 门诊商品项目ID
   * @return
   */
  @CurrentUser
  @ApiOperation("设置门诊商品项目项目是否启用")
  @GetMapping("/switch/{id}")
  public ResponseResult switchClinicTariff(@PathVariable(value = "id") Integer id) {
    clinicOralTariffBiz.switchClinicOralTariff(id);
    return ResponseUtil.success();
  }

  /**
   * 统一设置门诊商品项目会员折扣
   *
   * @param form 统一折扣参数
   * @return
   */
  @CurrentUser
  @ApiOperation("统一设置门诊商品项目会员折扣")
  @PostMapping("/unite")
  public ResponseResult uniteMemberDiscount(
      @RequestBody @Validated ClinicOralTariffUniteDiscountForm form) {
    clinicOralTariffBiz.uniteMemberDiscount(form);
    return ResponseUtil.success();
  }
}
