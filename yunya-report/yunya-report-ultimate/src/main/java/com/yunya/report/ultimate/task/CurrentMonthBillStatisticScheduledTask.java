package com.yunya.report.ultimate.task;

import com.yunya.feign.report.domain.query.StatementStatisticQuery;
import com.yunya.feign.report.domain.vo.CurrentMonthBillStatisticVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.mapper.BaseBillMapper;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import com.yunya.report.ultimate.mapper.CurrentMonthBillStatisticsMapper;
import com.yunya.models.report.CurrentMonthBillStatistics;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * 简介: 当前月账单统计存档定时任务
 *
 * @author: chow
 * @date: 2021/1/5 17:11
 * @description:
 * @since: 1.0.0
 */
@Component
@EnableScheduling
public class CurrentMonthBillStatisticScheduledTask {

  /** 注入日志对象 */
  private final Logger logger =
      LoggerFactory.getLogger(CurrentMonthBillStatisticScheduledTask.class);

  /** 组织 */
  @Autowired private BaseOrganizationMapper organizationMapper;
  /** 账单 */
  @Autowired private BaseBillMapper billMapper;
  /** 当前月账单统计 */
  @Autowired private CurrentMonthBillStatisticsMapper billStatisticsMapper;

  /** 每月最后一天23:59分存档当月账单信息（本月实收合计、本月优惠合计、本月账单已收合计、本月账单欠费合计） */
  @Scheduled(cron = "0 59 23 28-31 * ?")
  public void execute() {
    final Calendar c = Calendar.getInstance();
    if (c.get(Calendar.DATE) == c.getActualMaximum(Calendar.DATE)) {
      List<BaseOrganization> organizations = organizationMapper.selectAll();
      if (StringHelper.isNotEmpty(organizations)) {
        for (BaseOrganization organization : organizations) {
          Integer orgId = organization.getOrgId();
          StatementStatisticQuery query = new StatementStatisticQuery();
          query.setOrgId(orgId);
          String currentMonth = new DateTime().toString("yyyy-MM");
          query.setQueryDate(currentMonth);
          CurrentMonthBillStatisticVO statisticVO = billMapper.selectRealBillStatistic(query);
          if (null != statisticVO) {
            CurrentMonthBillStatistics billStatistics = new CurrentMonthBillStatistics();
            billStatistics.setId(statisticVO.getOrgId());
            billStatistics.setOrgId(statisticVO.getOrgId());
            billStatistics.setCurrentMonth(new DateTime(statisticVO.getCurrentMonth()).toDate());
            billStatistics.setActualReceivableAmount(
                statisticVO.getCurrentMonthTotalActualAmount());
            billStatistics.setPrivelegeAmount(statisticVO.getCurrentMonthTotalDiscountAmount());
            billStatistics.setReceivedAmount(statisticVO.getCurrentMonthTotalReceivedAmount());
            billStatistics.setDebtAmount(statisticVO.getCurrentMonthTotalDebtAmount());
            billStatisticsMapper.insertSelective(billStatistics);
          }
        }
      }
    }
  }
}
