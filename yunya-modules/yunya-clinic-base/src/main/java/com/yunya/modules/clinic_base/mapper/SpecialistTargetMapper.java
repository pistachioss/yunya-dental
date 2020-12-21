package com.yunya.modules.clinic_base.mapper;


import com.yunya.feign.clinic_base.domain.query.BusinessTargetTotalQuery;
import com.yunya.feign.clinic_base.domain.query.SpecialistAddOrUpdQuery;
import com.yunya.feign.clinic_base.domain.query.SpecialistTargetByDataQuery;
import com.yunya.feign.clinic_base.domain.query.SpecialistTargetQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecialistTargetByIdVo;
import com.yunya.feign.clinic_base.domain.vo.SpecialistTargetOrVo;
import com.yunya.feign.clinic_base.domain.vo.SpecialistTargetTotalVo;
import com.yunya.feign.clinic_base.domain.vo.SpecialistTargetVo;
import com.yunya.models.clinic_base.SpecialistTarget;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 简介: 专科数量目标
 *
 * @author: Zkq
 * @date: 2020/8/20
 * @description: 专科数量目标
 */
@org.apache.ibatis.annotations.Mapper
public interface SpecialistTargetMapper extends Mapper<SpecialistTarget> {

    /**
     * 查询专科数量目标列表
     *
     * @param query 判断添加或者修改
     * @return resultList
     */
    List<SpecialistTargetOrVo> specialistAddOrUpd(SpecialistAddOrUpdQuery query);

    /**
     * 添加专科数量目标
     *
     * @param specialistTarget 添加专科数量目标
     */
    void add(SpecialistTarget specialistTarget);

    /**
     * 修改专科数量目标
     *
     * @param specialistTarget 修改专科数量目标
     */
    void upd(SpecialistTarget specialistTarget);

    /**
     * 多选门诊查询结果
     *
     * @param businessTargetTotalQuery 多选门诊查询结果
     * @return SpecialistTargetTotalVo
     */
    SpecialistTargetTotalVo findAllData(BusinessTargetTotalQuery businessTargetTotalQuery);

    /**
     * 回显
     *
     * @param specialistTargetByDataQuery 回显
     * @return SpecialistTargetTotalVo
     */
    SpecialistTargetByIdVo findDataById(SpecialistTargetByDataQuery specialistTargetByDataQuery);

    /**
     * 判断添加或者修改
     *
     * @param query  查询专科数量目标列表
     * @return SpecialistTargetTotalVo
     */
    List<SpecialistTargetVo> findSpecialistTargetByPage(SpecialistTargetQuery query);
}