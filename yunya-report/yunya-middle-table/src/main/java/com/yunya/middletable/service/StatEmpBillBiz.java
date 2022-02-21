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
import tk.mybatis.mapper.entity.Example;

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
     * 账单生成时统计执行人的账单数据
     *
     * @param orderDetails
     * @param bill
     */
    public void statisticsEmployeeByBillDate(List<OrderDetail> orderDetails, BaseBill bill) {
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
                        billDate, null, null, executorIds);
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
    public void pullBillDateStatistics(PullForm form) throws InterruptedException {
        Date now = new Date(System.currentTimeMillis());
        String startDate = form.getStartDate();
        String endDate = form.getEndDate();
        deleteData(startDate, endDate);
        List<BillExecutorItemVO> details = baseBillDetailMapper.groupBillItemDetailListByDate(startDate, endDate, 0);
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

    /**
     * 清掉旧数据
     *
     * @param startDate
     * @param endDate
     */
    private void deleteData(String startDate, String endDate) {
        Example example = new Example(StatEmpBill.class);
        Example.Criteria c = example.createCriteria();
        Integer sDateInt = Integer.parseInt(StringHelper.remove(startDate,"-"));
        Integer eDateInt = Integer.parseInt(StringHelper.remove(endDate,"-"));
        c.andBetween("billDate", sDateInt, eDateInt);
        mapper.deleteByExample(example);
    }

    /**
     * 批量新增
     *
     * @param datas
     */
    public void insertBatch(List<StatEmpBill> datas) {
        mapper.insertBatch(datas);
    }
}
