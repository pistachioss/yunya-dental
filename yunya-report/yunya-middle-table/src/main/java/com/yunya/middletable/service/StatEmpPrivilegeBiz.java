package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.StatEmpPrivilegeMapper;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.StatEmpBill;
import com.yunya.models.report.StatEmpPrivilege;
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
public class StatEmpPrivilegeBiz extends BaseBiz<StatEmpPrivilegeMapper, StatEmpPrivilege> {
    @Autowired private RedisLockBiz redisLockBiz;
    @Autowired private StatEmpPayBiz statEmpPayBiz;
    /** 账单明细*/
    @Autowired private BaseBillDetailMapper baseBillDetailMapper;
    /** 多线程 */
    @Resource(name = "customizeThreadPool")
    private ExecutorService importExcelThreadPool;

    /**
     * 账单使用优惠时统计执行人的账单数据
     *
     * @param orderDetails
     * @param bill
     */
    public void statisticsEmployeeByPrivilegeDate(List<OrderDetail> orderDetails, BaseBill bill) {
        Integer orgId = bill.getOrgId();
        Date date = new Date(System.currentTimeMillis());
        Integer privilegeDate = DateUtil.date2Number(bill.getPrivilegeDate());
        if (StringHelper.isNotEmpty(orderDetails)) {
            Set<Integer> executorIds = new HashSet<>();
            Set<String> keys = new HashSet<>();
            orderDetails.forEach(vo->{
                Integer executorId = vo.getExecutorId();
                StatEmpBill entity = new StatEmpBill();
                entity.setOrgId(orgId);
                entity.setDentistId(executorId);
                entity.setBillDate(privilegeDate);
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
                        null, null, privilegeDate, executorIds);
                details = statEmpPayBiz.statisticsExecutorItem(details, keys, (vo)-> vo.getExecutorId() + "," + vo.getItemType() + "," + vo.getItemId());
                if (StringHelper.isNotEmpty(details)) {
                    Integer crtId = bill.getBillerId();
                    details.forEach(vo -> {
                        Integer executorId = vo.getExecutorId();
                        String lockKey = Joiner.on(":").join(LOCK_STATISTICS_EMP_BILL, orgId, privilegeDate);
                        String lockVal = String.valueOf(executorId);
                        redisLockBiz.lockedApply(lockKey, lockVal, (t) -> {
                            StatEmpPrivilege entity = new StatEmpPrivilege();
                            entity.setOrgId(orgId);
                            entity.setDentistId(executorId);
                            entity.setPrivilegeDate(privilegeDate);
                            entity.setItemType(vo.getItemType());
                            entity.setItemId(vo.getItemId());
                            entity.setCouponWorkload(vo.getCouponWorkload());
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
     * 根据条件拉取账单使用优惠时统计并更新中间表
     *
     * @param form
     * @throws InterruptedException
     */
    public void pullPrivilegeDateStatistics(PullForm form) throws InterruptedException {
        Date now = new Date(System.currentTimeMillis());
        String startDate = form.getStartDate();
        String endDate = form.getEndDate();
        deleteData(startDate, endDate);
        List<BillExecutorItemVO> details = baseBillDetailMapper.groupBillItemDetailListByDate(startDate, endDate, 1);
        if (StringHelper.isNotEmpty(details)) {
            List<StatEmpPrivilege> datas = new ArrayList<>();
            details.forEach(vo -> {
                StatEmpPrivilege entity = new StatEmpPrivilege();
                entity.setOrgId(vo.getOrgId());
                entity.setDentistId(vo.getExecutorId());
                entity.setCouponWorkload(vo.getCouponWorkload());
                entity.setItemType(vo.getItemType());
                entity.setItemId(vo.getItemId());
                entity.setPrivilegeDate(vo.getBillDate());
                entity.setCrtId(vo.getExecutorId());
                entity.setCrtTime(now);
                datas.add(entity);
            });
            List<Future> resultFutures = new ArrayList<>();
            List<List<StatEmpPrivilege>> partition = Lists.partition(datas, 1000);
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
        Example example = new Example(StatEmpPrivilege.class);
        Example.Criteria c = example.createCriteria();
        Integer sDateInt = Integer.parseInt(StringHelper.remove(startDate,"-"));
        Integer eDateInt = Integer.parseInt(StringHelper.remove(endDate,"-"));
        c.andBetween("privilegeDate", sDateInt, eDateInt);
        mapper.deleteByExample(example);
    }

    /**
     * 批量新增
     *
     * @param datas
     */
    public void insertBatch(List<StatEmpPrivilege> datas) {
        mapper.insertBatch(datas);
    }
}
