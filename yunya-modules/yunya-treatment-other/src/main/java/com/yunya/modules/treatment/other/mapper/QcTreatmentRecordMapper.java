package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment.domain.vo.QcTreatmentVO;
import com.yunya.feign.treatment_other.domain.query.QcRecommondInfoQuery;
import com.yunya.feign.treatment_other.domain.vo.QcRecommondInfoVO;
import com.yunya.models.treatment_other.QcTreatmentRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface QcTreatmentRecordMapper extends Mapper<QcTreatmentRecord> {
    List<QcRecommondInfoVO> selectMallRecommondList(@Param("query") QcRecommondInfoQuery query);

    /**
     * 根据绑定订单id查询全程医疗就诊记录列表
     *
     * @param orderRecordId
     * @return
     */
    List<QcTreatmentVO> selectQcTreatmentListByOrderId(@Param("orderRecordId") Integer orderRecordId);

    /**
     * 查询患者的可用全程医疗就诊记录列表
     *
     * @param patientId
     * @return
     */
    List<QcRecommondInfoVO> selectPatientEnableQcTreatmentRecord(@Param("patientId") Integer patientId);
}