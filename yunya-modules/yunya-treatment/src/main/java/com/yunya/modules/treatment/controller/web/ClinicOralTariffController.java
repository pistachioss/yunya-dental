package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.form.ClinicOralTariffForm;
import com.yunya.feign.treatment.domain.form.ClinicOralTariffUniteDiscountForm;
import com.yunya.feign.treatment.domain.query.ClinicOralTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.ClinicOralTariffVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.ClinicOralTariffBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 描述: 门诊商品项目管理控制器
 *
 * @author GaoLuding
 * @create 2020-05-27 14:08
 */
@Api(tags = "门诊商品项目管理接口")
@RestController
@RequestMapping("oral/clinic")
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
  @ApiOperation("根据门诊商品项目ID获取信息")
  @GetMapping("/one/{orgId}/{id}")
  public ResponseResult<ClinicOralTariffVO> findById(
      @PathVariable(value = "orgId") Integer orgId, @PathVariable(value = "id") Integer id) {
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
  public ResponseResult<PageInfo<ClinicOralTariffVO>> findList(
      @RequestBody @Validated ClinicOralTariffQueryForm queryForm) {
    PageInfo<ClinicOralTariffVO> resultList = clinicOralTariffBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 修改门诊商品项目价格信息
   *
   * @param form 修改参数
   * @return
   */
  @CurrentUser
  @ApiOperation("修改门诊商品项目价格信息")
  @PostMapping("/modify")
  public ResponseResult<T> modify(@RequestBody @Validated ClinicOralTariffForm form) {
    clinicOralTariffBiz.modify(form);
    return ResponseUtil.success(null);
  }

  /**
   * 设置门诊商品项目是否启用
   *
   * @param orgId 组织ID
   * @param oralTariffId 商品项目ID
   * @return
   */
  @CurrentUser
  @ApiOperation("设置门诊商品项目项目是否启用")
  @GetMapping("/switch/{orgId}/{oralTariffId}")
  public ResponseResult<T> switchClinicTariff(
      @PathVariable(value = "orgId") Integer orgId,
      @PathVariable(value = "oralTariffId") Integer oralTariffId) {
    clinicOralTariffBiz.switchClinicOralTariff(orgId, oralTariffId);
    return ResponseUtil.success(null);
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
  public ResponseResult<T> uniteMemberDiscount(
      @RequestBody @Validated ClinicOralTariffUniteDiscountForm form) {
    clinicOralTariffBiz.uniteMemberDiscount(form);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件导出门诊商品项目列表
   *
   * @param response 响应
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件导出门诊商品项目列表")
  @PostMapping("/export")
  public ResponseResult<T> export(
      HttpServletResponse response, @RequestBody @Validated ClinicOralTariffQueryForm queryForm)
      throws IOException {
    clinicOralTariffBiz.exportClinicOralTariffList(response, queryForm);
    return ResponseUtil.success(null);
  }
}
