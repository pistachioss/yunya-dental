package com.yunya.modules.treatment.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.model.RegisteredModel;
import com.yunya.feign.treatment.domain.query.RegisteredQueryForm;
import com.yunya.feign.treatment.domain.vo.WaitingPatientInfoVO;
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
    //model.setOrgId(35);
    model.setPatientId(1);
    model.setDentistId(12);
    model.setAssistantId(2);
    model.setDeptRoomId(3);
    ResponseResult result = registeredController.add(model);
    System.out.println(result);
  }

  @Test
  public void findList() {
    RegisteredQueryForm form = new RegisteredQueryForm();
    form.setCurrentDate("2020-09-29");
    form.setDentistId(526);
    form.setOrgId(35);
    form.setInservice(true);
    form.setWhetherPage(false);
    ResponseResult<PageInfo<WaitingPatientInfoVO>> list = registeredController.findList(form);
    System.out.println(list);
  }
}
