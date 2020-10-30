package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseRefundDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BaseRefundDetailMapper extends Mapper<BaseRefundDetail> {

    /**
     * 根据退费记录ID删除退费明细
     *
     * @param refundId 退费记录ID
     */
    void deleteByRefundId(@Param("refundId") Integer refundId);
}