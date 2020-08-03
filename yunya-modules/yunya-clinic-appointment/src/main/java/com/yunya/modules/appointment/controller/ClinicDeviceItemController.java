package com.yunya.modules.appointment.controller;

import com.yunya.feign.appointment.domain.form.DeviceItemForm;
import com.yunya.feign.appointment.domain.model.DeviceItemModel;
import com.yunya.feign.appointment.domain.query.DeviceItemQuery;
import com.yunya.feign.appointment.vo.DeviceItemVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.ClinicDeviceItemBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 预约设备项目服务控制层
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 21:00
 * @update yunya-lihuibin    2020-07-31    新建
 */
@Api(tags = "预约设备项目服务控制层")
@RestController
@RequestMapping("appoint_device")
public class ClinicDeviceItemController {

    @Autowired
    private ClinicDeviceItemBiz clinicDeviceItemBiz;

    /**
     * 根据条件查询设备列表
     *
     * @description
     * @param query 查询条件
     */
    @ApiOperation(value = "根据公司端门诊id查询设备列表")
    @PostMapping("/find")
    public ResponseResult findDeviceList(@RequestBody DeviceItemQuery query) {
        List<DeviceItemVo> resultMap = clinicDeviceItemBiz.selectDeviceItemByExample(query);
        return ResponseUtil.success(resultMap);
    }

    /**
     * 根据设备id查询设备
     * @param id  设备id
     * @return
     */
    @ApiOperation(value = "根据设备id查询设备")
    @GetMapping("/find/{id}")
    public ResponseResult findDeviceItemById(
            @PathVariable("id")
            @NotNull(message = "设备id不能为空！")
            @Min(value = 1) Integer id){
        DeviceItemVo deviceItemVo = clinicDeviceItemBiz.selectDeviceItemById(id);
        return ResponseUtil.success(deviceItemVo);
    }

    /**
     * 添加门诊设备
     *
     * @description 添加设备id到门诊设备表
     * @param deviceForms 设备列表
     */
    @ApiOperation(value = "添加设备id到门诊设备表")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult addDevice(@RequestBody @Validated DeviceItemModel deviceForms) {
        ResponseResult responseResult = clinicDeviceItemBiz.addDevice(deviceForms);
        return responseResult;
    }

    /**
     * 删除门诊设备
     *
     * @description 根据设备id删除门诊设备
     * @param id 门诊设备id
     */
    @ApiOperation(value = "根据设备id删除门诊设备")
    @DeleteMapping("/del/{id}")
    public ResponseResult deleteDevice(@PathVariable("id") Integer id) {
        return clinicDeviceItemBiz.deleteDeviceItemById(id);
    }

    /**
     * 修改设备项目
     * @param form  修改数据表单
     * @return
     */
    @ApiOperation(value = "修改设备项目")
    @CurrentUser
    @PostMapping("/update")
    public ResponseResult updateDevice(@RequestBody @Validated DeviceItemForm form){
        return clinicDeviceItemBiz.updateDeviceItem(form);
    }

}
