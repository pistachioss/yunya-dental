package com.yunya.modules.clinic.base.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.form.SpecializedSubjectProjectForm;
import com.yunya.feign.cash_balance.model.SpecializedSubjectProjectModel;
import com.yunya.feign.cash_balance.query.SpecializedSubjectProjectQuery;
import com.yunya.feign.cash_balance.vo.SpecializedSubjectProjectVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.clinic_base.SpecializedSubjectProject;
import com.yunya.modules.clinic.base.biz.SpecializedSubjectProjectBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 简介: 专科项目设置管理
 *
 * @author: Zkq
 * @date: 2020/8/20 14:53
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "专科项目设置管理增删改查")
@RestController
@RequestMapping("specialized")
public class SpecializedSubjectProjectController {

    @Resource
    private SpecializedSubjectProjectBiz specializedSubjectProjectBiz;

    /**
     * 专科项目设置列表
     *
     * @param
     * @return
     */
    @ApiOperation("专科项目设置列表")
    @PostMapping("/findSpecializedList")
    public ResponseResult<PageInfo<SpecializedSubjectProjectVo>> findSpecializedList(@Valid @RequestBody SpecializedSubjectProjectQuery query){
        PageInfo<SpecializedSubjectProjectVo> page = specializedSubjectProjectBiz.findSpecializedList(query);
        return ResponseUtil.success(page);
    }

    /**
     * 专科项目设置列表
     *
     * @param
     * @return
     */
    @CurrentUser
    @ApiOperation("添加专科项目设置")
    @PostMapping("/add")
    public ResponseResult add(@Valid @RequestBody SpecializedSubjectProjectModel model){
        Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
        SpecializedSubjectProject specializedSubjectProject = new SpecializedSubjectProject();
        specializedSubjectProject.setCrtId(crtId);
        BeanUtils.copyProperties(model,specializedSubjectProject);
        specializedSubjectProjectBiz.add(specializedSubjectProject);
        return ResponseUtil.success();
    }
    /**
     * 专科项目设置列表
     *
     * @param
     * @return
     */
    @CurrentUser
    @ApiOperation("修改专科项目设置")
    @PutMapping ("/upd")
    public ResponseResult upd(@Valid @RequestBody SpecializedSubjectProjectForm form){
        Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
        SpecializedSubjectProject specializedSubjectProject = new SpecializedSubjectProject();
        specializedSubjectProject.setCrtId(crtId);
        BeanUtils.copyProperties(form,specializedSubjectProject);
        specializedSubjectProjectBiz.upd(specializedSubjectProject);
        return ResponseUtil.success();
    }
    /**
     * 专科项目设置列表
     *
     * @param
     * @return
     */
    @ApiOperation("删除专科项目设置")
    @DeleteMapping("/del")
    public ResponseResult del(Integer id){
        specializedSubjectProjectBiz.del(id);
        return ResponseUtil.success();
    }
}
