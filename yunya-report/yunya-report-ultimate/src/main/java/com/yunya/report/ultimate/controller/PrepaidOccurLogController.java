package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.PrepaidQueryForm;
import com.yunya.feign.report.domain.vo.BasePrepaidExpendLogVo;
import com.yunya.feign.report.domain.vo.BasePrepaidRechargeLogVo;
import com.yunya.feign.report.domain.vo.BasePrepaidReturnLogVo;
import com.yunya.feign.report.domain.vo.BasePrepaidTransferVO;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.biz.MemberOccurLogBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
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
 * 简介:公司端-预付款充值记录控制层
 *
 * @author: WY
 * @date: 2020/10/24 13:30
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端-数据记录-预付款充值记录")
@RestController
@RequestMapping("prepaid")
public class PrepaidOccurLogController {

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
     * 查询预付款充值列表
     * @param prepaidQueryForm 预付款卡充值form
     * @return List<MemberRechargeLogBizVo>
     */
    @ApiOperation("查询预付款充值列表")
    @PostMapping("/recharge/list")
    public ResponseResult<PageInfo<BasePrepaidRechargeLogVo>> prepaidRechargeList(@RequestBody PrepaidQueryForm prepaidQueryForm) {
        PageInfo<BasePrepaidRechargeLogVo> basePrepaidRechargeLogVos = memberOccurLogBiz.prepaidRechargeList(prepaidQueryForm);
        if (StringHelper.isNotNull(basePrepaidRechargeLogVos)){
            return ResponseUtil.success(basePrepaidRechargeLogVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据", null);
    }

    /**
     * 导出预付款充值列表
     *
     * @param response 响应
     * @param prepaidQueryForm 查询条件
     * @return 预付款充值列表
     */
    @ApiOperation("导出预付款充值列表")
    @PostMapping(value = "/recharge/export", name = "公司端-数据记录-预付款充值列表-导出")
    public ResponseResult<T> exportPrepaidRechargeList(HttpServletResponse response, @RequestBody @Validated PrepaidQueryForm prepaidQueryForm) throws IOException {
        memberOccurLogBiz.exportPrepaidRechargeList(response,prepaidQueryForm);
        return ResponseUtil.success(null);
    }

    /**
     * 查询预付款消费列表
     * @param prepaidQueryForm 预付款卡消费form
     * @return 预付款消费列表
     */
    @ApiOperation("查询预付款消费列表")
    @PostMapping("/expend/list")
    public ResponseResult<PageInfo<BasePrepaidExpendLogVo>> prepaidExpendList(@RequestBody PrepaidQueryForm prepaidQueryForm){
        PageInfo<BasePrepaidExpendLogVo> basePrepaidExpendLogVos = memberOccurLogBiz.prepaidExpendList(prepaidQueryForm);
        if (StringHelper.isNotNull(basePrepaidExpendLogVos)){
            return ResponseUtil.success(basePrepaidExpendLogVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据", null);
    }

    /**
     * 导出预付款消费列表
     *
     * @param response 响应
     * @param prepaidQueryForm 查询条件
     * @return 预付款消费列表
     */
    @ApiOperation("导出预付款消费列表")
    @PostMapping(value = "/expend/export", name = "公司端-数据记录-预付款消费列表-导出")
    public ResponseResult<T> exportPrepaidExpendList(HttpServletResponse response, @RequestBody @Validated PrepaidQueryForm prepaidQueryForm) throws IOException {
        memberOccurLogBiz.exportPrepaidExpendList(response,prepaidQueryForm);
        return ResponseUtil.success(null);
    }


    /**
     * 查询预付款退费列表
     * @param prepaidQueryForm 预付款卡退费form
     * @return 预付款退费列表
     */
    @ApiOperation("查询预付款退费列表")
    @PostMapping("/return/list")
    public ResponseResult<PageInfo<BasePrepaidReturnLogVo>> prepaidReturnList(@RequestBody PrepaidQueryForm prepaidQueryForm) {
        PageInfo<BasePrepaidReturnLogVo> basePrepaidReturnLogVos = memberOccurLogBiz.prepaidReturnList(prepaidQueryForm);
        if (StringHelper.isNotNull(basePrepaidReturnLogVos)){
            return ResponseUtil.success(basePrepaidReturnLogVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据", null);
    }

    /**
     * 导出预付款退费列表
     *
     * @param response 响应
     * @param prepaidQueryForm 查询条件
     * @return 预付款退费列表
     */
    @ApiOperation("导出预付款退费列表")
    @PostMapping(value = "/return/export", name = "公司端-数据记录-预付款退费列表-导出")
    public ResponseResult<T> exportPrepaidReturnList(HttpServletResponse response, @RequestBody @Validated PrepaidQueryForm prepaidQueryForm) throws IOException {
        memberOccurLogBiz.exportPrepaidReturnList(response,prepaidQueryForm);
        return ResponseUtil.success(null);
    }

    /**
     * 查询预付款间转账记录列表
     *
     * @param prepaidQueryForm 预付款查询form
     * @return 预付款转账记录列表
     */
    @ApiOperation("查询预付款间转账记录列表")
    @PostMapping("/transfer/list")
    public ResponseResult<PageInfo<BasePrepaidTransferVO>> prepaidTransferList(@RequestBody PrepaidQueryForm prepaidQueryForm) {
        PageInfo<BasePrepaidTransferVO> result = memberOccurLogBiz.prepaidTransferList(prepaidQueryForm);
        if (StringHelper.isNotNull(result)){
            return ResponseUtil.success(result);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据", null);
    }

    /**
     * 导出预付款间转账记录列表
     *
     * @param response 响应
     * @param prepaidQueryForm 查询条件
     * @return 预付款退费列表
     */
    @ApiOperation("导出预付款间转账记录列表")
    @PostMapping(value = "/transfer/export")
    public ResponseResult<T> exportPrepaidTransferList(HttpServletResponse response, @RequestBody @Validated PrepaidQueryForm prepaidQueryForm) throws IOException {
        memberOccurLogBiz.exportPrepaidTransferList(response,prepaidQueryForm);
        return ResponseUtil.success(null);
    }

}