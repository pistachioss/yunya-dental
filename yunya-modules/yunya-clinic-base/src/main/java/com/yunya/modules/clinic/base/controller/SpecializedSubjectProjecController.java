package com.yunya.modules.clinic.base.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.form.SpecializedSubjectProjecForm;
import com.yunya.feign.cash_balance.model.SpecializedSubjectProjecModel;
import com.yunya.feign.cash_balance.query.CashBalanceQuery;
import com.yunya.feign.cash_balance.query.SpecializedSubjectProjecQuery;
import com.yunya.feign.cash_balance.vo.CashBalanceVo;
import com.yunya.feign.cash_balance.vo.SpecializedSubjectProjecVo;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.clinic_base.SpecializedSubjectProjec;
import com.yunya.modules.clinic.base.biz.SpecializedSubjectProjecBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class SpecializedSubjectProjecController {

    @Resource
    private SpecializedSubjectProjecBiz specializedSubjectProjecBiz;


    /**
     * 专科项目设置列表
     *
     * @param
     * @return
     */
    @ApiOperation("专科项目设置列表")
    @PostMapping("/findSpecializedList")
    public ResponseResult<PageInfo<SpecializedSubjectProjecVo>> findSpecializedList(@Valid @RequestBody SpecializedSubjectProjecQuery query){
        PageInfo<SpecializedSubjectProjecVo> page = specializedSubjectProjecBiz.findSpecializedList(query);
        return ResponseUtil.success(page);
    }

    /**
     * 专科项目设置列表
     *
     * @param
     * @return
     */
    @ApiOperation("添加专科项目设置")
    @PostMapping("/add")
    public ResponseResult add(@Valid @RequestBody SpecializedSubjectProjecModel model){
        Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
        SpecializedSubjectProjec specializedSubjectProjec = new SpecializedSubjectProjec();
        specializedSubjectProjec.setCrtId(crtId);
        BeanUtils.copyProperties(model,specializedSubjectProjec);
        specializedSubjectProjecBiz.add(specializedSubjectProjec);
        return ResponseUtil.success();
    }
    /**
     * 专科项目设置列表
     *
     * @param
     * @return
     */
    @ApiOperation("修改专科项目设置")
    @PostMapping("/upd")
    public ResponseResult upd(@Valid @RequestBody SpecializedSubjectProjecForm form){
        Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
        SpecializedSubjectProjec specializedSubjectProjec = new SpecializedSubjectProjec();
        specializedSubjectProjec.setCrtId(crtId);
        BeanUtils.copyProperties(form,specializedSubjectProjec);
        specializedSubjectProjecBiz.upd(specializedSubjectProjec);
        return ResponseUtil.success();
    }
    /**
     * 专科项目设置列表
     *
     * @param
     * @return
     */
    @ApiOperation("删除专科项目设置")
    @PostMapping("/del")
    public ResponseResult del(@Valid @RequestBody int id){
        specializedSubjectProjecBiz.del(id);
        return ResponseUtil.success();
    }


}
