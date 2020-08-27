//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(
        value = "亲属关系",
        description = "亲属关系（增删查改）"
)
@RestController
@RequestMapping({"kin"})
public class PatientKinRelationController {
    private PatientKinRelationBiz patientKinRelationBiz;

    public PatientKinRelationController(PatientKinRelationBiz patientKinRelationBiz) {
        this.patientKinRelationBiz = patientKinRelationBiz;
    }

    @ApiOperation("查询患者关系亲属列表（可分页)")
    @PostMapping({"/findList"})
    public ResponseResult findList(@RequestBody PatientKinRelationQueryForm patientKinRelationQueryForm) {
        return ResponseUtil.success(this.patientKinRelationBiz.findList(patientKinRelationQueryForm));
    }

    @CurrentUser
    @ApiOperation("添加患者亲属关系")
    @PostMapping({"/add"})
    public ResponseResult add(@RequestBody @Validated PatientKinRelationModel patientKinRelationModel) {
        return this.patientKinRelationBiz.add(patientKinRelationModel);
    }

    @CurrentUser
    @ApiOperation("修改患者亲属关系")
    @PostMapping({"/update"})
    public ResponseResult update(@RequestBody @Validated PatientKinRelationForm patientKinRelationForm) {
        this.patientKinRelationBiz.update(patientKinRelationForm);
        return ResponseUtil.success();
    }

    @ApiOperation("删除患者亲属关系")
    @DeleteMapping({"/deleteById/{id}"})
    public ResponseResult deleteById(@PathVariable("id") Integer id) {
        this.patientKinRelationBiz.deleteById(id);
        return ResponseUtil.success();
    }
}
