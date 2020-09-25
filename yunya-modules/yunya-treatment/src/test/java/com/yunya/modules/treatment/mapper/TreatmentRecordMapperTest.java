package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.models.treatment.TreatmentRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 简介: 就诊记录mapper测试
 *
 * @author: chow
 * @date: 2020/9/11 13:27
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TreatmentRecordMapperTest {
  /** 注入对象 */
  @Autowired private TreatmentRecordMapper treatmentRecordMapper;

  @Test
  public void findPatientTreatList() {
    PatientTreatmentRecordQueryForm queryForm = new PatientTreatmentRecordQueryForm();
    queryForm.setOrgIds(new Integer[] {21});

    queryForm.setTreatStatus(new Byte[] {0, 2});
    queryForm.setPatientId(11);
    List<PatientTreatmentRecordVO> list =
        treatmentRecordMapper.selectPatientTreatmentRecordList(queryForm);
    System.out.println(list);
  }

  @Test
  public void testFindList() {
    Set<Integer> ids = new HashSet<>();
    ids.add(917);
    ids.add(918);
    ids.add(919);
    ids.add(920);
    ids.add(921);
    ids.add(1);
    List<TreatmentRecord> records = treatmentRecordMapper.selectByIds(ids);
    System.out.println(records);
  }
}
