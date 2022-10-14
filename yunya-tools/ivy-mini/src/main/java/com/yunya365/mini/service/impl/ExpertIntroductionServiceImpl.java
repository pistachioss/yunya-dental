package com.yunya365.mini.service.impl;


import cn.hutool.core.convert.Convert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.ExpertIntroductionAddAndUpdateForm;
import com.yunya.feign.ivy_mini.domain.form.ExpertIntroductionForm;
import com.yunya.feign.ivy_mini.domain.vo.ExpertIntroductionVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.entity.ExpertIntroduction;
import com.yunya365.mini.entity.ExpertType;
import com.yunya365.mini.mapper.ExpertIntroductionMapper;
import com.yunya365.mini.mapper.ExpertTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/10
 * @description: 专家介绍
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ExpertIntroductionServiceImpl extends BaseBiz<ExpertIntroductionMapper, ExpertIntroduction> {

    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    private ExpertTypeMapper expertTypeMapper;

    public PageInfo<ExpertIntroductionVO> findList(ExpertIntroductionForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<ExpertIntroductionVO> result = mapper.findExpertIntroductionList(form);
        //获取门诊信息
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> clinicMap = new HashMap(16);
        clinics.forEach(z -> clinicMap.put(z.getId() + "", z));
        for(ExpertIntroductionVO expertIntroductionVO:result){
            StringBuffer clinicName  = new StringBuffer();
            if(!StringUtils.isEmpty(expertIntroductionVO.getVisitClinic())){
                String[]clinicIds = expertIntroductionVO.getVisitClinic().split(",");
                Integer[]clinicIdsint = Convert.toIntArray(clinicIds);
                expertIntroductionVO.setVisitClinicIds(clinicIdsint);
                if(clinicIds.length>0){
                    for (String clinicId:clinicIds){
                        clinicName = clinicName.append(clinicMap.get(clinicId).getAbbreviation()+",");
                    }
                }
            }
            if(!StringUtils.isEmpty(expertIntroductionVO.getTypeId())){
                String[]typeIds = expertIntroductionVO.getTypeId().split(",");
                Integer[]typeIdsint = Convert.toIntArray(typeIds);
                expertIntroductionVO.setTypeIds(typeIdsint);
            }
            expertIntroductionVO.setVisitClinic(clinicName.toString());
        }
        return new PageInfo<>(result);
    }

    public ExpertIntroductionVO findDetail(Integer id) {
        ExpertIntroductionVO result = new ExpertIntroductionVO();
        ExpertIntroduction expertIntroduction = mapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(expertIntroduction,result);

        //获取门诊信息
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> clinicMap = new HashMap(16);
        clinics.forEach(z -> clinicMap.put(z.getId() + "", z));

        StringBuffer clinicName  = new StringBuffer();
        if(!StringUtils.isEmpty(result.getVisitClinic())){
            String[]clinicIds = result.getVisitClinic().split(",");
            Integer[]clinicIdsint = Convert.toIntArray(clinicIds);
            result.setVisitClinicIds(clinicIdsint);
            if(clinicIds.length>0){
                for (String clinicId:clinicIds){
                    clinicName = clinicName.append(clinicMap.get(clinicId).getAbbreviation()+",");
                }
            }
        }
        result.setVisitClinic(clinicName.toString());
        return result;
    }

    public void add(ExpertIntroductionAddAndUpdateForm form) {
        ExpertIntroduction entity = new ExpertIntroduction();
        BeanUtils.copyProperties(form, entity);
        String str= StringUtils.join(form.getVisitClinic(),",");
        entity.setVisitClinic(str);
        //测试用，发布切换
//        entity.setCrtId(1);
        entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        entity.setCrtTime(new Date(System.currentTimeMillis()));
        mapper.insertSelective(entity);
        //科室id数组
        Integer[]ids = form.getTypeIds();
        List<ExpertType>List = new ArrayList();
        for(Integer id:ids){
            ExpertType expertType = new ExpertType();
            expertType.setTypeId(id);
            expertType.setExpertId(entity.getId());
            List.add(expertType);
        }
        expertTypeMapper.BatchInsert(List);
    }

    public ResponseResult update(ExpertIntroductionAddAndUpdateForm form) {
        Integer id = form.getId();
        ExpertIntroduction expertIntroduction = mapper.selectByPrimaryKey(id);
        if (expertIntroduction == null) {
            return ResponseUtil.success("修改的记录不存在！");
        }

        BeanUtils.copyProperties(form, expertIntroduction);

        String str= StringUtils.join(form.getVisitClinic(),",");
        expertIntroduction.setVisitClinic(str);
//        expertIntroduction.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        //测试用，发布切换
        expertIntroduction.setUptId(1);
        expertIntroduction.setUpdTime(new Date(System.currentTimeMillis()));

        int result = mapper.updateByPrimaryKeySelective(expertIntroduction);
        if (result <= 0){
            return ResponseUtil.success("数据修改失败！");
        }
        //科室id数组
        Integer[]ids = form.getTypeIds();
        List<ExpertType>List = new ArrayList();
        for(Integer tid:ids){
            ExpertType expertType = new ExpertType();
            expertType.setTypeId(tid);
            expertType.setExpertId(id);
            List.add(expertType);
        }
        ExpertType delete = new ExpertType();
        delete.setExpertId(id);
        expertTypeMapper.delete(delete);
        expertTypeMapper.BatchInsert(List);
        return ResponseUtil.success();
    }
    public ResponseResult delete(Integer id) {
        ExpertIntroduction expertIntroduction = mapper.selectByPrimaryKey(id);
        if (expertIntroduction == null) {
            return ResponseUtil.success("删除的记录不存在！");
        }
        int result = mapper.delete(expertIntroduction);
        if (result <= 0){
            return ResponseUtil.success("数据删除失败！");
        }
        ExpertType delete = new ExpertType();
        delete.setExpertId(id);
        expertTypeMapper.delete(delete);
        return ResponseUtil.success();
    }
}
