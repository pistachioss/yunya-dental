package com.yunya.modules.clinic.base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.model.BusinessTargetModel;
import com.yunya.feign.cash_balance.query.BusinessAddOrUpdQuery;
import com.yunya.feign.cash_balance.query.BusinessTargetQuery;
import com.yunya.feign.cash_balance.vo.BusinessTargetTotalVo;
import com.yunya.feign.cash_balance.vo.BusinessTargetVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.clinic_base.BusinessTarget;
import com.yunya.modules.clinic.base.mapper.BusinessTargetMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BusinessTargetBiz {

    @Resource
    private BusinessTargetMapper BusinessTargetMapper;


    public PageInfo<BusinessTargetVo> findBusinessTargetByPage(BusinessTargetQuery query) {
        int completeCash = 500;
        int completeNum = 500;
        int completeFirstVisit = 500;
        int completePatientNum = 500;
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<BusinessTargetVo> resultList = BusinessTargetMapper.findSpecializedList(query);
        for (BusinessTargetVo complete : resultList) {
            complete.setCompleteCash(completeCash);
            complete.setCompleteFirstVisit(completeFirstVisit);
            complete.setCompleteNum(completeNum);
            complete.setCompletePatientNum(completePatientNum);
        }
        return new PageInfo<>(resultList);
    }

    @CurrentUser
    public void add(List<BusinessTargetModel> model) {
        Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
        for (BusinessTargetModel list : model) {
            BusinessAddOrUpdQuery businessAddOrUpdQuery = new BusinessAddOrUpdQuery();
            BusinessTarget businessTarget = new BusinessTarget();
            BeanUtils.copyProperties(list, businessAddOrUpdQuery);
            BeanUtils.copyProperties(model, businessTarget);
            Integer i = BusinessTargetMapper.businessAddOrUpd(businessAddOrUpdQuery);
            if (i != null) {
                businessTarget.setCrtId(crtId);
                BusinessTargetMapper.add(businessTarget);
            } else {
                businessTarget.setUpdId(crtId);
                BusinessTargetMapper.upd(businessTarget);
            }
        }
    }

    public BusinessTargetTotalVo findAllData(String ids){
         BusinessTargetTotalVo businessTargetTotalVo = BusinessTargetMapper.findAllData(ids);
         return businessTargetTotalVo;
    }
}