package com.yunya.modules.emr.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.emr.MedicalOrthodonticsRecord;
import com.yunya.modules.emr.mapper.MedicalOrthodonticsRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class MedicalOrthodonticsRecordBiz extends BaseBiz<MedicalOrthodonticsRecordMapper, MedicalOrthodonticsRecord> {

  public int create(MedicalOrthodonticsRecord medicalOrthodonticsRecord){
    return mapper.insert(medicalOrthodonticsRecord);
  }

  public List<MedicalOrthodonticsRecord> selectByEntity(MedicalOrthodonticsRecord medicalOrthodonticsRecord){
        return mapper.selectByEntity(medicalOrthodonticsRecord);
  }

  public List<Integer> listOrthodonticsPatient() {
    Example example = new Example(MedicalOrthodonticsRecord.class);
    example.selectProperties("patientId");
    List<MedicalOrthodonticsRecord> list = mapper.selectByExample(example);
    return list.stream().filter(t -> Objects.nonNull(t.getPatientId())).map(MedicalOrthodonticsRecord::getPatientId).collect(Collectors.toList());
  }

}
