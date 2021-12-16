package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.StatEmpPayMapper;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseBillPay;
import com.yunya.models.report.StatEmpPay;
import com.yunya.models.treatment.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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
public class StatEmpPayBiz extends BaseBiz<StatEmpPayMapper, StatEmpPay> {
    @Autowired private RedisLockBiz redisLockBiz;
    /** 账单明细*/
    @Autowired private BaseBillDetailMapper baseBillDetailMapper;

    public void incStatEmpPay(List<OrderDetail> orderDetails, BaseBill bill, BaseBillPay baseBillPay) {
        Integer orgId = bill.getOrgId();
        Date date = new Date(System.currentTimeMillis());
        Integer payDate = DateUtil.date2Number(baseBillPay.getPayeeDate());
        if (StringHelper.isNotEmpty(orderDetails)) {
            Set<Integer> executorIds = new HashSet<>();
            orderDetails.forEach(vo->{
                Integer executorId = vo.getExecutorId();
                StatEmpPay entity = new StatEmpPay();
                entity.setOrgId(orgId);
                entity.setDentistId(executorId);
                entity.setPayDate(payDate);
                entity.setItemType(vo.getType());
                entity.setItemId(vo.getBillingItemId());
                mapper.deleteByPrimaryKey(entity);
                if (vo.getInservice() && !ObjectUtils.isEmpty(executorId)) {
                    executorIds.add(executorId);
                }
            });
            List<BillExecutorItemVO> details = baseBillDetailMapper.selectBillDetailByDateAndExecutorId(orgId,
                    null, payDate, executorIds);
            sharedItemAmount(details);
            details = statisticsExecutorItem(details);
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
                        entity.setReceivedWorkload(vo.getReceivedWorkload());
                        entity.setCrtId(payeeUserId);
                        entity.setCrtTime(date);
                        mapper.insertSelective(entity);
                        return null;
                    });
                });
            }
        }
    }

    /**
     * 统计执行人、项目的数量和实收
     * @param details
     * @return
     */
    public List<BillExecutorItemVO> statisticsExecutorItem(List<BillExecutorItemVO> details) {
        if (StringHelper.isNotEmpty(details)) {
            Map<String, BillExecutorItemVO> map = new HashMap<>(16);
            details.forEach(vo->{
                String key = vo.getExecutorId() + "," + vo.getItemType() + "," + vo.getItemId();
                BillExecutorItemVO executorItem = map.get(key);
                if (ObjectUtils.isEmpty(executorItem)) {
                    executorItem = new BillExecutorItemVO();
                    executorItem.setItemType(vo.getItemType());
                    executorItem.setItemId(vo.getItemId());
                    executorItem.setExecutorId(vo.getExecutorId());
                }
                executorItem.setQuantity(executorItem.getQuantity() + vo.getQuantity());
                executorItem.setReceivableWorkload(executorItem.getReceivableWorkload().add(vo.getReceivableWorkload()));
                executorItem.setReceivedWorkload(executorItem.getReceivedWorkload().add(vo.getReceivedWorkload()));
                map.put(key, executorItem);
            });
            return new ArrayList<>(map.values());
        }
        return null;
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
     *
     * @param details
     * @return
     */
    private void sharedItemAmount(List<BillExecutorItemVO> details) {
        // 每个账单的执行实收总额
        Map<Integer, BigDecimal[]> total = new HashMap<>(16);
        if (StringHelper.isNotEmpty(details)) {
            details.forEach(
                    detail -> {
                        Integer billId = detail.getBillId();
                        BigDecimal[] sum = total.get(billId);
                        if (sum == null) {
                            // 执行项目的总应收，累加项目应收，累加项目占比值
                            sum = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
                        }
                        sum[0] = sum[0].add(detail.getReceivableWorkload());
                        total.put(billId, sum);
                    });
            details.forEach(
                    detail -> {
                        BigDecimal receivableWorkload = detail.getReceivableWorkload();
                        Integer billId = detail.getBillId();
                        BigDecimal[] sum = total.get(billId);
                        sum[1] = sum[1].add(receivableWorkload);
                        BigDecimal amount = receivableWorkload.divide(sum[0], 4, BigDecimal.ROUND_HALF_UP);
                        if (sum[0].compareTo(sum[1]) == 0) { // 最后一个占比项目
                            amount = BigDecimal.ONE.subtract(sum[2]);
                        }
                        sum[2] = sum[2].add(amount);
                        BigDecimal receivedWorkload = amount.multiply(detail.getTotalReceivedWorkload());
                        if (receivedWorkload.compareTo(receivableWorkload)>0) {// 超出应收说明无欠费，则项目实收=项目总实收
                            receivedWorkload = detail.getReceivedWorkload();
                        }
                        detail.setReceivedWorkload(receivedWorkload);
                    });
        }
    }
}
