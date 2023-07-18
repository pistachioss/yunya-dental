package com.yunya.modules.patient_central.controller.web;

import com.yunya.feign.patient_central.domain.model.PatientTransferRecordModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientTransferRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: chenlin
 * @date: 2023/7/13 15:23
 * @description: 患者账户转账控制器
 * @since: 1.0.0
 */
@Api(tags = "患者账户转账控制器")
@RequestMapping("/transfer")
@RestController
public class PatientTransferRecordController {

    @Autowired private PatientTransferRecordBiz patientTransferRecordBiz;

    /**
     * 患者间预付款转账
     *
     * @param model
     * @return
     */
    @CurrentUser
    @ApiOperation("患者间预付款转账")
    @PostMapping("/prepayment/interpatient")
    public ResponseResult memberRechargeByPrepayment(@RequestBody @Validated PatientTransferRecordModel model) {
        patientTransferRecordBiz.transferPrepaymentAccount(model);
        return ResponseUtil.success();
    }

    /**
     * 患者预付款充值到会员卡
     *
     * @param model
     * @return
     */
    @CurrentUser
    @ApiOperation("患者预付款充值到会员卡")
    @PostMapping("/prepayment/toMember")
    public ResponseResult prepaymentTransferMember(@RequestBody @Validated PatientTransferRecordModel model) {
        patientTransferRecordBiz.transferPrepaymentAccount(model);
        return ResponseUtil.success();
    }

}
