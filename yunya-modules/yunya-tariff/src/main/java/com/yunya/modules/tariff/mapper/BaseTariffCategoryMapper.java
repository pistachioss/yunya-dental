package com.yunya.modules.tariff.mapper;

import com.yunya.feign.treatment.domain.query.BaseTariffCategoryQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseTariffCategoryVO;
import com.yunya.models.tariff.BaseTariffCategory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseTariffCategoryMapper extends Mapper<BaseTariffCategory> {

  /**
   * 根据ID查询价目表分类
   *
   * @param id 价目表分类ID
   * @return
   */
  BaseTariffCategoryVO selectBaseTariffCategoryById(@Param("id") Integer id);

  /**
   * 根据条件查询价目表分类列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<BaseTariffCategoryVO> selectBaseTariffCategoryList(
      @Param("queryForm") BaseTariffCategoryQueryForm queryForm);
}
