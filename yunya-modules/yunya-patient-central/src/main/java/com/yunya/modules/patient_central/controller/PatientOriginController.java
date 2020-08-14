package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.form.PatientOriginForm;
import com.yunya.feign.patient_central.domain.model.PatientOriginModel;
import com.yunya.feign.patient_central.domain.model.PictureModel;
import com.yunya.feign.patient_central.domain.query.PatientAndStaffListInfoQueryForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.modules.patient_central.biz.PatientOriginBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

/**
 * 简单介绍:</br> 患者来源管理 控制层
 *
 * @author: WY
 * @date 2020/8/5 12:45
 * @description: 患者来源管理
 * @since: 1.0.0
 */
@Api(value = "患者来源",description = "患者来源（增删查改）")
@RestController
@RequestMapping("origin")
public class PatientOriginController {

    /** 注入服务 */
    private PatientOriginBiz patientOriginBiz;

    public PatientOriginController(PatientOriginBiz patientOriginBiz) {
        this.patientOriginBiz = patientOriginBiz;
    }

    @CurrentUser
    @ApiOperation("新建患者来源分类")
    @PostMapping("/add")
    public ResponseResult add( @RequestBody @Validated PatientOriginModel patientOriginModel){
        return patientOriginBiz.add(patientOriginModel);
    }

    @ApiOperation("查询患者来源树状结构列表")
    @GetMapping("/initPatientOriginTree")
    public ResponseResult initPatientOriginTree(){
        return ResponseUtil.success(patientOriginBiz.initPatientOriginTree());
    }

    @CurrentUser
    @ApiOperation("修改患者来源")
    @PostMapping("/update")
    public ResponseResult update(@RequestBody PatientOriginForm patientOriginForm){
        return patientOriginBiz.update(patientOriginForm);
    }

    @ApiOperation("删除患者来源")
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable("id") Integer id){
        return patientOriginBiz.deleteOriginById(id);
    }


    @ApiOperation("模糊查询员工/老患者信息")
    @PostMapping("/originType")
    public ResponseResult findPatientAndStaffListInfo(@RequestBody @Validated PatientAndStaffListInfoQueryForm form){
        return patientOriginBiz.findPatientAndStaffListInfo(form);
    }

    @ApiOperation("模糊查询活动/合作商信息")
    @GetMapping("/originTypeList")
    public ResponseResult findPatientOriginByTypt(){
        return ResponseUtil.success(patientOriginBiz.findPatientOriginByTypt());
    }

}
