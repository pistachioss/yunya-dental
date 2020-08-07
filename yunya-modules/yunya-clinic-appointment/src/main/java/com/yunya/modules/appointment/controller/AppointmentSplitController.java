package com.yunya.modules.appointment.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.AppointmentSplitBiz;
import com.yunya.feign.appointment.domain.form.AppointmentSplitDelForm;
import com.yunya.feign.appointment.domain.model.AppointmentSplitModel;
import com.yunya.feign.appointment.domain.query.AppointmentSplitQuery;
import com.yunya.modules.appointment.vo.AppointmentSplitVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * "时长分解Controller
 *
 * @author yunya-lihuibin
 * @create 2020-07-30 14:15
 * @update yunya-lihuibin    2020-07-30    新建
 */
@RestController
@RequestMapping("appoint_split")
@Api(tags = "时长分解Controller")
public class AppointmentSplitController {

    @Autowired
    private AppointmentSplitBiz appointSpitBiz;

    /**
     * 添加时长分解
     * @param splitModel 数据列表
     * @return
     */
    @ApiOperation(value = "添加时长分解")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult addAppointSplit(@Validated @RequestBody AppointmentSplitModel splitModel) throws ParseException {
        Integer result = appointSpitBiz.insertAppointSplit(splitModel);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"时长分解失败！",null);
        }
        return ResponseUtil.success();
    }

    /**
     * 根据id删除时长分解
     * @param splitId  条件列表
     * @return
     */
    @ApiOperation(value = " 删除时长分解")
    @DeleteMapping("/del/{splitId}")
    public ResponseResult delAppointSplit(@PathVariable("splitId") Integer splitId){
        appointSpitBiz.deleteById(splitId);
        return ResponseUtil.success();
    }

    /**
     * 根据条件查询分解预约
     * @param query 条件列表
     * @return
     */
    @ApiOperation(value = "根据条件查询时长分解")
    @PostMapping("/findAll")
    public ResponseResult findAppointSplitByExample(@Validated @RequestBody AppointmentSplitQuery query){
        List<AppointmentSplitVo> result = appointSpitBiz.findAppointmentSplitByExample(query);
        return ResponseUtil.success(result);
    }

    /**
     * 修改时长分解
     * @param form  时长分解表单
     * @return
     */
    @ApiOperation(value = "修改时长分解")
    @PutMapping("/update")
    @CurrentUser
    public ResponseResult updateAppointSplit(@RequestBody @Validated AppointmentSplitModel form){
        Integer result = appointSpitBiz.updateAppointSplit(form);
        return ResponseUtil.success();
    }





}
