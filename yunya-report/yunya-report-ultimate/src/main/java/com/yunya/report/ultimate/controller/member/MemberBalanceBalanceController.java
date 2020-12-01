package com.yunya.report.ultimate.controller.member;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.vo.BaseMemberBalanceInfoVo;
import com.yunya.feign.report.domain.vo.MemberCardInfoVo;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.service.MemberOccurLogBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2020/11/24 13:29
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端-财务报表-余额结存")
@RestController
@RequestMapping("balance")
public class MemberBalanceBalanceController {

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
     * 会员余/预付款额结存信息列表
     * @param memberQueryForm 查询余额结存form
     * @return List<MemberRechargeLogBizVo>
     */
    @ApiOperation("会员余/预付款余额结存信息列表")
    @PostMapping("/balance/list")
    public ResponseResult<PageInfo<BaseMemberBalanceInfoVo>> memberBalanceList(@RequestBody MemberQueryForm memberQueryForm)  {
        PageInfo<BaseMemberBalanceInfoVo> baseMemberBalanceInfoVos = memberOccurLogBiz.memberBalanceList(memberQueryForm);
        if (StringHelper.isNotNull(baseMemberBalanceInfoVos)){
            return ResponseUtil.success(baseMemberBalanceInfoVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",baseMemberBalanceInfoVos);
    }

}