package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.CardBenefit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CardBenefitMapper extends tk.mybatis.mapper.common.Mapper<CardBenefit> {

    void insertList(@Param("List") List<CardBenefit> cardBenefits);

}