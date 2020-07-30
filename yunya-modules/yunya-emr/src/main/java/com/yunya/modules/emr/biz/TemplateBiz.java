package com.yunya.modules.emr.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.emr.domain.form.GeneralTemplateForm;
import com.yunya.feign.emr.domain.model.GeneralTemplateModel;
import com.yunya.feign.emr.domain.model.MedicalTemplateModel;
import com.yunya.feign.emr.domain.query.GeneralTemplateQuery;
import com.yunya.feign.emr.domain.vo.GeneralTemplatePageVo;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.emr.GeneralTemplate;
import com.yunya.models.emr.MedicalTemplate;
import com.yunya.modules.emr.mapper.GeneralTemplateMapper;
import com.yunya.modules.emr.mapper.MedicalTemplateMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

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
        generalMapper.insertSelective(createEntity);
    }

    public void updateGeneralRecord(Integer categoryId, Integer templateId,  GeneralTemplateForm updateForm) {
        GeneralTemplate updateEntity = EntityUtils.build(updateForm, GeneralTemplate.class);
        updateEntity.setMedicalTemplateCategoryId(categoryId);
        updateEntity.setId(templateId);
        generalMapper.updateByPrimaryKeySelective(updateEntity);
    }

    public void deleteGeneralRecord(Integer templateId) {
        //todo 查询词条关联的病例
        GeneralTemplate deleteEntity = new GeneralTemplate();
        deleteEntity.setId(templateId);
        generalMapper.delete(deleteEntity);
    }

    public PageInfo<GeneralTemplatePageVo> getGeneralTemplatePage(Integer categoryId, GeneralTemplateQuery query) {
        List<GeneralTemplatePageVo> list = Lists.newArrayList();
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<GeneralTemplate> templates = generalMapper.listByKeyword(query.getKeyword(), categoryId);
        if (CollectionUtils.isEmpty(templates)) {
            return new PageInfo<>(list);
        }
        list = EntityUtils.build(templates, GeneralTemplatePageVo.class);
        return new PageInfo<>(list);
    }

    public void createMedicalRecord(MedicalTemplateModel createModel) {
        int count = medicalMapper.countByName(createModel.getName(), null);
        if (count > 0) {
            throw new ClientServiceException("病例模板名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
        }
        MedicalTemplate createEntity = EntityUtils.build(createModel, MedicalTemplate.class);
        return;
    }


}
