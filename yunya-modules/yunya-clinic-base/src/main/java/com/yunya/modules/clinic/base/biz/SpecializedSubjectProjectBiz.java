package com.yunya.modules.clinic.base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.query.SpecializedSubjectProjectQuery;
import com.yunya.feign.cash_balance.vo.SpecialistTargetByIdVo;
import com.yunya.feign.cash_balance.vo.SpecializedSubjectProjectByIdVo;
import com.yunya.feign.cash_balance.vo.SpecializedSubjectProjectVo;
import com.yunya.feign.tariff.RemoteTariffServiceFeign;
import com.yunya.models.clinic_base.SpecializedSubjectProject;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseOralTariffCategory;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.tariff.BaseTariffCategory;
import com.yunya.modules.clinic.base.mapper.SpecializedSubjectProjectMapper;
import javafx.scene.Parent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecializedSubjectProjectBiz {
    @Resource
    private SpecializedSubjectProjectMapper  specializedSubjectProjectMapper;

    @Resource
    private RemoteTariffServiceFeign remoteTariffServiceFeign;

    public PageInfo<SpecializedSubjectProjectVo> findSpecializedList(SpecializedSubjectProjectQuery query){
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<SpecializedSubjectProjectVo> resultList = specializedSubjectProjectMapper.findSpecializedList(query);
        for (SpecializedSubjectProjectVo list:resultList) {
            String subitems = list.getSubitems();
            String[] split = subitems.split(",");
            for (String str: split) {
                BaseTariff baseTariffById = remoteTariffServiceFeign.findBaseTariffById(Integer.valueOf(str));
                list.setSubitems(baseTariffById.getName());
            }
        }
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

    public SpecializedSubjectProjectByIdVo findDataById(Integer id){
        SpecializedSubjectProjectByIdVo specializedSubjectProjectByIdVo = specializedSubjectProjectMapper.findDataById(id);
        return specializedSubjectProjectByIdVo;
    }

}