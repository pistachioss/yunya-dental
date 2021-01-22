package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.vo.PatientInfoVO;
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
 * @date: 2021/1/22 15:52
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BasePatientMapperTest {

  @Autowired private BasePatientMapper patientMapper;

  @Test
  public void find() {
    List<PatientInfoVO> list = patientMapper.selectPatientInfoByExample("wang");
    System.out.println(list);
  }
}
