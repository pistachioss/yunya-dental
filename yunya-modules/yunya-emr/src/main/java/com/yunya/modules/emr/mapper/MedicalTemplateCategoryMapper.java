package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.vo.ChildCategoryListVo;
import com.yunya.models.emr.MedicalTemplateCategory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MedicalTemplateCategoryMapper extends Mapper<MedicalTemplateCategory> {

    /**
     * 查询分类名称是否已存在
     * @param name
     * @param parentId
     * @param id 修改ID
     * @return
     */
    int countByName(@Param("name") String name, @Param("parentId") Integer parentId, @Param("id") Integer id);

    /**
     * 查询
     * @param parentId
     * @return
     */
    List<ChildCategoryListVo> getTemplateList(@Param("parentId") Integer parentId);
}