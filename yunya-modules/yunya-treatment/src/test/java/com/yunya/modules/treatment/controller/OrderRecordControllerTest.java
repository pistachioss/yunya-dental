package com.yunya.modules.treatment.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.report.domain.query.CategoryIncomeQuery;
import com.yunya.feign.report.domain.vo.CategoryInfoIncomeVO;
import com.yunya.feign.treatment.domain.model.OrderDetailModel;
import com.yunya.feign.treatment.domain.model.OrderRecordModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.treatment.controller.web.OrderDetailController;
import com.yunya.modules.treatment.controller.web.OrderRecordController;
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

  @Autowired private OrderDetailController orderDetailController;

  @Autowired private RemoteDiscountFeign discountFeign;

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
    ResponseResult result = orderRecordController.holdOrder(model);
    System.out.println(result);
  }

  @Test
  public void test(){
    ResponseResult info = orderRecordController.findOrderInfoByTreatmentId(11);
    System.out.println(info);
  }

  @Test
  public void categoryIncomeList() throws Exception {
    String param = "{\"orgIds\":[26],\"startDate\":\"2021-01\",\"startDate\":\"2021-01\",\"whetherPage\":false}";
    CategoryIncomeQuery query = JSONObject.parseObject(param, CategoryIncomeQuery.class);
    long l = System.currentTimeMillis();
    PageInfo<CategoryInfoIncomeVO> pageInfo = orderDetailController.categoryIncomeList(query).getData();
    System.out.println("耗时：" + (System.currentTimeMillis() - l));
    System.out.println(JSONObject.toJSON(pageInfo));
  }
}
