package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.query.MedicalCheckRecordQuery;
import com.yunya.feign.emr.domain.vo.MedicalCheckRecordVO;
import com.yunya.models.emr.MedicalCheckRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MedicalCheckRecordMapper extends Mapper<MedicalCheckRecord> {
    /**
     * 批量新增
     * @param records
     */
    void insertBatch(@Param("list") List<MedicalCheckRecord> records);

    /**
     * 条件查询检查记录
     *
     * @param query
     * @return
     */
    List<MedicalCheckRecordVO> selectMedicalCheckRecordList(@Param("query") MedicalCheckRecordQuery query);
}