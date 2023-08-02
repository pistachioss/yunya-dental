package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.query.DeductionRecordQuery;
import com.yunya.feign.discount.domain.vo.DeductionChangeRecordVO;
import com.yunya.feign.discount.domain.vo.DeductionRefundRecordVO;
import com.yunya.feign.discount.domain.vo.DeductionUsedRecordVO;
import com.yunya.models.discount.CouponOrderVirtual;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CouponOrderVirtualMapper extends Mapper<CouponOrderVirtual> {
    void insertList(@Param("virtuals") List<CouponOrderVirtual> virtuals);

    List<DeductionRefundRecordVO> refundList(@Param("query")DeductionRecordQuery query);
    List<DeductionUsedRecordVO> usedList(@Param("query")DeductionRecordQuery query);
    List<DeductionChangeRecordVO> changeList(@Param("query")DeductionRecordQuery query);
}