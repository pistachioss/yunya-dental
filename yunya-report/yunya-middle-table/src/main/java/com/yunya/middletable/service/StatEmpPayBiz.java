package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.StatEmpPayMapper;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseBillDetail;
import com.yunya.models.report.BaseBillPay;
import com.yunya.models.report.StatEmpPay;
import com.yunya.models.treatment.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.yunya.framework.common.constant.RedisConstants.LOCK_STATISTICS_EMP_PAY;

/**
 * 简介：员工收费时统计层
 *
 * @author: chenlin
 * @Description: 员工收费时统计业务层
 * @Date: 2021/12/13 14:42
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StatEmpPayBiz extends BaseBiz<StatEmpPayMapper, StatEmpPay> {
    @Autowired private RedisLockBiz redisLockBiz;
    /** 账单明细*/
    @Autowired private BaseBillDetailMapper baseBillDetailMapper;

    public void incStatEmpPay(List<OrderDetail> orderDetails, BaseBill bill, BaseBillPay baseBillPay) {
        Integer orgId = bill.getOrgId();
        Date date = new Date(System.currentTimeMillis());
        Integer payDate = DateUtil.date2Number(baseBillPay.getPayeeDate());
        if (StringHelper.isNotEmpty(orderDetails)) {
            List<Integer> executorIds = new ArrayList<>();
            orderDetails.forEach(vo->{
                Integer executorId = vo.getExecutorId();
                StatEmpPay entity = new StatEmpPay();
                entity.setOrgId(orgId);
                entity.setDentistId(executorId);
                entity.setPayDate(payDate);
                entity.setItemType(vo.getType());
                entity.setItemId(vo.getBillingItemId());
                mapper.deleteByPrimaryKey(entity);
                if (vo.getInservice() && !executorIds.contains(executorId)) {
                    executorIds.add(executorId);
                }
            });
            List<BaseBillDetail> details = baseBillDetailMapper.groupBillDetailByDateAndExecutorId(orgId, null, payDate, executorIds);
            sharedItemAmount(details);
            if (StringHelper.isNotEmpty(details)) {
                Integer payeeUserId = baseBillPay.getPayeeUserId();
                details.forEach(vo->{
                    Integer executorId = vo.getExecutorId();
                    String lockKey = Joiner.on(":").join(LOCK_STATISTICS_EMP_PAY, orgId, payDate);
                    String lockVal = String.valueOf(executorId);
                    redisLockBiz.lockedApply(lockKey, lockVal,(t)->{
                        StatEmpPay entity = new StatEmpPay();
                        entity.setOrgId(orgId);
                        entity.setDentistId(executorId);
                        entity.setPayDate(payDate);
                        entity.setItemType(vo.getItemType());
                        entity.setItemId(vo.getItemId());
                        entity.setReceivedWorkload(vo.getDiscountAmount().multiply(vo.getCouponWorkload()));
                        entity.setCrtId(payeeUserId);
                        entity.setCrtTime(date);
                        mapper.insertSelective(entity);
                        return null;
                    });
                });
            }
        }
    }

//    public List<OrderDetail> findOrderDetailByOrderRecordId(Integer orderRecordId, BigDecimal amount) {
//        OrderDetail query = new OrderDetail();
//        query.setOrderRecordId(orderRecordId);
//        query.setInservice(true);
//        List<OrderDetail> orderDetails = orderDetailMapper.select(query);
//        // 项目应收转项目实收
//        if (StringHelper.isNotEmpty(orderDetails)) {
//            // 分摊: 项目应收/账单应收 * 总金额（实收/应收）
//            BigDecimal totalWorkload = new BigDecimal("0.00");
//            for (OrderDetail vo : orderDetails) {
//                totalWorkload = totalWorkload.add(vo.getReceivableAmount());
//            }
//            BigDecimal percentotal = new BigDecimal("0.00");
//            for (int i = 0; i < orderDetails.size(); i++) {
//                OrderDetail detail = orderDetails.get(i);
//                BigDecimal receivableAmount = detail.getReceivableAmount();
//                BigDecimal percentage = BigDecimal.ZERO;
//                if (totalWorkload.compareTo(BigDecimal.ZERO) != 0) {
//                    percentage = receivableAmount.divide(totalWorkload, 2, BigDecimal.ROUND_HALF_UP);
//                    percentotal = percentotal.add(percentage);
//                }
//                if (i == orderDetails.size() - 1) {// 最后一个需要补差值，避免比例总和不为1
//                    percentage = percentage.add(BigDecimal.ONE.subtract(percentotal));
//                }
//                detail.setReceivableAmount(percentage.multiply(amount));
//            }
//        }
//        return orderDetails;
//    }

    /**
     * 对baseBillDetail中各项目的分摊占比值
     *      BaseBillDetail.discountAmount--项目应收
     *      BaseBillDetail.receivedAmount--占比值
     * @param details
     * @return
     */
    private void sharedItemAmount(List<BaseBillDetail> details) {
        // 每个账单的执行实收总额
        Map<Integer, BigDecimal[]> total = new HashMap<>(16);
        details.forEach(
                detail -> {
                    Integer billId = detail.getBillId();
                    BigDecimal[] sum = total.get(billId);
                    if (sum == null) {
                        sum = new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
                    }
                    sum[0] = sum[0].add(detail.getReceivedAmount());
                    total.put(billId, sum);
                });
        details.forEach(
                detail -> {
                    BigDecimal receivedAmount = detail.getReceivedAmount();
                    Integer billId = detail.getBillId();
                    BigDecimal[] sum = total.get(billId);
                    sum[1] = sum[1].add(receivedAmount);
                    BigDecimal amount = receivedAmount.divide(sum[0], 2, BigDecimal.ROUND_HALF_UP);
                    if (sum[0].compareTo(sum[1]) == 0) { // 最后一个占比项目
                        amount = BigDecimal.ONE.subtract(sum[2]);
                    }
                    sum[2] = sum[2].add(amount);
                    detail.setDiscountAmount(amount);
                });
    }
}
