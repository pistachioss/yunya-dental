package com.yunya.modules.treatment.controller.rpc;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.domain.model.SpecialistProjectReportModel;
import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectReportVO;
import com.yunya.feign.discount.domain.form.FreeStockForm;
import com.yunya.feign.discount.domain.form.LockStockForm;
import com.yunya.feign.discount.domain.query.ProductTypeQueryForm;
import com.yunya.feign.discount.domain.vo.ProductTypeVO;
import com.yunya.feign.ivy_mini.domain.bo.ProductBO;
import com.yunya.feign.ivy_mini.domain.query.GoodsQuery;
import com.yunya.feign.ivy_mini.domain.vo.GoodsVO;
import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.feign.report.domain.query.SpecialistProjectCompletedCountQuery;
import com.yunya.feign.treatment.domain.model.DebtAmountModel;
import com.yunya.feign.treatment.domain.query.*;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.tariff.*;
import com.yunya.models.treatment.*;
import com.yunya.modules.treatment.biz.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.*;

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
  /** 账单收费详情 */
  @Autowired private BillPayDetailRecordBiz billPayDetailRecordBiz;
  /** 账单退费 */
  @Autowired private BillRefundRecordBiz refundRecordBiz;

  /**
   * 根据开单ID查询开单详情与账单详情信息
   *
   * @return BigDecimal
   */
  @RequestMapping(value = "bill/detail/{orderRecordId}", method = RequestMethod.GET)
  public BillDetailGroupVO findOrderDetailAndBillDetailByOrderRecordId(
          @PathVariable(value = "orderRecordId") Integer orderRecordId) {
    return billRecordBiz.findOrderDetailAndBillDetail(orderRecordId);
  }

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
   * 根据预约ID查询患者接诊记录
   *
   * @param appointIds 预约记录
   * @return List<TreatmentRecord>
   */
  @RequestMapping(value = "/treatment/by/appointIds", method = RequestMethod.POST)
  public List<TreatmentRecord> findTreatmentRecordListByAppointIds(@RequestBody List<Integer> appointIds) {
    if (StringHelper.isNotEmpty(appointIds)) {
      return treatmentRecordBiz.findTreatmentRecordListByAppointIds(appointIds);
    }
    return null;
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
    return billRecordBiz.selectDebtAmountList(patientIds);
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
  public BusinessCompletedWorkGoalVO findClinicCompletedBusinessWorkGoal(
      @RequestBody @Validated CompletedWorkGoalQuery query) {
    return billRecordBiz.findClinicCompletedBusinessWorkGoal(query);
  }

  /**
   * 根据条件查询门诊营业收入完成情况
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  @RequestMapping(value = "/business/goal/completed", method = RequestMethod.POST)
  public BigDecimal findBusinessIncomeCompletedCount(
      @RequestBody @Validated BusinessGoalCompletedInfoQuery query) {
    return billRecordBiz.findBusinessIncomeCompletedCount(query);
  }

  /**
   * 根据条件查询门诊工作量完成情况
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  @RequestMapping(value = "/business/workload/completed", method = RequestMethod.POST)
  public BigDecimal findBusinessWorkloadCompletedCount(
      @RequestBody @Validated BusinessGoalCompletedInfoQuery query) {
    return billPayDetailRecordBiz.findBusinessWorkloadCompletedCount(query);
  }

  /**
   * 根据条件查询门诊初诊人数完成情况
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  @RequestMapping(value = "/business/first/treat/completed", method = RequestMethod.POST)
  public BigDecimal findBusinessFirstTreatCompletedCount(
      @RequestBody @Validated BusinessGoalCompletedInfoQuery query) {
    return treatmentRecordBiz.findBusinessFirstTreatCompletedCount(query);
  }

  /**
   * 根据条件查询门诊开单专科项目完成信息
   *
   * @param query 查询条件
   * @return SpecialistProjectTariffCompletedInfoVO
   */
  @RequestMapping(value = "/tariff/completed/goal", method = RequestMethod.POST)
  SpecialistProjectTariffCompletedInfoVO findClinicTariffOrderCompletedInfo(
      @RequestBody @Validated SpecialistProjectTariffCompletedInfoQuery query) {
    return orderDetailBiz.findClinicTariffOrderCompletedInfo(query);
  }

  /**
   * 查询专科项目数量
   *
   * @param specialistProjectReportModel 查询条件
   * @return Integer
   */
  @RequestMapping(value = "/tariff/specialist/percentage", method = RequestMethod.POST)
  public List<SpecialistProjectReportVO> findTariffSpecialistPercentage(
      @RequestBody SpecialistProjectReportModel specialistProjectReportModel) throws Exception {
    return orderDetailBiz.findTariffSpecialistPercentage(specialistProjectReportModel);
  }

  /**
   * 根据条件查询专科项目完成数量
   *
   * @param query 查询条件
   * @return Integer
   */
  @RequestMapping(value = "/tariff/specialist/completed", method = RequestMethod.POST)
  public Integer findSpecialistProjectCompletedCount(
      @RequestBody @Validated SpecialistProjectCompletedCountQuery query) {
    return orderDetailBiz.findSpecialistProjectCompletedCount(query);
  }

  /**
   * 根据支付方式统计账单的入账金额
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  @RequestMapping(value = "/bill/sumBillPayAmount", method = RequestMethod.POST)
  public BigDecimal sumBillPayAmount(@RequestBody @Validated CashReceiptOrRefundQuery query) {
    return billPayDetailRecordBiz.sumBillPayAmount(query);
  }

  /**
   * 根据条件查询账单退费退费现金总额
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  @RequestMapping(value = "bill/refund/cash", method = RequestMethod.POST)
  public BigDecimal findBillRefundTotalCashAmount(
      @RequestBody @Validated CashReceiptOrRefundQuery query) {
    return refundRecordBiz.findBillRefundTotalCashAmount(query);
  }

  /**
   * 查询患者末次就诊记录
   *
   * @param patientId 患者ID
   * @return 就诊信息
   */
  @RequestMapping(value = "/treat/last/{patientId}", method = RequestMethod.GET)
  public LastTreatmentInfoVO findLastTreatmentRecord(
      @PathVariable(value = "patientId") Integer patientId) {
    return treatmentRecordBiz.lastTreatmentInfo(patientId);
  }

  /**
   * 查询患者末次就诊记录 批量
   *
   * @param patientIds 患者ID
   * @return 就诊信息
   */
  @RequestMapping(value = "/treat/last/batch", method = RequestMethod.POST)
  public List<LastTreatmentInfoVO> lastTreatmentInfoByBatch(
          @RequestBody List<Integer> patientIds) {
    return treatmentRecordBiz.lastTreatmentInfoByBatch(patientIds);
  }


  /**
   * 根据条件查询患者就诊记录列表
   *
   * @param queryForm 查询条件
   * @return resultList
   */
  @PostMapping(value = "/patient/list", name = "患者就诊记录列表")
  public PageInfo<PatientTreatmentRecordVO> patientTreatmentRecordList(
          @RequestBody @Validated PatientTreatmentRecordQueryForm queryForm) {
    return treatmentRecordBiz.findPatientTreatList(queryForm);
  }

  /**
   * 根据就诊ID查询开单信息
   *
   * @param treatmentRecordId 就诊记录ID
   * @return
   */
  @GetMapping("/list/{treatmentRecordId}")
  public OrderDetailInfoVO findOrderInfoByTreatmentId(
          @PathVariable(value = "treatmentRecordId") Integer treatmentRecordId) {
    return orderRecordBiz.findOrderDetailInfoVO(treatmentRecordId);
  }

  /**
   * 患者就诊次数
   * @param patientId 患者ID
   * @return 返回就诊次数
   */
  @GetMapping("/{patientId}/treatment/times")
  public int patientTreatmentTimes(@PathVariable(value = "patientId") Integer patientId) {
    Example example = new Example(TreatmentRecord.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("patientId",patientId);
    criteria.andEqualTo("inservice",1);
    return treatmentRecordBiz.selectCountByExample(example);
  }

  /**
   * 根据多个商品表ID查询商品表名称
   *
   * @param ids 字符串ID
   * @return String
   */
  @RequestMapping(value = "/oral/name", method = RequestMethod.POST)
  public String findBaseOralNamesByIds(@RequestBody @NotEmpty String[] ids) {
    return baseOralTariffBiz.findBaseOralNamesByIds(ids);
  }

  /**
   * 根据条件查询商品表or价目表门诊折扣价
   *
   * @param query 字符串ID
   * @return ClinicItemPriceVO
   */
  @RequestMapping(value = "/clinic/itemMemberPrice", method = RequestMethod.POST)
  public List<ClinicItemPriceVO> findClinicItemMemberPrice(@Validated @RequestBody ClinicMemberPriceQuery query) {
    return clinicTariffMemberPriceBiz.findClinicItemMemberPrice(query);
  }

  /**
   * 小程序查询商品列表
   * @param query:
   * @return PageInfo<GoodsVO>
   */
  @RequestMapping(value = "/mini/goods/page", method = RequestMethod.POST)
  public PageInfo<GoodsVO> pageGoods(@Validated @RequestBody GoodsQuery query) {
    return baseOralTariffBiz.pageGoods(query);
  }

  /**
   * 查询线上商品集合
   *
   * @param ids 商品ids
   * @return List<BaseOralTariff>
   */
  @RequestMapping(value = "/list/oral", method = RequestMethod.POST)
  List<ProductBO> listOnSaleOral(@NotEmpty @RequestBody Collection<Integer> ids){
    return baseOralTariffBiz.listOnSaleOral(ids);
  }

  @RequestMapping(value = "/goods/lock/stock", method = RequestMethod.POST)
  void lockGoodsStock(@Valid @RequestBody List<LockStockForm> form){
    baseOralTariffBiz.lockGoodsStock(form);
  }

  @RequestMapping(value = "/goods/free/stock", method = RequestMethod.POST)
  void freeGoodsStock(@Valid @RequestBody List<FreeStockForm> form){
    baseOralTariffBiz.freeGoodsStock(form);
  }

  @PostMapping("/goods/productType/list")
  List<ProductTypeVO> findList(@RequestBody ProductTypeQueryForm queryForm){
    return baseOralTariffCategoryBiz.categortyList(queryForm);
  }
}
