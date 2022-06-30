package com.yunya.middletable.task;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.middletable.service.*;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/6/30 10:44
 * @since: 1.0.0
 */
@Slf4j
@Component
@EnableScheduling
public class AsyncStatisticsEmployeeExecutBill {

    @Autowired
    private StatEmpTreatBiz statEmpTreatBiz;
    @Autowired
    private StatEmpBillBiz statEmpBillBiz;
    @Autowired
    private StatEmpPrivilegeBiz statEmpPrivilegeBiz;
    @Autowired
    private StatEmpPayBiz statEmpPayBiz;
    @Autowired
    private StatEmpRefundBiz statEmpRefundBiz;

    /**
     * 每天凌晨1点执行统计
     *     统计员工操作（或执行）相关的就诊、账单、收费、免单、优惠、退款
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void yesterdayStatistics() {
        log.info("开始统计员工操作（或执行）相关的就诊、账单、收费、免单、优惠、退款");
        String yesterday = DateUtil.format(DateUtil.yesterday());
        PullForm form = new PullForm();
        form.setStartDate(yesterday);
        form.setEndDate(yesterday);
        try {
            statEmpTreatBiz.pullTreatDateStatistics(form);
            statEmpBillBiz.pullBillDateStatistics(form);
            statEmpPayBiz.pullPayDateStatistics(form);
            statEmpPrivilegeBiz.pullPrivilegeDateStatistics(form);
            statEmpRefundBiz.pullRefundDateStatistics(form);
        } catch (InterruptedException e) {
            log.error("AsyncStatisticsEmployeeExecutBill Error:{}", e);
        }
        log.info("员工统计结束！");
    }
}
