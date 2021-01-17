package com.yunya.modules.treatment.mapper;

import com.yunya.feign.report.domain.query.SpecialistProjectCompletedCountQuery;
import com.yunya.feign.treatment.domain.query.SpecialistProjectTariffCompletedInfoQuery;
import com.yunya.feign.treatment.domain.vo.SpecialistTariffCompletedDetailVO;
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
 * @date: 2020/12/29 19:47
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderDetailMapperTest {
  /***/

  @Autowired private OrderDetailMapper orderDetailMapper;

  @Test
  public void find() {
    SpecialistProjectTariffCompletedInfoQuery query =
        new SpecialistProjectTariffCompletedInfoQuery();
    query.setDateType((byte) 0);
    query.setQueryDate("2020-12");
    query.setTariffIds(new String[] {"570"});
    query.setBelongIds(new Integer[] {35, 42});
    Integer integer = orderDetailMapper.selectSpecialistProjectTariffCompletedAmount(query);
    System.out.println(integer);
  }

  @Test
  public void find1() {
    SpecialistProjectTariffCompletedInfoQuery query =
        new SpecialistProjectTariffCompletedInfoQuery();
    query.setDateType((byte) 0);
    query.setQueryDate("2020-12");
    query.setTariffIds(new String[] {"570", "565"});
    query.setBelongIds(new Integer[] {35, 42});
    List<SpecialistTariffCompletedDetailVO> vos =
        orderDetailMapper.selectSpecialistProjectTariffDetail(query);
    System.out.println(vos);
  }

  @Test
  public void find2() {
    SpecialistProjectCompletedCountQuery query = new SpecialistProjectCompletedCountQuery();
    query.setOrgId(35);
    query.setTariffIds(new String[] {"565"});
    query.setStartDate("2020-10");
    query.setEndDate("2020-12");
    Integer count = orderDetailMapper.selectSpecialistProjectCompletedCount(query);
    System.out.println(count);
  }
}
