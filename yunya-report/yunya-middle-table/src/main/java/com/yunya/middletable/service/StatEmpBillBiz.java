package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.StatEmpBillMapper;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.StatEmpBill;
import com.yunya.models.treatment.OrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.yunya.framework.common.constant.RedisConstants.LOCK_STATISTICS_EMP_BILL;

/**
 * 简介：员工账单时统计业务层
 *
 * @author: chenlin
 * @Description: 员工账单时统计业务层
 * @Date: 2021/12/13 17:31
 * @since: 1.0.0
 */
@Slf4j
@Service
public class StatEmpBillBiz extends BaseBiz<StatEmpBillMapper, StatEmpBill> {
    @Autowired private RedisLockBiz redisLockBiz;
    @Autowired private StatEmpPayBiz statEmpPayBiz;
    /** 账单明细*/
    @Autowired private BaseBillDetailMapper baseBillDetailMapper;
    /** 多线程 */
    @Resource(name = "customizeThreadPool")
    private ExecutorService importExcelThreadPool;

    /**
     * 账单生成时统计
     *
     * @param orderDetails
     * @param bill
     */
    public void statisticsInBillDate(List<OrderDetail> orderDetails, BaseBill bill) {
        Integer orgId = bill.getOrgId();
        Date date = new Date(System.currentTimeMillis());
        Integer billDate = DateUtil.date2Number(bill.getBillDate());
        if (StringHelper.isNotEmpty(orderDetails)) {
            Set<Integer> executorIds = new HashSet<>();
            Set<String> keys = new HashSet<>();
            orderDetails.forEach(vo->{
                Integer executorId = vo.getExecutorId();
                StatEmpBill entity = new StatEmpBill();
                entity.setOrgId(orgId);
                entity.setDentistId(executorId);
                entity.setBillDate(billDate);
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
                        billDate, null, executorIds);
                details = statEmpPayBiz.statisticsExecutorItem(details, keys, (vo)-> vo.getExecutorId() + "," + vo.getItemType() + "," + vo.getItemId());
                if (StringHelper.isNotEmpty(details)) {
                    Integer crtId = bill.getBillerId();
                    details.forEach(vo -> {
                        Integer executorId = vo.getExecutorId();
                        String lockKey = Joiner.on(":").join(LOCK_STATISTICS_EMP_BILL, orgId, billDate);
                        String lockVal = String.valueOf(executorId);
                        redisLockBiz.lockedApply(lockKey, lockVal, (t) -> {
                            StatEmpBill entity = new StatEmpBill();
                            entity.setOrgId(orgId);
                            entity.setDentistId(executorId);
                            entity.setBillDate(billDate);
                            entity.setItemType(vo.getItemType());
                            entity.setItemId(vo.getItemId());
                            entity.setQuantity(vo.getQuantity());
                            entity.setReceivableWorkload(vo.getReceivableWorkload());
                            entity.setReceivedWorkload(vo.getReceivedWorkload());
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

    /**
     * 根据条件拉取账单时统计并更新中间表
     *
     * @param form
     * @throws InterruptedException
     */
  /*public void pullBillDateStatistics(PullForm form) throws InterruptedException {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    List<String> dateRanges = DateUtil.sliceUpDateRange(startDate, endDate);
    if (StringHelper.isNotEmpty(dateRanges)) {
      CountDownLatch latch = new CountDownLatch(dateRanges.size());
      List<Future> resultFutures = new ArrayList<>();
      for (String date : dateRanges) {
        resultFutures.add(
                importExcelThreadPool.submit(
                        () -> {
                          try {
                            Example example = new Example(BaseBill.class);
                            example
                                    .createCriteria()
                                    .andCondition("org_id=", 26)
                                    .andCondition(
                                            "bill_date >= '" + new DateTime(date).toString("yyyy-MM-dd") + "'")
                                    .andCondition(
                                            "bill_date < '"
                                                    + new DateTime(date).plusDays(1).toString("yyyy-MM-dd")
                                                    + "'");
                            List<BaseBill> baseBills = mapper.selectByExample(example);
                            if (StringHelper.isNotEmpty(baseBills)) {
                              baseBills.forEach(vo-> statisticsInBillDate(vo, null));
                            }
                          } finally {
                            latch.countDown();
                          }
                        }));
      }
      latch.await();
      BaseTreatmentProcessBiz.printExceptionLog(resultFutures, log);
    }
  }*/

    /**
     * 根据条件拉取账单时统计并更新中间表
     *
     * @param form
     * @throws InterruptedException
     */
    public void pullBillDateStatistics(PullForm form) throws InterruptedException {
        Date now = new Date(System.currentTimeMillis());
        String startDate = form.getStartDate();
        String endDate = form.getEndDate();
        List<BillExecutorItemVO> details = baseBillDetailMapper.groupBillItemDetailListByBillDate(startDate, endDate);
        if (StringHelper.isNotEmpty(details)) {
            List<StatEmpBill> datas = new ArrayList<>();
            details.forEach(vo -> {
                StatEmpBill entity = new StatEmpBill();
                entity.setOrgId(vo.getOrgId());
                entity.setDentistId(vo.getExecutorId());
                entity.setReceivableWorkload(vo.getReceivableWorkload());
                entity.setReceivedWorkload(vo.getReceivedWorkload());
                entity.setItemType(vo.getItemType());
                entity.setItemId(vo.getItemId());
                entity.setQuantity(vo.getQuantity());
                entity.setBillDate(vo.getBillDate());
                entity.setCrtId(vo.getExecutorId());
                entity.setCrtTime(now);
                datas.add(entity);
            });
            List<Future> resultFutures = new ArrayList<>();
            List<List<StatEmpBill>> partition = Lists.partition(datas, 1000);
            CountDownLatch latch = new CountDownLatch(partition.size());
            partition.forEach(vo -> resultFutures.add(
                    importExcelThreadPool.submit(() -> {
                        try {
                            insertBatch(vo);
                        } finally {
                            latch.countDown();
                        }
                    })
            ));
            latch.await();
            BaseTreatmentProcessBiz.printExceptionLog(resultFutures, log);
        }
    }

    public void insertBatch(List<StatEmpBill> datas) {
        mapper.insertBatch(datas);
    }
}
