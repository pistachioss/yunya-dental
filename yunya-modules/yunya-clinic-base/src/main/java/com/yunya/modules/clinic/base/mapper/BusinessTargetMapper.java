package com.yunya.modules.clinic.base.mapper;


import com.yunya.feign.cash_balance.model.BusinessTargetModel;
import com.yunya.feign.cash_balance.query.BusinessAddOrUpdQuery;
import com.yunya.feign.cash_balance.query.BusinessTargetQuery;
import com.yunya.feign.cash_balance.vo.BusinessTargetTotalVo;
import com.yunya.feign.cash_balance.vo.BusinessTargetVo;
import com.yunya.models.clinic_base.BusinessTarget;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface BusinessTargetMapper extends Mapper<BusinessTarget> {

    List<BusinessTargetVo> findSpecializedList(BusinessTargetQuery query);

    void add(BusinessTarget businessTarget);

    void upd(BusinessTarget businessTarget);

    Integer businessAddOrUpd(BusinessAddOrUpdQuery businessAddOrUpdQuery);

    BusinessTargetTotalVo findAllData(String ids);
}