package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.bo.BillUsedCouponBo;
import com.yunya.feign.discount.domain.bo.CardUseBo;
import com.yunya.feign.discount.domain.query.DiscountCouponQuery;
import com.yunya.feign.treatment.domain.vo.ClinicTariffDiscountCouponVO;
import com.yunya.models.discount.CardBenefit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface CardBenefitMapper extends tk.mybatis.mapper.common.Mapper<CardBenefit> {

    void insertList(@Param("List") List<CardBenefit> cardBenefits);

    /**
     * 获取卡券使用情况
     * @param cardIds cardIds
     * @param couponType couponType
     * @return list
     */
    List<CardUseBo> getCardUseInfo(@Param("cardIds") Set<Integer> cardIds, @Param("couponType")Integer couponType);

    /**
     * 查询账单的优惠券使用详情
     */
    List<BillUsedCouponBo> getBillCoupons(@Param("orderId") Integer orderId);

    int countCardUsed(@Param("cardId") Integer cardId);

    /**
     * 查询门诊项目分类的优惠金额合计和补入工作量合计
     *
     * @param queryForm
     * @return
     */
    List<ClinicTariffDiscountCouponVO> selectClinicTariffCategoryDiscountCoupon(@Param("query") DiscountCouponQuery queryForm);
}