/**
 * Copyright (C), 2015-2019, XXX有限公司 FileName: AppAppItemController Author: Perter_Chou Date:
 * 2019/8/15 14:20 Description: APP端预约项目Controller History: <author> <time> <version> <desc>
 * Perter_Chou 14:20 Since 1.0 版权信息
 */
package com.yunya.modules.appointment.controller;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.ClinicAppointItem;
import com.yunya.modules.appointment.biz.AppointItemBiz;
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
@RequestMapping("appoit_item")
@Api(tags = "端预约项目Controller")
public class AppointItemController {
    /**
     * 预约项目
     */
    @Autowired
    private AppointItemBiz appItemBiz;

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
        /*List<ClinicAppointItem> appointItems = clinicAppointItemBiz.findByAppointItemId(id);
        if (!StringHelper.isEmpty(appointItems)){
            return ResponseUtil.fail(OperationCodeConstants.DELETE_NOT_ALLOW,"数据不允许被删除！",null);
        }*/

        Integer result = appItemBiz.delAppointItemById(id);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"删除失败！",null);
        }
        return ResponseUtil.success();
    }
}
