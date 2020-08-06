package com.yunya.modules.appointment.controller;

import com.yunya.feign.appointment.domain.form.AppointTypeForm;
import com.yunya.feign.appointment.domain.model.AppointTypeModel;
import com.yunya.feign.appointment.vo.AppointTypeListVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.AppointType;
import com.yunya.modules.appointment.biz.AppointTypeBiz;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约项目分类Controller（公司端）
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 17:27
 * @update yunya-lihuibin    2020-07-31    新建
 */
@Api(tags = "预约项目分类Controller（公司端）")
@RestController
@RequestMapping("appoint_type")
public class AppointTypeController {

    @Autowired
    private AppointTypeBiz appointTypeBiz;

    /**
     * 添加预约项目类型
     * @param model
     * @return
     */
    @ApiOperation(value = "添加预约项目类型")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult addAppointType(@RequestBody @Validated AppointTypeModel model){

        AppointType appointType = appointTypeBiz.selectAppointTypeByName(model.getName());
        if (appointType != null){
            return ResponseUtil.fail(OperationCodeConstants.NAME_IS_OCCUPIED,"新增预约类型已经存在！",null);
        }
        Integer integer = appointTypeBiz.insertAppointType(model);
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
        AppointType appointType = appointTypeBiz.selectById(id);
        if (appointType == null) {
            return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL, "要删除的数据不存在！", null);
        }
        // 删除数据
        Integer result = appointTypeBiz.delAppointType(id);
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
        // 检测数据库中是否存在要更新的数据
        AppointType appointType = appointTypeBiz.selectById(id);
        if (appointType == null) {
            return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL, "要更新的数据不存在！", null);
        }
        // 更新数据
        Integer result = appointTypeBiz.updateAppointType(form);
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
        AppointType appointType = appointTypeBiz.selectAppointTypeById(id);
        return ResponseUtil.success(appointType);
    }

    /**
     * 查询可预约项目类型列表
     * @return
     */
    @ApiOperation(value = "查询可预约项目类型列表")
    @GetMapping("/type_list")
    public ResponseResult findAppointTypeList(){
        List<AppointTypeListVo> appointTypeList = appointTypeBiz.findAppointTypeList();
        return ResponseUtil.success(appointTypeList);
    }


}
