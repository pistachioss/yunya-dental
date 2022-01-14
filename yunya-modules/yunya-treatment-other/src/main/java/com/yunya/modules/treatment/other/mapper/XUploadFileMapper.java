package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.query.XUploadFileQuery;
import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import com.yunya.models.treatment_other.XUploadFile;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface XUploadFileMapper extends Mapper<XUploadFile> {
    /**
     * 批量新增
     *
     */
    int addBatch(@Param("files") List<XUploadFile> files);

    /**
     * 逻辑删除
     *
     */
    void updateUnvaildByEntity(@Param("query") XUploadFile query, @Param("crtId") Integer crtId, @Param("crtTime") Date crtTime);

    /**
     * 条件查询上传文件
     *
     */
    List<XUploadFileVO> selectXUploadFileList(@Param("query") XUploadFileQuery query);
}