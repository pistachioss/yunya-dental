package com.yunya.modules.appointment.controller.app;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.query.ReservationCodeQuery;
import com.yunya.feign.appointment.vo.ReservationVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.ReservationCode;
import com.yunya.modules.appointment.biz.app.ReservationCodeBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @program: yunya-dental
 * @description: 预约意向登记渠道来源控制
 **/
@RestController
@RequestMapping("/reservation/code")
@Api(tags = "预约意向登记号码")
public class ReservationCodeController {

    @Autowired
    private ReservationCodeBiz biz;

    @ApiOperation("预约意向登记号码验证")
    @PostMapping("/valid")
    public ResponseResult find(@RequestBody ReservationCodeQuery model) {
        return ResponseUtil.success(biz.find(model));
    }
}
