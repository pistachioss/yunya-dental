package com.yunya.report.ultimate.controller;

import com.yunya.feign.report.domain.query.MarketRecommendationQueryFrom;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.report.ultimate.biz.MarketRecommendationBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 简介: 市场推荐报表
 *
 * @author: WY
 * @date: 2021/5/25 12:57
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端/门诊端-报表统计-市场报表-市场推荐报表")
@RestController
@RequestMapping("marketRecommendation")
public class MarketRecommendationController {

    @Resource
    MarketRecommendationBiz marketRecommendationBiz;

    @ApiOperation("市场推荐列表")
    @PostMapping("/list")
    public ResponseResult<T> marketRecommendationList(@RequestBody MarketRecommendationQueryFrom queryFrom){
        return null;
    }
}