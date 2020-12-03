package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.EmployeeMatchingDetailQuery;
import com.yunya.feign.report.domain.vo.EmployeeTreatMatchingDetailVO;
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
 * @date: 2020/12/3 16:54
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTreatmentProcessMapperTest {
  @Autowired private BaseTreatmentProcessMapper treatmentProcessMapper;

  @Test
  public void findMatchingDetailList() {
    EmployeeMatchingDetailQuery query = new EmployeeMatchingDetailQuery();
    query.setOrgId(35);
    query.setDateType((byte) 1);
    query.setStartDate("2020-10-01");
    query.setEndDate("2020-11-30");
    query.setAssistantType((byte) 0);
    query.setAssistantId(525);
    query.setKeyword("熊");
    List<EmployeeTreatMatchingDetailVO> vos =
        treatmentProcessMapper.selectAssistantMatchingDetailList(query);
    System.out.println(vos);
  }
}
