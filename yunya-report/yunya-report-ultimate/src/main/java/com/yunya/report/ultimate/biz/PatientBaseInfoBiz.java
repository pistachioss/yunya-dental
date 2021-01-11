package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.vo.PatientDataFirstVisitVo;
import com.yunya.feign.report.domain.vo.PatientDataVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BasePatient;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import com.yunya.report.ultimate.mapper.BaseTreatmentProcessMapper;
import org.springframework.beans.BeanUtils;
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
    public PatientDataVo patientDataVo(Integer id) {
        PatientDataFirstVisitVo patientDataFirstVisitVo = baseTreatmentProcessMapper.selectFirstVisitInfo(id);
        PatientDataVo patientDataVo = baseTreatmentProcessMapper.selectLastVisitInfo(id);
        BeanUtils.copyProperties(patientDataFirstVisitVo,patientDataVo);
        if (patientDataVo != null){
            patientDataVo.setTotalReservation(baseTreatmentProcessMapper.selectPatientReservation(id));
            patientDataVo.setTotalPerformance(baseTreatmentProcessMapper.selectPatientPerformance(id));
            patientDataVo.setTotalMissedAppointment(baseTreatmentProcessMapper.selectMissedAppointment(id));
            patientDataVo.setNumberOfVisits(baseTreatmentProcessMapper.selectNumberOfVisits(id));
        }
        return patientDataVo;
    }
}