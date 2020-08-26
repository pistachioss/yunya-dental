package com.yunya.modules.clinic.base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.query.CashBalanceQuery;
import com.yunya.feign.cash_balance.query.SpecializedSubjectProjecQuery;
import com.yunya.feign.cash_balance.vo.CashBalanceVo;
import com.yunya.feign.cash_balance.vo.SpecializedSubjectProjecVo;
import com.yunya.models.clinic_base.CashBalance;
import com.yunya.models.clinic_base.SpecializedSubjectProjec;
import com.yunya.modules.clinic.base.mapper.CashBalanceMapper;
import com.yunya.modules.clinic.base.mapper.SpecializedSubjectProjecMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecializedSubjectProjecBiz {
    @Resource
    private SpecializedSubjectProjecMapper  specializedSubjectProjecMapper;

    public PageInfo<SpecializedSubjectProjecVo> findSpecializedList(SpecializedSubjectProjecQuery query){
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<SpecializedSubjectProjecVo> resultList = specializedSubjectProjecMapper.findSpecializedList(query);
        return new PageInfo<>(resultList);
    }
    public void  add(SpecializedSubjectProjec specializedSubjectProjec){

        specializedSubjectProjecMapper.add(specializedSubjectProjec);
    }
    public void  upd(SpecializedSubjectProjec specializedSubjectProjec){
        specializedSubjectProjecMapper.upd(specializedSubjectProjec);
    }
    public void  del(Integer id){
        specializedSubjectProjecMapper.del(id);
    }

}