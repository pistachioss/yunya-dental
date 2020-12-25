package com.yunya.modules.treatment.controller.rpc;

import com.yunya.feign.treatment.domain.model.DebtAmountModel;
import com.yunya.feign.treatment.domain.vo.RegisteredVO;
import com.yunya.feign.treatment.domain.query.CompletedWorkGoalQuery;
import com.yunya.feign.treatment.domain.vo.CompletedBusinessWorkGoalVO;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordExtendVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.tariff.*;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.biz.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 简介: 价目表、诊疗服务接口暴露
 *
 * @author: chow
 * @date: 2020/8/15 15:45
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "诊疗服务接口暴露")
@RestController
@RequestMapping("rpc")
public class TreatmentServiceRest {

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
  /** 门诊价目表会员价 */
  @Autowired private ClinicTariffMemberPriceBiz clinicTariffMemberPriceBiz;
  /** 门诊商品会员价 */
  @Autowired private ClinicOralTariffMemberPriceBiz clinicOralTariffMemberPriceBiz;
  /** 挂号记录 */
  @Autowired private RegisteredBiz registeredBiz;
  /** 就诊记录 */
  @Autowired private TreatmentRecordBiz treatmentRecordBiz;
  /** 开单记录 */
  @Autowired private OrderRecordBiz orderRecordBiz;
  /** 开单明细 */
  @Autowired private OrderDetailBiz orderDetailBiz;
  /** 账单记录 */
  @Autowired private BillRecordBiz billRecordBiz;

  /**
   * 根据商品分类ID查询商品分类信息
   *
   * @param id 商品分类ID
   * @return BaseOralTariffCategory
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
   * @return BaseOralTariff
   */
  @RequestMapping(value = "/oral/one/{id}", method = RequestMethod.GET)
  public BaseOralTariff findBaseOralTariffById(@PathVariable(value = "id") Integer id) {
    return baseOralTariffBiz.selectById(id);
  }

  /**
   * 根据条件查询商品项目列表
   *
   * @param entity 查询条件
   * @return List<BaseOralTariff>
   */
  @RequestMapping(value = "/oral/list", method = RequestMethod.POST)
  public List<BaseOralTariff> findBaseOralTariffList(@RequestBody BaseOralTariff entity) {
    return baseOralTariffBiz.selectList(entity);
  }

  /**
   * 根据价目表分类ID查询价目表分类信息
   *
   * @param id 价目表分类ID
   * @return BaseTariffCategory
   */
  @RequestMapping(value = "/base/one/{id}", method = RequestMethod.GET)
  public BaseTariffCategory findBaseTariffCategoryById(@PathVariable(value = "id") Integer id) {
    return baseTariffCategoryBiz.selectById(id);
  }

  /**
   * 根据条件查询价目表分类列表
   *
   * @param entity 查询条件
   * @return List<BaseTariffCategory>
   */
  @RequestMapping(value = "/base/list", method = RequestMethod.POST)
  public List<BaseTariffCategory> findBaseTariffCategoryList(
      @RequestBody BaseTariffCategory entity) {
    return baseTariffCategoryBiz.selectList(entity);
  }

