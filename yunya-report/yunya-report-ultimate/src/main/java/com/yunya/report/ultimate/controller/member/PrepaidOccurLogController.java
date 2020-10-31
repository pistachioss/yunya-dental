package com.yunya.report.ultimate.controller.member;

import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.query.PrepaidQueryForm;
import com.yunya.feign.report.domain.vo.*;
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
 * 简介:公司端-预付款充值记录控制层
 *
 * @author: WY
 * @date: 2020/10/24 13:30
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("prepaid")
public class PrepaidOccurLogController {

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
     * 查询预付款充值列表
     * @param prepaidQueryForm 预付款卡充值form
     * @return List<MemberRechargeLogBizVo>
     */
    @ApiOperation("查询预付款充值列表")
    @PostMapping("/recharge/list")
    public ResponseResult<List<BasePrepaidRechargeLogVo>> prepaidRechargeList(@RequestBody PrepaidQueryForm prepaidQueryForm) throws ParseException {
        List<BasePrepaidRechargeLogVo> basePrepaidRechargeLogVos = memberOccurLogBiz.prepaidRechargeList(prepaidQueryForm);
        if (StringHelper.isNotEmpty(basePrepaidRechargeLogVos)){
            return ResponseUtil.success(basePrepaidRechargeLogVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",basePrepaidRechargeLogVos);
    }

    /**
     * 查询预付款消费列表
     * @param prepaidQueryForm 预付款卡消费form
     * @return List<MemberExpendLogBizVo>
     */
    @ApiOperation("查询预付款消费列表")
    @PostMapping("/expend/list")
    public ResponseResult<List<BasePrepaidExpendLogVo>> prepaidExpendList(@RequestBody PrepaidQueryForm prepaidQueryForm) throws ParseException{
        List<BasePrepaidExpendLogVo> basePrepaidExpendLogVos = memberOccurLogBiz.prepaidExpendList(prepaidQueryForm);
        if (StringHelper.isNotEmpty(basePrepaidExpendLogVos)){
            return ResponseUtil.success(basePrepaidExpendLogVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",basePrepaidExpendLogVos);
    }


    /**
     * 查询预付款退费列表
     * @param prepaidQueryForm 预付款卡退费form
     * @return List<MemberRechargeLogBizVo>
     * @throws ParseException
     */
    @ApiOperation("查询预付款退费列表")
    @PostMapping("/return/list")
    public ResponseResult<List<BasePrepaidReturnLogVo>> prepaidReturnList(@RequestBody PrepaidQueryForm prepaidQueryForm) throws ParseException {
        List<BasePrepaidReturnLogVo> basePrepaidReturnLogVos = memberOccurLogBiz.prepaidReturnList(prepaidQueryForm);
        if (StringHelper.isNotEmpty(basePrepaidReturnLogVos)){
            return ResponseUtil.success(basePrepaidReturnLogVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",basePrepaidReturnLogVos);
    }

}