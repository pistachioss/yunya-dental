package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.model.MemberBindingRelationInfoModel;
import com.yunya.feign.patient_central.domain.query.PatientMemberRelationQueryForm;
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
@RequestMapping("PatientMember")
public class PatientMemberInfoController {

    /** 注入服务 */
    private PatientMemberInfoBiz patientMemberInfoBiz;

    public PatientMemberInfoController(PatientMemberInfoBiz patientMemberInfoBiz) {
        this.patientMemberInfoBiz = patientMemberInfoBiz;
    }

    @ApiOperation("会员基本信息")
    @GetMapping("/findMemberBaseInfo/{id}")
    public ResponseResult findMemberBaseInfo(@PathVariable("id") Integer id){
        return ResponseUtil.success(patientMemberInfoBiz.findMemberBaseInfo(id));
    }

    @ApiOperation("会员卡关联查询")
    @GetMapping("/findMemberBindingRelation")
    public ResponseResult findMemberBindingRelation(@RequestBody @Validated PatientMemberRelationQueryForm patientMemberRelationQueryForm){
        return ResponseUtil.success(patientMemberInfoBiz.findMemberBindingRelation(patientMemberRelationQueryForm));
    }

    @CurrentUser
    @ApiOperation("添加会员卡关联关系/共享值关联关系")
    @PostMapping("/addMemberBindingRelation")
    public ResponseResult addMemberBindingRelation(@RequestBody MemberBindingRelationInfoModel form){
        patientMemberInfoBiz.addMemberBindingRelation(form);
        return ResponseUtil.success();
    }

    @ApiOperation("删除会员卡关联关系")
    @DeleteMapping("/deleteById/{id}")
    public ResponseResult deleteById(@PathVariable("id") Integer id){
        patientMemberInfoBiz.deleteById(id);
        return ResponseUtil.success();
    }




}
