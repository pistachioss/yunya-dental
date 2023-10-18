package com.yunya.modules.treatment.mapper;

import com.yunya.models.treatment.BillPayRecordLog;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BillPayRecordLogMapper extends Mapper<BillPayRecordLog> {
    /**
     * 查询账单首次收费日志
     *
     * @param orderRecordId
     * @return
     */
    BillPayRecordLog selectBillFirstPayRecordLogByOrderId(@Param("orderRecordId") Integer orderRecordId);
}