package com.yunya.modules.clinic_base.controller;

import com.yunya.feign.clinic_base.domain.model.SpecialistProjectTargetModel;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectTargetQuery;
import com.yunya.feign.clinic_base.domain.query.WorkGoalQuery;
import com.yunya.feign.clinic_base.domain.vo.TargetOfMonthVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.clinic_base.biz.SpecialistProjectTargetBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 简介: 专科项目数量分解管理控制层
 *
 * @author: chow
 * @date: 2020/12/23 10:54
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "专科项目数量分解")
@RestController
@RequestMapping("special")
public class SpecialistProjectTargetController {

  /** 业务目标 */
  @Autowired private SpecialistProjectTargetBiz specialistProjectTargetBiz;

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
  @ApiOperation("公司端报表-报表统计-运营报表-工作目标-专科数量目标")
  @PostMapping(value = "/goal/list", name = "根据条件查询门诊专科数量目标列表")
  public ResponseResult<T> specialistProjectWorkGoalList(@RequestBody @Validated WorkGoalQuery query) {

    return ResponseUtil.success(null);
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
          HttpServletResponse response, @RequestBody @Validated WorkGoalQuery query) {

    return ResponseUtil.success(null);
  }
}
