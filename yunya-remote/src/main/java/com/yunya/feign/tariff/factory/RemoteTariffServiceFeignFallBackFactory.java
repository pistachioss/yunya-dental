package com.yunya.feign.tariff.factory;

import com.yunya.feign.tariff.RemoteTariffServiceFeign;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseOralTariffCategory;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.tariff.BaseTariffCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 简介: 价目表服务调用降级处理
 *
 * @author: chow
 * @date: 2020/8/6 17:53
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Component
public class RemoteTariffServiceFeignFallBackFactory implements RemoteTariffServiceFeign {
  @Override
  public BaseOralTariffCategory findBaseOralTariffCategoryById(Integer id) {
    return null;
  }

  @Override
  public List<BaseOralTariffCategory> findBaseOralTariffCategoryList(
      BaseOralTariffCategory queryForm) {
    return null;
  }

  @Override
  public BaseOralTariff findBaseOralTariffById(Integer id) {
    return null;
  }

  @Override
  public List<BaseOralTariff> findBaseOralTariffList(BaseOralTariff entity) {
    return null;
  }

  @Override
  public BaseTariffCategory findBaseTariffCategoryById(Integer id) {
    return null;
  }

  @Override
  public List<BaseTariffCategory> findBaseTariffCategoryList(BaseTariffCategory entity) {
    return null;
  }

  @Override
  public BaseTariff findBaseTariffById(Integer id) {
    return null;
  }

  @Override
  public List<BaseTariff> findBaseTariffList(BaseTariff entity) {
    return null;
  }
  /***/
}
