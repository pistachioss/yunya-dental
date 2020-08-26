package com.yunya.modules.clinic.base.mapper;

import com.yunya.feign.cash_balance.query.CashBalanceQuery;
import com.yunya.feign.cash_balance.query.SpecializedSubjectProjecQuery;
import com.yunya.feign.cash_balance.vo.CashBalanceVo;
import com.yunya.feign.cash_balance.vo.SpecializedSubjectProjecVo;
import com.yunya.models.clinic_base.SpecializedSubjectProjec;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface SpecializedSubjectProjecMapper extends Mapper<SpecializedSubjectProjec> {

    List<SpecializedSubjectProjecVo> findSpecializedList(SpecializedSubjectProjecQuery query);

    void add(SpecializedSubjectProjec specializedSubjectProjec);

    void upd(SpecializedSubjectProjec specializedSubjectProjec);

    void del(Integer id);
}