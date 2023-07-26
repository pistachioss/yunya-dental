package com.yunya.report.ultimate.biz.tag;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BasePatientBehaviorTagVO;
import com.yunya.feign.report.domain.vo.PatientCountVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.report.ultimate.enums.ActivityDegreeEnum;
import com.yunya.report.ultimate.enums.FrequencyTreatmentEnum;
import com.yunya.report.ultimate.enums.TreatmentTariffEnum;
import com.yunya.report.ultimate.mapper.BaseBillDetailMapper;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import com.yunya.report.ultimate.mapper.BaseTreatmentProcessMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: chenlin
 * @date: 2023/5/15 14:23
 * @description: 患者就诊相关标签业务层
 * @since: 1.0.0
 */
@Slf4j
@Service
public class PatientTreatmentTagBiz {

    @Autowired
    private BasePatientMapper basePatientMapper;
    @Autowired
    private BaseBillDetailMapper baseBillDetailMapper;
    @Autowired
    private BaseTreatmentProcessMapper baseTreatmentProcessMapper;

    @Resource(name = "customizeThreadPool")
    private ThreadPoolExecutor executor;

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
        List<BasePatientBehaviorTagVO> result = baseTreatmentProcessMapper.selectPatientFrequencyOfTreatment(query);
        result.forEach(vo-> vo.setTagName(FrequencyTreatmentEnum.getName(vo.getTimes())));
        return result;
    }

    /**
     * 患者的治疗项目
     *
     * @param query
     * @return
     */
    public List<BasePatientBehaviorTagVO> findPatientTreatmentTariffTag(DateRangeQueryForm query) throws InterruptedException {
        List<Future<List<BasePatientBehaviorTagVO>>> futures = Lists.newArrayList();
        TreatmentTariffEnum[] values = TreatmentTariffEnum.values();
        CountDownLatch latch = new CountDownLatch(values.length);
        for (TreatmentTariffEnum tagEnum : values) {
            futures.add(
                executor.submit(()->{
                    List<BasePatientBehaviorTagVO> result = null;
                    try {
                        result = baseBillDetailMapper.selectPatientTreatmentTariffTag(query, tagEnum);
                    } catch (Exception e) {
                        log.error("findPatientTreatmentTariffTag error: {}", e);
                    } finally {
                        latch.countDown();
                    }
                    return result;
                })
            );
        }
        latch.await();
        List<BasePatientBehaviorTagVO> result = Lists.newArrayList();
        futures.forEach(future->{
            try {
                List<BasePatientBehaviorTagVO> vos = future.get();
                if (StringHelper.isNotEmpty(vos)) {
                    result.addAll(vos);
                    vos.clear();
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("patientTreatmentTariffTag package data error: {}", e);
            }
        });
        return result;
    }
}
