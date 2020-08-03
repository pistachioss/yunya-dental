package com.yunya.modules.appointment.controller;

import com.yunya.framework.common.annation.CurrentUser;
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
import java.util.Map;

/**
 * 预约分解
 *
 * @author yunya-lihuibin
 * @create 2020-07-30 14:15
 * @update yunya-lihuibin    2020-07-30    新建
 */
@RestController
@RequestMapping("appointment/appointSplit")
@Api(tags = "预约分解控制器")
public class AppointmentSplitController {

    @Autowired
    private AppointmentSplitBiz appointSpitBiz;

    /**
     * 添加预约分解
     * @param splitList 数据列表
     * @return
     */
    @ApiOperation(value = "添加预约分解")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult addSplit(@Validated @RequestBody AppointmentSplitModel splitList) throws ParseException {
        Map<String, Object> result = appointSpitBiz.insertSplit(splitList);
        return ResponseUtil.success(result);
    }

    /**
     * 删除分解预约
     * @param delForm  条件列表
     * @return
     */
    @ApiOperation(value = " 删除分解预约")
    @PostMapping("/del")
    public ResponseResult delSplit(@Validated @RequestBody AppointmentSplitDelForm delForm){
        Map<String, Object> result = appointSpitBiz.delSplit(delForm);
        return ResponseUtil.success(result);
    }

    /**
     * 根据条件查询分解预约
     * @param query 条件列表
     * @return
     */
    @ApiOperation(value = "根据条件查询分解预约")
    @PostMapping("/findAll")
    public ResponseResult findAppointmentSplitByExample(@Validated @RequestBody AppointmentSplitQuery query){
        List<AppointmentSplitVo> result = appointSpitBiz.findAppointmentSplitByExample(query);
        return ResponseUtil.success(result);
    }



}
