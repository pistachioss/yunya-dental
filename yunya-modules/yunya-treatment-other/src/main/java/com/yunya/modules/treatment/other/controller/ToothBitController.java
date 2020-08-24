package com.yunya.modules.treatment.other.controller;

import com.yunya.feign.treatment_other.domain.form.ToothBitDataForm;
import com.yunya.feign.treatment_other.domain.model.ToothBitDataModel;
import com.yunya.feign.treatment_other.domain.query.ToothBitDataQuery;
import com.yunya.feign.treatment_other.domain.vo.ToothBitDataVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.treatment_other.ToothBitData;
import com.yunya.modules.treatment.other.biz.ToothBitDataBiz;
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
@Api(tags = "牙根尖模块(增删改查)")
@RestController
@RequestMapping("bit")
public class ToothBitController {


    @Autowired
    private ToothBitDataBiz toothBitDataBiz;
    /**
     * 查询牙根尖
     *
     * @param
     * @return
     */
    @ApiOperation("查询牙周模块")
    @PostMapping("/findBitDataList")
    public ResponseResult findBitDataList(ToothBitDataQuery query){
        List<ToothBitDataVo> bitDataList = toothBitDataBiz.findBitDataList(query);
        return ResponseUtil.success(bitDataList);
    }
    /**
     * 添加牙根尖记录
     *
     * @param
     * @return
     */
    @CurrentUser
    @ApiOperation("添加牙根尖记录")
    @PostMapping("/add")
    public ResponseResult add(@Valid @RequestBody ToothBitDataModel model){
        Integer toothBit = model.getToothBit();
        Integer userID = Integer.valueOf(BaseContextHandler.getUserID());
        ToothBitData toothBitData = new ToothBitData();
        BeanUtils.copyProperties(model,toothBitData);
        toothBitData.setCrtId(userID);
        toothBitDataBiz.add(toothBitData);
        return ResponseUtil.success();
    }
    /**
     * 修改牙周期记录
     *
     * @param
     * @return
     */
    @CurrentUser
    @ApiOperation("修改牙根尖记录")
    @PostMapping("/upd")
    public ResponseResult upd(@Valid @RequestBody ToothBitDataForm form){
        Integer userID = Integer.valueOf(BaseContextHandler.getUserID());
        ToothBitData toothBitData = new ToothBitData();
        BeanUtils.copyProperties(form,toothBitData);
        toothBitData.setUpdId(userID);
        toothBitDataBiz.upd(toothBitData);
        return ResponseUtil.success();
    }
    /**
     * 删除牙周期记录
     *
     * @param
     * @return
     */
    @ApiOperation("删除牙根尖记录")
    @PostMapping("/del")
    public ResponseResult upd(Integer id){
        toothBitDataBiz.del(id);
        return ResponseUtil.success();
    }

}
