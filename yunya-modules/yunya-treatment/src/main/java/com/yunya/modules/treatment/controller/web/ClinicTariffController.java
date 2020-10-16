package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.form.ClinicTariffForm;
import com.yunya.feign.treatment.domain.form.ClinicTariffUniteDiscountForm;
import com.yunya.feign.treatment.domain.query.ClinicTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.ClinicTariffVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.ClinicTariffBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 描述: 门诊价目表控制器
 *
 * @author GaoLuding
 * @create 2020-05-27 14:11
 */
@Api(tags = "门诊价目表管理接口")
@RestController
@RequestMapping("clinic")
public class ClinicTariffController {

  @Autowired private ClinicTariffBiz clinicTariffBiz;

  /**
   * 根据门诊价目表ID获取门诊价目表信息
   *
   * @param orgId 组织ID
   * @param id 门诊价目表ID
   * @return
   */
  @ApiOperation("根据门诊价目表ID获取信息")
  @GetMapping("/one/{orgId}/{id}")
  public ResponseResult findById(
      @PathVariable(value = "orgId") Integer orgId, @PathVariable(value = "id") Integer id) {
    ClinicTariffVO resultData = clinicTariffBiz.findById(orgId, id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询门诊价目表信息列表（可分页）
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询门诊价目表信息列表（可分页）")
  @PostMapping("/list")
  public ResponseResult findList(@RequestBody @Validated ClinicTariffQueryForm queryForm) {
    PageInfo<ClinicTariffVO> resultList = clinicTariffBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 修改门诊价目表价格信息
   *
   * @param id 门诊价目表ID
   * @param form 修改参数
   * @return
   */
  @CurrentUser
  @ApiOperation("修改门诊价目表价格信息")
  @PutMapping("/modify/{id}")
  public ResponseResult modify(
      @PathVariable(value = "id") Integer id, @RequestBody @Validated ClinicTariffForm form) {
    clinicTariffBiz.modify(id, form);
    return ResponseUtil.success();
  }

  /**
   * 设置门诊价目表是否启用
   *
   * @param id 门诊价目表ID
   * @return
   */
  @CurrentUser
  @ApiOperation("设置门诊价目表项目是否启用")
  @GetMapping("/switch/{id}")
  public ResponseResult switchClinicTariff(@PathVariable(value = "id") Integer id) {
    clinicTariffBiz.switchClinicTariff(id);
    return ResponseUtil.success();
  }

  /**
   * 统一设置门诊价目表会员折扣
   *
   * @param form 统一折扣参数
   * @return
   */
  @CurrentUser
  @ApiOperation("统一设置门诊价目表会员折扣")
  @PostMapping("/unite")
  public ResponseResult uniteMemberDiscount(
      @RequestBody @Validated ClinicTariffUniteDiscountForm form) {
    clinicTariffBiz.uniteMemberDiscount(form);
    return ResponseUtil.success();
  }

  /**
   * 根据条件导出门诊价目表列表
   *
   * @param response 响应
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件导出门诊价目表列表")
  @PostMapping("/export")
  public ResponseResult export(
      HttpServletResponse response, @RequestBody @Validated ClinicTariffQueryForm queryForm)
      throws IOException {
    clinicTariffBiz.exportClinicTariffList(response, queryForm);
    return ResponseUtil.success();
  }

  /**
   * 根据条件查询门诊基础价目表（卡券设计-产品详情-适用项目）
   * @param search 检索名称首字母缩写
   * @param type 项目类型 0-价目表；1-商品表
   * @return 返回实体对象
   */
  @ApiModelProperty("根据条件查询门诊基础价目表（卡券设计-产品详情-适用项目）项目类型 0-价目表；1-商品表")
  @GetMapping("/category/{type}")
  public ResponseResult clinicBaseTariff(@PathVariable("type") Byte type, String search) {
    return this.clinicTariffBiz.clinicBaseTariff(type,search);
  }

}
