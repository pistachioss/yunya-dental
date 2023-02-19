package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.vo.MemberDataStatisticVO;
import com.yunya.feign.report.domain.vo.PrepaymentsDataStatisticVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.BasePatientMember;
import com.yunya.report.ultimate.mapper.BasePatientMemberMapper;
import org.springframework.stereotype.Service;

/**
 * 简介: 患者会员业务层
 *
 * @author: chow
 * @date: 2020/12/8 16:01
 * @description:
 * @since: 1.0.0
 */
@Service
public class BasePatientMemberBiz extends BaseBiz<BasePatientMemberMapper, BasePatientMember> {

  /**
   * 根据条件查询门诊会员数据总览
   *
   * @param query 查询条件
   * @return MemberDataStatisticVO
   */
  public MemberDataStatisticVO findClinicMemberDataStatistic(DataStatisticsQuery query) {
    MemberDataStatisticVO resultData = mapper.selectClinicMemberDataStatistic(query);
    return resultData;
  }

  /**
   * 根据条件查询门诊预付款数据总览
   *
   * @param query 查询条件
   * @return PrepaymentsDataStatisticVO
   */
  public PrepaymentsDataStatisticVO findClinicPrepaymentsDataStatistic(DataStatisticsQuery query, int type) {
    PrepaymentsDataStatisticVO resultData = mapper.selectClinicPrepaymentsDataStatistic(query, type);
    return resultData;
  }
}
