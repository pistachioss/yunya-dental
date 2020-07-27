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
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
    Date date1 = null;
    Date date2 = null;
    Date date3 = null;
    Date date4 = null;
    try {
      date1 = simpleDateFormat.parse("2020-07-13");
      date2 = simpleDateFormat.parse("2020-07-14");
      date3 = simpleDateFormat.parse("2020-08-23");
      date4 = simpleDateFormat.parse("2020-08-24");
    } catch (ParseException e) {
      e.printStackTrace();
    }
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
