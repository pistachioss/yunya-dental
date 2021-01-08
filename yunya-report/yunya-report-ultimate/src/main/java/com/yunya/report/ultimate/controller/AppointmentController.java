package com.yunya.report.ultimate.controller;

import com.yunya.feign.report.domain.query.AppointmentCountQuery;
import com.yunya.feign.report.domain.vo.AppointmentCountVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.report.ultimate.biz.BaseTreatmentProcessBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: yunya-dental
 * @description: 预约中心中间表控制器
 * @author: LHB
 * @create: 2021-01-08 15:18
 **/
@Api(tags = "预约中心中间表控制器")
@RestController
@RequestMapping("appoint")
public class AppointmentController {
    @Autowired
    private BaseTreatmentProcessBiz baseTreatmentProcessBiz;

    @ApiOperation("患者档案-预约信息-履约次数/失约次数/改约次数/取消预约次数")
    @PostMapping("/count/{patientId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "patientId", value = "患者ID", required = true, dataTypeClass = Integer.class)
    })
    public ResponseResult<AppointmentCountVO> appointmentCount(@PathVariable("patientId") Integer patientId,
                                                               @RequestBody AppointmentCountQuery query) {
        return this.baseTreatmentProcessBiz.appointmentCount(patientId,query);
    }


}
