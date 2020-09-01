package com.yunya.feign.tariff;

import com.yunya.feign.tariff.factory.RemoteTariffServiceFeignFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.tariff.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 简介: 云牙价目表服务接口调用
 *
 * @author: chow
 * @date: 2020/8/6 17:50
 * @description:
 * @since: 1.0.0
 */
@FeignClient(
    name = YunyaServiceNameConstants.YUNYA_TARIFF_SERVICE,
    fallbackFactory = RemoteTariffServiceFeignFallBackFactory.class)
public interface RemoteTariffServiceFeign {
  /**
   * 根据商品分类ID查询商品分类信息
   *
   * @param id 商品分类ID
   * @return
   */
  @RequestMapping(value = "/rpc/oral/category/{id}", method = RequestMethod.GET)
  BaseOralTariffCategory findBaseOralTariffCategoryById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询门诊商品分类列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @RequestMapping(value = "/rpc/oral/category/list", method = RequestMethod.POST)
  List<BaseOralTariffCategory> findBaseOralTariffCategoryList(
      @RequestBody BaseOralTariffCategory queryForm);

  /**
   * 根据商品项目ID查询商品项目信息
   *
   * @param id 商品项目ID
   * @return
   */
  @RequestMapping(value = "/rpc/oral/one/{id}", method = RequestMethod.GET)
  BaseOralTariff findBaseOralTariffById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询商品项目列表
   *
   * @param entity 查询条件
   * @return
   */
  @RequestMapping(value = "/rpc/oral/list", method = RequestMethod.POST)
  List<BaseOralTariff> findBaseOralTariffList(@RequestBody BaseOralTariff entity);

  /**
   * 根据价目表分类ID查询价目表分类信息
   *
   * @param id
   * @return
   */
  @RequestMapping(value = "/rpc/base/one/{id}", method = RequestMethod.GET)
  BaseTariffCategory findBaseTariffCategoryById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询价目表分类列表
   *
   * @param entity 查询条件
   * @return
   */
  @RequestMapping(value = "/rpc/base/list", method = RequestMethod.POST)
  List<BaseTariffCategory> findBaseTariffCategoryList(@RequestBody BaseTariffCategory entity);

  /**
   * 根据价目表项目ID查询基础价目表信息
   *
   * @param id 基础价目表ID
   * @return
   */
  @RequestMapping(value = "/rpc/tariff/{id}", method = RequestMethod.GET)
  BaseTariff findBaseTariffById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询价目表列表
   *
   * @param entity 查询条件
   * @return
   */
  @RequestMapping(value = "/rpc/tariff/list", method = RequestMethod.POST)
  List<BaseTariff> findBaseTariffList(@RequestBody BaseTariff entity);

  /**
   * 根据条件查询门诊价目表信息
   *
   * @param entity 门诊价目表
   * @return
   */
  @RequestMapping(value = "/rpc/clinic/tariff/one", method = RequestMethod.POST)
  ClinicTariff findClinicTariff(@RequestBody ClinicTariff entity);

  /**
   * 根据条件查询门诊商品信息
   *
   * @param entity 商品价目表
   * @return
   */
  @RequestMapping(value = "/rpc/clinic/oral/one", method = RequestMethod.POST)
  ClinicOralTariff findClinicOralTariff(@RequestBody ClinicOralTariff entity);
}
