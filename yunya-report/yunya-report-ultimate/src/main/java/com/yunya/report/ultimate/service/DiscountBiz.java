package com.yunya.report.ultimate.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.CardSoldStatisticsQuery;
import com.yunya.feign.report.domain.query.CardStatisticsQuery;
import com.yunya.feign.report.domain.query.CouponActiveDetailQuery;
import com.yunya.feign.report.domain.query.CouponSoldDetailQuery;
import com.yunya.feign.report.domain.query.CouponSoldStatisticsQuery;
import com.yunya.feign.report.domain.query.CouponStatisticsQuery;
import com.yunya.feign.report.domain.query.CouponUsedQuery;
import com.yunya.feign.report.domain.query.RechargeCardStatisticsQuery;
import com.yunya.feign.report.domain.vo.CardSoldStatisticsVo;
import com.yunya.feign.report.domain.vo.CardStatisticsVo;
import com.yunya.feign.report.domain.vo.CouponActiveDetailVo;
import com.yunya.feign.report.domain.vo.CouponSoldDetailVo;
import com.yunya.feign.report.domain.vo.CouponSoldStatisticsVo;
import com.yunya.feign.report.domain.vo.CouponStatisticsVo;
import com.yunya.feign.report.domain.vo.CouponUsedVo;
import com.yunya.feign.report.domain.vo.RechargeCardStatisticsVo;
import com.yunya.report.ultimate.mapper.BaseCardMapper;
import com.yunya.report.ultimate.mapper.BaseCouponItemMapper;
import com.yunya.report.ultimate.mapper.BaseCouponMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xiangyang
 * @date 2020/10/26
 */
@Service
@Slf4j
public class DiscountBiz {
	@Resource
	private BaseCouponMapper couponMapper;
	@Resource
	private BaseCardMapper cardMapper;
	@Resource
	private BaseCouponItemMapper itemMapper;

	/**
	 * 产品售出激活统计
	 * @param query query
	 * @return page
	 */
	public PageInfo<CouponStatisticsVo> getCouponStatisticsPage(CouponStatisticsQuery query) {
		Page<CouponStatisticsVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		couponMapper.listCouponStatistics(query.getCouponName(), query.getCouponCategoryIds(), query.getCouponTypes());
		return new PageInfo<>(page);
	}

	/**
	 * 产品售出激活卡券明细（代金、折扣、兑换、套餐）
	 * @param query query
	 * @return page
	 */
	public PageInfo<CardStatisticsVo> getCardStatisticsPage(Integer couponId, CardStatisticsQuery query) {
		Page<CardStatisticsVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		cardMapper.listCardUsedByParam(query.getCardNumber(), query.getAllocateOrgIds(), query.getSoldTypes(),
				query.getSoldStartDate(), query.getSoldEndDate(), query.getActiveOrgIds(), query.getActiveStartDate(),
				query.getActiveEndDate(), query.getSoldWays(), query.getChargeStatus(), couponId);
		return new PageInfo<>(page);
	}

	/**
	 * 产品售出激活卡券明细（充值卡）
	 * @param query query
	 * @return page
	 */
	public PageInfo<RechargeCardStatisticsVo> getRechargeCardStatistics(Integer couponId, RechargeCardStatisticsQuery query) {
		Page<RechargeCardStatisticsVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		cardMapper.listRechargeUsedByParam(query.getCardNumber(), query.getAllocateOrgIds(), query.getSoldTypes(),
				query.getSoldStartDate(), query.getSoldEndDate(), query.getRechargeOrgIds(), query.getRechargeStartDate(),
				query.getRechargeEndDate(), query.getSoldWays(), couponId);
		return new PageInfo<>(page);
	}

	/**
	 * 产品售出统计-产品维度
	 * @param query query
	 * @return page
	 */
	public PageInfo<CouponSoldStatisticsVo> getCouponSoldPage(CouponSoldStatisticsQuery query) {
		Page<CouponSoldStatisticsVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		couponMapper.listCouponSold(query.getCouponName(), query.getCouponCategoryIds(), query.getCouponTypes(),
				query.getCrtStartDate(), query.getCrtEndDate());
		return new PageInfo<>(page);
	}

	/**
	 * 产品售出统计-时间维度（自有平台）
	 * @param query query
	 * @return page
	 */
	public PageInfo<CardSoldStatisticsVo> getCardSoldPage(CardSoldStatisticsQuery query) {
		Page<CardSoldStatisticsVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		cardMapper.listAllCardSoldParam(query.getCouponName(), query.getCouponTypes(), query.getCardNumber(),
				query.getSoldTarget(), query.getAllocateOrgIds(), query.getSoldTypes(), query.getSoldStartDate(),
				query.getSoldEndDate(), query.getSoldWays(), query.getChargeStatus());
		return new PageInfo<>(page);
	}

	/**
	 * 产品售出统计-自有平台卡券售出明细
	 * @param query query
	 * @return page
	 */
	public PageInfo<CouponSoldDetailVo> getCouponSoldDetailPage(Integer couponId, CouponSoldDetailQuery query) {
		Page<CouponSoldDetailVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		cardMapper.listCouponSoldDetailByParam(query.getCardNumber(), query.getSoldTarget(), query.getAllocateOrgIds(),
				query.getSoldTypes(), query.getSoldStartDate(), query.getSoldEndDate(), query.getSoldWays(),
				query.getChargeStatus(), couponId);
		return new PageInfo<>(page);
	}

	/**
	 * 产品售出统计-第三方平台卡券激活明细
	 * @param query query
	 * @return page
	 */
	public PageInfo<CouponActiveDetailVo> getCouponActivePage(Integer couponId, CouponActiveDetailQuery query) {
		Page<CouponActiveDetailVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		cardMapper.listCouponActiveByParam(query.getCardNumber(), query.getSoldTarget(), query.getSoldChannelIds(),
				query.getActiveStartDate(), query.getActiveEndDate(), query.getActiveOrgIds(), couponId);
		return new PageInfo<>(page);
	}

	/**
	 * 产品使用统计
	 * @param query query
	 * @return page
	 */
	public PageInfo<CouponUsedVo> getCouponUsedPage(CouponUsedQuery query) {
		Page<CouponUsedVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		couponMapper.listCouponUsedByParam(query.getCouponName(), query.getCouponCategoryIds(), query.getCouponTypes());
		return new PageInfo<>(page);
	}
}
