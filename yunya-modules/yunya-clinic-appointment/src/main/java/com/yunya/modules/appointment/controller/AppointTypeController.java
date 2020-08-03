package com.yunya.modules.appointment.controller;

import com.yunya.feign.appointment.domain.form.AppointTypeForm;
import com.yunya.feign.appointment.domain.model.AppointTypeModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.AppointType;
import com.yunya.modules.appointment.biz.ClinicAppointTypeBiz;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 预约类型项目控制器
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 17:27
 * @update yunya-lihuibin    2020-07-31    新建
 */
@Api(tags = "预约类型项目控制器")
@RestController
@RequestMapping("appoint/type")
public class AppointTypeController {

    @Autowired
    private ClinicAppointTypeBiz clinicAppointTypeBiz;

    /**
     * 添加预约项目类型
     * @param model
     * @return
     */
    @ApiOperation(value = "添加预约项目类型")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult addAppointType(@RequestBody @Validated AppointTypeModel model){

        AppointType appointType = clinicAppointTypeBiz.selectAppointTypeByName(model.getName());
        if (appointType != null){
            return ResponseUtil.fail(OperationCodeConstants.NAME_IS_OCCUPIED,"新增预约类型已经存在！",null);
        }
        Integer integer = clinicAppointTypeBiz.insertAppointType(model);
        if (integer <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"数据更新失败！！",null);
        }
        return ResponseUtil.success();
    }

    /**
     * 根据id删除预约项目
     * @param id  预约项目id
     * @return
     */
    @ApiOperation(value = "根据id删除预约项目")
    @DeleteMapping("/del/{id}")
    public ResponseResult delAppointTypeById(@PathVariable("id") Integer id) {
        if (id == null || id <= 0) {
            return ResponseUtil.fail(OperationCodeConstants.PARAMETERS_IS_ILLEGAL, "参数非法！", null);
        }

        // 检测数据库中是否存在要删除的数据
        AppointType appointType = clinicAppointTypeBiz.selectById(id);
        if (appointType == null) {
            return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL, "要删除的数据不存在！", null);
        }
        // 删除数据
        Integer result = clinicAppointTypeBiz.delAppointType(id);
        if (result <= 0) {
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL, "删除失败！", null);
        }
        return ResponseUtil.success();
    }

    /**
     * 更新数据
     * @param form  数据表单
     * @return
     */
    @ApiOperation(value = "更新数据")
    @PostMapping("/update")
    @CurrentUser
    public ResponseResult updateAppointType(@RequestBody @Validated AppointTypeForm form){
        Integer id = form.getId();
        if (id == null || id <= 0){
            return ResponseUtil.fail(OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY,"id参数非法！",null);
        }

        // 检测数据库中是否存在要更新的数据
        AppointType appointType = clinicAppointTypeBiz.selectById(id);
        if (appointType == null) {
            return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL, "要更新的数据不存在！", null);
        }
        // 更新数据
        Integer result = clinicAppointTypeBiz.updateAppointType(form);
        if (result <= 0) {
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL, "更新失败！", null);
        }
        return ResponseUtil.success();
    }

    /**
     * 根据id查询预约项目种类
     * @param id 预约项目id
     * @return
     */
    @ApiOperation(value = "根据id查询预约项目种类")
    @GetMapping("/select/{id}")
    public ResponseResult selectAppointTypeById(@PathVariable("id") Integer id){
        if (id == null || id <= 0){
            return ResponseUtil.fail(OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY,"id参数非法！",null);
        }
        AppointType appointType = clinicAppointTypeBiz.selectAppointTypeById(id);
        return ResponseUtil.success(appointType);
    }


}
