package com.yunya.modules.clinic.base.mapper;


import com.yunya.feign.cash_balance.query.BusinessTargetTotalQuery;
import com.yunya.feign.cash_balance.query.SpecialistAddOrUpdQuery;
import com.yunya.feign.cash_balance.query.SpecialistTargetByDataQuery;
import com.yunya.feign.cash_balance.query.SpecialistTargetQuery;
import com.yunya.feign.cash_balance.vo.SpecialistTargetByIdVo;
import com.yunya.feign.cash_balance.vo.SpecialistTargetOrVo;
import com.yunya.feign.cash_balance.vo.SpecialistTargetTotalVo;
import com.yunya.feign.cash_balance.vo.SpecialistTargetVo;
import com.yunya.models.clinic_base.SpecialistTarget;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface SpecialistTargetMapper extends Mapper<SpecialistTarget> {

    List<SpecialistTargetOrVo>  specialistAddOrUpd(SpecialistAddOrUpdQuery specialistAddOrUpdQuery);

    void add(SpecialistTarget specialistTarget);

    void upd(SpecialistTarget specialistTarget);

    SpecialistTargetTotalVo findAllData(BusinessTargetTotalQuery businessTargetTotalQuery);

    SpecialistTargetByIdVo findDataById(SpecialistTargetByDataQuery specialistTargetByDataQuery);

    List<SpecialistTargetVo> findSpecialistTargetByPage(SpecialistTargetQuery query);
}