package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.vo.CardUsedStatisticsVo;
import com.yunya.feign.report.domain.vo.CouponUsedDetailVo;
import com.yunya.models.report.BaseBenefit;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;

public interface BaseBenefitMapper extends Mapper<BaseBenefit> {
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