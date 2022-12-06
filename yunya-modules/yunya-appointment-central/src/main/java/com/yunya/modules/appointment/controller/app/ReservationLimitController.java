package com.yunya.modules.appointment.controller.app;

import com.yunya.feign.appointment.domain.model.ReservationLimitModel;
import com.yunya.feign.appointment.domain.query.ReservationLimitQuery;
import com.yunya.feign.appointment.vo.ReservationLimitVO;
import com.yunya.framework.common.annation.CurrentUser;
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

import javax.annotation.Resource;
import java.text.ParseException;

/**
 * @program: yunya-dental
 * @description: 预约意向登记控制
 **/
@RestController
@RequestMapping("/reservation/limit")
@Api(tags = "预约意向登记流量管理-app")
public class ReservationLimitController {

    @Resource
    private ReservationLimitBiz limitBiz;

    @ApiOperation("获取当月预约登记流量")
    @PostMapping("/app")
    public ResponseResult<ReservationLimitVO> list(@RequestBody @Validated ReservationLimitQuery query) throws ParseException {
        return ResponseUtil.success(limitBiz.list(query));
    }

}
