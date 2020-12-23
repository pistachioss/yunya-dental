package com.yunya.modules.appointment.controller.app;

import com.yunya.feign.appointment.domain.form.AppointmentForMonthForm;
import com.yunya.feign.appointment.vo.AppointmentForMonthVo;
import com.yunya.feign.treatment.domain.query.TreatmentInfoForMonthForm;
import com.yunya.feign.treatment.domain.vo.TreatmentInfoForMonthVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.app.AppBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: yunya-dental
 * @description: app端控制层
 * @author: LHB
 * @create: 2020-11-23 13:08
 **/
@RestController
@RequestMapping("/app")
@Api("app端控制层相关接口")
public class AppController {
    @Autowired
    private AppBiz appBiz;

    @ApiOperation("查询指定时间段内每个医生每天的预约人数")
    @PostMapping(value = "/everyday/count")
    @CurrentUser
    public ResponseResult<List<TreatmentInfoForMonthVO>> appointmentForMonth(@RequestBody @Validated AppointmentForMonthForm form) {
        List<TreatmentInfoForMonthVO> appointmentForMonthVOS = appBiz.appointmentForMonth(form);
        return ResponseUtil.success(appointmentForMonthVOS);
    }

}
