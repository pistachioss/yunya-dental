package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.CardConsumeQuery;
import com.yunya.feign.report.domain.vo.CardConsumeRecordVO;
import com.yunya.framework.common.model.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2022/7/24 23:07
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DiscountControllerTest {
  @Autowired private CompanyReportOfMarket companyReportOfMarket;

  @Test
  public void find() {
    CardConsumeQuery query = new CardConsumeQuery();
    query.setActivationStartDate("2022-07-01");
    query.setActivationEndDate("2022-07-24");
    ResponseResult<PageInfo<CardConsumeRecordVO>> result =
        companyReportOfMarket.getCardConsumeRecordList(query);
    System.out.println(result.getData());
  }
}
