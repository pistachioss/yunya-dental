package com.yunya.modules.system.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.EquipmentBiz;
import com.yunya.modules.system.domain.model.EquipmentInfoModel;
import com.yunya.modules.system.domain.query.EquipmentInfoQueryForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 简单介绍:</br> 硬件设备管理
 *
 * @author: WY
 * @date 2020/9/2 10:09
 * @description:
 * @since: 1.0.0
 */

@Api(value = "硬件设备管理", description = "硬件设备增删改查")
@RestController
@RequestMapping("equipment")
public class EquipmentController {

    /** 注入对象 */
    private final EquipmentBiz equipmentBiz;

    public EquipmentController(EquipmentBiz equipmentBiz) {
        this.equipmentBiz = equipmentBiz;
    }

    @CurrentUser
    @ApiModelProperty(value = "设备列表")
    @PostMapping(value = "/findList")
    public ResponseResult findList(@RequestBody EquipmentInfoQueryForm queryForm){
        return ResponseUtil.success(equipmentBiz.findList(queryForm));
    }

    @CurrentUser
    @ApiModelProperty(value = "新增设备")
    @PostMapping(value = "/add")
    public ResponseResult add(@RequestBody @Validated EquipmentInfoModel model, HttpServletRequest request){
        equipmentBiz.add(model,request);
        return ResponseUtil.success();
    }

    @ApiModelProperty(value = "删除")
    @DeleteMapping(value = "delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") Integer id){
        equipmentBiz.deleteById(id);
        return ResponseUtil.success();
    }

    @IgnoreUserToken
    @ApiOperation(value = "拍照回调")
    @RequestMapping(value = "/paizhao")
    public ResponseResult takePictures() {
        System.out.println("---------------------拍照回调------------------");
        System.out.println("---------------------拍照回调------------------");
        System.out.println("---------------------拍照回调------------------");
        System.out.println("---------------------拍照回调------------------");
        return ResponseUtil.success();
    }

}
