package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.ArrearsQueryForm;
import com.yunya.feign.report.domain.query.PatientAnalysisQueryForm;
import com.yunya.feign.report.domain.query.PatientReportQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseEmployee;
import com.yunya.models.report.BaseOrganization;
import com.yunya.models.report.BasePatient;
import com.yunya.report.ultimate.mapper.*;
import io.swagger.models.auth.In;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 简介:患者报表业务层
 *
 * @author: WY
 * @date: 2020/10/27 20:13
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientReportBiz extends BaseBiz<BasePatientMapper, BasePatient> {

  /** 员工Mapper */
  @Resource private BaseEmployeeMapper baseEmployeeMapper;

  /** 随访提醒Mapper */
  @Resource private BaseVisitRemindMapper baseVisitRemindMapper;

  /** 订单mapper */
  @Resource private BaseBillMapper baseBillMapper;

  @Resource private BaseOrganizationMapper baseOrganizationMapper;

  /**
   * 查询末诊医生列表
   *
   * @return 末诊医生列表
   */
  public List<BaseEmployee> employeeList(Integer orgId) {
    return baseEmployeeMapper.selectByOrgId(orgId);
  }

  /**
   * 患者报表-未复诊预约且未提醒List
   *
   * @param form 条件
   * @return 未复诊预约且未提醒集合
   */
  public PageInfo<BasePatientNotSeenVo> notSeenList(PatientReportQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<BasePatientNotSeenVo> basePatientNotSeenVoList = mapper.selectNotSeenList(form);
    return new PageInfo<>(basePatientNotSeenVoList);
  }
  public void deleteTrent(Integer treatmentId) {
   mapper.deleteTrent(treatmentId);
  }
  /**
   * 查询并装配下次提醒
   *
   * @param basePatientNotSeenVoList
   * @param orgId
   */
  //  private void assemblyNextRemind(List<BasePatientNotSeenVo> basePatientNotSeenVoList, Integer
  // orgId) {
  //    Map<Integer, Date> patientMap =
  // basePatientNotSeenVoList.stream().collect(Collectors.toMap(BasePatientNotSeenVo::getPatientId,BasePatientNotSeenVo::getLastVisitDate));
  //    List<BaseVisitRemind> baseVisitReminds =
  // baseVisitRemindMapper.findVisitRemindListInPatientId(patientMap.keySet(), 1, orgId);
  //    basePatientNotSeenVoList.forEach(vo->{
  //      Date date = vo.getLastVisitDate();
  //      baseVisitReminds.forEach(remind->{
  //        Date time = remind.getTime();
  //        if (vo.getPatientId().equals(remind.getPatientId())
  //                && time.compareTo(date)>=0) {
  //          vo.setNoticeTime(time);
  //          vo.setNoticeContent(remind.getContent());
  //          return;
  //        }
  //      });
  //    });
  //  }

  /**
   * 导出未复诊预约且未提醒记录列表
   *
   * @param response 导出响应
   * @param form 条件
   */
  public void exportNotSeenList(HttpServletResponse response, PatientReportQueryForm form)
      throws IOException {
    form.setWhetherPage(false);
    PageInfo<BasePatientNotSeenVo> pageInfo = notSeenList(form);
    List<BasePatientNotSeenVo> basePatientNotSeenVoList = pageInfo.getList();
    ExcelUtil<BasePatientNotSeenVo> excelUtil = new ExcelUtil<>(BasePatientNotSeenVo.class);
    if (StringHelper.isNotNull(form.getOrgId())) {
      BaseOrganization baseOrganization = new BaseOrganization();
      baseOrganization.setOrgId(form.getOrgId());
      BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
      if (baseOrganizationv != null) {
        excelUtil.exportExcel(
            response,
            basePatientNotSeenVoList,
            "未复诊预约且未提醒统计表",
            baseOrganizationv.getAbbreviation() + "未复诊预约且未提醒统计表");
      }
    } else {
      excelUtil.exportExcel(response, basePatientNotSeenVoList, "未复诊预约且未提醒统计表", "未复诊预约且未提醒统计表");
    }
  }

  /**
   * 欠费查询
   *
   * @param form 条件
   * @return 欠费查询记录
   */
  public ArrearsStatisticsVo findArrears(ArrearsQueryForm form) {
    ArrearsStatisticsVo arrearsStatisticsVo = new ArrearsStatisticsVo();
    ArrearsStatisticsVo arrearsStatistics = baseBillMapper.selectArrears(form.getOrgId());
    if (arrearsStatistics != null) {
      BeanUtils.copyProperties(arrearsStatistics, arrearsStatisticsVo);
    }
    PageInfo<ArrearsVo> arrears = arrears(form);
    if (arrears != null) {
      arrearsStatisticsVo.setArrearsVoList(arrears);
    }
    return arrearsStatisticsVo;
  }

  /**
   * 欠费明细查询
   *
   * @param form 欠费查询form
   * @return 欠费查询记录
   */
  public PageInfo<ArrearsVo> arrears(ArrearsQueryForm form) {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<ArrearsVo> arrearsVoList = baseBillMapper.arrears(form);
    return new PageInfo<>(arrearsVoList);
  }

  /**
   * 导出欠费查询记录列表
   *
   * @param response 导出响应
   * @param form 条件
   */
  public void exportArrearsList(HttpServletResponse response, ArrearsQueryForm form)
      throws IOException {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    List<ArrearsVo> arrearsVoList = baseBillMapper.arrears(form);
    ExcelUtil<ArrearsVo> excelUtil = new ExcelUtil<>(ArrearsVo.class);
    if (StringHelper.isNotNull(form.getOrgId())) {
      BaseOrganization baseOrganizationv =
          baseOrganizationMapper.selectByPrimaryKey(form.getOrgId());
      if (baseOrganizationv != null) {
        excelUtil.exportExcel(
            response, arrearsVoList, "账单欠费统计表", baseOrganizationv.getAbbreviation() + "账单欠费统计表");
      }
    } else {
      excelUtil.exportExcel(response, arrearsVoList, "账单欠费统计表", "账单欠费统计表");
    }
  }

  /**
   * 就诊患者分析
   *
   * @param form 就诊患者分析查询条件
   * @return 就诊患者分析信息
   */
  public AnalysisVo analysis(PatientAnalysisQueryForm form) {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    AnalysisVo analysisVo = new AnalysisVo();
    // 来源类型比例
    Integer countOriginType = mapper.selectCountOriginType(form);
    List<AnalysisPatientOriginVo> analysisPatientOriginVoList = mapper.analysis(form, countOriginType);
    if (StringHelper.isNotEmpty(analysisPatientOriginVoList)) {
      analysisVo.setAnalysisPatientOriginVoList(analysisPatientOriginVoList);
    }
    // 男女比例
    Integer countGender = mapper.selectCountGender(form);
    List<AnalysisPatientGenderVo> analysisPatientGenderVoList =
        mapper.selectAnalysisPatientGender(form, countGender);
    if (StringHelper.isNotEmpty(analysisPatientGenderVoList)) {
      analysisVo.setAnalysisPatientGenderVoList(analysisPatientGenderVoList);
    }

    // 年龄比例
    List<AnalysisPatientAgeVo> analysisPatientAgeVoList = new ArrayList<>();
    // 患者总数量(有过就诊)
    Integer count = mapper.selectCountAnalysisAge(form);
    AnalysisPatientAgeVo notAge = new AnalysisPatientAgeVo();
    Integer notAgeCount = mapper.selectNotAgeCount(form);
    String percentageNotAge = mapper.calculateAgePercentage(notAgeCount, count);
    notAge.setAgeBracket("未知");
    notAge.setPercentage(percentageNotAge);
    analysisPatientAgeVoList.add(notAge);

    AnalysisPatientAgeVo youngVo = new AnalysisPatientAgeVo();
    Integer countYoungAge = mapper.selectAnalysisAge(form, 14, 0, 0);
    String percentageYoung = mapper.calculateAgePercentage(countYoungAge, count);
    youngVo.setAgeBracket("14岁及以下");
    youngVo.setPercentage(percentageYoung);
    analysisPatientAgeVoList.add(youngVo);

    AnalysisPatientAgeVo wrinklyList = new AnalysisPatientAgeVo();
    Integer countWrinkly = mapper.selectAnalysisAge(form, 0, 15, 60);
    String percentageWrinkly = mapper.calculateAgePercentage(countWrinkly, count);
    wrinklyList.setAgeBracket("14岁-60岁(包含60岁)");
    wrinklyList.setPercentage(percentageWrinkly);
    analysisPatientAgeVoList.add(wrinklyList);

    AnalysisPatientAgeVo oldPeopleList = new AnalysisPatientAgeVo();
    Integer countOldPeople = mapper.selectAnalysisAge(form, 0, 60, 999);
    String percentageOldPeople = mapper.calculateAgePercentage(countOldPeople, count);
    oldPeopleList.setAgeBracket("60岁以上");
    oldPeopleList.setPercentage(percentageOldPeople);
    analysisPatientAgeVoList.add(oldPeopleList);
    analysisVo.setAnalysisPatientAgeVoList(analysisPatientAgeVoList);
    return analysisVo;
  }
}
