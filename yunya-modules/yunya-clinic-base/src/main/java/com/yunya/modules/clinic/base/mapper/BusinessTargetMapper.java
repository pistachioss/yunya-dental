package com.yunya.modules.clinic.base.mapper;


import com.yunya.feign.cash_balance.query.BusinessAddOrUpdQuery;
import com.yunya.feign.cash_balance.query.BusinessTargerByDataQuery;
import com.yunya.feign.cash_balance.query.BusinessTargetQuery;
import com.yunya.feign.cash_balance.query.BusinessTargetTotalQuery;
import com.yunya.feign.cash_balance.vo.BusinessTargetByIdVo;
import com.yunya.feign.cash_balance.vo.BusinessTargetOrVo;
import com.yunya.feign.cash_balance.vo.BusinessTargetTotalVo;
import com.yunya.feign.cash_balance.vo.BusinessTargetVo;
import com.yunya.models.clinic_base.BusinessTarget;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface BusinessTargetMapper extends Mapper<BusinessTarget> {

    List<BusinessTargetVo> findBusinessTargetByPage(BusinessTargetQuery query);

    void add(BusinessTarget businessTarget);

    void upd(BusinessTarget businessTarget);

    BusinessTargetTotalVo findAllData(BusinessTargetTotalQuery businessTargetTotalQuery);

    BusinessTargetByIdVo findDataById(BusinessTargerByDataQuery businessTargerByDataQuery);

    List<BusinessTargetOrVo> businessAddOrUpd(BusinessAddOrUpdQuery businessAddOrUpdQuery);
}