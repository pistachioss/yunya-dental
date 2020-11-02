package com.yunya.report.ultimate.controller;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.CardSoldStatisticsQuery;
import com.yunya.feign.report.domain.query.CardStatisticsQuery;
import com.yunya.feign.report.domain.query.CardUsedStatisticsQuery;
import com.yunya.feign.report.domain.query.CardUsedRecordQuery;
import com.yunya.feign.report.domain.query.CouponActiveDetailQuery;
import com.yunya.feign.report.domain.query.CouponSoldDetailQuery;
import com.yunya.feign.report.domain.query.CardSoldRecordQuery;
import com.yunya.feign.report.domain.query.CouponSoldStatisticsQuery;
import com.yunya.feign.report.domain.query.CouponStatisticsQuery;
import com.yunya.feign.report.domain.query.CouponUsedDetailQuery;
import com.yunya.feign.report.domain.query.CouponUsedQuery;
import com.yunya.feign.report.domain.query.RechargeCardStatisticsQuery;
import com.yunya.feign.report.domain.query.RechargeDetailQuery;
import com.yunya.feign.report.domain.query.RechargeQuery;
import com.yunya.feign.report.domain.vo.CardSoldStatisticsVo;
import com.yunya.feign.report.domain.vo.CardStatisticsVo;
import com.yunya.feign.report.domain.vo.CardUsedRecordVo;
import com.yunya.feign.report.domain.vo.CardUsedStatisticsVo;
import com.yunya.feign.report.domain.vo.CouponActiveDetailVo;
import com.yunya.feign.report.domain.vo.CouponSoldDetailVo;
import com.yunya.feign.report.domain.vo.CouponSoldRecordVo;
import com.yunya.feign.report.domain.vo.CouponSoldStatisticsVo;
import com.yunya.feign.report.domain.vo.CouponStatisticsVo;
import com.yunya.feign.report.domain.vo.CouponUsedDetailVo;
import com.yunya.feign.report.domain.vo.CouponUsedVo;
import com.yunya.feign.report.domain.vo.RechargeCardStatisticsVo;
import com.yunya.feign.report.domain.vo.RechargeDetailVo;
import com.yunya.feign.report.domain.vo.RechargeVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.service.DiscountBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @author xiangyang
 * @date 2020/10/26
 */
@Api(tags = {"公司端-优惠券、卡券报表接口"})
@Slf4j
@RestController
public class DiscountController {
	@Resource
	private DiscountBiz discountBiz;

	@ApiOperation(value = "产品售出激活统计")
	@PostMapping("/coupon/statistics")
	public ResponseResult<PageInfo<CouponStatisticsVo>> getCouponStatistics(@Valid @RequestBody CouponStatisticsQuery query) {
		return ResponseUtil.success(discountBiz.getCouponStatisticsPage(query));
	}

	@ApiOperation(value = "产品售出激活统计-售出激活统计（代金、折扣、兑换、套餐）")
	@PostMapping("/{couponId}/card/statistics")
	public ResponseResult<PageInfo<CardStatisticsVo>> getCardStatistics(@PathVariable(value = "couponId") Integer couponId,
	                                                                    @Valid @RequestBody CardStatisticsQuery query) {
		return ResponseUtil.success(discountBiz.getCardStatisticsPage(couponId, query));
	}

	@ApiOperation(value = "产品售出激活统计-售出激活统计（充值卡）")
	@PostMapping("/{couponId}/recharge/statistics")
	public ResponseResult<PageInfo<RechargeCardStatisticsVo>> getRechargeCardStatistics(@PathVariable(value = "couponId") Integer couponId,
	                                                                                    @Valid @RequestBody RechargeCardStatisticsQuery query) {
		return ResponseUtil.success(discountBiz.getRechargeCardStatistics(couponId, query));
	}

	@ApiOperation(value = "产品售出统计-产品维度")
	@PostMapping("/coupon/sold/statistics")
	public ResponseResult<PageInfo<CouponSoldStatisticsVo>> getCouponSold(@Valid @RequestBody CouponSoldStatisticsQuery query) {
		return ResponseUtil.success(discountBiz.getCouponSoldPage(query));
	}

	@ApiOperation(value = "产品售出统计-时间维度")
	@PostMapping("/card/sold/statistics")
	public ResponseResult<PageInfo<CardSoldStatisticsVo>> getCardSold(@Valid @RequestBody CardSoldStatisticsQuery query) {
		return ResponseUtil.success(discountBiz.getCardSoldPage(query));
	}

