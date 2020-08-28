package com.yunya.modules.clinic.base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.model.BusinessTargetModel;
import com.yunya.feign.cash_balance.model.SpecialistTargetModel;
import com.yunya.feign.cash_balance.query.BusinessAddOrUpdQuery;
import com.yunya.feign.cash_balance.query.BusinessTargetQuery;
import com.yunya.feign.cash_balance.query.SpecialistTargetQuery;
import com.yunya.feign.cash_balance.vo.BusinessTargetTotalVo;
import com.yunya.feign.cash_balance.vo.BusinessTargetVo;
import com.yunya.feign.cash_balance.vo.SpecialistTargetVo;
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


    public PageInfo<SpecialistTargetVo> findspecialistTargetByPage(SpecialistTargetQuery query) {
        int completeData = 500;
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<SpecialistTargetVo> resultList = specialistTargetMapper.findspecialistTargetByPage(query);
        for (SpecialistTargetVo complete : resultList) {
            complete.setComplete(completeData);
        }
        return new PageInfo<>(resultList);
    }

    @CurrentUser
    public void add(List<SpecialistTargetModel> model) {
        Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
        for (SpecialistTargetModel list : model) {
            SpecialistTarget specialistTarget = new SpecialistTarget();
            SpecialistTargetQuery specialistTargetQuery = new SpecialistTargetQuery();
            BeanUtils.copyProperties(list, specialistTargetQuery);
            BeanUtils.copyProperties(model, specialistTarget);
            Integer i = specialistTargetMapper.specialistAddOrUpd(specialistTargetQuery);
            if (i != null) {
                specialistTarget.setCrtId(crtId);
                specialistTargetMapper.add(specialistTarget);
            } else {
                specialistTarget.setUptId(crtId);
                specialistTargetMapper.upd(specialistTarget);
            }
        }
    }

    public BusinessTargetTotalVo findAllData(String ids){
        BusinessTargetTotalVo businessTargetTotalVo = specialistTargetMapper.findAllData(ids);
        return businessTargetTotalVo;
    }
}
