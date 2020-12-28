package com.yunya.modules.clinic_base.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.domain.model.BusinessTargetModel;
import com.yunya.feign.clinic_base.domain.query.BusinessTargetQuery;
import com.yunya.feign.clinic_base.domain.query.WorkGoalQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessWorkGoalVO;
import com.yunya.feign.clinic_base.domain.vo.TargetOfMonthVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.clinic_base.biz.BusinessTargetBiz;
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
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 业务目标控制器
 *
 * @author: chow
 * @date: 2020/12/22 16:08
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "业务目标分解")
@RestController
@RequestMapping("business")
public class BusinessTargetController implements Serializable {

  /** 业务目标 */
  @Autowired private BusinessTargetBiz businessTargetBiz;

  /**
   * 根据条件查询业务目标
   *
   * @param query 查询条件
   * @return List<BusinessTargetOfMonthVO>
   */
  @ApiOperation("根据条件查询业务目标")
  @PostMapping(value = "/list", name = "根据条件查询业务目标")
  public ResponseResult<List<TargetOfMonthVO>> businessTargetList(
      @RequestBody @Validated BusinessTargetQuery query) {
    List<TargetOfMonthVO> resultList = businessTargetBiz.findBusinessTargetList(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增业务目标
   *
   * @param model 新增参数
   * @return void
   */
  @CurrentUser
  @ApiOperation("新增业务目标")
  @PostMapping(value = "/save", name = "新增业务目标")
  public ResponseResult<T> saveBusinessTarget(@RequestBody @Validated BusinessTargetModel model) {
    businessTargetBiz.saveOrUpdate(model);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊业务目标列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-工作目标-业务目标")
  @PostMapping(value = "/goal/list", name = "根据条件查询门诊业务目标列表")
  public ResponseResult<PageInfo<BusinessWorkGoalVO>> businessWorkGoalList(
      @RequestBody @Validated WorkGoalQuery query) {
    PageInfo<BusinessWorkGoalVO> pageInfo = businessTargetBiz.findBusinessWorkGoalList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出门诊业务目标列表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-工作目标-业务目标-导出")
  @PostMapping(value = "/goal/list/export", name = "根据条件导出门诊业务目标列表")
  public ResponseResult<T> exportBusinessWorkGoalList(
      HttpServletResponse response, @RequestBody @Validated WorkGoalQuery query)
      throws IOException {
    businessTargetBiz.exportBusinessWorkGoalList(response, query);
    return ResponseUtil.success(null);
  }
}
