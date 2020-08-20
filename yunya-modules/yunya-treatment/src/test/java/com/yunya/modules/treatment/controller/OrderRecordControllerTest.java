package com.yunya.modules.treatment.controller;

import com.google.common.collect.Lists;
import com.yunya.feign.treatment.domain.model.OrderDetailModel;
import com.yunya.feign.treatment.domain.model.OrderRecordModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.biz.TreatmentRecordBiz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介: 开单控制器测试
 *
 * @author: chow
 * @date: 2020/8/18 14:48
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderRecordControllerTest {

  /** 注入对象 */
  @Autowired private OrderRecordController orderRecordController;

  @Test
  public void save() {
    OrderRecordModel model = new OrderRecordModel();
    model.setTreatmentRecordId(919);
    List<OrderDetailModel> details = Lists.newArrayList();
    OrderDetailModel detail = new OrderDetailModel();
    detail.setType((byte)0);
    detail.setBillingItemId(89);
    detail.setQuantity(1);
    details.add(detail);
    model.setOrderDetails(details);
    ResponseResult result = orderRecordController.add(model);
    System.out.println(result);
  }
}
