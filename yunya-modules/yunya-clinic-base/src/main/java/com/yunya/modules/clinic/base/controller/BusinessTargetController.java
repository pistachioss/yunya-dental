package com.yunya.modules.clinic.base.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.form.SpecializedSubjectProjectForm;
import com.yunya.feign.cash_balance.model.BusinessTargetModel;
import com.yunya.feign.cash_balance.model.SpecializedSubjectProjectModel;
import com.yunya.feign.cash_balance.query.*;
import com.yunya.feign.cash_balance.vo.*;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.clinic_base.BusinessTarget;
import com.yunya.models.clinic_base.SpecializedSubjectProject;
import com.yunya.modules.clinic.base.biz.BusinessTargetBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 简介: 现金模块管理
 *
 * @author: Zkq
 * @date: 2020/8/20 14:53
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "业务目标模块增删改查")
@RestController
@RequestMapping("business")
public class BusinessTargetController {

    @Resource
    private BusinessTargetBiz businessTargetBiz;

    /**
     * 业务目标分页列表
     *
     * @param
     * @return
     */
    @ApiOperation("业务目标模块列表")
    @PostMapping("/findBusinessTargetByPage")
    public ResponseResult<PageInfo<BusinessTargetVo>> findBusinessTargetByPage(@Valid @RequestBody BusinessTargetQuery query){
        PageInfo<BusinessTargetVo> page = businessTargetBiz.findBusinessTargetByPage(query);
        return ResponseUtil.success(page);
    }

    /**
     * 添加业务目标
     *
     * @param
     * @return
     */
    @ApiOperation("添加业务目标")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult add(@Valid @RequestBody List<BusinessTargetModel> model){
        Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
        for (BusinessTargetModel data : model) {
            BusinessAddOrUpdQuery businessAddOrUpdQuery = new BusinessAddOrUpdQuery();
            BusinessTarget businessTarget = new BusinessTarget();
            BeanUtils.copyProperties(data, businessAddOrUpdQuery);
            BeanUtils.copyProperties(data, businessTarget);
            List<BusinessTargetOrVo> businessTargetOrVo = businessTargetBiz.businessAddOrUpd(businessAddOrUpdQuery);
            if (businessTargetOrVo!=null && !businessTargetOrVo.isEmpty()) {
                Integer id = businessTargetOrVo.get(0).getId();
                businessTarget.setUpdId(crtId);
                businessTarget.setId(id);
                businessTargetBiz.upd(businessTarget);
            } else {
                businessTarget.setCrtId(crtId);
                businessTargetBiz.add(businessTarget);
            }
        }
        return ResponseUtil.success();
    }

    /**
     * 多选门诊查询结果
     *
     * @param
     * @return
     */
    @ApiOperation("多选门诊查询结果")
    @PostMapping("/findAllData")
    public ResponseResult findAllData(@Valid @RequestBody BusinessTargetTotalQuery businessTargetTotalQuery){
       BusinessTargetTotalVo businessTargetTotalVo =  businessTargetBiz.findAllData(businessTargetTotalQuery);
        return ResponseUtil.success(businessTargetTotalVo);
    }

    /**
     * 回显公司目标
     *
     * @param
     * @return
     */
    @ApiOperation("回显公司目标")
    @PostMapping("/findDataById")
    public ResponseResult findDataById(@Valid @RequestBody BusinessTargerByDataQuery businessTargerByDataQuery){
        BusinessTargetByIdVo businessTargetByIdVo = businessTargetBiz.findDataById(businessTargerByDataQuery);
        return ResponseUtil.success(businessTargetByIdVo);
    }
}
