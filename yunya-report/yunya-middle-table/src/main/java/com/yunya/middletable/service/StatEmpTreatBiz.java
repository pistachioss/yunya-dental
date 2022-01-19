package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseTreatmentProcessMapper;
import com.yunya.middletable.dao.report.StatEmpTreatMapper;
import com.yunya.models.report.BaseTreatmentProcess;
import com.yunya.models.report.StatEmpTreat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.yunya.framework.common.constant.RedisConstants.LOCK_STATISTICS_EMP_TREAT;
import static com.yunya.middletable.service.BaseTreatmentProcessBiz.printExceptionLog;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/12/13 14:36
 * @since: 1.0.0
 */
@Slf4j
@Service
public class StatEmpTreatBiz extends BaseBiz<StatEmpTreatMapper, StatEmpTreat> {

    @Autowired private RedisLockBiz redisLockBiz;
    /*就诊流程*/
    @Autowired private BaseTreatmentProcessMapper baseTreatmentProcessMapper;
    /** 多线程 */
    @Resource(name = "customizeThreadPool")
    private ExecutorService importExcelThreadPool;

    /**
     * 就诊完成时统计就诊相关数据
     *
     * @param treatmentProcess
     */
    public void statisticsEmployeeByTreatDate(BaseTreatmentProcess treatmentProcess) {
        Integer orgId = treatmentProcess.getOrgId();
        Integer dentistId = treatmentProcess.getRegisteredDentistId();
        Integer treatDate = DateUtil.date2Number(treatmentProcess.getTreatEndTime());
        StatEmpTreat entity = new StatEmpTreat();
        entity.setOrgId(orgId);
        entity.setDentistId(dentistId);
        entity.setTreatDate(treatDate);
        String lockKey = Joiner.on(":").join(LOCK_STATISTICS_EMP_TREAT, orgId, dentistId);
        String lockVal = String.valueOf(treatDate);
        redisLockBiz.lockedApply(lockKey, lockVal, (t)->{
            mapper.deleteByPrimaryKey(entity);
            StatEmpTreat statEmpTreat = baseTreatmentProcessMapper.countTreatNumByDateAndDentist(orgId, dentistId, treatDate);
            if (!ObjectUtils.isEmpty(statEmpTreat)) {
                statEmpTreat.setCrtId(treatmentProcess.getRegisteredDentistId());
                statEmpTreat.setCrtTime(new Date(System.currentTimeMillis()));
                mapper.insertSelective(statEmpTreat);
            }
            return null;
        });
    }

    /**
     * 批量拉取
     *
     * @param form
     * @throws InterruptedException
     */
    public void pullTreatDateStatistics(PullForm form) throws InterruptedException {
        String startDate = form.getStartDate();
        String endDate = form.getEndDate();
        deleteData(startDate, endDate);
        List<StatEmpTreat> datas = baseTreatmentProcessMapper.countTreatNumByDate(startDate, endDate);
        if (StringHelper.isNotEmpty(datas)) {
            List<List<StatEmpTreat>> partition = Lists.partition(datas, 1000);
            CountDownLatch latch = new CountDownLatch(partition.size());
            List<Future> resultFutures = new ArrayList<>();
            partition.forEach(list-> resultFutures.add(
                importExcelThreadPool.submit(
                    () -> {
                        try {
                            insertBatch(list);
                        } finally {
                            latch.countDown();
                        }
                    })));
            latch.await();
            printExceptionLog(resultFutures, log);
        }
    }

    /**
     * 清掉旧数据
     *
     * @param startDate
     * @param endDate
     */
    private void deleteData(String startDate, String endDate) {
        Example example = new Example(StatEmpTreat.class);
        Example.Criteria c = example.createCriteria();
        Integer sDateInt = Integer.parseInt(StringHelper.remove(startDate,"-"));
        Integer eDateInt = Integer.parseInt(StringHelper.remove(endDate,"-"));
        c.andBetween("treatDate", sDateInt, eDateInt);
        mapper.deleteByExample(example);
    }

    /**
     * 批量新增
     *
     * @param list
     */
    private void insertBatch(List<StatEmpTreat> list) {
        mapper.insertBatch(list);
    }
}
