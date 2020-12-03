package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.EmployeeMatchingRecordQuery;
import com.yunya.feign.treatment.domain.vo.AssistantMatchingStatisticsVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介: 助手配诊统计
 *
 * @author: chow
 * @date: 2020/12/3 13:45
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseUserPostMapperTest {
  /** 助手配诊 */
  @Autowired private BaseUserPostMapper baseUserPostMapper;

  @Test
  public void findMatchingList() {
    EmployeeMatchingRecordQuery query = new EmployeeMatchingRecordQuery();
    query.setStartDate("2020-10-01");
    query.setEndDate("2020-11-30");
    query.setOrgId(35);
    query.setAssistantIds(new Integer[]{573});
    List<AssistantMatchingStatisticsVO> vos =
        baseUserPostMapper.selectAssistantMatchingStatisticsListByDay(query);
    System.out.println(vos);
  }
}
