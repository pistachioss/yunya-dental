package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.CardConsumeQuery;
import com.yunya.feign.report.domain.query.CardStatisticsQuery;
import com.yunya.feign.report.domain.query.CouponStatisticsQuery;
import com.yunya.feign.report.domain.vo.CardConsumeRecordVO;
import com.yunya.feign.report.domain.vo.CardStatisticsVo;
import com.yunya.feign.report.domain.vo.CouponStatisticsVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.DiscountBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 简介:公司端报表-报表统计-市场报表控制层
 *
 * @author: chow
 * @date: 2020/11/24 19:26
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端报表-报表统计-市场报表")
@RestController
@RequestMapping("market")
public class CompanyReportOfMarket {
  @Resource private DiscountBiz discountBiz;

  @ApiOperation(value = "产品售出激活统计")
  @PostMapping("/coupon/statistics")
  public ResponseResult<PageInfo<CouponStatisticsVo>> getCouponStatistics(
      @Valid @RequestBody CouponStatisticsQuery query) {
    return ResponseUtil.success(discountBiz.getCouponStatisticsPage(query));
  }

  @ApiOperation(value = "产品售出激活统计-售出激活统计（代金、折扣、兑换、套餐）")
  @PostMapping("/{couponId}/card/statistics")
  public ResponseResult<PageInfo<CardStatisticsVo>> getCardStatistics(
      @PathVariable(value = "couponId") Integer couponId,
      @Valid @RequestBody CardStatisticsQuery query) {
    return ResponseUtil.success(discountBiz.getCardStatisticsPage(couponId, query));
  }

  @ApiOperation("获取卡券使用记录列表（销售渠道消费报表）")
  @PostMapping(value = "/card-consume/list", name = "卡券使用记录列表")
  public ResponseResult<PageInfo<CardConsumeRecordVO>> getCardConsumeRecordList(
      @RequestBody @Validated CardConsumeQuery query) {
    PageInfo<CardConsumeRecordVO> pageInfo = discountBiz.getCardConsumeRecordList(query);
    return ResponseUtil.success(pageInfo);
  }
}
