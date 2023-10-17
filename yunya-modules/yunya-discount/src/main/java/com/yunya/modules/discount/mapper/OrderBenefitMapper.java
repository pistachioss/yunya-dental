package com.yunya.modules.discount.mapper;

import com.yunya.feign.report.domain.query.base.EmployeeDateRangeQueryForm;
import com.yunya.models.discount.OrderBenefit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface OrderBenefitMapper extends tk.mybatis.mapper.common.Mapper<OrderBenefit>{

    /**
     * 根据条件查询员工的剩余年度授权折扣额度
     *
     * @param query
     * @return
     */
    BigDecimal selectAccreditDiscountAmountByQuery(@Param("query") EmployeeDateRangeQueryForm query);
}