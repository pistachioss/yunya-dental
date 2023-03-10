package com.yunya.modules.emr.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.emr.domain.form.MedicalTempCategoryForm;
import com.yunya.feign.emr.domain.form.MedicalTempCategorySortForm;
import com.yunya.feign.emr.domain.model.MedicalTempCategoryModel;
import com.yunya.feign.emr.domain.vo.TemplateCategoryVo;
import com.yunya.feign.emr.domain.vo.TemplateParentCategoryVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.emr.MedicalTemplateCategory;
import com.yunya.modules.emr.mapper.GeneralTemplateMapper;
import com.yunya.modules.emr.mapper.MedicalTemplateCategoryMapper;
import com.yunya.modules.emr.mapper.MedicalTemplateMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        createEntity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        createEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        mapper.insertSelective(createEntity);
        createEntity.setSort(createEntity.getId());
        mapper.updateByPrimaryKeySelective(createEntity);
    }

    public void updateRecord(Integer parentId, Integer id, MedicalTempCategoryForm modifyForm) {
        //校验数据
        checkCategory(id);
        //查询名称是否存在
        int count = mapper.countByName(modifyForm.getName(), parentId, id);
        if (count > 0 ) {
            throw new ClientServiceException("新增分类与系统中已有分类重复，不允许新增！", OperationCodeConstants.NAME_IS_OCCUPIED);
        }
        MedicalTemplateCategory updateEntity = EntityUtils.build(modifyForm, MedicalTemplateCategory.class);
        updateEntity.setId(id);
        updateEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        mapper.updateByPrimaryKeySelective(updateEntity);
    }
    public void updateSort( List<MedicalTempCategorySortForm> medicalTempCategorySortForms) {
        mapper.updateSort(medicalTempCategorySortForms);
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

    /**
     * 病例模板父子分类返回
     * @return
     */
    public List<TemplateCategoryVo> getAllCategory() {
        List<TemplateCategoryVo> resultList = Lists.newArrayList();
        List<MedicalTemplateCategory> list = mapper.selectAll();
        if (CollectionUtils.isEmpty(list)) {
            return resultList;
        }
        //查询父分类集合
        List<MedicalTemplateCategory> parentList = list.stream().filter(obj ->
                            Objects.equals(BusinessConstants.DEFAULT_PARENT_ID, obj.getParentId()))
                            .collect(Collectors.toList());
        parentList.sort(Comparator.comparing(MedicalTemplateCategory::getId));
        //父分类子分类做map映射
        Map<Integer, List<MedicalTemplateCategory>> categoryMap = list.stream().
                                    collect(Collectors.groupingBy(obj -> obj.getParentId()));
        //生成父分类的vo集合
        List<TemplateCategoryVo> parentVos = EntityUtils.build(parentList, TemplateCategoryVo.class);

        parentVos.forEach(superVo -> {
            //取出原始数据子分类集合
            List<MedicalTemplateCategory> childList = categoryMap.get(superVo.getId());
            if (CollectionUtils.isEmpty(childList)) {
                return;
            }
            //子分类进行排序（更新时间倒叙）
            childList.sort(Comparator.comparing(MedicalTemplateCategory::getSort).reversed());
            //构建子分类返回vo
            List<TemplateCategoryVo> childResultList = EntityUtils.build(childList, TemplateCategoryVo.class);
            superVo.setChildList(childResultList);
        });
        return parentVos;
    }

    public List<TemplateParentCategoryVo> getParentCategory() {
        Example example = new Example(MedicalTemplateCategory.class);
        example.createCriteria().andEqualTo("parentId", BusinessConstants.DEFAULT_PARENT_ID);
        example.setOrderByClause("upd_time desc");
        List<MedicalTemplateCategory> list = mapper.selectByExample(example);
        List<TemplateParentCategoryVo> result = EntityUtils.build(list, TemplateParentCategoryVo.class);
        return result;
    }

    private void checkCategory(Integer id) {
        Example example = new Example(MedicalTemplateCategory.class);
        example.createCriteria().andEqualTo("id", id);
        MedicalTemplateCategory category = mapper.selectOneByExample(example);
        if (category == null) {
            throw new ClientServiceException("数据不存在", OperationCodeConstants.DATA_NOT_EXIST);
        }
    }

}
