package com.yunya.modules.treatment.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.query.BillRefundQuery;
import com.yunya.feign.treatment.domain.vo.BillRefundGroupInfoVO;
import com.yunya.feign.treatment.domain.vo.BillRefundRecordVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.treatment.controller.web.BillRefundRecordController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介: 账单退费记录控制器测试
 *
 * @author: chow
 * @date: 2020/11/3 16:06
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillRefundRecordControllerTest {
  /** 账单退费记录 */
  @Autowired private BillRefundRecordController billRefundRecordController;

  @Test
  public void testFindRefundList() {
    BillRefundQuery query = new BillRefundQuery();
    query.setPatientId(186);
    query.setBillNum("ZD00352010220001");
    ResponseResult<PageInfo<BillRefundRecordVO>> result =
        billRefundRecordController.billRefundList(query);
    System.out.println(result);
  }

  @Test
  public void findDetail() {
    ResponseResult<BillRefundGroupInfoVO> detail =
        billRefundRecordController.findBillRefundDetail(3);
    System.out.println(detail);
  }
}
