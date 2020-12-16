package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.InboundAndOutboundStatementQuery;
import com.yunya.feign.report.domain.vo.BoundPaymentVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 支付方式相关业务测试
 *
 * @author: chow
 * @date: 2020/12/14 11:23
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseAccountItemMapperTest {

  @Autowired BaseAccountItemMapper accountItemMapper;

  @Test
  public void find() {
    InboundAndOutboundStatementQuery query = new InboundAndOutboundStatementQuery();
    query.setOrgId(35);
    query.setDateType((byte) 2);
    query.setStartDate("2020");
    query.setEndDate("2020");
    List<BoundPaymentVO> vos = accountItemMapper.selectBillChargePaymentInfo(query);
    System.out.println(vos);
  }

  @Test
  public void find1() {
    InboundAndOutboundStatementQuery query = new InboundAndOutboundStatementQuery();
    query.setOrgId(35);
    query.setDateType((byte) 2);
    query.setStartDate("2020");
    query.setEndDate("2020");
    List<BoundPaymentVO> vos = accountItemMapper.selectClinicCollectionPaymentInfo(query);
    System.out.println(vos);
  }

  @Test
  public void find2() {
    InboundAndOutboundStatementQuery query = new InboundAndOutboundStatementQuery();
    query.setOrgId(35);
    query.setDateType((byte) 2);
    query.setStartDate("2020");
    query.setEndDate("2020");
    List<BoundPaymentVO> vos = accountItemMapper.selectCollectArrearsPaymentInfo(query);
    System.out.println(vos);
  }

  @Test
  public void find3() {
    InboundAndOutboundStatementQuery query = new InboundAndOutboundStatementQuery();
    query.setOrgId(35);
    query.setDateType((byte) 2);
    query.setStartDate("2020");
    query.setEndDate("2020");
    BigDecimal bonus = accountItemMapper.selectBillChargeBonus(60, query);
    System.out.println(bonus);
  }
}
