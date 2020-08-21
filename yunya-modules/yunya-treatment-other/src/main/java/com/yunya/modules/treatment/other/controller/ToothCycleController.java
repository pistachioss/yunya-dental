package com.yunya.modules.treatment.other.controller;

import com.yunya.feign.treatment_other.domain.form.ToothCycleForm;
import com.yunya.feign.treatment_other.domain.model.ToothCycleModel;
import com.yunya.feign.treatment_other.domain.query.ToothCycleQuery;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.treatment_other.ToothCycle;
import com.yunya.modules.treatment.other.biz.ToothCycleBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 简介: 就诊牙周期模块管理
 *
 * @author: Zkq
 * @date: 2020/8/20 14:53
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "牙周期模块(增删改查)")
@RestController
@RequestMapping("cycle")
public class ToothCycleController {


    @Autowired
    private ToothCycleBiz toothCycleBiz;
    /**
     * 查询牙周期列表
     *
     * @param
     * @return
     */
    @ApiOperation("查询牙周期列表")
    @PostMapping("/findCycleList")
    public ResponseResult findCycleList(ToothCycleQuery cycle){
        List<ToothCycle> cycleList = toothCycleBiz.findCycleList(cycle);
        return ResponseUtil.success(cycleList);
    }
    /**
     * 添加牙周期记录
     *
     * @param
     * @return
     */
    @CurrentUser
    @ApiOperation("添加牙周期记录")
    @PostMapping("/add")
    public ResponseResult add(ToothCycleModel cycle){
        Integer userID = Integer.valueOf(BaseContextHandler.getUserID());
        cycle.setCrtId(userID);
        toothCycleBiz.add(cycle);
        return ResponseUtil.success();
    }
    /**
     * 修改牙周期记录
     *
     * @param
     * @return
     */
    @CurrentUser
    @ApiOperation("修改牙周期记录")
    @PostMapping("/upd")
    public ResponseResult upd(ToothCycleForm cycle){
        Integer userID = Integer.valueOf(BaseContextHandler.getUserID());
        cycle.setUpdId(userID);
        toothCycleBiz.upd(cycle);
        return ResponseUtil.success();
    }
    /**
     * 删除牙周期记录
     *
     * @param
     * @return
     */
    @ApiOperation("删除牙周期记录")
    @PostMapping("/del")
    public ResponseResult upd(Integer id){
        toothCycleBiz.del(id);
        return ResponseUtil.success();
    }

}
