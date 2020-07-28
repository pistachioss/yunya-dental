package com.yunya.modules.emr.biz;

import com.yunya.feign.emr.domain.form.MedicalTempCategoryForm;
import com.yunya.feign.emr.domain.model.MedicalTempCategoryModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.emr.MedicalTemplateCategory;
import com.yunya.modules.emr.mapper.GeneralTemplateMapper;
import com.yunya.modules.emr.mapper.MedicalTemplateCategoryMapper;
import com.yunya.modules.emr.mapper.MedicalTemplateMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author bruce
 * @date 2020/7/28
 */
@Service
public class MedicalTemplateCategoryBiz extends BaseBiz<MedicalTemplateCategoryMapper, MedicalTemplateCategory> {

    @Resource
    private MedicalTemplateMapper medicalTemplateMapper;

    @Resource
    private GeneralTemplateMapper generalTemplateMapper;

    public void createRecord(MedicalTempCategoryModel createModel) {
        //查询名称是否存在
        int count = mapper.countByName(createModel.getName(), createModel.getParentId(), null);
        if (count > 0 ) {
            throw new ClientServiceException("新增分类与系统中已有分类重复，不允许新增！", OperationCodeConstants.NAME_IS_OCCUPIED);
        }
        MedicalTemplateCategory createEntity = EntityUtils.build(createModel, MedicalTemplateCategory.class);
        mapper.insertSelective(createEntity);
    }

    public void updateRecord(Integer id, MedicalTempCategoryForm modifyForm) {
        //查询名称是否存在
        int count = mapper.countByName(modifyForm.getName(), modifyForm.getParentId(), id);
        if (count > 0 ) {
            throw new ClientServiceException("新增分类与系统中已有分类重复，不允许新增！", OperationCodeConstants.NAME_IS_OCCUPIED);
        }
        MedicalTemplateCategory createEntity = EntityUtils.build(modifyForm, MedicalTemplateCategory.class);
        createEntity.setId(id);
        mapper.updateByPrimaryKeySelective(createEntity);
    }

    public void deleteRecord(Integer id) {
        //查询分类对应的模板数
        int genCount = generalTemplateMapper.countByCategoryId(id);
        int medCount = medicalTemplateMapper.countByCategoryId(id);
        if (genCount > 0 || medCount > 0) {
            throw new ClientServiceException("该病历模板子分类下有词条或者病历模板，不允许删除！", OperationCodeConstants.NAME_IS_OCCUPIED);
        }
        MedicalTemplateCategory category = new MedicalTemplateCategory();
        category.setId(id);
        mapper.deleteByPrimaryKey(category);
    }

}
