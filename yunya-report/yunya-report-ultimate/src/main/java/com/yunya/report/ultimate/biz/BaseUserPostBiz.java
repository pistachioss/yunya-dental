package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.ClinicPerformanceBusinessQuery;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.EmployeeDiagnosisQuery;
import com.yunya.feign.report.domain.query.EmployeeMatchingRecordQuery;
import com.yunya.feign.report.domain.vo.EmployeeDiagnosisInfoVO;
import com.yunya.feign.treatment.domain.vo.AssistantMatchingStatisticsVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseOrganization;
import com.yunya.models.report.BaseUserPost;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import com.yunya.report.ultimate.mapper.BaseUserPostMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 简介: 员工可登录组织
 *
 * @author: chow
 * @date: 2020/12/3 13:24
 * @description:
 * @since: 1.0.0
 */
@Service
@Slf4j
public class BaseUserPostBiz extends BaseBiz<BaseUserPostMapper, BaseUserPost> {

  /** 组织 */
  @Autowired private BaseOrganizationMapper organizationMapper;

  @Resource(name = "customizeThreadPool")
  private ExecutorService executorService;

  /**
   * 根据条件查询配诊统计列表
   *
   * @param query 查询条件
   * @return PageInfo<AssistantMatchingStatisticsVO>
   */
  public PageInfo<AssistantMatchingStatisticsVO> findTreatMatchingStatisticsList(
      EmployeeMatchingRecordQuery query) throws InterruptedException, ExecutionException {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<AssistantMatchingStatisticsVO> resultList =
        mapper.selectAssistantMatchingStatisticsByAssistant("assistant_1", query);
    CountDownLatch latch_1 = new CountDownLatch(2);

    List<AssistantMatchingStatisticsVO> resultList2 = this.getAssistant2List(query, latch_1);
    List<AssistantMatchingStatisticsVO> resultList3 = this.getAssistant3List(query, latch_1);

    latch_1.await();

    if (StringHelper.isNotEmpty(resultList)) {
      for (AssistantMatchingStatisticsVO assistant1 : resultList) {
        Integer orgId = assistant1.getOrgId();
        Integer assistantId = assistant1.getAssistantId();
        if (StringHelper.isNotEmpty(resultList2)) {
          resultList2.forEach(
              assistant2 -> {
                if (assistant2.getAssistantId().equals(assistantId)
                    && orgId.equals(assistant2.getOrgId())) {
                  assistant1.setTreatMatchingActualWorkloadAsAssistant2(
                      assistant2.getTreatMatchingActualWorkloadAsAssistant1());
                  assistant1.setTreatMatchingRefundWorkloadAsAssistant2(
                      assistant2.getTreatMatchingRefundWorkloadAsAssistant1());
                  assistant1.setTreatMatchingTimeAsAssistant2(
                      assistant2.getTreatMatchingTimeAsAssistant1());
                }
              });
        }
        if (StringHelper.isNotEmpty(resultList3)) {
          resultList3.forEach(
              assistant3 -> {
                if (assistant3.getAssistantId().equals(assistantId)
                    && orgId.equals(assistant3.getOrgId())) {
                  assistant1.setTreatMatchingActualWorkloadAsAssistant3(
                      assistant3.getTreatMatchingActualWorkloadAsAssistant1());
                  assistant1.setTreatMatchingRefundWorkloadAsAssistant3(
                      assistant3.getTreatMatchingRefundWorkloadAsAssistant1());
                  assistant1.setTreatMatchingTimeAsAssistant3(
                      assistant3.getTreatMatchingTimeAsAssistant1());
                }
              });
        }
      }
    }
    for(AssistantMatchingStatisticsVO vo:resultList){
      List<Integer>olist = new ArrayList<>();
      olist.add(vo.getOrgId());
      vo.setOrgIds(olist);
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 查询助手2信息
   *
   * @param query
   * @param latch
   * @return
   */
  private List<AssistantMatchingStatisticsVO> getAssistant2List(
      EmployeeMatchingRecordQuery query, CountDownLatch latch)
      throws ExecutionException, InterruptedException {
    List<AssistantMatchingStatisticsVO> result;
    try {
      Future<List<AssistantMatchingStatisticsVO>> assistant_2 =
          executorService.submit(
              () -> mapper.selectAssistantMatchingStatisticsByAssistant("assistant_2", query));
      result = getFutureObj(assistant_2);
    } finally {
      latch.countDown();
    }
    return result;
  }

  /**
   * 查询助手3信息
   *
   * @param query
   * @param latch
   * @return
   */
  private List<AssistantMatchingStatisticsVO> getAssistant3List(
      EmployeeMatchingRecordQuery query, CountDownLatch latch)
      throws ExecutionException, InterruptedException {
    List<AssistantMatchingStatisticsVO> result;
    try {
      Future<List<AssistantMatchingStatisticsVO>> assistant_3 =
          executorService.submit(
              () -> mapper.selectAssistantMatchingStatisticsByAssistant("assistant_3", query));
      result = getFutureObj(assistant_3);
    } finally {
      latch.countDown();
    }
    return result;
  }

  /**
   * 获取线程结果
   *
   * @param futureList
   * @return
   * @throws ExecutionException
   * @throws InterruptedException
   */
  private List<AssistantMatchingStatisticsVO> getFutureObj(
      Future<List<AssistantMatchingStatisticsVO>> futureList)
      throws ExecutionException, InterruptedException {
    List<AssistantMatchingStatisticsVO> assistantMatchingStatisticsVOS = futureList.get();
    return assistantMatchingStatisticsVOS == null
        ? new ArrayList<>()
        : assistantMatchingStatisticsVOS;
  }

  /**
   * 根据条件导出助手配诊统计列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeeMatchingStatisticsList(
      HttpServletResponse response, EmployeeMatchingRecordQuery query)
      throws IOException, InterruptedException, ExecutionException {
    query.setWhetherPage(false);
    PageInfo<AssistantMatchingStatisticsVO> pageInfo = findTreatMatchingStatisticsList(query);
    List<AssistantMatchingStatisticsVO> list = pageInfo.getList();
    ExcelUtil<AssistantMatchingStatisticsVO> excelUtil =
        new ExcelUtil<>(AssistantMatchingStatisticsVO.class);
    String fileName = query.getStartDate() + query.getEndDate() + "助手配诊统计";

    ClinicPerformanceBusinessQuery dataStatisticsQuery = new ClinicPerformanceBusinessQuery();
    dataStatisticsQuery.setOrgIds(query.getOrgIds());
    List<BaseOrganization>orgList = organizationMapper.selectOrganizationList(dataStatisticsQuery);
//    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (orgList.size()>0) {
      for(AssistantMatchingStatisticsVO vo:list){
        BaseOrganization organization = orgList.stream().filter(item -> item.getOrgId().equals(vo.getOrgId())).findFirst().get();
        String abbreviation = organization.getAbbreviation();
        fileName = abbreviation + fileName;
        vo.setOrgName(fileName);
      }
//      list.forEach(vo -> vo.setOrgName(abbreviation));
    }
    excelUtil.exportExcel(response, list, "员工配诊记录列表", fileName);
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
    String fileName = query.getStartDate() + "-" + query.getEndDate() + "员工看诊情况统计";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, resultList, "员工看诊情况列表", fileName);
  }
}
