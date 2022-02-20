package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.StatEmpBill;
import com.yunya.report.ultimate.mapper.StatEmpBillMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 简介：员工账单时统计业务层
 *
 * @author: chenlin
 * @Description: 员工账单时统计业务层
 * @Date: 2021/12/15 16:59
 * @since: 1.0.0
 */
@Service
public class StatEmpBillBiz extends BaseBiz<StatEmpBillMapper, StatEmpBill> {
    /**
     * 查询账单时项目数量
     *
     * @param query
     * @param groupByOrg
     * @return
     */
    public List<StatEmpBill> findBillItemNum(MultiClinicDateRangeQueryForm query, boolean groupByOrg) {
        return mapper.selectBillItemNum(query, groupByOrg);
    }

    public List<BillExecutorItemVO> findStatisticsEmployeeBillWorkload(MultiClinicDateRangeQueryForm query, List<Integer> employeeIds) {
        return mapper.selectStatisticsEmployeeBillWorkload(query, employeeIds);
    }
}
