package com.yunya365.mini.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.AdjustOrderReq;
import com.yunya.feign.ivy_mini.domain.form.ArticleAddForm;
import com.yunya.feign.ivy_mini.domain.form.ArticleForm;
import com.yunya.feign.ivy_mini.domain.form.ArticleUpdateForm;
import com.yunya.feign.ivy_mini.domain.vo.ArticleVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.ArticleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/10
 * @description: 文章管理
 */
@RestController
@Api(tags = "后台-文章管理")
public class ArticlePcController extends PcBaseController{

    @Resource
    private ArticleServiceImpl articleService;

    @PostMapping("/article/findlist")
    @ApiOperation("后台-艾维动态/口腔科普-列表")
    public ResponseResult<PageInfo<ArticleVO>> findList(@RequestBody @Valid ArticleForm form) {
        return ResponseUtil.success(articleService.findList(form));
    }

    /**
     * 艾维动态/口腔科普 置顶列表更新
     *
     * @return ResponseResult
     */
    @ApiOperation("后台-艾维动态/口腔科普-置顶列表更新")
    @PutMapping("/article/updateList/{type}")
    public ResponseResult updateList(@PathVariable(value = "type") Integer type,@RequestBody @Validated List<ArticleVO>list) {
        articleService.updateList(type,list);
        return ResponseUtil.success(null);
    }



    @PostMapping("/article/add")
    @ApiOperation("后台-艾维动态/口腔科普-新增")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult add(@RequestBody @Valid ArticleAddForm form) {
        articleService.add(form);
        return ResponseUtil.success(null);
    }

    /**
     * 艾维动态/口腔科普 修改
     *
     * @return ResponseResult
     */
    @ApiOperation("后台-艾维动态/口腔科普-修改/上下架")
    @PutMapping("/article/update")
    @CurrentUser
    public ResponseResult update(@RequestBody @Validated ArticleUpdateForm form) {
        return articleService.update(form);
    }


    @ApiOperation("后台-艾维动态/口腔科普-删除")
    @DeleteMapping("/article/delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        return articleService.delete(id);
    }

//    @PostMapping("/article/adjustorder")
//    @ApiOperation("后台-艾维动态/口腔科普-上移下移")
//    public ResponseResult adjustOrder(@RequestBody @Validated AdjustOrderReq orderReq){
//        return articleService.adjustOrder(orderReq);
//    }

}
