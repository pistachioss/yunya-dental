package com.yunya.modules.treatment.other.biz;

import com.yunya.feign.treatment_other.domain.form.ToothCycleForm;
import com.yunya.feign.treatment_other.domain.model.ToothCycleModel;
import com.yunya.feign.treatment_other.domain.query.ToothCycleQuery;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.treatment_other.ToothCycle;
import com.yunya.modules.treatment.other.mapper.ToothCycleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ToothCycleBiz {

    @Resource
    private ToothCycleMapper toothCycleMapper;

    public List<ToothCycle>  findCycleList(ToothCycleQuery cycle){
        List<ToothCycle> cycleList = toothCycleMapper.findCycleList(cycle);
        return cycleList;
    }


    public void  add(ToothCycleModel cycle){
        toothCycleMapper.add(cycle);
    }

    public void  upd(ToothCycleForm cycle){
        toothCycleMapper.upd(cycle);
    }

    public void  del(Integer id){
        toothCycleMapper.del(id);
    }
}
