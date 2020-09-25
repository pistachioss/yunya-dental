package com.yunya.modules.emr.mapper;


import com.yunya.models.emr.MedicalTemplate;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


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
    int countByName(@Param("name") String name, @Param("categoryId") Integer categoryId, @Param("id") Integer id);

    /**
     * 根据内容模糊查询
     * @param keyword keyword
     * @param categoryId categoryId
     * @return List
     */
    List<MedicalTemplate> listByKeyword(@Param("keyword") String keyword, @Param("categoryId")Integer categoryId
            , @Param("enable") Integer enable, @Param("type") Integer type);

    /**
     * 启用禁用模板
     * @param categoryId
     * @param templateId
     * @param enable
     */
    void enableById(@Param("categoryId") Integer categoryId, @Param("templateId") Integer templateId, @Param("enable") Integer enable);

    void updateContent(@Param("template") MedicalTemplate template);
}