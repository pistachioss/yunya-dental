package com.yunya.modules.discount.controller;

import com.github.pagehelper.*;
import com.yunya.feign.discount.domain.query.*;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.framework.common.model.*;
import com.yunya.framework.common.utils.*;
import com.yunya.modules.discount.biz.*;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.*;
import javax.validation.*;

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
    @PostMapping("/coupon/generate/allocation/page")
    public ResponseResult getGenerateAllocateList(@Valid @RequestBody CouponAllocateQuery query) {
        PageInfo<GenerateAllocatePageVo> pageInfo = cardBiz.getCouponAllocatePage(query);
        return ResponseUtil.success(pageInfo);
    }
}
