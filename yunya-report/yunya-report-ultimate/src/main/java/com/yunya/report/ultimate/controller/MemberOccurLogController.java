package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.vo.BaseMemberExpendLogVo;
import com.yunya.feign.report.domain.vo.BaseMemberRechargeLogVo;
import com.yunya.feign.report.domain.vo.BaseMemberReturnLogVo;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.biz.MemberOccurLogBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
@Api(tags = "公司端-数据记录-会员卡充值记录")
@RestController
@RequestMapping("member")
public class MemberOccurLogController {

    /** 注入服务 */
    @Autowired
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
     * 查询会员充值列表
     * @param memberQueryForm 会员卡充值form
     * @return List<MemberRechargeLogBizVo>
     */
    @ApiOperation("查询会员充值列表")
    @PostMapping("/recharge/list")
    public ResponseResult<PageInfo<BaseMemberRechargeLogVo>> memberRechargeList(@RequestBody MemberQueryForm memberQueryForm) throws ParseException {
        PageInfo<BaseMemberRechargeLogVo> baseMemberRechargeLogVos = memberOccurLogBiz.memberRechargeList(memberQueryForm);
        if (StringHelper.isNotNull(baseMemberRechargeLogVos)){
            return ResponseUtil.success(baseMemberRechargeLogVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",baseMemberRechargeLogVos);
    }

    /**
     * 导出会员充值列表
     *
     * @param response 响应
     * @param memberQueryForm 查询条件
     * @return
     */
    @ApiOperation("导出会员充值列表")
    @PostMapping(value = "/recharge/export", name = "公司端-数据记录-会员卡充值记录-导出")
    public ResponseResult<T> exportMemberRechargeList(HttpServletResponse response, @RequestBody @Validated MemberQueryForm memberQueryForm) throws IOException, ParseException {
        memberOccurLogBiz.exportMemberRechargeList(response,memberQueryForm);
        return ResponseUtil.success(null);
    }


    /**
     * 查询会员消费列表
     * @param memberQueryForm 会员卡消费form
     * @return List<MemberExpendLogBizVo>
     */
    @ApiOperation("查询会员消费列表")
    @PostMapping("/expend/list")
    public ResponseResult<PageInfo<BaseMemberExpendLogVo>> memberExpendList(@RequestBody MemberQueryForm memberQueryForm) throws ParseException {
        PageInfo<BaseMemberExpendLogVo> baseMemberExpendLogVos = memberOccurLogBiz.memberExpendList(memberQueryForm);
        if (StringHelper.isNotNull(baseMemberExpendLogVos)){
            return ResponseUtil.success(baseMemberExpendLogVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,  "暂无相关数据",baseMemberExpendLogVos);
    }

    /**
     * 导出会员消费列表
     *
     * @param response 响应
     * @param memberQueryForm 查询条件
     * @return
     */
    @ApiOperation("导出会员消费列表")
    @PostMapping(value = "/expend/export", name = "公司端-数据记录-会员卡消费记录-导出")
    public ResponseResult<T> exportMemberExpendList(HttpServletResponse response, @RequestBody @Validated MemberQueryForm memberQueryForm) throws IOException, ParseException {
        memberOccurLogBiz.exportMemberExpendList(response,memberQueryForm);
        return ResponseUtil.success(null);
    }


    /**
     * 查询会员退费列表
     * @param memberQueryForm 会员卡退费form
     * @return List<MemberRechargeLogBizVo>
     * @throws ParseException
     */
    @ApiOperation("查询会员退费列表")
    @PostMapping("/return/list")
    public ResponseResult<PageInfo<BaseMemberReturnLogVo>> memberReturnList(@RequestBody MemberQueryForm memberQueryForm) throws ParseException {
        PageInfo<BaseMemberReturnLogVo> baseMemberReturnLogVos = memberOccurLogBiz.memberReturnList(memberQueryForm);
        if (StringHelper.isNotNull(baseMemberReturnLogVos)){
            return ResponseUtil.success(baseMemberReturnLogVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",baseMemberReturnLogVos);
    }

    /**
     * 导出会员退费列表
     *
     * @param response 响应
     * @param memberQueryForm 查询条件
     * @return
     */
    @ApiOperation("导出会员退费列表")
    @PostMapping(value = "/return/export", name = "公司端-数据记录-会员退费列表-导出")
    public ResponseResult<T> exportMemberReturnList(HttpServletResponse response, @RequestBody @Validated MemberQueryForm memberQueryForm) throws IOException, ParseException {
        memberOccurLogBiz.exportMemberReturnList(response,memberQueryForm);
        return ResponseUtil.success(null);
    }


}