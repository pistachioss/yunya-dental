package com.yunya.modules.discount.mapper;

import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.models.discount.CouponRefundPay;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;

public interface CouponRefundPayMapper extends Mapper<CouponRefundPay> {
    /**
     * 根据条件查询卡券账单退费现金总和
     *
     * @param query
     * @return
     */
    BigDecimal sumCouponBillRefundCashReceipt(@Param("query") CashReceiptOrRefundQuery query);
}