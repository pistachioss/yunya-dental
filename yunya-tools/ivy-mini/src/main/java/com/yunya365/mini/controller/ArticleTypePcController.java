package com.yunya365.mini.controller;

import com.yunya.feign.ivy_mini.domain.form.ArticleTypeForm;
import com.yunya.feign.ivy_mini.domain.vo.ArticleTypeVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.entity.ArticleType;
import com.yunya365.mini.service.impl.ArticleTypeServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/10/12
 * @description:
 */
@RestController
@Api(tags = "后台-文章分类管理")
public class ArticleTypePcController extends PcBaseController{

    @Resource
    private ArticleTypeServiceImpl articleTypeService;

    @PostMapping("/articleType/findlist")
    @ApiOperation("后台-艾维动态/口腔科普/专家介绍-分类-列表")
    public ResponseResult<List<ArticleTypeVO>> findList(@RequestBody @Valid ArticleTypeForm form) {
        return ResponseUtil.success(articleTypeService.findList(form));
    }

    @PostMapping("/articleType/add")
    @ApiOperation("后台-艾维动态/口腔科普/专家介绍-分类-新增")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult add(@RequestBody @Valid ArticleTypeForm form) {
        return ResponseUtil.success(articleTypeService.add(form));
    }

    /**
     * 艾维动态/口腔科普 修改
     *
     * @return ResponseResult
     */
    @ApiOperation("后台-艾维动态/口腔科普/专家介绍-分类-修改")
    @PutMapping("/articleType/update")
    @CurrentUser
    public ResponseResult update(@RequestBody @Validated ArticleTypeForm form) {
        return articleTypeService.update(form);
    }


    @ApiOperation("后台-艾维动态/口腔科普/专家介绍-分类-删除")
    @DeleteMapping("/articleType/delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        return articleTypeService.delete(id);
    }
}
