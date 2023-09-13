package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.discount.CouponChangeRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CouponChangeRecordMapper extends Mapper<CouponChangeRecord> {
    List<DeductionBalanceInfoVO> deductionBalance(@Param("query") DeductionPeriodQuery query);

    List<DeductionUsedVO> deductionUse(@Param("query")DeductionUseQuery query);

    List<DeductionBuyVO> deductionBuy(@Param("query")DeductionBuyQuery query);

    List<DeductionRefundVO> deductionRefund(@Param("query")DeductionRefundQuery query);

    DeductionDataStatisticVO selectClinicDeductionDataStatistic( @Param("query") DataStatisticsQuery query);
}