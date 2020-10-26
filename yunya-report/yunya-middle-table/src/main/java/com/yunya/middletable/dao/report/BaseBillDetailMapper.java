package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseBillDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BaseBillDetailMapper extends Mapper<BaseBillDetail> {

    /**
     * 根据账单ID删除账单明细
     *
     * @param billId 账单ID
     */
    void deleteByBillId(@Param("billId") Integer billId);
}