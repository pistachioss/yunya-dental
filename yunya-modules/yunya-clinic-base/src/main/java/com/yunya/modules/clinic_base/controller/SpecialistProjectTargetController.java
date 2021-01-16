package com.yunya.modules.clinic_base.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.domain.model.SpecialistProjectTargetModel;
import com.yunya.feign.clinic_base.domain.query.BusinessWorkGoalQuery;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectTargetQuery;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectWorkGoalQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectWorkGoalVO;
import com.yunya.feign.clinic_base.domain.vo.TargetOfMonthVO;
import com.yunya.feign.report.domain.query.SpecialistProjectTargetCompletedInfoQuery;
import com.yunya.feign.report.domain.vo.SpecialistProjectCompletedInfoVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.clinic_base.biz.SpecialistProjectTargetBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简介: 专科项目数量分解管理控制层
 *
 * @author: chow
 * @date: 2020/12/23 10:54
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "专科项目数量管理（分解、修改、删除、查询）")
@RestController
@RequestMapping("special")
public class SpecialistProjectTargetController {

  /** 业务目标 */
  private final SpecialistProjectTargetBiz specialistProjectTargetBiz;

  public SpecialistProjectTargetController(SpecialistProjectTargetBiz specialistProjectTargetBiz) {
    this.specialistProjectTargetBiz = specialistProjectTargetBiz;
  }

  /**
   * 根据条件查询专科数量目标
   *
   * @param query 查询条件
   * @return List<SpecialistProjectTargetOfMonthVO>
   */
  @ApiOperation("根据条件查询专科数量目标")
  @PostMapping(value = "/list", name = "根据条件查询专科数量目标")
  public ResponseResult<List<TargetOfMonthVO>> findSpecialistProjectTargetList(
      @RequestBody @Validated SpecialistProjectTargetQuery query) {
    List<TargetOfMonthVO> resultList =
        specialistProjectTargetBiz.findSpecialistProjectTargetList(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 保存专科数量目标
   *
   * @param model 新增参数
   * @return void
   */
  @CurrentUser
  @ApiOperation("保存专科数量目标")
  @PostMapping(value = "/save", name = "保存专科数量目标")
  public ResponseResult<T> saveSpecialistProjectTarget(
      @RequestBody @Validated SpecialistProjectTargetModel model) {
    specialistProjectTargetBiz.saveOrUpdate(model);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊业务目标列表
   *
   * @param query 查询条件
   * @return
   */
  @Deprecated
  @ApiOperation("公司端报表-报表统计-运营报表-工作目标-专科数量目标")
  @PostMapping(value = "/goal/list", name = "根据条件查询门诊专科数量目标列表")
  public ResponseResult<PageInfo<SpecialistProjectWorkGoalVO>> specialistProjectWorkGoalList(
      @RequestBody @Validated BusinessWorkGoalQuery query) {
    PageInfo<SpecialistProjectWorkGoalVO> pageInfo =
        specialistProjectTargetBiz.findSpecialistProjectWorkGoalList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询门诊业务目标列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-工作目标-专科数量目标")
  @PostMapping(value = "/work/goal/list", name = "根据条件查询门诊专科数量目标列表")
  public ResponseResult<PageInfo<SpecialistProjectWorkGoalVO>> specialistProjectWorkGoalList(
      @RequestBody @Validated SpecialistProjectWorkGoalQuery query) {
    PageInfo<SpecialistProjectWorkGoalVO> pageInfo =
        specialistProjectTargetBiz.findSpecialistProjectWorkGoalList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出门诊业务目标列表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-工作目标-专科数量目标-导出")
  @PostMapping(value = "/goal/list/export", name = "根据条件导出门诊专科数量目标列表")
  public ResponseResult<T> exportSpecialistProjectWorkGoalList(
      HttpServletResponse response, @RequestBody @Validated SpecialistProjectWorkGoalQuery query)
      throws IOException {
    specialistProjectTargetBiz.exportSpecialistProjectWorkGoalList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询专科数量完成情况
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-运营BI-专科数量完成情况")
  @PostMapping(value = "/specialist/completed", name = "根据条件查询专科数量完成情况")
  public ResponseResult<List<SpecialistProjectCompletedInfoVO>>
      specialistProjectTargetCompletedInfo(
          @RequestBody @Validated SpecialistProjectTargetCompletedInfoQuery query) {
    List<SpecialistProjectCompletedInfoVO> resultList =
        specialistProjectTargetBiz.findSpecialistProjectTargetCompletedInfo(query);
    return ResponseUtil.success(resultList);
  }
}
