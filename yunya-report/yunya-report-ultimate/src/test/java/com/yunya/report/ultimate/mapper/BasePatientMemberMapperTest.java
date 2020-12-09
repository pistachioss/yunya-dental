package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.vo.MemberDataStatisticVO;
import com.yunya.feign.report.domain.vo.PrepaymentsDataStatisticVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/12/9 17:41
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BasePatientMemberMapperTest {
  @Autowired private BasePatientMemberMapper patientMemberMapper;

  @Test
  public void find1() {
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setOrgIds(new Integer[] {35, 42, 72});
    query.setDateType((byte) 0);
    query.setStartDate("2020-07-01");
    query.setEndDate("2020-12-12");
    MemberDataStatisticVO vo = patientMemberMapper.selectClinicMemberDataStatistic(query);
    System.out.println(vo);
  }
  @Test
  public void find2() {
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setOrgIds(new Integer[] {35, 42, 72});
    query.setDateType((byte) 0);
    query.setStartDate("2020-07-01");
    query.setEndDate("2020-12-12");
    PrepaymentsDataStatisticVO vo = patientMemberMapper.selectClinicPrepaymentsDataStatistic(query);
    System.out.println(vo);
  }
}
