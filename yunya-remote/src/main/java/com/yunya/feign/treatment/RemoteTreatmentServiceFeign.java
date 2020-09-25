package com.yunya.feign.treatment;

import com.yunya.feign.treatment.factory.RemoteTreatmentServiceFeignFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.tariff.*;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

/**
 * 简介: 云牙价目表、就诊服务接口调用
 *
 * @author: chow
 * @date: 2020/8/6 17:50
 * @description:
 * @since: 1.0.0
 */
@FeignClient(
    name = YunyaServiceNameConstants.YUNYA_TREATMENT_SERVICE,
    fallbackFactory = RemoteTreatmentServiceFeignFallBackFactory.class)
public interface RemoteTreatmentServiceFeign {
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
   * @param id 价目表分类ID
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

  /**
   * 根据挂号记录ID查询挂号记录
   *
   * @param id 挂号记录ID
   * @return
   */
  @RequestMapping(value = "/rpc/registered/one/{id}", method = RequestMethod.GET)
  Registered findRegisteredById(@PathVariable(value = "id") Integer id);

  /**
   * 根据挂号对象查询挂号信息
   *
   * @param entity 挂号对象
   * @return Registered
   */
  @RequestMapping(value = "/rpc/registered/example", method = RequestMethod.POST)
  Registered findRegisteredByExample(@RequestBody Registered entity);

  /**
   * 根据条件查询挂号记录列表
   *
   * @param entity 挂号记录
   * @return
   */
  @RequestMapping(value = "/rpc/registered/list", method = RequestMethod.POST)
  List<Registered> findRegisteredList(@RequestBody Registered entity);

  /**
   * 根据就诊记录ID查询就诊记录
   *
   * @param id 就诊记录ID
   * @return
   */
  @RequestMapping(value = "/rpc/treatment/one/{id}", method = RequestMethod.GET)
  TreatmentRecord findTreatmentRecordById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询就诊记录信息
   *
   * @param entity 就诊记录
   * @return
   */
  @RequestMapping(value = "/rpc/treatment/example", method = RequestMethod.POST)
  TreatmentRecord findTreatmentRecordByExample(@RequestBody TreatmentRecord entity);

  /**
   * 根据就诊记录ID列表查询就诊记录列表
   *
   * @param ids 就诊记录ID列表
   * @return
   */
  @RequestMapping(value = "/rpc/treatment/section", method = RequestMethod.POST)
  List<TreatmentRecord> findTreatmentRecordByIds(@RequestBody @NotEmpty Set<Integer> ids);

  /**
   * 根据条件查询就诊记录列表
   *
   * @param entity 就诊记录
   * @return
   */
  @RequestMapping(value = "/rpc/treatment/list", method = RequestMethod.POST)
  List<TreatmentRecord> findTreatmentRecordList(@RequestBody TreatmentRecord entity);

  /**
   * 修改就诊记录病历书写状态
   *
   * @param id 就诊记录ID
   */
  @RequestMapping(value = "/rpc/treatment/modify/{id}", method = RequestMethod.GET)
  void updateTreatmentRecord(@PathVariable(value = "id") Integer id);

  /**
   * 根据开单记录ID查询开单记录
   *
   * @param id 开单记录ID
   * @return
   */
  @RequestMapping(value = "/rpc/order/one/{id}", method = RequestMethod.GET)
  OrderRecord findOrderRecordById(@PathVariable(value = "id") Integer id);

  /**
   * 根据开单记录ID查询开单明细列表
   *
   * @param orderRecordId 开单记录ID
   * @return
   */
  @RequestMapping(value = "/rpc/order/detail/list/{orderRecordId}", method = RequestMethod.GET)
  List<OrderDetail> findOrderDetailByOrderRecordId(
          @PathVariable(value = "orderRecordId") Integer orderRecordId);

}
