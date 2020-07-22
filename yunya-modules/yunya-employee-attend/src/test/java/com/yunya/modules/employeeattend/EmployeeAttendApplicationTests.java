package com.yunya.modules.employeeattend;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.employeeattend.controller.EmployeeScheduleController;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;

@SpringBootTest
class EmployeeAttendApplicationTests {
  @Autowired
  private EmployeeScheduleController employeeScheduleController;
  @Test
  void contextLoads() {
  }
  /**
   * 导出排班
   */
  @Test
  public void export() {
    MockHttpServletResponse response = new MockHttpServletResponse();
    try {
      EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
//      employeeScheduleController.export(response,employeeScheduleQueryForm);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  /**
   * 查询排班
   */
  @Test
  public void textfindList() {
    EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
    employeeScheduleQueryForm.setPage(1);
    employeeScheduleQueryForm.setSize(10);
    employeeScheduleQueryForm.setClinicId(35);
    ResponseResult responseResult = employeeScheduleController.findList(employeeScheduleQueryForm);
    System.out.println(responseResult);
  }
}
