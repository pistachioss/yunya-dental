package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.query.TreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.models.treatment.TreatmentRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
   * 根据就诊记录ID查询就诊信息
   *
   * @param id 就诊记录ID
   * @return
   */
  TreatmentRecordVO selectTreatmentInfoById(@Param("id") Integer id);

  /**
   * 根据条件查询患者就诊记录列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<PatientTreatmentRecordVO> selectPatientTreatmentRecordList(
      @Param("queryForm") PatientTreatmentRecordQueryForm queryForm);

  /**
   * 根据就诊记录ID列表查询就诊记录列表
   *
   * @param ids 就诊记录ID
   * @return
   */
  List<TreatmentRecord> selectByIds(@Param("ids") Set<Integer> ids);

  /**
   * 根据挂号ID查询就诊数量
   *
   * @param regId 跟据挂号ID查询就诊记录数量
   * @return int
   */
  int selectCountByRegisteredId(@Param("regId") Integer regId);

  /**
   * 根据患者ID查询末次就诊信息
   *
   * @param patientId 患者ID
   * @return 末次就诊信息
   */
  LastTreatmentInfoVO lastTreatmentInfo(@Param("patientId") Integer patientId);

  /**
   * 查询指定时间段内每个医生每天患者就诊人数
   * @param dentistId 医生ID
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @return 实体列表
   */
  List<TreatmentInfoForMonthVO> treatInfoForMonth(
          @Param("dentistId") Integer dentistId,
          @Param("startDate") Date startDate,
          @Param("endDate") Date endDate);
}
