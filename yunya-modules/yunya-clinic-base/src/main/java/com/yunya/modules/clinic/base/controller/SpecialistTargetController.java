package com.yunya.modules.clinic.base.controller;


import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.model.BusinessTargetModel;
import com.yunya.feign.cash_balance.model.SpecialistTargetModel;
import com.yunya.feign.cash_balance.query.*;
import com.yunya.feign.cash_balance.vo.*;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.clinic_base.BusinessTarget;
import com.yunya.models.clinic_base.SpecialistTarget;
import com.yunya.modules.clinic.base.biz.SpecialistTargetBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 专科数量目标
 *
 * @author: Zkq
 * @date: 2020/8/20 14:53
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "专科数量目标增删改查")
@RestController
@RequestMapping("specialist")
public class SpecialistTargetController {

    @Resource
    private SpecialistTargetBiz specialistTargetBiz;

    /**
     * 业务目标分页列表
     *
     * @param
     * @return
     */
    @ApiOperation("业务目标模块列表")
    @PostMapping("/findSpecialistTargetByPage")
    public ResponseResult<PageInfo<SpecialistTargetVo>> findSpecialistTargetByPage(@Valid @RequestBody SpecialistTargetQuery query){
        PageInfo<SpecialistTargetVo> page = specialistTargetBiz.findSpecialistTargetByPage(query);
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
    public ResponseResult add(@Valid @RequestBody List<SpecialistTargetModel> model){
        Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
        for (SpecialistTargetModel data : model) {
            SpecialistAddOrUpdQuery specialistAddOrUpdQuery = new SpecialistAddOrUpdQuery();
            SpecialistTarget specialistTarget = new SpecialistTarget();
            BeanUtils.copyProperties(data, specialistAddOrUpdQuery);
            BeanUtils.copyProperties(data, specialistTarget);
            List<SpecialistTargetOrVo> specialistTargetOrVo = specialistTargetBiz.specialistAddOrUpd(specialistAddOrUpdQuery);
            if (!specialistTargetOrVo.isEmpty()) {
                Integer id = specialistTargetOrVo.get(0).getId();
                specialistTarget.setUpdId(crtId);
                specialistTarget.setId(id);
                specialistTargetBiz.upd(specialistTarget);
            } else {
                specialistTarget.setCrtId(crtId);
                specialistTargetBiz.add(specialistTarget);
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
        SpecialistTargetTotalVo specialistTargetTotalVo =  specialistTargetBiz.findAllData(businessTargetTotalQuery);
        specialistTargetTotalVo.setComplete(new BigDecimal(500));
        return ResponseUtil.success(specialistTargetTotalVo);
    }

    /**
     * 回显专科目标设置记录
     *
     * @param
     * @return
     */
    @ApiOperation("回显专科目标设置记录")
    @PostMapping("/findDataById")
    public ResponseResult findDataById(SpecialistTargetByDataQuery specialistTargetByDataQuery){
        SpecialistTargetByIdVo specialistTargetByIdVo = specialistTargetBiz.findDataById(specialistTargetByDataQuery);
        return ResponseUtil.success(specialistTargetByIdVo);
    }

}
