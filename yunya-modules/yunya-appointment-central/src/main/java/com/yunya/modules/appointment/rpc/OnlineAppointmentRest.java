package com.yunya.modules.appointment.rpc;

import com.yunya.feign.appointment.domain.form.OnlineAppointItemSettingForm;
import com.yunya.feign.appointment.vo.EnableOnlineAppointItemVo;
import com.yunya.feign.appointment.vo.OnlineAppointItemSettingVo;
import com.yunya.feign.appointment.vo.OnlineAppointItemVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.modules.appointment.biz.app.OnlineAppointItemSettingBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: yunya-dental
 * @description: 在线预约服务rpc调用中心
 * @author: LHB
 * @create: 2021-05-26 15:56
 **/
@RestController
@RequestMapping("api/online/appoint")
@Slf4j
public class OnlineAppointmentRest {

    @Autowired
    private OnlineAppointItemSettingBiz appointItemSettingBiz;

    /**
     * 新增、更新线上预约设置
     * @param form
     * @return
     */
    @RequestMapping(value = "/",method = RequestMethod.POST)
    @CurrentUser
    public ResponseResult<T> addOrUpdateOnlineAppointItem(@RequestBody @Validated OnlineAppointItemSettingForm form) {
        return appointItemSettingBiz.addOrUpdateOnlineAppointItem(form);
    }

    /**
     * 查询医生线上可预约项目(门诊端-诊所设置-员工设置)
     * @param dentistId 医生ID
     * @param orgId 门诊ID
     * @return
     */
    @RequestMapping(value = "/setting/{dentistId}/{orgId}",method = RequestMethod.GET)
    public EnableOnlineAppointItemVo findOnlineAppointItemById(@PathVariable(value = "dentistId") Integer dentistId,
                                                               @PathVariable(value = "orgId")Integer orgId) {
        EnableOnlineAppointItemVo enableOnlineAppointItemVo = new EnableOnlineAppointItemVo();
        OnlineAppointItemSettingVo itemSettingVo = appointItemSettingBiz.findItemSettingByDentistId(dentistId, orgId);
        List<OnlineAppointItemVo> lists = itemSettingVo.getLists();
        if (StringHelper.isNotEmpty(lists)) {
            List<Integer> collect = lists.stream().mapToInt(OnlineAppointItemVo::getItemId).boxed().collect(Collectors.toList());
            enableOnlineAppointItemVo.setLists(collect);
        }else {
            enableOnlineAppointItemVo.setLists(new ArrayList<>());
        }
        enableOnlineAppointItemVo.setItemSettingId(itemSettingVo.getItemSettingId());
        enableOnlineAppointItemVo.setDentistId(dentistId);
        enableOnlineAppointItemVo.setOrgId(orgId);
        return enableOnlineAppointItemVo;
    }

    /**
     * 删除预约项目配置
     * @param dentistId 医生ID
     * @param orgId 门诊ID
     * @return 返回状态
     */
    @DeleteMapping("/setting/{dentistId}/{orgId}")
    public ResponseResult<T> deleteOnlineAppointItemSetting(@PathVariable("dentistId") Integer dentistId,
                                                                @PathVariable("orgId") Integer orgId) {
        return appointItemSettingBiz.deleteOnlineAppointItemSetting(dentistId,orgId);
    }
}
