package com.yunya.modules.treatment.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.tariff.domain.form.BaseOralTariffForm;
import com.yunya.feign.tariff.domain.model.BaseOralTariffModel;
import com.yunya.feign.tariff.domain.query.BaseOralTariffQueryForm;
import com.yunya.feign.tariff.domain.vo.BaseOralTariffInfoVO;
import com.yunya.feign.tariff.domain.vo.BaseOralTariffVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BaseOralTariffBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 描述: 商品项目管理控制器
 *
 * @author GaoLuding
 * @create 2020-05-19 13:28
 */
@Api(tags = "商品项目管理")
@RestController
@RequestMapping("oral/base")
public class BaseOralTariffController {

  /** 注入对象 */
  private final BaseOralTariffBiz baseOralTariffBiz;

  public BaseOralTariffController(BaseOralTariffBiz baseOralTariffBiz) {
    this.baseOralTariffBiz = baseOralTariffBiz;
  }

  /**
   * 根据ID获取商品项目信息
   *
   * @param id 商品项目ID
   * @return
   */
  @ApiOperation("根据ID(商品项目ID)获取商品项目信息(包含门诊商品项目价格)")
  @GetMapping("/one/{id}")
  public ResponseResult findById(@PathVariable(value = "id") Integer id) {
    BaseOralTariffInfoVO resultData = baseOralTariffBiz.findBaseOralTariffInfoById(id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询商品项目列表
   *
   * @return
   */
  @ApiOperation("根据条件查询商品项目列表(可分页)")
  @PostMapping("/list")
  public ResponseResult findList(@RequestBody BaseOralTariffQueryForm queryForm) {
    PageInfo<BaseOralTariffVO> resultList = baseOralTariffBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增商品项目
   *
   * @param model 新增参数
   * @return
   */
  @CurrentUser
  @ApiOperation("新增单个商品项目")
  @PostMapping("/add")
  public ResponseResult save(@RequestBody @Validated BaseOralTariffModel model) {
    baseOralTariffBiz.add(model);
    return ResponseUtil.success();
  }

  /**
   * 修改商品项目
   *
   * @param form 修改参数
   * @return
   */
  @CurrentUser
  @ApiOperation("修改公司商品")
  @PutMapping("/modify/{id}")
  public ResponseResult modify(
      @PathVariable("id") Integer id, @RequestBody @Validated BaseOralTariffForm form) {
    baseOralTariffBiz.modify(id, form);
    return ResponseUtil.success();
  }

  /**
   * 根据ID删除商品项目
   *
   * @param id 商品项目ID
   * @return
   */
  @ApiOperation("根据ID删除商品项目")
  @DeleteMapping("/delete/{id}")
  public ResponseResult delete(@PathVariable(value = "id") Integer id) {
    baseOralTariffBiz.delete(id);
    return ResponseUtil.success();
  }

  /**
   * 导入商品项目价目表
   *
   * @param excelFile 文件
   * @return
   */
  @CurrentUser
  @ApiOperation("导入商品项目价目表")
  @PostMapping("/import")
  public ResponseResult importExcel(MultipartFile excelFile) throws Exception {
    String resultStr = baseOralTariffBiz.importExcel(excelFile);
    return ResponseUtil.success(resultStr);
  }

  /**
   * 根据条件导出商品项目价目表
   *
   * @param response http响应
   * @param queryForm 查询条件
   * @throws Exception
   */
  @ApiOperation("根据条件导出商品项目价目表")
  @PostMapping("/export")
  public ResponseResult exportExcel(
      HttpServletResponse response, @RequestBody BaseOralTariffQueryForm queryForm)
      throws Exception {
    baseOralTariffBiz.exportExcel(response, queryForm);
    return ResponseUtil.success();
  }
}
