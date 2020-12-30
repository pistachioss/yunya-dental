package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.EmployeeDiagnosisQuery;
import com.yunya.feign.report.domain.query.EmployeeMatchingRecordQuery;
import com.yunya.feign.report.domain.vo.EmployeeDiagnosisInfoVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.domain.vo.AssistantMatchingStatisticsVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseUserPost;
import com.yunya.report.ultimate.mapper.BaseUserPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
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
  @Autowired
  private RemoteSystemServiceFeign remoteSystemServiceFeign;

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
        mapper.selectAssistantMatchingStatisticsByAssistant("assistant_1", query);
    List<AssistantMatchingStatisticsVO> resultList2 =
            mapper.selectAssistantMatchingStatisticsByAssistant("assistant_2", query);
    List<AssistantMatchingStatisticsVO> resultList3 =
            mapper.selectAssistantMatchingStatisticsByAssistant("assistant_3", query);
    if (resultList!=null && !resultList.isEmpty()) {
      resultList.forEach(assistant1 -> {
        Integer orgId = assistant1.getOrgId();
        Integer assistantId = assistant1.getAssistantId();
        if (resultList2!=null && !resultList2.isEmpty()) {
          resultList2.forEach(assistant2->{
            if (assistant2.getAssistantId().equals(assistantId) && orgId.equals(assistant2.getOrgId())) {
              assistant1.setTreatMatchingActualWorkloadAsAssistant2(assistant2.getTreatMatchingActualWorkloadAsAssistant1());
              assistant1.setTreatMatchingRefundWorkloadAsAssistant2(assistant2.getTreatMatchingRefundWorkloadAsAssistant1());
              assistant1.setTreatMatchingTimeAsAssistant2(assistant2.getTreatMatchingTimeAsAssistant1());
            }
          });
        }
        if (resultList3!=null && !resultList3.isEmpty()) {
          resultList3.forEach(assistant3->{
            if (assistant3.getAssistantId().equals(assistantId) && orgId.equals(assistant3.getOrgId())) {
              assistant1.setTreatMatchingActualWorkloadAsAssistant3(assistant3.getTreatMatchingActualWorkloadAsAssistant1());
              assistant1.setTreatMatchingRefundWorkloadAsAssistant3(assistant3.getTreatMatchingRefundWorkloadAsAssistant1());
              assistant1.setTreatMatchingTimeAsAssistant3(assistant3.getTreatMatchingTimeAsAssistant1());
            }
          });
        }
      });
    }
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
    String fileName = getFileName(query.getOrgId(), query.getStartDate(), query.getEndDate(), "助手配诊统计");
    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");
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
    String fileName = getFileName(query.getOrgId(), query.getStartDate(), query.getEndDate(), "看诊情况统计");
    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");
    excelUtil.exportExcel(response, resultList, "员工看诊情况列表");
  }

  /**
   * 获取文件名
   *
   * @param orgId
   * @param sDate
   * @param eDate
   * @return
   */
  private String getFileName(Integer orgId, String sDate, String eDate, String tail) {
    OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
    StringBuilder res = new StringBuilder();
    String[] str = sDate.split("-");
    if (str.length == 1) {//年
      res.append(sDate).append("年")
        .append(organizationInfo.getAbbreviation());
    } else if (str.length == 2) {//月
      res.append(str[0]).append("年").append(str[1]).append("月")
              .append(organizationInfo.getAbbreviation());
    } else if (str.length == 3) {//日
      res.append(organizationInfo.getAbbreviation());
      for (int i = 0; i < str.length; i++) {
        if (i>0 && res.length() > 0) {
          res.append(".");
        }
        res.append(str[i]);
      }
      str = eDate.split("-");
      res.append("-");
      for (int i = 0; i < str.length; i++) {
        if (i>0 && res.length() > 0) {
          res.append(".");
        }
        res.append(str[i]);
      }
    }
    res.append(tail);
    return res.toString();
  }
}
