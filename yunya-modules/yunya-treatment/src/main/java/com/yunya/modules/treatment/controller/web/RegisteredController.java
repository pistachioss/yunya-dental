package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.form.ReferredForm;
import com.yunya.feign.treatment.domain.form.ReferredInfoForm;
import com.yunya.feign.treatment.domain.form.ReferredRrportForm;
import com.yunya.feign.treatment.domain.model.RegisteredModel;
import com.yunya.feign.treatment.domain.query.RegisteredQueryForm;
import com.yunya.feign.treatment.domain.vo.ReferredInfoVO;
import com.yunya.feign.treatment.domain.vo.ReferredRrportVO;
import com.yunya.feign.treatment.domain.vo.WaitingPatientInfoVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.modules.treatment.biz.RegisteredBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简介: 患者挂号控制器
 *
 * @author: chow
 * @date: 2020/8/11 11:21
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "患者挂号管理（新增、取消、侯诊列表查询）")
@RestController
@RequestMapping("registered")
public class RegisteredController {

  /** 注入对象 */
  private final RegisteredBiz registeredBiz;

  public RegisteredController(RegisteredBiz registeredBiz) {
    this.registeredBiz = registeredBiz;
  }

  /**
   * 新增患者挂号
   *
   * @param model 挂号信息
   * @return
   */
  @CurrentUser
  @ApiOperation("新增患者挂号")
  @PostMapping("/save")
  public ResponseResult<T> add(@RequestBody @Validated RegisteredModel model) {
    registeredBiz.save(model);
    return ResponseUtil.success(null);
  }

  /**
   * 转诊
   *
   * @param
   * @return
   */
  @CurrentUser
  @ApiOperation("转诊")
  @PostMapping("/referred")
  public ResponseResult referred(@RequestBody @Validated ReferredForm referredForm) {
    return ResponseUtil.success( registeredBiz.referred(referredForm));
  }

  /**
   * 门诊端转诊记录
   *
   * @param
   * @return
   */
  @CurrentUser
  @ApiOperation("门诊端转诊记录")
  @PostMapping("/referredInfo")
  public ResponseResult<PageInfo<ReferredInfoVO>> referredInfo(@RequestBody @Validated ReferredInfoForm referredForm) {
    List<ReferredInfoVO> relist = registeredBiz.referredInfo(referredForm);
    if (referredForm.getWhetherPage()) {
      Integer pageNum = referredForm.getPageNum();
      Integer pageSize = referredForm.getPageSize();
      int total = relist.size();
      PageInfo<ReferredInfoVO> pageInfo = new PageInfo<>();
      pageInfo.setPageNum(pageNum);
      pageInfo.setPageSize(pageSize);
      pageInfo.setTotal(total);
      List<ReferredInfoVO> list =
              relist.subList(
                      pageSize * (pageNum - 1), (Math.min((pageSize * pageNum), total)));
      pageInfo.setList(list);
      return ResponseUtil.success(pageInfo);
    }
    return ResponseUtil.success(new PageInfo<>(relist));
  }
  /**
   * 导出门诊端转诊记录
   *
   * @param
   * @return
   */
  @CurrentUser
  @ApiOperation("导出门诊端转诊记录")
  @PostMapping("/referredInfo/export")
  public ResponseResult<T> exportReferredInfo(HttpServletResponse response,@RequestBody @Validated ReferredInfoForm referredForm) throws IOException {
    List<ReferredInfoVO> relist = registeredBiz.referredInfo(referredForm);
    ExcelUtil<ReferredInfoVO> excelUtil = new ExcelUtil<>(ReferredInfoVO.class);
    String fileName =
            excelUtil.getFileName(
                    null,
                    null,
                    null,
                    "转诊统计");
    excelUtil.exportExcel(response, relist, "转诊统计", fileName);
    return ResponseUtil.success(null);
  }

  /**
   * 转诊报表
   *
   * @param
   * @return
   */
  @CurrentUser
  @ApiOperation("转诊报表")
  @PostMapping("/referredReport")
  public ResponseResult<PageInfo<ReferredRrportVO>> referredReport(@RequestBody @Validated ReferredRrportForm referredRrportForm) {
      List<ReferredRrportVO> relist = registeredBiz.referredReport(referredRrportForm);
      if (referredRrportForm.getWhetherPage()) {
          Integer pageNum = referredRrportForm.getPageNum();
          Integer pageSize = referredRrportForm.getPageSize();
          int total = relist.size();
          PageInfo<ReferredRrportVO> pageInfo = new PageInfo<>();
          pageInfo.setPageNum(pageNum);
          pageInfo.setPageSize(pageSize);
          pageInfo.setTotal(total);
          List<ReferredRrportVO> list =
                  relist.subList(
                          pageSize * (pageNum - 1), (Math.min((pageSize * pageNum), total)));
          pageInfo.setList(list);
          return ResponseUtil.success(pageInfo);
      }
    return ResponseUtil.success(new PageInfo<>(relist));
  }

  /**
   * 导出转诊报表
   *
   * @param
   * @return
   */
  @CurrentUser
  @ApiOperation("导出转诊报表")
  @PostMapping("/referredReport/export")
  public ResponseResult<T> exportReferredReport(HttpServletResponse response,@RequestBody @Validated ReferredRrportForm referredRrportForm) throws IOException {
    List<ReferredRrportVO> relist = registeredBiz.referredReport(referredRrportForm);
    ExcelUtil<ReferredRrportVO> excelUtil = new ExcelUtil<>(ReferredRrportVO.class);
    String fileName =
            excelUtil.getFileName(
                    null,
                    null,
                    null,
                    "转诊报表");
    excelUtil.exportExcel(response, relist, "转诊报表", fileName);
    return ResponseUtil.success(null);
  }
  /**
   * 根据挂号ID取消患者挂号
   *
   * @param id 挂号ID
   * @return
   */
  @ApiOperation("根据挂号ID取消患者挂号")
  @GetMapping("/cancel/{id}")
  public ResponseResult<T> cancel(@PathVariable(value = "id") Integer id) {
    registeredBiz.cancelRegistered(id);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊候诊中患者（挂号）列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询门诊候诊中患者列表（可分页）")
  @PostMapping("/list")
  public ResponseResult<PageInfo<WaitingPatientInfoVO>> findList(
      @RequestBody @Validated RegisteredQueryForm queryForm) {
    PageInfo<WaitingPatientInfoVO> resultList = registeredBiz.findRegisteredList(queryForm);
    return ResponseUtil.success(resultList);
  }
}
