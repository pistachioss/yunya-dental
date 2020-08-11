package com.yunya.modules.appointment.controller;

import com.yunya.feign.appointment.domain.form.AppointSplitCheckForm;
import com.yunya.feign.appointment.domain.form.AppointmentSplitForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.AppointmentSplit;
import com.yunya.modules.appointment.biz.AppointmentSplitBiz;
import com.yunya.feign.appointment.domain.model.AppointmentSplitModel;
import com.yunya.feign.appointment.domain.query.AppointmentSplitQuery;
import com.yunya.modules.appointment.vo.AppointmentSplitVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * "时长分解Controller
 *
 * @author yunya-lihuibin
 * @create 2020-07-30 14:15
 * @update yunya-lihuibin    2020-07-30    新建
 */
@RestController
@RequestMapping("appoint_split")
@Api(tags = "时长分解Controller")
public class AppointmentSplitController {

    @Autowired
    private AppointmentSplitBiz appointSpitBiz;

    /**
     * 添加时长分解
     * @param splitModel 数据列表
     * @return
     */
    @ApiOperation(value = "添加时长分解")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult addAppointSplit(@Validated @RequestBody AppointmentSplitModel splitModel) throws ParseException {
        Integer result = appointSpitBiz.insertAppointSplit(splitModel);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"时长分解失败！",null);
        }
        return ResponseUtil.success();
    }

    /**
     * 预约检测（第一次新增时长分解（提交）用）
     * @param form
     * @return
     */
    @ApiOperation(value = "预约检测（第一次新增时长分解（提交）用，第一次只检测时长分解是否正确，数据库不进行实际的新增，整个添加预约表单提交时才会在数据库新增）")
    @PostMapping("/add/check")
    @CurrentUser
    public ResponseResult addAppointSplitCheck(@RequestBody @Validated AppointSplitCheckForm form){
        List<AppointmentSplit> splits = appointSpitBiz.appointSplitCheck(form.getAppointDuration(), form.getSplitList());
        if (splits == null || splits.isEmpty()){
            return ResponseUtil.fail(OperationCodeConstants.PARAMETERS_IS_ILLEGAL,"时长分解有误！",null);
        }
        return ResponseUtil.success(splits);
    }

    /**
     * 根据id删除时长分解
     * @param splitId  条件列表
     * @return
     */
    @ApiOperation(value = " 删除时长分解（新增/修改预约-预约时长分解-删除 用）")
    @DeleteMapping("/delete/{splitId}")
    public ResponseResult delAppointSplit(@PathVariable("splitId") Integer splitId){
        appointSpitBiz.deleteById(splitId);
        return ResponseUtil.success();
    }

    /**
     * 根据条件查询分解预约（时长分解按钮用）
     * @param query 条件列表
     * @return
     */
    @ApiOperation(value = "根据条件查询时长分解（新增/修改预约-时长分解 用）")
    @PostMapping("/findAll")
    public ResponseResult findAppointSplitByExample(@Validated @RequestBody AppointmentSplitQuery query){
        List<AppointmentSplitVo> result = appointSpitBiz.findAppointmentSplitByExample(query);
        return ResponseUtil.success(result);
    }

    /**
     * 修改时长分解（修改预约-预约时长分解-提交 用）
     * @param form  时长分解表单
     * @return
     */
    @ApiOperation(value = "修改时长分解")
    @PutMapping("/update")
    @CurrentUser
    @ApiImplicitParams(
            @ApiImplicitParam(name="AppointmentSplitForm")
    )
    public ResponseResult updateAppointSplit(@RequestBody @Validated AppointmentSplitForm form){
        Integer result = appointSpitBiz.updateAppointSplit(form);
        return ResponseUtil.success();
    }





}
