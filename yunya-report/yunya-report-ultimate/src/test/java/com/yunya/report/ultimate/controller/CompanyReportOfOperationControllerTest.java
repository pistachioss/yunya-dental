package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.EmployeeWorkloadQuery;
import com.yunya.feign.report.domain.vo.EmployeeWorkloadOfOperationVO;
import com.yunya.framework.common.model.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    query.setQueryDate("2020-11");
    ResponseResult<PageInfo<EmployeeWorkloadOfOperationVO>> result =
        companyReportOfOperationController.employeeWorkloadListOfOperation(query);
    System.out.println(result);
  }
}
