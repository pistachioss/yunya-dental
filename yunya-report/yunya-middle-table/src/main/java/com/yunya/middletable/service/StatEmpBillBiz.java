package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.StatEmpBillMapper;
import com.yunya.models.report.StatEmpBill;
import com.yunya.models.treatment.BillRecord;
import com.yunya.models.treatment.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.RedisConstants.LOCK_STATISTICS_EMP_BILL;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/12/13 17:31
 * @since: 1.0.0
 */
@Service
public class StatEmpBillBiz extends BaseBiz<StatEmpBillMapper, StatEmpBill> {
    @Autowired private RedisLockBiz redisLockBiz;
    /** 员工收费时统计*/
    @Autowired private StatEmpPayBiz statEmpPayBiz;

    public void incStatEmpBill(BillRecord billRecord) {
        Integer crtId = billRecord.getCrtId();
        List<OrderDetail> details = statEmpPayBiz.findOrderDetailByOrderRecordId(billRecord.getOrderRecordId(),
                billRecord.getActualReceivableAmount());
        if (StringHelper.isNotEmpty(details)) {
            details.forEach(detail -> {
                Integer orgId = detail.getOrgId();
                Integer executorId = detail.getExecutorId();
                Integer billDate = DateUtil.date2Number(billRecord.getCrtTime());
                StatEmpBill entity = new StatEmpBill();
                entity.setOrgId(orgId);
                entity.setDentistId(executorId);
                entity.setBillDate(billDate);
                entity.setItemType(detail.getType());
                entity.setItemId(detail.getBillingItemId());
                String lockKey = Joiner.on(":").join(LOCK_STATISTICS_EMP_BILL, orgId, executorId);
                String lockVal = String.valueOf(billDate);
                redisLockBiz.lockedApply(lockKey, lockVal, (t) -> {
                    StatEmpBill statEmpBill = mapper.selectByPrimaryKey(entity);
                    if (detail.getInservice()) {
                        if (ObjectUtils.isEmpty(statEmpBill)) {// 生成新数据
                            entity.setReceivableWorkload(detail.getReceivableAmount());
                            entity.setCrtId(crtId);
                            entity.setCrtTime(new Date(System.currentTimeMillis()));
                            mapper.insertSelective(entity);
                        } else { // 增量更新
                            entity.setReceivableWorkload(entity.getReceivableWorkload().add(detail.getReceivableAmount()));
                            entity.setQuantity(entity.getQuantity() + detail.getQuantity());
                            mapper.updateByPrimaryKeySelective(entity);
                        }
                    } else {// 减量更新
                        entity.setReceivableWorkload(entity.getReceivableWorkload().subtract(detail.getReceivableAmount()));
                        entity.setQuantity(entity.getQuantity()-detail.getQuantity());
                        mapper.updateByPrimaryKeySelective(entity);
                    }
                    return null;
                });
            });
        }
    }
}
