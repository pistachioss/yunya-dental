package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.bo.ClinicDataStatisticsInfoVO;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.EmployeeWorkloadQuery;
import com.yunya.feign.report.domain.query.OperationDataComplexQuery;
import com.yunya.feign.report.domain.query.VisitAndRemindCompletedInfoQuery;
import com.yunya.feign.report.domain.vo.EmployeeWorkloadOfOperationVO;
import com.yunya.feign.report.domain.vo.OperationDataComplexInfoVO;
import com.yunya.feign.report.domain.vo.VisitAndRemindCompletedInfoVO;
import com.yunya.framework.common.model.ResponseResult;
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
}
