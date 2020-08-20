package com.yunya.modules.tariff.rpc;

import com.yunya.feign.tariff.domain.vo.ClinicTariffVO;
import com.yunya.models.tariff.*;
import com.yunya.modules.tariff.biz.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简介: 价目表服务接口暴露
 *
 * @author: chow
 * @date: 2020/8/10 15:43
 * @description:
 * @since: 1.0.0
 */
@Api("价目表服务接口暴露")
@RestController
@RequestMapping("rpc")
public class TariffServiceRest {

  /** 商品分类 */
  @Autowired private BaseOralTariffCategoryBiz baseOralTariffCategoryBiz;
  /** 商品项目 */
  @Autowired private BaseOralTariffBiz baseOralTariffBiz;
  /** 价目表分类 */
  @Autowired private BaseTariffCategoryBiz baseTariffCategoryBiz;
  /** 基础价目表 */
  @Autowired private BaseTariffBiz baseTariffBiz;
  /** 门诊价目表 */
  @Autowired private ClinicTariffBiz clinicTariffBiz;
  /** 门诊商品表 */
  @Autowired private ClinicOralTariffBiz clinicOralTariffBiz;

  /**
   * 根据商品分类ID查询商品分类信息
   *
   * @param id 商品分类ID
   * @return
   */
  @RequestMapping(value = "/oral/category/{id}", method = RequestMethod.GET)
  public BaseOralTariffCategory findBaseOralTariffCategoryById(
      @PathVariable(value = "id") Integer id) {
    return baseOralTariffCategoryBiz.selectById(id);
  }

  /**
   * 根据条件查询门诊商品分类列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @RequestMapping(value = "/oral/category/list", method = RequestMethod.POST)
  public List<BaseOralTariffCategory> findBaseOralTariffCategoryList(
      @RequestBody BaseOralTariffCategory queryForm) {
    return baseOralTariffCategoryBiz.selectList(queryForm);
  }

  /**
   * 根据商品项目ID查询商品项目信息
   *
   * @param id 商品项目ID
   * @return
   */
  @RequestMapping(value = "/oral/one/{id}", method = RequestMethod.GET)
  public BaseOralTariff findBaseOralTariffById(@PathVariable(value = "id") Integer id) {
    return baseOralTariffBiz.selectById(id);
  }

  /**
   * 根据条件查询商品项目列表
   *
   * @param entity 查询条件
   * @return
   */
  @RequestMapping(value = "/oral/list", method = RequestMethod.POST)
  public List<BaseOralTariff> findBaseOralTariffList(@RequestBody BaseOralTariff entity) {
    return baseOralTariffBiz.selectList(entity);
  }

  /**
   * 根据价目表分类ID查询价目表分类信息
   *
   * @param id 价目表分类ID
   * @return
   */
  @RequestMapping(value = "/base/one/{id}", method = RequestMethod.GET)
  public BaseTariffCategory findBaseTariffCategoryById(@PathVariable(value = "id") Integer id) {
    return baseTariffCategoryBiz.selectById(id);
  }

  /**
   * 根据条件查询价目表分类列表
   *
   * @param entity 查询条件
   * @return
   */
  @RequestMapping(value = "/base/list", method = RequestMethod.POST)
  public List<BaseTariffCategory> findBaseTariffCategoryList(
      @RequestBody BaseTariffCategory entity) {
    return baseTariffCategoryBiz.selectList(entity);
  }

  /**
   * 根据价目表项目ID查询基础价目表信息
   *
   * @param id 基础价目表ID
   * @return
   */
  @RequestMapping(value = "/tariff/{id}", method = RequestMethod.GET)
  public BaseTariff findBaseTariffById(@PathVariable(value = "id") Integer id) {
    return baseTariffBiz.selectById(id);
  }

  /**
   * 根据条件查询价目表列表
   *
   * @param entity 查询条件
   * @return
   */
  @RequestMapping(value = "/tariff/list", method = RequestMethod.POST)
  public List<BaseTariff> findBaseTariffList(@RequestBody BaseTariff entity) {
    return baseTariffBiz.selectList(entity);
  }

  /**
   * 根据条件查询门诊价目表信息
   *
   * @param entity 门诊价目表
   * @return
   */
  @RequestMapping(value = "/clinic/tariff/one", method = RequestMethod.POST)
  public ClinicTariff findClinicTariff(@RequestBody ClinicTariff entity) {
    return clinicTariffBiz.selectOne(entity);
  }

  /**
   * 根据条件查询门诊商品信息
   *
   * @param entity 商品价目表
   * @return
   */
  @RequestMapping(value = "/clinic/oral/one", method = RequestMethod.POST)
  public ClinicOralTariff findClinicOralTariff(@RequestBody ClinicOralTariff entity) {
    return clinicOralTariffBiz.selectOne(entity);
  }
}
