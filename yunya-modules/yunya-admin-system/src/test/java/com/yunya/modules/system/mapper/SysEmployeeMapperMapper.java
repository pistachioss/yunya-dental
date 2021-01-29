package com.yunya.modules.system.mapper;

import com.yunya.models.system.SysEmployee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/7/22 15:17
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SysEmployeeMapperMapper {
  /** 注入对象 */
  @Autowired private SysEmployeeMapper sysEmployeeMapper;

  @Test
  public void test() {
    SysEmployee sysEmployee = sysEmployeeMapper.selectByUserId(533);
    System.out.println(sysEmployee);
  }
}
