package com.yunya.modules.clinic.base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.model.CashBalanceModel;
import com.yunya.feign.cash_balance.query.CashBalanceQuery;
import com.yunya.feign.cash_balance.vo.CashBalanceByIdVo;
import com.yunya.feign.cash_balance.vo.CashBalanceVo;
import com.yunya.feign.emr.domain.vo.MedicalApplyPageVo;
import com.yunya.models.clinic_base.CashBalance;
import com.yunya.modules.clinic.base.mapper.CashBalanceMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CashBalanceBiz {

    @Resource
    private CashBalanceMapper cashBalanceMapper;


    public PageInfo<CashBalanceVo> findCashListByPage(CashBalanceQuery query){
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<CashBalanceVo> resultList = cashBalanceMapper.findCashListByPage(query);
        return new PageInfo<>(resultList);
    }

    public void  add(CashBalance cashBalance){ cashBalanceMapper.add(cashBalance); }

    public void  upd(CashBalance cashBalance){
        cashBalanceMapper.upd(cashBalance);
    }

    public void  del(Integer id){
        cashBalanceMapper.del(id);
    }

    public Integer  findLastData(int orgId){
        Integer lastData = cashBalanceMapper.findLastData(orgId);
        if (lastData == null){
            lastData = 0;
        }
        return lastData;
    }
    public CashBalanceByIdVo  findDataById(int id) {
        CashBalanceByIdVo cashBalanceByIdVo = cashBalanceMapper.findDataById(id);
        return cashBalanceByIdVo;
    }
}
