package com.yunya.modules.emr.mapper;


import com.yunya.models.emr.MedicalTemplate;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface MedicalTemplateMapper extends Mapper<MedicalTemplate> {

    /**
     * 查询分类下病历模板数量
     *
     * @param categoryId
     * @return
     */
    int countByCategoryId(@Param("categoryId") Integer categoryId);

    /**
     * 根据模板名称查询数量
     * @param name
     * @param id
     * @return
     */
    int countByName(@Param("name") String name, @Param("id") Integer id);
}