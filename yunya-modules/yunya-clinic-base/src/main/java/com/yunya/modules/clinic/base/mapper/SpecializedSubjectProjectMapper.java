package com.yunya.modules.clinic.base.mapper;

import com.yunya.feign.cash_balance.query.SpecializedSubjectProjectQuery;
import com.yunya.feign.cash_balance.vo.SpecialistTargetByIdVo;
import com.yunya.feign.cash_balance.vo.SpecializedSubjectProjectByIdVo;
import com.yunya.feign.cash_balance.vo.SpecializedSubjectProjectVo;
import com.yunya.models.clinic_base.SpecializedSubjectProject;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface SpecializedSubjectProjectMapper extends Mapper<SpecializedSubjectProject> {

    List<SpecializedSubjectProjectVo> findSpecializedList(SpecializedSubjectProjectQuery query);

    void add(SpecializedSubjectProject specializedSubjectProject);

    void upd(SpecializedSubjectProject specializedSubjectProject);

    void del(Integer id);

}