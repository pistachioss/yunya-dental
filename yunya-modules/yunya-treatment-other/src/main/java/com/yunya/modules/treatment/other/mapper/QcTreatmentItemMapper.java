package com.yunya.modules.treatment.other.mapper;

import com.yunya.models.treatment_other.QcTreatmentItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface QcTreatmentItemMapper extends Mapper<QcTreatmentItem> {
    List<QcTreatmentItem> selectListByQcTreatmentId(@Param("qcTreatmentId") Integer qcTreatmentId);
}