package com.yunya.modules.appointment.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.DeviceTypeForm;
import com.yunya.feign.appointment.domain.model.DeviceTypeModel;
import com.yunya.feign.appointment.domain.query.DeviceTypeQuery;
import com.yunya.feign.appointment.vo.DeviceTypeVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.ClinicDeviceTypeBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * 预约设备类型Controller
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 0:28
 * @update yunya-lihuibin    2020-08-03    新建
 */
@Api(tags = "预约设备类型Controller（公司端）")
@RestController
@RequestMapping("appoint_device/type")
public class ClinicDeviceTypeController {

    @Autowired
    private ClinicDeviceTypeBiz clinicDeviceTypeBiz;

    /**
     * 添加设备类型(公司端-门诊设备-新增)
     * @param typeModel  设备类型参数封装
     * @return
     */
    @ApiOperation(value = "添加设备类型(公司端-门诊设备-新增)")
    @CurrentUser
    @PostMapping("/add")
    public ResponseResult addDeviceType(@RequestBody @Validated DeviceTypeModel typeModel){
        ResponseResult responseResult = clinicDeviceTypeBiz.addDeviceType(typeModel);
        return responseResult;
    }

    /**
     * 根据设备类型id删除设备
     * @param id  设备类型id
     * @return
     */
    @ApiOperation(value = "根据设备类型id删除设备类型")
    @DeleteMapping("/del/{id}")
    public ResponseResult delDeviceTypeById(@PathVariable @Min(value = 1) Integer id){
        ResponseResult responseResult = clinicDeviceTypeBiz.delDeviceTypeById(id);
        return responseResult;
    }

    /**
     * 设备名称修改（公司端-门诊设备-修改）
     * @param from  修改数据信息表单
     * @return
     */
    @ApiOperation(value = "设备名称修改（公司端-门诊设备-修改）")
    @CurrentUser
    @PutMapping("/update")
    public ResponseResult updateDeviceType(@RequestBody @Validated DeviceTypeForm from){
        ResponseResult responseResult = clinicDeviceTypeBiz.updateDeviceType(from);
        return responseResult;
    }

    /**
     * 根据设备类型id查询设备
     * @param id  设备类型id
     * @return
     */
    @ApiOperation(value = "根据设备类型id查询设备")
    @GetMapping("/find/{id}")
    public ResponseResult findDeviceTypeById(@PathVariable("id") @Min(value = 1) Integer id){
        DeviceTypeVo deviceTypeVo = clinicDeviceTypeBiz.findDeviceTypeById(id);
        return ResponseUtil.success(deviceTypeVo);
    }

    /**
     * 根据条件查询设备
     * @param query 查询条件
     * @return
     */
    @ApiOperation(value = "根据条件查询设备")
    @PostMapping("/find")
    public ResponseResult findDeviceTypeByExample(@RequestBody DeviceTypeQuery query){
        PageInfo pageInfo = clinicDeviceTypeBiz.findDeviceTypeList(query);
        return ResponseUtil.success(pageInfo);
    }
}
