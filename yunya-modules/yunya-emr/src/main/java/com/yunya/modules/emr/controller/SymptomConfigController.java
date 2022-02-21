package com.yunya.modules.emr.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.form.SymptomConfigForm;
import com.yunya.feign.emr.domain.model.SymptomConfigModel;
import com.yunya.feign.emr.domain.query.SymptomConfigQuery;
import com.yunya.feign.emr.domain.vo.SymptomConfigVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.emr.biz.SymptomConfigBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author chenlin
 * @description: 症状设置控制器
 * @date 2022/1/07
 */
@Api(tags = {"症状设置控制器"})
@RestController
@RequestMapping("symptomConfig")
public class SymptomConfigController {

    @Autowired
    private SymptomConfigBiz symptomConfigBiz;

    @ApiOperation(value = "新增症状")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult add(@Valid @RequestBody SymptomConfigModel model) {
        return ResponseUtil.success(symptomConfigBiz.add(model));
    }

    @ApiOperation(value = "修改症状")
    @PutMapping("/update")
    @CurrentUser
    public ResponseResult update(@Valid @RequestBody SymptomConfigForm form) {
        return ResponseUtil.success(symptomConfigBiz.update(form));
    }

    @ApiOperation(value = "删除症状")
    @DeleteMapping("/delete/{id}")
    @CurrentUser
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        return ResponseUtil.success(symptomConfigBiz.delete(id));
    }

    @ApiOperation(value = "根据id查询")
    @GetMapping("/one/{id}")
    public ResponseResult<SymptomConfigVO> findOneById(@PathVariable(value = "id") Integer id) {
        return ResponseUtil.success(symptomConfigBiz.findOneById(id));
    }

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public ResponseResult<PageInfo<SymptomConfigVO>> findList(@Valid @RequestBody SymptomConfigQuery query) {
        PageInfo<SymptomConfigVO> page = symptomConfigBiz.findSymptomConfigList(query);
        return ResponseUtil.success(page);
    }
}
