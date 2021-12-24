package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.StatEmpPay;
import com.yunya.report.ultimate.mapper.StatEmpPayMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 简介：员工收费时统计业务层
 *
 * @author: chenlin
 * @Description: 员工收费时统计业务层
 * @Date: 2021/12/15 17:05
 * @since: 1.0.0
 */
@Service
public class StatEmpPayBiz extends BaseBiz<StatEmpPayMapper, StatEmpPay> {
    /**
     * 统计实收工作量
     *
     * @param query
     * @param orgIds 非空时，根据门诊分组统计
     * @param employeeIds 非空时，根据员工分组统计
     * @return
     */
    public List<StatEmpPay> findClinicReceivedWorkload(DateRangeQueryForm query, List<Integer> orgIds, List<Integer> employeeIds) {
        return mapper.selectClinicReceivedWorkload(query, orgIds, employeeIds);
    }
}
