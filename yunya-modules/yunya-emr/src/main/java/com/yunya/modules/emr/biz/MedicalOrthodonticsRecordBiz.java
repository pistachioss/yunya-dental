package com.yunya.modules.emr.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.emr.MedicalOrthodonticsRecord;
import com.yunya.modules.emr.mapper.MedicalOrthodonticsRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class MedicalOrthodonticsRecordBiz extends BaseBiz<MedicalOrthodonticsRecordMapper, MedicalOrthodonticsRecord> {

  public int create(MedicalOrthodonticsRecord medicalOrthodonticsRecord){
    return mapper.insert(medicalOrthodonticsRecord);
  }

  public List<MedicalOrthodonticsRecord> selectByEntity(MedicalOrthodonticsRecord medicalOrthodonticsRecord){
        return mapper.selectByEntity(medicalOrthodonticsRecord);
  }

}
