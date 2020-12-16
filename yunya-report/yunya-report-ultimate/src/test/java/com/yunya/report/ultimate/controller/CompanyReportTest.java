package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介: 公司端报表测试
 *
 * @author: chow
 * @date: 2020/10/26 20:25
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CompanyReportTest {
  @Autowired private CompanyReportOfDataRecordController companyReportOfDataRecordController;

  @Autowired private CompanyReportOfPersonnelController personnelController;

  @Autowired private CompanyReportOfCustomerCentralController customerCentralController;

  @Autowired private CompanyReportOfFinanceController financeController;

  @Test
  public void findTreatList() {
    TreatmentRecordQuery query = new TreatmentRecordQuery();
    query.setOrgId(35);
    ResponseResult<PageInfo<TreatmentRecordReportVO>> treatmentList =
        companyReportOfDataRecordController.findTreatmentList(query);
    System.out.println(treatmentList);
  }

  @Test
  public void findOrderList() {
    OrderRecordQuery query = new OrderRecordQuery();
    query.setOrgId(35);
    ResponseResult<PageInfo<BillOfOrderRecordVO>> result =
        companyReportOfDataRecordController.findBillRecordOfOrderList(query);
    System.out.println(result);
  }

  /** 员工实收工作量明细列表 */
  @Test
  public void findEmpWorkload() {
    EmployeePersonalWorkloadDetailQuery query = new EmployeePersonalWorkloadDetailQuery();
    query.setOrgId(35);
    query.setDateType((byte) 0);
    query.setQueryDate("2020-12");
    query.setEmployeeId(526);
    query.setKeyword("三七");
    ResponseResult<PageInfo<EmployeePersonalActualWorkloadDetailVO>> list =
        personnelController.findEmployeePersonalActualWorkloadDetailList(query);
    System.out.println(list);
  }

  /** 员工已收工作量明细列表 */
  @Test
  public void findReceivedWorkload() {
    EmployeePersonalWorkloadDetailQuery query = new EmployeePersonalWorkloadDetailQuery();
    query.setOrgId(35);
    query.setDateType((byte) 1);
    query.setQueryDate("2020");
    query.setEmployeeId(526);
    query.setKeyword("三七");
    ResponseResult<PageInfo<EmployeePersonalReceivedWorkloadDetailVO>> list =
        personnelController.findEmployeePersonalReceivedWorkloadDetailList(query);
    System.out.println(list);
  }

  @Test
  public void find() {
    DentistArrearsDetailQuery query = new DentistArrearsDetailQuery();
    query.setDentistId(526);
    query.setBillStartDate("2020-11-17");
    query.setBillEndDate("2020-11-17");
    query.setKeyword("王一博");
    ResponseResult<PageInfo<DentistArrearsDetailVO>> result =
        customerCentralController.dentistArrearsDetailList(query);
    System.out.println(result);
  }

  @Test
  public void find1() {
    ResponseResult<List<BaseAccountItemVO>> list = financeController.findAllPaymentList();
    System.out.println(list);
  }

  @Test
  public void find2() {
    InboundAndOutboundStatementQuery query = new InboundAndOutboundStatementQuery();
    query.setOrgId(141);
    query.setDateType((byte) 0);
    query.setStartDate("2020-12-16");
    query.setEndDate("2020-12-18");
    ResponseResult<List<ClinicInboundAndOutboundVO>> result =
        financeController.inboundAndOutboundStatement(query);
    System.out.println(result);
  }
}
