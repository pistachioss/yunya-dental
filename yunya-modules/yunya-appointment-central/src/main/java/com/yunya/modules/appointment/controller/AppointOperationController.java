package com.yunya.modules.appointment.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.AppointOperationForm;
import com.yunya.feign.appointment.domain.model.AppointOperationModel;
import com.yunya.feign.appointment.domain.query.AppointOperationQuery;
import com.yunya.feign.appointment.vo.AppointOperationRecordVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.AppointmentOperateRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约操作记录Controller
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 13:59
 * @update yunya-lihuibin    2020-08-10    新建
 */
@Api(tags = "预约操作记录Controller")
@RestController
@RequestMapping("appoint/operatioin_record")
public class AppointOperationController {
    @Autowired
    private AppointmentOperateRecordBiz operateRecordBiz;

    /**
     * 新增预约操作记录
     * @param model
     * @return
     */
    @ApiOperation(value = "新增预约操作记录")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult addAppointOperationRecord(@RequestBody @Validated AppointOperationModel model){
        operateRecordBiz.insertAppointmentOperateRecord(model);
        return ResponseUtil.success();
    }

    /**
     * 根据id删除预约操作记录
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id删除预约操作记录")
    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteAppointOperationRecordById(@PathVariable("id") Integer id){
        operateRecordBiz.deleteAppointOperatioinById(id);
        return ResponseUtil.success();
    }

    /**
     * 修改预约操作记录
     * @param form
     * @return
     */
    @ApiOperation(value = "修改预约操作记录")
    @PutMapping("/update")
    @CurrentUser
    public ResponseResult updateAppointOperationRecord(@RequestBody @Validated AppointOperationForm form){
        operateRecordBiz.updateAppointOperationRecord(form);
        return ResponseUtil.success();
    }

    /**
     * 条件查询预约操作记录
     * @param query
     * @return
     */
    @ApiOperation(value = "条件查询预约操作记录")
    @GetMapping("/find")
    public ResponseResult findAppointOperationRecordAll(AppointOperationQuery query){
        if (query.getWhetherPage()){
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        List<AppointOperationRecordVo> appointOperationRecordVoList = operateRecordBiz.findAppointOperationRecordByExample(query);
        return ResponseUtil.success(new PageInfo<>(appointOperationRecordVoList));
    }


}
