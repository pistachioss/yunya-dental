package com.yunya.modules.employeeattend;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.employee_attend.form.AttendanceItemCountQuery;
import com.yunya.feign.employee_attend.vo.*;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.modules.employeeattend.biz.EmployeePushBiz;
import com.yunya.modules.employeeattend.controller.*;
import com.yunya.modules.employeeattend.form.*;
import com.yunya.modules.employeeattend.util.JpushManager;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.*;


@SpringBootTest
class EmployeeAttendApplicationTests {
  @Autowired
  private EmployeeScheduleController employeeScheduleController;

  @Autowired
  private LeaveInfoController leaveInfoController;

  @Autowired
  private FieldInfoController fieldInfoController;

  @Autowired
  private WorkOvertimeInfoController workOvertimeInfoController;

  @Autowired
  private CopyInfoController copyInfoController;

  @Autowired
  private EmployeePushBiz employeePushBiz;

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

  @Test
  public void leaveInfoFindList() {
    LeaveInfoForm form = new LeaveInfoForm();
    form.setCopyId(15);
    List<LeaveInfoListVO> list = leaveInfoController.findList(form).getData();
    System.out.println(JSONObject.toJSON(list));
  }

  @Test
  public void findApprovalByMe() {
    FindApprovalByMeForm form = new FindApprovalByMeForm();
    form.setUserId(15);
    List<LeaveAppVO> list = leaveInfoController.findApprovalByMe(form).getData();
    System.out.println(JSONObject.toJSON(list));
  }

  @Test
  public void fieldFindList() {
    FieldInfoForm form = new FieldInfoForm();
    form.setApprovalPeopleId(15);
    form.setApprovalStatus(0);
    List<FieldInfoListVO> list = fieldInfoController.findList(form).getData();
    System.out.println(JSONObject.toJSON(list));
  }

  @Test
  public void workOverFindList() {
    BaseContextHandler.setUserID("635");
    WorkOvertimeInfoForm form = new WorkOvertimeInfoForm();
//    form.setApprovalPeopleId(38);
//    form.setApprovalStatus(0);
    form.setId(38);
    form.setQueryCopyInfo(true);
    List<WorkOvertimeInfoListVO> list = workOvertimeInfoController.findList(form).getData();
    System.out.println(JSONObject.toJSON(list));
  }

  @Test
  public void leaveInfoFindById() {
    BaseContextHandler.setUserID("635");
    LeaveInfoForm form = new LeaveInfoForm();
    form.setId(346);
    List<LeaveInfoListVO> list = leaveInfoController.findList(form).getData();
    System.out.println(JSONObject.toJSON(list));
  }

  @Test
  public void associatedItemCount() {
    AttendanceItemCountQuery query = new AttendanceItemCountQuery();
    query.setQueryType(0);
    query.setUserId(15);
    AttendanceItemCountVO data = copyInfoController.attendanceItemCount(query).getData();
    System.out.println(JSONObject.toJSON(data));
  }

  @Test
  public void testPush() {
    EmployeePushForm form = new EmployeePushForm();
    // 组装
    Set<Integer> emp_ids = new HashSet<>();
    emp_ids.add(635);
    form.setEmpId(emp_ids);
    form.setShowName("xxx");
    form.setIds(Arrays.asList(1000));
    List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(form);
    employeePushFormList.forEach(el -> {
      JpushManager.getInstance().pushLeaveApproval(el, 2);
    });
  }

  @Test
  public void testPushSchedule() {
    EmployeePushForm form = new EmployeePushForm();
    // 组装
    Set<Integer> emp_ids = new HashSet<>();
    emp_ids.add(635);
    form.setEmpId(emp_ids);
    form.setIsSchedule(true);
    Date dt = DateTime.parse("2022-04-01").toDate();
    dt.setHours(18);
    dt.setMinutes(0);
    dt.setSeconds(1);
    form.setScheTime(DateUtil.format(new Date(dt.getTime() - 10*60*1000)));
    List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(form);
    employeePushFormList.forEach(el -> {
      JpushManager.getInstance().pushAttend(el);
    });
  }
}
