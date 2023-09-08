package com.yunya.report.ultimate.biz;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.DeductionBuyQuery;
import com.yunya.feign.report.domain.query.DeductionPeriodQuery;
import com.yunya.feign.report.domain.query.DeductionUseQuery;
import com.yunya.feign.report.domain.vo.DeductionBalanceInfoVO;
import com.yunya.feign.report.domain.vo.DeductionBuyVO;
import com.yunya.feign.report.domain.vo.DeductionUsedVO;
import com.yunya.report.ultimate.mapper.CouponChangeRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
        Page<DeductionBalanceInfoVO> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        changeRecordMapper.deductionBalance(query);
        return new PageInfo<>(page);
    }

    public void buildResponse(HttpServletResponse response, String fileName)
            throws UnsupportedEncodingException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String encodeFileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + encodeFileName + ".xlsx");
    }

    public PageInfo<DeductionUsedVO> deductionUse(DeductionUseQuery query) {
        Page<DeductionUsedVO> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        changeRecordMapper.deductionUse(query);
        return new PageInfo<>(page);
    }

    public PageInfo<DeductionBuyVO> deductionBuy(DeductionBuyQuery query) {
        Page<DeductionBuyVO> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        changeRecordMapper.deductionBuy(query);
        return new PageInfo<>(page);
    }
}
