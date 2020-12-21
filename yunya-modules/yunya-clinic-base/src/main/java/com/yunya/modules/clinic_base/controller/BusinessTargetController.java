package com.yunya.modules.clinic_base.controller;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.domain.model.BusinessTargetModel;
import com.yunya.feign.clinic_base.domain.query.*;
import com.yunya.feign.clinic_base.domain.vo.*;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.clinic_base.BusinessTarget;
import com.yunya.modules.clinic_base.biz.BusinessTargetBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;

/**
 * 简介: 公司业务目标
 *
 * @author: Zkq
 * @date: 2020/8/20 14:53
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "业务目标模块增删改查")
@RestController
@RequestMapping("business")
public class BusinessTargetController {

  @Resource private BusinessTargetBiz businessTargetBiz;

  /**
   * 业务目标分页列表
   *
   * @param query 业务目标分页查询模型
   * @return page
   */
  @ApiOperation("业务目标模块列表")
  @PostMapping("/findBusinessTargetByPage")
  public ResponseResult<PageInfo<BusinessTargetVo>> findBusinessTargetByPage(
      @Valid @RequestBody BusinessTargetQuery query) {
    PageInfo<BusinessTargetVo> page = businessTargetBiz.findBusinessTargetByPage(query);
    return ResponseUtil.success(page);
  }

  /**
   * 添加业务目标
   *
   * @param model 添加业务目标
   */
  @ApiOperation("添加业务目标")
  @PostMapping("/add")
  @CurrentUser
  public ResponseResult add(@Valid @RequestBody List<BusinessTargetModel> model) {
    Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
    for (BusinessTargetModel data : model) {
      BusinessAddOrUpdQuery businessAddOrUpdQuery = new BusinessAddOrUpdQuery();
      BusinessTarget businessTarget = new BusinessTarget();
      BeanUtils.copyProperties(data, businessAddOrUpdQuery);
      BeanUtils.copyProperties(data, businessTarget);
      List<BusinessTargetOrVo> businessTargetOrVo =
          businessTargetBiz.businessAddOrUpd(businessAddOrUpdQuery);
      if (businessTargetOrVo != null && !businessTargetOrVo.isEmpty()) {
        Integer id = businessTargetOrVo.get(0).getId();
        businessTarget.setUpdId(crtId);
        businessTarget.setId(id);
        businessTargetBiz.upd(businessTarget);
      } else {
        businessTarget.setCrtId(crtId);
        businessTargetBiz.add(businessTarget);
      }
    }
    return ResponseUtil.success();
  }

  /**
   * 多选门诊查询结果
   *
   * @param businessTargetTotalQuery 根据门诊id获取列表
   * @return businessTargetTotalVo
   */
  @ApiOperation("多选门诊查询结果")
  @PostMapping("/findAllData")
  public ResponseResult<BusinessTargetTotalVo> findAllData(
      @Valid @RequestBody BusinessTargetTotalQuery businessTargetTotalQuery) {
    BusinessTargetTotalVo businessTargetTotalVo =
        businessTargetBiz.findAllData(businessTargetTotalQuery);
    return ResponseUtil.success(businessTargetTotalVo);
  }

  /**
   * 回显公司目标
   *
   * @param businessTargetByDataQuery 回显公司目标
   * @return businessTargetByIdVo
   */
  @ApiOperation("回显公司目标")
  @PostMapping("/findDataById")
  public ResponseResult<BusinessTargetByIdVo> findDataById(
      @Valid @RequestBody BusinessTargetByDataQuery businessTargetByDataQuery) {
    BusinessTargetByIdVo businessTargetByIdVo =
        businessTargetBiz.findDataById(businessTargetByDataQuery);
    return ResponseUtil.success(businessTargetByIdVo);
  }

  @ApiOperation(value = "业务目标导出")
  @PostMapping("/target/export")
  public void exportListByDate(
      HttpServletResponse response, @Valid @RequestBody BusinessTargetExportQuery query)
      throws IOException {
    response.setContentType("application/vnd.ms-excel");
    response.setCharacterEncoding("utf-8");
    String fileName = URLEncoder.encode("业务目标导出", "UTF-8");
    response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
    List<BusinessTargetExportVo> businessTargetExportVos =
        businessTargetBiz.exportListByDatee(query);
    BigDecimal bigDecimal = new BigDecimal(500);
    for (BusinessTargetExportVo list : businessTargetExportVos) {
      list.setCompleteCash(bigDecimal);
      list.setPercentCash(bigDecimal.divide(list.getTargetCash(), 2) + "%");
      list.setCompleteFirstVisit(500);
      list.setPercentVisit(500 / list.getTargetFirstVisit() + "%");
      list.setCompleteNum(bigDecimal);
      list.setPercentNum(bigDecimal.divide(list.getTargetNum(), 2) + "%");
      list.setCompletePatientNum(500);
      list.setPercentPatientNum(500 / list.getTargetPatientNum() + "%");
    }
    EasyExcel.write(response.getOutputStream(), BusinessTargetExportVo.class)
        .sheet("sheet")
        .doWrite(businessTargetExportVos);
  }

  /**
   * 根据条件业务目标导出
   *
   * @param response 响应
   * @param query 查询条件
   */
  @ApiOperation("业务目标导出")
  @PostMapping("/export")
  public ResponseResult exportUserInfo(
      HttpServletResponse response, @Valid @RequestBody BusinessTargetExportQuery query)
      throws IOException {
    businessTargetBiz.exportListByDate(response, query);
    return ResponseUtil.success();
  }
}
