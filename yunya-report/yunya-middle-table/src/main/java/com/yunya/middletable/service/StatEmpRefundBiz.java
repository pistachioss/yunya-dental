package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.query.StatisticsEmployeeQueryForm;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.StatEmpRefundMapper;
import com.yunya.middletable.dao.treatment.OrderDetailMapper;
import com.yunya.models.report.BaseRefund;
import com.yunya.models.report.StatEmpRefund;
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
public class StatEmpRefundBiz extends BaseBiz<StatEmpRefundMapper, StatEmpRefund> {
    @Autowired private RedisLockBiz redisLockBiz;
    /** 账单退费*/
    @Autowired private BaseRefundBiz baseRefundBiz;
    /** 订单详情*/
    @Autowired private OrderDetailMapper orderDetailMapper;
    /** 多线程 */
    @Resource(name = "customizeThreadPool")
    private ExecutorService importExcelThreadPool;

    /**
     * 账单退费时统计执行人的账单相关数据
     *
     * @param refund
     */
    public void statisticsEmployeeByRefundDate(BaseRefund refund) {
        OrderDetail detailQuery = new OrderDetail();
        detailQuery.setOrderRecordId(refund.getBillId());
        List<OrderDetail> orderDetails = orderDetailMapper.select(detailQuery);
        Integer orgId = refund.getOrgId();
        Date date = new Date(System.currentTimeMillis());
        Integer refundDate = DateUtil.date2Number(refund.getRefundDate());
        if (StringHelper.isNotEmpty(orderDetails)) {
            Set<String> keys = new HashSet<>();
            Set<Integer> executorIds = new HashSet<>();
            orderDetails.forEach(vo->{
                Integer executorId = vo.getExecutorId();
                StatEmpRefund entity = new StatEmpRefund();
                entity.setOrgId(orgId);
                entity.setDentistId(executorId);
                entity.setRefundDate(refundDate);
                entity.setItemType(vo.getType());
                entity.setItemId(vo.getBillingItemId());
                mapper.deleteByPrimaryKey(entity);
                if (vo.getInservice() && !ObjectUtils.isEmpty(executorId)) {
                    executorIds.add(executorId);
                }
                keys.add(executorId + "," + vo.getType() + "," + vo.getBillingItemId());
            });
            if (StringHelper.isNotEmpty(executorIds)) {
                StatisticsEmployeeQueryForm query = new StatisticsEmployeeQueryForm();
                query.setDentistIds(executorIds);
                query.setOrgId(orgId);
                query.setSDateInt(refundDate);
                query.setEDateInt(refundDate);
                List<BillExecutorItemVO> details = baseRefundBiz.findBillItemRefundListByDate(query);
                if (StringHelper.isNotEmpty(details)) {
                    Integer userId = refund.getRefundOperatorId();
                    details.forEach(vo -> {
                        Integer executorId = vo.getExecutorId();
                        String lockKey = Joiner.on(":").join(LOCK_STATISTICS_EMP_PAY, orgId, refundDate);
                        String lockVal = String.valueOf(executorId);
                        redisLockBiz.lockedApply(lockKey, lockVal, (t) -> {
                            StatEmpRefund entity = new StatEmpRefund();
                            entity.setOrgId(orgId);
                            entity.setDentistId(executorId);
                            entity.setRefundDate(refundDate);
                            entity.setItemType(vo.getItemType());
                            entity.setItemId(vo.getItemId());
                            entity.setRefundWorkload(vo.getRefundWorkload());
                            entity.setCrtId(userId);
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
     * 批量拉取
     *
     * @param form
     * @throws InterruptedException
     */
    public void pullRefundDateStatistics(PullForm form) throws InterruptedException {
        Date now = new Date(System.currentTimeMillis());
        String startDate = form.getStartDate();
        String endDate = form.getEndDate();
        deleteData(startDate, endDate);
        StatisticsEmployeeQueryForm query = new StatisticsEmployeeQueryForm();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        List<BillExecutorItemVO> details = baseRefundBiz.findBillItemRefundListByDate(query);
        if (StringHelper.isNotEmpty(details)) {
            List<StatEmpRefund> datas = new ArrayList<>();
            details.forEach(vo->{
                Integer executorId = vo.getExecutorId();
                StatEmpRefund entity = new StatEmpRefund();
                entity.setOrgId(vo.getOrgId());
                entity.setDentistId(executorId);
                entity.setRefundDate(vo.getBillDate());
                entity.setItemType(vo.getItemType());
                entity.setItemId(vo.getItemId());
                entity.setRefundWorkload(vo.getRefundWorkload());
                entity.setCrtId(vo.getExecutorId());
                entity.setCrtTime(now);
                datas.add(entity);
            });
            List<Future> resultFutures = new ArrayList<>();
            List<List<StatEmpRefund>> partition = Lists.partition(datas, 1000);
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

    /**
     * 清掉旧数据
     *
     * @param startDate
     * @param endDate
     */
    private void deleteData(String startDate, String endDate) {
        Example example = new Example(StatEmpRefund.class);
        Example.Criteria c = example.createCriteria();
        Integer sDateInt = Integer.parseInt(StringHelper.remove(startDate,"-"));
        Integer eDateInt = Integer.parseInt(StringHelper.remove(endDate,"-"));
        c.andBetween("refundDate", sDateInt, eDateInt);
        mapper.deleteByExample(example);
    }

    /**
     * 批量新增
     *
     * @param datas
     */
    private void insertBatch(List<StatEmpRefund> datas) {
        mapper.insertBatch(datas);
    }
}
