package com.yunya.modules.clinic.base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.query.*;
import com.yunya.feign.cash_balance.vo.*;
import com.yunya.models.clinic_base.SpecialistTarget;
import com.yunya.modules.clinic.base.mapper.SpecialistTargetMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 简介: 专科数量目标
 *
 * @author: Zkq
 * @date: 2020/8/20
 * @description: 专科数量目标
 */
@Service
public class SpecialistTargetBiz {

    @Resource
    private SpecialistTargetMapper specialistTargetMapper;

    /**
     * 查询专科数量目标列表
     *
     * @param query 查询专科数量目标列表
     * @return resultList
     */
    public PageInfo<SpecialistTargetVo> findSpecialistTargetByPage(SpecialistTargetQuery query) {
        int completeData = 500;
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<SpecialistTargetVo> resultList = specialistTargetMapper.findSpecialistTargetByPage(query);
        for (SpecialistTargetVo complete : resultList) {
            complete.setComplete(completeData);
        }
        return new PageInfo<>(resultList);
    }

    /**
     * 添加专科数量目标
     *
     * @param specialistTarget 添加专科数量目标
     */
    public void add(SpecialistTarget specialistTarget) {
        specialistTargetMapper.add(specialistTarget);
    }

    /**
     * 修改专科数量目标
     *
     * @param specialistTarget 修改专科数量目标
     */
    public void upd(SpecialistTarget specialistTarget) {
        specialistTargetMapper.upd(specialistTarget);
    }

    /**
     * 多选门诊查询结果
     *
     * @param businessTargetTotalQuery 多选门诊查询结果
     * @return SpecialistTargetTotalVo
     */
    public SpecialistTargetTotalVo findAllData(BusinessTargetTotalQuery businessTargetTotalQuery) {
        return specialistTargetMapper.findAllData(businessTargetTotalQuery);
    }


    /**
     * 回显
     *
     * @param specialistTargetByDataQuery 回显
     * @return SpecialistTargetTotalVo
     */
    public SpecialistTargetByIdVo findDataById(SpecialistTargetByDataQuery specialistTargetByDataQuery) {
        return specialistTargetMapper.findDataById(specialistTargetByDataQuery);
    }

    /**
     * 判断添加或者修改
     *
     * @param specialistAddOrUpdQuery 判断添加或者修改
     * @return SpecialistTargetTotalVo
     */
    public List<SpecialistTargetOrVo> specialistAddOrUpd(SpecialistAddOrUpdQuery specialistAddOrUpdQuery) {
        return specialistTargetMapper.specialistAddOrUpd(specialistAddOrUpdQuery);
    }
}
