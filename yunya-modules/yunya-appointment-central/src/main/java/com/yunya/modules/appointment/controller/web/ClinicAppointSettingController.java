package com.yunya.modules.appointment.controller.web;

import com.yunya.feign.appointment.domain.form.AppointSettingForm;
import com.yunya.feign.appointment.vo.AppointSettingVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.web.ClinicAppointSettingBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 预约设置Controller
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 18:57
 * @update yunya-lihuibin    2020-08-03    新建
 */
@RestController
@RequestMapping("appoint/setting")
@Api(tags = "预约设置Controller")
public class ClinicAppointSettingController {

    @Autowired
    private ClinicAppointSettingBiz clinicAppointSettingBiz;
    /**
     * 修改预约设置
     * @param form  设置表单
     * @return
     */
    @ApiOperation(value = "修改预约设置")
    @PutMapping("/update")
    @CurrentUser
    @RepeatSubmit
    public ResponseResult editOrAddAppointSetting(@RequestBody @Validated AppointSettingForm form){
        ResponseResult responseResult = clinicAppointSettingBiz.editOrAddSetting(form);
        return responseResult;
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
     * 根据条件查询预约显示设置
     * @param userId
     * @return
     */
    @ApiOperation(value = "根据条件查询预约显示设置")
    @GetMapping("/find")
    public ResponseResult findAppointSettingByExample(Integer userId){
        AppointSettingVo appointSettingVo = clinicAppointSettingBiz.findAppointSettingByUserId(userId);
        return ResponseUtil.success(appointSettingVo);
    }

    /**
     * 根据设置id删除数据
     * @param id
     * @return
     */
    @ApiOperation(value = "根据设置id删除数据")
    @DeleteMapping("/delete/{id}")
    public ResponseResult delAppointSettingById(@PathVariable("id") Integer id){
        return clinicAppointSettingBiz.delAppointSetting(id);
    }
}
