package com.yunya.report.ultimate.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.BillItemTollWorkloadQuery;
import com.yunya.feign.report.domain.query.ClinicEmployeeWorkloadQuery;
import com.yunya.feign.report.domain.query.EmployeeWorkStatusQueryForm;
import com.yunya.feign.report.domain.query.PatientDimensionQueryForm;
import com.yunya.feign.report.domain.query.base.MultiClinicDateRangetQueryForm;
import com.yunya.feign.report.domain.query.base.MultiClinicNumDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BillItemTollAndWorkloadVO;
import com.yunya.feign.report.domain.vo.ClinicEmployeeWorkloadOfOperationVO;
import com.yunya.feign.report.domain.vo.ClinicEmployeeWorkloadOfPersonnelVO;
import com.yunya.feign.report.domain.vo.DynamicHeaderPageInfo;
import com.yunya.framework.common.model.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chenl
 * @date: 2020/11/4 10:10
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class EmployeeWorkloadControllerTest {
  @Autowired private EmployeeReportController employeeReportController;
  @Autowired private DimensionReportController dimensionReportController;

  @Test
  public void employeeWorkloadListOfOperation() throws Exception {
    // 26,27,28,29,30,31,32,33
    String param = "{\"dateType\":0,\"employeeIds\":[],\"postIds\":[],\"workStatus\":[],\"enableFilter\":1,\"orgIds\":[35],\"pageNum\":1,\"pageSize\":10,\"startDate\":\"2021-12-16\",\"endDate\":\"2021-12-16\",\"whetherPage\":true}";
    ClinicEmployeeWorkloadQuery query = JSONObject.parseObject(param, ClinicEmployeeWorkloadQuery.class);
    long t1 = System.currentTimeMillis();
    ResponseResult<PageInfo<ClinicEmployeeWorkloadOfOperationVO>> result = employeeReportController.employeeWorkloadListOfOperation(query);
    System.out.println("总耗时：" + (System.currentTimeMillis() - t1));
    System.out.println(JSONObject.toJSON(result.getData()));
  }

  @Test
  public void employeeWorkloadListOfPersonnel() throws Exception {
    // 26,27,28,29,30,31,32,33
    String param = "{\"dateType\":1,\"employeeIds\":[],\"postIds\":[],\"workStatus\":[],\"enableFilter\":1,\"orgIds\":[26],\"pageNum\":1,\"pageSize\":10,\"startDate\":\"2021-01\",\"endDate\":\"2021-12\",\"whetherPage\":true}";
    ClinicEmployeeWorkloadQuery query = JSONObject.parseObject(param, ClinicEmployeeWorkloadQuery.class);
    long t1 = System.currentTimeMillis();
    ResponseResult<PageInfo<ClinicEmployeeWorkloadOfPersonnelVO>> result = employeeReportController.employeeWorkloadListOfPersonnel(query);
    System.out.println("总耗时：" + (System.currentTimeMillis() - t1));
    System.out.println(JSONObject.toJSON(result.getData()));
  }

  @Test
  public void tariffPaymentWorkloadStatistics() throws Exception {
    // 26,27,28,29,30,31,32,33
    String param = "{\"categoryItems\":[],\"employeeIds\":[],\"workStatus\":[],\"startDate\":\"2021-01-01\",\"endDate\":\"2021-01-31\",\"orgIds\":[],\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true}";
    BillItemTollWorkloadQuery query = JSONObject.parseObject(param, BillItemTollWorkloadQuery.class);
    long t1 = System.currentTimeMillis();
    ResponseResult<PageInfo<BillItemTollAndWorkloadVO>> result = employeeReportController.tariffPaymentWorkloadStatistics(query);
    System.out.println("总耗时：" + (System.currentTimeMillis() - t1));
    System.out.println(JSONObject.toJSON(result.getData()));
  }

  @Test
  public void exportEmployeeWorkloadListOfPersonnel() throws Exception {
    // 26,27,28,29,30,31,32,33
    String param = "{\"dateType\":1,\"employeeIds\":[],\"postIds\":[],\"workStatus\":[],\"enableFilter\":1,\"orgIds\":[26],\"pageNum\":1,\"pageSize\":10,\"startDate\":\"2021-01\",\"endDate\":\"2021-12\",\"whetherPage\":true}";
    ClinicEmployeeWorkloadQuery query = JSONObject.parseObject(param, ClinicEmployeeWorkloadQuery.class);
    long t1 = System.currentTimeMillis();
    employeeReportController.exportEmployeeWorkloadListOfPersonnel(new MockHttpServletResponse(), query);
    System.out.println("总耗时：" + (System.currentTimeMillis() - t1));
  }

  @Test
  public void testPatientDimensionStatistics() throws Exception {
    String param = "{\"patientName\":\"张小勇\",\"dateType\":2,\"startDate\":\"2015\",\"endDate\":\"2015\",\"whetherPage\":true,\"pageNum\":1,\"pageSize\":50}";
    PatientDimensionQueryForm query = JSONObject.parseObject(param, PatientDimensionQueryForm.class);
    long t1 = System.currentTimeMillis();
    ResponseResult<DynamicHeaderPageInfo<JSONObject>> result = dimensionReportController.patientDimensionStatistics(query);
    System.out.println(System.currentTimeMillis() - t1);
    System.out.println(JSONObject.toJSON(result));
  }

  @Test
  public void testDentistDimensionStatistics() throws Exception {
    String param = "{\"employeeIds\":[341],\"dateType\":2,\"startDate\":\"2021\",\"endDate\":\"2021\",\"whetherPage\":true,\"pageNum\":1,\"pageSize\":10}";
    ClinicEmployeeWorkloadQuery query = JSONObject.parseObject(param, ClinicEmployeeWorkloadQuery.class);
    long t1 = System.currentTimeMillis();
    ResponseResult<DynamicHeaderPageInfo<JSONObject>> result = dimensionReportController.dentistDimensionStatistics(query);
    System.out.println(System.currentTimeMillis() - t1);
    System.out.println(JSONObject.toJSON(result));
  }

  @Test
  public void testClinicDimensionStatistics() throws Exception {
    String param = "{\"employeeIds\":[341],\"orgIds\":[26],\"dateType\":2,\"startDate\":\"2021\",\"endDate\":\"2021\",\"whetherPage\":true,\"pageNum\":1,\"pageSize\":10}";
    ClinicEmployeeWorkloadQuery query = JSONObject.parseObject(param, ClinicEmployeeWorkloadQuery.class);
    long t1 = System.currentTimeMillis();
    ResponseResult<DynamicHeaderPageInfo<JSONObject>> result = dimensionReportController.clinicDimensionStatistics(query);
    System.out.println(System.currentTimeMillis() - t1);
    System.out.println(JSONObject.toJSON(result));
  }

  @Test
  public void testClinicFirstVisitSourceRatio() throws Exception {
    String param = "{\"orgIds\":[43, 26, 27, 36, 35, 37, 28, 29, 30, 31, 32, 33, 34, 39, 40, 45],\"dateType\":2,\"startDate\":\"2021\",\"endDate\":\"2021\",\"whetherPage\":true,\"pageNum\":1,\"pageSize\":10}";
    MultiClinicDateRangetQueryForm query = JSONObject.parseObject(param, MultiClinicDateRangetQueryForm.class);
    long t1 = System.currentTimeMillis();
    ResponseResult<DynamicHeaderPageInfo<JSONObject>> result = dimensionReportController.clinicFirstVisitSourceRatio(query);
    System.out.println(System.currentTimeMillis() - t1);
    System.out.println(JSONObject.toJSON(result));
  }

  @Test
  public void testClinicSpecialProjectWorkloadRatio() throws Exception {
    String param = "{\"orgIds\":[43, 26, 27, 36, 35, 37, 28, 29, 30, 31, 32, 33, 34, 39, 40, 45],\"dateType\":2,\"startDate\":\"2021\",\"endDate\":\"2021\",\"whetherPage\":true,\"pageNum\":1,\"pageSize\":10}";
    MultiClinicDateRangetQueryForm query = JSONObject.parseObject(param, MultiClinicDateRangetQueryForm.class);
    long t1 = System.currentTimeMillis();
    ResponseResult<DynamicHeaderPageInfo<JSONObject>> result = dimensionReportController.clinicSpecialProjectWorkloadRatio(query);
    System.out.println(System.currentTimeMillis() - t1);
    System.out.println(JSONObject.toJSON(result));
  }

  @Test
  public void testClinicWorkloadVisitStatistics() throws Exception {
    String param = "{\"orgIds\":[43, 26, 27, 36, 35, 37, 28, 29, 30, 31, 32, 33, 34, 39, 40, 45],\"dateType\":2,\"startDate\":\"2021\",\"endDate\":\"2021\",\"whetherPage\":true,\"pageNum\":1,\"pageSize\":2}";
    MultiClinicNumDateRangeQueryForm query = JSONObject.parseObject(param, MultiClinicNumDateRangeQueryForm.class);
    long t1 = System.currentTimeMillis();
    ResponseResult<DynamicHeaderPageInfo<JSONObject>> result = dimensionReportController.clinicWorkloadVisitStatistics(query);
    System.out.println(System.currentTimeMillis() - t1);
    System.out.println(JSONObject.toJSON(result));
  }

  @Test
  public void testDentistWorkloadVisitStatistics() throws Exception {
    String param = "{\"employeeIds\":[],\"workstatus\":[],\"dateType\":2,\"startDate\":\"2021\",\"endDate\":\"2021\",\"whetherPage\":true,\"pageNum\":1,\"pageSize\":2}";
    EmployeeWorkStatusQueryForm query = JSONObject.parseObject(param, EmployeeWorkStatusQueryForm.class);
    long t1 = System.currentTimeMillis();
    ResponseResult<DynamicHeaderPageInfo<JSONObject>> result = dimensionReportController.dentistWorkloadVisitStatistics(query);
    System.out.println(System.currentTimeMillis() - t1);
    System.out.println(JSONObject.toJSON(result));
  }
}
