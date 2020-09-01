package com.yunya.modules.clinic.base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.query.*;
import com.yunya.feign.cash_balance.vo.*;
import com.yunya.models.clinic_base.BusinessTarget;
import com.yunya.modules.clinic.base.mapper.BusinessTargetMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


@Service
public class BusinessTargetBiz {

    @Resource
    private BusinessTargetMapper businessTargetMapper;

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

    public void add(BusinessTarget businessTarget) {
        businessTargetMapper.add(businessTarget);
    }

    public void upd(BusinessTarget businessTarget) {
        businessTargetMapper.upd(businessTarget);
    }

    public BusinessTargetTotalVo findAllData(BusinessTargetTotalQuery businessTargetTotalQuery){
         BusinessTargetTotalVo businessTargetTotalVo = businessTargetMapper.findAllData(businessTargetTotalQuery);
         return businessTargetTotalVo;
    }

    public BusinessTargetByIdVo findDataById(BusinessTargetByDataQuery businessTargetByDataQuery){
        BusinessTargetByIdVo businessTargetByIdVo = businessTargetMapper.findDataById(businessTargetByDataQuery);
        return businessTargetByIdVo;
    }

    public List<BusinessTargetOrVo> businessAddOrUpd(BusinessAddOrUpdQuery businessAddOrUpdQuery){
        List<BusinessTargetOrVo> businessTargetOrVo = businessTargetMapper.businessAddOrUpd(businessAddOrUpdQuery);
        return businessTargetOrVo;
    }

    public List<BusinessTargetExportVo> exportListByDate(BusinessTargetExportQuery query){
        List<BusinessTargetExportVo> businessTargetExportVo = businessTargetMapper.exportListByDate(query);
        return businessTargetExportVo;
    }


}