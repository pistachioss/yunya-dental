package com.yunya.middletable.dao.discount;

import com.yunya.models.discount.CouponChangeRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface CouponChangeRecordMapper extends Mapper<CouponChangeRecord> {
    CouponChangeRecord getLatest(@Param("patientId") Integer patientId, @Param("cardId") Integer cardId);
}