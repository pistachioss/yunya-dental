package com.yunya.report.ultimate.biz;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BaseCoupon;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

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
	@Resource
	private BaseBenefitMapper benefitMapper;
	@Resource
	private BaseOrganizationMapper orgMapper;

	/**
	 * 产品售出激活统计
	 *
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
	 *
	 * @param query query
	 * @return page
	 */
	public PageInfo<CardStatisticsVo> getCardStatisticsPage(Integer couponId, CardStatisticsQuery query) {
		Page<CardStatisticsVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		List<CardStatisticsVo> cardStatisticsVos = cardMapper.listCardByParam(query.getCardNumber(), query.getAllocateOrgIds(), query.getSoldTypes(),
				query.getSoldStartDate(), query.getSoldEndDate(), query.getActiveOrgIds(), query.getActiveStartDate(),
				query.getActiveEndDate(), query.getSoldWays(), query.getChargeStatus(), couponId);
		return new PageInfo<>(page);
	}

	/**
	 * 产品售出激活卡券明细（充值卡）
	 *
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
	 *
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
	 *
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
	 *
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
	 *
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
	 *
	 * @param query query
	 * @return page
	 */
	public PageInfo<CouponUsedVo> getCouponUsedPage(CouponUsedQuery query) {
		Page<CouponUsedVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		couponMapper.listCouponUsedByParam(query.getCouponName(), query.getCouponCategoryIds(), query.getCouponTypes());
		return new PageInfo<>(page);
	}

	/**
	 * 产品使用统计-时间维度
	 *
	 * @param query query
	 * @return page
	 */
	public PageInfo<CardUsedStatisticsVo> getCardUsedPage(CardUsedStatisticsQuery query) {
		Page<CardUsedStatisticsVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		benefitMapper.listCardUsedByParam(query.getCouponName(), query.getBillNumber(), query.getCardNumber(),
				query.getPatientKeyWord(), query.getOrgIds(), query.getDentistIds(), query.getUsedStartDate(),
				query.getUsedEndDate(), query.getSoldChannelIds(), query.getCouponTypes());
		return new PageInfo<>(page);
	}

	/**
	 * 产品使用统计-产品维度-使用统计
	 *
	 * @param query query
	 * @return page
	 */
	public PageInfo<CouponUsedDetailVo> getCouponDetailUsedPage(Integer couponId, CouponUsedDetailQuery query) {
		Page<CouponUsedDetailVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		benefitMapper.listCouponDetailUsedByParam(query.getCardNumber(), query.getPatientKeyWord(), query.getBillNumber(),
				query.getOrgIds(), query.getDentistIds(), query.getUsedStartDate(),
				query.getUsedEndDate(), query.getSoldChannelIds(), couponId);
		return new PageInfo<>(page);
	}

	/**
	 * 充值卡充值统计
	 *
	 * @param query query
	 * @return page
	 */
	public PageInfo<RechargeVo> getRechargePage(RechargeQuery query) {
		Page<RechargeVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		List<RechargeVo> rechargeVos = couponMapper.listRechargeByParam(query.getCouponName(), query.getCouponCategoryIds());
		return new PageInfo<>(page);
	}

	/**
	 * 充值卡充值统计-充值统计
	 *
	 * @param query query
	 * @return page
	 */
	public PageInfo<RechargeDetailVo> getRechargeDetailPage(Integer couponId, RechargeDetailQuery query) {
		Page<RechargeDetailVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		cardMapper.listRechargeDetailByParam(query.getCardNumber(), query.getPatientKeyWord(), query.getRechargeOrgIds(),
				query.getRechargeAccount(), query.getRechargeStartDate(), query.getRechargeEndDate(), couponId);
		return new PageInfo<>(page);
	}

	/**
	 * 产品记录-产品售出记录
	 *
	 * @param query query
	 * @return page
	 */
	public PageInfo<CouponSoldRecordVo> getCardSoldRecordPage(CardSoldRecordQuery query) {
		Page<CouponSoldRecordVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		cardMapper.listCardSoldRecord(query.getOrgId(), query.getSoldStartDate(), query.getSoldEndDate(),
				query.getCouponName(), query.getCardNumber(), query.getSoldTarget(), query.getSoldPhoneNumber(),
				query.getCouponTypes());
		return new PageInfo<>(page);
	}

	/**
	 * 产品记录-产品使用记录
	 *
	 * @param query query
	 * @return page
	 */
	public PageInfo<CardUsedRecordVo> getCardUsedRecordPage(CardUsedRecordQuery query) {
		Page<CardUsedRecordVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		benefitMapper.listCardUsedRecordByParam(query.getOrgId(), query.getUsedStartDate(), query.getUsedEndDate(),
				query.getCouponName(), query.getCardNumber(), query.getPatientKeyword(), query.getCouponTypes(),
				query.getSaleChannelIds());
		return new PageInfo<>(page);
	}

	public void buildResponse(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding("utf-8");
		String encodeFileName = URLEncoder.encode(fileName, "UTF-8");
		response.setHeader("Content-disposition", "attachment;filename=" + encodeFileName + ".xlsx");
	}

	public String getCouponName(Integer couponId) {
		BaseCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
		return coupon == null ? "未知" : coupon.getCouponName();
	}

	public String getOrgName(Integer couponId) {
		BaseOrganization org = orgMapper.selectByPrimaryKey(couponId);
		return org == null ? "未知" : org.getAbbreviation();
	}

	/**
	 * 产品售出激活卡券明细（代金、折扣、兑换、套餐）- 导出
	 *
	 * @param query query
	 * @return list
	 */
	public List<CardStatisticsVo> getCardStatisticsList(Integer couponId, CardStatisticsQuery query) {
		return cardMapper.listCardByParam(query.getCardNumber(), query.getAllocateOrgIds(), query.getSoldTypes(),
				query.getSoldStartDate(), query.getSoldEndDate(), query.getActiveOrgIds(), query.getActiveStartDate(),
				query.getActiveEndDate(), query.getSoldWays(), query.getChargeStatus(), couponId);
	}

	/**
	 * 产品售出激活卡券明细（充值卡）- 导出
	 *
	 * @param query query
	 * @return list
	 */
	public List<RechargeCardStatisticsVo> getRechargeCardStatisticsList(Integer couponId, RechargeCardStatisticsQuery query) {
		return cardMapper.listRechargeUsedByParam(query.getCardNumber(), query.getAllocateOrgIds(), query.getSoldTypes(),
				query.getSoldStartDate(), query.getSoldEndDate(), query.getRechargeOrgIds(), query.getRechargeStartDate(),
				query.getRechargeEndDate(), query.getSoldWays(), couponId);
	}

	/**
	 * 产品售出统计-时间维度（自有平台）- 导出
	 *
	 * @param query query
	 * @return list
	 */
	public List<CardSoldStatisticsVo> getCardSoldList(CardSoldStatisticsQuery query) {
		return cardMapper.listAllCardSoldParam(query.getCouponName(), query.getCouponTypes(), query.getCardNumber(),
				query.getSoldTarget(), query.getAllocateOrgIds(), query.getSoldTypes(), query.getSoldStartDate(),
				query.getSoldEndDate(), query.getSoldWays(), query.getChargeStatus());
	}

	/**
	 * 产品售出统计-自有平台卡券售出明细 - 导出
	 *
	 * @param query query
	 * @return list
	 */
	public List<CouponSoldDetailVo> getCouponSoldDetailList(Integer couponId, CouponSoldDetailQuery query) {
		return cardMapper.listCouponSoldDetailByParam(query.getCardNumber(), query.getSoldTarget(), query.getAllocateOrgIds(),
				query.getSoldTypes(), query.getSoldStartDate(), query.getSoldEndDate(), query.getSoldWays(),
				query.getChargeStatus(), couponId);
	}

	/**
	 * 产品售出统计-第三方平台卡券激活明细 - 导出
	 *
	 * @param query query
	 * @return list
	 */
	public List<CouponActiveDetailVo> getCouponActiveList(Integer couponId, CouponActiveDetailQuery query) {
		return cardMapper.listCouponActiveByParam(query.getCardNumber(), query.getSoldTarget(), query.getSoldChannelIds(),
				query.getActiveStartDate(), query.getActiveEndDate(), query.getActiveOrgIds(), couponId);
	}

	/**
	 * 产品使用统计-时间维度 - 导出
	 *
	 * @param query query
	 * @return list
	 */
	public List<CardUsedStatisticsVo> getCardUsedList(CardUsedStatisticsQuery query) {
		return benefitMapper.listCardUsedByParam(query.getCouponName(), query.getBillNumber(), query.getCardNumber(),
				query.getPatientKeyWord(), query.getOrgIds(), query.getDentistIds(), query.getUsedStartDate(),
				query.getUsedEndDate(), query.getSoldChannelIds(), query.getCouponTypes());
	}

	/**
	 * 产品使用统计-产品维度-使用统计 - 导出
	 *
	 * @param query query
	 * @return list
	 */
	public List<CouponUsedDetailVo> getCouponDetailUsedList(Integer couponId, CouponUsedDetailQuery query) {
			return benefitMapper.listCouponDetailUsedByParam(query.getCardNumber(), query.getPatientKeyWord(), query.getBillNumber(),
				query.getOrgIds(), query.getDentistIds(), query.getUsedStartDate(),
				query.getUsedEndDate(), query.getSoldChannelIds(), couponId);
	}

	/**
	 * 充值卡充值统计-充值统计 - 导出
	 *
	 * @param query query
	 * @return page
	 */
	public List<RechargeDetailVo> getRechargeDetailList(Integer couponId, RechargeDetailQuery query) {
		return cardMapper.listRechargeDetailByParam(query.getCardNumber(), query.getPatientKeyWord(), query.getRechargeOrgIds(),
				query.getRechargeAccount(), query.getRechargeStartDate(), query.getRechargeEndDate(), couponId);
	}

	/**
	 * 查询卡券的使用记录
	 * @param cardId cardId
	 * @param query query
	 * @return page
	 */
	public PageInfo<OnceCardUseVo> getCardUsePage(Integer cardId, OnceCardUseQuery query) {
		Page<OnceCardUseVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		benefitMapper.listCardUseById(cardId);
		return new PageInfo<>(page);
	}

	public MultiCardUseVo getMultiCardUsePage(Integer cardId, MultiCardUseQuery query) {
		MultiCardUseVo vo = new MultiCardUseVo();
		Page<OnceCardUseVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		benefitMapper.listCardUseById(cardId);
		vo.setUseVos(new PageInfo<>(page));
		List<BenefitItemVo> itemVos = benefitMapper.listItemUseById(cardId);
		vo.setItemVos(itemVos);
		return vo;
	}

	/**
	 * 产品记录-产品售出记录-导出
	 *
	 * @param query query
	 * @return List
	 */
	public List<CouponSoldRecordVo> getCardSoldRecordList(CardSoldRecordQuery query) {
		return cardMapper.listCardSoldRecord(query.getOrgId(), query.getSoldStartDate(), query.getSoldEndDate(),
				query.getCouponName(), query.getCardNumber(), query.getSoldTarget(), query.getSoldPhoneNumber(),
				query.getCouponTypes());
	}

	/**
	 * 产品记录-产品使用记录-导出
	 *
	 * @param query query
	 * @return page
	 */
	public List<CardUsedRecordVo> getCardUsedRecordList(CardUsedRecordQuery query) {
		return benefitMapper.listCardUsedRecordByParam(query.getOrgId(), query.getUsedStartDate(), query.getUsedEndDate(),
				query.getCouponName(), query.getCardNumber(), query.getPatientKeyword(), query.getCouponTypes(),
				query.getSaleChannelIds());
	}
}