	@ApiOperation(value = "产品售出统计-自有平台卡券售出明细")
	@PostMapping("/{couponId}/card/sold/statistics")
	public ResponseResult<PageInfo<CouponSoldDetailVo>> getCouponSoldStatistics(@PathVariable(value = "couponId") Integer couponId,
	                                                                            @Valid @RequestBody CouponSoldDetailQuery query) {
		return ResponseUtil.success(discountBiz.getCouponSoldDetailPage(couponId, query));
	}

	@ApiOperation(value = "产品售出统计-第三方平台卡券激活")
	@PostMapping("/{couponId}/card/active/statistics")
	public ResponseResult<PageInfo<CouponActiveDetailVo>> getCouponActiveStatistics(@PathVariable(value = "couponId") Integer couponId,
	                                                                                @Valid @RequestBody CouponActiveDetailQuery query) {
		return ResponseUtil.success(discountBiz.getCouponActivePage(couponId, query));
	}

	@ApiOperation(value = "产品使用统计-产品维度")
	@PostMapping("/coupon/used/statistics")
	public ResponseResult<PageInfo<CouponUsedVo>> getCouponUsed(@Valid @RequestBody CouponUsedQuery query) {
		return ResponseUtil.success(discountBiz.getCouponUsedPage(query));
	}

	@ApiOperation(value = "产品使用统计-时间维度")
	@PostMapping("/card/used/statistics")
	public ResponseResult<PageInfo<CardUsedStatisticsVo>> getCouponUsed(@Valid @RequestBody CardUsedStatisticsQuery query) {
		return ResponseUtil.success(discountBiz.getCardUsedPage(query));
	}

	@ApiOperation(value = "产品使用统计-产品维度-使用统计")
	@PostMapping("/{couponId}/card/used/statistics")
	public ResponseResult<PageInfo<CouponUsedDetailVo>> getCouponUsed(@PathVariable(value = "couponId") Integer couponId,
	                                                                  @Valid @RequestBody CouponUsedDetailQuery query) {
		return ResponseUtil.success(discountBiz.getCouponDetailUsedPage(couponId, query));
	}

	@ApiOperation(value = "充值卡充值统计")
	@PostMapping("/recharge/statistics")
	public ResponseResult<PageInfo<RechargeVo>> getRechargePage(@Valid @RequestBody RechargeQuery query) {
		return ResponseUtil.success(discountBiz.getRechargePage(query));
	}

	@ApiOperation(value = "充值卡充值统计-充值统计")
	@PostMapping("/{couponId}/rechargeCard/statistics")
	public ResponseResult<PageInfo<RechargeDetailVo>> getRechargePage(@PathVariable(value = "couponId") Integer couponId,
	                                                                  @Valid @RequestBody RechargeDetailQuery query) {
		return ResponseUtil.success(discountBiz.getRechargeDetailPage(couponId, query));
	}

	@ApiOperation(value = "产品记录-产品售出记录")
	@PostMapping("/coupon/sold/record")
	public ResponseResult<PageInfo<CouponSoldRecordVo>> getCardSoldRecord(@Valid @RequestBody CardSoldRecordQuery query) {
		return ResponseUtil.success(discountBiz.getCardSoldRecordPage(query));
	}

	@ApiOperation(value = "产品记录-产品使用记录")
	@PostMapping("/coupon/used/record")
	public ResponseResult<PageInfo<CardUsedRecordVo>> getCardUsedRecord(@Valid @RequestBody CardUsedRecordQuery query) {
		return ResponseUtil.success(discountBiz.getCardUsedRecordPage(query));
	}

	@ApiOperation(value = "产品售出激活统计-售出激活统计（代金、折扣、兑换、套餐）- 导出")
	@PostMapping("/{couponId}/card/export")
	public void exportCard(HttpServletResponse response, @PathVariable(value = "couponId") Integer couponId,
	                       @Valid @RequestBody CardStatisticsQuery query) throws IOException {
		discountBiz.buildResponse(response, discountBiz.getCouponName(couponId) + "售出激活统计");
		EasyExcel.write(response.getOutputStream(), CardStatisticsVo.class)
				.sheet("sheet").doWrite(discountBiz.getCardStatisticsList(couponId, query));
	}

