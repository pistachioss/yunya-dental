package com.yunya.feign.treatment_other.factory;

import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.feign.treatment_other.domain.model.VisitingRecordModel;
import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import com.yunya.models.treatment_other.VisitingRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 就诊扩展外部调用接口降级处理
 * @author: LHB
 * @create: 2020-08-25 20:03
 **/
@Component
@Slf4j
public class RemoteTreatmentOtherFactory implements RemoteTreatmentOtherFeign {

    @Override
    public void insertVisitingRecord(List<VisitingRecord> visitingRecords) {
    }

    @Override
    public List<VisitingRecordVo> findVisitingRecordByConditionRest(VisitingRecordQuery query) {
        return null;
    }

    @Override
    public void deleteVisitingRecordByTreatmentIdRest(Integer treatmentId) {

    }

    @Override
    public Integer countNextVisiting(Integer patientId, String regDate) {
        return null;
    }

}
