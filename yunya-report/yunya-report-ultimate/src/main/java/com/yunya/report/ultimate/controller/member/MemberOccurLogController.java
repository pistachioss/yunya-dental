package com.yunya.report.ultimate.controller.member;

import com.yunya.feign.patient_central.domain.vo.web.PatientPublicInfoVo;
import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.vo.MemberExpendLogBizVo;
import com.yunya.feign.report.domain.vo.MemberRechargeLogBizVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.service.MemberOccurLogBiz;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简介:公司端-会员卡充值记录控制层
 *
 * @author: WY
 * @date: 2020/10/24 13:30
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("member")
public class MemberOccurLogController {

    /** 注入服务 */
    @Autowired MemberOccurLogBiz memberOccurLogBiz;

    /**
     * 查询会员充值列表
     * @param memberQueryForm 会员卡充值form
     * @return
     */
    @ApiOperation("查询会员充值列表")
    @PostMapping("/recharge/list")
    public ResponseResult<List<MemberRechargeLogBizVo>> rechargeList(@RequestBody MemberQueryForm memberQueryForm) {
        return ResponseUtil.success(this.memberOccurLogBiz.rechargeList(memberQueryForm));
    }

    /**
     * 查询会员充值列表
     * @param memberQueryForm 会员卡充值form
     * @return
     */
    @ApiOperation("查询会员充值列表")
    @PostMapping("/expend/list")
    public ResponseResult<List<MemberExpendLogBizVo>> expendList(@RequestBody MemberQueryForm memberQueryForm) {
        return ResponseUtil.success(this.memberOccurLogBiz.expendList(memberQueryForm));
    }

    /**
     * 查询会员充值列表
     * @param memberQueryForm 会员卡充值form
     * @return
     *//*
    @ApiOperation("查询会员充值列表")
    @PostMapping("/return/list")
    public ResponseResult<List<MemberRechargeLogBizVo>> returnList(@RequestBody MemberQueryForm memberQueryForm) {
        return ResponseUtil.success(this.memberOccurLogBiz.returnList(memberQueryForm));
    }*/

}