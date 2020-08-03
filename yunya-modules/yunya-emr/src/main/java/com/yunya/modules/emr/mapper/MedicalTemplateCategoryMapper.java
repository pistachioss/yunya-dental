package com.yunya.modules.emr.mapper;

import com.yunya.models.emr.MedicalTemplateCategory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface MedicalTemplateCategoryMapper extends Mapper<MedicalTemplateCategory> {

    /**
     * 查询分类名称是否已存在
     * @param name
     * @param parentId
     * @param id 修改ID
     * @return
     */
    int countByName(@Param("name") String name, @Param("parentId") Integer parentId, @Param("id") Integer id);

}