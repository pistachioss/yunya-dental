package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.StatEmpTreat;
import com.yunya.report.ultimate.mapper.StatEmpTreatMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 简介：员工就诊统计业务层
 *
 * @author: chenlin
 * @Description: 员工就诊统计业务层
 * @Date: 2021/12/15 17:06
 * @since: 1.0.0
 */
@Service
public class StatEmpTreatBiz extends BaseBiz<StatEmpTreatMapper, StatEmpTreat> {
    /**
     * 统计就诊次数
     *
     * @param query
     * @param orgIds 非空时，根据门诊分组统计
     * @param employeeIds 非空时，根据员工分组统计
     * @return
     */
    public List<StatEmpTreat> findClinicTreatVisitNum(DateRangeQueryForm query, List<Integer> orgIds, List<Integer> employeeIds) {
        return mapper.selectClinicTreatVisitNum(query, orgIds, employeeIds);
    }
}
