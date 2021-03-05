package com.yunya.report.ultimate.biz;

import com.yunya.feign.clinic_base.RemoteClinicBaseServiceFeign;
import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessGoalCompletedInfoVO;
import com.yunya.feign.report.domain.bo.ClinicDataStatisticsInfoVO;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.OperationDataComplexQuery;
import com.yunya.feign.report.domain.query.PatientFirstTreatOriginQuery;
import com.yunya.feign.report.domain.query.VisitAndRemindCompletedInfoQuery;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.utils.StringHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
  @Resource private BaseOrganizationBiz organizationBiz;
  /** 账单 */
  @Resource private BaseBillBiz billBiz;
  /** 账单收费 */
  @Resource private BaseBillPayBiz billPayBiz;
  /** 账单明细 */
  @Resource private BaseBillDetailBiz billDetailBiz;
  /** 会员信息 */
  @Resource private BasePatientMemberBiz memberBiz;
  /** 就诊 */
  @Resource private BaseTreatmentProcessBiz treatmentProcessBiz;
  /** 随访提醒 */
  @Resource private BaseVisitRemindBiz visitRemindBiz;
  /** 诊所基础信息 */
  @Resource private RemoteClinicBaseServiceFeign clinicBaseServiceFeign;

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
    clinicWorkloadStatistic.setTotalFreePaymentAmount(clinicBillDataStatistic.getTotalFreePaymentAmount());
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

  /**
   * 根据条件查询门诊运营综合数据
   *
   * @param query 查询条件
   * @return Map<String, Object>
   */
  public List<OperationDataComplexInfoVO> findOperationDataComplexInfo(
      OperationDataComplexQuery query) {
    Integer orgId = query.getOrgId();
    String startDate = query.getStartDate();
    String endDate = query.getEndDate();
    List<OperationDataComplexInfoVO> resultList = new ArrayList<>();

    BusinessGoalCompletedInfoQuery businessGoalQuery = new BusinessGoalCompletedInfoQuery();
    businessGoalQuery.setBusinessType((byte) 1);
    businessGoalQuery.setOrgId(orgId);
    businessGoalQuery.setStartDate(startDate);
    businessGoalQuery.setEndDate(endDate);
    BusinessGoalCompletedInfoVO workloadCompleted =
        clinicBaseServiceFeign.businessGoalCompletedInfo(businessGoalQuery);
    OperationDataComplexInfoVO workload = new OperationDataComplexInfoVO();
    workload.setComplexInfoName("工作量目标完成率");
    workload.setGoalCount(String.format("%s", workloadCompleted.getBusinessGoalCount()));
    workload.setCompletedCount(String.format("%s", workloadCompleted.getBusinessCompletedCount()));
    workload.setCompletedPercentage(workloadCompleted.getBusinessCompletedPercentage());
    resultList.add(0, workload);

    businessGoalQuery.setBusinessType((byte) 2);
    BusinessGoalCompletedInfoVO firstTreatCompleted =
        clinicBaseServiceFeign.businessGoalCompletedInfo(businessGoalQuery);
    OperationDataComplexInfoVO firstTreat = new OperationDataComplexInfoVO();
    firstTreat.setComplexInfoName("初诊人数目标完成率");
    firstTreat.setGoalCount(String.format("%s", firstTreatCompleted.getBusinessGoalCount()));
    firstTreat.setCompletedCount(
        String.format("%s", firstTreatCompleted.getBusinessCompletedCount()));
    firstTreat.setCompletedPercentage(firstTreatCompleted.getBusinessCompletedPercentage());
    resultList.add(1, firstTreat);

    PatientFirstTreatOriginQuery patientTreatOriginQuery = new PatientFirstTreatOriginQuery();
    patientTreatOriginQuery.setOrgId(orgId);
    patientTreatOriginQuery.setStartDate(startDate);
    patientTreatOriginQuery.setEndDate(endDate);
    PatientFirstTreatOriginInfoVO treatOriginInfo =
        treatmentProcessBiz.findPatientFirstTreatOriginInfo(patientTreatOriginQuery);
    OperationDataComplexInfoVO firstPatient = new OperationDataComplexInfoVO();
    firstPatient.setCompletedCount("0");
    firstPatient.setCompletedPercentage(new BigDecimal("0.00"));
    firstPatient.setComplexInfoName("老患者介绍率");
    Integer firstTreatTotalCount = treatOriginInfo.getFirstTreatTotalCount();
    if (null != firstTreatTotalCount) {
      firstPatient.setGoalCount(String.format("%d", firstTreatTotalCount));
    }
    List<PatientFirstTreatOriginVO> treatOrigins = treatOriginInfo.getPatientFirstTreatOrigins();
    if (StringHelper.isNotEmpty(treatOrigins)) {
      for (PatientFirstTreatOriginVO treatOrigin : treatOrigins) {
        if ("老患者介绍".equals(treatOrigin.getPatientOriginTypeName())) {
          firstPatient.setCompletedCount(String.format("%d", treatOrigin.getFirstTreatCount()));
          firstPatient.setCompletedPercentage(treatOrigin.getFirstTreatPercentage());
        }
      }
    }
    resultList.add(2, firstPatient);

    VisitAndRemindCompletedInfoQuery visitQuery = new VisitAndRemindCompletedInfoQuery();
    visitQuery.setBusinessType((byte) 0);
    visitQuery.setOrgId(orgId);
    visitQuery.setStartDate(startDate);
    visitQuery.setEndDate(endDate);
    VisitAndRemindCompletedInfoVO visitCompletedInfo =
        visitRemindBiz.findVisitAndRemindCompletedInfo(visitQuery);
    OperationDataComplexInfoVO visit = new OperationDataComplexInfoVO();
    visit.setComplexInfoName("随访完成率");
    visit.setGoalCount(visitCompletedInfo.getWaitingForCompletedCount().toString());
    visit.setCompletedCount(visitCompletedInfo.getCompletedCount().toString());
    visit.setCompletedPercentage(visitCompletedInfo.getCompletedPercentage());
    resultList.add(3, visit);

    visitQuery.setBusinessType((byte) 1);
    VisitAndRemindCompletedInfoVO remindCompletedInfo =
        visitRemindBiz.findVisitAndRemindCompletedInfo(visitQuery);
    OperationDataComplexInfoVO remind = new OperationDataComplexInfoVO();
    remind.setComplexInfoName("提醒完成率");
    remind.setGoalCount(remindCompletedInfo.getWaitingForCompletedCount().toString());
    remind.setCompletedCount(remindCompletedInfo.getCompletedCount().toString());
    remind.setCompletedPercentage(remindCompletedInfo.getCompletedPercentage());
    resultList.add(4, remind);
    return resultList;
  }
}
