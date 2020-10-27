package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.TreatmentRecordQuery;
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
}
