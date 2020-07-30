package com.yunya.modules.employeeattend;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.employeeattend.controller.EmployeeScheduleController;
import com.yunya.modules.employeeattend.form.EmployeeScheduleCopyForm;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
  /**
   * 复制排班
   */
  @Test
  public void copy() {
    EmployeeScheduleCopyForm employeeScheduleCopyForm = new EmployeeScheduleCopyForm();


      String date1 = "2020-07-12";
      String date2 = "2020-07-13";
      String date3 = "2020-08-10";
      String date4 = "2020-08-11";
    employeeScheduleCopyForm.setStartDate(date1);
    employeeScheduleCopyForm.setEndDate(date2);
    employeeScheduleCopyForm.setTargetStartDate(date3);
    employeeScheduleCopyForm.setTargetEndDate(date4);
    List<Integer> list = new ArrayList<>();
    list.add(533);
    list.add(526);
    employeeScheduleCopyForm.setEmployeeIdLIst(list);
    employeeScheduleCopyForm.setClinicId("35");
    ResponseResult responseResult = employeeScheduleController.copy(employeeScheduleCopyForm);
    System.out.println(responseResult);
  }
}
