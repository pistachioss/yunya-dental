package com.yunya.modules.clinic_base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.domain.query.*;
import com.yunya.feign.clinic_base.domain.vo.*;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.clinic_base.BusinessTarget;
import com.yunya.modules.clinic_base.mapper.BusinessTargetMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 简介: 公司业务目标
 *
 * @author: Zkq
 * @date: 2020/8/20 14:53
 * @description:
 * @since: 1.0.0
 */
@Service
public class BusinessTargetBiz {

    @Resource
    private BusinessTargetMapper businessTargetMapper;

    /**
     * 查询现金模块列表
     *
     * @param query 查询公司业务目标模板
     * @return resultList
     */
    public PageInfo<BusinessTargetVo> findBusinessTargetByPage(BusinessTargetQuery query) {
        BigDecimal completeCash = new BigDecimal(500);
        BigDecimal completeNum = new BigDecimal(500);
        BigDecimal completeFirstVisit = new BigDecimal(500);
        BigDecimal completePatientNum = new BigDecimal(500);
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<BusinessTargetVo> resultList = businessTargetMapper.findBusinessTargetByPage(query);
        for (BusinessTargetVo complete : resultList) {
            complete.setCompleteCash(completeCash);
            complete.setCompleteFirstVisit(completeFirstVisit);
            complete.setCompleteNum(completeNum);
            complete.setCompletePatientNum(completePatientNum);
        }
        return new PageInfo<>(resultList);
    }

    /**
     * 添加现金模块
     *
     * @param businessTarget 添加公司业务目标
     */
    public void add(BusinessTarget businessTarget) {
        businessTargetMapper.add(businessTarget);
    }

    /**
     * 修改现金模块
     *
     * @param businessTarget 修改公司业务目标
     */
    public void upd(BusinessTarget businessTarget) {
        businessTargetMapper.upd(businessTarget);
    }

    public BusinessTargetTotalVo findAllData(BusinessTargetTotalQuery businessTargetTotalQuery) {
        return businessTargetMapper.findAllData(businessTargetTotalQuery);
    }

    public BusinessTargetByIdVo findDataById(BusinessTargetByDataQuery businessTargetByDataQuery) {
        return businessTargetMapper.findDataById(businessTargetByDataQuery);
    }

    public List<BusinessTargetOrVo> businessAddOrUpd(BusinessAddOrUpdQuery businessAddOrUpdQuery) {
        return businessTargetMapper.businessAddOrUpd(businessAddOrUpdQuery);
    }

    public List<BusinessTargetExportVo> exportListByDatee(BusinessTargetExportQuery query) {
        return businessTargetMapper.exportListByDate(query);
    }

    /**
     * 根据条件导出业务目标导出
     *
     * @param response 响应
     * @param query    查询条件
     */
    public void exportListByDate(HttpServletResponse response, BusinessTargetExportQuery query)
            throws IOException {
        List<BusinessTargetExportVo> businessTargetExportVo = businessTargetMapper.exportListByDate(query);
        BigDecimal bigDecimal = new BigDecimal(500);
        BigDecimal big = new BigDecimal(100);
        DecimalFormat df = new DecimalFormat("0.0000");
        for (BusinessTargetExportVo list : businessTargetExportVo) {
            list.setCompleteCash(bigDecimal);
            list.setPercentCash(bigDecimal.divide(list.getTargetCash(), 2, BigDecimal.ROUND_HALF_UP).multiply(big) + "%");
            list.setCompleteFirstVisit(500);
            list.setPercentVisit(df.format((float) 500 / list.getTargetFirstVisit() * 100) + "%");
            list.setCompleteNum(bigDecimal);
            list.setPercentNum(bigDecimal.divide(list.getTargetNum(), 2, BigDecimal.ROUND_HALF_UP).multiply(big) + "%");
            list.setCompletePatientNum(500);
            list.setPercentPatientNum(df.format((float) 500 / list.getTargetPatientNum() * 100) + "%");
        }
        ExcelUtil<BusinessTargetExportVo> excelUtil = new ExcelUtil<>(BusinessTargetExportVo.class);
        excelUtil.exportExcel(response, businessTargetExportVo, "业务目标列表", "业务目标导出");
    }


}