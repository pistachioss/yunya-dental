package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.InboundAndOutboundStatementQuery;
import com.yunya.feign.report.domain.vo.ClinicInboundAndOutboundVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2021/3/5 20:31
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseAccountItemBizTest {
  /***/
  @Autowired private BaseAccountItemBiz accountItemBiz;

  @Test
  public void test() {
    InboundAndOutboundStatementQuery query = new InboundAndOutboundStatementQuery();
    query.setOrgId(26);
    query.setDateType((byte) 2);
    query.setStartDate("2020");
    query.setEndDate("2020");
    List<ClinicInboundAndOutboundVO> list = new ArrayList<>();
    accountItemBiz.billChargeThisMonth(query, list);
  }
}
