package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.vo.PatientDataStatisticsVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import org.springframework.stereotype.Service;

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
   * 根据条件查询门诊患者数据总览
   *
   * @param query 查询条件
   * @return PatientDataStatisticsVO
   */
  public PatientDataStatisticsVO findClinicPatientDataStatistic(DataStatisticsQuery query) {
    PatientDataStatisticsVO resultData = mapper.selectClinicPatientDataStatistic(query);
    return resultData;
  }
}
