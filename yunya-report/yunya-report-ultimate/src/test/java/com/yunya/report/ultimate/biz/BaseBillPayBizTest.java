package com.yunya.report.ultimate.biz;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.report.domain.bo.ClinicWorkloadGroupInfoVO;
import com.yunya.feign.report.domain.query.BillDetailIncomeDetailQuery;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.vo.BillTariffIncomeDetailVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

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

  @Autowired private BaseBillDetailBiz baseBillDetailBiz;

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

  @Test
  public void test() {
    BillDetailIncomeDetailQuery query = new BillDetailIncomeDetailQuery();
    query.setOrgIds(Arrays.asList(43, 26, 27, 36, 35, 37, 28, 29, 30, 31, 32, 33, 34, 39, 40, 45));
    query.setStartDate("2021-01");
    query.setEndDate("2021-12");
    List<BillTariffIncomeDetailVO> list = baseBillDetailBiz.findBillDetailIncomeList(query).getList();
    System.out.println("数据：" + JSONObject.toJSON(list));
  }
}
