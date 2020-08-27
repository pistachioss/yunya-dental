//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.form.PatientOriginForm;
import com.yunya.feign.patient_central.domain.model.PatientOriginModel;
import com.yunya.feign.patient_central.domain.query.OriginTypeQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientAndStaffListInfoQueryForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientOriginBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(
        value = "患者来源",
        description = "患者来源（增删查改）"
)
@RestController
@RequestMapping({"origin"})
public class PatientOriginController {
    private PatientOriginBiz patientOriginBiz;

    public PatientOriginController(PatientOriginBiz patientOriginBiz) {
        this.patientOriginBiz = patientOriginBiz;
    }

    @CurrentUser
    @ApiOperation("新建患者来源分类")
    @PostMapping({"/add"})
    public ResponseResult add(@RequestBody @Validated PatientOriginModel patientOriginModel) {
        return this.patientOriginBiz.add(patientOriginModel);
    }

    @ApiOperation("查询患者来源树状结构列表")
    @GetMapping({"/initPatientOriginTree"})
    public ResponseResult initPatientOriginTree() {
        return ResponseUtil.success(this.patientOriginBiz.initPatientOriginTree());
    }

    @CurrentUser
    @ApiOperation("修改患者来源")
    @PostMapping({"/update"})
    public ResponseResult update(@RequestBody PatientOriginForm patientOriginForm) {
        return this.patientOriginBiz.update(patientOriginForm);
    }

    @ApiOperation("删除患者来源")
    @DeleteMapping({"/delete/{id}"})
    public ResponseResult delete(@PathVariable("id") Integer id) {
        return this.patientOriginBiz.deleteOriginById(id);
    }

    @ApiOperation("模糊查询员工/老患者信息")
    @PostMapping({"/originType"})
    public ResponseResult findPatientAndStaffListInfo(@RequestBody @Validated PatientAndStaffListInfoQueryForm form) {
        return this.patientOriginBiz.findPatientAndStaffListInfo(form);
    }

    @ApiOperation("查询活动/合作商信息")
    @PostMapping({"/originTypeList"})
    public ResponseResult findPatientOriginByTypt(@RequestBody OriginTypeQueryForm form) {
        return ResponseUtil.success(this.patientOriginBiz.findPatientOriginByTypt(form));
    }

    @ApiOperation("查询患者来源类型")
    @GetMapping({"/originalType"})
    public ResponseResult findoriginalType() {
        return ResponseUtil.success(this.patientOriginBiz.originalType());
    }
}
