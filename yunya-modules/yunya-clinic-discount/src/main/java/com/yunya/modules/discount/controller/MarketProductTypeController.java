package com.yunya.modules.discount.controller;

import com.yunya.modules.discount.biz.MarketProductTypeBiz;
import com.yunya.models.discount.MarketProductType;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-09 12:32
 */
@Api(tags = "产品分类")
@RestController
@RequestMapping("/market_product_type")
public class MarketProductTypeController {
    @Autowired
    private MarketProductTypeBiz marketProductTypeBiz;

    /**
     * 新增产品分类
     *
     * @param marketProductType
     * @return
     */
    @PostMapping
    @ApiOperation("新增产品分类")
    public ResponseResult save(@RequestBody @Valid MarketProductType marketProductType) {
        marketProductTypeBiz.saveMarketProductType(marketProductType);
        return ResponseUtil.success();
    }

    /**
     * 修改产品分类
     *
     * @param marketProductType
     * @return
     */
    @PutMapping
    @ApiOperation("修改产品分类")
    public ResponseResult update(@RequestBody MarketProductType marketProductType) {
        marketProductTypeBiz.updateMarketProductType(marketProductType);
        return ResponseUtil.success();
    }

    /**
     * 获取产品分类
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("获取产品分类")
    public ResponseResult find(@PathVariable(name = "id") Integer id) {
        return ResponseUtil.success(marketProductTypeBiz.selectById(id));
    }

    /**
     * 列表
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("列表")
    public ResponseResult list() {
        return ResponseUtil.success(marketProductTypeBiz.selectListAll());
    }

    /**
     * 删除
     *
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除")
    public ResponseResult delete(@PathVariable(name = "id") Integer id) {
        marketProductTypeBiz.deleteById(id);
        return ResponseUtil.success();
    }
}
