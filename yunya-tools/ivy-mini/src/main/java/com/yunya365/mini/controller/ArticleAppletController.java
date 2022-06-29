package com.yunya365.mini.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.ArticleForm;
import com.yunya.feign.ivy_mini.domain.vo.ArticleVO;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.ArticleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/2
 * @description: 小程序-文章管理
 */
@RestController
@Api(tags = "小程序-文章管理")
public class ArticleAppletController extends BaseController{
    @Resource
    private ArticleServiceImpl articleService;

    @ApiOperation("小程序-艾维动态/口腔科普-阅读数+1")
    @PutMapping("/addReading/{id}")
    public ResponseResult addReading(@PathVariable(value = "id") Integer id) {
        return articleService.addReading(id);
    }


    @PostMapping("/article/findlist")
    @ApiOperation("小程序-口腔科普/艾维动态-列表(传type和status即可)")
    @IgnoreUserToken
    public ResponseResult<PageInfo<ArticleVO>> findList(@RequestBody @Valid ArticleForm form) {
        return ResponseUtil.success(articleService.findList(form));
    }

}
