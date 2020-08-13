package com.yunya.modules.treatment.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.model.RegisteredModel;
import com.yunya.feign.treatment.domain.query.RegisteredQueryForm;
import com.yunya.feign.treatment.domain.vo.WaitingPatientInfoVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.RegisteredBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
  public ResponseResult add(@RequestBody @Validated RegisteredModel model) {
    registeredBiz.save(model);
    return ResponseUtil.success();
  }

  /**
   * 根据挂号ID取消患者挂号
   *
   * @param id 挂号ID
   * @return
   */
  @ApiOperation("根据挂号ID取消患者挂号")
  @GetMapping("/cancel/{id}")
  public ResponseResult cancel(@PathVariable(value = "id") Integer id) {
    registeredBiz.cancelRegistered(id);
    return ResponseUtil.success();
  }

  /**
   * 根据条件查询门诊候诊中患者（挂号）列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询门诊候诊中患者列表（可分页）")
  @PostMapping("/list")
  public ResponseResult findList(@RequestBody @Validated RegisteredQueryForm queryForm) {
    PageInfo<WaitingPatientInfoVO> resultList = registeredBiz.findRegisteredList(queryForm);
    return ResponseUtil.success(resultList);
  }
}
