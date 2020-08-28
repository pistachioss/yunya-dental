package com.yunya.modules.clinic.base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.query.SpecializedSubjectProjectQuery;
import com.yunya.feign.cash_balance.vo.SpecializedSubjectProjectVo;
import com.yunya.models.clinic_base.SpecializedSubjectProject;
import com.yunya.modules.clinic.base.mapper.SpecializedSubjectProjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecializedSubjectProjectBiz {
    @Resource
    private SpecializedSubjectProjectMapper  specializedSubjectProjectMapper;

    public PageInfo<SpecializedSubjectProjectVo> findSpecializedList(SpecializedSubjectProjectQuery query){
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<SpecializedSubjectProjectVo> resultList = specializedSubjectProjectMapper.findSpecializedList(query);
        return new PageInfo<>(resultList);
    }
    public void  add(SpecializedSubjectProject specializedSubjectProject){

        specializedSubjectProjectMapper.add(specializedSubjectProject);
    }
    public void  upd(SpecializedSubjectProject specializedSubjectProject){
        specializedSubjectProjectMapper.upd(specializedSubjectProject);
    }
    public void  del(Integer id){
        specializedSubjectProjectMapper.del(id);
    }

}