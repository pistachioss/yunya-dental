package com.yunya.modules.appointment.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.crypto.provider.HmacPKCS12PBESHA1;
import com.yunya.feign.appointment.domain.form.AppointModifyRecordForm;
import com.yunya.feign.appointment.domain.model.AppointModifyRecordModel;
import com.yunya.feign.appointment.domain.query.AppointModifyRecordQuery;
import com.yunya.feign.appointment.vo.AppointModifyRecordVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.AppointmentModifyRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约修改记录Controller(记录修改的预约医生和预约时间)
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 11:03
 * @update yunya-lihuibin    2020-08-10    新建
 */
@Api(tags = "预约修改记录Controller(记录修改的预约医生和预约时间)")
@RestController
@RequestMapping("appoint/modify_record")
public class AppointModifyRecordController {
    @Autowired
    private AppointmentModifyRecordBiz appointmentModifyRecordBiz;

    /**
     * 新增预约修改记录
     * @param model
     * @return
     */
    @ApiOperation(value = "新增预约修改记录")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult addAppointModifyRecord(@RequestBody @Validated AppointModifyRecordModel model){
        Integer result = appointmentModifyRecordBiz.addAppointModifyRecord(model);
        return ResponseUtil.success();
    }

    /**
     * 根据id删除预约修改记录
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id删除预约修改记录")
    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteAppointModifyRecord(@PathVariable("id") Integer id){
        appointmentModifyRecordBiz.deleteAppointModifyRecordById(id);
        return ResponseUtil.success();
    }

    /**
     * 编辑预约修改内容
     * @param form
     * @return
     */
    @ApiOperation(value = "编辑预约修改内容")
    @PutMapping("/update")
    @CurrentUser
    public ResponseResult updateAppointModifyRecord(@RequestBody @Validated AppointModifyRecordForm form){
        Integer result = appointmentModifyRecordBiz.updateAppointModify(form);
        return ResponseUtil.success();
    }


    /**
     * 根据id查询预约修改记录
     * @param id  预约修改记录id
     * @return
     */
    @ApiOperation(value = "根据id查询预约修改记录")
    @GetMapping("/find/{id}")
    public ResponseResult findApointModifyRecordById(@PathVariable("id") Integer id){
        AppointModifyRecordVo appointModifyRecordVo = appointmentModifyRecordBiz.findAppointModifyRecordById(id);
        return ResponseUtil.success(appointModifyRecordVo);
    }

    /**
     * 根据条件查询预约修改记录
     * @param query  查询条件
     * @return
     */
    @ApiOperation(value = "根据条件查询预约修改记录")
    @GetMapping("/find")
    public ResponseResult findApointModifyRecordByExample(@RequestBody @Validated AppointModifyRecordQuery query){
        if (query.getWhetherPage()){
            PageHelper.startPage(query.getPageNum(),query.getPageNum());
        }
        List<AppointModifyRecordVo> appointModifyRecordVo = appointmentModifyRecordBiz.findAppointModifyRecordByExample(query);
        return ResponseUtil.success(new PageInfo<>(appointModifyRecordVo));
    }
}
