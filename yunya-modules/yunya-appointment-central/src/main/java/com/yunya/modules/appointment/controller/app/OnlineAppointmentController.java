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
import com.yunya.modules.appointment.biz.app.OnlineAppointmentBiz;
import io.swagger.annotations.*;
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
 * @description: 在线预约控制
 * @author: LHB
 * @create: 2021-05-18 16:20
 **/
@RestController
@RequestMapping("/online/appoint")
@Api(tags = "线上预约相关接口")
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

    @ApiOperation("预约申请(新增/修改)")
    @PostMapping("/apply")
    @CurrentUser
    public ResponseResult<T> applyOnlineAppointment(@RequestBody @Validated OnlineAppointmentModel model) {
        Integer id = model.getId();
        if (id == null) {
            return this.addOnlineAppointment(model);
        } else {
            OnlineAppointmentForm build = EntityUtils.build(model, OnlineAppointmentForm.class);
            return this.updateOnlineAppointment(build);
        }
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
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        List<OnlineAppointmentVo> results = onlineAppointmentBiz.findByCondition(query);
        return ResponseUtil.success(new PageInfo<OnlineAppointmentVo>(results));
    }

    @ApiOperation("导出预约申请")
    @PostMapping("/export")
    public ResponseResult<T> export(HttpServletResponse response, @Validated OnlineAppointmentQuery query) throws IOException {
        onlineAppointmentBiz.export(response,query);
        return ResponseUtil.success();
    }

    @ApiOperation("预约消息通知")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId",value = "门诊ID",required = true,dataTypeClass = Integer.class)
    })
    @GetMapping("/message/notice/{orgId}")
    public ResponseResult<OnlineAppointNewMessageNoticeVo> newMessageNotice(@PathVariable("orgId") @NotNull(message = "门诊ID不能为空") Integer orgId) {
        OnlineAppointNewMessageNoticeVo result = onlineAppointmentBiz.newMessageNotice(orgId);
        return ResponseUtil.success(result);
    }

    /**
     * 查询预约时间列表
     * @param orgId 门诊ID
     * @param itemId 预约项目ID
     * @return
     */
    @ApiOperation("查询预约时间列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "门诊ID",value = "orgId",required = true,dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "项目ID",value = "itemId",required = true,dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "预约日期",value = "date",required = true,dataTypeClass = String.class,defaultValue = "2021-06-01"),
    })
    @GetMapping("/time/list/{orgId}/{itemId}")
    public ResponseResult<Map<String,List<CountOnlineAppointVo>>>  appointTimeList(@PathVariable("orgId") @NotNull(message = "门诊ID不能为空") Integer orgId,
                                                                                   @PathVariable("itemId") @NotNull(message = "项目ID不能为空") Integer itemId,
                                                                                   @RequestParam("date") @NotNull(message = "日期不能为空")
                                                                         @NotBlank(message = "日期不能为空") String date) {
        Map<String, List<CountOnlineAppointVo>> result = onlineAppointmentBiz.appointTimeList(orgId, itemId, date);
        return ResponseUtil.success(result);
    }
}
