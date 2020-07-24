package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.MemberTypeBiz;
import com.yunya.modules.system.form.MemberTypeForm;
import com.yunya.modules.system.form.MemberTypeModel;
import com.yunya.modules.system.form.query.MemberTypeQueryForm;
import com.yunya.modules.system.vo.MemberTypeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介: 会员卡控制器
 *
 * @author: chow
 * @date: 2020/7/22 15:42
 * @description:
 * @since: 1.0.0
 */
@Api(value = "会员卡类型管理", description = "会员卡增删改查")
@RestController
@RequestMapping("memberCard")
public class MemberTypeController {

  /** 注入对象 */
  private final MemberTypeBiz memberTypeBiz;

  public MemberTypeController(MemberTypeBiz memberTypeBiz) {
    this.memberTypeBiz = memberTypeBiz;
  }

  /**
   * 根据ID查询会员卡类型信息
   *
   * @param id 会员卡类型ID
   * @return
   */
  @ApiOperation("根据ID查询会员卡类型信息")
  @GetMapping("/one/{id}")
  public ResponseResult findById(@PathVariable("id") Integer id) {
    MemberTypeVO resultData = memberTypeBiz.findById(id);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询会员卡类型列表
   *
   * @param queryForm 查询参数
   * @return
   */
  @ApiOperation("根据条件查询会员卡类型列表(可分页)")
  @PostMapping("/list")
  public ResponseResult findList(@RequestBody MemberTypeQueryForm queryForm) {
    PageInfo<MemberTypeVO> resultLIst = memberTypeBiz.findList(queryForm);
    return ResponseUtil.success(resultLIst);
  }

  /**
   * 新增会员类型方式
   *
   * @param model 参数模型
   * @return
   */
  @PostMapping("/save")
  @ApiOperation("新增会员类型方式")
  public ResponseResult save(@RequestBody @Validated MemberTypeModel model) {
    memberTypeBiz.add(model);
    return ResponseUtil.success();
  }

  /**
   * 修改会员卡信息
   *
   * @param id 会员卡ID
   * @param form 参数封装
   * @return
   */
  @ApiOperation("修改会员卡类型信息")
  @PutMapping("/edit/{id}")
  public ResponseResult modify(
      @PathVariable("id") Integer id, @RequestBody @Validated MemberTypeForm form) {
    memberTypeBiz.modify(id, form);
    return ResponseUtil.success();
  }

  /**
   * 根据ID删除会员卡类型
   *
   * @param id 会员卡类型ID
   * @return
   */
  @ApiOperation("根据ID删除会员卡类型")
  @DeleteMapping("/delete/{id}")
  public ResponseResult delete(@PathVariable("id") Integer id) {
    memberTypeBiz.deleteMemberTypeById(id);
    return ResponseUtil.success();
  }
}