	@ApiOperation(value = "产品售出激活统计 - 售出激活统计（充值卡）- 导出")
	@PostMapping("/{couponId}/rechargeCard/export")
	public void exportRechargeCard(HttpServletResponse response, @PathVariable(value = "couponId") Integer couponId,
	                               @Valid @RequestBody RechargeCardStatisticsQuery query) throws IOException {
		discountBiz.buildResponse(response, discountBiz.getCouponName(couponId) + "售出激活统计");
		EasyExcel.write(response.getOutputStream(), RechargeCardStatisticsVo.class)
				.sheet("sheet").doWrite(discountBiz.getRechargeCardStatisticsList(couponId, query));
	}

	@ApiOperation(value = "产品售出统计-时间维度 - 导出")
	@PostMapping("/card/sold/export")
	public void exportCardSold(HttpServletResponse response, @Valid @RequestBody CardSoldStatisticsQuery query) throws IOException {
		discountBiz.buildResponse(response, "自有平台卡券售出明细");
		EasyExcel.write(response.getOutputStream(), CardSoldStatisticsVo.class)
				.sheet("sheet").doWrite(discountBiz.getCardSoldList(query));
	}

	@ApiOperation(value = "产品售出统计-自有平台卡券售出明细 - 导出")
	@PostMapping("/{couponId}/card/sold/export")
	public void exportCouponSoldStatistics(HttpServletResponse response, @PathVariable(value = "couponId") Integer couponId,
	                                       @Valid @RequestBody CouponSoldDetailQuery query) throws IOException {
		discountBiz.buildResponse(response, discountBiz.getCouponName(couponId) + "自有平台卡券售出明细");
		EasyExcel.write(response.getOutputStream(), CouponSoldDetailVo.class)
				.sheet("sheet").doWrite(discountBiz.getCouponSoldDetailList(couponId, query));
	}

	@ApiOperation(value = "产品售出统计-第三方平台卡券激活 - 导出")
	@PostMapping("/{couponId}/card/active/export")
	public void exportCouponActiveStatistics(HttpServletResponse response, @PathVariable(value = "couponId") Integer couponId,
	                                         @Valid @RequestBody CouponActiveDetailQuery query) throws IOException {
		discountBiz.buildResponse(response, discountBiz.getCouponName(couponId) + "第三方平台卡券售出明细");
		EasyExcel.write(response.getOutputStream(), CouponActiveDetailVo.class)
				.sheet("sheet").doWrite(discountBiz.getCouponActiveList(couponId, query));
	}

	@ApiOperation(value = "产品使用统计-时间维度 - 导出")
	@PostMapping("/card/used/export")
	public void exportCouponUsed(HttpServletResponse response, @Valid @RequestBody CardUsedStatisticsQuery query) throws IOException {
		discountBiz.buildResponse(response, "产品使用统计明细");
		EasyExcel.write(response.getOutputStream(), CardUsedStatisticsVo.class)
				.sheet("sheet").doWrite(discountBiz.getCardUsedList(query));
	}

	@ApiOperation(value = "产品使用统计-产品维度-使用统计 - 导出")
	@PostMapping("/{couponId}/card/used/export")
	public void exportCouponUsed(HttpServletResponse response, @PathVariable(value = "couponId") Integer couponId,
	                             @Valid @RequestBody CouponUsedDetailQuery query) throws IOException {
		discountBiz.buildResponse(response, discountBiz.getCouponName(couponId) + "产品使用统计明细");
		EasyExcel.write(response.getOutputStream(), CouponUsedDetailVo.class)
				.sheet("sheet").doWrite(discountBiz.getCouponDetailUsedList(couponId, query));
	}

	@ApiOperation(value = "充值卡充值统计-充值统计 - 导出")
	@PostMapping("/{couponId}/recharge/export")
	public void getRechargePage(HttpServletResponse response, @PathVariable(value = "couponId") Integer couponId,
	                            @Valid @RequestBody RechargeDetailQuery query) throws IOException {
		discountBiz.buildResponse(response, discountBiz.getCouponName(couponId) + "充值卡充值统计明细");
		EasyExcel.write(response.getOutputStream(), RechargeDetailVo.class)
				.sheet("sheet").doWrite(discountBiz.getRechargeDetailList(couponId, query));
	}


}
