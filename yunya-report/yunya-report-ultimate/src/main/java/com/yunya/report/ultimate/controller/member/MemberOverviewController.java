package com.yunya.report.ultimate.controller.member;

import com.yunya.feign.report.domain.query.MemberOverviewQueryForm;
import com.yunya.feign.report.domain.vo.BaseMemberOverviewVo;
import com.yunya.feign.report.domain.vo.BasePatientMemberOverviewVo;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.service.MemberOccurLogBiz;
import com.yunya.report.ultimate.service.MemberOverviewBiz;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 简介: 会员卡概况控制层
 *
 * @author: WY
 * @date: 2020/10/27 09:50
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("overview")
public class MemberOverviewController {
    /** 服务注入 */
    @Autowired MemberOverviewBiz memberOverviewBiz;

    /** 会员卡操作Biz */
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
     * 查询患者会员卡/预付款概况
     * @param memberOverviewQueryForm 患者会员卡概况form
     * @return List<MemberOverviewVo>
     */
    @ApiOperation("查询患者会员卡/预付款概况")
    @PostMapping("/patientOverview/list")
    public ResponseResult<List<BasePatientMemberOverviewVo>> patientOverviewList(@RequestBody MemberOverviewQueryForm memberOverviewQueryForm) throws ParseException {
        List<BasePatientMemberOverviewVo> basePatientMemberOverviewVos = memberOverviewBiz.patientOverviewList(memberOverviewQueryForm);
        if (StringHelper.isNotEmpty(basePatientMemberOverviewVos)){
            return ResponseUtil.success(basePatientMemberOverviewVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",basePatientMemberOverviewVos);
    }

    /**
     * 会员卡概况
     * @return List<BaseOrganization>
     */
    @ApiOperation("会员卡概况")
    @PostMapping("/member/list")
    public ResponseResult<Map<String,Object>> memberList() {
        return ResponseUtil.success(this.memberOverviewBiz.memberList());
    }


}