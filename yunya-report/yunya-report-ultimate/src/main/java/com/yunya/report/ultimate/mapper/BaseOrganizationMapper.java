package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.vo.PatientDataStatisticsVO;
import com.yunya.models.report.BaseOrganization;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BaseOrganizationMapper extends Mapper<BaseOrganization> {

  /**
   * 根据条件查询门诊患者数据总览
   *
   * @param query 查询条件
   * @return PatientDataStatisticsVO
   */
  PatientDataStatisticsVO selectClinicPatientDataStatistic(
      @Param("query") DataStatisticsQuery query);
}
