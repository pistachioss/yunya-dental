package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.vo.web.MarketRecommendationDetailedVo;
import com.yunya.feign.patient_central.domain.vo.web.MarketRecommendationVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginActivityVo;
import com.yunya.feign.report.domain.query.MarketRecommendationDetailedQueryFrom;
import com.yunya.feign.report.domain.query.MarketRecommendationQueryFrom;
import com.yunya.feign.report.domain.vo.ExcelBaseMemberBalanceInfoVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BasePatientOrigin;
import com.yunya.report.ultimate.mapper.BaseBillPayMapper;
import com.yunya.report.ultimate.mapper.BasePatientOriginMapper;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.RETURN_VALUE_ISNULL;

/**
 * 简介: 市场推荐业务层
 *
 * @author: WY
 * @date: 2021/5/25 13:00
 * @description: 市场推荐业务层
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MarketRecommendationBiz extends BaseBiz<BasePatientOriginMapper, BasePatientOrigin> {

    @Resource
    private BaseBillPayMapper baseBillPayMapper;

    /**
     * 市场推荐列表
     * @param queryFrom 查询条件
     * @return 推荐列表
     */
    public List<MarketRecommendationVo> marketRecommendationList(MarketRecommendationQueryFrom queryFrom) {
        List<MarketRecommendationVo> marketRecommendationVos = baseBillPayMapper.selectActivityRecommendationNumber(queryFrom);
        if (StringHelper.isNotEmpty(marketRecommendationVos)){
            for (MarketRecommendationVo marketRecommendationVo : marketRecommendationVos) {
                marketRecommendationVo.setTotalAmountPaid(baseBillPayMapper.selectTotalAmountPaid(marketRecommendationVo,queryFrom, BusinessConstants.FREE_PAYMENT_ID));
                marketRecommendationVo.setTotalFreePayment(baseBillPayMapper.selectTotalAmountPaid(marketRecommendationVo,queryFrom, null));
                marketRecommendationVo.setTotalRefundAmount(baseBillPayMapper.selectTotalRefundAmount(marketRecommendationVo,queryFrom));
            }
        }
        return marketRecommendationVos;
    }

    /**
     * 市场推荐列表查询
     * @param query 条件
     * @return 市场推荐列表信息
     */
    public ResponseResult<PageInfo<MarketRecommendationVo>> findMarketRecommendationList(MarketRecommendationQueryFrom query) {
        // 查询市场患者数量以及实收/免单/退费 信息
        List<MarketRecommendationVo> marketRecommendationVoList = marketRecommendationList(query);
        // 分页
        PageInfo<MarketRecommendationVo> pageInfo = new PageInfo<>();
        if (!StringHelper.isEmpty(marketRecommendationVoList)){
            if (query.getWhetherPage()) {
                Integer pageNum = query.getPageNum();
                Integer pageSize = query.getPageSize();
                int total = marketRecommendationVoList.size();
                pageInfo.setPageNum(pageNum);
                pageInfo.setPageSize(pageSize);
                pageInfo.setTotal(total);
                List<MarketRecommendationVo> list =
                        marketRecommendationVoList.subList(
                                pageSize * (pageNum - 1), (Math.min((pageSize * pageNum), total)));
                pageInfo.setList(list);
                return ResponseUtil.success(pageInfo);
            }
            return ResponseUtil.fail(RETURN_VALUE_ISNULL,"未查询到市场推荐数据",pageInfo);
        }
        return ResponseUtil.fail(RETURN_VALUE_ISNULL,"未查询到市场推荐数据",pageInfo);
    }

    /**
     * 市场推荐患者数量以及实收、免单、退费信息-导出
     * @param response 请求
     * @param query 条件
     */
    public void exportMarketRecommendationList(HttpServletResponse response, MarketRecommendationQueryFrom query) throws IOException {
        List<MarketRecommendationVo> marketRecommendationVoList = marketRecommendationList(query);
        ExcelUtil<MarketRecommendationVo> excelUtil = new ExcelUtil<>(MarketRecommendationVo.class);
        List<MarketRecommendationVo> build = EntityUtils.build(marketRecommendationVoList, MarketRecommendationVo.class);
        excelUtil.exportExcel(response, build, "市场推荐明细", "市场推荐明细");
    }

    /**
     * 公司端/门诊端-报表统计-市场报表-市场推荐列表详情
     * @param query 请求
     * @return 市场推荐列表详情
     */
    public PageInfo<MarketRecommendationDetailedVo> findMarketRecommendationDetailed(MarketRecommendationDetailedQueryFrom query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<MarketRecommendationDetailedVo> marketRecommendationDetailedVos = baseBillPayMapper.selectMarketRecommendationDetailed(query);
        return new PageInfo<>(marketRecommendationDetailedVos);
    }
}