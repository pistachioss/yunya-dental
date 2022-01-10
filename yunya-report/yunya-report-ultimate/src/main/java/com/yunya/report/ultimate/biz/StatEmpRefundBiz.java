package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.StatEmpRefund;
import com.yunya.report.ultimate.mapper.StatEmpRefundMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/1/4 13:25
 * @since: 1.0.0
 */
@Service
public class StatEmpRefundBiz extends BaseBiz<StatEmpRefundMapper, StatEmpRefund> {
    public List<BillExecutorItemVO> findStatisticsEmployeeRefundWorkload(MultiClinicDateRangeQueryForm query, List<Integer> employeeIds) {
        return mapper.selectStatisticsEmployeeRefundWorkload(query, employeeIds);
    }
}
