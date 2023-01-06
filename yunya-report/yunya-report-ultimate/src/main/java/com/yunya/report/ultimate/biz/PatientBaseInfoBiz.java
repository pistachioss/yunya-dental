package com.yunya.report.ultimate.biz;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.PatientSearchQuery;
import com.yunya.feign.report.domain.query.ClinicPerformanceBusinessQuery;
import com.yunya.feign.report.domain.query.PatientDimensionQueryForm;
import com.yunya.feign.report.domain.query.PatientManageQuery;
import com.yunya.feign.report.domain.query.PatientOriginConsumptionQuery;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseEmployee;
import com.yunya.models.report.BasePatient;
import com.yunya.models.report.BasePatientOrigin;
import com.yunya.models.report.CreditsShop;
import com.yunya.report.ultimate.mapper.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 简介:患者信息业务层
 *
 * @author: WY
 * @date: 2020/12/16 17:00
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientBaseInfoBiz extends BaseBiz<BasePatientMapper, BasePatient> {

  @Resource private BaseTreatmentProcessMapper baseTreatmentProcessMapper;

  @Resource private BaseBillMapper billMapper;

  @Resource private BaseEmployeeMapper baseEmployeeMapper;

  @Resource private BasePatientOriginMapper basePatientOriginMapper;

  @Resource private RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;

  public CreditsShop lastPatientCredits(Integer patientId) {
    return mapper.lastPatientCredits(patientId);
  }

  /**
   * 查询患者预约信息
   *
   * @param patientId 患者id
   * @return 患者预约信息
   */
  public PatientDataVo patientDataVo(Integer patientId) {
    PatientDataVo patientDataVo = new PatientDataVo();
    patientDataVo.setFirstVisitDate("");
    patientDataVo.setFirstVisitDoctors("");
    patientDataVo.setFirstVisitOutpatient("");
    patientDataVo.setCumulativeConsumption(new BigDecimal("0"));
    patientDataVo.setLastVisitDate("");
    patientDataVo.setLastVisitDoctors("");
    patientDataVo.setLastVisitOutpatient("");
    patientDataVo.setTotalArrears(new BigDecimal("0"));
    patientDataVo.setTotalReservation(0);
    patientDataVo.setTotalPerformance(0);
    patientDataVo.setTotalMissedAppointment(0);
    patientDataVo.setNumberOfVisits(0);

    patientDataVo.setPatientId(patientId);
    // 初诊信息
    PatientTreatInfoVo firstTreatInfo = baseTreatmentProcessMapper.selectFirstVisitInfo(patientId);
    if (null != firstTreatInfo) {
      patientDataVo.setFirstVisitDate(firstTreatInfo.getTreatDate());
      patientDataVo.setFirstVisitDoctors(firstTreatInfo.getTreatDentistName());
      patientDataVo.setFirstVisitOutpatient(firstTreatInfo.getTreatOutpatient());
    }
    // 末诊信息
    PatientTreatInfoVo lastTreatInfo = baseTreatmentProcessMapper.selectLastVisitInfo(patientId);
    if (null != lastTreatInfo) {
      patientDataVo.setLastVisitDate(lastTreatInfo.getTreatDate());
      patientDataVo.setLastVisitDoctors(lastTreatInfo.getTreatDentistName());
      patientDataVo.setLastVisitOutpatient(lastTreatInfo.getTreatOutpatient());
    }
    // 预约次数、履约次数、失约次数、就诊次数
    PatientAppointmentInfoVO appointmentInfo =
        baseTreatmentProcessMapper.selectPatientAppointmentInfo(patientId);
    if (null != appointmentInfo) {
      patientDataVo.setTotalReservation(appointmentInfo.getTotalReservation());
      patientDataVo.setTotalPerformance(appointmentInfo.getTotalPerformance());
      patientDataVo.setTotalMissedAppointment(appointmentInfo.getTotalMissedAppointment());
      patientDataVo.setNumberOfVisits(appointmentInfo.getNumberOfVisits());
    }
    // 患者消费
    List<PatientCostInfoVO> costInfos = billMapper.selectPatientCostInfoById(Collections.singleton(patientId));
    if (StringHelper.isNotEmpty(costInfos)) {
      PatientCostInfoVO costInfo = costInfos.get(0);
      patientDataVo.setCumulativeConsumption(costInfo.getCumulativeConsumption());
      patientDataVo.setTotalArrears(costInfo.getTotalArrears());
    }
    return patientDataVo;
  }



  /**
   * 根据条件查询患者信息
   *
   * @param query 关键字
   * @return 患者信息列表
   */
  public PageInfo<PatientInfoVO> findPatientInfoByExample(PatientSearchQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<PatientInfoVO> list = mapper.selectPatientInfoByExample(query);
    return new PageInfo<>(list);
  }


  /**
   * 根据条件统计患者来源
   *
   * @param query
   * @param patientIds
   * @return
   */
  public List<PatientFirstVisitSourceVO> clinicFirstVisitSourceList(ClinicPerformanceBusinessQuery query, Integer orgId, Collection<Integer> patientIds){
    List<PatientFirstVisitSourceVO> data = mapper.selectClinicFirstVisitSourceList(query, orgId, patientIds);
    if (StringHelper.isNotEmpty(data)) {
      Map<String, PatientFirstVisitSourceVO> countMap = new LinkedHashMap<>(16);
      data.forEach(vo->{
        Integer originType = vo.getOriginType();
        Integer firstVisitCount = vo.getFirstVisitCount();
        if (ObjectUtils.isEmpty(originType)) {
          originType = 12;
          vo.setOriginType(originType);
          vo.setOriginTypeName("未知来源");
        }
        String key = vo.getOrgId() + "," + originType;
        PatientFirstVisitSourceVO obj = countMap.get(key);

        if (!ObjectUtils.isEmpty(obj)) {
          firstVisitCount += obj.getFirstVisitCount();
        }
        vo.setFirstVisitCount(firstVisitCount);
        countMap.put(key, vo);
      });
      return new ArrayList<>(countMap.values());
    }
    return data;
  }

  /**
   * 根据条件查询初诊患者ID
   * @param query
   * @return
   */
  public List<BaseTreatmentProcessVO> firstVisitPatientList(ClinicPerformanceBusinessQuery query) {
    return mapper.selectFirstVisitPatientList(query);
  }

  public PageInfo<PatientManageVo> getPatientManagePage(PatientManageQuery query) {
    LocalDate now = LocalDate.now();
    Integer startAge = null;
    Integer endAge = null;
    if (query.getStartAge() != null && query.getEndAge() != null) {
      startAge = now.minusYears(query.getEndAge()).getYear();
      endAge = now.minusYears(query.getStartAge()).getYear();
    }
    Page<PatientManageVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
    mapper.listPatientByKeys(query, startAge, endAge);
    this.assembleOrigin(page.getResult());
    return new PageInfo<>(page);
  }

  public PageInfo<PatientBirthdayVo> getPatientBirthdayPage(PatientManageQuery query) {
    LocalDate now = LocalDate.now();
    Integer startAge = null;
    Integer endAge = null;
    if (query.getStartAge() != null && query.getEndAge() != null) {
      startAge = now.minusYears(query.getEndAge()).getYear();
      endAge = now.minusYears(query.getStartAge()).getYear();
    }
    Page<PatientManageVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
    mapper.listPatientByKeys(query, startAge, endAge);
    this.assembleOrigin(page.getResult());

    List<PatientBirthdayVo> patientBirthdayVoList = new ArrayList<>();
    for (PatientManageVo patientManageVo : page.getResult()) {
      PatientBirthdayVo patientBirthdayVo = new PatientBirthdayVo();
      patientBirthdayVo.setPatientId(patientManageVo.getPatientId());
      patientBirthdayVo.setLastVisitOutpatient(patientManageVo.getLastVisitOutpatient());
      patientBirthdayVo.setPatientName(patientManageVo.getPatientName());
      patientBirthdayVo.setGender(patientManageVo.getGender());
      patientBirthdayVo.setAge(patientManageVo.getAge());
      patientBirthdayVo.setMobile(patientManageVo.getMobile());
      patientBirthdayVo.setPatientOrionTypeName(patientManageVo.getPatientOrionTypeName());
      patientBirthdayVo.setPatientOrionName(patientManageVo.getPatientOrionName());
      patientBirthdayVo.setLastVisitDate(patientManageVo.getLastVisitDate());
      patientBirthdayVo.setLastVisitDoctors(patientManageVo.getLastVisitDoctors());
      Date dt = remotePatientCentralServiceFeign.getPatientBirthdayCheck(patientManageVo.getPatientId());
      patientBirthdayVo.setBirthdayCheck(dt.toString());
      patientBirthdayVoList.add(patientBirthdayVo);
    }
    PageInfo<PatientBirthdayVo> page1 = new PageInfo<PatientBirthdayVo>();
    page1.setList(patientBirthdayVoList);
    page1.setTotal(page.getTotal());
    page1.setPageSize(page.getPageSize());
    page1.setPageNum(page.getPageNum());
    return page1;
  }

  public List<PatientManageVo> listPatientManage(PatientManageQuery query) {
    LocalDate now = LocalDate.now();
    Integer startAge = null;
    Integer endAge = null;
    if (query.getStartAge() != null && query.getEndAge() != null) {
      startAge = now.minusYears(query.getEndAge()).getYear();
      endAge = now.minusYears(query.getStartAge()).getYear();
    }
    List<PatientManageVo> list = mapper.listPatientByKeys(query, startAge, endAge);
    this.assembleOrigin(list);
    return list;
  }

  private void assembleOrigin(List<PatientManageVo> list) {
    Map<String, List<PatientManageVo>> originMap = list.stream()
            .filter(obj -> StringUtils.isNotBlank(obj.getPatientOrionTypeName()))
            .filter(obj -> StringUtils.isNotBlank(obj.getPatientOrionName()))
            .collect(Collectors
                    .groupingBy(PatientManageVo::getPatientOrionTypeName, Collectors.toList()));
    originMap.forEach((k, v) -> {
      Example example;
      Map<Integer, String> collect;
      Set<Integer> originIds = v.stream()
              .map(obj -> Integer.valueOf(obj.getPatientOrionName()))
              .collect(Collectors.toSet());
      if ("员工转介绍".equals(k)) {
        example = new Example(BaseEmployee.class);
        example.selectProperties("userId","employeeName");
        example.createCriteria().andIn("userId", originIds);
        List<BaseEmployee> list1 = baseEmployeeMapper.selectByExample(example);
        collect = list1.stream().collect(Collectors.toMap(BaseEmployee::getUserId, BaseEmployee::getEmployeeName));
      } else if ("患者转介绍".equals(k)) {
        example = new Example(BasePatient.class);
        example.selectProperties("patientId","name");
        example.createCriteria().andIn("patientId", originIds);
        List<BasePatient> list1 = mapper.selectByExample(example);
        collect = list1.stream().collect(Collectors.toMap(BasePatient::getPatientId, BasePatient::getName));
      } else {
        example = new Example(BasePatientOrigin.class);
        example.selectProperties("id","name");
        example.createCriteria().andIn("id", originIds);
        List<BasePatientOrigin> list1 = basePatientOriginMapper.selectByExample(example);
        collect = list1.stream().collect(Collectors.toMap(BasePatientOrigin::getId, BasePatientOrigin::getName));
      }
      v.forEach(obj -> obj.setPatientOrionName(collect.get(Integer.valueOf(obj.getPatientOrionName()))));
    });
  }

  public void buildResponse(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
    response.setContentType("application/vnd.ms-excel");
    response.setCharacterEncoding("utf-8");
    String encodeFileName = URLEncoder.encode(fileName, "UTF-8");
    response.setHeader("Content-disposition", "attachment;filename=" + encodeFileName + ".xlsx");
  }

  public List<PatientManageVo> findPatientInfoList(PatientDimensionQueryForm queryForm) {
    return mapper.selectPatientInfoList(queryForm);
  }

  /**
   * 渠道来源消费报表
   *
   * @param query
   * @return
   */
  public PageInfo<PatientOriginConsumptionVO> findPatientOriginConsumption(PatientOriginConsumptionQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<PatientOriginConsumptionVO> data = mapper.selectPatientOriginConsumption(query);
    if (StringHelper.isNotEmpty(data)) {
      List<Integer> patientIds = data.stream().map(PatientOriginConsumptionVO::getPatientId).collect(Collectors.toList());
      // 患者消费
      List<PatientCostInfoVO> costInfos = billMapper.selectPatientCostInfoById(patientIds);
      if (StringHelper.isNotEmpty(costInfos)) {
        costInfos.forEach(cost->{
          Integer patientId = cost.getPatientId();
          data.forEach(patient->{
            if (patient.getPatientId().equals(patientId)) {
              patient.setCumulativeConsumption(cost.getCumulativeConsumption());
              patient.setTotalArrears(cost.getTotalArrears());
            }
          });
        });

      }
    }
    return new PageInfo<>(data);
  }

  /**
   * 导出渠道来源消费报表
   *
   * @param query
   * @param response
   * @throws IOException
   */
  public void exportPatientOriginConsumption(PatientOriginConsumptionQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    List<PatientOriginConsumptionVO> result = findPatientOriginConsumption(query).getList();
    ExcelUtil<PatientOriginConsumptionVO> excelUtil = new ExcelUtil(PatientOriginConsumptionVO.class);
    String sheetName = "渠道来源消费报表";
    String fileName = excelUtil.getFileName(query.getStartDate()+"", query.getEndDate()+"", "", sheetName);
    excelUtil.exportExcel(response, result, sheetName, fileName);
  }

  /**
   * 根据患者id查询末次就诊信息
   *
   * @param patientId
   * @return
   */
  public PatientTreatInfoVo findPatientLastTreatmentInfo(Integer patientId) {
    return baseTreatmentProcessMapper.selectLastVisitInfo(patientId);
  }
}
