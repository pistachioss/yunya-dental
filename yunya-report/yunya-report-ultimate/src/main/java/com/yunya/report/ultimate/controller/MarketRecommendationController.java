package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.PatientOriginActivityQuery;
import com.yunya.feign.patient_central.domain.vo.web.ActivityVo;
import com.yunya.feign.patient_central.domain.vo.web.MarketRecommendationDetailedVo;
import com.yunya.feign.patient_central.domain.vo.web.MarketRecommendationVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginActivityVo;
import com.yunya.feign.report.domain.query.MarketRecommendationDetailedQueryFrom;
import com.yunya.feign.report.domain.query.MarketRecommendationQueryFrom;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.report.ultimate.biz.MarketRecommendationBiz;
import com.yunya.report.ultimate.biz.PatientOriginActivityRelationsBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.RETURN_VALUE_ISNULL;

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

    @Resource
    PatientOriginActivityRelationsBiz patientOriginActivityRelationsBiz;

    /**
     * 获取活动列表
     * @return 活动列表
     */
    @ApiOperation("获取活动列表")
    @GetMapping(value = "/activity", name = "公司端/门诊端-报表统计-市场报表-获取活动列表")
    public ResponseResult<List<ActivityVo>> getActivityList(){
        List<ActivityVo> basePatientOrigins = patientOriginActivityRelationsBiz.getActivityList();
        return ResponseUtil.success(basePatientOrigins);
    }

    @ApiOperation("公司端/门诊端-报表统计-市场报表-市场推荐列表")
    @PostMapping("/list")
    public ResponseResult<PageInfo<MarketRecommendationVo>> marketRecommendationList(@RequestBody MarketRecommendationQueryFrom query){
       return marketRecommendationBiz.findMarketRecommendationList(query);
    }


    /**
     * 导出活动推荐推荐分页列表查询
     *
     * @param response 响应
     * @param query 查询条件
     * @return 导出活动推荐推荐分页列表查询
     */
    @ApiOperation("公司端/门诊端-报表统计-市场报表-市场推荐列表-导出")
    @PostMapping(value = "/list/export", name = "公司端/门诊端-报表统计-市场报表-市场推荐列表-导出")
    public ResponseResult<T> export(
            HttpServletResponse response, @RequestBody MarketRecommendationQueryFrom query)
            throws IOException {
        marketRecommendationBiz.exportMarketRecommendationList(response,query);
        return ResponseUtil.success(null);
    }

    @ApiOperation("公司端/门诊端-报表统计-市场报表-市场推荐列表详情")
    @PostMapping("/list/detailed")
    public ResponseResult<PageInfo<MarketRecommendationDetailedVo>> marketRecommendationDetailed(@RequestBody MarketRecommendationDetailedQueryFrom query){
        PageInfo<MarketRecommendationDetailedVo> marketRecommendationDetailedVos = marketRecommendationBiz.findMarketRecommendationDetailed(query);
        if (StringHelper.isNotNull(marketRecommendationDetailedVos)){
            return ResponseUtil.success(marketRecommendationDetailedVos);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据", null);
    }


}