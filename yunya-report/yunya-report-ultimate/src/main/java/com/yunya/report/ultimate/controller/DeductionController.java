package com.yunya.report.ultimate.controller;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.DeductionBuyQuery;
import com.yunya.feign.report.domain.query.DeductionPeriodQuery;
import com.yunya.feign.report.domain.query.DeductionRefundQuery;
import com.yunya.feign.report.domain.query.DeductionUseQuery;
import com.yunya.feign.report.domain.vo.DeductionBalanceInfoVO;
import com.yunya.feign.report.domain.vo.DeductionBuyVO;
import com.yunya.feign.report.domain.vo.DeductionRefundVO;
import com.yunya.feign.report.domain.vo.DeductionUsedVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.DeductionBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Api(tags = {"划扣表接口"})
@Slf4j
@RestController
public class DeductionController {
    @Resource
    private DeductionBiz deductionBiz;

    @ApiOperation(value = "公司端-报表统计-财务报表-划扣卡结存表")
    @PostMapping("/deduction/chang/balance")
    public ResponseResult<PageInfo<DeductionBalanceInfoVO>> deductionChange(@RequestBody DeductionPeriodQuery query) {
        return ResponseUtil.success(deductionBiz.deductionChange(query));
    }

    @ApiOperation(value = "公司端-报表统计-财务报表-划扣卡结存表-导出")
    @PostMapping("/deduction/chang/balance/export")
    public void deductionChangeExport(HttpServletResponse response, @Valid @RequestBody DeductionPeriodQuery query) throws IOException {
        deductionBiz.buildResponse(response, "划扣结存表");
        EasyExcel.write(response.getOutputStream(), DeductionBalanceInfoVO.class)
                .sheet("sheet").doWrite(deductionBiz.deductionChange(query).getList());
    }

    @ApiOperation(value = "运营报表-划扣卡购买记录")
    @PostMapping("/deduction/buy/record")
    public ResponseResult<PageInfo<DeductionBuyVO>> deductionBuy(@RequestBody DeductionBuyQuery query) {
        return ResponseUtil.success(deductionBiz.deductionBuy(query));
    }

    @ApiOperation(value = "运营报表-划扣卡购买记录-导出")
    @PostMapping("/deduction/buy/record/export")
    public void deductionBuyExport(HttpServletResponse response, @Valid @RequestBody DeductionBuyQuery query) throws IOException {
        deductionBiz.buildResponse(response, "划扣购买记录表");
        EasyExcel.write(response.getOutputStream(), DeductionBuyVO.class)
                .sheet("sheet").doWrite(deductionBiz.deductionBuy(query).getList());
    }

    @ApiOperation(value = "运营报表-划扣卡消耗记录")
    @PostMapping("/deduction/used/record")
    public ResponseResult<PageInfo<DeductionUsedVO>> deductionUse(@RequestBody DeductionUseQuery query) {
        return ResponseUtil.success(deductionBiz.deductionUse(query));
    }

    @ApiOperation(value = "运营报表-划扣卡消耗记录-导出")
    @PostMapping("/deduction/used/record/export")
    public void deductionUseExport(HttpServletResponse response, @Valid @RequestBody DeductionUseQuery query) throws IOException {
        deductionBiz.buildResponse(response, "划扣消耗记录表");
        EasyExcel.write(response.getOutputStream(), DeductionUsedVO.class)
                .sheet("sheet").doWrite(deductionBiz.deductionUse(query).getList());
    }

    @ApiOperation(value = "运营报表-划扣卡退费记录")
    @PostMapping("/deduction/refund/record")
    public ResponseResult<PageInfo<DeductionRefundVO>> deductionRefund(@RequestBody DeductionRefundQuery query) {
        return ResponseUtil.success(deductionBiz.deductionRefund(query));
    }

    @ApiOperation(value = "运营报表-划扣卡退费记录-导出")
    @PostMapping("/deduction/refund/record/export")
    public void deductionRefundExport(HttpServletResponse response, @Valid @RequestBody DeductionRefundQuery query) throws IOException {
        deductionBiz.buildResponse(response, "划扣退费记录表");
        EasyExcel.write(response.getOutputStream(), DeductionRefundVO.class)
                .sheet("sheet").doWrite(deductionBiz.deductionRefund(query).getList());
    }
}
