package com.yunya.modules.clinic_base.mapper;

import com.yunya.feign.clinic_base.domain.query.WorkGoalQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessWorkGoalVO;
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
 * @date: 2020/12/24 19:51
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BusinessTargetMapperTest {
  /***/
  @Autowired private BusinessTargetMapper businessTargetMapper;

  @Test
  public void find() {
    WorkGoalQuery query = new WorkGoalQuery();
    query.setDateType((byte) 0);
    query.setStartDate("2020-01");
    query.setEndDate("2020-12");
    query.setBelongType((byte) 0);
    query.setBelongIds(new Integer[] {35});
    List<BusinessWorkGoalVO> vos = businessTargetMapper.selectBusinessWorkGoalList(query);
    System.out.println(vos);
  }
}
