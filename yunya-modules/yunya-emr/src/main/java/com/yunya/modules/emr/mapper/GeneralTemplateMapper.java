package com.yunya.modules.emr.mapper;

import com.yunya.models.emr.GeneralTemplate;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface GeneralTemplateMapper extends Mapper<GeneralTemplate> {

    /**
     * 查询分类下普通模板数量
     * @param categoryId
     * @return
     */
    int countByCategoryId(@Param("categoryId") Integer categoryId);
}