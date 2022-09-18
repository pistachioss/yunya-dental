package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.form.BaseTariffAssociationForm;
import com.yunya.feign.treatment.domain.query.BaseTariffAssociationQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseTariffAssociationVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BaseTariffBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 简介: 价目表开单关联控制器
 *
 * @author: chow
 * @date: 2020/8/8 15:53
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "价目表开单关联信息管理")
@RestController
@RequestMapping("association")
public class BaseTariffAssociationController {

  /** 注入对象 */
  private final BaseTariffBiz baseTariffBiz;

  public BaseTariffAssociationController(BaseTariffBiz baseTariffBiz) {
    this.baseTariffBiz = baseTariffBiz;
  }

  /**
   * 根据条件查询开单关联信息列表（可分页）
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询开单关联信息列表（可分页）")
  @PostMapping("/list")
  public ResponseResult<PageInfo<BaseTariffAssociationVO>> findList(@RequestBody BaseTariffAssociationQueryForm queryForm) {
    PageInfo<BaseTariffAssociationVO> resultList =
        baseTariffBiz.findTariffAssociationList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 修改价目表开单关联信息
   *
   * @param id 价目表ID
   * @param form 开单关联信息
   * @return
   */
  @ApiOperation("修改价目表开单关联信息")
  @PutMapping("/modify/{id}")
  @CurrentUser
  public ResponseResult<T> modifyTariffAssociation(
      @PathVariable(value = "id") Integer id, @RequestBody BaseTariffAssociationForm form) {
    baseTariffBiz.modifyTariffAssociation(id, form);
    return ResponseUtil.success(null);
  }

  /**
   * 导入价目表开单关联信息
   *
   * @param excelFile 导入文件
   * @return
   * @throws Exception
   */
  @ApiOperation("导入价目表开单关联信息")
  @PostMapping("/import")
  public ResponseResult<String> importTariffAssociation(MultipartFile excelFile) throws Exception {
    String resultStr = baseTariffBiz.importTariffAssociation(excelFile);
    return ResponseUtil.success(resultStr);
  }

  /**
   * 导出价目表开单关联文件
   *
   * @param response 响应
   * @param queryForm 查询条件
   * @return
   * @throws Exception
   */
  @ApiOperation("导出价目表开单关联文件")
  @PostMapping("/export")
  public ResponseResult<T> exportTariffAssociation(
      HttpServletResponse response, @RequestBody BaseTariffAssociationQueryForm queryForm)
      throws Exception {
    baseTariffBiz.exportTariffAssociation(response, queryForm);
    return ResponseUtil.success(null);
  }
}
