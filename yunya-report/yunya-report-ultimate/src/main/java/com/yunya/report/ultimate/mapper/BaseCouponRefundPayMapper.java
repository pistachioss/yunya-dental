package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.InboundAndOutboundStatementQuery;
import com.yunya.feign.report.domain.vo.StatementPaymentVO;
import com.yunya.models.report.BaseCouponRefundPay;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseCouponRefundPayMapper extends Mapper<BaseCouponRefundPay> {
    List<StatementPaymentVO> selectDeductionRefundPaymentInfo(
            @Param("query") InboundAndOutboundStatementQuery query);
}