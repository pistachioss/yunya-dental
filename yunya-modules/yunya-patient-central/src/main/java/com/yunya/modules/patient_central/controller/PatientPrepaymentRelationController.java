//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.model.PatientPrepaymentRelationModel;
import com.yunya.feign.patient_central.domain.model.PrepaidMeturnRecordModel;
import com.yunya.feign.patient_central.domain.model.PrepaidRechargeModel;
import com.yunya.feign.patient_central.domain.query.PrepaidExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PrepaidMeturnRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PrepaidRechargeRecordQueryForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientPrepaymentRelationBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping({"/prepaidAccountBaseInfo/{id}"})
    public ResponseResult findPrepaymentInfo(@PathVariable("id") Integer id) {
        return ResponseUtil.success(this.patientPrepaymentBiz.findPrepaymentInfo(id));
    }

    @CurrentUser
    @ApiOperation("新增关联")
    @PostMapping({"/prepaymentLink"})
    public ResponseResult addPrepaymentLink(@RequestBody PatientPrepaymentRelationModel model) {
        return this.patientPrepaymentBiz.addPrepaymentLink(model);
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
    @PostMapping({"/recharge"})
    public ResponseResult Recharge(@RequestBody PrepaidRechargeModel memberRechargeModel) {
        this.patientPrepaymentBiz.Recharge(memberRechargeModel);
        return ResponseUtil.success();
    }

    @CurrentUser
    @ApiOperation("充值记录")
    @PostMapping({"/rechargeRecord"})
    public ResponseResult RechargeRecord(@RequestBody PrepaidRechargeRecordQueryForm form) {
        return ResponseUtil.success(this.patientPrepaymentBiz.RechargeRecord(form));
    }

    @CurrentUser
    @ApiOperation("退费")
    @PostMapping("/refund")
    public ResponseResult refund(@RequestBody PrepaidMeturnRecordModel model ){
        patientPrepaymentBiz.refund(model);
        return ResponseUtil.success();
    }

    @CurrentUser
    @ApiOperation("退费记录")
    @PostMapping("/refundList")
    public ResponseResult refundList(@RequestBody PrepaidMeturnRecordQueryForm queryForm ){
        return ResponseUtil.success(patientPrepaymentBiz.refundList(queryForm));
    }

    /*@CurrentUser
    @ApiOperation("消费")
    @PostMapping("/expend")
    public ResponseResult expend(@RequestBody MemberExpendRecordModel model ){
        return patientMemberInfoBiz.expend(model);
    }*/

    @CurrentUser
    @ApiOperation("消费记录")
    @PostMapping("/expendList")
    public ResponseResult expendList(@RequestBody PrepaidExpendRecordQueryForm queryForm ){
        return ResponseUtil.success(patientPrepaymentBiz.expendList(queryForm));
    }
}
