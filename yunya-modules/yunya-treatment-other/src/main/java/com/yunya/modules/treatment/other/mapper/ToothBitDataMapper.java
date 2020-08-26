package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.query.ToothBitDataQuery;
import com.yunya.feign.treatment_other.domain.vo.ToothBitDataVo;
import com.yunya.models.treatment_other.ToothBitData;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface  ToothBitDataMapper extends Mapper<ToothBitData> {

    /**
     * 查询牙根尖表
     * @param query
     * @return
     */
    List<ToothBitDataVo> findBitDataList(ToothBitDataQuery query);

    /**
     * 添加牙根尖记录
     * @param bitData
     */
    void add(ToothBitData bitData);

    /**
     * 修改牙根尖表
     * @param toothBitData
     */
    void upd(ToothBitData toothBitData);

    /**
     * 删除牙根尖表
     * @param id
     */
    void del(Integer id);
}