package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.PostGroup;
import com.yunya.modules.system.biz.PostGroupBiz;
import com.yunya.modules.system.form.PostGroupForm;
import com.yunya.modules.system.form.query.PostGroupQueryForm;
import com.yunya.modules.system.vo.PostGroupVO;
import com.yunya.modules.system.vo.tree.PostGroupTreeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简单介绍:</br> 岗位组控制层
 *
 * @author: chow
 * @date: 2020/6/10 10:44
 * @description:
 * @since: 1.0.0
 */
@Api(value = "岗位组管理", description = "岗位组管理（岗位组的增删改查功能）")
@RestController
@RequestMapping("post")
public class PostGroupController {

  /** 注入对象 */
  private final PostGroupBiz postGroupBiz;

  public PostGroupController(PostGroupBiz postGroupBiz) {
    this.postGroupBiz = postGroupBiz;
  }

  /**
   * 根据ID查询岗位组信息
   *
   * @param id 岗位组ID
   * @return obj
   */
  @ApiOperation("根据岗位组ID查询岗位组信息")
  @GetMapping("/group/one/{id}")
  public ResponseResult findById(@PathVariable Integer id) {
    PostGroup postGroup = postGroupBiz.selectById(id);
    return ResponseUtil.success(postGroup);
  }

  /**
   * 根据条件查询岗位组列表（可分页）
   *
   * @param queryForm 参数封装
   * @return list
   */
  @ApiOperation("根据条件查询岗位组列表（可分页）")
  @PostMapping("/group/list")
  public ResponseResult findList(@RequestBody @Validated PostGroupQueryForm queryForm) {
    PageInfo<PostGroupVO> resultList = postGroupBiz.findList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 获取岗位组树列表
   *
   * @return list
   */
  @ApiOperation("获取岗位组树列表")
  @GetMapping("/group/tree")
  public ResponseResult getTree() {
    List<PostGroupTreeVO> resultList = postGroupBiz.initPostGroupTree();
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增岗位分组
   *
   * @param resource 参数封装
   * @return map
   */
  @ApiOperation("新增岗位分组")
  @PostMapping("/group/add")
  public ResponseResult add(@RequestBody @Validated PostGroup resource) {
    postGroupBiz.add(resource);
    return ResponseUtil.success();
  }

  /**
   * 编辑岗位组
   *
   * @param id 岗位组ID
   * @param form 参数封装
   * @return map
   */
  @ApiOperation("修改岗位组")
  @ApiImplicitParam(
      name = "id",
      value = "岗位组ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @PutMapping("/group/edit/{id}")
  public ResponseResult edit(@PathVariable Integer id, @RequestBody @Validated PostGroupForm form) {
    postGroupBiz.edit(id, form);
    return ResponseUtil.success();
  }

  /**
   * 删除岗位组信息
   *
   * @param id 岗位组ID
   * @return map
   */
  @ApiOperation("根据岗位组ID删除岗位组信息")
  @ApiImplicitParam(
      name = "id",
      value = "岗位组ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @DeleteMapping("/group/delete/{id}")
  public ResponseResult delete(@PathVariable Integer id) {
    postGroupBiz.deletePostGroup(id);
    return ResponseUtil.success();
  }
}
