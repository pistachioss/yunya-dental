package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
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
    public List<StatEmpBill> findBillItemList(MultiClinicDateRangeQueryForm query) {
        return mapper.selectBillItemList(query);
    }
}
