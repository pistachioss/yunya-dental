package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.EmployeeDiagnosisQuery;
import com.yunya.feign.report.domain.query.EmployeeMatchingRecordQuery;
import com.yunya.feign.report.domain.vo.EmployeeDiagnosisInfoVO;
import com.yunya.feign.treatment.domain.vo.AssistantMatchingStatisticsVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseUserPost;
import com.yunya.report.ultimate.mapper.BaseUserPostMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简介: 员工可登录组织
 *
 * @author: chow
 * @date: 2020/12/3 13:24
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseUserPostBiz extends BaseBiz<BaseUserPostMapper, BaseUserPost> {
  /**
   * 根据条件查询配诊统计列表
   *
   * @param query 查询条件
   * @return PageInfo<AssistantMatchingStatisticsVO>
   */
  public PageInfo<AssistantMatchingStatisticsVO> findTreatMatchingStatisticsList(
      EmployeeMatchingRecordQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<AssistantMatchingStatisticsVO> resultList =
        mapper.selectAssistantMatchingStatisticsList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出助手配诊统计列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeeMatchingStatisticsList(
      HttpServletResponse response, EmployeeMatchingRecordQuery query) throws IOException {
    PageInfo<AssistantMatchingStatisticsVO> pageInfo = findTreatMatchingStatisticsList(query);
    List<AssistantMatchingStatisticsVO> list = pageInfo.getList();
    ExcelUtil<AssistantMatchingStatisticsVO> excelUtil =
        new ExcelUtil<>(AssistantMatchingStatisticsVO.class);
    excelUtil.exportExcel(response, list, "员工配诊记录列表");
  }

  /**
   * 根据条件查询员工看诊情况列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeDiagnosisInfoVO> 员工看诊情况分页列表
   */
  public PageInfo<EmployeeDiagnosisInfoVO> findEmployeeDiagnosisInfoList(
      EmployeeDiagnosisQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeDiagnosisInfoVO> resultList = mapper.selectEmployeeDiagnosisInfoList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出员工看诊情况列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeeDiagnosisInfoList(
      HttpServletResponse response, EmployeeDiagnosisQuery query) throws IOException {
    List<EmployeeDiagnosisInfoVO> resultList = mapper.selectEmployeeDiagnosisInfoList(query);
    ExcelUtil<EmployeeDiagnosisInfoVO> excelUtil = new ExcelUtil<>(EmployeeDiagnosisInfoVO.class);
    excelUtil.exportExcel(response, resultList, "员工看诊情况列表");
  }
}
