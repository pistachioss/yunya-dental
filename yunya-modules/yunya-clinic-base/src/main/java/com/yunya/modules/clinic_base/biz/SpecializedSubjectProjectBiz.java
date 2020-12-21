package com.yunya.modules.clinic_base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.domain.query.SpecializedSubjectProjectQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecializedSubjectProjectVo;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.models.clinic_base.SpecializedSubjectProject;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.modules.clinic_base.mapper.SpecializedSubjectProjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 简介: 专科项目设置管理
 *
 * @author: Zkq
 * @date: 2020/8/20 14:53
 * @description:
 * @since: 1.0.0
 */
@Service
public class SpecializedSubjectProjectBiz {
    @Resource
    private SpecializedSubjectProjectMapper specializedSubjectProjectMapper;

    @Resource
    private RemoteTreatmentServiceFeign remoteTariffServiceFeign;

    /**
     * 专科项目设置列表
     *
     * @param query 专科项目设置列表
     * @return resultList
     */
    public PageInfo<SpecializedSubjectProjectVo> findSpecializedList(SpecializedSubjectProjectQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<SpecializedSubjectProjectVo> resultList = specializedSubjectProjectMapper.findSpecializedList(query);
        for (SpecializedSubjectProjectVo list : resultList) {
            String subitems = list.getSubitems();
            String[] split = subitems.split(",");
            String subitem = "";
            for (String str : split) {
                BaseTariff baseTariffById = remoteTariffServiceFeign.findBaseTariffById(Integer.valueOf(str));
                subitem += "," + baseTariffById.getName();
                String substring = subitem.substring(1);
                list.setSubitems(substring);
            }
        }
        return new PageInfo<>(resultList);
    }

    /**
     * 添加专科项目设置
     *
     * @param specializedSubjectProject 专科项目设置列表
     */
    public void add(SpecializedSubjectProject specializedSubjectProject) {
        specializedSubjectProjectMapper.add(specializedSubjectProject);
    }

    /**
     * 修改专科项目设置
     *
     * @param specializedSubjectProject 修改专科项目设置
     */
    public void upd(SpecializedSubjectProject specializedSubjectProject) {
        specializedSubjectProjectMapper.upd(specializedSubjectProject);
    }

    /**
     * 删除专科项目设置
     *
     * @param id 通过id删除数据
     */
    public void del(Integer id) {
        specializedSubjectProjectMapper.del(id);
    }

}