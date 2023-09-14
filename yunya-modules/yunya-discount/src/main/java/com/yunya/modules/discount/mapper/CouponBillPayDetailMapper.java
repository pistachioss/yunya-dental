package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.vo.CouponRefundAccountVO;
import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.models.discount.CouponBillPayDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

public interface CouponBillPayDetailMapper extends Mapper<CouponBillPayDetail> {
    List<CouponRefundAccountVO> listPayAccount(@Param("orderId") Integer orderId);

    /**
     * 根据条件查询划扣卡账单收款现金总和
     *
     * @param query
     * @return
     */
    BigDecimal sumCouponBillSaleCashReceipt(@Param("query") CashReceiptOrRefundQuery query);
}