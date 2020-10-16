package com.yunya.middletable.dao.report;

import com.yunya.models.middletable.BaseTariffInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BaseTariffInfoMapper extends Mapper<BaseTariffInfo> {
  /**
   * 根据联合主键查询
   *
   * @param dataId 数据ID
   * @param dateType 数据类型
   * @return
   */
  BaseTariffInfo selectByPrimaryKey1(
      @Param("dataId") Integer dataId, @Param("dateType") Integer dateType);

  /**
   * 根据联合主键删除
   *
   * @param itemId 数据ID
   * @param itemType 数据类型
   * @return
   */
  void deleteByPrimaryKey1(@Param("itemId") Integer itemId, @Param("itemType") Integer itemType);
}
