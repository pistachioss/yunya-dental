package com.yunya.feign.treatment.factory;

import com.yunya.feign.treatment_other.RemoteTreatmentServiceFeign;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 简介: 云牙诊疗服务降级处理
 *
 * @author: chow
 * @date: 2020/8/14 20:56
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Component
public class RemoteTreatmentServiceFallBackFactory implements RemoteTreatmentServiceFeign {
    @Override
    public Registered findRegisteredById(Integer id) {
        return null;
    }

    @Override
    public List<Registered> findRegisteredList(Registered entity) {
        return null;
    }

    @Override
    public TreatmentRecord findTreatmentRecordById(Integer id) {
        return null;
    }

    @Override
    public List<TreatmentRecord> findTreatmentRecordList(TreatmentRecord entity) {
        return null;
    }

    @Override
    public void updateTreatmentRecord(Integer id) {

    }
    /***/
}
