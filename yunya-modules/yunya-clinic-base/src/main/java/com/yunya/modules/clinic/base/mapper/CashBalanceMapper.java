package com.yunya.modules.clinic.base.mapper;


import com.yunya.feign.cash_balance.query.CashBalanceQuery;
import com.yunya.feign.cash_balance.vo.CashBalanceByIdVo;
import com.yunya.feign.cash_balance.vo.CashBalanceVo;
import com.yunya.models.clinic_base.CashBalance;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface CashBalanceMapper extends Mapper<CashBalance> {
    List<CashBalanceVo> findCashListByPage(CashBalanceQuery query);

    void add(CashBalance cashBalance);

    void upd(CashBalance cashBalance);

    void del(Integer id);

    BigDecimal findLastData(Integer orgId);

    CashBalanceByIdVo findDataById(Integer id);
}