package com.yunya.modules.appointment.controller.web;

import com.yunya.feign.appointment.domain.model.ReservationLimitModel;
import com.yunya.feign.appointment.domain.query.ReservationLimitQuery;
import com.yunya.feign.appointment.vo.ReservationLimitVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.web.ReservationLimitBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDate;

/**
 * @program: yunya-dental
 * @description: 预约意向登记控制
 **/
@RestController
@RequestMapping("/reservation/limit")
@Api(tags = "预约意向登记流量管理")
public class ReservationLimitController {

    @Resource
    private ReservationLimitBiz limitBiz;

    @CurrentUser
    @ApiOperation("编辑预约登记流量")
    @PostMapping
    public ResponseResult<Boolean> addOnlineAppointment(@RequestBody @Validated ReservationLimitModel model) {
        limitBiz.modify(model);
        return ResponseUtil.success();
    }

    @ApiOperation("获取当月预约登记流量")
    @PostMapping("/list")
    public ResponseResult<ReservationLimitVO> list(@RequestBody @Validated ReservationLimitQuery query) throws ParseException {
        if (DateUtil.isCurrentMonth(query.getConfigDate())) {
            query.setConfigDate(LocalDate.now());
        }
        return ResponseUtil.success(limitBiz.list(query));
    }

}
