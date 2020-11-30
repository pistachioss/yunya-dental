package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.EmployeePersonalWorkloadDetailQuery;
import com.yunya.feign.report.domain.query.OrderRecordQuery;
import com.yunya.feign.report.domain.query.TreatmentRecordQuery;
import com.yunya.feign.report.domain.vo.BillOfOrderRecordVO;
import com.yunya.feign.report.domain.vo.EmployeePersonalActualWorkloadDetailVO;
import com.yunya.feign.report.domain.vo.TreatmentRecordReportVO;
import com.yunya.framework.common.model.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    EmployeePersonalWorkloadDetailQuery query =
        new EmployeePersonalWorkloadDetailQuery();
    query.setOrgId(35);
    query.setDateType((byte) 0);
    query.setQueryDate("2020-11");
    query.setEmployeeId(570);
    query.setBillNum("ZD00352011300001");
    ResponseResult<PageInfo<EmployeePersonalActualWorkloadDetailVO>> list =
        personnelController.findEmployeePersonalActualWorkloadDetailList(query);
    System.out.println(list);
  }
}
