package com.yunya.modules.clinic.base.mapper;

import com.yunya.feign.cash_balance.query.SpecializedSubjectProjectQuery;
import com.yunya.feign.cash_balance.vo.SpecializedSubjectProjectVo;
import com.yunya.models.clinic_base.SpecializedSubjectProject;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 简介: 专科项目设置管理
 *
 * @author: Zkq
 * @date: 2020/8/20 14:53
 * @description:
 * @since: 1.0.0
 */
@org.apache.ibatis.annotations.Mapper
public interface SpecializedSubjectProjectMapper extends Mapper<SpecializedSubjectProject> {

    /**
     * 专科项目设置列表
     *
     * @param query 专科项目设置列表
     * @return resultList
     */
    List<SpecializedSubjectProjectVo> findSpecializedList(SpecializedSubjectProjectQuery query);

    /**
     * 添加专科项目设置
     *
     * @param specializedSubjectProject 添加专科项目设置
     */
    void add(SpecializedSubjectProject specializedSubjectProject);

    /**
     * 修改专科项目设置
     *
     * @param specializedSubjectProject 修改专科项目设置
     */
    void upd(SpecializedSubjectProject specializedSubjectProject);

    /**
     * 删除专科项目设置
     *
     * @param id 通过id删除数据
     */
    void del(Integer id);

}