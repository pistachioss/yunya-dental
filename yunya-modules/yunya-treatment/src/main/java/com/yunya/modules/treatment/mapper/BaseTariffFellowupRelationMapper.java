package com.yunya.modules.treatment.mapper;

import com.yunya.models.tariff.BaseTariffFellowupRelation;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseTariffFellowupRelationMapper extends Mapper<BaseTariffFellowupRelation> {

    /**
     * 批量保存项目随访信息
     * @param lists
     * @return
     */
    public int batchSave(@Param("lists") List<BaseTariffFellowupRelation> lists);

    /**
     * 批量修改项目随访信息
     * @param updateList
     * @return
     */
    public int batchUpdate(@Param("updateList") List<BaseTariffFellowupRelation> updateList);

    /**
     * 根据项目ID查询随访信息
     * @param itemId
     * @return
     */
    List<BaseTariffFellowupRelation> queryByItemId(@Param("itemId") Integer itemId);
}
