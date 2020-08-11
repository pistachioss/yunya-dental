package com.yunya.modules.emr.biz;

import com.github.pagehelper.*;
import com.google.common.collect.*;
import com.yunya.feign.emr.domain.form.*;
import com.yunya.feign.emr.domain.model.*;
import com.yunya.feign.emr.domain.query.*;
import com.yunya.feign.emr.domain.vo.*;
import com.yunya.framework.common.constant.*;
import com.yunya.framework.common.context.*;
import com.yunya.framework.common.exception.*;
import com.yunya.framework.common.utils.*;
import com.yunya.models.emr.*;
import com.yunya.modules.emr.enums.*;
import com.yunya.modules.emr.mapper.*;
import org.apache.commons.collections4.*;
import org.springframework.stereotype.*;
import tk.mybatis.mapper.common.*;
import tk.mybatis.mapper.entity.*;

import javax.annotation.*;
import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * @author xiangyang
 * @date 2020/7/29
 */
@Service
public class TemplateBiz {
    @Resource
    private GeneralTemplateMapper generalMapper;

    @Resource
    private MedicalTemplateMapper medicalMapper;

    public void createGeneralRecord(Integer categoryId, GeneralTemplateModel createModel) {
        GeneralTemplate createEntity = EntityUtils.build(createModel, GeneralTemplate.class);
        createEntity.setMedicalTemplateCategoryId(categoryId);
        createEntity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        createEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        generalMapper.insertSelective(createEntity);
    }

    public void updateGeneralRecord(Integer categoryId, Integer templateId, GeneralTemplateForm updateForm) {
        //校验数据是否存在
        checkTemp(categoryId, templateId, generalMapper, GeneralTemplate.class);
        GeneralTemplate updateEntity = EntityUtils.build(updateForm, GeneralTemplate.class);
        updateEntity.setId(templateId);
        updateEntity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        updateEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        generalMapper.updateByPrimaryKeySelective(updateEntity);
    }

    public void enableRecord(Integer categoryId, Integer templateId, Integer enable) {
        generalMapper.enableById(categoryId, templateId, enable);
        medicalMapper.enableById(categoryId, templateId, enable);
    }

    public PageInfo<GeneralTemplatePageVo> getGeneralTemplatePage(Integer categoryId, TemplateQuery query) {
        Page<GeneralTemplate> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        generalMapper.listByKeyword(query.getKeyword(), categoryId, query.getEnable());
        List<GeneralTemplatePageVo> list = Lists.newArrayListWithExpectedSize(page.size());
        list = EntityUtils.build(page.getResult(), GeneralTemplatePageVo.class);
        list.forEach(obj -> obj.setEnable(EnableEnum.getValue(Integer.valueOf(obj.getEnable()))));
        return new PageInfo<>(list);
    }

    public void createMedicalRecord(Integer categoryId, MedicalTemplateModel createModel) {
        int count = medicalMapper.countByName(createModel.getName(), categoryId, null);
        if (count > 0) {
            throw new ClientServiceException("病例模板名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
        }

        MedicalTemplate createEntity = EntityUtils.build(createModel, MedicalTemplate.class);
        createEntity.setMedicalTemplateCategoryId(categoryId);
        createEntity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        createEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        medicalMapper.insertSelective(createEntity);
    }

    public void updateMedicalRecord(Integer categoryId, Integer templateId, MedicalTemplateForm updateForm) {
        //校验数据是否存在
        checkTemp(categoryId, templateId, medicalMapper, MedicalTemplate.class);
        int count = medicalMapper.countByName(updateForm.getName(), categoryId, templateId);
        if (count > 0) {
            throw new ClientServiceException("病例模板名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
        }
        MedicalTemplate updateEntity = EntityUtils.build(updateForm, MedicalTemplate.class);
        updateEntity.setId(templateId);
        updateEntity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        updateEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        medicalMapper.updateByPrimaryKeySelective(updateEntity);
    }

    public PageInfo<MedicalTemplatePageVo> getMedicalTemplatePage(Integer categoryId, TemplateQuery query) {
        Page<MedicalTemplate> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        medicalMapper.listByKeyword(query.getKeyword(), categoryId, query.getEnable());
        List<MedicalTemplatePageVo> list = Lists.newArrayListWithExpectedSize(page.size());
        list = EntityUtils.build(page.getResult(), MedicalTemplatePageVo.class);
        list.forEach(obj -> {
            obj.setEnable(EnableEnum.getValue(Integer.valueOf(obj.getEnable())));
            obj.setType(TemplateTypeEnum.getValue(Integer.valueOf(obj.getType())));
        });
        return new PageInfo<>(list);
    }

    public List<String> getEnableMedicalTemplate(Integer categoryId) {
        List<String> result = Lists.newArrayList();
        List<GeneralTemplate> generalTemps = generalMapper.listByKeyword(null, categoryId, BusinessConstants.ENABLE_NUM);
        if (CollectionUtils.isNotEmpty(generalTemps)) {
            result = generalTemps.stream().map(GeneralTemplate::getContent).collect(toList());
            return result;
        }
        List<MedicalTemplate> medicalTemps = medicalMapper.listByKeyword(null, categoryId, BusinessConstants.ENABLE_NUM);
        if (CollectionUtils.isNotEmpty(medicalTemps)) {
            result = medicalTemps.stream().map(MedicalTemplate::getName).collect(toList());
            return result;
        }
        return result;
    }

    public MedicalDetailDetailVo getMedicalTemplateDetail(Integer templateId) {
        MedicalTemplate entity = medicalMapper.selectByPrimaryKey(templateId);
        MedicalDetailDetailVo detail = EntityUtils.build(entity, MedicalDetailDetailVo.class);
        return detail;
    }

    private void checkTemp(Integer categoryId, Integer templateId, Mapper mapper, Class<?> clazz) {
        Example example = new Example(clazz);
        example.createCriteria().andEqualTo("medicalTemplateCategoryId", categoryId)
                .andEqualTo("id", templateId);
        Object record = mapper.selectOneByExample(example);
        if (record == null) {
            throw new ClientServiceException("数据不存在", OperationCodeConstants.DATA_NOT_EXIST);
        }
    }
}
