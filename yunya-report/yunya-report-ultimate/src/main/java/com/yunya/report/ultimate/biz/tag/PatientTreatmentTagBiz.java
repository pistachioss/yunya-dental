package com.yunya.report.ultimate.biz.tag;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BasePatientBehaviorTagVO;
import com.yunya.feign.report.domain.vo.PatientCountVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseTreatmentProcess;
import com.yunya.report.ultimate.enums.ActivityDegreeEnum;
import com.yunya.report.ultimate.enums.FrequencyTreatmentEnum;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import com.yunya.report.ultimate.mapper.BaseTreatmentProcessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author: chenlin
 * @date: 2023/5/15 14:23
 * @description: 患者就诊相关标签业务层
 * @since: 1.0.0
 */
@Service
public class PatientTreatmentTagBiz {

    @Autowired
    private BasePatientMapper basePatientMapper;
    @Autowired
    private BaseTreatmentProcessMapper baseTreatmentProcessMapper;

    public List<BasePatientBehaviorTagVO> findPatientDayOfLastVisit() {
        List<PatientCountVO> patients = basePatientMapper.selectPatientDayOfLastVisit();
        List<BasePatientBehaviorTagVO> result = new ArrayList<>();
        patients.forEach(patient->{
            ActivityDegreeEnum activityEnum = ActivityDegreeEnum.getEnum(patient.getCount());
            if (StringHelper.isNotNull(activityEnum)) {
                result.add(
                    BasePatientBehaviorTagVO.builder()
                        .patientId(patient.getPatientId())
                        .tagName(activityEnum.getName())
                        .build()
                );

            }
        });
        return result;
    }

    /**
     * 患者的诊疗频率
     *
     * @param query
     * @return
     */
    public List<BasePatientBehaviorTagVO> findPatientFrequencyOfTreatment(DateRangeQueryForm query) {
        Map<Integer, Set<Date>> registeredDateMap = new HashMap<>();
        List<BaseTreatmentProcess> treatments = baseTreatmentProcessMapper.selectPatientRegisteredList(query);
        treatments.forEach(treatment->{
            Integer patientId = treatment.getPatientId();
            Date registeredDate = treatment.getRegisteredDate();
            Set<Date> dates = registeredDateMap.computeIfAbsent(patientId, HashSet::new);
            dates.add(registeredDate);
        });
        List<BasePatientBehaviorTagVO> result = new ArrayList<>();
        registeredDateMap.forEach((patientId, dates)->{
            int times = dates.size();
            FrequencyTreatmentEnum frequencyEnum = FrequencyTreatmentEnum.getEnum(times);
            if (StringHelper.isNotNull(frequencyEnum)) {
                result.add(
                    BasePatientBehaviorTagVO.builder()
                        .patientId(patientId)
                        .tagName(frequencyEnum.getName())
                        .build()
                );
            }
        });
        return result;
    }
}
