package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.vo.ItemCategoryInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.BaseTariffInfo;
import com.yunya.report.ultimate.mapper.BaseTariffInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 简介: 价目表信息业务层
 *
 * @author: chow
 * @date: 2020/12/7 13:59
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseTariffInfoBiz extends BaseBiz<BaseTariffInfoMapper, BaseTariffInfo> {

  /** 价目表信息 */
  @Autowired private BaseTariffInfoMapper tariffInfoMapper;

  /**
   * 获取全部开单项目分类列表
   *
   * @return List<ItemCategoryInfoVO>
   */
  public List<ItemCategoryInfoVO> findItemCategoryList() {
    return tariffInfoMapper.selectItemCategoryList();
  }
}
