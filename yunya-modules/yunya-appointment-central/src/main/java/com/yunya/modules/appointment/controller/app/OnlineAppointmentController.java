package com.yunya.modules.appointment.controller.app;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.OnlineAppointmentForm;
import com.yunya.feign.appointment.domain.model.OnlineAppointmentModel;
import com.yunya.feign.appointment.domain.query.OnlineAppointmentQuery;
import com.yunya.feign.appointment.vo.OnlineAppointmentVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.appointment.biz.app.OnlineAppointmentBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 在线预约控制
 * @author: LHB
 * @create: 2021-05-18 16:20
 **/
@RestController
@RequestMapping("/online/appoint")
@Api(tags = "在线预约相关接口")
public class OnlineAppointmentController {

    @Autowired
    private OnlineAppointmentBiz onlineAppointmentBiz;

    @ApiOperation("根据id查询线上预约申请")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "预约申请ID",required = true, dataTypeClass = Integer.class)
    )
    @GetMapping("/{id}")
    public ResponseResult<OnlineAppointmentVo> findOnlineAppointmentById(@PathVariable("id")
                                                                                     @NotNull(message = "预约申请ID不能为空") Integer id) {
        return onlineAppointmentBiz.findOnlineAppointmentById(id);
    }

    @ApiOperation("新增在线预约申请")
    @PostMapping
    @CurrentUser
    public ResponseResult<T> addOnlineAppointment(@RequestBody @Validated OnlineAppointmentModel model) {
        return onlineAppointmentBiz.addOnlineAppointment(model);
    }

    @ApiOperation("修改在线预约申请")
    @PutMapping
    @CurrentUser
    public ResponseResult<T> updateOnlineAppointment(@RequestBody @Validated OnlineAppointmentForm form) {
        return onlineAppointmentBiz.updateOnlineAppointment(form);
    }

    @ApiOperation("删除预约申请")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "预约申请ID",required = true, dataTypeClass = Integer.class)
    )
    @DeleteMapping("/{id}")
    @CurrentUser
    public ResponseResult<T> deleteOnlineAppointmentById(@PathVariable("id") @NotNull(message = "预约申请ID不能为空") Integer id) {
        return onlineAppointmentBiz.deleteOnlineAppointmentById(id);
    }

    @ApiOperation("根据条件批量查询预约申请")
    @PostMapping("/list")
    public ResponseResult<PageInfo<OnlineAppointmentVo>> findByCondition(@RequestBody
                                                                     @Validated OnlineAppointmentQuery query) {
        return onlineAppointmentBiz.findByCondition(query);
    }
}
