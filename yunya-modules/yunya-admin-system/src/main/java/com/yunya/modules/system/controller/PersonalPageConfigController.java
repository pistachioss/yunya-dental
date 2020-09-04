package com.yunya.modules.system.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.PersonalPageConfigBiz;
import com.yunya.modules.system.domain.model.PersonalPageConfigModel;
import com.yunya.modules.system.vo.PersonalPageFieldVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简介: 个人页面字段配置控制器
 *
 * @author: chow
 * @date: 2020/8/31 20:41
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "个人页面字段显示配置管理（新增、修改）")
@RestController
@RequestMapping("page")
public class PersonalPageConfigController {

  /** 注入对象 */
  @Autowired private PersonalPageConfigBiz personalPageConfigBiz;

  /**
   * 根据用户ID、页面名称查询用户页面自定义字段列表
   *
   * @param userId 用户ID
   * @param pageName 页面名称
   * @return
   */
  @ApiOperation("个人页面字段展示列表查询")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
    @ApiImplicitParam(name = "pageName", value = "页面", required = true)
  })
  @GetMapping(value = "/field/list/{userId}/{pageName}")
  public ResponseResult findFieldList(
      @PathVariable(value = "userId") Integer userId,
      @PathVariable(value = "pageName") String pageName) {
    List<PersonalPageFieldVO> resultList = personalPageConfigBiz.findFieldList(userId, pageName);
    return ResponseUtil.success(resultList);
  }

  /**
   * 保存个人页面字段配置信息
   *
   * @param model 配置参数
   * @return
   */
  @CurrentUser
  @ApiOperation("保存个人页面字段显示配置信息")
  @PostMapping(value = "/config/save")
  public ResponseResult savePersonalPageConfig(
      @RequestBody @Validated PersonalPageConfigModel model) {
    personalPageConfigBiz.save(model);
    return ResponseUtil.success();
  }
}
