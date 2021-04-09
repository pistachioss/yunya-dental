package com.yunya.report.ultimate.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.bo.ClinicWorkloadGroupInfoVO;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.FirstVisitDetailQuery;
import com.yunya.feign.report.domain.vo.FirstVisitDetailVO;
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
  @Autowired private BaseTreatmentProcessBiz baseTreatmentProcessBiz;

  @Test
  public void find() {
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setDateType((byte) 0);
    query.setOrgIds(new Integer[] {43});
    query.setStartDate("2021-03-30");
    query.setEndDate("2021-03-30");
    ClinicWorkloadGroupInfoVO vo = billPayBiz.generateClinicWorkloadInfo(query);
    System.out.println(vo);
  }

  /**
   * 初诊患者统计明细
   */
  @Test
  public void test() {
    String param = "{\"patientName\":\"\",\"orgId\":26,\"pageNum\":1,\"pageSize\":10,\"registeredDentistIds\":[317],\"whetherPage\":true,\"userStatus\":[],\"startDate\":\"2021-01-07\",\"endDate\":\"2021-04-07\",\"showClinic\":true}";
    FirstVisitDetailQuery query = JSONObject.parseObject(param,FirstVisitDetailQuery.class);
    long t1 = System.currentTimeMillis();
    PageInfo<FirstVisitDetailVO> pageInfo = baseTreatmentProcessBiz.firstVisitRecordDetail(query);
    System.out.println(System.currentTimeMillis() - t1);
    System.out.println(JSONObject.toJSON(pageInfo));
  }
}
