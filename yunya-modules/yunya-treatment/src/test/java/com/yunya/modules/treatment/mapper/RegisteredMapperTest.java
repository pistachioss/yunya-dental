package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.RegisteredQueryForm;
import com.yunya.feign.treatment.domain.vo.WaitingPatientInfoVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介: 挂号mapper层测试
 *
 * @author: chow
 * @date: 2020/8/12 13:44
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RegisteredMapperTest {
  /** 注入对象 */
  @Autowired private RegisteredMapper registeredMapper;

  @Test
  public void find() {
    List<WaitingPatientInfoVO> vos =
        registeredMapper.selectRegisteredList((byte) 0, new RegisteredQueryForm());
    System.out.println(vos);
  }
}
