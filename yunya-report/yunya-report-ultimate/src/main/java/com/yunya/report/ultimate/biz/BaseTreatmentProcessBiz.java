package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.TreatmentMatchingRecordQuery;
import com.yunya.feign.report.domain.query.TreatmentRecordQuery;
import com.yunya.feign.report.domain.vo.TreatmentMatchingRecordVO;
import com.yunya.feign.report.domain.vo.TreatmentRecordReportVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseTreatmentProcess;
import com.yunya.report.ultimate.mapper.BaseTreatmentProcessMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简介: 就诊流程业务层
 *
 * @author: chow
 * @date: 2020/10/26 15:46
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseTreatmentProcessBiz
    extends BaseBiz<BaseTreatmentProcessMapper, BaseTreatmentProcess> {

  /**
   * 根据条件查询就诊记录报表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<TreatmentRecordReportVO> findTreatmentList(TreatmentRecordQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<TreatmentRecordReportVO> resultList = mapper.selectTreatmentRecordReportVOList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询就诊列表并导出excel
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportTreatmentList(HttpServletResponse response, TreatmentRecordQuery query)
      throws IOException {
    List<TreatmentRecordReportVO> list = mapper.selectTreatmentRecordReportVOList(query);
    ExcelUtil<TreatmentRecordReportVO> excelUtil = new ExcelUtil<>(TreatmentRecordReportVO.class);
    excelUtil.exportExcel(response, list, "患者就诊记录");
  }

  /**
   * 根据条件查询就诊配诊记录列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<TreatmentMatchingRecordVO> findTreatmentMatchingRecord(
      TreatmentMatchingRecordQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<TreatmentMatchingRecordVO> resultList = mapper.selectTreatmentMatchingRecord(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出配诊记录列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportTreatmentMatchingRecord(
      HttpServletResponse response, TreatmentMatchingRecordQuery query) throws IOException {
    List<TreatmentMatchingRecordVO> list = mapper.selectTreatmentMatchingRecord(query);
    ExcelUtil<TreatmentMatchingRecordVO> excelUtil =
        new ExcelUtil<>(TreatmentMatchingRecordVO.class);
    excelUtil.exportExcel(response, list, "配诊记录表");
  }
}
