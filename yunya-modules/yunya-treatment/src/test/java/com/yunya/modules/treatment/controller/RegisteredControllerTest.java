package com.yunya.modules.treatment.controller;

import com.yunya.feign.treatment.domain.model.RegisteredModel;
import com.yunya.framework.common.model.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介: 患者挂号控制器测试
 *
 * @author: chow
 * @date: 2020/8/11 12:59
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RegisteredControllerTest {

  /** 注入对象 */
  @Autowired private RegisteredController registeredController;

  @Test
  public void add() {
    RegisteredModel model = new RegisteredModel();
    model.setOrgId(35);
    model.setPatientId(1);
    model.setDentistId(12);
    model.setAssistantId(2);
    model.setDeptRoomId(3);
    ResponseResult result = registeredController.add(model);
    System.out.println(result);
  }
}
