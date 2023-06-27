package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.BillItemInfoQuery;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.EmployeeFreePaymentWorkloadDetailQuery;
import com.yunya.feign.report.domain.vo.BillChargeVO;
import com.yunya.feign.report.domain.vo.BillItemReceivedStatisticsVO;
import com.yunya.feign.report.domain.vo.BillWorkloadVO;
import com.yunya.feign.report.domain.vo.EmployeeReceivedDetailWorkloadVO;
import com.yunya.models.report.BaseBillPayShare;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBillPayShareMapper extends Mapper<BaseBillPayShare> {

    /**
     * 根据条件查询收费记录ID、订单ID列表
     *
     * @param query 查询条件
     * @param isExecutor 是否查询执行人
     * @return list
     */
    List<BillChargeVO> selectBillIdsAndBillPayIds(@Param("query") DataStatisticsQuery query, @Param("isExecutor") Boolean isExecutor);

    /**
     * 根据月份分组求已收工作量合计
     *
     * @param query 查询条件
     * @param isExecutor 是否查询执行人
     * @return
     */
    List<BillWorkloadVO> selectRecievedWorkloadsGroupByMonth(
            @Param("query") DataStatisticsQuery query, @Param("isExecutor") Boolean isExecutor);

    /**
     * 根据条件查询个人开单项目实收金额统计明细表
     *
     * @param query
     * @return
     */
    List<BillItemReceivedStatisticsVO> billItemReceivedStatistics(
            @Param("query") BillItemInfoQuery query);

    /**
     * 根据条件查询员工免单支付工作量明细项目列表
     *
     * @param query
     * @return
     */
    List<EmployeeReceivedDetailWorkloadVO> selectEmployeeFreePaymentDetailList(@Param("query") EmployeeFreePaymentWorkloadDetailQuery query);
}