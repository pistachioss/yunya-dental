package com.yunya.middletable.dao.treatment_other;

import com.yunya.models.report.BaseQcylTreatment;
import com.yunya.models.treatment_other.QcTreatmentRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface QcTreatmentRecordMapper extends Mapper<QcTreatmentRecord> {
    List<BaseQcylTreatment> selectQcylTreatmentRecordList(
            @Param("id") Integer id,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );
}