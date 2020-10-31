package com.yunya.report.ultimate.controller.member;

import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.vo.BaseMemberExpendLogVo;
import com.yunya.feign.report.domain.vo.BaseMemberRechargeLogVo;
import com.yunya.feign.report.domain.vo.BaseMemberReturnLogVo;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.service.MemberOccurLogBiz;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
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
     * 查询门诊列表
     * @return List<BaseOrganization>
     */
    @ApiOperation("查询门诊列表")
    @PostMapping("/org/list")
    public ResponseResult<List<BaseOrganization>> orgList() {
        return ResponseUtil.success(this.memberOccurLogBiz.orgList());
    }

    /**
     * 查询会员充值列表
     * @param memberQueryForm 会员卡充值form
     * @return List<MemberRechargeLogBizVo>
     */
    @ApiOperation("查询会员充值列表")
    @PostMapping("/recharge/list")
    public ResponseResult<List<BaseMemberRechargeLogVo>> memberRechargeList(@RequestBody MemberQueryForm memberQueryForm) throws ParseException {
        List<BaseMemberRechargeLogVo> baseMemberRechargeLogVos = memberOccurLogBiz.memberRechargeList(memberQueryForm);
        if (StringHelper.isNotEmpty(baseMemberRechargeLogVos)){
            return ResponseUtil.success(baseMemberRechargeLogVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",baseMemberRechargeLogVos);
    }

    /**
     * 查询会员消费列表
     * @param memberQueryForm 会员卡消费form
     * @return List<MemberExpendLogBizVo>
     */
    @ApiOperation("查询会员消费列表")
    @PostMapping("/expend/list")
    public ResponseResult<List<BaseMemberExpendLogVo>> memberExpendList(@RequestBody MemberQueryForm memberQueryForm) throws ParseException {
        List<BaseMemberExpendLogVo> baseMemberExpendLogVos = memberOccurLogBiz.memberExpendList(memberQueryForm);
        if (StringHelper.isNotEmpty(baseMemberExpendLogVos)){
            return ResponseUtil.success(baseMemberExpendLogVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",baseMemberExpendLogVos);
    }


    /**
     * 查询会员退费列表
     * @param memberQueryForm 会员卡退费form
     * @return List<MemberRechargeLogBizVo>
     * @throws ParseException
     */
    @ApiOperation("查询会员退费列表")
    @PostMapping("/return/list")
    public ResponseResult<List<BaseMemberReturnLogVo>> memberReturnList(@RequestBody MemberQueryForm memberQueryForm) throws ParseException {
        List<BaseMemberReturnLogVo> baseMemberReturnLogVos = memberOccurLogBiz.memberReturnList(memberQueryForm);
        if (StringHelper.isNotEmpty(baseMemberReturnLogVos)){
            return ResponseUtil.success(baseMemberReturnLogVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",baseMemberReturnLogVos);
    }

}