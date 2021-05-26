package com.yunya.modules.appointment.controller.app;

import com.yunya.feign.appointment.domain.form.OnlineAppointItemForm;
import com.yunya.feign.appointment.domain.model.OnlineAppointmentModel;
import com.yunya.feign.appointment.domain.query.OnlineAppointItemQuery;
import com.yunya.feign.appointment.vo.OnlineAppointItemVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.app.OnlineAppointItemBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @program: yunya-dental
 * @description: 预约项目设置控制器
 * @author: LHB
 * @create: 2021-05-26 10:32
 **/
@RestController
@RequestMapping("/online/appoint/item")
@Api(value = "线上预约项目(公司端-用户设置-运营设置-线上预约项目)")
public class OnlineAppointItemController {
    @Autowired
    private OnlineAppointItemBiz onlineAppointItemBiz;


    @ApiOperation("查询线上预约项目")
    @GetMapping("/{id}")
    public ResponseResult<T> findOnlineAppointItemById(@PathVariable("id") @NotNull(message = "id不能为空") Integer id) {
        return onlineAppointItemBiz.findOnlineAppointItemById(id);
    }

    @ApiOperation("查询线上预约项目列表")
    @PostMapping("/all")
    public ResponseResult<OnlineAppointItemVo> findOnlineAppointItemByCondition(@RequestBody OnlineAppointItemQuery query) {
        return onlineAppointItemBiz.findOnlineAppointItemByCondition(query);
    }

    @ApiOperation("新增线上预约项目")
    @PostMapping
    public ResponseResult<T> addItem(@RequestBody @Validated OnlineAppointmentModel model) {
        return onlineAppointItemBiz.addItem(model);
    }

    @ApiOperation("删除线上预约项目")
    @DeleteMapping("/{id}")
    public ResponseResult<T> deleteItemById(@PathVariable("id") @NotNull(message = "id不能为空") Integer id) {
        return onlineAppointItemBiz.deleteItemById(id);
    }

    @ApiOperation("修改线上预约项目")
    @PutMapping
    public ResponseResult<T> updateItem(@RequestBody @Validated OnlineAppointItemForm form) {
        return onlineAppointItemBiz.updateItem(form);
    }
}
