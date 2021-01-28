package com.yunya.modules.treatment.mapper;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.treatment.domain.query.CompletedWorkGoalQuery;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.query.TreatmentCountQuery;
import com.yunya.feign.treatment.domain.vo.CountTreatmentRecordVO;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.models.treatment.AssistantMatchingRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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

  @Autowired private AssistantMatchingRecordMapper assistantMatchingRecordMapper;

  @Test
  public void findPatientTreatList() {
    PatientTreatmentRecordQueryForm queryForm = new PatientTreatmentRecordQueryForm();
    //queryForm.setOrgIds(new Integer[] {21});

    //queryForm.setTreatStatus(new Byte[] {0, 2});
    queryForm.setPatientId(13282);
    List<PatientTreatmentRecordVO> list =
        treatmentRecordMapper.selectPatientTreatmentRecordList(queryForm);
    System.out.println(list);
  }

  @Test
  public void find() {
    CompletedWorkGoalQuery query = new CompletedWorkGoalQuery();
    query.setDateType((byte) 0);
    query.setBusinessDate("2020-12");
    query.setBelongIds(new Integer[] {35, 72});
    Integer integer = treatmentRecordMapper.selectCompletedTreatPerTimes(query);
    System.out.println(integer);
  }

  @Test
  public void find1() {
    CompletedWorkGoalQuery query = new CompletedWorkGoalQuery();
    query.setDateType((byte) 0);
    query.setBusinessDate("2020-12");
    query.setBelongIds(new Integer[] {35, 72});
    Integer integer = treatmentRecordMapper.selectCompletedFirstTreatPerNum(query);
    System.out.println(integer);
  }

  @Test
  public void find3() {
    AssistantMatchingRecord assistantMatchRecord = new AssistantMatchingRecord();
    assistantMatchRecord.setTreatmentRecordId(null);
    List<AssistantMatchingRecord> matchingRecords =
        assistantMatchingRecordMapper.select(assistantMatchRecord);
    System.out.println(JSONObject.toJSONString(matchingRecords));
  }

  @Test
  public void count() {
    System.out.println(treatmentRecordMapper.countByPatientId(3414));
  }

  @Test
  public void count1() {
    TreatmentCountQuery query = new TreatmentCountQuery();
    //query.setDentistId(559);
    query.setOrgId(32);
    query.setQueryDate("2021-01-25");
    CountTreatmentRecordVO vo = treatmentRecordMapper.selectTreatCountByExample(query);
    System.out.println(vo);
  }
}
