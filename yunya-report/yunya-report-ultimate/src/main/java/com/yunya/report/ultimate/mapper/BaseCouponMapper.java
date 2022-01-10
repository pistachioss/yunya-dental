package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.ClinicPerformanceBusinessQuery;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BaseCoupon;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface BaseCouponMapper extends Mapper<BaseCoupon> {
	/**
	 * 查询产品售出激活统计
	 */
	List<CouponStatisticsVo> listCouponStatistics(@Param("couponName") String couponName, @Param("couponCategoryIds") List<Integer> couponCategoryIds,
	                                              @Param("couponTypes") List<Integer> couponTypes);
	/**
	 * 查询产品售出统计-产品维度
	 */
	List<CouponSoldStatisticsVo> listCouponSold(@Param("couponName") String couponName, @Param("couponCategoryIds") List<Integer> couponCategoryIds,
	                                            @Param("couponTypes") List<Integer> couponTypes, @Param("crtStartDate") LocalDate crtStartDate,
	                                            @Param("crtEndDate") LocalDate crtEndDate);
	/**
	 * 查询产品使用统计
	 */
	List<CouponUsedVo> listCouponUsedByParam(@Param("couponName") String couponName, @Param("couponCategoryIds") List<Integer> couponCategoryIds,
	                                         @Param("couponTypes") List<Integer> couponTypes);
	/**
	 * 查询充值卡充值统计
	 */
	List<RechargeVo> listRechargeByParam(@Param("couponName") String couponName, @Param("couponCategoryIds") List<Integer> couponCategoryIds);

	List<CouponActiveVo> listCouponActive(@Param("couponIds") List<Integer> couponIds, @Param("soldChannelIds") List<Integer> soldChannelIds,
										  @Param("activeOrgIds") List<Integer> activeOrgIds,
										  @Param("activeStartDate") String activeStartDate,@Param("activeEndDate") String activeEndDate);

	List<CouponActiveVo> couponActivedGroupByOrgId(@Param("query") ClinicPerformanceBusinessQuery query);

	List<BaseCoupon> selectBaseCouponListByCouponId(@Param("couponIds") Collection<Integer> couponIds);
}