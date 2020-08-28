package com.yunya.modules.clinic.base.controller;


import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.model.BusinessTargetModel;
import com.yunya.feign.cash_balance.model.SpecialistTargetModel;
import com.yunya.feign.cash_balance.query.BusinessTargetQuery;
import com.yunya.feign.cash_balance.query.SpecialistTargetQuery;
import com.yunya.feign.cash_balance.vo.BusinessTargetTotalVo;
import com.yunya.feign.cash_balance.vo.BusinessTargetVo;
import com.yunya.feign.cash_balance.vo.SpecialistTargetVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.clinic.base.biz.SpecialistTargetBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
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
    @PostMapping("/findspecialistTargetByPage")
    public ResponseResult<PageInfo<SpecialistTargetVo>> findspecialistTargetByPage(@Valid @RequestBody SpecialistTargetQuery query){
        PageInfo<SpecialistTargetVo> page = specialistTargetBiz.findspecialistTargetByPage(query);
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
    public ResponseResult add(@Valid @RequestBody List<SpecialistTargetModel> model){
        specialistTargetBiz.add(model);
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
    public ResponseResult findAllData(String ids){
        BusinessTargetTotalVo businessTargetTotalVo =  specialistTargetBiz.findAllData(ids);
        return ResponseUtil.success(businessTargetTotalVo);
    }

}
