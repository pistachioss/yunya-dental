package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.StatEmpBillMapper;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseBillDetail;
import com.yunya.models.report.StatEmpBill;
import com.yunya.models.treatment.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.RedisConstants.LOCK_STATISTICS_EMP_BILL;

/**
 * 简介：员工账单时统计业务层
 *
 * @author: chenlin
 * @Description: 员工账单时统计业务层
 * @Date: 2021/12/13 17:31
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StatEmpBillBiz extends BaseBiz<StatEmpBillMapper, StatEmpBill> {
    @Autowired private RedisLockBiz redisLockBiz;
    /** 账单明细*/
    @Autowired private BaseBillDetailMapper baseBillDetailMapper;

    public void incStatEmpBill(List<OrderDetail> orderDetails, BaseBill bill) {
        Integer orgId = bill.getOrgId();
        Date date = new Date(System.currentTimeMillis());
        Integer billDate = DateUtil.date2Number(bill.getBillDate());
        if (StringHelper.isNotEmpty(orderDetails)) {
            List<Integer> executorIds = new ArrayList<>();
            orderDetails.forEach(vo->{
                Integer executorId = vo.getExecutorId();
                StatEmpBill entity = new StatEmpBill();
                entity.setOrgId(orgId);
                entity.setDentistId(executorId);
                entity.setBillDate(billDate);
                entity.setItemType(vo.getType());
                entity.setItemId(vo.getBillingItemId());
                mapper.deleteByPrimaryKey(entity);
                if (vo.getInservice() && !executorIds.contains(executorId)) {
                    executorIds.add(executorId);
                }
            });
            List<BaseBillDetail> details = baseBillDetailMapper.groupBillDetailByDateAndExecutorId(orgId, billDate, null, executorIds);
            if (StringHelper.isNotEmpty(details)) {
                Integer crtId = bill.getBillerId();
                details.forEach(vo->{
                    Integer executorId = vo.getExecutorId();
                    String lockKey = Joiner.on(":").join(LOCK_STATISTICS_EMP_BILL, orgId, billDate);
                    String lockVal = String.valueOf(executorId);
                    redisLockBiz.lockedApply(lockKey, lockVal,(t)->{
                        StatEmpBill entity = new StatEmpBill();
                        entity.setOrgId(orgId);
                        entity.setDentistId(executorId);
                        entity.setBillDate(billDate);
                        entity.setItemType(vo.getItemType());
                        entity.setItemId(vo.getItemId());
                        entity.setQuantity(vo.getQuantity());
                        entity.setReceivableWorkload(vo.getDiscountAmount());
                        entity.setReceivedWorkload(vo.getReceivedAmount());
                        entity.setCrtId(crtId);
                        entity.setCrtTime(date);
                        mapper.insertSelective(entity);
                        return null;
                    });
                });
            }
        }
    }
}
