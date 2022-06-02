package com.yunya365.mini.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya365.mini.service.impl.ArticleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/2
 * @description: 小程序-文章管理
 */
@RestController
@Api(tags = "小程序-文章管理")
public class AppletArticleController {
    @Resource
    private ArticleServiceImpl articleService;

    @ApiOperation("小程序-艾维动态/口腔科普-阅读数+1")
    @PutMapping("/addReading/{id}")
    public ResponseResult addReading(@PathVariable(value = "id") Integer id) {
        return articleService.addReading(id);
    }
}
