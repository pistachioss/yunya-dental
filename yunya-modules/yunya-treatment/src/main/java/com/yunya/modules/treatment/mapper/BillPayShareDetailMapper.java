package com.yunya.modules.treatment.mapper;

import com.yunya.models.treatment.BillPayShareDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BillPayShareDetailMapper extends Mapper<BillPayShareDetail> {
    void batchSave(List<BillPayShareDetail> shareDetails);

    void removeByUniqueKey(
            @Param("orderRecordId") Integer orderRecordId,
            @Param("billPayId") Integer billPayId,
            @Param("orderDetailId") Integer orderDetailId);
}