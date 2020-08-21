package com.yunya.modules.treatment.other.mapper;


import com.yunya.feign.treatment_other.domain.form.ToothCycleForm;
import com.yunya.feign.treatment_other.domain.model.ToothCycleModel;
import com.yunya.feign.treatment_other.domain.query.ToothCycleQuery;
import com.yunya.models.treatment_other.ToothCycle;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface ToothCycleMapper extends Mapper<ToothCycle> {

    //查询牙周期表
    List<ToothCycle> findCycleList(ToothCycleQuery cycle);

    //添加牙周期记录
    void add(ToothCycleModel toothCycle);

    //修改牙周期记录
    void upd(ToothCycleForm toothCycle);

    //删除牙周期记录
    void del(Integer id);
}