/**
 * Copyright (C), 2015-2019, XXX有限公司 FileName: AppAppItemController Author: Perter_Chou Date:
 * 2019/8/15 14:20 Description: APP端预约项目Controller History: <author> <time> <version> <desc>
 * Perter_Chou 14:20 Since 1.0 版权信息
 */
package com.yunya.modules.appointment.controller;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.AppointItemModifyForm;
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
 * 〈预约项目详情Controller（公司端）
 *
 * @author Peter_Chou
 * @create 2019/8/15
 * @since 1.0.0
 */
@RestController
@RequestMapping("appoint_item")
@Api(tags = "预约项目详情Controller（公司端）")
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
            @PathVariable("compClinId") Integer compClinId) {
        List<AppointmentItemEnableModelVo> availableAppItemList = appItemBiz.findAvailableAppItemList(compClinId);
        return ResponseUtil.success(availableAppItemList);
    }



    /**
     * 通过预约项目id查询配置适用门诊列表(公司端-预约项目-配置)
     * @param query 查询条件
     * @return
     */
    @ApiOperation(value = "通过预约项目id查询配置适用门诊列表(公司端-预约项目-配置)")
    @PostMapping("/find/item_config")
    public ResponseResult findAppointItemAndOrgInfo(@RequestBody @Validated AppointItemConfigQuery query){
        if (query.getWhetherPage()){
            PageHelper.startPage(query.getPageNum(),query.getPageNum());
        }
        List<ClinicAppointItemConfigVo> clinicAppointItemConfigVos = clinicAppointItemBiz.findByAppointItemId(query.getAppointItemId());
        return ResponseUtil.success(new PageInfo<>(clinicAppointItemConfigVos));
    }

    /**
     * 新增门诊预约项目(公司端-预约项目-新增)
     *
     * @param appItemForm 预约项目Form
     */
    @ApiOperation(value = "新增门诊预约项目(公司端-预约项目-新增)")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult saveAppItem(@RequestBody @Validated AppointmentItemModel appItemForm) {
        return appItemBiz.insertAppointItem(appItemForm);
    }

    /**
     * 预约项目检索搜索（公司端-查询）
     *
     * @param baseQueryForm 查询条件
     * @return
     */
    @ApiOperation(value = "预约项目检索搜索（公司端-查询）")
    @PostMapping("/search")
    public ResponseResult searchAppItem(@Validated @RequestBody AppointItemQuery baseQueryForm) {

        if (baseQueryForm.getWhetherPage()){
            PageHelper.startPage(baseQueryForm.getPageNum(),baseQueryForm.getPageSize());
        }
        List<AppointmentItemVo>  appointmentItemVos = appItemBiz.findByAppItemName(baseQueryForm);
        return ResponseUtil.success(new PageInfo<>(appointmentItemVos));
    }

    /**
     * 修改门诊预约项目（公司端-预约项目-操作-修改-提交）
     *
     * @param appItemForm 预约项目Form
     */
    @ApiOperation(value = "修改门诊预约项目（公司端-预约项目-操作-修改-提交）")
    @PutMapping("/appoint_modify")
    @CurrentUser
    public ResponseResult update(@RequestBody @Validated AppointItemModifyForm appItemForm) {
        Integer integer = appItemBiz.updateAppItem(appItemForm);
        if (integer > 0) {
            return ResponseUtil.success();
        }
        return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL, "修改失败",null);
    }

    /**
     *  修改预约项目适用门诊（公司端-预约项目-配置-是否适用）
     * @param form 修改数据表单
     * @return
     */
    @ApiOperation(value = "修改预约项目适用门诊（公司端-预约项目-配置-是否适用）")
    @PutMapping("/config")
    @CurrentUser
    public ResponseResult addAndUpdate(@RequestBody @Validated ClinicAppointItemForm form){
        Integer result = clinicAppointItemBiz.addAndUpdateAppItem(form);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"修改失败！",null);
        }
        return ResponseUtil.success();
    }

    /**
     *  可预约项目统一设置配置（公司端-预约项目-配置-统一设置适用）
     * @return
     */
    @ApiOperation(value = "可预约项目统一设置配置（公司端-预约项目-配置-统一设置适用）")
    @PutMapping("/config/batch")
    @CurrentUser
    public ResponseResult updateAppointItemWithBatch(@RequestBody @Validated AppointItemBatchConfigModel configModel){
        Integer result = clinicAppointItemBiz.updateAppointItemWithBatch(configModel.getAppointItemId());
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"设置配置失败！",null);
        }
        return ResponseUtil.success();
    }

    /**
     * 根据id删除项目（公司端--预约项目-操作-删除）
     * @param id  预约项目id
     * @return
     */
    @ApiOperation(value = "根据id删除项目（公司端--预约项目-操作-删除）")
    @DeleteMapping("/del/{id}")
    public ResponseResult del(@PathVariable("id") Integer id){

        // 检测要删除的预约项目是否已被预约，如果已被预约则不能删除
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
