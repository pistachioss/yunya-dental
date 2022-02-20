package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.StatEmpPrivilege;
import com.yunya.report.ultimate.mapper.StatEmpPrivilegeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/1/18 17:49
 * @since: 1.0.0
 */
@Service
public class StatEmpPrivilegeBiz extends BaseBiz<StatEmpPrivilegeMapper, StatEmpPrivilege> {

    public List<BillExecutorItemVO> findStatisticsEmployeeCouponWorkload(MultiClinicDateRangeQueryForm query, List<Integer> employeeIds) {
        return mapper.selectStatisticsEmployeeCouponWorkload(query, employeeIds);
    }
}
