package com.yunya.modules.emr.biz;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.emr.domain.form.GeneralTemplateForm;
import com.yunya.feign.emr.domain.form.MedicalTemplateForm;
import com.yunya.feign.emr.domain.model.GeneralTemplateModel;
import com.yunya.feign.emr.domain.model.MedicalTemplateModel;
import com.yunya.feign.emr.domain.query.TemplateQuery;
import com.yunya.feign.emr.domain.vo.GeneralTemplatePageVo;
import com.yunya.feign.emr.domain.vo.MedicalDetailDetailVo;
import com.yunya.feign.emr.domain.vo.MedicalTemplatePageVo;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.emr.GeneralTemplate;
import com.yunya.models.emr.MedicalTemplate;
import com.yunya.modules.emr.enums.EnableEnum;
import com.yunya.modules.emr.enums.TemplateTypeEnum;
import com.yunya.modules.emr.mapper.GeneralTemplateMapper;
import com.yunya.modules.emr.mapper.MedicalTemplateMapper;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

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

    public void updateGeneralRecord(Integer categoryId, Integer templateId,  GeneralTemplateForm updateForm) {
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
        generalMapper.listByKeyword(query.getKeyword(), categoryId);
        List<GeneralTemplatePageVo> list = Lists.newArrayListWithExpectedSize(page.size());
        list = EntityUtils.build(page.getResult(), GeneralTemplatePageVo.class);
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
        medicalMapper.listByKeyword(query.getKeyword(), categoryId);
        List<MedicalTemplatePageVo> list = Lists.newArrayListWithExpectedSize(page.size());
        list = EntityUtils.build(page.getResult(), MedicalTemplatePageVo.class);
        list.forEach(obj -> {
            obj.setEnable(EnableEnum.getValue(Integer.valueOf(obj.getEnable())));
            obj.setType(TemplateTypeEnum.getValue(Integer.valueOf(obj.getType())));
        });
        return new PageInfo<>(list);
    }

    public MedicalDetailDetailVo getMedicalTemplateDetail(Integer templateId) {
        MedicalTemplate entity = medicalMapper.selectByPrimaryKey(templateId);
        MedicalDetailDetailVo detail = EntityUtils.build(entity, MedicalDetailDetailVo.class);
        detail.setType(TemplateTypeEnum.getValue(Integer.valueOf(entity.getType())));
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
