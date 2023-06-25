package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.vo.BillChargeVO;
import com.yunya.feign.report.domain.vo.BillWorkloadVO;
import com.yunya.models.report.BaseBillPayShare;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBillPayShareMapper extends Mapper<BaseBillPayShare> {

    /**
     * 根据条件查询收费记录ID、订单ID列表
     *
     * @param query 查询条件
     * @return list
     */
    List<BillChargeVO> selectBillIdsAndBillPayIds(@Param("query") DataStatisticsQuery query);

    /**
     * 根据月份分组求已收工作量合计
     *
     * @param query
     * @return
     */
    List<BillWorkloadVO> selectRecievedWorkloadsGroupByMonth(
            @Param("query") DataStatisticsQuery query);
}