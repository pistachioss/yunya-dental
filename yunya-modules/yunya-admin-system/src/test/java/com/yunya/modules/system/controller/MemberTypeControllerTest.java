package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.system.domain.model.MemberTypeModel;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * 简介: 会员类型控制器测试
 *
 * @author: chow
 * @date: 2020/10/24 12:34
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MemberTypeControllerTest {
  @Autowired private MemberTypeController memberTypeController;

  @Test
  public void testAdd() {
    String icon = "7";
    MemberTypeModel entity = new MemberTypeModel();
    entity.setType((byte)7);
    entity.setName("测试会员");
    entity.setRenewalAmount(BigDecimal.valueOf(100));
    entity.setAgeLimit(1);
    entity.setRate(0.1F);

    /*entity.setIcon(icon);
    entity.setPictureCode("adadasdq");
    entity.setDescription("qqweqe");
    ResponseResult<T> result = memberTypeController.save(entity);
    System.out.println(result);*/
  }
}
