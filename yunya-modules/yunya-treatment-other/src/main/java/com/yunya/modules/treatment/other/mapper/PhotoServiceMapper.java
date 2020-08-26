package com.yunya.modules.treatment.other.mapper;


import com.yunya.feign.treatment_other.domain.query.PhotoServiceQuery;
import com.yunya.feign.treatment_other.domain.vo.PhotoServiceVo;
import com.yunya.models.treatment_other.PhotoService;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface PhotoServiceMapper extends Mapper<PhotoService> {

    List<PhotoServiceVo> findPhotoServiceData(PhotoServiceQuery query);

    void add(PhotoService photoService);

    void upd(PhotoService photoService);

    void del(Integer id);
}