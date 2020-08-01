package com.yunya.modules.emr.controller;

import com.yunya.feign.emr.domain.form.MedicalTempCategoryForm;
import com.yunya.feign.emr.domain.model.MedicalTempCategoryModel;
import com.yunya.feign.emr.domain.vo.TemplateCategoryVo;
import com.yunya.feign.emr.domain.vo.TemplateParentCategoryVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.emr.biz.MedicalTemplateCategoryBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author bruce
 * @date 2020/7/28
 */
@Api(tags = {"公司端-病历模板分类管理"})
@RestController
public class MedicalTemplateCategoryController {

    @Resource
    private MedicalTemplateCategoryBiz categoryBiz;

    @ApiOperation("公司端-病历模板分类-新增")
    @PostMapping("medical/template/category")
    @CurrentUser
    public ResponseResult createRecord(@Valid @RequestBody MedicalTempCategoryModel createModel) {
        categoryBiz.createRecord(createModel);
        return ResponseUtil.success();
    }

    @ApiOperation("公司端-病历模板分类-修改")
    @PutMapping("medical/template/{parentId}/category/{id}")
    @CurrentUser
    public ResponseResult updateRecord(@PathVariable("parentId") Integer parentId,
                                       @PathVariable("id") Integer id,
                                       @Valid @RequestBody MedicalTempCategoryForm updateForm) {
        categoryBiz.updateRecord(parentId, id, updateForm);
        return ResponseUtil.success();
    }


    @ApiOperation("公司端-病历模板分类-删除")
    @DeleteMapping("medical/template/category/{id}")
    public ResponseResult deleteRecord(@PathVariable("id") Integer id) {
        categoryBiz.deleteRecord(id);
        return ResponseUtil.success();
    }

    @ApiOperation("公司端-病历模板分类查询")
    @GetMapping("medical/template/category/all")
    public ResponseResult<TemplateCategoryVo> getChildRecord() {
        List<TemplateCategoryVo> list = categoryBiz.getAllCategory();
        return ResponseUtil.success(list);
    }

    @ApiOperation("父分类字典")
    @GetMapping("medical/template/category/parent")
    public ResponseResult<TemplateParentCategoryVo> getParentCategory() {
        return ResponseUtil.success(categoryBiz.getParentCategory());
    }
}
