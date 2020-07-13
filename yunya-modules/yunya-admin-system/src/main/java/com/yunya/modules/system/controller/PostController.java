package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.PostBiz;
import com.yunya.modules.system.entity.Post;
import com.yunya.modules.system.form.PostForm;
import com.yunya.modules.system.form.query.PostQueryForm;
import com.yunya.modules.system.vo.PostVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简单介绍:</br> 岗位控制层
 *
 * @author: chow
 * @date: 2020/6/3 11:55
 * @description:
 * @since: 1.0.0
 */
@Api(value = "岗位管理", description = "岗位增删改查")
@RestController
@RequestMapping("post")
public class PostController {
  /** 注入对象 */
  private final PostBiz postBiz;

  public PostController(PostBiz postBiz) {
    this.postBiz = postBiz;
  }

  /**
   * 根据条件查询岗位列表
   *
   * @param queryForm 参数封装
   * @return list
   */
  @ApiOperation("根据条件查询岗位列表（可分页）")
  @PostMapping("/list")
  public ResponseResult findList(@RequestBody @Validated PostQueryForm queryForm) {
    PageInfo<PostVO> resultList = postBiz.findAll(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 新增岗位
   *
   * @param resource 参数封装
   * @return map
   */
  @ApiOperation("新增岗位")
  @PostMapping("/add")
  public ResponseResult add(@RequestBody @Validated Post resource) {
    postBiz.add(resource);
    return ResponseUtil.success();
  }

  /**
   * 编辑岗位
   *
   * @param id 岗位ID
   * @param form 参数封装
   * @return map
   */
  @ApiOperation("编辑岗位")
  @ApiImplicitParam(
      name = "id",
      value = "岗位ID",
      dataType = "int",
      required = true,
      paramType = "path")
  @PutMapping("/edit/{id}")
  public ResponseResult edit(@PathVariable Integer id, @RequestBody @Validated PostForm form) {
    postBiz.edit(id, form);
    return ResponseUtil.success();
  }

  /**
   * 删除岗位
   *
   * @param id 岗位ID
   * @return map
   */
  @ApiOperation("删除岗位")
  @ApiImplicitParam(
      name = "id",
      value = "岗位ID",
      dataType = "int",
      required = true,
      paramType = "path")
  @DeleteMapping("/delete/{id}")
  public ResponseResult delete(@PathVariable Integer id) {
    postBiz.deletePost(id);
    return ResponseUtil.success();
  }
}
