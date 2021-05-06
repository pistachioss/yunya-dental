package com.yunya.report.ultimate.mapper;
import com.yunya.feign.patient_central.domain.query.PatientSearchQuery;
import com.yunya.feign.report.domain.query.PatientReportQueryForm;
import com.yunya.feign.report.domain.vo.BasePatientNotSeenVo;
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
    PatientSearchQuery query = new PatientSearchQuery();
    query.setPatientKeyWord("wang");
    List<PatientInfoVO> list = patientMapper.selectPatientInfoByExample(query);
    System.out.println(list);
  }

  @Test
  public void find1() {
    PatientReportQueryForm query = new PatientReportQueryForm();
    query.setOrgId(26);
    query.setStartDate("2021-03-01");
    query.setEndDate("2021-04-15");
    List<BasePatientNotSeenVo> vos = patientMapper.selectNotSeenList(query);
    System.out.println(vos);
  }
}
