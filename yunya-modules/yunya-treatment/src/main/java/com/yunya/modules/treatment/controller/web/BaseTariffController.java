package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.form.BaseTariffForm;
import com.yunya.feign.treatment.domain.model.BaseTariffModel;
import com.yunya.feign.treatment.domain.query.BaseTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseTariffInfoVO;
import com.yunya.feign.treatment.domain.vo.BaseTariffVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BaseTariffBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 描述: 基础价目表控制器
 *
 * @author GaoLuding
 * @create 2020-05-19 13:28
 */
@Api(tags = "基础价目表管理接口")
@RestController
@RequestMapping("base")
public class BaseTariffController {

  /** 注入对象 */
  private final BaseTariffBiz baseTariffBiz;

  public BaseTariffController(BaseTariffBiz baseTariffBiz) {
    this.baseTariffBiz = baseTariffBiz;
  }

  /**
   * 根据ID获取价目表信息
   *
   * @param id ID
   * @return
   */
  @ApiOperation("根据ID(基础价目表ID)获取价目表信息(包含门诊价目表价格)")
  @GetMapping("/one/{id}")
  public ResponseResult<BaseTariffInfoVO> findById(@PathVariable(value = "id") Integer id) {
    BaseTariffInfoVO resultData = baseTariffBiz.findBaseTariffInfoById(id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询基础价目表列表
   *
   * @return
   */
  @ApiOperation("根据条件查询基础价目表列表(可分页)")
  @PostMapping("/list")
  public ResponseResult<PageInfo<BaseTariffVO>> findList(
      @RequestBody BaseTariffQueryForm queryForm) {
    PageInfo<BaseTariffVO> resultList = baseTariffBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增价目表
   *
   * @param model 新增参数
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("新增价目表")
  @PostMapping("/add")
  public ResponseResult<T> save(@RequestBody @Validated BaseTariffModel model) {
    baseTariffBiz.add(model);
    return ResponseUtil.success(null);
  }

  /**
   * 修改价目表信息
   *
   * @param form 修改参数
   * @return
   */
  @CurrentUser
  @ApiOperation("修改价目表信息")
  @PutMapping("/modify/{id}")
  public ResponseResult<T> modify(
      @PathVariable(value = "id") Integer id, @RequestBody @Validated BaseTariffForm form) {
    baseTariffBiz.modify(id, form);
    return ResponseUtil.success(null);
  }

  /**
   * 根据ID删除
   *
   * @param id ID
   * @return
   */
  @ApiOperation("根据ID删除")
  @DeleteMapping("/delete/{id}")
  public ResponseResult<T> delete(@PathVariable(value = "id") Integer id) {
    baseTariffBiz.delete(id);
    return ResponseUtil.success(null);
  }

  /**
   * 导入价目表
   *
   * @param excelFile 文件
   * @return
   */
  @CurrentUser
  @ApiOperation("导入价目表")
  @PostMapping("/import")
  public ResponseResult<String> importExcel(MultipartFile excelFile) throws Exception {
    String resultStr = baseTariffBiz.importExcel(excelFile);
    return ResponseUtil.success(resultStr);
  }

  /**
   * 根据条件导出价目表
   *
   * @param response http响应
   * @param queryForm 查询条件
   * @throws Exception
   */
  @ApiOperation("根据条件导出价目表")
  @PostMapping("/export")
  public ResponseResult<T> exportExcel(
      HttpServletResponse response, @RequestBody BaseTariffQueryForm queryForm) throws Exception {
    baseTariffBiz.exportExcel(response, queryForm);
    return ResponseUtil.success(null);
  }
}
