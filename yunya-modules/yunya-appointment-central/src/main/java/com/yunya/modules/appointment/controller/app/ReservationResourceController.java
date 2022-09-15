package com.yunya.modules.appointment.controller.app;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.OnlineAppointmentForm;
import com.yunya.feign.appointment.domain.model.OnlineAppointmentModel;
import com.yunya.feign.appointment.domain.query.OnlineAppointmentQuery;
import com.yunya.feign.appointment.vo.CountOnlineAppointVo;
import com.yunya.feign.appointment.vo.OnlineAppointNewMessageNoticeVo;
import com.yunya.feign.appointment.vo.OnlineAppointmentVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.ReservationResource;
import com.yunya.modules.appointment.biz.app.OnlineAppointmentBiz;
import com.yunya.modules.appointment.biz.app.ReservationResourceBiz;
import com.yunya.modules.appointment.biz.web.AppointmentBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @program: yunya-dental
 * @description: 预约意向登记渠道来源控制
 **/
@RestController
@RequestMapping("/reservation/resource")
@Api(tags = "预约意向登记渠道来源")
public class ReservationResourceController {

    @Autowired
    private ReservationResourceBiz biz;

    @ApiOperation("新增预约意向登记渠道来源")
    @PostMapping
    public ResponseResult<T> addOnlineAppointment(@RequestBody @NotNull(message = "sourceName不能为空") String sourceName) {
        return biz.add(sourceName);
    }

    @ApiOperation("修改预约意向登记渠道来源")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "预约渠道来源ID",required = true, dataTypeClass = Integer.class)
    )
    @PutMapping(value = "/{id}")
    public ResponseResult<T> updateOnlineAppointment(@PathVariable("id") @NotNull(message = "ID不能为空") Integer id, @RequestBody @NotNull(message = "sourceName不能为空") String sourceName) {
        return biz.update(id, sourceName);
    }

    @ApiOperation("删除预约意向登记渠道来源")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "预约渠道来源ID",required = true, dataTypeClass = Integer.class)
    )
    @DeleteMapping("/{id}")
    @CurrentUser
    public ResponseResult<T> deleteOnlineAppointmentById(@PathVariable("id") @NotNull(message = "预约申请ID不能为空") Integer id) {
        return biz.delete(id);
    }

    @ApiOperation("获取预约意向登记渠道来源列表")
    @PostMapping("/list")
    public ResponseResult<ReservationResource> find() {
        return biz.list();
    }
}
