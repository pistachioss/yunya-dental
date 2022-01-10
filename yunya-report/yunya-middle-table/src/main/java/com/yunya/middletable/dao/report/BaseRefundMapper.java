package com.yunya.middletable.dao.report;

import com.yunya.feign.report.domain.query.StatisticsEmployeeQueryForm;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.models.report.BaseRefund;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseRefundMapper extends Mapper<BaseRefund> {
    List<BillExecutorItemVO> selectBillItemRefundListByDate(
            @Param("query") StatisticsEmployeeQueryForm query);
}