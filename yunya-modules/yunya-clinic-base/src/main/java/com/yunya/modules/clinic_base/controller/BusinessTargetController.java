package com.yunya.modules.clinic_base.controller;

import com.yunya.feign.clinic_base.domain.model.BusinessTargetModel;
import com.yunya.feign.clinic_base.domain.query.BusinessTargetQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessTargetOfMonthVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.clinic_base.biz.BusinessTargetBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseResult<List<BusinessTargetOfMonthVO>> businessTargetList(
      @RequestBody @Validated BusinessTargetQuery query) {
    List<BusinessTargetOfMonthVO> resultList = businessTargetBiz.findBusinessTargetList(query);
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
}
