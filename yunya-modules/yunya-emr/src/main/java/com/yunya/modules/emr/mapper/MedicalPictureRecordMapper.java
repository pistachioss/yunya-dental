package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.query.MedicalPictureRecordQuery;
import com.yunya.feign.emr.domain.vo.MedicalPictureRecordVO;
import com.yunya.models.emr.MedicalPictureRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MedicalPictureRecordMapper extends Mapper<MedicalPictureRecord> {
    /**
     * 条件查询病历照片记录
     *
     * @param query
     * @return
     */
    List<MedicalPictureRecordVO> findMedicalPictureRecordList(@Param("query") MedicalPictureRecordQuery query);
}