  /**
   * 根据多个价目表ID查询价目表名称
   *
   * @param ids 字符串ID
   * @return String
   */
  @RequestMapping(value = "/tariff/name", method = RequestMethod.POST)
  public String findBaseTariffNamesByIds(@RequestBody @NotEmpty String[] ids) {
    return baseTariffBiz.findBaseTariffNamesByIds(ids);
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
   * @return List<BaseTariff>
   */
  @RequestMapping(value = "/tariff/list", method = RequestMethod.POST)
  public List<BaseTariff> findBaseTariffList(@RequestBody BaseTariff entity) {
    return baseTariffBiz.selectList(entity);
  }

  /**
   * 根据条件查询门诊价目表信息
   *
   * @param entity 门诊价目表
   * @return ClinicTariff
   */
  @RequestMapping(value = "/clinic/tariff/one", method = RequestMethod.POST)
  public ClinicTariff findClinicTariff(@RequestBody ClinicTariff entity) {
    return clinicTariffBiz.selectOne(entity);
  }

  /**
   * 根据对象查询门诊价目表会员价
   *
   * @param orgId 组织ID
   * @param memberType 会员类型
   * @param itemId 项目ID
   * @return ClinicTariffMemberPrice
   */
  @RequestMapping(value = "/clinic/tariff/param", method = RequestMethod.POST)
  public ClinicTariffMemberPrice findClinicTariffMemberPrice(
      @RequestParam(value = "orgId") Integer orgId,
      @RequestParam(value = "memberType") Integer memberType,
      @RequestParam(value = "itemId") Integer itemId) {
    ClinicTariffMemberPrice entity = new ClinicTariffMemberPrice();
    entity.setClinicId(orgId);
    entity.setMemberTypeId(memberType);
    entity.setTariffId(itemId);
    return clinicTariffMemberPriceBiz.selectOne(entity);
  }

  /**
   * 根据条件查询门诊商品信息
   *
   * @param entity 商品价目表
   * @return ClinicOralTariff
   */
  @RequestMapping(value = "/clinic/oral/one", method = RequestMethod.POST)
  public ClinicOralTariff findClinicOralTariff(@RequestBody ClinicOralTariff entity) {
    return clinicOralTariffBiz.selectOne(entity);
  }

  /**
   * 根据对象查询门诊商品会员价
   *
   * @param orgId 组织ID
   * @param memberType 会员类型
   * @param itemId 项目ID
   * @return ClinicOralTariffMemberPrice
   */
  @RequestMapping(value = "/clinic/oral/param", method = RequestMethod.POST)
  public ClinicOralTariffMemberPrice findClinicOralTariffMemberPrice(
      @RequestParam(value = "orgId") Integer orgId,
      @RequestParam(value = "memberType") Integer memberType,
      @RequestParam(value = "itemId") Integer itemId) {
    ClinicOralTariffMemberPrice entity = new ClinicOralTariffMemberPrice();
    entity.setClinicId(orgId);
    entity.setMemberTypeId(memberType);
    entity.setOralTariffId(itemId);
    return clinicOralTariffMemberPriceBiz.selectOne(entity);
  }

  /**
   * 根据挂号记录ID查询挂号记录
   *
   * @param id 挂号记录ID
   * @return Registered
   */
  @RequestMapping(value = "/registered/one/{id}", method = RequestMethod.GET)
  public Registered findRegisteredById(@PathVariable(value = "id") Integer id) {
    return registeredBiz.selectById(id);
  }

  /**
   * 根据挂号对象查询挂号信息
   *
   * @param entity 挂号对象
   * @return Registered
   */
  @RequestMapping(value = "/registered/example", method = RequestMethod.POST)
  public Registered findRegisteredByExample(@RequestBody Registered entity) {
    return registeredBiz.selectOne(entity);
  }

  /**
   * 根据条件查询挂号记录列表
   *
   * @param entity 挂号记录
   * @return List<Registered>
   */
  @RequestMapping(value = "/registered/list", method = RequestMethod.POST)
  public List<Registered> findRegisteredList(@RequestBody Registered entity) {
    return registeredBiz.selectList(entity);
  }

  /**
   * 根据就诊记录ID查询就诊记录
   *
   * @param id 就诊记录ID
   * @return TreatmentRecord
   */
  @RequestMapping(value = "/treatment/one/{id}", method = RequestMethod.GET)
  public TreatmentRecord findTreatmentRecordById(@PathVariable(value = "id") Integer id) {
    return treatmentRecordBiz.selectById(id);
  }

  /**
   * 根据就诊记录ID列表查询就诊记录列表
   *
   * @param ids 就诊记录ID列表
   * @return List<TreatmentRecordExtendVO>
   */
  @RequestMapping(value = "/treatment/section", method = RequestMethod.POST)
  public List<TreatmentRecordExtendVO> findTreatmentRecordByIds(
      @RequestBody @NotEmpty Set<Integer> ids) {
    if (StringHelper.isNotEmpty(ids)) {
      return treatmentRecordBiz.selectByIds(ids);
    }
    return new ArrayList<>();
  }

  /**
   * 根据条件查询就诊记录信息
   *
   * @param entity 就诊记录
   * @return TreatmentRecord
   */
  @RequestMapping(value = "/treatment/example", method = RequestMethod.POST)
  public TreatmentRecord findTreatmentRecordByExample(@RequestBody TreatmentRecord entity) {
    return treatmentRecordBiz.selectOne(entity);
  }

  /**
   * 根据条件查询就诊记录列表
   *
   * @param entity 就诊记录
   * @return List<TreatmentRecord>
   */
  @RequestMapping(value = "/treatment/list", method = RequestMethod.POST)
  public List<TreatmentRecord> findTreatmentRecordList(@RequestBody TreatmentRecord entity) {
    return treatmentRecordBiz.selectList(entity);
  }

  /**
   * 修改就诊记录病历书写状态
   *
   * @param id 就诊记录ID
   */
  @RequestMapping(value = "/treatment/modify/{id}", method = RequestMethod.GET)
  public void updateTreatmentRecord(@PathVariable(value = "id") Integer id) {
    treatmentRecordBiz.modifyTreatmentRecord(id);
  }

  /**
   * 根据开单记录ID查询开单记录
   *
   * @param id 开单记录ID
   * @return OrderRecord
   */
  @RequestMapping(value = "/order/one/{id}", method = RequestMethod.GET)
  public OrderRecord findOrderRecordById(@PathVariable(value = "id") Integer id) {
    return orderRecordBiz.selectById(id);
  }

  /**
   * 根据开单记录ID查询开单明细列表
   *
   * @param orderRecordId 开单记录ID
   * @return List<OrderDetail>
   */
  @RequestMapping(value = "/order/detail/list/{orderRecordId}", method = RequestMethod.GET)
  public List<OrderDetail> findOrderDetailByOrderRecordId(
      @PathVariable(value = "orderRecordId") Integer orderRecordId) {
    OrderDetail entity = new OrderDetail();
    entity.setOrderRecordId(orderRecordId);
    return orderDetailBiz.selectList(entity);
  }

  /**
   * 通过患者ID批量查询患者欠费总额
   *
   * @param patientIds 患者ID
   * @return 返回患者欠费集合
   */
  @RequestMapping(value = "/patient/debt/amount", method = RequestMethod.POST)
  public List<DebtAmountModel> selectDebtAmountList(@RequestBody List<Integer> patientIds) {
    List<DebtAmountModel> debtAmountModels = billRecordBiz.selectDebtAmountList(patientIds);
    return debtAmountModels;
  }

  /**
   * 通过挂号ID批量查询挂号信息
   *
   * @param registeredIds 挂号ID
   * @return 返回挂号信息集合
   */
  @RequestMapping(value = "/patient/registered/list", method = RequestMethod.POST)
  List<RegisteredVO> registeredInfoDetails(@RequestBody List<Integer> registeredIds) {
    return registeredBiz.registeredInfoDetails(registeredIds);
  }

  /**
   * 查询完成的业务目标
   *
   * @param query 查询条件
   * @return CompletedBusinessWorkGoalVO
   */
  @RequestMapping(value = "/business/goal", method = RequestMethod.POST)
  public CompletedBusinessWorkGoalVO findClinicCompletedBusinessWorkGoal(
      @RequestBody @Validated CompletedWorkGoalQuery query) {
    CompletedBusinessWorkGoalVO resultData =
        billRecordBiz.findClinicCompletedBusinessWorkGoal(query);
    return resultData;
  }
}
