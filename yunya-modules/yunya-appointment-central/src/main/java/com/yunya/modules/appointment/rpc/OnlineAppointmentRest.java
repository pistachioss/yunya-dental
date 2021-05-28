package com.yunya.modules.appointment.rpc;

import com.yunya.feign.appointment.domain.form.OnlineAppointItemSettingForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.appointment.biz.app.OnlineAppointItemSettingBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @program: yunya-dental
 * @description: 在线预约服务rpc调用中心
 * @author: LHB
 * @create: 2021-05-26 15:56
 **/
@Api(tags = "在线预约服务rpc调用中心")
@RestController
@RequestMapping("api/online/appoint")
@Slf4j
public class OnlineAppointmentRest {

    @Autowired
    private OnlineAppointItemSettingBiz appointItemSettingBiz;

    @ApiOperation("新增、更新线上预约设置")
    @RequestMapping(value = "/",method = RequestMethod.POST)
    @CurrentUser
    public ResponseResult<T> addOrUpdateOnlineAppointItem(@RequestBody @Validated OnlineAppointItemSettingForm form) {
        return appointItemSettingBiz.addOrUpdateOnlineAppointItem(form);
    }
}
