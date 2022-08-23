package com.yunya365.mini.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.discount.domain.vo.ProductTypeVO;
import com.yunya.feign.ivy_mini.domain.query.GoodsQuery;
import com.yunya.feign.ivy_mini.domain.query.VirtualProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.*;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.IProductService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2022/4/28 15:52
 **/
@Api(tags = {"商品、虚拟服务api"})
@RestController
@IgnoreUserToken
public class ProductController extends BaseController{

    @Resource
    private IProductService productService;

    @PostMapping("/goods/search")
    @ApiOperation("【小程序】商品搜索")
    public ResponseResult<PageInfo<GoodsVO>> pageGoods(@RequestBody @Valid GoodsQuery query) {
        return ResponseUtil.success(productService.pageGoods(query));
    }

    @PostMapping("/virtual/search")
    @ApiOperation("【小程序】虚拟服务搜索")
    public ResponseResult<PageInfo<VirtualProductVO>> pageVirtual(@RequestBody @Valid VirtualProductQuery query) {
        return ResponseUtil.success(productService.pageVirtual(query));
    }

    @GetMapping("/hot/product")
    @ApiOperation("【小程序】热销产品")
    public ResponseResult<List<HotSaleVO>> hotSale() {
        return ResponseUtil.success(productService.hotSale());
    }

    @GetMapping("/goods/{id}")
    @ApiOperation("【小程序】商品详情-价目")
    public ResponseResult<GoodsDetailVO> goodsDetail(@PathVariable Integer id) {
        return ResponseUtil.success(productService.goodsDetail(id));
    }

    @GetMapping("/virtual/{id}")
    @ApiOperation("【小程序】虚拟服务详情-价目")
    public ResponseResult<VirtualDetailVO> virtualDetail(@PathVariable Integer id) {
        return ResponseUtil.success(productService.virtualDetail(id));
    }

    @GetMapping("/category/{type}")
    @ApiImplicitParam(name = "type", value = "商品或虚拟服务分类（0-商品 1-虚拟服务）", required = true, dataType = "int")
    @ApiOperation("【小程序】查询商品分类或虚拟服务分类")
    public ResponseResult<List<ProductTypeVO>> cateGoryList(@PathVariable Integer type) {
        return ResponseUtil.success(productService.cateGoryList(type));
    }
}
