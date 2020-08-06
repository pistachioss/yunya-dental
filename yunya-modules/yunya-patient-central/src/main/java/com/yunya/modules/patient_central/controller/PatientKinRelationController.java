package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.form.PatientKinRelationForm;
import com.yunya.feign.patient_central.domain.model.PatientKinRelationModel;
import com.yunya.feign.patient_central.domain.query.PatientKinRelationQueryForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientKinRelationBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简单介绍:</br> 亲属关系模块控制器
 *
 * @author: WY
 * @date 2020/7/28 20:45
 * @description: 亲属关系（增删查改）
 * @since: 1.0.0
 */
@Api(value = "亲属关系",description = "亲属关系（增删查改）")
@RestController
@RequestMapping("kin")
public class PatientKinRelationController {

    /** 注入服务 */
    private PatientKinRelationBiz patientKinRelationBiz;

    public PatientKinRelationController(PatientKinRelationBiz patientKinRelationBiz) {
        this.patientKinRelationBiz = patientKinRelationBiz;
    }

    @ApiOperation("查询患者关系亲属列表（可分页)")
    @GetMapping("/findList")
    public ResponseResult findList(@RequestBody PatientKinRelationQueryForm patientKinRelationQueryForm){
        return ResponseUtil.success(patientKinRelationBiz.findList(patientKinRelationQueryForm));
    }

    @CurrentUser
    @ApiOperation("添加患者亲属关系")
    @PostMapping("/add")
    public ResponseResult add(@RequestBody @Validated PatientKinRelationModel patientKinRelationModel ){
        return patientKinRelationBiz.add(patientKinRelationModel);
    }

    @CurrentUser
    @ApiOperation("修改患者亲属关系")
    @PostMapping("/update")
    public ResponseResult update(@RequestBody @Validated PatientKinRelationForm patientKinRelationForm ){
        patientKinRelationBiz.update(patientKinRelationForm);
        return ResponseUtil.success();
    }

    @ApiOperation("删除患者亲属关系")
    @DeleteMapping("/deleteById/{id}")
    public ResponseResult deleteById(@PathVariable("id") Integer id){
        patientKinRelationBiz.deleteById(id);
        return ResponseUtil.success();
    }
}
