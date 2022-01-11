package com.yunya.modules.emr.mapper;

import com.yunya.models.emr.MedicalCheckHistory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MedicalCheckHistoryMapper extends Mapper<MedicalCheckHistory> {
    void insertBatch(@Param("list") List<MedicalCheckHistory> list);
}