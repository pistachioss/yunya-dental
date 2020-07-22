package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.system.form.DepartmentRoomForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介: 科室控制层测试
 *
 * @author: chow
 * @date: 2020/7/20 17:55
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DeptRoomControllerTest {
  /** 注入对象 */
  @Autowired private DepartmentRoomController departmentRoomController;

  @Test
  public void add() {
    DepartmentRoomForm resource = new DepartmentRoomForm();
    resource.setName("正畸中心");
    ResponseResult result = departmentRoomController.add(resource);
    System.out.println(result);
  }
}
