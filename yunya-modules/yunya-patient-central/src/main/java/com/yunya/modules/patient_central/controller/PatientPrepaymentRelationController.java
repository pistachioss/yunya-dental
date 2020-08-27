//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.model.PatientPrepaymentRelationModel;
import com.yunya.feign.patient_central.domain.model.PrepaidMeturnRecordModel;
import com.yunya.feign.patient_central.domain.model.PrepaidRechargeModel;
import com.yunya.feign.patient_central.domain.query.PrepaidMeturnRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PrepaidRechargeRecordQueryForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientPrepaymentRelationBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(
        value = "患者预付款",
        description = "患者预付款（增删查改）"
)
@RestController
@RequestMapping({"prepayment"})
public class PatientPrepaymentRelationController {
    private PatientPrepaymentRelationBiz patientPrepaymentBiz;

    public PatientPrepaymentRelationController(PatientPrepaymentRelationBiz patientPrepaymentBiz) {
        this.patientPrepaymentBiz = patientPrepaymentBiz;
    }

    @ApiOperation("账户基本信息")
    @GetMapping({"/PrepaidAccountBaseInfo/{id}"})
    public ResponseResult findPrepaymentInfo(@PathVariable("id") Integer id) {
        return ResponseUtil.success(this.patientPrepaymentBiz.findPrepaymentInfo(id));
    }

    @CurrentUser
    @ApiOperation("新增关联")
    @PostMapping({"/PrepaymentLink"})
    public ResponseResult addPrepaymentLink(@RequestBody PatientPrepaymentRelationModel model) {
        this.patientPrepaymentBiz.addPrepaymentLink(model);
        return ResponseUtil.success();
    }

    @ApiOperation("查询关联")
    @GetMapping({"/findPrepaymentLink/{id}"})
    public ResponseResult findPrepaymentLink(@PathVariable("id") Integer id) {
        return ResponseUtil.success(this.patientPrepaymentBiz.findPrepaymentLink(id));
    }

    @ApiOperation("删除")
    @DeleteMapping({"/delete/{id}"})
    public ResponseResult deletePrepaymentLink(@PathVariable("id") Integer id) {
        this.patientPrepaymentBiz.deletePrepaymentLink(id);
        return ResponseUtil.success();
    }

    @CurrentUser
    @ApiOperation("充值")
    @PostMapping({"/Recharge"})
    public ResponseResult Recharge(@RequestBody PrepaidRechargeModel memberRechargeModel) {
        this.patientPrepaymentBiz.Recharge(memberRechargeModel);
        return ResponseUtil.success();
    }

    @ApiOperation("充值记录")
    @PostMapping({"/RechargeRecord"})
    public ResponseResult RechargeRecord(@RequestBody PrepaidRechargeRecordQueryForm form) {
        return ResponseUtil.success(this.patientPrepaymentBiz.RechargeRecord(form));
    }

    @ApiOperation("退费")
    @PostMapping("/refund")
    public ResponseResult refund(@RequestBody PrepaidMeturnRecordModel model ){
        patientPrepaymentBiz.refund(model);
        return ResponseUtil.success();
    }

    @ApiOperation("退费记录")
    @PostMapping("/refundList")
    public ResponseResult refundList(@RequestBody PrepaidMeturnRecordQueryForm queryForm ){
        return ResponseUtil.success(patientPrepaymentBiz.refundList(queryForm));
    }
}
