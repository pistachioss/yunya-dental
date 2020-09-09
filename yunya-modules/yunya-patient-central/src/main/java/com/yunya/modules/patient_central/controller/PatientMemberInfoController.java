package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.form.CardRelationForm;
import com.yunya.feign.patient_central.domain.form.CardTypeForm;
import com.yunya.feign.patient_central.domain.model.MemberBindingRelationInfoModel;
import com.yunya.feign.patient_central.domain.model.MemberRechargeModel;
import com.yunya.feign.patient_central.domain.model.MemberReturnRecordModel;
import com.yunya.feign.patient_central.domain.model.OpenCardModel;
import com.yunya.feign.patient_central.domain.query.MemberExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.MemberReturnRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientMemberRelationQueryForm;
import com.yunya.feign.patient_central.domain.query.RechargeRecordQueryForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientMemberInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简单介绍:</br> 患者会员卡信息 控制层
 *
 * @author: WY
 * @date 2020/7/30 13:22
 * @description: 患者会员卡信息增删改查
 * @since: 1.0.0
 */

@Api(value = "患者会员卡信息",description = "患者会员卡信息（增删查改）")
@RestController
@RequestMapping("patientMember")
public class PatientMemberInfoController {

    /** 注入服务 */
    private PatientMemberInfoBiz patientMemberInfoBiz;

    public PatientMemberInfoController(PatientMemberInfoBiz patientMemberInfoBiz) {
        this.patientMemberInfoBiz = patientMemberInfoBiz;
    }

    @ApiOperation("会员基本信息")
    @GetMapping("/basicInformation/{id}")
    public ResponseResult findMemberBaseInfo(@PathVariable("id") Integer id){
        return ResponseUtil.success(patientMemberInfoBiz.findMemberBaseInfo(id));
    }

    @ApiOperation("会员卡关联查询")
    @PostMapping("/relatedInformation")
    public ResponseResult findMemberBindingRelation(@RequestBody @Validated PatientMemberRelationQueryForm patientMemberRelationQueryForm){
        return ResponseUtil.success(patientMemberInfoBiz.findMemberBindingRelation(patientMemberRelationQueryForm));
    }

    @CurrentUser
    @ApiOperation("添加会员卡关联关系/共享值关联关系")
    @PostMapping("/addMemberBindingRelation")
    public ResponseResult addMemberBindingRelation(@RequestBody MemberBindingRelationInfoModel form){
        return patientMemberInfoBiz.addMemberBindingRelation(form);
    }

    @ApiOperation("删除会员卡关联关系")
    @DeleteMapping("/delete")
    public ResponseResult deleteById(@RequestBody CardRelationForm cardRelationForm){
        patientMemberInfoBiz.deleteRelationById(cardRelationForm);
        return ResponseUtil.success();
    }

    @CurrentUser
    @ApiOperation("开卡")
    @PostMapping("/openCard")
    public ResponseResult addMemberCard(@RequestBody OpenCardModel openCardModel){
        patientMemberInfoBiz.addMemberCard(openCardModel);
        return ResponseUtil.success();
    }

    @CurrentUser
    @ApiOperation("会员卡变更")
    @PostMapping("/change")
    public ResponseResult changeType(@RequestBody @Validated CardTypeForm form){
        patientMemberInfoBiz.changeType(form);
        return ResponseUtil.success();
    }

    @ApiOperation("变更记录")
    @GetMapping("/changeLog/{cardNumber}")
    public ResponseResult changeLog(@PathVariable(value = "cardNumber") String cardNumber){
        return ResponseUtil.success(patientMemberInfoBiz.changeLog(cardNumber));
    }

    @CurrentUser
    @ApiOperation("充值")
    @PostMapping("/recharge")
    public ResponseResult Recharge(@RequestBody MemberRechargeModel memberRechargeModel ){
        patientMemberInfoBiz.Recharge(memberRechargeModel);
        return ResponseUtil.success();
    }


    @CurrentUser
    @ApiOperation("充值记录")
    @PostMapping("/rechargeRecord")
    public ResponseResult RechargeRecord(@RequestBody RechargeRecordQueryForm form ){
        return ResponseUtil.success(patientMemberInfoBiz.RechargeRecord(form));
    }

    @CurrentUser
    @ApiOperation("退费")
    @PostMapping("/refund")
    public ResponseResult refund(@RequestBody MemberReturnRecordModel model ){
        patientMemberInfoBiz.refund(model);
        return ResponseUtil.success();
    }

    @CurrentUser
    @ApiOperation("退费记录")
    @PostMapping("/refundList")
    public ResponseResult refundList(@RequestBody MemberReturnRecordQueryForm queryForm ){
        return ResponseUtil.success(patientMemberInfoBiz.refundList(queryForm));
    }

    @CurrentUser
    @ApiOperation("消费记录")
    @PostMapping("/expendList")
    public ResponseResult expendList(@RequestBody MemberExpendRecordQueryForm queryForm ){
        return ResponseUtil.success(patientMemberInfoBiz.expendList(queryForm));
    }

}
