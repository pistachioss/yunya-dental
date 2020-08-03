package com.yunya.modules.appointment.controller;

import com.yunya.feign.appointment.domain.form.AppointSettingForm;
import com.yunya.feign.appointment.domain.model.AppointSettingModel;
import com.yunya.feign.appointment.domain.query.AppointSettingQuery;
import com.yunya.feign.appointment.vo.AppointSettingVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.ClinicAppointSettingBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 预约设置控制器
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 18:57
 * @update yunya-lihuibin    2020-08-03    新建
 */
@RestController
@RequestMapping("appoint_set")
@Api(tags = "预约设置控制器")
public class ClinicAppointSettingController {

    @Autowired
    private ClinicAppointSettingBiz clinicAppointSettingBiz;

    /**
     * 编辑预约设置
     * @param form  设置表单
     * @return
     */
    @ApiOperation(value = "编辑预约设置")
    @PostMapping("/edit")
    @CurrentUser
    public ResponseResult editAppointSetting(@RequestBody @Validated AppointSettingForm form){
        ResponseResult responseResult = clinicAppointSettingBiz.editSetting(form);
        return responseResult;
    }

    /**
     * 根据条件查询预约显示设置
     * @param query  用户名
     * @return
     */
    @ApiOperation(value = "根据用户id查询预约显示设置")
    @PostMapping("/find")
    public ResponseResult selectAppointSettingByUserId(@RequestBody AppointSettingQuery query){
        AppointSettingVo appointSettingVo = clinicAppointSettingBiz.selectAppointSettingByUserId(query);
        return ResponseUtil.success(appointSettingVo);
    }

    /**
     * 根据id查询预约显示设置
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id查询预约显示设置")
    @GetMapping("/find/{id}")
    public ResponseResult selectAppointSettingById(@PathVariable("id") Integer id){
        AppointSettingVo appointSettingVo = clinicAppointSettingBiz.selectAppointSettingById(id);
        return ResponseUtil.success(appointSettingVo);
    }

    /**
     * 添加预约设置
     * @param form  数据表单
     * @return
     */
    @ApiOperation(value = "添加预约设置")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult addAppointSetting(@RequestBody @Validated AppointSettingForm form){
        ResponseResult responseResult = clinicAppointSettingBiz.editSetting(form);
        return ResponseUtil.success(responseResult);
    }

    /**
     * 根据设置id删除数据
     * @param id
     * @return
     */
    @ApiOperation(value = "根据设置id删除数据")
    @DeleteMapping("/del/{id}")
    public ResponseResult delAppointSettingById(@PathVariable("id") Integer id){
        return clinicAppointSettingBiz.delAppointSetting(id);
    }
}
