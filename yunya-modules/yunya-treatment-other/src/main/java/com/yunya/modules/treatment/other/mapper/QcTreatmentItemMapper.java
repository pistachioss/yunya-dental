package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.vo.OrderAdviceItemVO;
import com.yunya.models.treatment_other.QcTreatmentItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface QcTreatmentItemMapper extends Mapper<QcTreatmentItem> {

    void saveByPrimaryKeySelective(QcTreatmentItem data);
    /**
     *  根据全程就诊id查询全程开的医嘱项目明细列表
     *
     * @param qcTreatmentId
     * @return
     */
    List<QcTreatmentItem> selectAdviceItemsByTreatmentId(@Param("qcTreatmentId") Integer qcTreatmentId);

    /**
     * 根据全程就诊id查询项目明细列表（含二开和全程医嘱）
     *
     * @param qcTreatmentId
     * @return
     */
    List<OrderAdviceItemVO> selectOrderAdviceItemsByTreatmentId(@Param("qcTreatmentId") Integer qcTreatmentId);

    List<QcTreatmentItem> selectQcTreatmentItemsByTreatmentId(
            @Param("qcTreatmentId") Integer qcTreatmentId,
            @Param("qcCollected") Boolean qcCollected);
}