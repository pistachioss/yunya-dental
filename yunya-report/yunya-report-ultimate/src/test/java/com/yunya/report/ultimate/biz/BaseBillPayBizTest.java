package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.bo.ClinicWorkloadGroupInfoVO;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2021/3/21 20:08
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseBillPayBizTest {
  @Autowired private BaseBillPayBiz billPayBiz;

  @Test
  public void find() {
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setDateType((byte) 0);
    query.setOrgIds(new Integer[] {26});
    query.setStartDate("2021-03-01");
    query.setEndDate("2021-03-20");
    ClinicWorkloadGroupInfoVO vo = billPayBiz.generateClinicWorkloadInfo(query);
    System.out.println(vo);
  }
}
