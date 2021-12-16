package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.base.NumDateRangeQueryForm;
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
    public List<StatEmpTreat> findClinicTreatVisitNum(NumDateRangeQueryForm query, List<Integer> orgIds, List<Integer> employeeIds) {
        return mapper.selectClinicTreatVisitNum(query, orgIds, employeeIds);
    }
}
