package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.vo.StatementPaymentVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介: 账单收费支付明细测试
 *
 * @author: chow
 * @date: 2021/1/13 10:25
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseBillPayDetailMapperTest {

  @Autowired private BaseBillPayDetailMapper billPayDetailMapper;

  @Test
  public void find() {
    List<StatementPaymentVO> vos = billPayDetailMapper.selectBillPayDetailList(1301);
    System.out.println(vos);
  }
}
