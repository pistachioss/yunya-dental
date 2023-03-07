package com.yunya.modules.emr.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.form.GeneralTemplateForm;
import com.yunya.feign.emr.domain.form.MedicalTempCategorySortForm;
import com.yunya.feign.emr.domain.form.MedicalTemplateForm;
import com.yunya.feign.emr.domain.model.GeneralTemplateModel;
import com.yunya.feign.emr.domain.model.MedicalTemplateModel;
import com.yunya.feign.emr.domain.query.TemplateQuery;
import com.yunya.feign.emr.domain.vo.EnableTemplateVo;
import com.yunya.feign.emr.domain.vo.GeneralTemplatePageVo;
import com.yunya.feign.emr.domain.vo.MedicalDetailDetailVo;
import com.yunya.feign.emr.domain.vo.MedicalTemplatePageVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.emr.biz.TemplateBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/7/29
 */
@Api(tags = {"公司端-模板内容管理（词条，范句，要点，诊断，病例模板）"})
@RestController
public class TemplateController {

    @Resource
    private TemplateBiz templateBiz;


//    @ApiOperation("公司端-普通模板-排序修改")
//    @PutMapping("medical/template/updateGenSort")
//    @CurrentUser
//    public ResponseResult updateGenSort(@RequestBody List<MedicalTempCategorySortForm> medicalTempCategorySortForms) {
//        templateBiz.updateGenSort(medicalTempCategorySortForms);
//        return ResponseUtil.success();
//    }

    @ApiOperation("公司端-普通模板新增")
    @PostMapping("template/{categoryId}/general")
    @CurrentUser
    public ResponseResult createGeneralRecord(@PathVariable(value = "categoryId") Integer categoryId,
                                              @Valid @RequestBody GeneralTemplateModel createModel) {
        templateBiz.createGeneralRecord(categoryId, createModel);
        return ResponseUtil.success();
    }

    @ApiOperation("公司端-普通模板修改")
    @PutMapping("template/{categoryId}/general/{templateId}")
    @CurrentUser
    public ResponseResult updateGeneralRecord(@PathVariable(value = "templateId") Integer templateId,
                                              @PathVariable(value = "categoryId") Integer categoryId,
                                              @Valid @RequestBody GeneralTemplateForm updateForm) {
        templateBiz.updateGeneralRecord(categoryId, templateId, updateForm);
        return ResponseUtil.success();
    }

    @ApiOperation("公司端-模板启用/禁用（普通和病例）")
    @PutMapping("template/{categoryId}/{templateId}/{enable}")
    public ResponseResult enableRecord(@PathVariable(value = "templateId") Integer templateId,
                                       @PathVariable(value = "categoryId") Integer categoryId,
                                       @PathVariable(value = "enable") Integer enable) {
        templateBiz.enableRecord(categoryId, templateId, enable);
        return ResponseUtil.success();
    }

    @ApiOperation("公司端-普通模板分页查询")
    @PostMapping("template/{categoryId}/general/page")
    public ResponseResult<PageInfo<GeneralTemplatePageVo>> queryGeneralPageRecord(@PathVariable(value = "categoryId") Integer categoryId,
                                              @RequestBody TemplateQuery query) {
        PageInfo<GeneralTemplatePageVo> pageResult = templateBiz.getGeneralTemplatePage(categoryId, query);
        return ResponseUtil.success(pageResult);
    }

    @ApiOperation("公司端-病例模板新增")
    @PostMapping("template/{categoryId}/medical")
    @CurrentUser
    public ResponseResult createMedicalRecord(@PathVariable(value = "categoryId") Integer categoryId,
                                              @Valid @RequestBody MedicalTemplateModel createModel) {
        templateBiz.createMedicalRecord(categoryId, createModel);
        return ResponseUtil.success();
    }
    @ApiOperation("公司端-病历模板-排序修改")
    @PutMapping("medical/template/sort")
    @ApiImplicitParam(name="type", value="分类（0：普通模板 1：病例模板）", dataType = "integer")
    @CurrentUser
    public ResponseResult updateSort(@PathVariable(value = "type") Integer type,
                                     @RequestBody List<MedicalTempCategorySortForm> medicalTempCategorySortForms) {
        if(type == 0){
            templateBiz.updateGenSort(medicalTempCategorySortForms);
        }else{
            templateBiz.updateSort(medicalTempCategorySortForms);
        }

        return ResponseUtil.success();
    }
    @ApiOperation("公司端-病例模板修改")
    @PutMapping("template/{categoryId}/medical/{templateId}")
    @CurrentUser
    public ResponseResult updateMedicalRecord(@PathVariable(value = "categoryId") Integer categoryId,
                                              @PathVariable(value = "templateId") Integer templateId,
                                              @Valid @RequestBody MedicalTemplateForm updateForm) {
        templateBiz.updateMedicalRecord(categoryId, templateId, updateForm);
        return ResponseUtil.success();
    }

    @ApiOperation("公司端病例模板分页查询")
    @PostMapping("template/{categoryId}/medical/page")
    public ResponseResult<PageInfo<MedicalTemplatePageVo>> queryMedicalPageRecord(@PathVariable(value = "categoryId") Integer categoryId,
                                                                                 @RequestBody TemplateQuery query) {
        PageInfo<MedicalTemplatePageVo> pageResult = templateBiz.getMedicalTemplatePage(categoryId, query);
        return ResponseUtil.success(pageResult);
    }

    @ApiOperation("公司端病例模板详情")
    @GetMapping("template/medical/{templateId}")
    public ResponseResult<MedicalDetailDetailVo> getMedicalTemplateRecord(@PathVariable(value = "templateId") Integer templateId) {
        MedicalDetailDetailVo result = templateBiz.getMedicalTemplateDetail(templateId);
        return ResponseUtil.success(result);
    }

    /**
     * 电子病例-病例模板内容查询（无条件查询）
     * @param categoryId categoryId
     * @param type type（0：初复诊（0：初诊 1：复诊））
     * @return Response
     */
    @ApiOperation("电子病例-病例模板内容查询（无条件查询）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="categoryId", value="分类id",dataType = "integer", required=true),
            @ApiImplicitParam(name="type", value="初复诊（0：初诊 1：复诊）", dataType = "integer")
    })
    @GetMapping("template/{categoryId}/medical/list")
    public ResponseResult<List<EnableTemplateVo>> getTemplateList(@PathVariable(value = "categoryId") Integer categoryId, @RequestParam Integer type) {
        List<EnableTemplateVo> list = templateBiz.getEnableMedicalTemplate(categoryId, type);
        return ResponseUtil.success(list);
    }

    @ApiOperation("公司端-模板-删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="模板id",dataType = "integer", required=true),
            @ApiImplicitParam(name="type", value="模板类型（0：普通模板（词条，范句，要点，诊断） 1：病历模板）", dataType = "integer", required=true)
    })
    @DeleteMapping("template/{type}/{id}")
    public ResponseResult deleteRecord(@PathVariable("type") Integer type, @PathVariable("id") Integer id) {
        templateBiz.deleteRecord(type, id);
        return ResponseUtil.success();
    }
}
