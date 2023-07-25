package com.yunya.modules.treatment.mapper;

import com.yunya.models.treatment.BillRefundOrderDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;

public interface BillRefundOrderDetailMapper extends Mapper<BillRefundOrderDetail> {

    /**
     * 根据orderDetailId查询项目的可退费用
     *
     * @param orderDetailId
     * @return
     */
    BigDecimal selectItemRefundableAmountByOrderDetailId(@Param("orderDetailId") Integer orderDetailId);
}