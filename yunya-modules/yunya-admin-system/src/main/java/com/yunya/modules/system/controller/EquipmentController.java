package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.system.biz.EquipmentBiz;
import com.yunya.modules.system.domain.model.EquipmentInfoModel;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简单介绍:</br>
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

    @PostMapping(value = "新增设备")
    public ResponseResult add(@RequestBody EquipmentInfoModel model){

        return null;
    }
}
