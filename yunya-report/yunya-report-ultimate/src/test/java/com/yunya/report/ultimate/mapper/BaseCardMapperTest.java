package com.yunya.report.ultimate.mapper;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.StatementProductSoldDetailQuery;
import com.yunya.feign.report.domain.vo.StatementProductSoldDetailVO;
import com.yunya.report.ultimate.biz.BaseCardBiz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2021/1/11 19:33
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseCardMapperTest {

  @Autowired private BaseCardMapper cardMapper;
  @Autowired private BaseCardBiz cardBiz;

  @Test
  public void find() {
    StatementProductSoldDetailQuery query = new StatementProductSoldDetailQuery();
    query.setOrgId(35);
    query.setDateType((byte)0);
    query.setStartDate("2020-10-01");
    query.setEndDate("2020-12-31");
    //query.setSoldTargetName("");
    //query.setSoldTargetMobile("");
    query.setCardNum("DJ0200000002");
    //query.setProductName("");
    //query.setSoldStartDate("");
    //query.setSoldEndDate("");
    List<StatementProductSoldDetailVO> vos = cardMapper.selectProductSoldDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void find1() {
    StatementProductSoldDetailQuery query = new StatementProductSoldDetailQuery();
    query.setOrgId(35);
    query.setDateType((byte)0);
    query.setStartDate("2020-10-01");
    query.setEndDate("2020-12-31");
    //query.setSoldTargetName("");
    //query.setSoldTargetMobile("");
    query.setCardNum("DJ0200000002");
    //query.setProductName("");
    //query.setSoldStartDate("");
    //query.setSoldEndDate("");
    PageInfo<StatementProductSoldDetailVO> pageInfo = cardBiz.findProductSoldDetailList(query);
    System.out.println(pageInfo);
  }
}
