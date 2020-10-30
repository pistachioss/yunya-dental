package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.vo.CardSoldStatisticsVo;
import com.yunya.feign.report.domain.vo.CardUsedStatisticsVo;
import com.yunya.feign.report.domain.vo.CouponActiveDetailVo;
import com.yunya.feign.report.domain.vo.CouponSoldDetailVo;
import com.yunya.feign.report.domain.vo.CardStatisticsVo;
import com.yunya.feign.report.domain.vo.CouponUsedDetailVo;
import com.yunya.feign.report.domain.vo.RechargeCardStatisticsVo;
import com.yunya.models.report.BaseCard;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;

public interface BaseCardMapper extends Mapper<BaseCard> {
	/**
	 * 查询产品售出激活明细（代金、折扣、兑换、套餐）
	 */
	List<CardStatisticsVo> listCardByParam(@Param("cardNumber") String cardNumber, @Param("allocateOrgIds") List<Integer> allocateOrgIds,
	                                           @Param("soldTypes") List<Integer> soldTypes, @Param("soldStartDate")LocalDate soldStartDate,
	                                           @Param("soldEndDate") LocalDate soldEndDate, @Param("activeOrgIds") List<Integer> activeOrgIds,
	                                           @Param("activeStartDate") LocalDate activeStartDate, @Param("activeEndDate") LocalDate activeEndDate,
	                                           @Param("soldWays") List<Integer> soldWays, @Param("chargeStatus")List<Integer> chargeStatus,
	                                           @Param("couponId") Integer couponId);
	/**
	 * 查询产品售出激活明细（充值卡）
	 */
	List<RechargeCardStatisticsVo> listRechargeUsedByParam(@Param("cardNumber") String cardNumber, @Param("allocateOrgIds") List<Integer> allocateOrgIds,
	                                                       @Param("soldTypes") List<Integer> soldTypes, @Param("soldStartDate")LocalDate soldStartDate,
	                                                       @Param("soldEndDate") LocalDate soldEndDate, @Param("rechargeOrgIds") List<Integer> rechargeOrgIds,
	                                                       @Param("rechargeStartDate") LocalDate rechargeStartDate, @Param("rechargeEndDate") LocalDate rechargeEndDate,
	                                                       @Param("soldWays") List<Integer> soldWays, @Param("couponId") Integer couponId);
	/**
	 * 查询自有平台卡券售出明细
	 */
	List<CouponSoldDetailVo> listCouponSoldDetailByParam(@Param("cardNumber") String cardNumber, @Param("soldTarget") String soldTarget,
	                                             @Param("allocateOrgIds") List<Integer> allocateOrgIds, @Param("soldTypes") List<Integer> soldTypes,
	                                             @Param("soldStartDate")LocalDate soldStartDate, @Param("soldEndDate") LocalDate soldEndDate,
	                                             @Param("soldWays") List<Integer> soldWays, @Param("chargeStatus")List<Integer> chargeStatus,
	                                             @Param("couponId") Integer couponId);
	/**
	 * 查询三方平台卡券激活明细
	 */
	List<CouponActiveDetailVo> listCouponActiveByParam(@Param("cardNumber") String cardNumber, @Param("soldTarget") String soldTarget,
	                                                 @Param("soldChannelIds") List<Integer> soldChannelIds, @Param("activeStartDate") LocalDate activeStartDate,
	                                                 @Param("activeEndDate") LocalDate activeEndDate, @Param("activeOrgIds") List<Integer> activeOrgIds,
	                                                 @Param("couponId") Integer couponId);
	/**
	 * 产品售出统计-时间维度
	 */
	List<CardSoldStatisticsVo> listAllCardSoldParam(@Param("couponName") String couponName, @Param("couponTypes") List<Integer> couponTypes,
	                                                @Param("cardNumber") String cardNumber, @Param("soldTarget") String soldTarget,
	                                                @Param("allocateOrgIds") List<Integer> allocateOrgIds, @Param("soldTypes") List<Integer> soldTypes,
	                                                @Param("soldStartDate") LocalDate soldStartDate, @Param("soldEndDate") LocalDate soldEndDate,
	                                                @Param("soldWays") List<Integer> soldWays, @Param("chargeStatus")List<Integer> chargeStatus);
	/**
	 * 产品使用统计-时间维度
	 */
	List<CardUsedStatisticsVo> listCardUsedByParam(@Param("couponName") String couponName, @Param("billNumber") String billNumber, @Param("cardNumber") String cardNumber,
	                                               @Param("patientKeyWord") String patientKeyWord, @Param("orgIds") List<Integer> orgIds,
	                                               @Param("dentistIds") List<Integer> dentistIds, @Param("usedStartDate") LocalDate usedStartDate,
	                                               @Param("usedEndDate") LocalDate usedEndDate, @Param("soldChannelIds") List<Integer> soldChannelIds,
	                                               @Param("couponTypes") List<Integer> couponTypes);
	/**
	 * 产品使用统计-产品维度-使用统计
	 */
	List<CouponUsedDetailVo> listCouponDetailUsedByParam(@Param("cardNumber") String cardNumber, @Param("patientKeyWord") String patientKeyWord,
	                                               @Param("billNumber") String billNumber, @Param("orgIds") List<Integer> orgIds,
	                                               @Param("dentistIds") List<Integer> dentistIds, @Param("usedStartDate") LocalDate usedStartDate,
	                                               @Param("usedEndDate") LocalDate usedEndDate, @Param("soldChannelIds") List<Integer> soldChannelIds,
	                                               @Param("couponId") Integer couponId);
}