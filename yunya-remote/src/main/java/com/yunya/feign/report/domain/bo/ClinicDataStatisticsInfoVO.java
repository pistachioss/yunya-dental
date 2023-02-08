package com.yunya.feign.report.domain.bo;

import com.yunya.feign.report.domain.vo.*;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 门诊数据总览组合信息VO
 *
 * @author: chow
 * @date: 2020/12/8 14:02
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊数据总览组合信息VO")
@Data
@ToString
public class ClinicDataStatisticsInfoVO implements Serializable {
  /** 患者数据统计 */
  private PatientDataStatisticsVO patientDataStatistics;
  /** 账单数据统计 */
  private BillDataStatisticsVO billDataStatistics;
  /** 收费数据统计 */
  private TollDataStatisticsVO tollDataStatistics;
  /** 工作量 */
  private WorkloadStatisticsVO workloadStatistic;
  /** 会员数据 */
  private MemberDataStatisticVO memberDataStatistic;
  /** 预付款数据 */
  private PrepaymentsDataStatisticVO prepaymentsDataStatistic;
  /** 正畸预付款数据 */
  private PrepaymentsDataStatisticVO prepaymentsDataStatistic2;
  /** 美白预付款数据 */
  private PrepaymentsDataStatisticVO prepaymentsDataStatistic3;
}
