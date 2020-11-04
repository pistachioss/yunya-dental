package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.AuthDiscountBenefit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthDiscountBenefitMapper extends tk.mybatis.mapper.common.Mapper<AuthDiscountBenefit> {

    void insertList(@Param("List") List<AuthDiscountBenefit> authBenefits);
}