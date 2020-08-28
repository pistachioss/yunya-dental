package com.yunya.modules.treatment.other.biz;

import com.yunya.feign.treatment_other.domain.form.ToothCycleForm;
import com.yunya.feign.treatment_other.domain.model.ToothCycleModel;
import com.yunya.feign.treatment_other.domain.query.ToothCycleQuery;
import com.yunya.feign.treatment_other.domain.vo.ToothCycleFindDataByIdVo;
import com.yunya.feign.treatment_other.domain.vo.ToothCycleVo;
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

    public List<ToothCycleVo> findCycleList(ToothCycleQuery cycle) {
        List<ToothCycleVo> cycleList = toothCycleMapper.findCycleList(cycle);
        return cycleList;
    }


    public void add(ToothCycle toothCycle) {toothCycleMapper.add(toothCycle);
    }

    public void upd(ToothCycle toothCycle) {toothCycleMapper.upd(toothCycle);
    }

    public void del(Integer id) {toothCycleMapper.del(id);
    }

    public ToothCycleFindDataByIdVo findDataById(Integer id) {
        ToothCycleFindDataByIdVo toothCycleFindDataByIdVo =toothCycleMapper.findDataById(id);
        return toothCycleFindDataByIdVo;
    }
}
