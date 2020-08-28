package com.yunya.modules.treatment.other.mapper;


import com.yunya.feign.treatment_other.domain.form.ToothCycleForm;
import com.yunya.feign.treatment_other.domain.model.ToothCycleModel;
import com.yunya.feign.treatment_other.domain.query.ToothCycleQuery;
import com.yunya.feign.treatment_other.domain.vo.ToothCycleFindDataByIdVo;
import com.yunya.feign.treatment_other.domain.vo.ToothCycleVo;
import com.yunya.models.treatment_other.ToothCycle;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface ToothCycleMapper extends Mapper<ToothCycle> {

    /**
     * 查询牙周期表
     * @param cycle
     * @return
     */
    List<ToothCycleVo> findCycleList(ToothCycleQuery cycle);

    /**
     * 添加牙周期记录
     * @param toothCycle
     */
    void add(ToothCycle toothCycle);

    /**
     * 修改牙周期表
     * @param toothCycle
     */
    void upd(ToothCycle toothCycle);

    /**
     * 删除牙周期表
     * @param id
     */
    void del(Integer id);

    /**
     * 删除牙周期表
     * @param id
     */
    ToothCycleFindDataByIdVo findDataById(Integer id);
}