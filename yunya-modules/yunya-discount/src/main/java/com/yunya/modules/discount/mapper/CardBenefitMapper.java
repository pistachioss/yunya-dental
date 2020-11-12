package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.bo.CardUseBo;
import com.yunya.models.discount.CardBenefit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CardBenefitMapper extends tk.mybatis.mapper.common.Mapper<CardBenefit> {

    void insertList(@Param("List") List<CardBenefit> cardBenefits);

    /**
     * 获取卡券使用情况
     * @param couponIds couponIds
     * @param couponType couponType
     * @return list
     */
    List<CardUseBo> getCardUseInfo(@Param("couponIds") List<Integer> couponIds, @Param("couponType")Integer couponType);
}