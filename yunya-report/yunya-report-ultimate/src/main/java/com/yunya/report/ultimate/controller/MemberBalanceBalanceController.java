package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.vo.BaseMemberBalanceInfoVo;
import com.yunya.feign.report.domain.vo.BasePrepaidBalanceInfoVO;
import com.yunya.feign.report.domain.vo.BaseSpecialPrepaidBalanceInfoVO;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.biz.MemberOccurLogBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2020/11/24 13:29
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "门诊端-患者资料-会员卡预付款")
@RestController
@RequestMapping("balance")
public class MemberBalanceBalanceController {

    /** 注入服务 */
    @Resource
    MemberOccurLogBiz memberOccurLogBiz;


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
     * 会员卡额结存信息列表
     * @param memberQueryForm 查询余额结存form
     * @return List<MemberRechargeLogBizVo>
     */
    @ApiOperation("会员余额结存信息列表")
    @PostMapping("/member/balance")
    public ResponseResult<PageInfo<BaseMemberBalanceInfoVo>> findMemberBalanceList(@RequestBody @Validated MemberQueryForm memberQueryForm) {
        PageInfo<BaseMemberBalanceInfoVo> pageInfo = memberOccurLogBiz.findMemberBalanceList(memberQueryForm);
        if (StringHelper.isNotNull(pageInfo)){
            return ResponseUtil.success(pageInfo);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",pageInfo);
    }

    /**
     * 导出会员卡余额结存记录列表
     *
     * @param response 响应
     * @param memberQueryForm 查询条件
     * @return 就诊配诊记录列表
     */
    @ApiOperation("导出会员卡余额结存记录列表")
    @PostMapping(value = "/member/balance/export", name = "公司端-财务报表-导出会员卡余额结存表")
    public ResponseResult exportMemberBalanceList(HttpServletResponse response, @RequestBody @Validated MemberQueryForm memberQueryForm) throws IOException {
        memberOccurLogBiz.exportMemberBalanceList(response,memberQueryForm);
        return ResponseUtil.success(null);
    }

    /**
     * 预付款额结存信息列表
     * @param memberQueryForm 查询余额结存form
     * @return List<MemberRechargeLogBizVo>
     */
    @ApiOperation("预付款额结存信息列表")
    @PostMapping("/prepaid/balance")
    public ResponseResult<PageInfo<BasePrepaidBalanceInfoVO>> findPrepaidBalanceList(@RequestBody @Validated MemberQueryForm memberQueryForm) {
        PageInfo<BasePrepaidBalanceInfoVO> pageInfo = memberOccurLogBiz.findPrepaidBalanceList(memberQueryForm);
        if (StringHelper.isNotNull(pageInfo)){
            return ResponseUtil.success(pageInfo);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",pageInfo);
    }

    /**
     * 导出预付款余额结存记录列表
     *
     * @param response 响应
     * @param memberQueryForm 查询条件
     * @return 就诊配诊记录列表
     */
    @ApiOperation("导出预付款余额结存记录列表")
    @PostMapping(value = "/prepaid/balance/export")
    public ResponseResult exportPrepaidBalanceList(HttpServletResponse response, @RequestBody @Validated MemberQueryForm memberQueryForm) throws IOException {
        memberOccurLogBiz.exportPrepaidBalanceList(response,memberQueryForm);
        return ResponseUtil.success(null);
    }

    /**
     * 专项预付款额结存信息列表
     * @param memberQueryForm 查询余额结存form
     * @return List<MemberRechargeLogBizVo>
     */
    @ApiOperation("专项预付款余额结存信息列表")
    @PostMapping("/balance/list")
    public ResponseResult<PageInfo<BaseSpecialPrepaidBalanceInfoVO>> findSpecialPrepaidBalanceList(@RequestBody @Validated MemberQueryForm memberQueryForm) {
        PageInfo<BaseSpecialPrepaidBalanceInfoVO> pageInfo = memberOccurLogBiz.findSpecialPrepaidBalanceList(memberQueryForm);
        if (StringHelper.isNotNull(pageInfo)){
            return ResponseUtil.success(pageInfo);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",pageInfo);
    }

    /**
     * 导出专项预付款余额结存记录列表
     *
     * @param response 响应
     * @param memberQueryForm 查询条件
     * @return 就诊配诊记录列表
     */
    @ApiOperation("导出专项预付款余额结存信息记录列表")
    @PostMapping(value = "/export", name = "公司端-财务报表-导出专项预付款余额结存表")
    public ResponseResult exportSpecialPrepaidBalanceList(HttpServletResponse response, @RequestBody @Validated MemberQueryForm memberQueryForm) throws IOException {
        memberOccurLogBiz.exportSpecialPrepaidBalanceList(response, memberQueryForm);
        return ResponseUtil.success(null);
    }
}