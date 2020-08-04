package com.yunya.modules.emr.mapper;

import com.yunya.models.emr.MedicalCommonRecord;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MedicalCommonRecordMapper extends Mapper<MedicalCommonRecord> {
  List<MedicalCommonRecord> findList(MedicalCommonRecord model);

  int insertMedical(MedicalCommonRecord model);

}