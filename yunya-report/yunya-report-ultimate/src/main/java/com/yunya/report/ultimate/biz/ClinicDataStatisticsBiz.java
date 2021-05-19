package com.yunya.report.ultimate.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.RemoteClinicBaseServiceFeign;
import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessGoalCompletedInfoVO;
import com.yunya.feign.clinic_base.domain.vo.BusinessGoalVO;
import com.yunya.feign.report.domain.bo.ClinicDataStatisticsInfoVO;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.OperationDataComplexQuery;
import com.yunya.feign.report.domain.query.PatientFirstTreatOriginQuery;
import com.yunya.feign.report.domain.query.VisitAndRemindCompletedInfoQuery;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.annation.Excel;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.common.utils.poi.RowStyle;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  /**
   * 根据条件查询门诊患者数据
   *
   * @param query 查询条件
   * @return PatientDataStatisticsVO
   */
  public PageInfo<PatientDataStatisticsVO> findClinicPatientDataList(DataStatisticsQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    return organizationBiz.findClinicPatientDataList(query, true);
  }

  /**
   * 根据条件查询门诊患者数据导出
   *
   * @param query 查询条件
   * @return PatientDataStatisticsVO
   */
  public void ClinicPatientDataExport(DataStatisticsQuery query, HttpServletResponse response)
      throws IOException {
    query.setWhetherPage(false);
    List<PatientDataStatisticsVO> resultList = findClinicPatientDataList(query).getList();
    ExcelUtil<PatientDataStatisticsVO> excelUtil = new ExcelUtil<>(PatientDataStatisticsVO.class);
    String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "患者数据报表");
    excelUtil.exportExcel(response, resultList, "患者数据报表", fileName);
  }

  /**
   * 根据条件查询运营报表的业务目标
   *
   * @param query
   * @return
   */
  public PageInfo<OperationDataBusinessGoalVO> findAnalysisBusinessGoalList(
      DataStatisticsQuery query) {
    // 目标
    Map<Integer, BigDecimal[]> goalMap = getBusinessGoalMap(query);
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<OperationDataBusinessGoalVO> result = billDetailBiz.selectWorkloadCompletedList(query);
    result.forEach(
        vo -> {
          Integer orgId = vo.getOrgId();
          String workload = vo.getWorkload();
          BigDecimal workloadGoal = BigDecimal.ZERO;
          BigDecimal firstVisitGoal = BigDecimal.ZERO;
          BigDecimal[] goals = goalMap.get(orgId);
          if (goals != null) {
            workloadGoal = goals[0];
            firstVisitGoal = goals[1];
          }
          String firstVisit = vo.getFirstVisit(); // 患者总人数或患者完成数
          vo.setWorkload(computeRatio(workload, workloadGoal));
          vo.setFirstVisit(computeRatio(firstVisit, firstVisitGoal));
          vo.setIntroduction(computeRatio(vo.getIntroduction(), Integer.parseInt(firstVisit)));
          vo.setFollowUp(computeRatio(vo.getFollowUp(), vo.getFollowUpTotal()));
          vo.setNotice(computeRatio(vo.getNotice(), vo.getNoticeTotal()));
        });
    return new PageInfo<>(result);
  }

  /**
   * 根据条件查询运营报表的业务目标导出
   *
   * @param query 查询条件
   * @return PageInfo<PatientDataStatisticsVO>
   */
  public void analysisBusinessGoalExport(DataStatisticsQuery query, HttpServletResponse response)
      throws IOException {
    query.setWhetherPage(false);
    List<OperationDataBusinessGoalVO> resultList = findAnalysisBusinessGoalList(query).getList();
    ExcelUtil<OperationDataBusinessGoalVO> excelUtil =
        new ExcelUtil<>(OperationDataBusinessGoalVO.class);
    String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "业务目标报表");
    excelUtil.exportExcel(response, resultList, "业务目标报表", fileName);
  }

  /**
   * 查询业务目标，并按门诊分组
   *
   * @param query
   * @return
   */
  private Map<Integer, BigDecimal[]> getBusinessGoalMap(DataStatisticsQuery query) {
    Map<Integer, BigDecimal[]> goalMap = new HashMap<>(16);
    BusinessGoalCompletedInfoQuery goalQuery = new BusinessGoalCompletedInfoQuery();
    List<String> dateRange = DateUtil.sliceUpDateRange(query.getStartDate(), query.getEndDate());
    goalQuery.setStartDate(query.getStartDate());
    goalQuery.setEndDate(query.getEndDate());
    goalQuery.setBusinessType((byte) 2);
    goalQuery.setOrgId(-1);
    goalQuery.setDateType(query.getDateType());
    goalQuery.setDateRange(dateRange);
    goalQuery.setBusinessTypes(new Byte[] {2, 3});
    goalQuery.setOrgIds(query.getOrgIds());
    List<BusinessGoalVO> goalVOS = clinicBaseServiceFeign.businessGoalList(goalQuery);
    goalVOS.forEach(
        vo -> {
          Integer orgId = vo.getBelongId();
          BigDecimal[] goals = goalMap.get(orgId);
          if (goals == null) {
            goals = new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO};
          }
          if (vo.getBusinessType() == 2) { // 工作量目标
            goals[0] = vo.getBusinessGoal();
          } else if (vo.getBusinessType() == 3) { // 初诊目标
            goals[0] = vo.getBusinessGoal();
          }
          goalMap.put(orgId, goals);
        });
    return goalMap;
  }

  /**
   * 计算并转换
   *
   * @param dividend 被除数
   * @param divisor 除数
   * @return
   */
  public String computeRatio(String dividend, Integer divisor) {
    BigDecimal ratio = BigDecimal.ZERO;
    if (divisor != 0) {
      ratio =
          new BigDecimal(dividend)
              .divide(new BigDecimal(divisor), 2, BigDecimal.ROUND_HALF_UP)
              .multiply(new BigDecimal(100));
    }
    return divisor + "/" + dividend + "/" + ratio + "%";
  }

  /**
   * 计算并转换
   *
   * @param dividendStr 被除数
   * @param divisor 除数
   * @return
   */
  public String computeRatio(String dividendStr, BigDecimal divisor) {
    BigDecimal dividend = new BigDecimal(dividendStr).setScale(2);
    divisor = divisor.setScale(2);
    BigDecimal ratio = BigDecimal.ZERO;
    if (divisor.compareTo(BigDecimal.ZERO) != 0) {
      ratio = dividend.divide(divisor, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
    }
    return divisor + "/" + dividend + "/" + ratio + "%";
  }

  public void exportClinicDataStatisticsInfo(DataStatisticsQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    List<JSONObject> resultList = reorganizeStructure(findClinicDataStatisticsInfo(query));
    ExcelUtil<JSONObject> excelUtil = new ExcelUtil<>(JSONObject.class);
    String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "数据总览导出");
    excelUtil.exportExcelByRow(response, resultList, "数据总览导出", fileName);
  }

  private List<JSONObject> reorganizeStructure(ClinicDataStatisticsInfoVO clinicDataStatisticsInfoVO) {
    List<JSONObject> resultList = new ArrayList<>();
    JSONObject obj0th = new JSONObject(true);
    obj0th.put(RowStyle.IS_BOLD, true);
    obj0th.put(RowStyle.CELL_WiTH, 22);
    obj0th.put("1", "就诊人数");
    obj0th.put("2", "初诊人数");
    obj0th.put("3", "复诊人数");
    obj0th.put("4", "复诊人次");
    obj0th.put("5", "均次消费");
    obj0th.put("6", "人均消费");
    resultList.add(obj0th);
    PatientDataStatisticsVO patientDataStatisticsVO = clinicDataStatisticsInfoVO.getPatientDataStatistics();
    JSONObject obj1th = new JSONObject(true);
    obj1th.put(RowStyle.COLUMN_TYPE, Excel.ColumnType.NUMERIC);
    obj1th.put("1",patientDataStatisticsVO.getTreatPerTimes());
    obj1th.put("2",patientDataStatisticsVO.getFirstVisitPerNum());
    obj1th.put("3",patientDataStatisticsVO.getRepeatVisitsPerNum());
    obj1th.put("4",patientDataStatisticsVO.getRepeatVisitsPerTimes());
    obj1th.put("5",patientDataStatisticsVO.getAverageConsumption());
    obj1th.put("6",patientDataStatisticsVO.getPerCapitaConsumption());
    resultList.add(obj1th);
    resultList.add(createEmptyObj());

    JSONObject obj3th = new JSONObject(true);
    obj3th.put(RowStyle.IS_BOLD, true);
    obj3th.put("1","预约人数");
    obj3th.put("2","预约人次");
    obj3th.put("3","改约人次");
    obj3th.put("4","取消预约人次");
    obj3th.put("5","失约人次");
    obj3th.put("6","就诊人次");
    resultList.add(obj3th);
    JSONObject obj4th = new JSONObject(true);
    obj4th.put(RowStyle.COLUMN_TYPE, Excel.ColumnType.NUMERIC);
    obj4th.put("1",patientDataStatisticsVO.getAppointPerNum());
    obj4th.put("2",patientDataStatisticsVO.getAppointPerTimes());
    obj4th.put("3",patientDataStatisticsVO.getAppointModifyPerTimes());
    obj4th.put("4",patientDataStatisticsVO.getAppointCancelPerTimes());
    obj4th.put("5",patientDataStatisticsVO.getAppointMissedPerTimes());
    obj4th.put("6",patientDataStatisticsVO.getTreatPerTimes());
    resultList.add(obj4th);
    resultList.add(createEmptyObj());

    BillDataStatisticsVO billDataStatistics = clinicDataStatisticsInfoVO.getBillDataStatistics();
    JSONObject obj5th = new JSONObject(true);
    obj5th.put(RowStyle.IS_BOLD, true);
    obj5th.put("1","原价合计");
    obj5th.put("2","优惠金额合计");
    obj5th.put("3","实收金额合计");
    obj5th.put("4","其中含免单支付合计");
    obj5th.put("5","欠费金额合计");
    obj5th.put("6","");
    resultList.add(obj5th);
    JSONObject obj6th = new JSONObject(true);
    obj6th.put(RowStyle.COLUMN_TYPE, Excel.ColumnType.NUMERIC);
    obj6th.put("1",billDataStatistics.getTotalOriginalAmount());
    obj6th.put("2",billDataStatistics.getTotalDiscountAmount());
    obj6th.put("3",billDataStatistics.getTotalActualReceiveAmount());
    obj6th.put("4",billDataStatistics.getTotalFreePaymentAmount());
    obj6th.put("5",billDataStatistics.getTotalDebtAmount());
    obj6th.put("6","");
    resultList.add(obj6th);
    resultList.add(createEmptyObj());

    TollDataStatisticsVO tollDataStatistics = clinicDataStatisticsInfoVO.getTollDataStatistics();
    JSONObject obj7th = new JSONObject(true);
    obj7th.put(RowStyle.IS_BOLD, true);
    obj7th.put("1","收欠费合计");
    obj7th.put("2","实收金额合计");
    obj7th.put("3","账单退费合计");
    obj7th.put("4","门诊代收金额合计");
    obj7th.put("5","门诊被代收金额合计");
    resultList.add(obj7th);
    JSONObject obj8th = new JSONObject(true);
    obj8th.put(RowStyle.COLUMN_TYPE, Excel.ColumnType.NUMERIC);
    obj8th.put("1",tollDataStatistics.getTotalReceivedDebtAmount());
    obj8th.put("2",tollDataStatistics.getTotalReceivedAmount());
    obj8th.put("3",tollDataStatistics.getTotalBillRefundAmount());
    obj8th.put("4",tollDataStatistics.getTotalClinicCollectionAmount());
    obj8th.put("5",tollDataStatistics.getTotalClinicCollectedAmount());
    resultList.add(obj8th);
    resultList.add(createEmptyObj());

    WorkloadStatisticsVO workloadStatistic = clinicDataStatisticsInfoVO.getWorkloadStatistic();
    JSONObject obj9th = new JSONObject(true);
    obj9th.put(RowStyle.IS_BOLD, true);
    obj9th.put("1","门诊实收工作量合计");
    obj9th.put("2","账单退费工作量合计");
    resultList.add(obj9th);
    JSONObject obj10th = new JSONObject(true);
    obj10th.put(RowStyle.COLUMN_TYPE, Excel.ColumnType.NUMERIC);
    obj10th.put("1",workloadStatistic.getTotalClinicActualWorkload());
    obj10th.put("2",workloadStatistic.getTotalBillRefundWorkload());
    resultList.add(obj10th);

    JSONObject obj11th = new JSONObject(true);
    obj11th.put(RowStyle.IS_BOLD, true);
    obj11th.put("1","首次实收工作量合计");
    obj11th.put("2","其中首次含免单支付工作量合计");
    obj11th.put("3","首次补入工作量合计");
    resultList.add(obj11th);
    JSONObject obj12th = new JSONObject(true);
    obj12th.put(RowStyle.COLUMN_TYPE, Excel.ColumnType.NUMERIC);
    obj12th.put("1",workloadStatistic.getTotalReceivedWorkload());
    obj12th.put("2",workloadStatistic.getTotalFreePaymentWorkload());
    obj12th.put("3",workloadStatistic.getTotalClinicCouponWorkload());
    resultList.add(obj12th);

    JSONObject obj13th = new JSONObject(true);
    obj13th.put(RowStyle.IS_BOLD, true);
    obj13th.put("1","（被代收）实收工作量合计");
    obj13th.put("2","（被代收）其中含免单支付工作量合计");
    obj13th.put("3","（被代收）补入工作量合计");
    resultList.add(obj13th);
    JSONObject obj14th = new JSONObject(true);
    obj14th.put(RowStyle.COLUMN_TYPE, Excel.ColumnType.NUMERIC);
    obj14th.put("1",workloadStatistic.getTotalClinicBeCollectedReceivedWorkload());
    obj14th.put("2",workloadStatistic.getTotalClinicBeCollectedFreePaymentWorkload());
    obj14th.put("3",workloadStatistic.getTotalClinicBeCollectedCouponWorkload());
    resultList.add(obj14th);

    JSONObject obj15th = new JSONObject(true);
    obj15th.put(RowStyle.IS_BOLD, true);
    obj15th.put("1","（收欠费）实收工作量合计");
    obj15th.put("2","（收欠费）其中含免单支付工作量合计");
    obj15th.put("3","（收欠费）补入工作量合计");
    resultList.add(obj15th);
    JSONObject obj16th = new JSONObject(true);
    obj16th.put(RowStyle.COLUMN_TYPE, Excel.ColumnType.NUMERIC);
    obj16th.put("1",workloadStatistic.getTotalClinicArrearsReceivedWorkload());
    obj16th.put("2",workloadStatistic.getTotalClinicArrearsFreePaymentWorkload());
    obj16th.put("3",workloadStatistic.getTotalClinicArrearsCouponWorkload());
    resultList.add(obj16th);
    resultList.add(createEmptyObj());

    JSONObject obj17th = new JSONObject(true);
    obj17th.put(RowStyle.IS_BOLD, true);
    obj17th.put("1","（首次）门诊实收非工作量合计");
    obj17th.put("2","（收欠费）门诊实收非工作量合计");
    obj17th.put("3","（被代收）门诊实收非工作量合计");
    resultList.add(obj17th);
    JSONObject obj18th = new JSONObject(true);
    obj18th.put(RowStyle.COLUMN_TYPE, Excel.ColumnType.NUMERIC);
    obj18th.put("1",workloadStatistic.getTotalClinicReceivedNotWorkload());
    obj18th.put("2",workloadStatistic.getTotalClinicArrearsReceivedNotWorkload());
    obj18th.put("3",workloadStatistic.getTotalBeCollectedNotWorkload());
    resultList.add(obj18th);
    resultList.add(createEmptyObj());

    MemberDataStatisticVO memberDataStatistic = clinicDataStatisticsInfoVO.getMemberDataStatistic();
    JSONObject obj19th = new JSONObject(true);
    obj19th.put(RowStyle.IS_BOLD, true);
    obj19th.put("1","会员卡充值(本金+赠金)");
    obj19th.put("2","会员卡消费(本金+赠金)");
    obj19th.put("3","会员卡退费(本金+赠金)");
    resultList.add(obj19th);
    JSONObject obj20th = new JSONObject(true);
    obj20th.put(RowStyle.COLUMN_TYPE, Excel.ColumnType.NUMERIC);
    obj20th.put("1",memberDataStatistic.getTotalMemberRechargeAmount());
    obj20th.put("2",memberDataStatistic.getTotalMemberExpendAmount());
    obj20th.put("3",memberDataStatistic.getTotalMemberRefundAmount());
    resultList.add(obj20th);
    resultList.add(createEmptyObj());


    PrepaymentsDataStatisticVO prepaymentsDataStatistic = clinicDataStatisticsInfoVO.getPrepaymentsDataStatistic();
    JSONObject obj21th = new JSONObject(true);
    obj21th.put(RowStyle.IS_BOLD, true);
    obj21th.put("1","预付款充值(本金+赠金)");
    obj21th.put("2","预付款消费(本金+赠金)");
    obj21th.put("3","预付款退费(本金+赠金)");
    resultList.add(obj21th);
    JSONObject obj22th = new JSONObject(true);
    obj20th.put(RowStyle.COLUMN_TYPE, Excel.ColumnType.NUMERIC);
    obj20th.put("1",prepaymentsDataStatistic.getTotalPrepaymentsRechargeAmount());
    obj20th.put("2",prepaymentsDataStatistic.getTotalPrepaymentsExpendAmount());
    obj20th.put("3",prepaymentsDataStatistic.getTotalPrepaymentsRefundAmount());
    resultList.add(obj20th);
    return resultList;
  }

  /**
   * 创建空元素
   *
   * @return
   */
  private JSONObject createEmptyObj() {
    JSONObject object = new JSONObject(true);
    object.put("1","");
    object.put("2","");
    object.put("3","");
    object.put("4","");
    object.put("5","");
    object.put("6","");
    return object;
  }
}
