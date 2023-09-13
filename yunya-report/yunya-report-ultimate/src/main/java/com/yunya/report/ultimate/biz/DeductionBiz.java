package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.DeductionBuyQuery;
import com.yunya.feign.report.domain.query.DeductionPeriodQuery;
import com.yunya.feign.report.domain.query.DeductionRefundQuery;
import com.yunya.feign.report.domain.query.DeductionUseQuery;
import com.yunya.feign.report.domain.vo.DeductionBalanceInfoVO;
import com.yunya.feign.report.domain.vo.DeductionBuyVO;
import com.yunya.feign.report.domain.vo.DeductionRefundVO;
import com.yunya.feign.report.domain.vo.DeductionUsedVO;
import com.yunya.report.ultimate.mapper.CouponChangeRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Service
@Slf4j
public class DeductionBiz {
    @Resource
    private CouponChangeRecordMapper changeRecordMapper;


    /**
     * 产品售出激活统计
     *
     * @param query query
     * @return page
     */
    public PageInfo<DeductionBalanceInfoVO> deductionChange(DeductionPeriodQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<DeductionBalanceInfoVO> list = changeRecordMapper.deductionBalance(query);
        return new PageInfo<>(list);
    }

    public void buildResponse(HttpServletResponse response, String fileName)
            throws UnsupportedEncodingException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String encodeFileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + encodeFileName + ".xlsx");
    }

    public PageInfo<DeductionUsedVO> deductionUse(DeductionUseQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<DeductionUsedVO> list = changeRecordMapper.deductionUse(query);
        return new PageInfo<>(list);
    }

    public PageInfo<DeductionBuyVO> deductionBuy(DeductionBuyQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<DeductionBuyVO> lis = changeRecordMapper.deductionBuy(query);
        return new PageInfo<>(lis);
    }

    public PageInfo<DeductionRefundVO> deductionRefund(DeductionRefundQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<DeductionRefundVO> list = changeRecordMapper.deductionRefund(query);
        return new PageInfo<>(list);
    }
}
