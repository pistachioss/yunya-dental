package com.yunya.modules.treatment.other.mapper;

import com.yunya.models.treatment_other.XUploadFile;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface XUploadFileMapper extends Mapper<XUploadFile> {
    int addBatch(@Param("files") List<XUploadFile> files);
}