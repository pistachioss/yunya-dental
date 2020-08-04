/**
 * Copyright (C), 2015-2019, XXX有限公司 FileName: AppAppItemController Author: Perter_Chou Date:
 * 2019/8/15 14:20 Description: APP端预约项目Controller History: <author> <time> <version> <desc>
 * Perter_Chou 14:20 Since 1.0 版权信息
 */
package com.yunya.modules.appointment.controller;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.ClinicAppointItemForm;
import com.yunya.feign.appointment.domain.model.AppointItemBatchConfigModel;
import com.yunya.feign.appointment.domain.query.AppointItemConfigQuery;
import com.yunya.feign.appointment.domain.query.AppointmentQuery;
import com.yunya.feign.appointment.vo.AppointmentVo;
import com.yunya.feign.appointment.vo.ClinicAppointItemConfigVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.AppointItemBiz;
import com.yunya.modules.appointment.biz.AppointmentBiz;
import com.yunya.modules.appointment.biz.ClinicAppointItemBiz;
import com.yunya.feign.appointment.domain.model.AppointmentItemModel;
import com.yunya.feign.appointment.domain.query.AppointItemQuery;
import com.yunya.modules.appointment.vo.AppointmentItemEnableModelVo;
import com.yunya.modules.appointment.vo.AppointmentItemVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈APP端预约项目Controller〉
 *
 * @author Peter_Chou
 * @create 2019/8/15
 * @since 1.0.0
 */
@RestController
@RequestMapping("appoint_item")
@Api(tags = "端预约项目Controller")
public class AppointItemController {
    /**
     * 预约项目
     */
    @Autowired
    private AppointItemBiz appItemBiz;

    @Autowired
    private AppointmentBiz appointmentBiz;

    @Autowired
    private ClinicAppointItemBiz clinicAppointItemBiz;

    /**
     * 查询门诊可用的预约项目
     *
     * @param compClinId
     * @return
     */
    @ApiOperation(value = "查询门诊可用的预约项目")
    @GetMapping("/list/{compClinId}")
    public ResponseResult findClinicAvailableItem(
            @PathVariable("compClinId") String compClinId) {
        List<AppointmentItemEnableModelVo> availableAppItemList = appItemBiz.findAvailableAppItemList(compClinId);
        return ResponseUtil.success(availableAppItemList);
    }

    /**
     * 通过预约项目id查询配置适用门诊列表
     * @param query 查询条件
     * @return
     */
    @ApiOperation(value = "通过预约项目id查询配置适用门诊列表")
    @PostMapping("/find/item_config")
    public ResponseResult findAppointItemAndOrgInfo(@RequestBody @Validated AppointItemConfigQuery query){
        List<ClinicAppointItemConfigVo> clinicAppointItemConfigVos = clinicAppointItemBiz.findByAppointItemId(query.getAppointItemId());
        if (query.getWhetherPage()){
            return ResponseUtil.success(new PageInfo<>(clinicAppointItemConfigVos));
        }
        return ResponseUtil.success(clinicAppointItemConfigVos);
    }

    /**
     * 新增门诊预约项目
     *
     * @param appItemForm 预约项目Form
     */
    @ApiOperation(value = "新增门诊预约项目")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult saveAppItem(@RequestBody @Validated AppointmentItemModel appItemForm) {
        Integer integer = appItemBiz.insertAppointItem(appItemForm);
        if (integer > 0) {
            return ResponseUtil.success();
        }
        return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"预约项目添加失败！",null);
    }

    /**
     * 预约搜索
     *
     * @param baseQueryForm 查询条件
     * @return
     */
    @ApiOperation(value = "预约搜索")
    @PostMapping("/search")
    public ResponseResult searchAppItem(@Validated @RequestBody AppointItemQuery baseQueryForm) {
        List<AppointmentItemVo>  appointmentItemVos = appItemBiz.findByAppItemName(baseQueryForm);
        if (baseQueryForm.getWhetherPage()){
            return ResponseUtil.success(new PageInfo(appointmentItemVos));
        }
        return ResponseUtil.success(appointmentItemVos);
    }

    /**
     * 修改门诊预约项目（不启用）
     *
     * @param id          门诊预约类id
     * @param appItemForm 预约项目Form
     */
    @ApiOperation(value = "修改门诊预约项目")
    @PutMapping("/update/{id}")
    @CurrentUser
    public ResponseResult update(@PathVariable("id") Integer id, @RequestBody @Validated AppointmentItemModel appItemForm) {
        Integer integer = appItemBiz.updateAppItem(id, appItemForm);
        if (integer > 0) {
            return ResponseUtil.success();
        }
        return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL, "修改失败",null);
    }

    /**
     *  修改预约项目适用门诊（公司端可用不可用）
     * @param form 修改数据表单
     * @return
     */
    @ApiOperation(value = "修改预约项目适用门诊（公司端可用不可用）")
    @PostMapping("/update")
    @CurrentUser
    public ResponseResult addAndUpdate(@RequestBody @Validated ClinicAppointItemForm form){
        Integer result = clinicAppointItemBiz.addAndUpdateAppItem(form);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"修改失败！",null);
        }
        return ResponseUtil.success();
    }

    /**
     *  可预约项目统一设置配置（公司端）
     * @return
     */
    @ApiOperation(value = "可预约项目统一设置配置（公司端）")
    @PostMapping("/update/batch")
    @CurrentUser
    public ResponseResult updateAppointItemWithBatch(@RequestBody @Validated AppointItemBatchConfigModel configModel){
        clinicAppointItemBiz.updateAppointItemWithBatch(configModel.getAppointItemId(),configModel.getBytes());
        return ResponseUtil.success();
    }

    /**
     * 根据id删除项目
     * @param id
     * @return
     */
    @ApiOperation(value = "根据条件删除项目")
    @DeleteMapping("/del/{id}")
    public ResponseResult del(@PathVariable("id") Integer id){

        if (id == null || id <= 0){
            return ResponseUtil.fail(OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY,"id参数非法！",null);
        }
        // TODO 检测要删除的预约项目是否已被预约，如果已被预约则不能删除
        AppointmentQuery query = new AppointmentQuery();
        query.setClinicAppointItemId(id);
        List<AppointmentVo> appointmentByExample = appointmentBiz.findAppointmentByExample(query);
        if (appointmentByExample != null && !appointmentByExample.isEmpty()){
            return ResponseUtil.fail(OperationCodeConstants.DELETE_NOT_ALLOW,"数据不允许被删除！",null);
        }

        Integer result = appItemBiz.delAppointItemById(id);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"删除失败！",null);
        }
        return ResponseUtil.success();
    }
}
