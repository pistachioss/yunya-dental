package com.yunya.modules.appointment.controller.app;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.model.ReservationModel;
import com.yunya.feign.appointment.domain.query.ReservationQuery;
import com.yunya.feign.appointment.vo.ReservationVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.Reservation;
import com.yunya.modules.appointment.biz.app.ReservationBiz;
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
 * @description: 预约意向登记控制
 **/
@RestController
@RequestMapping("/reservation")
@Api(tags = "预约意向登记")
public class ReservationController {

    @Autowired
    private ReservationBiz biz;

    @ApiOperation("新增预约意向登记")
    @PostMapping
    public ResponseResult<T> addOnlineAppointment(@RequestBody @Validated ReservationModel model) {
        return biz.add(model);
    }

    @ApiOperation("修改预约意向登记")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "预约意向登记ID",required = true, dataTypeClass = Integer.class)
    )
    @PutMapping(value = "/{id}")
    public ResponseResult<T> updateOnlineAppointment(@RequestBody ReservationModel model) {
        return biz.update(model);
    }

    @ApiOperation("删除预约意向登记")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "预约意向登记ID",required = true, dataTypeClass = Integer.class)
    )
    @DeleteMapping("/{id}")
    @CurrentUser
    public ResponseResult<T> deleteOnlineAppointmentById(@PathVariable("id") @NotNull(message = "预约申请ID不能为空") Integer id) {
        return biz.delete(id);
    }

    @ApiOperation("获取预约意向登记列表")
    @PostMapping("/list")
    public ResponseResult<PageInfo<ReservationVo>> find(@RequestBody @Validated ReservationQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        List<ReservationVo> results = biz.list(query);
        return ResponseUtil.success(new PageInfo<ReservationVo>(results));
    }
}
