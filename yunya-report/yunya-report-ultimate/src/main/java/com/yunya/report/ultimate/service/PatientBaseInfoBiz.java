package com.yunya.report.ultimate.service;

import com.yunya.feign.report.domain.vo.PatientDataVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.BasePatient;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import com.yunya.report.ultimate.mapper.BaseTreatmentProcessMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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

    @Resource
    private BaseTreatmentProcessMapper baseTreatmentProcessMapper;

    /**
     * 查询患者预约信息
     * @param id 患者id
     * @return PatientDataVo
     */
    public PatientDataVo PatientDataVo(Integer id) {
        PatientDataVo patientDataVo = mapper.findPatientDataVo(id);
        if (patientDataVo != null){
            patientDataVo.setTotalReservation(baseTreatmentProcessMapper.selectPatientReservation(id));
            patientDataVo.setTotalPerformance(baseTreatmentProcessMapper.selectPatientPerformance(id));
            patientDataVo.setTotalMissedAppointment(baseTreatmentProcessMapper.selectMissedAppointment(id));
        }
        return patientDataVo;
    }
}