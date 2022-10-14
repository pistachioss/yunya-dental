package com.yunya365.mini.controller;

import com.yunya.feign.ivy_mini.domain.form.ArticleTypeForm;
import com.yunya.feign.ivy_mini.domain.vo.ArticleTypeVO;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.ArticleTypeServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
@Api(tags = "小程序-文章分类")
public class ArticleTypeAppController extends BaseController{

    @Resource
    private ArticleTypeServiceImpl articleTypeService;

    @PostMapping("/articleType/findlist")
    @ApiOperation("小程序-艾维动态/口腔科普/专家介绍-分类-列表")
    @IgnoreUserToken
    public ResponseResult<List<ArticleTypeVO>> findList(@RequestBody @Valid ArticleTypeForm form) {
        List<ArticleTypeVO> list = articleTypeService.findList(form);
        ArticleTypeVO vo = new ArticleTypeVO();
        vo.setName("全部");
        vo.setType(form.getType());
        list.add(0,vo);
        return ResponseUtil.success(list);
    }
}
