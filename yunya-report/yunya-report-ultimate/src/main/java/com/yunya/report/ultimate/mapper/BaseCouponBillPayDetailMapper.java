package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.InboundAndOutboundStatementQuery;
import com.yunya.feign.report.domain.vo.StatementPaymentVO;
import com.yunya.models.report.BaseCouponBillPayDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseCouponBillPayDetailMapper extends Mapper<BaseCouponBillPayDetail> {
    List<StatementPaymentVO> selectDeductionSoldPaymentInfo(
            @Param("query") InboundAndOutboundStatementQuery query);
}