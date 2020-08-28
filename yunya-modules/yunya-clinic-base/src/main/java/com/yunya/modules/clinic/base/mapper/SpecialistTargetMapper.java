package com.yunya.modules.clinic.base.mapper;


import com.yunya.feign.cash_balance.query.BusinessAddOrUpdQuery;
import com.yunya.feign.cash_balance.query.BusinessTargetQuery;
import com.yunya.feign.cash_balance.query.SpecialistTargetQuery;
import com.yunya.feign.cash_balance.vo.BusinessTargetTotalVo;
import com.yunya.feign.cash_balance.vo.BusinessTargetVo;
import com.yunya.feign.cash_balance.vo.SpecialistTargetVo;
import com.yunya.models.clinic_base.BusinessTarget;
import com.yunya.models.clinic_base.SpecialistTarget;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface SpecialistTargetMapper extends Mapper<SpecialistTarget> {

     List<SpecialistTargetVo> findspecialistTargetByPage(SpecialistTargetQuery query);

    void add(SpecialistTarget specialistTarget);

    void upd(SpecialistTarget specialistTarget);

    Integer specialistAddOrUpd(SpecialistTargetQuery specialistAddOrUpdQuery);

    BusinessTargetTotalVo findAllData(String ids);
}