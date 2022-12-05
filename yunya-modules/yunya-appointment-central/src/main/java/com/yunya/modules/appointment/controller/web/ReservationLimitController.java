package com.yunya.modules.appointment.controller.web;

import com.yunya.feign.appointment.domain.model.ReservationModel;
import com.yunya.feign.appointment.domain.query.ReservationQuery;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.web.ReservationLimitBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: yunya-dental
 * @description: 预约意向登记控制
 **/
@RestController
@RequestMapping("/reservation")
@Api(tags = "预约意向登记")
public class ReservationLimitController {

    @Autowired
    private ReservationLimitBiz limitBiz;

    @ApiOperation("编辑预约登记流量")
    @PostMapping
    public ResponseResult<Boolean> addOnlineAppointment(@RequestBody @Validated ReservationModel model) {
        return ResponseUtil.success();
    }

    @ApiOperation("获取预约意向登记列表")
    @PostMapping("/list")
    public ResponseResult<Boolean> find(@RequestBody @Validated ReservationQuery query) {
        return ResponseUtil.success();
    }
}
