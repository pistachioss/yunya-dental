package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.bo.ClinicDataStatisticsInfoVO;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.PatientFirstTreatOriginQuery;
import com.yunya.feign.report.domain.query.VisitAndRemindCompletedInfoQuery;
import com.yunya.feign.report.domain.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 简介: 门诊数据统计业务层
 *
 * @author: chow
 * @date: 2020/12/8 14:00
 * @description:
 * @since: 1.0.0
 */
@Service
public class ClinicDataStatisticsBiz {

  /** 组织 */
  @Autowired private BaseOrganizationBiz organizationBiz;
  /** 账单 */
  @Autowired private BaseBillBiz billBiz;
  /** 账单收费 */
  @Autowired private BaseBillPayBiz billPayBiz;
  /** 账单明细 */
  @Autowired private BaseBillDetailBiz billDetailBiz;
  /** 会员信息 */
  @Autowired private BasePatientMemberBiz memberBiz;
  /** 就诊 */
  @Autowired private BaseTreatmentProcessBiz treatmentProcessBiz;
  /** 随访提醒 */
  @Autowired private BaseVisitRemindBiz visitRemindBiz;

  /**
   * 根据条件查询门诊数据统计
   *
   * @param query 查询条件
   * @return ClinicDataStatisticsInfoVO
   */
  public ClinicDataStatisticsInfoVO findClinicDataStatisticsInfo(DataStatisticsQuery query) {
    ClinicDataStatisticsInfoVO resultData = new ClinicDataStatisticsInfoVO();
    // 门诊患者数据总览
    PatientDataStatisticsVO clinicPatientDataStatistic =
        organizationBiz.findClinicPatientDataStatistic(query);
    resultData.setPatientDataStatistics(clinicPatientDataStatistic);
    // 门诊账单数据总览
    BillDataStatisticsVO clinicBillDataStatistic = billBiz.findClinicBillDataStatistic(query);
    resultData.setBillDataStatistics(clinicBillDataStatistic);
    // 门诊收费数据总览
    TollDataStatisticsVO clinicTollDataStatistic = billPayBiz.findClinicTollDataStatistic(query);
    resultData.setTollDataStatistics(clinicTollDataStatistic);
    // 门诊工作量总览
    WorkloadStatisticsVO clinicWorkloadStatistic = billDetailBiz.findClinicWorkloadStatistic(query);
    resultData.setWorkloadStatistic(clinicWorkloadStatistic);
    // 门诊会员数据总览
    MemberDataStatisticVO clinicMemberDataStatistic =
        memberBiz.findClinicMemberDataStatistic(query);
    resultData.setMemberDataStatistic(clinicMemberDataStatistic);
    // 门诊预付款数据总览
    PrepaymentsDataStatisticVO clinicPrepaymentsDataStatistic =
        memberBiz.findClinicPrepaymentsDataStatistic(query);
    resultData.setPrepaymentsDataStatistic(clinicPrepaymentsDataStatistic);
    return resultData;
  }

  /**
   * 根据条件查询门诊患者就诊数据
   *
   * @param query 查询条件
   * @return PatientDataStatisticsVO
   */
  public PatientDataStatisticsVO findClinicPatientDataStatisticsInfo(DataStatisticsQuery query) {
    return organizationBiz.findClinicPatientDataStatistic(query);
  }

  /**
   * 根据条件查询初诊患者来源分布信息
   *
   * @param query 查询条件
   * @return PatientFirstTreatOriginInfoVO
   */
  public PatientFirstTreatOriginInfoVO findPatientFirstTreatOriginInfo(
      PatientFirstTreatOriginQuery query) {
    return treatmentProcessBiz.findPatientFirstTreatOriginInfo(query);
  }

  /**
   * 根据条件查询随访或随访完成信息
   *
   * @param query 查询条件
   * @return VisitAndRemindCompletedInfoVO
   */
  public VisitAndRemindCompletedInfoVO findVisitAndRemindCompletedInfo(
      VisitAndRemindCompletedInfoQuery query) {
    return visitRemindBiz.findVisitAndRemindCompletedInfo(query);
  }
}
