package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.BillPayShareDetailQuery;
import com.yunya.models.treatment.BillPayShareDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BillPayShareDetailMapper extends Mapper<BillPayShareDetail> {
    void batchSave(List<BillPayShareDetail> shareDetails);

    /**
     * 根据条件逻辑删除
     *
     * @param orderRecordId
     * @param billPayId
     * @param orderDetailId
     */
    void tombstoneByCombinationKey(
            @Param("orderRecordId") Integer orderRecordId,
            @Param("billPayId") Integer billPayId,
            @Param("orderDetailId") Integer orderDetailId);

    /**
     * 根据条件物理删除
     *
     * @param query
     */
    void removeByBillDateRange(@Param("query") BillPayShareDetailQuery query);
}