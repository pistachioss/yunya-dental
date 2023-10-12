package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment.domain.vo.QcTreatmentVO;
import com.yunya.feign.treatment_other.domain.query.QcRecommondInfoQuery;
import com.yunya.feign.treatment_other.domain.vo.QcCustomerTreatmentVO;
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
    List<QcTreatmentVO> selectQcTreatmentBindedByOrderId(@Param("orderRecordId") Integer orderRecordId);

    /**
     * 查询患者的可用全程医疗就诊记录列表
     *
     * @param patientId
     * @return
     */
    List<QcRecommondInfoVO> selectPatientEnableQcTreatmentRecord(@Param("patientId") Integer patientId);

    /**
     * 根据id查询全程医疗就诊基础信息
     *
     * @param id
     * @return
     */
    QcCustomerTreatmentVO selectPatientTreatmentById(@Param("id") Integer id);

    /**
     * 根据id列表查询全程医疗就诊记录列表
     *
     * @param ids
     * @return
     */
    List<QcTreatmentRecord> selectQcTreatmentListByIds(@Param("ids") List<Integer> ids);

    /**
     * 保存（不存在则新增，存在则更新）
     * @param data
     */
    void saveByPrimaryKeySelective(QcTreatmentRecord data);
}