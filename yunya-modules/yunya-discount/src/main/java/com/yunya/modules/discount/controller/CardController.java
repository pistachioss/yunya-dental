package com.yunya.modules.discount.controller;

import com.alibaba.excel.*;
import com.github.pagehelper.*;
import com.yunya.feign.discount.domain.model.*;
import com.yunya.feign.discount.domain.query.*;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.framework.common.annation.*;
import com.yunya.framework.common.model.*;
import com.yunya.framework.common.utils.*;
import com.yunya.modules.discount.biz.*;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.*;
import javax.servlet.http.*;
import javax.validation.*;
import java.io.*;
import java.net.*;
import java.util.*;

import static com.yunya.framework.common.constant.BusinessConstants.*;

/**
 * 描述:
 *
 * @author xiangyang
 * @create 2020-08-19
 */
@Api(tags = {"卡券"})
@RestController
public class CardController {

    @Resource
    private CardBiz cardBiz;

    @ApiOperation(value = "产品生成分配分页查询")
    @PostMapping("/coupon/allocation/generate/page")
    public ResponseResult<PageInfo<GenerateAllocatePageVo>> getGenerateAllocateList(@Valid @RequestBody CouponAllocateQuery query) {
        PageInfo<GenerateAllocatePageVo> pageInfo = cardBiz.getCouponAllocatePage(query);
        return ResponseUtil.success(pageInfo);
    }

    @ApiOperation(value = "产品生成分配--生成分配")
    @PostMapping("/coupon/generate/allocation")
    @CurrentUser
    public ResponseResult generateCard(@Valid @RequestBody GenerateAllocateModel allocateModel) throws Exception {
        return cardBiz.generateAllocate(allocateModel);
    }

    @ApiOperation(value = "产品生成分配--查看配给")
    @PostMapping("/coupon/generate/allocation/detail")
    public ResponseResult<List<ViewAllocateVo>> getAllocateDetail(@Valid @RequestBody GenerateAllocateQuery query) {
        List<ViewAllocateVo> list = cardBiz.getAllocateDetail(query);
        return ResponseUtil.success(list);
    }

    @ApiOperation(value = "产品生成分配--查看配给--导出")
    @PostMapping("/coupon/generate/allocation/detail/export")
    public void exportAllocateDetail(HttpServletResponse response, @Valid @RequestBody GenerateAllocateQuery query) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(EXPORT_CARD_FILENAME, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExportCardAllocateVo.class)
                .sheet("sheet").doWrite(cardBiz.getExportCardAllocateList(query));
    }

    @ApiOperation(value = "产品售卖分页查询")
    @PostMapping("/coupon/sale/page")
    public ResponseResult<PageInfo<CouponSalePageVo>> getGenerateAllocateList(@Valid @RequestBody CouponSaleQuery query) {
        PageInfo<CouponSalePageVo> pageInfo = cardBiz.getCouponSalePage(query);
        return ResponseUtil.success(pageInfo);
    }

    @ApiOperation(value = "产品售卖--查看配给分页查询")
    @PostMapping("/coupon/sale/card/page")
    public ResponseResult<PageInfo<CardSalePageVo>> getCardSalePageVo(@Valid @RequestBody CardSaleQuery query) {
        PageInfo<CardSalePageVo> pageInfo = cardBiz.getCardSalePageVo(query);
        return ResponseUtil.success(pageInfo);
    }
}
