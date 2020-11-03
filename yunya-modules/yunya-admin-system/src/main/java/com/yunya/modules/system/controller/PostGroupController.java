package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.PostGroup;
import com.yunya.modules.system.biz.PostGroupBiz;
import com.yunya.modules.system.domain.form.PostGroupForm;
import com.yunya.modules.system.domain.model.PostGroupModel;
import com.yunya.modules.system.domain.query.PostGroupQueryForm;
import com.yunya.modules.system.vo.PostGroupVO;
import com.yunya.modules.system.vo.tree.PostGroupTreeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
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
  public ResponseResult<PostGroup> findById(@PathVariable(value = "id") Integer id) {
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
  public ResponseResult<PageInfo<PostGroupVO>> findList(
      @RequestBody @Validated PostGroupQueryForm queryForm) {
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
  public ResponseResult<List<PostGroupTreeVO>> getTree() {
    List<PostGroupTreeVO> resultList = postGroupBiz.initPostGroupTree();
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增岗位分组
   *
   * @param resource 参数封装
   * @return map
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("新增岗位分组")
  @PostMapping("/group/add")
  public ResponseResult<T> add(@RequestBody @Validated PostGroupModel resource) {
    postGroupBiz.add(resource);
    return ResponseUtil.success(null);
  }

  /**
   * 编辑岗位组
   *
   * @param id 岗位组ID
   * @param form 参数封装
   * @return map
   */
  @CurrentUser
  @ApiOperation("修改岗位组")
  @ApiImplicitParam(
      name = "id",
      value = "岗位组ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @PutMapping("/group/edit/{id}")
  public ResponseResult<T> edit(
      @PathVariable(value = "id") Integer id, @RequestBody @Validated PostGroupForm form) {
    postGroupBiz.edit(id, form);
    return ResponseUtil.success(null);
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
  public ResponseResult<T> delete(@PathVariable(value = "id") Integer id) {
    postGroupBiz.deletePostGroup(id);
    return ResponseUtil.success(null);
  }
}
