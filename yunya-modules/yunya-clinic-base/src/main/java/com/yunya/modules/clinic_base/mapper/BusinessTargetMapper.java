package com.yunya.modules.clinic_base.mapper;


import com.yunya.feign.clinic_base.domain.query.*;
import com.yunya.feign.clinic_base.domain.vo.*;
import com.yunya.models.clinic_base.BusinessTarget;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 简介: 公司业务目标
 *
 * @author: Zkq
 * @date: 2020/8/20 14:53
 * @description:
 * @since: 1.0.0
 */
@org.apache.ibatis.annotations.Mapper
public interface BusinessTargetMapper extends Mapper<BusinessTarget> {

    /**
     * 查询公司业务目标列表
     *
     * @param query 查询公司业务目标列表
     * @return BusinessTargetVo
     */
    List<BusinessTargetVo> findBusinessTargetByPage(BusinessTargetQuery query);

    /**
     * 添加现金模块
     *
     * @param businessTarget 添加现金模块
     */
    void add(BusinessTarget businessTarget);

    /**
     * 修改现金模块列表
     *
     * @param businessTarget 修改现金模块列表
     */
    void upd(BusinessTarget businessTarget);

    /**
     * 多门诊查询列表
     *
     * @param businessTargetTotalQuery 多门诊查询列表
     * @return BusinessTargetTotalVo
     */
    BusinessTargetTotalVo findAllData(BusinessTargetTotalQuery businessTargetTotalQuery);

    /**
     * 回显公司信息
     *
     * @param businessTargetByDataQuery 回显公司信息
     * @return BusinessTargetByIdVo
     */
    BusinessTargetByIdVo findDataById(BusinessTargetByDataQuery businessTargetByDataQuery);

    /**
     * 查询是否存在 看是增加还是修改
     *
     * @param businessAddOrUpdQuery 查询是否存在 看是增加还是修改
     * @return List<BusinessTargetOrVo>
     */
    List<BusinessTargetOrVo> businessAddOrUpd(BusinessAddOrUpdQuery businessAddOrUpdQuery);

    /**
     * 导出
     *
     * @param query 根据条件导出
     * @return List<BusinessTargetExportVo>
     */
    List<BusinessTargetExportVo> exportListByDate(BusinessTargetExportQuery query);
}