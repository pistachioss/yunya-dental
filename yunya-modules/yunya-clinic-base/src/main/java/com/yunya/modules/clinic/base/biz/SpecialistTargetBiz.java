package com.yunya.modules.clinic.base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.model.BusinessTargetModel;
import com.yunya.feign.cash_balance.model.SpecialistTargetModel;
import com.yunya.feign.cash_balance.query.*;
import com.yunya.feign.cash_balance.vo.*;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.clinic_base.BusinessTarget;
import com.yunya.models.clinic_base.SpecialistTarget;
import com.yunya.modules.clinic.base.mapper.BusinessTargetMapper;
import com.yunya.modules.clinic.base.mapper.SpecialistTargetMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecialistTargetBiz {

    @Resource
    private SpecialistTargetMapper specialistTargetMapper;


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


    public void add(SpecialistTarget specialistTarget) {
        specialistTargetMapper.add(specialistTarget);
    }

    public void upd(SpecialistTarget specialistTarget) {
        specialistTargetMapper.upd(specialistTarget);
    }

    public SpecialistTargetTotalVo findAllData(BusinessTargetTotalQuery businessTargetTotalQuery){
        SpecialistTargetTotalVo specialistTargetTotalVo = specialistTargetMapper.findAllData(businessTargetTotalQuery);
        return specialistTargetTotalVo;
    }


    public SpecialistTargetByIdVo findDataById(SpecialistTargetByDataQuery specialistTargetByDataQuery){
        SpecialistTargetByIdVo specialistTargetByIdVo = specialistTargetMapper.findDataById(specialistTargetByDataQuery);
        return specialistTargetByIdVo;
    }
    public List<SpecialistTargetOrVo> specialistAddOrUpd(SpecialistAddOrUpdQuery specialistAddOrUpdQuery){
        List<SpecialistTargetOrVo> specialistTargetOrVo = specialistTargetMapper.specialistAddOrUpd(specialistAddOrUpdQuery);
        return specialistTargetOrVo;
    }
}
