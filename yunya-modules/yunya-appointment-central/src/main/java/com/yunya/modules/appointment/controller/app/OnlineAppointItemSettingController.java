package com.yunya.modules.appointment.controller.app;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.OnlineAppointItemSettingForm;
import com.yunya.feign.appointment.domain.model.OnlineAppointItemSettingModel;
import com.yunya.feign.appointment.domain.query.OnlineAppointItemSettingQuery;
import com.yunya.feign.appointment.vo.OnlineAppointItemSettingModelVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.appointment.biz.app.OnlineAppointItemSettingBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @program: yunya-dental
 * @description: 在线预约设置相关接口(公司端/用户设置/运营设置/线上预约项目)
 * @author: LHB
 * @create: 2021-05-18 16:20
 **/
@RestController
@RequestMapping("/online/appoint/setting/item")
@Api(tags = "在线预约设置相关接口(公司端/用户设置/运营设置/线上预约项目)")
public class OnlineAppointItemSettingController {

    @Autowired
    private OnlineAppointItemSettingBiz appointItemSettingBiz;

    @ApiOperation("根据id查询线上预约项目")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "预约项目ID",required = true, dataTypeClass = Integer.class)
    )
    @GetMapping("/{id}")
    public ResponseResult<OnlineAppointItemSettingModelVo> findOnlineAppointItemById(@PathVariable("id")
                                                                                     @NotNull(message = "预约项目ID不能为空") Integer id) {
        return appointItemSettingBiz.findOnlineAppointItemById(id);
    }

    @ApiOperation("新增在线预约项目")
    @PostMapping
    @CurrentUser
    public ResponseResult<T> addOnlineAppointItemSetting(@RequestBody @Validated OnlineAppointItemSettingModel model) {
        return appointItemSettingBiz.addOnlineAppointItemSetting(model);
    }

    @ApiOperation("修改在线预约项目")
    @PutMapping
    @CurrentUser
    public ResponseResult<T> updateOnlineAppointItemSetting(@RequestBody @Validated OnlineAppointItemSettingForm form) {
        return appointItemSettingBiz.updateOnlineAppointItemSetting(form);
    }

    @ApiOperation("删除预约项目")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "预约项目ID",required = true, dataTypeClass = Integer.class)
    )
    @DeleteMapping("/{id}")
    @CurrentUser
    public ResponseResult<T> deleteOnlineAppointItemSettingById(@PathVariable("id") @NotNull(message = "预约项目ID不能为空") Integer id) {
        return appointItemSettingBiz.deleteOnlineAppointItemSettingById(id);
    }

    @ApiOperation("根据条件批量查询预约项目")
    @PostMapping("/list")
    public ResponseResult<PageInfo<OnlineAppointItemSettingModelVo>> findByCondition(@RequestBody
                                                                                 @Validated
                                                                                 OnlineAppointItemSettingQuery query) {
        return appointItemSettingBiz.findByCondition(query);
    }
}
