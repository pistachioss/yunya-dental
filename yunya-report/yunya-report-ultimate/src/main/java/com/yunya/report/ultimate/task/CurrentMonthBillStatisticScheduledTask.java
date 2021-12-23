package com.yunya.report.ultimate.task;

import com.yunya.feign.report.domain.query.FirstVisitDetailQuery;
import com.yunya.feign.report.domain.query.StatementStatisticQuery;
import com.yunya.feign.report.domain.vo.CurrentMonthBillStatisticVO;
import com.yunya.feign.report.domain.vo.FirstVisitDetailVO;
import com.yunya.feign.treatment.domain.vo.BasePeizhenCentreVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.mapper.*;
import com.yunya.models.report.CurrentMonthBillStatistics;
import com.yunya.report.ultimate.model.BasePeizhenCentre;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
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
  /** 收费 */
  @Autowired private BaseBillPayMapper billPayMapper;
  /** 当前月账单统计 */
  @Autowired private CurrentMonthBillStatisticsMapper billStatisticsMapper;
  /** 初诊明细 */
  @Autowired private BaseFirstVisitDetailMapper baseFirstVisitDetailMapper;
  /** 初诊明细 */
  @Autowired private BaseTreatmentProcessMapper baseTreatmentProcessMapper;
  /** 配诊统计*/
  @Autowired private BasePeizhenCentreMapper basePeizhenCentreMapper;




  /** 每月最后一天23:59分存档当月账单信息（本月实收合计、本月优惠合计、本月免单合计、本月账单已收合计、本月账单欠费合计） */
  @Scheduled(cron = "0 59 23 28-31 * ?")
  public void execute() {
    final Calendar c = Calendar.getInstance();
    if (c.get(Calendar.DATE) == c.getActualMaximum(Calendar.DATE)) {
      BaseOrganization org = new BaseOrganization();
      org.setOrgType((byte) 2);
      List<BaseOrganization> organizations = organizationMapper.select(org);
      if (StringHelper.isNotEmpty(organizations)) {
        for (BaseOrganization organization : organizations) {
          Integer orgId = organization.getOrgId();
          StatementStatisticQuery query = new StatementStatisticQuery();
          query.setOrgId(orgId);
          String currentMonth = new DateTime().toString("yyyy-MM");
          query.setQueryDate(currentMonth);
          CurrentMonthBillStatisticVO statisticVO = billMapper.selectRealBillStatistic(query);
          if (null != statisticVO) {
            BigDecimal totalFreePayAmount =
                billPayMapper.selectCurrentMonthTotalFreePayAmount(query);
            CurrentMonthBillStatistics billStatistics = new CurrentMonthBillStatistics();
            billStatistics.setOrgId(statisticVO.getOrgId());
            billStatistics.setCurrentMonth(new DateTime(statisticVO.getCurrentMonth()).toDate());
            billStatistics.setActualReceivableAmount(
                statisticVO.getCurrentMonthTotalActualAmount());
            billStatistics.setPrivelegeAmount(statisticVO.getCurrentMonthTotalDiscountAmount());
            billStatistics.setReceivedAmount(statisticVO.getCurrentMonthTotalReceivedAmount());
            billStatistics.setFreePayAmount(totalFreePayAmount);
            billStatistics.setDebtAmount(statisticVO.getCurrentMonthTotalDebtAmount());
            billStatisticsMapper.insertSelective(billStatistics);
          }
        }
      }
    }
  }
  /** 每天23:59分存档当天之前的初诊信息*/
  @Scheduled(cron = "0 0 23 * * ?")
  public void firstVisitdetail() {
    List<FirstVisitDetailVO>list =  baseTreatmentProcessMapper.findFirstVisitRecordDetail();
    int size = list.size();
    int temp = size / 5000 + 1;
    boolean result = size % 5000 == 0;
    List<List<FirstVisitDetailVO>> subList = new ArrayList<>();
    for (int i = 0; i < temp; i++) {
      if (i == temp - 1) {
        if (result) {
          break;
        }
        subList.add(list.subList(5000 * i, size)) ;
      } else {
        subList.add(list.subList(5000 * i, 5000 * (i + 1))) ;
      }
    }
    baseFirstVisitDetailMapper.deleteAll();
    for(List<FirstVisitDetailVO>relist:subList){
      baseFirstVisitDetailMapper.batchIntert(relist);
    }
  }

  /** 每天23:59分存档当天之前的配诊统计信息*/
  @Scheduled(cron = "0 0 23 * * ?")
  public void peizhenVisitdetail() {
    List<BasePeizhenCentreVO>list =  basePeizhenCentreMapper.findFirstVisitRecordDetail();
    int size = list.size();
    int temp = size / 5000 + 1;
    boolean result = size % 5000 == 0;
    List<List<BasePeizhenCentreVO>> subList = new ArrayList<>();
    for (int i = 0; i < temp; i++) {
      if (i == temp - 1) {
        if (result) {
          break;
        }
        subList.add(list.subList(5000 * i, size)) ;
      } else {
        subList.add(list.subList(5000 * i, 5000 * (i + 1))) ;
      }
    }
    basePeizhenCentreMapper.deleteAll();
    for(List<BasePeizhenCentreVO>relist:subList){
      basePeizhenCentreMapper.batchIntert(relist);
    }
  }
}
