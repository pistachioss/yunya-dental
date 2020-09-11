package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.query.TreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.feign.treatment.domain.vo.TreatmentPatientInfoVO;
import com.yunya.models.treatment.TreatmentRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TreatmentRecordMapper extends Mapper<TreatmentRecord> {

  /**
   * 根据条件查询就诊中患者信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<TreatmentPatientInfoVO> selectTreatingList(
      @Param("queryForm") TreatmentRecordQueryForm queryForm);

  /**
   * 根据条件查询患者就诊记录列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<PatientTreatmentRecordVO> selectPatientTreatmentRecordList(
      @Param("queryForm") PatientTreatmentRecordQueryForm queryForm);
}
