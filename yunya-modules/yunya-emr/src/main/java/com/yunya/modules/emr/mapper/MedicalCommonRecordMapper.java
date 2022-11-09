package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.vo.MedicalCommonRecordVO;
import com.yunya.models.emr.MedicalCommonRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MedicalCommonRecordMapper extends Mapper<MedicalCommonRecord> {
  List<MedicalCommonRecord> findList(MedicalCommonRecord model);

  int insertMedical(MedicalCommonRecord model);

  MedicalCommonRecord selectMedicalIllnessHistoryById(@Param("id") Integer id);

  /**
   * 根据就诊id查询普通电子病历
   *
   * @param treatmentId
   * @return
   */
  MedicalCommonRecordVO selectOneByTreatmentId(@Param("treatmentId") Integer treatmentId);
}