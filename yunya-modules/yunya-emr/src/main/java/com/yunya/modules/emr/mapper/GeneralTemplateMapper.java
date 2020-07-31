package com.yunya.modules.emr.mapper;

import com.yunya.models.emr.GeneralTemplate;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface GeneralTemplateMapper extends Mapper<GeneralTemplate> {

    /**
     * 查询分类下普通模板数量
     * @param categoryId
     * @return
     */
    int countByCategoryId(@Param("categoryId") Integer categoryId);

    /**
     * 根据内容模糊查询
     * @param keyword
     * @param categoryId
     * @return
     */
    List<GeneralTemplate> listByKeyword(@Param("keyword") String keyword, @Param("categoryId")Integer categoryId);

    /**
     * 启用禁用模板
     * @param categoryId
     * @param templateId
     * @param enable
     */
    void enableById(@Param("categoryId") Integer categoryId, @Param("templateId") Integer templateId, @Param("enable") Integer enable);

}