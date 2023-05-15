package com.yunya.report.ultimate.biz.tag;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BasePatientActivityDayVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseTreatmentProcess;
import com.yunya.report.ultimate.enums.ActivityDegreeEnum;
import com.yunya.report.ultimate.enums.FrequencyTreatmentEnum;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import com.yunya.report.ultimate.mapper.BaseTreatmentProcessMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: chenlin
 * @date: 2023/5/15 14:23
 * @description: 患者就诊相关标签业务层
 * @since: 1.0.0
 */
public class PatientTreatmentTagBiz {

    @Autowired
    private BasePatientMapper basePatientMapper;
    @Autowired
    private BaseTreatmentProcessMapper baseTreatmentProcessMapper;

    public List<BasePatientActivityDayVO> findPatientDayOfLastVisit() {
        List<BasePatientActivityDayVO> patients = basePatientMapper.selectPatientDayOfLastVisit();
        return patients.stream().filter(patientTag->{
            ActivityDegreeEnum activityEnum = ActivityDegreeEnum.getEnum(patientTag.getDayOfLastVisit());
            boolean hasActivityTag = StringHelper.isNotNull(activityEnum);
            if (hasActivityTag) {
                patientTag.setTagName(activityEnum.getName());
            }
            return hasActivityTag;
        }).collect(Collectors.toList());
    }

    /**
     * 患者的诊疗频率
     *
     * @param query
     * @return
     */
    public List<BasePatientActivityDayVO> findPatientFrequencyOfTreatment(DateRangeQueryForm query) {
        Map<Integer, Set<Date>> registeredDateMap = new HashMap<>();
        List<BaseTreatmentProcess> treatments = baseTreatmentProcessMapper.selectPatientRegisteredList(query);
        treatments.forEach(treatment->{
            Integer patientId = treatment.getPatientId();
            Date registeredDate = treatment.getRegisteredDate();
            Set<Date> dates = registeredDateMap.computeIfAbsent(patientId, HashSet::new);
            dates.add(registeredDate);
        });
        List<BasePatientActivityDayVO> result = new ArrayList<>();
        registeredDateMap.forEach((patientId, dates)->{
            int times = dates.size();
            FrequencyTreatmentEnum frequencyEnum = FrequencyTreatmentEnum.getEnum(times);
            BasePatientActivityDayVO patientTag = new BasePatientActivityDayVO();
            patientTag.setPatientId(patientId);
            patientTag.setTagName(frequencyEnum.getName());
            result.add(patientTag);
        });
        return result;
    }
}
