package com.yunya.modules.emr.mapper;

import com.yunya.models.emr.MedicalOrthodonticsRecord;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MedicalOrthodonticsRecordMapper extends Mapper<MedicalOrthodonticsRecord> {

   List<MedicalOrthodonticsRecord> selectByEntity(MedicalOrthodonticsRecord medicalOrthodonticsRecord);

}