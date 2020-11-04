package com.yunya.modules.treatment.controller;

import com.yunya.feign.treatment.domain.vo.BillDetailGroupVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.treatment.controller.web.BillRecordController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介: 账单控制器测试
 *
 * @author: chow
 * @date: 2020/10/20 14:39
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillControllerTest {
  @Autowired private BillRecordController billRecordController;

  @Test
  public void testFind() {
    ResponseResult<BillDetailGroupVO> record =
        billRecordController.findOrderDetailAndBillDetailByOrderRecordId(23);
    System.out.println(record);
  }
}
