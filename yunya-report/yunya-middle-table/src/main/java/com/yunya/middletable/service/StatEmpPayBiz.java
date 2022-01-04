package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.StatEmpPayMapper;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseBillPay;
import com.yunya.models.report.BaseBillPayDetail;
import com.yunya.models.report.StatEmpPay;
import com.yunya.models.treatment.OrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.RedisConstants.LOCK_STATISTICS_EMP_PAY;

/**
 * 简介：员工收费时统计层
 *
 * @author: chenlin
 * @Description: 员工收费时统计业务层
 * @Date: 2021/12/13 14:42
 * @since: 1.0.0
 */
@Slf4j
@Service
public class StatEmpPayBiz extends BaseBiz<StatEmpPayMapper, StatEmpPay> {
    @Autowired private RedisLockBiz redisLockBiz;
    /** 账单明细*/
    @Autowired private BaseBillDetailMapper baseBillDetailMapper;
    /** 多线程 */
    @Resource(name = "customizeThreadPool")
    private ExecutorService importExcelThreadPool;

    /**
     * 账单收费时统计执行人的账单相关数据
     *
     * @param orderDetails
     * @param bill
     * @param baseBillPay
     */
    public void statisticsEmployeeByPayDate(List<OrderDetail> orderDetails, BaseBill bill, BaseBillPay baseBillPay) {
        Integer orgId = bill.getOrgId();
        Date date = new Date(System.currentTimeMillis());
        Integer payDate = DateUtil.date2Number(baseBillPay.getPayeeDate());
        if (StringHelper.isNotEmpty(orderDetails)) {
            Set<String> keys = new HashSet<>();
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
                keys.add(executorId + "," + vo.getType() + "," + vo.getBillingItemId());
            });
            if (StringHelper.isNotEmpty(executorIds)) {
                List<BillExecutorItemVO> details = baseBillDetailMapper.selectBillDetailByDateAndExecutorId(orgId,
                        null, payDate, executorIds);
                Map<String, BigDecimal> freeMap = findFreePaymentMap(orgId, payDate, executorIds);
                sharedItemAmount(details, freeMap, (vo)-> vo.getBillId()+"");
                details = statisticsExecutorItem(details, keys, (vo)-> vo.getExecutorId() + "," + vo.getItemType() + "," + vo.getItemId());
                if (StringHelper.isNotEmpty(details)) {
                    Integer payeeUserId = baseBillPay.getPayeeUserId();
                    details.forEach(vo -> {
                        Integer executorId = vo.getExecutorId();
                        String lockKey = Joiner.on(":").join(LOCK_STATISTICS_EMP_PAY, orgId, payDate);
                        String lockVal = String.valueOf(executorId);
                        redisLockBiz.lockedApply(lockKey, lockVal, (t) -> {
                            StatEmpPay entity = new StatEmpPay();
                            entity.setOrgId(orgId);
                            entity.setDentistId(executorId);
                            entity.setPayDate(payDate);
                            entity.setItemType(vo.getItemType());
                            entity.setItemId(vo.getItemId());
                            entity.setReceivedWorkload(vo.getReceivedWorkload());
                            entity.setFreePaymentWorkload(vo.getFreePaymentWorkload());
                            entity.setCouponWorkload(vo.getCouponWorkload());
//                            entity.setRefundWorkload(vo.getRefundWorkload());
                            entity.setCrtId(payeeUserId);
                            entity.setCrtTime(date);
                            mapper.insertSelective(entity);
                            return null;
                        });
                    });
                }
            }
        }
    }

    private Map<String, BigDecimal> findFreePaymentMap(Integer orgId, Integer payDate, Set<Integer> executorIds) {
        Map<String, BigDecimal> result = new HashMap<>(16);
        List<BaseBillPayDetail> frees = baseBillDetailMapper.selectFreePaymentAmountByDate(orgId, payDate, executorIds);
        if (StringHelper.isNotEmpty(frees)) {
            frees.forEach(vo-> result.put(vo.getBillId()+"", vo.getPrincipalAmount()));
        }
        return result;
    }

    /**
     * 统计执行人的项目的数量、应收、实收
     * @param details
     * @param keys
     * @return
     */
    public List<BillExecutorItemVO> statisticsExecutorItem(List<BillExecutorItemVO> details, Set<String> keys, Function<BillExecutorItemVO, String> func) {
        if (StringHelper.isNotEmpty(details)) {
            Map<String, BillExecutorItemVO> map = new HashMap<>(16);
            details.stream().filter(vo->{
                if (ObjectUtils.isEmpty(vo.getExecutorId())) {
                    return false;
                }
                if (StringHelper.isNotEmpty(keys)) {
                    return keys.contains(func.apply(vo));
                }
                return true;
            }).forEach(vo->{
                String key = func.apply(vo);
                BillExecutorItemVO executorItem = map.get(key);
                if (ObjectUtils.isEmpty(executorItem)) {
                    executorItem = new BillExecutorItemVO();
                    executorItem.setOrgId(vo.getOrgId());
                    executorItem.setBillDate(vo.getBillDate());
                    executorItem.setItemType(vo.getItemType());
                    executorItem.setItemId(vo.getItemId());
                    executorItem.setExecutorId(vo.getExecutorId());
                }
                executorItem.setQuantity(executorItem.getQuantity() + vo.getQuantity());
                executorItem.setReceivableWorkload(executorItem.getReceivableWorkload().add(vo.getReceivableWorkload()));
                executorItem.setReceivedWorkload(executorItem.getReceivedWorkload().add(vo.getReceivedWorkload()));
                executorItem.setCouponWorkload(executorItem.getCouponWorkload().add(vo.getCouponWorkload()));
                executorItem.setFreePaymentWorkload(executorItem.getFreePaymentWorkload().add(vo.getFreePaymentWorkload()));
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
    private void sharedItemAmount(List<BillExecutorItemVO> details, Map<String, BigDecimal> freeMap, Function<BillExecutorItemVO, String> func) {
        // 每个账单的执行实收总额
        Map<String, BigDecimal[]> total = new HashMap<>(16);
        if (StringHelper.isNotEmpty(details)) {
            details = details.stream().filter(vo->vo.getReceivableWorkload().compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
            details.forEach(
                    detail -> {
                        String key = func.apply(detail);
//                        Integer billId = detail.getBillId();
                        BigDecimal[] sum = total.get(key);
                        if (sum == null) {
                            // 执行项目的总应收，累加项目应收，累加项目占比值
                            sum = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
                        }
//                        sum[0] = sum[0].add(detail.getReceivableWorkload());
                        sum[0] = detail.getActualWorkload();
                        total.put(key, sum);
                    });
            details.forEach(
                    detail -> {
                        BigDecimal receivableWorkload = detail.getReceivableWorkload();
                        String key = func.apply(detail);
                        BigDecimal[] sum = total.get(key);
                        BigDecimal freePayment = freeMap.get(key);
                        if (   ObjectUtils.isEmpty(freePayment)) {
                            freePayment = BigDecimal.ZERO;
                        } else {
                            System.out.println("");
                        }
                        sum[1] = sum[1].add(receivableWorkload);
//                        BigDecimal amount = BigDecimal.ZERO;
//                        if (sum[0].compareTo(BigDecimal.ZERO) != 0) {
//                            amount = receivableWorkload.divide(sum[0]);
//                        }
//                        if (sum[0].compareTo(sum[1]) == 0) { // 最后一个占比项目
//                            amount = BigDecimal.ONE.subtract(sum[2]);
//                        }
//                        sum[2] = sum[2].add(amount);
//                        BigDecimal receivedWorkload = amount.multiply(detail.getTotalReceivedWorkload()).setScale(2, BigDecimal.ROUND_HALF_UP);
//                        if (receivedWorkload.compareTo(receivableWorkload)>0) {// 超出应收说明无欠费，则项目实收=项目总实收
//                            receivedWorkload = detail.getReceivedWorkload();
//                        }
                        BigDecimal receivedWorkload = BigDecimal.ZERO;
                        BigDecimal freePaymentWorkload = BigDecimal.ZERO;
                        if (sum[0].compareTo(BigDecimal.ZERO) != 0) {
                            receivedWorkload = receivableWorkload.divide(sum[0], 8, BigDecimal.ROUND_HALF_UP).multiply(detail.getTotalReceivedWorkload());
                            if (freePayment.compareTo(sum[0])>0) {
                                freePayment = sum[0];
                            }
                            freePaymentWorkload = receivableWorkload.divide(sum[0], 8, BigDecimal.ROUND_HALF_UP).multiply(freePayment);
                        }
                        detail.setReceivedWorkload(receivedWorkload);
                        detail.setFreePaymentWorkload(freePaymentWorkload);
                    });
        }
    }

    /**
     * 批量拉取
     *
     * @param form
     * @throws InterruptedException
     */
    public void pullPayDateStatistics(PullForm form) throws InterruptedException {
        Date now = new Date(System.currentTimeMillis());
        String startDate = form.getStartDate();
        String endDate = form.getEndDate();
        List<BillExecutorItemVO> details = baseBillDetailMapper.groupBillItemDetailListByPayDate(startDate, endDate);
        Map<String, BigDecimal> freeMap = findFreePaymentMap(startDate, endDate);
        sharedItemAmount(details, freeMap, (vo)-> vo.getBillId() + "," +vo.getBillDate());
        details = statisticsExecutorItem(details, null, (vo)-> vo.getOrgId() + "," + vo.getBillDate() + "," + vo.getExecutorId() + "," + vo.getItemType() + "," + vo.getItemId());
        if (StringHelper.isNotEmpty(details)) {
            List<StatEmpPay> datas = new ArrayList<>();
            details.forEach(vo->{
                Integer executorId = vo.getExecutorId();
                StatEmpPay entity = new StatEmpPay();
                entity.setOrgId(vo.getOrgId());
                entity.setDentistId(executorId);
                entity.setPayDate(vo.getBillDate());
                entity.setItemType(vo.getItemType());
                entity.setItemId(vo.getItemId());
                entity.setReceivedWorkload(vo.getReceivedWorkload());
                entity.setCouponWorkload(vo.getCouponWorkload());
                entity.setFreePaymentWorkload(vo.getFreePaymentWorkload());
//                entity.setRefundWorkload(vo.getRefundWorkload());
                entity.setCrtId(vo.getExecutorId());
                entity.setCrtTime(now);
                datas.add(entity);
            });
            List<Future> resultFutures = new ArrayList<>();
            List<List<StatEmpPay>> partition = Lists.partition(datas, 1000);
            CountDownLatch latch = new CountDownLatch(partition.size());
            partition.forEach(vo -> resultFutures.add(
                    importExcelThreadPool.submit(() -> {
                        try {
                            insertBatch(vo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            latch.countDown();
                        }
                    })
            ));
            latch.await();
            BaseTreatmentProcessBiz.printExceptionLog(resultFutures, log);

        }
    }

    private Map<String, BigDecimal> findFreePaymentMap(String startDate, String endDate) {
        Map<String, BigDecimal> result = new HashMap<>(16);
        List<BillExecutorItemVO> frees = baseBillDetailMapper.selectFreePaymentAmount(startDate, endDate);
        if (StringHelper.isNotEmpty(frees)) {
            frees.forEach(vo->{
                String key = vo.getBillId() + "," + vo.getBillDate();
                BigDecimal freePayment = result.get(key);
                if (freePayment == null) {
                    freePayment = BigDecimal.ZERO;
                }
                result.put(key, freePayment.add(vo.getFreePaymentWorkload()));
            });
        }
        return result;
    }

    /**
     * 批量新增
     *
     * @param datas
     */
    private void insertBatch(List<StatEmpPay> datas) {
        mapper.insertBatch(datas);
    }
}
