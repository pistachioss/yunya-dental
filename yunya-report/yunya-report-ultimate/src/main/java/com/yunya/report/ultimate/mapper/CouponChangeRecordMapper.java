package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.DeductionPeriodQuery;
import com.yunya.feign.report.domain.vo.DeductionBalanceInfoVO;
import com.yunya.models.discount.CouponChangeRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CouponChangeRecordMapper extends Mapper<CouponChangeRecord> {
    List<DeductionBalanceInfoVO> deductionBalance(@Param("query") DeductionPeriodQuery query);
}