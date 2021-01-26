package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.vo.OrderProcessVO;
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
 * @date: 2021/1/26 13:56
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderRecordMapperTest {
  /***/
  @Autowired private OrderRecordMapper orderRecordMapper;

  @Test
  public void find() {
    List<OrderProcessVO> vos = orderRecordMapper.selectOrderProcess("", new Integer[] {25, 26});
    System.out.println(vos);
  }
}
