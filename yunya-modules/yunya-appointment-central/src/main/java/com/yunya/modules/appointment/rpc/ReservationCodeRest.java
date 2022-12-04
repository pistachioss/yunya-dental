package com.yunya.modules.appointment.rpc;

import com.yunya.feign.appointment.domain.form.OnlineAppointItemSettingForm;
import com.yunya.feign.appointment.domain.query.ReservationCodeQuery;
import com.yunya.feign.appointment.vo.EnableOnlineAppointItemVo;
import com.yunya.feign.appointment.vo.OnlineAppointItemSettingVo;
import com.yunya.feign.appointment.vo.OnlineAppointItemVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.modules.appointment.biz.app.OnlineAppointItemSettingBiz;
import com.yunya.modules.appointment.biz.app.ReservationCodeBiz;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: yunya-dental
 * @description: 预约登记权益码校验服务rpc调用中心
 **/
@RestController
@RequestMapping("api/reservation/code")
@Slf4j
public class ReservationCodeRest {

    @Autowired
    private ReservationCodeBiz biz;

    @ApiOperation("预约意向登记号码验证")
    @GetMapping("/valid")
    public boolean find(@RequestBody ReservationCodeQuery model) {
        return biz.find(model);
    }

    @ApiOperation("预约意向登记号码更新")
    @PostMapping("/use")
    public boolean use(@RequestBody ReservationCodeQuery model) {
        return biz.use(model);
    }
}
