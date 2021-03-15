package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.vo.NextVisitingRecordVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 随访记录接口测试
 * @author: LHB
 * @create: 2020-08-24 11:01
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class VisitingRecordMapperTest {
  @Autowired private VisitingRecordMapper visitingRecordMapper;

  @Test
  public void findVisitingRecordByIdTest() {
    VisitingRecordVo visitingRecordVo = visitingRecordMapper.findVisitingRecordById(3);
    System.out.println(visitingRecordVo.toString());
  }

  @Test
  public void deleteById() {
    visitingRecordMapper.deleteVisitingRecordByTreatmentId(1);
  }

  @Test
  public void find() {
    ArrayList<Integer> integers = new ArrayList<>();
    integers.add(78307);
    integers.add(78309);
    List<NextVisitingRecordVo> vos =
        visitingRecordMapper.countNextVisitingListByIds(integers, "2021-03-15");
    System.out.println(vos);
  }
}
