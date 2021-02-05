package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.PatientSearchQuery;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.BasePatient;
import com.yunya.report.ultimate.mapper.BaseBillMapper;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import com.yunya.report.ultimate.mapper.BaseTreatmentProcessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

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

  @Autowired private BaseBillMapper billMapper;

  /**
   * 查询患者预约信息
   *
   * @param patientId 患者id
   * @return PatientDataVo
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
    PatientCostInfoVO costInfo = billMapper.selectPatientCostInfo(patientId);
    if (null != costInfo) {
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
}
