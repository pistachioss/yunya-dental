package com.yunya365.mini.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.query.GoodsQuery;
import com.yunya.feign.ivy_mini.domain.query.VirtualProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.GoodsVO;
import com.yunya.feign.ivy_mini.domain.vo.VirtualProductVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.IProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @description:
 * @author: xy
 * @date 2022/4/28 15:52
 **/
@Api(tags = {"商品、虚拟服务api"})
@RestController
@RequestMapping("/white")
public class ProductController {

    @Resource
    private IProductService productService;

    @PostMapping("/goods/search")
    @ApiOperation("商品搜索")
    public ResponseResult<PageInfo<GoodsVO>> pageGoods(@RequestBody @Valid GoodsQuery query) {
        return ResponseUtil.success(productService.pageGoods(query));
    }

    @PostMapping("/virtual/search")
    @ApiOperation("虚拟服务搜索")
    public ResponseResult<PageInfo<VirtualProductVO>> pageVirtual(@RequestBody @Valid VirtualProductQuery query) {
        return ResponseUtil.success(productService.pageVirtual(query));
    }
}
