package com.yunya.modules.treatment.other.biz;

import com.yunya.feign.treatment_other.domain.query.ToothBitDataByNumQuery;
import com.yunya.feign.treatment_other.domain.query.ToothBitDataQuery;
import com.yunya.feign.treatment_other.domain.query.ToothCycleQuery;
import com.yunya.feign.treatment_other.domain.vo.ToothBitDataByNumVo;
import com.yunya.feign.treatment_other.domain.vo.ToothBitDataVo;
import com.yunya.feign.treatment_other.domain.vo.ToothCycleVo;
import com.yunya.models.treatment_other.ToothBitData;
import com.yunya.models.treatment_other.ToothCycle;
import com.yunya.modules.treatment.other.mapper.ToothBitDataMapper;
import com.yunya.modules.treatment.other.mapper.ToothCycleMapper;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ToothBitDataBiz {

    @Resource
    private ToothBitDataMapper toothBitDataMapper;

    public List<ToothBitDataVo>  findBitDataList(ToothBitDataQuery toothBitData){
        List<ToothBitDataVo> cycleList = toothBitDataMapper.findBitDataList(toothBitData);
        return cycleList;
    }


    public void  add(ToothBitData toothBitData){
        toothBitDataMapper.add(toothBitData);
    }

    public void  upd(ToothBitData toothBitData){
        toothBitDataMapper.upd(toothBitData);
    }

    public void  del(Integer id){
        toothBitDataMapper.del(id);
    }


    public List<ToothBitDataByNumVo> findPhotoAndNum(ToothBitDataByNumQuery query){
        List<ToothBitDataByNumVo> toothBitDataByNumVo = toothBitDataMapper.findPhotoAndNum(query);
        return toothBitDataByNumVo;
    }
}
