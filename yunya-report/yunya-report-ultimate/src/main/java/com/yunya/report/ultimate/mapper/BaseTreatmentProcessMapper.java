package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.TreatmentMatchingRecordQuery;
import com.yunya.feign.report.domain.query.TreatmentRecordQuery;
import com.yunya.feign.report.domain.vo.TreatmentMatchingRecordVO;
import com.yunya.feign.report.domain.vo.TreatmentRecordReportVO;
import com.yunya.models.report.BaseTreatmentProcess;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseTreatmentProcessMapper extends Mapper<BaseTreatmentProcess> {

  /**
   * 根据条件查询就诊列表
   *
   * @param query 查询条件
   * @return
   */
  List<TreatmentRecordReportVO> selectTreatmentRecordReportVOList(
      @Param("query") TreatmentRecordQuery query);

  /**
   * 根据条件查询就诊配诊记录列表
   *
   * @param query 查询条件
   *
   * @return
   */
  List<TreatmentMatchingRecordVO> selectTreatmentMatchingRecord(
      @Param("query") TreatmentMatchingRecordQuery query);
}
