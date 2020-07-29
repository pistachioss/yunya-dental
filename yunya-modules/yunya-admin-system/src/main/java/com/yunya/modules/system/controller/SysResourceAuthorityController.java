package com.yunya.modules.system.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.SysResourceAuthorityBiz;
import com.yunya.modules.system.domain.form.ResourceAuthorityForm;
import com.yunya.modules.system.domain.form.SysPostResourceForm;
import com.yunya.modules.system.vo.SysResourceAuthorityVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 简单介绍: 资源权限控制器
 *
 * @author: chow
 * @date: 2020/6/30 17:22
 * @description:
 * @since: 1.0.0
 */
@Api(value = "岗位资源权限", description = "岗位资源权限配置管理")
@RestController
@RequestMapping("authority")
public class SysResourceAuthorityController {

  /** 注入对象 */
  private final SysResourceAuthorityBiz sysResourceAuthorityBiz;

  public SysResourceAuthorityController(SysResourceAuthorityBiz sysResourceAuthorityBiz) {
    this.sysResourceAuthorityBiz = sysResourceAuthorityBiz;
  }

  /**
   * 添加岗位权限
   *
   * @param resource 参数封装
   * @return map
   */
  @CurrentUser
  @ApiOperation("新增岗位资源权限")
  @PostMapping("/add")
  public ResponseResult add(@RequestBody @Validated SysPostResourceForm resource) {
    sysResourceAuthorityBiz.add(resource);
    return ResponseUtil.success();
  }

  /**
   * 根据岗位ID查询岗位的资源权限列表
   *
   * @param resourceAuthorityForm 参数封装
   * @return map
   */
  @ApiOperation("根据岗位ID查询岗位的资源权限列表")
  @PostMapping("/list")
  public ResponseResult findList(
      @RequestBody @Validated ResourceAuthorityForm resourceAuthorityForm) {
    List<SysResourceAuthorityVO> resultList =
        sysResourceAuthorityBiz.findResourceAuthorityList(resourceAuthorityForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 删除岗位权限
   *
   * @param resourceForm 参数封装
   * @return map
   */
  @ApiOperation("删除岗位资源权限")
  @PostMapping("/delete")
  public ResponseResult delete(@RequestBody @Validated SysPostResourceForm resourceForm) {
    sysResourceAuthorityBiz.deleteResourceAuthority(resourceForm);
    return ResponseUtil.success();
  }
}
