package com.yunya.modules.appointment.controller.app;

import com.yunya.feign.appointment.domain.form.OnlineAppointItemSettingForm;
import com.yunya.feign.appointment.vo.EnableOnlineAppointDentistsVo;
import com.yunya.feign.appointment.vo.OnlineAppointItemSettingVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
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
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 在线预约设置相关接口(公司端/用户设置/运营设置/线上预约项目)
 * @author: LHB
 * @create: 2021-05-18 16:20
 **/
@RestController
@RequestMapping("/online/appoint/item")
@Api(tags = "线上预约设置相关接口(门诊端)")
public class OnlineAppointItemSettingController {

    @Autowired
    private OnlineAppointItemSettingBiz appointItemSettingBiz;

    @ApiOperation("查询医生线上可预约项目(门诊端-诊所设置-员工设置)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "patientId", value = "医生ID",required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "orgId", value = "门诊ID",required = true, dataTypeClass = Integer.class)}
    )
    @GetMapping("/setting/{dentistId}/{orgId}")
    public ResponseResult<OnlineAppointItemSettingVo> findOnlineAppointItemById(@PathVariable("dentistId")
                                                                                     @NotNull(message = "医生ID不能为空") Integer dentistId,
                                                                                @PathVariable("orgId") @NotNull(message = "门诊不能为空") Integer orgId) {
        OnlineAppointItemSettingVo result = appointItemSettingBiz.findItemSettingByDentistId(dentistId,orgId);
        return ResponseUtil.success(result);
    }
    @ApiOperation("新增、更新线上预约设置")
    @PostMapping("/setting")
    @CurrentUser
    public ResponseResult<T> addOrUpdateOnlineAppointItem(@RequestBody @Validated OnlineAppointItemSettingForm form) {
        return appointItemSettingBiz.addOrUpdateOnlineAppointItem(form);
    }

    @ApiOperation("查询可预约医生列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId",value = "门诊ID",required = true,dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "itemId",value = "预约项目ID",required = true,dataTypeClass = Integer.class)
    })
    @GetMapping("/enableDentists/{orgId}")
    public ResponseResult<List<EnableOnlineAppointDentistsVo>> findDentistsByAppointItem(@PathVariable("orgId") @NotNull(message = "门诊ID不能为空") Integer orgId,
                                                                                         @RequestParam("itemId") Integer itemId) {
        return appointItemSettingBiz.findDentistsByAppointItem(orgId,itemId);
    }

}
