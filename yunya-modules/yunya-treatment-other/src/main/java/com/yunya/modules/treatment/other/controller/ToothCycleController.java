package com.yunya.modules.treatment.other.controller;

import com.yunya.feign.auth.RemoteServiceAuthFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment_other.domain.form.ToothCycleForm;
import com.yunya.feign.treatment_other.domain.model.ToothCycleModel;
import com.yunya.feign.treatment_other.domain.query.ToothCycleQuery;
import com.yunya.feign.treatment_other.domain.vo.ToothCycleVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.treatment_other.ToothCycle;
import com.yunya.modules.treatment.other.biz.ToothCycleBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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

    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    /**
     * 查询牙周期列表
     *
     * @param
     * @return
     */
    @ApiOperation("查询牙周期列表")
    @PostMapping("/findCycleList")
    public ResponseResult findCycleList(ToothCycleQuery cycle){
        List<ToothCycleVo> cycleList = toothCycleBiz.findCycleList(cycle);
        for (ToothCycleVo cycleData: cycleList) {
            SysUserInfoDetail dentistData = remoteSystemServiceFeign.
                    findSysUserEmployeeInfoByUserId(cycleData.getDentistId());
            cycleData.setDentistName(dentistData.getName());
        }
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
    public ResponseResult add(@Valid @RequestBody ToothCycleModel cycle){
        Integer userID = Integer.valueOf(BaseContextHandler.getUserID());
        ToothCycle toothCycle = new ToothCycle();
        BeanUtils.copyProperties(cycle,toothCycle);
        toothCycle.setCrtId(userID);
        toothCycleBiz.add(toothCycle);
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
    public ResponseResult upd(@Valid @RequestBody ToothCycleForm cycle){
        Integer userID = Integer.valueOf(BaseContextHandler.getUserID());
        ToothCycle toothCycle = new ToothCycle();
        BeanUtils.copyProperties(cycle,toothCycle);
        toothCycle.setUpdId(userID);
        toothCycleBiz.upd(toothCycle);
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
