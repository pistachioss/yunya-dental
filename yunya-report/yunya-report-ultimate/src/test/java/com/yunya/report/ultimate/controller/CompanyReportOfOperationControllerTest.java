package com.yunya.report.ultimate.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.bo.ClinicDataStatisticsInfoVO;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.report.ultimate.biz.BaseOrganizationBiz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/12/4 17:12
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CompanyReportOfOperationControllerTest {

  @Autowired private CompanyReportOfOperationController companyReportOfOperationController;
  @Autowired private DiscountController discountController;
  @Autowired private BaseOrganizationBiz organizationBiz;

  @Test
  public void findList() {
    EmployeeWorkloadQuery query = new EmployeeWorkloadQuery();
    query.setOrgId(35);
    query.setDateType((byte) 0);
    query.setQueryDate("2020-12");
    query.setEmployeeIds(new Integer[] {526});
    ResponseResult<PageInfo<EmployeeWorkloadOfOperationVO>> result =
        companyReportOfOperationController.employeeWorkloadListOfOperation(query);
    System.out.println(result);
  }

  @Test
  public void find() {
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setOrgIds(new Integer[] {35, 42, 72});
    query.setDateType((byte) 0);
    query.setStartDate("2020-07-01");
    query.setEndDate("2020-12-12");
    ResponseResult<ClinicDataStatisticsInfoVO> result =
        companyReportOfOperationController.operationalAnalysisDataStatistics(query);
    System.out.println(result);
  }

  @Test
  public void find1() {
    VisitAndRemindCompletedInfoQuery query = new VisitAndRemindCompletedInfoQuery();
    query.setBusinessType((byte) 0);
    query.setOrgId(35);
    query.setStartDate("2020-10");
    query.setEndDate("2020-12");
    ResponseResult<VisitAndRemindCompletedInfoVO> result =
        companyReportOfOperationController.visitAndRemindCompletedInfo(query);
    System.out.println(result);
  }

  @Test
  public void find2() {
    OperationDataComplexQuery query = new OperationDataComplexQuery();
    query.setOrgId(35);
    query.setStartDate("2021-01");
    query.setEndDate("2021-01");
    ResponseResult<List<OperationDataComplexInfoVO>> result =
        companyReportOfOperationController.operationDataComplexInfo(query);
    System.out.println(result);
  }

  @Test
  public void find3() {
    PatientFirstTreatOriginQuery query = new PatientFirstTreatOriginQuery();
    query.setOrgId(35);
    query.setStartDate("2020-12");
    query.setEndDate("2021-08");
    ResponseResult<PatientFirstTreatOriginInfoVO> result =
        companyReportOfOperationController.firstTreatPatientOrigin(query);
    System.out.println(result);
  }

  @Test
  public void find4() {
    String param = "{\"orgIds\":[26,27,36],\"dateType\":1,\"startDate\":\"2021-03\",\"endDate\":\"2021-03\"}";
    DataStatisticsQuery query = JSONObject.parseObject(param,DataStatisticsQuery.class);
    ResponseResult<PageInfo<PatientDataStatisticsVO>> result = companyReportOfOperationController.operationalAnalysisPatientDataList(query);
    System.out.println(JSONObject.toJSON(result));
  }

  @Test
  public void find5() {
    String param = "{\"orgIds\":[26,27,36],\"dateType\":1,\"startDate\":\"2021-03\",\"endDate\":\"2021-03\"}";
    DataStatisticsQuery query = JSONObject.parseObject(param,DataStatisticsQuery.class);
    PatientDataStatisticsVO result =
            organizationBiz.findClinicPatientDataStatistic(query);
    System.out.println(JSONObject.toJSON(result));
  }
}
