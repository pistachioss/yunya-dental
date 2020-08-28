package com.yunya.modules.treatment.biz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/8/28 16:12
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillRecordBizTest {
  @Autowired BillRecordBiz billRecordBiz;

  @Test
  public void getNum() {
    String number = billRecordBiz.generateBillNumber(21);
    System.out.println(number);
  }
}
