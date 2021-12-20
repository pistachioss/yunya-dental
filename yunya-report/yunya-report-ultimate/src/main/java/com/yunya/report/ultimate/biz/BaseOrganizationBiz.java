package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.ClinicPerformanceBusinessQuery;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.vo.BaseOrganizationVO;
import com.yunya.feign.report.domain.vo.PatientDataStatisticsVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简介: 门诊相关功能业务层
 *
 * @author: chow
 * @date: 2020/12/8 17:54
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseOrganizationBiz extends BaseBiz<BaseOrganizationMapper, BaseOrganization> {

  /**
   * 门诊患者数据汇总
   *
   * @param query
   * @return
   */
  public PatientDataStatisticsVO findClinicPatientDataStatistic(DataStatisticsQuery query) {
    query.setWhetherPage(false);
    return findClinicPatientDataList(query, false).getList().get(0);
  }

  /**
   * 根据条件查询门诊患者数据总览
   *
   * @param query 查询条件
   * @return PatientDataStatisticsVO
   * @return notSummarize 是否不进行汇总
   */
  public PageInfo<PatientDataStatisticsVO> findClinicPatientDataList(
      DataStatisticsQuery query, boolean notSummarize) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    Map<Integer, PatientDataStatisticsVO> patientMap = new HashMap<>(16);
    List<PatientDataStatisticsVO> resultData = mapper.selectClinicPatientDataStatistic(query);
    PageInfo<PatientDataStatisticsVO> pageInfo = new PageInfo<>(resultData);
    for (PatientDataStatisticsVO resultDatum : resultData) {
      Integer orgId = -1;
      if (notSummarize) {
        orgId = resultDatum.getOrgId();
      }
      PatientDataStatisticsVO patientDataStatisticsVO = patientMap.get(orgId);
      Integer firstVisitPerNum = 0;
      Integer repeatVisitsPerNum = 0;
      Integer treatPerNum = 0;
      Integer appointPerNum = 0;
      Integer repeatVisitsPerTimes = 0;
      Integer treatPerTimes = 0;
      Integer appointPerTimes = 0;
      Integer appointModifyPerTimes = 0;
      Integer appointCancelPerTimes = 0;
      Integer appointMissedPerTimes = 0;
      BigDecimal totalActualAmount = BigDecimal.ZERO;
      if (patientDataStatisticsVO == null) {
        patientDataStatisticsVO = new PatientDataStatisticsVO();
      } else {
        firstVisitPerNum = patientDataStatisticsVO.getFirstVisitPerNum();
        repeatVisitsPerNum = patientDataStatisticsVO.getRepeatVisitsPerNum();
        treatPerNum = patientDataStatisticsVO.getTreatPerNum();
        appointPerNum = patientDataStatisticsVO.getAppointPerNum();
        repeatVisitsPerTimes = patientDataStatisticsVO.getRepeatVisitsPerTimes();
        treatPerTimes = patientDataStatisticsVO.getTreatPerTimes();
        appointPerTimes = patientDataStatisticsVO.getAppointPerTimes();
        appointModifyPerTimes = patientDataStatisticsVO.getAppointModifyPerTimes();
        appointCancelPerTimes = patientDataStatisticsVO.getAppointCancelPerTimes();
        appointMissedPerTimes = patientDataStatisticsVO.getAppointMissedPerTimes();
        totalActualAmount = patientDataStatisticsVO.getTotalActualAmount();
      }
      patientDataStatisticsVO.setFirstVisitPerNum(
          firstVisitPerNum + resultDatum.getFirstVisitPerNum());
      patientDataStatisticsVO.setRepeatVisitsPerNum(
          repeatVisitsPerNum + resultDatum.getRepeatVisitsPerNum());
      patientDataStatisticsVO.setRepeatVisitsPerTimes(
          repeatVisitsPerTimes + resultDatum.getRepeatVisitsPerTimes());
      patientDataStatisticsVO.setTreatPerNum(treatPerNum + resultDatum.getTreatPerNum());
      patientDataStatisticsVO.setTreatPerTimes(treatPerTimes + resultDatum.getTreatPerTimes());
      patientDataStatisticsVO.setAppointPerNum(appointPerNum + resultDatum.getAppointPerNum());
      patientDataStatisticsVO.setAppointPerTimes(
          appointPerTimes + resultDatum.getAppointPerTimes());
      patientDataStatisticsVO.setAppointModifyPerTimes(
          appointModifyPerTimes + resultDatum.getAppointModifyPerTimes());
      patientDataStatisticsVO.setAppointCancelPerTimes(
          appointCancelPerTimes + resultDatum.getAppointCancelPerTimes());
      patientDataStatisticsVO.setAppointMissedPerTimes(
          appointMissedPerTimes + resultDatum.getAppointMissedPerTimes());
      patientDataStatisticsVO.setTotalActualAmount(
          totalActualAmount.add(resultDatum.getTotalActualAmount()));
      patientMap.put(orgId, patientDataStatisticsVO);
    }
    List<PatientDataStatisticsVO> list = new ArrayList<>();
    if (StringHelper.isNotEmpty(patientMap)) {
      patientMap.forEach(
          (id, patientDataStatisticsVO) -> {
            BigDecimal averageConsumption = BigDecimal.ZERO;
            BigDecimal perCapitaConsumption = BigDecimal.ZERO;
            Integer treatPerNum = patientDataStatisticsVO.getTreatPerNum();
            Integer treatPerTimes = patientDataStatisticsVO.getTreatPerTimes();
            BigDecimal totalActualAmount = patientDataStatisticsVO.getTotalActualAmount();
            if (treatPerTimes != 0) {
              averageConsumption =
                  totalActualAmount.divide(
                      BigDecimal.valueOf(treatPerTimes), 2, BigDecimal.ROUND_HALF_UP);
            }
            if (treatPerNum != 0) {
              perCapitaConsumption =
                  totalActualAmount.divide(
                      BigDecimal.valueOf(treatPerNum), 2, BigDecimal.ROUND_HALF_UP);
            }
            patientDataStatisticsVO.setAverageConsumption(averageConsumption);
            patientDataStatisticsVO.setPerCapitaConsumption(perCapitaConsumption);
            list.add(patientDataStatisticsVO);
          });
    } else {
      list.add(new PatientDataStatisticsVO(true));
    }
    if (notSummarize && query.getSource() == 0) {
      list.add(summary(list));
    }
    pageInfo.setList(list);
    return pageInfo;
  }

  /**
   * 数据汇总
   *
   * @param list
   * @return
   */
  private PatientDataStatisticsVO summary(List<PatientDataStatisticsVO> list) {
    PatientDataStatisticsVO result = new PatientDataStatisticsVO();
    Integer firstVisitPerNum = 0;
    Integer repeatVisitsPerNum = 0;
    Integer treatPerNum = 0;
    Integer appointPerNum = 0;
    Integer repeatVisitsPerTimes = 0;
    Integer treatPerTimes = 0;
    Integer appointPerTimes = 0;
    Integer appointModifyPerTimes = 0;
    Integer appointCancelPerTimes = 0;
    Integer appointMissedPerTimes = 0;
    BigDecimal averageConsumption = BigDecimal.ZERO;
    BigDecimal perCapitaConsumption = BigDecimal.ZERO;
    for (PatientDataStatisticsVO vo : list) {
      firstVisitPerNum += vo.getFirstVisitPerNum();
      repeatVisitsPerNum += vo.getRepeatVisitsPerNum();
      repeatVisitsPerTimes += vo.getRepeatVisitsPerTimes();
      treatPerNum += vo.getTreatPerNum();
      treatPerTimes += vo.getTreatPerTimes();
      appointPerNum += vo.getAppointPerNum();
      appointPerTimes += vo.getAppointPerTimes();
      appointModifyPerTimes += vo.getAppointModifyPerTimes();
      appointCancelPerTimes += vo.getAppointCancelPerTimes();
      appointMissedPerTimes += vo.getAppointMissedPerTimes();
      averageConsumption = averageConsumption.add(vo.getAverageConsumption());
      perCapitaConsumption = perCapitaConsumption.add(vo.getPerCapitaConsumption());
    }
    result.setFirstVisitPerNum(firstVisitPerNum);
    result.setRepeatVisitsPerTimes(repeatVisitsPerTimes);
    result.setRepeatVisitsPerNum(repeatVisitsPerNum);
    result.setAppointMissedPerTimes(appointMissedPerTimes);
    result.setAppointCancelPerTimes(appointCancelPerTimes);
    result.setAppointModifyPerTimes(appointModifyPerTimes);
    result.setAppointPerTimes(appointPerTimes);
    result.setAppointPerNum(appointPerNum);
    result.setTreatPerTimes(treatPerTimes);
    result.setTreatPerNum(treatPerNum);
    result.setAverageConsumption(averageConsumption);
    result.setPerCapitaConsumption(perCapitaConsumption);
    result.setOrgId(-1);
    result.setAbbreviation("\\/");
    return result;
  }

  /**
   * 获取所有门诊信息
   *
   * @param query
   */
  public List<BaseOrganization> getOrganization(ClinicPerformanceBusinessQuery query) {
    return mapper.selectOrganizationList(query);
  }


  /**
   * 获取所有门诊信息及其上级信息
   *
   * @param query
   */
  public List<BaseOrganizationVO> getOrganizationWithParent(ClinicPerformanceBusinessQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    return mapper.selectOrganizationWithParentList(query);
  }
}
