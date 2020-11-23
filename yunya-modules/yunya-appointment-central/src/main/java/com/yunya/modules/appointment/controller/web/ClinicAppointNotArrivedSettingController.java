package com.yunya.modules.appointment.controller.web;

import com.yunya.feign.appointment.domain.form.AppointNotArrivedSettingForm;
import com.yunya.feign.appointment.domain.model.AppointNotArrivedSettingModel;
import com.yunya.feign.appointment.vo.AppointNotArrivedSettingVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.web.ClinicAppointNotArrivedSettingBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 预约未到列表设置
 *
 * @author yunya-lihuibin
 * @create 2020-08-11 10:56
 * @update yunya-lihuibin    2020-08-11    新建
 */
@Api(tags = "预约未到列表设置")
@RestController
@RequestMapping("appoint/setting/not_arrived")
public class ClinicAppointNotArrivedSettingController {

    @Autowired
    private ClinicAppointNotArrivedSettingBiz appointNotArrivedSettingBiz;


    /**
     * 新增设置
     * @param model 设置参数
     * @return
     */
    @ApiOperation(value = "新增设置")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult addAppointNotArrivedSetting(@RequestBody @Validated AppointNotArrivedSettingModel model){
        Integer result = appointNotArrivedSettingBiz.addAppointNotArrivedSetting(model);
        return ResponseUtil.success();
    }

    /**
     * 根据id删除设置
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id删除设置")
    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteAppointNotArrivedSetting(@PathVariable("id") Integer id){
        Integer result = appointNotArrivedSettingBiz.deleteAppointNotArrivedSetting(id);
        return ResponseUtil.success();
    }

    /**
     * 修改设置
     * @param form
     * @return
     */
    @ApiOperation(value = "修改设置")
    @PutMapping("/update")
    @CurrentUser
    public ResponseResult updateAppointNotArrivedSetting(@RequestBody @Validated AppointNotArrivedSettingForm form){
        Integer result = appointNotArrivedSettingBiz.updateAppointNotArrivedSetting(form);
        return ResponseUtil.success();
    }

    /**
     * 查询设置
     * @param id
     * @return
     */
    @ApiOperation(value = "查询设置")
    @GetMapping("/find/{id}")
    public ResponseResult findAppointNotArrivedSetting(@PathVariable("id") Integer id){
        AppointNotArrivedSettingVo appointNotArrivedSettingVo = appointNotArrivedSettingBiz.findAppointNotArrivedSettingById(id);
        return ResponseUtil.success(appointNotArrivedSettingVo);
    }

    /**
     * 根据用户id查询设置
     * @param userId
     * @return
     */
    @ApiOperation(value = "根据用户id查询设置")
    @GetMapping("/find/userId/{userId}")
    public ResponseResult findAppointNotArrivedSettingByUserId(@PathVariable("userId") Integer userId){
        AppointNotArrivedSettingVo appointNotArrivedSettingVo = appointNotArrivedSettingBiz.findAppointNotArrivedSettingByUserId(userId);
        return ResponseUtil.success(appointNotArrivedSettingVo);
    }



}
